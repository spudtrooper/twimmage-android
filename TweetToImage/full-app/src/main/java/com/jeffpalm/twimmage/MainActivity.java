package com.jeffpalm.twimmage;

public final class MainActivity extends com.jeffpalm.tweettoimage.MainActivity {

  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }

}
