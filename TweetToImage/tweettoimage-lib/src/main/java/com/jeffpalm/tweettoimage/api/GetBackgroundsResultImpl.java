package com.jeffpalm.tweettoimage.api;


import com.google.common.base.MoreObjects;

import java.util.List;

final class GetBackgroundsResultImpl implements GetBackgroundsResult {

  private final List<Background> backgrounds;

  public GetBackgroundsResultImpl(List<Background> backgrounds) {
    this.backgrounds = backgrounds;
  }

  @Override
  public List<Background> getBackgrounds() {
    return backgrounds;
  }

  final static class BackgroundImpl implements Background {

    private final String id;
    private final String filename;
    private final String fullImageUrl;
    private final String largeImageUrl;
    private final String previewImageUrl;

    BackgroundImpl(String id,
                   String filename,
                   String fullImageUrl,
                   String largeImageUrl,
                   String previewImageUrl) {
      this.id = id;
      this.filename = filename;
      this.fullImageUrl = fullImageUrl;
      this.largeImageUrl = largeImageUrl;
      this.previewImageUrl = previewImageUrl;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public String getFilename() {
      return filename;
    }

    @Override
    public String getFullImageUrl() {
      return fullImageUrl;
    }

    @Override
    public String getLargeImageUrl() {
      return largeImageUrl;
    }

    @Override
    public String getPreviewImageUrl() {
      return previewImageUrl;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("id", id).add("filename", filename).add(
          "fullImageUrl",
          fullImageUrl).add("largeImageUrl", largeImageUrl).add("previewImageUrl",
          previewImageUrl).toString();
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Background)) {
        return false;
      }
      Background that = (Background) obj;
      return getEquals().equals(this, that);
    }
  }

  @Override
  public int hashCode() {
    return getBackgrounds().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GetBackgroundsResult)) {
      return false;
    }
    GetBackgroundsResult that = (GetBackgroundsResult) obj;
    return getEquals().equals(this, that);
  }
}
