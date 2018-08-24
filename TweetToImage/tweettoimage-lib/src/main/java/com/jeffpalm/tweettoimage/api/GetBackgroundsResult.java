package com.jeffpalm.tweettoimage.api;

import java.util.List;

public interface GetBackgroundsResult {
  List<Background> getBackgrounds();

  interface Background {
    String getId();

    String getFilename();

    String getFullImageUrl();

    String getLargeImageUrl();

    String getPreviewImageUrl();
  }
}
