package com.jeffpalm.tweettoimage;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.jeffpalm.tweettoimage.di.ActivityContext;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

  private final AppCompatActivity mActivity;

  public ActivityModule(AppCompatActivity activity) {
    mActivity = activity;
  }

  @Provides
  @ActivityContext
  Context provideContext() {
    return mActivity;
  }

  @Provides
  Activity provideActivity() {
    return mActivity;
  }
}