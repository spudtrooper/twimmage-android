package com.jeffpalm.twimmage.di;

import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.tweettoimage.ActivityModule;
import com.jeffpalm.tweettoimage.ApplicationModule;
import com.jeffpalm.twimmage.CheckTwitterService;
import com.jeffpalm.twimmage.TwimmageApplication;

/**
 * This class bootstraps dependency injection.
 */
public final class DaggerUtil {

  private DaggerUtil() {
  }

  public static ApplicationComponent inject(TwimmageApplication application) {
    ApplicationComponent component = DaggerApplicationComponent.builder().applicationModule(new
        ApplicationModule(
        application)).applicationModule(new com.jeffpalm.twimmage.ApplicationModule()).build();
    component.inject(application);
    return component;
  }

  public static void inject(TwimmageApplication application, CheckTwitterService service) {
    ApplicationComponent component = DaggerApplicationComponent.builder().applicationModule(new
        ApplicationModule(
        application)).applicationModule(new com.jeffpalm.twimmage.ApplicationModule()).build();
    component.inject(service);
  }

  public static ActivityComponent newActivityComponent(AppCompatActivity context) {
    return DaggerActivityComponent.builder().activityModule(new ActivityModule(context))
        .applicationComponent(
        TwimmageApplication.get(context).getComponent()).build();
  }
}
