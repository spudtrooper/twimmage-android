package com.jeffpalm.tweettoimage;

import android.app.Application;
import android.content.SharedPreferences;

import com.jeffpalm.tweettoimage.util.Log;

import java.util.Map;

public class TweetToImageApplication extends Application {

  private final Log log = new Log(this);

  @Override
  public void onCreate() {
    super.onCreate();
    dumpSharedPreferences();
  }

  private void dumpSharedPreferences() {
    log.d("Dumping shared preferences");
    log.d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    SharedPreferences prefs = LoginController.getLoginSharedPreferences(this);
    int i = 0;
    for (Map.Entry<String, ?> e : prefs.getAll().entrySet()) {
      log.d("[%d] %s = %s", i++, e.getKey(), e.getValue());
    }
    log.d("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
  }
}
