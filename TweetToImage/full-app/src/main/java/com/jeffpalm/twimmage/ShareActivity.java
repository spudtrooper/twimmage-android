package com.jeffpalm.twimmage;

public final class ShareActivity extends com.jeffpalm.tweettoimage.ShareActivity {
  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }
}
