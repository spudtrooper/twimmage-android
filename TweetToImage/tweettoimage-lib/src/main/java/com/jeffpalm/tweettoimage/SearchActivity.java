package com.jeffpalm.tweettoimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.api.StatusImpl;
import com.jeffpalm.tweettoimage.api.TwitterUser;
import com.jeffpalm.tweettoimage.prefs.Preferences;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.StatusAndTemplateKey;
import com.jeffpalm.tweettoimage.util.TextWatcherAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public abstract class SearchActivity extends BaseAppCompatActivity {

  private final static int KEY_PRESS_DETECTION_TIME_MILLIS = 1 * 1000;
  private final static int MIN_QUERY_LENGTH = 2;
  private final Log log = new Log(this);
  private final List<SearchTask> searchTasks = new ArrayList<>();
  @Inject TwimmageUser twimmageUser;
  @Inject TemplatesProvider templatesProvider;
  @Inject TwitterUserSearchController twitterUserSearchManager;
  @Inject TwitterUserArrayAdapter usersAdapter;
  @Inject ActivityUtil activityUtil;
  @Inject Class<? extends EditActivity> editActivityClass;
  private ProgressDialog progress;
  private ListView listView;
  private EditText searchText;
  private final EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
    @Override
    public boolean onLoadMore(int page, int totalItemsCount) {
      requestMoreSearchResults(false);
      return true;
    }
  };
  private final TextWatcher textWatcher = new TextWatcherAdapter() {
    @Override
    public void afterTextChanged(Editable editable) {
      super.afterTextChanged(editable);
      maybeStartSearchTask();
    }
  };

  private void maybeStartSearchTask() {
    log.d("maybeStartSearchTask");
    cancelCurrentSearchTasks();
    String query = getQuery();
    if (Strings.isNullOrEmpty(query) || query.length() < MIN_QUERY_LENGTH) {
      log.d("Skipping query because it's too short");
      return;
    }
    new SearchTask().execute();
  }

  private void cancelCurrentSearchTasks() {
    synchronized (searchTasks) {
      for (SearchTask t : searchTasks) {
        t.cancel(true);
      }
      searchTasks.clear();
    }
  }

  private String getQuery() {
    return searchText.getText().toString();
  }

  @Override
  protected final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    activityUtil.onCreate();
    listView = activityUtil.findViewById(R.id.searchList);
    searchText = activityUtil.findViewById(R.id.searchText);
    listView.setAdapter(usersAdapter);
    listView.setOnItemClickListener((av, view, i, l) -> showUser(usersAdapter.get(i)));
    searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    searchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        twitterUserSearchManager.stop();
        startSearch(true);
        activityUtil.hideKeyboard();
        return true;
      }
      return false;
    });
    listView.setOnScrollListener(endlessScrollListener);
    // Hide these until the first page of tweets arrives.
    listView.setVisibility(View.INVISIBLE);
    // Perform incremental search.
    if (activityUtil.getBooleanPreference(Preferences.INCREMENTAL_SEARCH)) {
      searchText.addTextChangedListener(textWatcher);
    }
  }

  private void showUser(TwitterUser twitterUser) {
    Status status = new StatusImpl(1, "", new Date(), 1, 1, twitterUser);
    Intent intent = new Intent(this, editActivityClass);
    intent.putExtra(EditActivity.EXTRA_STATUS_AND_TEMPLATE_KEY,
        StatusAndTemplateKey.newInstance(status, templatesProvider.getDefaultTemplate()));
    intent.putExtra(EditActivity.EXTRA_SELECT_ALL, true);
    startActivity(intent);
  }

  private void startSearch(boolean showProgress) {
    String query = getQuery();
    usersAdapter.reset();
    twitterUserSearchManager.reset();
    twitterUserSearchManager.setQuery(query);
    requestMoreSearchResults(showProgress);
  }

  private void requestMoreSearchResults(final boolean showProgress) {
    twitterUserSearchManager.getMoreResults(new TwitterUserSearchController.StatusCallback() {
      @Override
      public void onPreExecute(int page) {
        if (showProgress && page == 1) {
          progress = ProgressDialog.show(SearchActivity.this,
              getString(R.string.searching_users_title),
              getString(R.string.searching_users_message, getQuery()),
              true);
        }
        activityUtil.showProgress(true);
      }

      @Override
      public void onPostExecute(int page, List<TwitterUser> users) {
        if (progress != null) {
          progress.dismiss();
          progress = null;
        }
        activityUtil.showProgress(false);
        log.d("Found %s statuses for page %d", users.size(), page);
        addMoresUsers(page, users);
      }
    });
  }

  private void addMoresUsers(int page, List<TwitterUser> users) {
    if (users.isEmpty()) {
      log.e("No users");
      return;
    }
    listView.setVisibility(View.VISIBLE);
    usersAdapter.addStatuses(users);
  }

  @Override
  public final void onDestroy() {
    super.onDestroy();
    searchText.removeTextChangedListener(textWatcher);
    cancelCurrentSearchTasks();
  }

  @Override
  protected final void onPause() {
    super.onPause();
    twitterUserSearchManager.stop();
  }

  private final class SearchTask extends AsyncTask<Void, Void, Void> {

    private SearchTask() {
      synchronized (searchTasks) {
        searchTasks.add(this);
      }
    }

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        Thread.sleep(KEY_PRESS_DETECTION_TIME_MILLIS);
      } catch (InterruptedException expected) {
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void v) {
      super.onPostExecute(v);
      log.d("onPostExecute");
      startSearch(false);
    }
  }
}
