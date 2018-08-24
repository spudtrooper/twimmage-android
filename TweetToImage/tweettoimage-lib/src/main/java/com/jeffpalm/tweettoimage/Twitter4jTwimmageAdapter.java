package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.api.StatusImpl;
import com.jeffpalm.tweettoimage.api.TwitterUser;
import com.jeffpalm.tweettoimage.api.TwitterUserImpl;

import twitter4j.User;

public final class Twitter4jTwimmageAdapter {

  private Twitter4jTwimmageAdapter() {
  }

  public static Status createInstance(twitter4j.Status status) {
    return new StatusImpl(status.getId(),
        status.getText(),
        status.getCreatedAt(),
        status.getFavoriteCount(),
        status.getRetweetCount(),
        createInstance(status.getUser()));
  }

  public static TwitterUser createInstance(User user) {
    return new TwitterUserImpl(user.getName(),
        user.getScreenName(),
        user.getOriginalProfileImageURL(),
        user.getDescription());
  }
}
