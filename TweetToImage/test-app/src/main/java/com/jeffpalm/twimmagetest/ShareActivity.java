package com.jeffpalm.twimmagetest;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.util.Fakes;

import javax.annotation.Nullable;

public final class ShareActivity extends com.jeffpalm.tweettoimage.ShareActivity {
  private final ActivityComponentProvider activityComponentProvider = new
      ActivityComponentProvider();

  @Override
  protected void configure() {
    activityComponentProvider.getActivityComponent(this).inject(this);
  }

  @Nullable
  @Override
  protected Status getStatusToShowOnCreate() {
    return Fakes.createStatusForTesting();
  }
}
