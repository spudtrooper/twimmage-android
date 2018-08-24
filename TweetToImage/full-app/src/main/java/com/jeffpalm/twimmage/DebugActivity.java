package com.jeffpalm.twimmage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jeffpalm.tweettoimage.ActivityUtil;
import com.jeffpalm.tweettoimage.ShareActivity;
import com.jeffpalm.tweettoimage.SharingController;
import com.jeffpalm.tweettoimage.util.Fakes;
import com.jeffpalm.tweettoimage.util.Log;

import java.util.List;

import javax.inject.Inject;

public final class DebugActivity extends com.jeffpalm.tweettoimage.DebugActivity {

  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();
  private final Log log = new Log(this);

  @Inject ActivityUtil activityUtil;
  @Inject CheckTwitterDatabaseHelper checkTwitterDatabaseHelper;
  @Inject TwitterNotificationChecker twitterNotificationChecker;
  @Inject Class<? extends ShareActivity> shareActivityClass;

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debug);
    Toolbar toolbar = activityUtil.findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    activityUtil.findViewById(R.id.clearTwitterNotificationsButton).setOnClickListener
        (this::clearTwitterNotifications);
    activityUtil.findViewById(R.id.showTwitterNotificationsButton).setOnClickListener
        (this::showTwitterNotifications);
    activityUtil.findViewById(R.id.checkForTwitterNotificationsButton).setOnClickListener
        (this::checkForTwitterNotifications);
    activityUtil.findViewById(R.id.launchShareActivityButton).setOnClickListener
        (this::launchShareActivity);
  }

  private void clearTwitterNotifications(View unusedView) {
    log.d("Clearing twitter notifications");
    checkTwitterDatabaseHelper.clear();
  }

  private void checkForTwitterNotifications(View unusedView) {
    log.d("Checking for twitter notifications");
    twitterNotificationChecker.checkTwitter(() -> log.d("Done checking for notifications"));
  }

  private void showTwitterNotifications(View unusedView) {
    log.d("Showing twitter notifications");
    log.d("Notifications:");
    List<TwitterNotification> tns = checkTwitterDatabaseHelper.getTwitterNotifications();
    if (tns.isEmpty()) {
      log.d("Empty");
    } else {
      for (TwitterNotification tn : tns) {
        log.d(" - %s", tn);
      }
    }
  }

  private void launchShareActivity(View unusedView) {
    log.d("launchShareActivity");
    Intent intent = new Intent(this, shareActivityClass);
    intent.putExtra(SharingController.EXTRA_STATUS, Fakes.createStatusForTesting());
    startActivity(intent);
  }
}
