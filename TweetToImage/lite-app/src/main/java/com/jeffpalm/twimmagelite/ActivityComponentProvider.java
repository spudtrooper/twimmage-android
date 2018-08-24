package com.jeffpalm.twimmagelite;

import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.twimmagelite.di.ActivityComponent;
import com.jeffpalm.twimmagelite.di.DaggerUtil;

final class ActivityComponentProvider {
  private ActivityComponent activityComponent;

  public ActivityComponent getActivityComponent(AppCompatActivity context) {
    if (activityComponent == null) {
      activityComponent = DaggerUtil.newActivityComponent(context);
    }
    return activityComponent;
  }
}
