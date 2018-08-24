package com.jeffpalm.twimmagetest;

import com.jeffpalm.tweettoimage.ActivityUtil;
import com.jeffpalm.tweettoimage.RemoteTemplatesProvider;
import com.jeffpalm.tweettoimage.TemplatesProvider;
import com.jeffpalm.tweettoimage.TemplatesRequestor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

  @Provides
  @Singleton
  ActivityUtil.Options provideActivityUtilOptions() {
    return new ActivityUtil.Options() {

      @Override
      public boolean shouldShowNagivationView() {
        return false;
      }

      @Override
      public boolean shouldShowTrump() {
        return false;
      }
    };
  }

  @Provides
  @Singleton
  TemplatesProvider providerTemplatesProvider(TemplatesRequestor requestor) {
    return new RemoteTemplatesProvider(requestor);
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.ShareActivity> getShareActivityClass() {
    return com.jeffpalm.twimmagetest.ShareActivity.class;
  }

  // These aren't used. We use abstract classes for List and Main as no-ops. We cannot return null
  // and don't want to create classes that will never be used.

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.ListActivity> getListActivityClass() {
    return com.jeffpalm.tweettoimage.ListActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.MainActivity> getMainActivityClass() {
    return com.jeffpalm.tweettoimage.MainActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.DebugActivity> getDebugActivityClass() {
    return com.jeffpalm.tweettoimage.DebugActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.SearchActivity> getSearchActivityClass() {
    return com.jeffpalm.tweettoimage.SearchActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.EditActivity> getEditActivityClass() {
    return com.jeffpalm.tweettoimage.EditActivity.class;
  }
}