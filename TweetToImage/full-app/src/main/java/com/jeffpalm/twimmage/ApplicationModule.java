package com.jeffpalm.twimmage;

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
        return true;
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
  Class<? extends com.jeffpalm.tweettoimage.ListActivity> getListActivityClass() {
    return com.jeffpalm.twimmage.ListActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.SearchActivity> getSearchActivityClass() {
    return com.jeffpalm.twimmage.SearchActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.EditActivity> getEditActivityClass() {
    return com.jeffpalm.twimmage.EditActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.MainActivity> getMainActivityClass() {
    return com.jeffpalm.twimmage.MainActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.ShareActivity> getShareActivityClass() {
    return com.jeffpalm.twimmage.ShareActivity.class;
  }

  @Provides
  Class<? extends com.jeffpalm.tweettoimage.DebugActivity> getDebugActivityClass() {
    return com.jeffpalm.twimmage.DebugActivity.class;
  }
}