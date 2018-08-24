package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.Urls;

final class DevUrls extends UrlsBase implements Urls {
  @Override
  public String getProtocolAndHostname() {
    return "http://10.0.2.2:3000";
  }
}
