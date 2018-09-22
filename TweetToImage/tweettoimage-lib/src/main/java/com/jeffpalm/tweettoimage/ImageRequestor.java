package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.CreateJsonResult;
import com.jeffpalm.tweettoimage.api.Status;

import javax.annotation.Nullable;

public interface ImageRequestor {
  void requestImage(Status status,
                    Template template,
                    @Nullable Background background,
                    Callback callback);

  interface Callback {
    void handleImageCreated(Status s, CreateJsonResult response);

    void handleError(Throwable t,
                     Status status,
                     Template template,
                     @Nullable Background background);
  }
}
