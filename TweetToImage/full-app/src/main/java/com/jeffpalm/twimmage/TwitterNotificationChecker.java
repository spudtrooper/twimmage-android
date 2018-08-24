package com.jeffpalm.twimmage;

import android.content.Context;
import android.content.Intent;

import com.jeffpalm.tweettoimage.LoginController;
import com.jeffpalm.tweettoimage.ShareActivity;
import com.jeffpalm.tweettoimage.SharingController;
import com.jeffpalm.tweettoimage.TwitterTimelineController;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.di.ApplicationContext;
import com.jeffpalm.tweettoimage.prefs.Preferences;
import com.jeffpalm.tweettoimage.prefs.PreferencesUtil;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.NotificationUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

final class TwitterNotificationChecker {

  // One hour
  private final static int MIN_TWEETS_IN_TIMELINE = 10;
  private final Log log = new Log(this);
  private final Context context;
  private final LoginController loginController;
  private final NotificationUtil notificationUtil;
  private final CheckTwitterDatabaseHelper checkTwitterDatabaseHelper;
  private final Class<? extends com.jeffpalm.tweettoimage.ShareActivity> shareActivityClass;
  private final AtomicBoolean checking = new AtomicBoolean(false);

  @Inject
  TwitterNotificationChecker(@ApplicationContext Context context,
                             LoginController loginController,
                             NotificationUtil notificationUtil,
                             CheckTwitterDatabaseHelper checkTwitterDatabaseHelper,
                             Class<? extends ShareActivity> shareActivityClass) {
    this.context = context;
    this.loginController = loginController;
    this.notificationUtil = notificationUtil;
    this.checkTwitterDatabaseHelper = checkTwitterDatabaseHelper;
    this.shareActivityClass = shareActivityClass;
  }

  public void checkTwitter(CheckTwitterListener listener) {
    if (checking.getAndSet(true)) {
      log.d("Already checking");
      return;
    }
    TwitterTimelineController timelineManager = new TwitterTimelineController(loginController
        .getTwitter());
    AtomicInteger seen = new AtomicInteger(0);
    checkTwitter(listener, timelineManager, seen);
  }

  private void checkTwitter(final CheckTwitterListener listener,
                            final TwitterTimelineController timelineManager,
                            final AtomicInteger seen) {
    timelineManager.getMoreStatuses(new TwitterTimelineController.StatusCallbackAdapter() {
      @Override
      public void onPostExecute(int page, List<Status> statuses, boolean hasMore) {
        for (Status status : statuses) {
          log.d("Looking at status id=%s retweets=%d favs=%d",
              status.getId(),
              status.getRetweetCount(),
              status.getFavoriteCount());
          maybeCreateNotification(status);
        }
        if (hasMore && seen.addAndGet(statuses.size()) < MIN_TWEETS_IN_TIMELINE) {
          log.d("Getting more statuses");
          checkTwitter(listener, timelineManager, seen);
        } else {
          listener.done();
          checking.set(false);
        }
      }
    });
  }

  private void maybeCreateNotification(Status status) {
    final int minRetweets = PreferencesUtil.getIntFromStringPreference(context,
        Preferences.NOTIFICATIONS_MIN_RETWEETS);
    final int minFavorites = PreferencesUtil.getIntFromStringPreference(context,
        Preferences.NOTIFICATIONS_MIN_FAVORITES);
    if (status.getRetweetCount() >= minRetweets) {
      log.d("Have enough retweets, %d >= %d", status.getRetweetCount(), minRetweets);
      maybeCreateNotification(status,
          context.getString(R.string.your_tweet_has_reached_retweets, status.getRetweetCount()));
      return;
    }
    if (status.getFavoriteCount() >= minFavorites) {
      log.d("Have enough favorites, %d >= %d", status.getFavoriteCount(), minFavorites);
      maybeCreateNotification(status,
          context.getString(R.string.your_tweet_has_reached_favorites, status.getFavoriteCount()));
      return;
    }
  }

  private void maybeCreateNotification(Status status, String reason) {
    log.d("maybeCreateNotification status(%s)", status.getId());
    if (checkTwitterDatabaseHelper.exists(status.getId())) {
      log.d("status id=%s already exists", status.getId());
      return;
    }
    log.d("status id=%s doesn't exist yet", status.getId());
    createNotification(status, reason);
    checkTwitterDatabaseHelper.insert(status.getId());
  }

  private void createNotification(Status status, String reason) {
    log.d("Creating notification for id=%s and status=%s", status.getId(), status.getText());
    int notificationId = (int) status.getId();
    Intent intent = new Intent(context, shareActivityClass);
    intent.putExtra(SharingController.EXTRA_STATUS, status);
    // Need this to avoid all notifications starting to first action.
    // https://stackoverflow
    // .com/questions/12968280/android-multiple-notifications-and-with
    // -multiple-intents
    intent.setAction("dummy_action_" + notificationId);
    notificationUtil.createNotification(context,
        notificationId,
        status,
        reason + ": " + status.getText(),
        intent);
  }

  interface CheckTwitterListener {
    void done();
  }
}
