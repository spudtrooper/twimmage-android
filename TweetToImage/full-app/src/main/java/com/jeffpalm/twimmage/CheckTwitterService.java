package com.jeffpalm.twimmage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jeffpalm.tweettoimage.LoginController;
import com.jeffpalm.tweettoimage.TwimmageUser;
import com.jeffpalm.tweettoimage.prefs.Preferences;
import com.jeffpalm.tweettoimage.prefs.PreferencesUtil;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.NotificationUtil;
import com.jeffpalm.twimmage.di.DaggerUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public final class CheckTwitterService extends Service implements SharedPreferences
    .OnSharedPreferenceChangeListener {

  // One hour: TODO: Could make this an option?
  private final static int WAKE_UP_PERIOD_MILLIS = 1000 * 60 * 60;

  private final Log log = new Log(this);
  private final AtomicBoolean alarmScheduled = new AtomicBoolean(false);

  @Inject TwimmageUser twimmageUser;
  @Inject LoginController loginController;
  @Inject NotificationUtil notificationUtil;
  @Inject CheckTwitterDatabaseHelper checkTwitterDatabaseHelper;
  @Inject Class<? extends com.jeffpalm.tweettoimage.ShareActivity> shareActivityClass;
  @Inject TwitterNotificationChecker twitterNotificationChecker;

  @Override
  public void onCreate() {
    super.onCreate();
    DaggerUtil.inject(TwimmageApplication.get(getApplicationContext()), this);
    PreferencesUtil.getSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    log.d("onStartCommand intent(%s) flags(%d) startId(%d)", intent, flags, startId);
    checkTwitter();
    return START_NOT_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    log.d("onBind intent(%s)", intent);

    return null;
  }

  private void checkTwitter() {
    twitterNotificationChecker.checkTwitter(this::schedulAlarm);
  }

  private void schedulAlarm() {
    if (skipCheck()) {
      log.d("Don't set alarm");
      return;
    }
    if (alarmScheduled.getAndSet(true)) {
      log.d("Alarm already scheduled");
      return;
    }
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis() + WAKE_UP_PERIOD_MILLIS,
        createAlarmPendingIntent());
  }

  private boolean skipCheck() {
    if (!twimmageUser.isLoggedIn()) {
      log.d("not logged in...returning");
      return true;
    }
    if (!PreferencesUtil.getBooleanPreference(this, Preferences.NOTIFICATIONS_ENABLED)) {
      log.d("Notifications not enabled");
      return true;
    }
    return false;
  }

  private PendingIntent createAlarmPendingIntent() {
    return PendingIntent.getService(this, 0, new Intent(this, CheckTwitterService.class), 0);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    log.d("Preference changes for key=%s", key);
    if (Preferences.NOTIFICATIONS_ENABLED.getKey().equals(key)) {
      boolean newVal = sharedPreferences.getBoolean(key, false);
      if (newVal) {
        cancelPendingAlarm();
        checkTwitter();
      } else {
        cancelPendingAlarm();
      }
    }
  }

  private void cancelPendingAlarm() {
    log.d("Cancel pending alarm");
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.cancel(createAlarmPendingIntent());
    alarmScheduled.set(false);
  }
}
