package com.jeffpalm.twimmagetest;

import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.twimmagetest.di.ActivityComponent;
import com.jeffpalm.twimmagetest.di.DaggerUtil;

final class ActivityComponentProvider {
  private ActivityComponent activityComponent;

  public ActivityComponent getActivityComponent(AppCompatActivity context) {
    if (activityComponent == null) {
      activityComponent = DaggerUtil.newActivityComponent(context);
    }
    return activityComponent;
  }
}
