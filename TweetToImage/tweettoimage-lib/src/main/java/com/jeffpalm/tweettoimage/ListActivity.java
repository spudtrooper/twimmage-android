package com.jeffpalm.tweettoimage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.prefs.Preferences;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.StatusAndTemplateKey;
import com.jeffpalm.tweettoimage.widget.BackgroundsWithProgress;
import com.jeffpalm.tweettoimage.widget.TemplatedImagePagerAdapter;
import com.jeffpalm.tweettoimage.widget.ViewPagerWithProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import twitter4j.Twitter;

public abstract class ListActivity extends BaseAppCompatActivity implements SharedPreferences
    .OnSharedPreferenceChangeListener {

  private final static int MIN_TWEETS_IN_TIMELINE = 10;
  private final Log log = new Log(this);
  private final SelectionState selectionState = new SelectionState();
  @Inject TemplatesProvider templatesProvider;
  @Inject SharingController sharingController;
  @Inject TwitterTimelineController timelineManager;
  @Inject TemplatedImagePagerAdapter pagerAdapter;
  @Inject StatusArrayAdapter statusesAdapter;
  @Inject WriterController writerController;
  @Inject TwimmageUser twimmageUser;
  @Inject ActivityUtil activityUtil;
  @Inject LoginController loginController;
  @Inject @Named("loggedInTwitter") Twitter twitter;
  @Inject Class<? extends EditActivity> editActivityClass;
  @Inject Class<? extends ShareActivity> shareActivityClass;
  @Inject ViewPagerAndBackgrounds viewPagerAndBackgrounds;
  private ListView listView;
  private ViewPager viewPager;
  private final EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
    @Override
    public boolean onLoadMore(int page, int totalItemsCount) {
      requestMoreStatuses();
      return true;
    }
  };

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    viewPagerAndBackgrounds.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected final void onPause() {
    super.onPause();
    timelineManager.stop();
  }

  @Override
  public final void onRequestPermissionsResult(int requestCode,
                                               String permissions[],
                                               int[] grantResults) {
    writerController.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (activityUtil.checkForLogin()) {
      return;
    }
    setContentView(R.layout.activity_list);
    activityUtil.onCreate();
    updateStatusArrayAdapter(getSharedPreferences());
    listView = activityUtil.findViewById(R.id.tweet_list);
    listView.setAdapter(statusesAdapter);
    listView.setOnItemClickListener((adapterView, view, i, l) -> maybeShowStatus(statusesAdapter
        .get(
        i)));
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    listView.setSelector(android.R.color.darker_gray);
    FloatingActionButton fab = activityUtil.findViewById(R.id.fab);
    fab.setOnClickListener(view -> tryToWriteToStorageAndShare());

    // If the twitter user is null at this point force a log out.
    if (!twimmageUser.isLoggedIn() || twimmageUser.getTwitterUser() == null) {
      activityUtil.logout();
      return;
    }

    ViewPagerWithProgress viewPagerWithProgress = activityUtil.findViewById(R.id
        .view_pager_and_progress_container);
    viewPagerWithProgress.setStatus(getString(R.string.loading_tweets_message,
        twimmageUser.getTwitterUser().getScreenName()));
    viewPager = viewPagerWithProgress.getViewPager();

    BackgroundsWithProgress backgroundsWithProgress = activityUtil.findViewById(R.id.backgrounds);

    getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    sharingController.maybeHandleShareIntent(new SharingController.Callback() {
      @Override
      public void handleShare(Status status) {
        handleDownloadedStatus(status);
      }

      @Override
      public void handleShare(String user, long tweetId) {
        downloadTweetImage(user, tweetId);
      }
    });

    pagerAdapter.setViewCreationCallback(v -> {
      v.setOnClickListener(view -> tryToWriteToStorageAndDownload());
      v.setOnLongClickListener(view -> {
        editImage();
        return true;
      });
    });
    pagerAdapter.setHasSelectedPosition(() -> viewPager.getCurrentItem());
    pagerAdapter.setSetsCurrentPosition(pos -> viewPager.setCurrentItem(pos));

    // Render the page adapter after the templates/status has loaded.
    pagerAdapter.setOnReadyListener(viewPagerWithProgress::showViewPager);

    listView.setOnScrollListener(endlessScrollListener);
    activityUtil.listenTo(writerController);

    // Hide these until the first page of tweets arrives.
    viewPager.setVisibility(View.INVISIBLE);
    listView.setVisibility(View.INVISIBLE);

    // Add background actions.
    viewPagerAndBackgrounds.setUp(viewPagerWithProgress,
        backgroundsWithProgress,
        this::handleBackgroundClick);

    requestMoreStatuses();
  }

  private void handleBackgroundClick(Background background) {
    log.d("Use background: %s", background);
    selectionState.background = background;
  }

  private Template getSelectedTemplate() {
    return templatesProvider.getTemplates().get(viewPager.getCurrentItem());
  }

  private void editImage() {
    com.jeffpalm.tweettoimage.api.Status status = pagerAdapter.getStatus();
    log.d("Edit status: %s", status);
    Intent intent = new Intent(this, editActivityClass);
    intent.putExtra(EditActivity.EXTRA_STATUS_AND_TEMPLATE_KEY,
        StatusAndTemplateKey.newInstance(status, getSelectedTemplate()));
    startActivity(intent);
  }

  private void downloadTweetImage(String user, long id) {
    log.d("Download tweet image for user %s and id %d", user, id);
    new ShowStatusTask(twitter) {
      @Override
      protected void onPostExecute(com.jeffpalm.tweettoimage.api.Status status) {
        super.onPostExecute(status);
        handleDownloadedStatus(status);
      }
    }.execute(id);
  }

  private void tryToWriteToStorageAndShare() {
    writerController.share();
  }

  private void handleDownloadedStatus(Status status) {
    statusesAdapter.addHighPriorityStatus(status);
    showStatus(status);
  }

  private void tryToWriteToStorageAndDownload() {
    writerController.download();
  }

  private void showStatus(Status status) {
    selectionState.status = status;
    pagerAdapter.setStatus(status);
  }

  @Override
  public final void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    log.d("Changing preference with key=" + key);
    if (key.equals(Preferences.PREF_TWEET_SORT_TYPE)) {
      log.d("Sort type changed");
      updateStatusArrayAdapter(prefs);
      listView.invalidate();
    }
  }

  private void updateStatusArrayAdapter(SharedPreferences prefs) {
    String sortType = prefs.getString(Preferences.PREF_TWEET_SORT_TYPE, "");
    log.d("Update status array adapter with sortType=" + sortType);
    switch (sortType) {
      case "Date":
        statusesAdapter.setComparator((Status a, Status b) -> b.getCreatedAt().compareTo(a
            .getCreatedAt()));
        break;
      case "# Favorites":
        statusesAdapter.setComparator((Status a, Status b) -> b.getFavoriteCount() - a
            .getFavoriteCount());
        break;
      case "# Retweets":
        statusesAdapter.setComparator((Status a, Status b) -> b.getRetweetCount() - a
            .getRetweetCount());
        break;
      default:
        log.e("Unknown sort type=%s ", sortType);
    }
  }

  private SharedPreferences getSharedPreferences() {
    return activityUtil.getSharedPreferences();
  }

  private void requestMoreStatuses() {
    timelineManager.getMoreStatuses(new TwitterTimelineController.StatusCallback() {
      @Override
      public void onPreExecute(int page) {
        activityUtil.showProgress(true);
      }

      @Override
      public void onPostExecute(int page, List<Status> statuses, boolean hasMore) {
        activityUtil.showProgress(false);
        log.d("Found %s statuses for page %d hasMore=%s # statusesSoFar=%d",
            statuses.size(),
            page,
            hasMore,
            statusesAdapter.getCount());
        addMoreStatuses(statuses);
        if (hasMore && statusesAdapter.getCount() < MIN_TWEETS_IN_TIMELINE) {
          requestMoreStatuses();
        }
      }
    });
  }

  private void maybeShowStatus(Status s) {
    if (selectionState.status.getId() == s.getId()) {
      log.d("Dedup'ed status %s", s);
      return;
    }
    showStatus(s);
  }

  private void addMoreStatuses(List<Status> statuses) {
    if (statuses.isEmpty()) {
      log.e("No statuses");
      return;
    }
    viewPager.setVisibility(View.VISIBLE);
    listView.setVisibility(View.VISIBLE);
    boolean isFirst = statusesAdapter.getCount() == 0;
    statusesAdapter.addStatuses(statuses);
    if (isFirst) {
      showStatus(statuses.get(0));
    }
  }

  private final class SelectionState {
    Status status;
    Background background;
  }
}
