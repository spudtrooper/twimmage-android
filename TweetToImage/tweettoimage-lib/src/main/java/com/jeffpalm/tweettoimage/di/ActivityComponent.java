package com.jeffpalm.tweettoimage.di;


import android.app.Activity;
import android.content.Context;

@PerActivity
public interface ActivityComponent {
  @ActivityContext
  Context getContext();

  Activity getActivity();

}