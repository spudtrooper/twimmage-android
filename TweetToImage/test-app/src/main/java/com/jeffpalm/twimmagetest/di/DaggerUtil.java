package com.jeffpalm.twimmagetest.di;

import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.tweettoimage.ActivityModule;
import com.jeffpalm.tweettoimage.ApplicationModule;
import com.jeffpalm.twimmagetest.TwimmageTestApplication;

/**
 * This class bootstraps dependency injection.
 */
public final class DaggerUtil {

  private DaggerUtil() {
  }

  public static ApplicationComponent inject(TwimmageTestApplication application) {
    ApplicationComponent component = DaggerApplicationComponent
        .builder()
        .applicationModule(new ApplicationModule(application))
        .applicationModule(new com.jeffpalm.twimmagetest.ApplicationModule())
        .build();
    component.inject(application);
    return component;
  }

  public static ActivityComponent newActivityComponent(AppCompatActivity context) {
    return DaggerActivityComponent
        .builder()
        .activityModule(new ActivityModule(context))
        .applicationComponent(TwimmageTestApplication.get(context).getComponent())
        .build();
  }
}
