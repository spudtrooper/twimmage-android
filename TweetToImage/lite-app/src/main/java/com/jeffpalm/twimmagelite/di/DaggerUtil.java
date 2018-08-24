package com.jeffpalm.twimmagelite.di;

import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.tweettoimage.ActivityModule;
import com.jeffpalm.tweettoimage.ApplicationModule;
import com.jeffpalm.twimmagelite.TwimmageLiteApplication;

/**
 * This class bootstraps dependency injection.
 */
public final class DaggerUtil {

  private DaggerUtil() {
  }

  public static ApplicationComponent inject(TwimmageLiteApplication
                                                application) {
    ApplicationComponent component = DaggerApplicationComponent.builder()
        .applicationModule(
        new ApplicationModule(application)).applicationModule(new com
            .jeffpalm.twimmagelite.ApplicationModule()).build();
    component.inject(application);
    return component;
  }

  public static ActivityComponent newActivityComponent(AppCompatActivity
                                                           context) {
    return DaggerActivityComponent.builder().activityModule(new ActivityModule(
        context)).applicationComponent(TwimmageLiteApplication.get(context)
        .getComponent()).build();
  }
}
