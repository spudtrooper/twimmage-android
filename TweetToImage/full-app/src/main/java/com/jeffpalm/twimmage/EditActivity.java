package com.jeffpalm.twimmage;

public final class EditActivity extends com.jeffpalm.tweettoimage.EditActivity {

  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }
}
