package com.jeffpalm.twimmagelite;

public final class SearchActivity extends com.jeffpalm.tweettoimage
    .SearchActivity {

  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }

}
