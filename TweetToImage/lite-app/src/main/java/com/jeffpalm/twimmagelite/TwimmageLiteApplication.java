package com.jeffpalm.twimmagelite;

import android.content.Context;

import com.jeffpalm.tweettoimage.TweetToImageApplication;
import com.jeffpalm.twimmagelite.di.ApplicationComponent;
import com.jeffpalm.twimmagelite.di.DaggerUtil;

public class TwimmageLiteApplication extends TweetToImageApplication {

  private ApplicationComponent component;

  public static TwimmageLiteApplication get(Context context) {
    return (TwimmageLiteApplication) context.getApplicationContext();
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
