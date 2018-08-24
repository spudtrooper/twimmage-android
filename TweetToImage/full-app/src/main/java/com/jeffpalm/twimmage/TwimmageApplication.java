package com.jeffpalm.twimmage;

import android.content.Context;
import android.content.Intent;

import com.jeffpalm.tweettoimage.TweetToImageApplication;
import com.jeffpalm.twimmage.di.ApplicationComponent;
import com.jeffpalm.twimmage.di.DaggerUtil;

public class TwimmageApplication extends TweetToImageApplication {

  private ApplicationComponent component;

  public static TwimmageApplication get(Context context) {
    return (TwimmageApplication) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    component = DaggerUtil.inject(this);

    Intent startServiceIntent = new Intent(this, CheckTwitterService.class);
    startService(startServiceIntent);
  }

  public ApplicationComponent getComponent() {
    return component;
  }
}
