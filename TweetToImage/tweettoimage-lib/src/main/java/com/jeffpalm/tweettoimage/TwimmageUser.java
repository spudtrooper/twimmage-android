package com.jeffpalm.tweettoimage;

import android.support.annotation.Nullable;

import com.jeffpalm.tweettoimage.api.TwitterUser;

public interface TwimmageUser {
  TwitterUser getTwitterUser();

  void setTwitterUser(@Nullable TwitterUser twitterUser);

  boolean isLoggedIn();
}

