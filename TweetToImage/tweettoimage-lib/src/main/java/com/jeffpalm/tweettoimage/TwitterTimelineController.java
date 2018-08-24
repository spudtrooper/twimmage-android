package com.jeffpalm.tweettoimage;

import android.os.AsyncTask;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Retrieves twitter statuses, each time requesting the next page.
 */
public final class TwitterTimelineController {

  private final List<AsyncTask<?, ?, ?>> searchTasks = new ArrayList<>();
  private final Log log = new Log(this);
  private final Twitter twitter;
  private final AtomicBoolean gettingTimeline = new AtomicBoolean();
  private final AtomicInteger nextPage = new AtomicInteger(1);

  @Inject
  public TwitterTimelineController(@Named("loggedInTwitter") Twitter twitter) {
    this.twitter = twitter;
  }

  public void stop() {
    synchronized (searchTasks) {
      for (AsyncTask<?, ?, ?> t : searchTasks) {
        t.cancel(true);
      }
      searchTasks.clear();
    }
  }

  public synchronized void getMoreStatuses(final StatusCallback callback) {
    int page = nextPage.getAndAdd(1);
    if (gettingTimeline.get()) {
      log.d("Alreadying getting statuses");
      callback.onPreExecute(page);
      callback.onPostExecute(page, Collections.EMPTY_LIST, false /* hasMore */);
      return;
    }
    log.d("Getting statuses start");
    gettingTimeline.set(true);
    new GetTimelineTask(twitter, callback, page, gettingTimeline).execute();
  }

  public interface StatusCallback {
    /**
     * @see android.os.AsyncTask#onPreExecute()
     */
    void onPreExecute(int page);


    /**
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    void onPostExecute(int page, List<Status> statuses, boolean hasMore);
  }

  public static abstract class StatusCallbackAdapter implements StatusCallback {

    @Override
    public void onPreExecute(int page) {
    }

    @Override
    public void onPostExecute(int page, List<Status> statuses, boolean hasMore) {
    }
  }

  private final static class StatusesResult {
    final List<Status> statuses;
    final boolean hasMore;

    StatusesResult(List<Status> statuses, boolean hasMore) {
      this.statuses = statuses;
      this.hasMore = hasMore;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(getClass()).add("# statuses",
          statuses.size()).add("hasMore", hasMore).toString();
    }
  }

  private final class GetTimelineTask extends AsyncTask<Void, Void, StatusesResult> {

    private final Twitter twitter;
    private final StatusCallback callback;
    private final int page;
    private final AtomicBoolean gettingTimeline;

    GetTimelineTask(Twitter twitter,
                    StatusCallback callback,
                    int page,
                    AtomicBoolean gettingTimeline) {
      this.twitter = twitter;
      this.callback = callback;
      this.page = page;
      this.gettingTimeline = gettingTimeline;
      synchronized (searchTasks) {
        searchTasks.add(this);
      }
    }

    @Override
    protected StatusesResult doInBackground(Void... strings) {
      log.d("Requesting statuses for page=%s", page);
      final List<com.jeffpalm.tweettoimage.api.Status> res = new ArrayList<>();
      int total = 0;
      int retweets = 0;
      int replies = 0;
      try {
        for (twitter4j.Status s : twitter.getUserTimeline(new Paging(page))) {
          total++;
          if (!Strings.isNullOrEmpty(s.getInReplyToScreenName())) {
            replies++;
            continue;
          }
          if (s.isRetweet()) {
            retweets++;
            continue;
          }
          res.add(Twitter4jTwimmageAdapter.createInstance(s));
        }
      } catch (TwitterException e) {
        log.d("Getting timeline", e);
      }
      log.d("Statuses stats total=%d kept=%d replies=%d", total, retweets, replies);
      return new StatusesResult(res, total == 20);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      callback.onPreExecute(page);
    }

    @Override
    protected void onPostExecute(StatusesResult result) {
      log.d("onPostExecute result=%s", result);
      super.onPostExecute(result);
      // Set this before calling the callback, so the callback can call us
      // recursively.
      gettingTimeline.set(false);
      log.d("Getting statuses done");
      synchronized (searchTasks) {
        searchTasks.remove(this);
      }
      callback.onPostExecute(page, result.statuses, result.hasMore);
    }
  }
}
