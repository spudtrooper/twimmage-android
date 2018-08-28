package com.jeffpalm.tweettoimage.api;

import java.util.List;
import java.util.Objects;

public interface GetBackgroundsResult {
  List<Background> getBackgrounds();

  interface Background {
    String getId();

    String getFilename();

    String getFullImageUrl();

    String getLargeImageUrl();

    String getPreviewImageUrl();

    interface Equals {
      boolean equals(Background a, Background b);
    }

    default Equals getEquals() {
      return (a, b) -> Objects.equals(a.getId(), b.getId()) && Objects.equals(a.getFilename(),
          b.getFilename()) && Objects.equals(a.getFullImageUrl(),
          b.getFullImageUrl()) && Objects.equals(a.getLargeImageUrl(),
          b.getLargeImageUrl()) && Objects.equals(a.getPreviewImageUrl(), b.getPreviewImageUrl());
    }
  }

  interface Equals {
    boolean equals(GetBackgroundsResult a, GetBackgroundsResult b);
  }

  default Equals getEquals() {
    return (a, b) -> Objects.deepEquals(a.getBackgrounds(), b.getBackgrounds());
  }
}
