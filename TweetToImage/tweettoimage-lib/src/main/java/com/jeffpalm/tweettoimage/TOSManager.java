package com.jeffpalm.tweettoimage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.jeffpalm.tweettoimage.util.Log;

import javax.inject.Inject;

/**
 * Manages state whether the user has accepted terms and services or not.
 */
final class TOSManager {

  private static final String ACCEPTED_TOC = "acceptedToc";

  private final Log log = new Log(this);

  private final Application application;
  private final SharedPreferences prefs;

  @Inject
  TOSManager(Application application) {
    this(application, application.getSharedPreferences("toc", Context.MODE_PRIVATE));
  }

  private TOSManager(Application application, SharedPreferences prefs) {
    this.application = application;
    this.prefs = prefs;
  }

  public void checkTos(Context context, final Runnable onAccepted, final Runnable onRejection) {
    if (hasAcceptedToc()) {
      onAccepted.run();
      return;
    }
    AlertDialog.Builder adb = new AlertDialog.Builder(context);
    adb.setTitle(R.string.toc_title);
    adb.setMessage(R.string.toc_message);
    adb.setIcon(android.R.drawable.ic_dialog_alert);
    adb.setPositiveButton(R.string.accept, (dialog, which) -> {
      log.d("accepted TOC");
      userHasAcceptedToc();
      Toast.makeText(context, R.string.terms_accepted, Toast.LENGTH_SHORT).show();
      onAccepted.run();
    });
    adb.setNegativeButton(R.string.disagree, (dialog, which) -> {
      log.d("reject TOC");
      Toast.makeText(context, R.string.terms_rejected, Toast.LENGTH_SHORT).show();
      onRejection.run();
    });
    adb.show();
  }

  private boolean hasAcceptedToc() {
    return prefs.getBoolean(ACCEPTED_TOC, false);
  }

  private void userHasAcceptedToc() {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(ACCEPTED_TOC, true);
    editor.commit();
  }

  private void reset() {
    SharedPreferences.Editor editor = prefs.edit();
    editor.remove(ACCEPTED_TOC);
    editor.commit();
  }
}
