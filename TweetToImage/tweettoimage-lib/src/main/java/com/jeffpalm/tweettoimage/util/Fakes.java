package com.jeffpalm.tweettoimage.util;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.api.StatusImpl;
import com.jeffpalm.tweettoimage.api.TwitterUser;
import com.jeffpalm.tweettoimage.api.TwitterUserImpl;

import java.util.Date;

public final class Fakes {

  private Fakes() {
  }

  public static StatusAndTemplateKey createStatusAndTemplateKeyForTesting() {
    return StatusAndTemplateKey.newInstanceForTesting(createStatusForTesting(), "twitter");
  }

  public static Status createStatusForTesting() {
    return new StatusImpl(1 /* id */,
        "testing status" /* text */,
        new Date(),
        1 /* favoriteCount */,
        1 /* retweetCount */,
        createTwitterUserForTesting());
  }

  private static TwitterUser createTwitterUserForTesting() {
    return new TwitterUserImpl("Sarah Cooper" /* name */,
        "sarahcpr" /* screenName */,
        "https://pbs.twimg.com/profile_images/882426787854073856/hEvcUyA" + "-_normal.jpg" /*
        originalProfileImageURL */,
        "Some description" /* description */);
  }
}
