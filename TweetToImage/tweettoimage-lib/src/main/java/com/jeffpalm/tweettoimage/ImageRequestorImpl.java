package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.Api;
import com.jeffpalm.tweettoimage.api.CreateJsonResult;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.util.Log;

import javax.annotation.Nullable;
import javax.inject.Inject;

final class ImageRequestorImpl implements ImageRequestor {

  private final Log log = new Log(this);

  private final Api api;
  private final TwimmageUser twimmageUser;

  // When we request to edit a tweet we need to provide a creator screen name.
  // If there isn't a logged in user, we'll use a random string.
  private final String creatorId = String.valueOf(Math.random() * Integer.MAX_VALUE);

  @Inject
  ImageRequestorImpl(Api api, TwimmageUser twimmageUser) {
    this.api = api;
    this.twimmageUser = twimmageUser;
  }

  @Override
  public void requestImage(final Status status,
                           final Template template,
                           final @Nullable Background background,
                           final Callback callback) {
    api.createJson(status,
        twimmageUser.isLoggedIn() ? twimmageUser.getTwitterUser().getScreenName() : creatorId,
        template.getKey(),
        background == null ? null : convertToApiBackground(background),
        new Api.CreateJsonHandler() {

          @Override
          public void onSuccess(CreateJsonResult result) {
            log.i("Loaded JSON for status[%s], template[%s], background[%s]",
                status,
                template,
                background);
            callback.handleImageCreated(status, result);
          }

          @Override
          public void onError(Throwable error) {
            log.e(error,
                "Error loading JSON for status[%s], template[%s], background[%s]",
                status,
                template,
                background);
            callback.handleError(error, status, template, background);
          }
        });
  }

  private com.jeffpalm.tweettoimage.api.Background convertToApiBackground(Background background) {
    if (background instanceof BackgroundFile) {
      return api.getBackgroundFactory().newBackgroundFile(((BackgroundFile) background)
          .getFileName());
    }
    if (background instanceof BackgroundColor) {
      return api.getBackgroundFactory().newBackgroundColor(((BackgroundColor) background)
          .getColor());
    }
    if (background instanceof BackgroundBitmap) {
      return api.getBackgroundFactory().newBackgroundBitmap(((BackgroundBitmap) background)
          .getBitmap());
    }
    throw new IllegalArgumentException("Invalid background: " + background);
  }
}
