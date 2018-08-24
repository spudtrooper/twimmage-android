package com.jeffpalm.tweettoimage;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.jeffpalm.tweettoimage.api.Api;
import com.jeffpalm.tweettoimage.api.GetBackgroundsResult;
import com.jeffpalm.tweettoimage.api.Urls;
import com.jeffpalm.tweettoimage.di.ApplicationContext;
import com.jeffpalm.tweettoimage.util.Consumer;
import com.jeffpalm.tweettoimage.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public final class BackgroundsRequestor {
  private final static String PERSISTED_DOWNLOADS_FILENAME = "persistedDownloads.ser";

  private final Log log = new Log(this);
  private final Urls urls;
  private final Api api;
  private final Context context;
  private final RequestQueue requestQueue;

  @Inject
  public BackgroundsRequestor(Urls urls, Api api, @ApplicationContext Context context) {
    this(urls, api, context, Volley.newRequestQueue(context));
  }

  private BackgroundsRequestor(Urls urls, Api api, Context context, RequestQueue requestQueue) {
    this.urls = urls;
    this.api = api;
    this.context = context;
    this.requestQueue = requestQueue;
  }

  private void requestImage(String imageUrl, final BitmapCallback callback) {
    String url = urls.getProtocolAndHostname() + imageUrl;
    ImageRequest request = new ImageRequest(url,
        callback::handle,
        0,
        0,
        null,
        error -> log.e(error, "Requesting image %s", imageUrl));
    requestQueue.add(request);
  }

  public void requestDownloads(final Consumer<List<Background>> callback) {
    BackgroundsDeserializer backgroundsDeserializer = new BackgroundsDeserializer() {
      @Override
      protected void onPostExecute(GetBackgroundsResult bootstrappedBackgrounds) {
        super.onPostExecute(bootstrappedBackgrounds);
        if (bootstrappedBackgrounds != null) {
          log.i("Using bootstrapped backgrounds: %s", bootstrappedBackgrounds);
          callback.accept(createBackgrounds(bootstrappedBackgrounds));
        }
        api.getBackgrounds(new Api.GetBackgroundsHandler() {

          @Override
          public void onSuccess(GetBackgroundsResult result, String serializableBackgrounds) {
            callback.accept(createBackgrounds(result));
            new SaveBackgroundsTask().execute(serializableBackgrounds);
          }

          @Override
          public void onError(Throwable error) {
            log.e(error, "Requesting JSON");
          }
        });
      }
    };
    backgroundsDeserializer.execute();
  }

  private List<Background> createBackgrounds(GetBackgroundsResult result) {
    ImmutableList.Builder<Background> resBuilder = ImmutableList.builder();
    for (GetBackgroundsResult.Background background : result.getBackgrounds()) {
      resBuilder.add(new BackgroundFileImpl(background.getFilename(),
          background.getPreviewImageUrl(),
          background.getLargeImageUrl()));
    }
    return resBuilder.build();
  }

  private String getSerializedBackgrounds(final Context context) {
    File file = new File(context.getFilesDir(), PERSISTED_DOWNLOADS_FILENAME);
    try {
      return Files.toString(file, Charsets.UTF_8);
    } catch (IOException e) {
      log.e(e, "reading from file: %s", file);
    }
    return null;
  }

  private void saveBackgroundsThrowing(String serializableBackgrounds) throws IOException {
    File file = new File(context.getFilesDir(), PERSISTED_DOWNLOADS_FILENAME);
    Files.write(serializableBackgrounds.getBytes(), file);
  }

  abstract class BackgroundsDeserializer extends AsyncTask<Void, Void, GetBackgroundsResult> {
    @Override
    protected GetBackgroundsResult doInBackground(Void... voids) {
      String serializedBackgrounds = getSerializedBackgrounds(context);
      if (serializedBackgrounds != null) {
        return api.getBackgrounds(serializedBackgrounds);
      }
      return null;
    }
  }

  private final class BackgroundFileImpl implements BackgroundFile {
    private final String fileName;
    private final String previewImageUrl;
    private final String largeImageUrl;

    BackgroundFileImpl(String fileName, String previewImageUrl, String largeImageUrl) {
      this.fileName = fileName;
      this.previewImageUrl = previewImageUrl;
      this.largeImageUrl = largeImageUrl;
    }

    @Override
    public String getFileName() {
      return fileName;
    }

    @Override
    public void getPreviewImage(Context context, BitmapCallback callback) {
      requestImage(previewImageUrl, callback);
    }

    @Override
    public void getLargeImage(Context context, BitmapCallback callback) {
      requestImage(largeImageUrl, callback);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("getFileName", fileName).toString();
    }
  }

  class SaveBackgroundsTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... sers) {
      String ser = sers[0];
      try {
        saveBackgroundsThrowing(ser);
      } catch (IOException e) {
        log.e(e, "serializing: %s", ser);
      }
      return null;
    }
  }
}
