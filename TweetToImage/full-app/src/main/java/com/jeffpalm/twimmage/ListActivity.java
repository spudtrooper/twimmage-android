package com.jeffpalm.twimmage;

public final class ListActivity extends com.jeffpalm.tweettoimage.ListActivity {

  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }
}
