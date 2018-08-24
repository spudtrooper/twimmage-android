package com.jeffpalm.twimmagetest;

import android.content.Context;

import com.jeffpalm.tweettoimage.TweetToImageApplication;
import com.jeffpalm.twimmagetest.di.ApplicationComponent;
import com.jeffpalm.twimmagetest.di.DaggerUtil;

public class TwimmageTestApplication extends TweetToImageApplication {

  private ApplicationComponent component;

  public static TwimmageTestApplication get(Context context) {
    return (TwimmageTestApplication) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    component = DaggerUtil.inject(this);
  }

  public ApplicationComponent getComponent() {
    return component;
  }
}
