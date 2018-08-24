package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.Urls;

abstract class UrlsBase implements Urls {

  public abstract String getProtocolAndHostname();

  @Override
  public final String getCreateJsonUrl() {
    return getProtocolAndHostname() + "/create/json";
  }

  @Override
  public final String getCreateBackgroundsUrl() {
    return getProtocolAndHostname() + "/create/backgrounds";
  }

  @Override
  public final String getCreateTemplatesUrl() {
    return getProtocolAndHostname() + "/create/templates";
  }
}
