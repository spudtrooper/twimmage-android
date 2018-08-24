package com.jeffpalm.tweettoimage;

import android.os.AsyncTask;

import com.jeffpalm.tweettoimage.api.TwitterUser;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Retrieves twitter statuses, each time requesting the next page.
 */
final class TwitterUserSearchController {

  private final List<AsyncTask<?, ?, ?>> searchTasks = new ArrayList<>();
  private final Log log = new Log(this);
  private final Twitter twitter;
  private final AtomicBoolean isBusy = new AtomicBoolean();
  private int nextPage = 1;
  private String query;

  @Inject
  TwitterUserSearchController(Twitter twitter) {
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

  public void setQuery(String query) {
    this.query = query;
  }

  public synchronized void getMoreResults(StatusCallback callback) {
    Assert.notNull(query, "Must call setQuery before getting more users");
    int page = nextPage;
    nextPage++;
    if (isBusy.get()) {
      log.d("Alreadying getting users");
      callback.onPreExecute(page);
      callback.onPostExecute(page, Collections.EMPTY_LIST);
      return;
    }
    log.d("Getting users start");
    isBusy.set(true);
    new GetUsersTask(callback, page, query).execute();
  }

  public int getNumPages() {
    return nextPage - 1;
  }

  public void reset() {
    nextPage = 1;
    query = null;
    isBusy.set(false);
  }

  interface StatusCallback {
    /**
     * @see AsyncTask#onPreExecute()
     */
    void onPreExecute(int page);


    /**
     * @see AsyncTask#onPostExecute(Object)
     */
    void onPostExecute(int page, List<TwitterUser> results);
  }

  private final class GetUsersTask extends AsyncTask<Void, Void, List<TwitterUser>> {

    private final StatusCallback callback;
    private final int page;
    private final String query;

    GetUsersTask(StatusCallback callback, int page, String query) {
      this.callback = callback;
      this.page = page;
      this.query = query;
      synchronized (searchTasks) {
        searchTasks.add(this);
      }
    }

    @Override
    protected List<TwitterUser> doInBackground(Void... strings) {
      log.d("Requesting results for page=%s", page);
      final List<TwitterUser> res = new ArrayList<>();
      try {
        int i = 0;
        for (twitter4j.User user : twitter.searchUsers(query, page)) {
          log.d("user[%d] %s", i++, user);
          res.add(Twitter4jTwimmageAdapter.createInstance(user));
        }
      } catch (TwitterException e) {
        log.e(e, "Getting users");
      }
      return res;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      callback.onPreExecute(page);
    }

    @Override
    protected void onPostExecute(List<TwitterUser> results) {
      super.onPostExecute(results);
      callback.onPostExecute(page, results);
      isBusy.set(false);
      log.d("Getting users done");
      synchronized (searchTasks) {
        searchTasks.remove(this);
      }
    }
  }
}
