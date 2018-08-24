package com.jeffpalm.tweettoimage;

import android.os.AsyncTask;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;

class ShowStatusTask extends AsyncTask<Long, Void, Status> {

  private final Log log = new Log(this);
  private final Twitter twitter;

  ShowStatusTask(Twitter twitter) {
    this.twitter = twitter;
  }

  @Override
  protected final com.jeffpalm.tweettoimage.api.Status doInBackground(Long... longs) {
    Assert.eq(1, longs.length);
    long id = longs[0];
    try {
      twitter4j.Status s = twitter.tweets().showStatus(id);
      log.d("Got status: %s from id %s", s, id);
      return Twitter4jTwimmageAdapter.createInstance(s);
    } catch (TwitterException e) {
      log.e("downloadTweetImage", e);
    }
    log.d("Not tweet for id %s", id);
    return null;
  }
}
