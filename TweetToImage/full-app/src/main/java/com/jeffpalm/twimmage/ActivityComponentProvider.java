package com.jeffpalm.twimmage;

import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.twimmage.di.ActivityComponent;
import com.jeffpalm.twimmage.di.DaggerUtil;

final class ActivityComponentProvider {
  private ActivityComponent activityComponent;

  public ActivityComponent getActivityComponent(AppCompatActivity context) {
    if (activityComponent == null) {
      activityComponent = DaggerUtil.newActivityComponent(context);
    }
    return activityComponent;
  }
}
