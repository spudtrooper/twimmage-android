package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.Urls;

final class ProdUrls extends UrlsBase implements Urls {
  @Override
  public String getProtocolAndHostname() {
    return "https://tweettoimage.herokuapp.com";
  }

}
