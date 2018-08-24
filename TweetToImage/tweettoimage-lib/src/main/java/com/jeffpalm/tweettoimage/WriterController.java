package com.jeffpalm.tweettoimage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.NotificationUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

final class WriterController {
  private static final int REQUEST_WRITE_EXTERNAL_STORAGE_TO_DOWNLOAD = 1;
  private static final int REQUEST_WRITE_EXTERNAL_STORAGE_TO_SHARE = 2;
  private final Log log = new Log(WriterController.class.getCanonicalName());
  private final HasSelectedInfo hasSelectedInfo;
  private final NotificationUtil notificationUtil;
  private final Activity context;
  private final AtomicBoolean busy = new AtomicBoolean(false);
  private Listener listener;

  @Inject
  WriterController(Activity context,
                   HasSelectedInfo hasSelectedInfo,
                   NotificationUtil notificationUtil) {
    this.context = context;
    this.hasSelectedInfo = hasSelectedInfo;
    this.notificationUtil = notificationUtil;
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public void download() {
    tryToWriteToStorageAndShare(Action.DOWNLOAD);
  }

  private void tryToWriteToStorageAndShare(Action action) {
    if (busy.getAndSet(true)) {
      log.d("Already writing");
      return;
    }
    if (listener != null) {
      listener.onStart();
    }
    log.d("Trying to write to storage");
    int permissionCheck = ContextCompat.checkSelfPermission(context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
            action == Action.DOWNLOAD ? REQUEST_WRITE_EXTERNAL_STORAGE_TO_DOWNLOAD :
                REQUEST_WRITE_EXTERNAL_STORAGE_TO_SHARE);
      } else {
        log.d("Downloading because we already have permission");
        writeToStorageAndShare(action);
      }
    }
  }

  private void writeToStorageAndShare(final Action action) {
    hasSelectedInfo.getSelectedInfo(selectedInfo -> writeToStorageAndShare(action, selectedInfo));
  }

  private void writeToStorageAndShare(Action action, SelectedInfo selInfo) {
    // Write the image to a file.
    Assert.notNull(selInfo);
    log.d("selInfo: %s", selInfo);
    String imageFileName = selInfo.getTemplate().getKey() + "-" + selInfo.getStatus().getId() + "" +
        ".png";
    File outfile = new File(Environment.getExternalStoragePublicDirectory(Environment
        .DIRECTORY_DOWNLOADS),
        imageFileName);
    log.d("Writing to %s", outfile);
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outfile))) {
      out.write(selInfo.getImageBytes());
    } catch (IOException e) {
      log.e(e, "writing to %s", outfile);
    }

    switch (action) {
      case SHARE:
        shareDownload(outfile);
        break;
      case DOWNLOAD:
        showNotification(outfile, selInfo);
        break;
      default:
        log.e("Unhandled action: %s", action);
    }

    // Done.
    busy.set(false);
    if (listener != null) {
      listener.onStop();
    }
  }

  private void shareDownload(File outfile) {
    Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("image/png");
    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    Uri uri = uriFromFile(outfile);
    log.d("Sharing uri: %s", uri);
    share.putExtra(Intent.EXTRA_STREAM, uri);
    context.startActivity(Intent.createChooser(share, "Share image to"));
  }

  private void showNotification(File outfile, SelectedInfo selInfo) {
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    String type = mime.getMimeTypeFromExtension("png");
    Uri uri = uriFromFile(outfile);
    log.d("Sharing uri: %s", uri);
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setDataAndType(uri, type);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    notificationUtil.createNotification(context,
        selInfo.hashCode() /* notificationId */,
        selInfo.getStatus(),
        selInfo.getStatus().getText(),
        intent);
  }

  private Uri uriFromFile(File file) {
    return FileProvider.getUriForFile(context,
        context.getApplicationContext().getPackageName() + ".com.jeffpalm" + ".tweettoimage" +
            ".provider",
        file);
  }

  public void share() {
    tryToWriteToStorageAndShare(Action.SHARE);
  }

  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults) {
    if (!isGranted(grantResults)) {
      return;
    }
    switch (requestCode) {
      case REQUEST_WRITE_EXTERNAL_STORAGE_TO_DOWNLOAD:
        log.d("Downloading after being granted permission");
        writeToStorageAndShare(Action.DOWNLOAD);
        break;
      case REQUEST_WRITE_EXTERNAL_STORAGE_TO_SHARE:
        log.d("Sharing after being granted permission");
        writeToStorageAndShare(Action.SHARE);
        break;
    }
  }

  private boolean isGranted(int[] grantResults) {
    return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
  }

  private enum Action {
    DOWNLOAD, SHARE
  }

  interface Listener {
    void onStart();

    void onStop();
  }
}
