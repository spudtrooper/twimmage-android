package com.jeffpalm.tweettoimage.di;

import android.app.Application;
import android.content.Context;

import com.jeffpalm.tweettoimage.ActivityUtil;
import com.jeffpalm.tweettoimage.BackgroundsProvider;
import com.jeffpalm.tweettoimage.TemplatesProvider;
import com.jeffpalm.tweettoimage.TwimmageUser;
import com.jeffpalm.tweettoimage.api.Requests;
import com.jeffpalm.tweettoimage.api.Urls;
import com.jeffpalm.tweettoimage.util.ImageDownloader;

import javax.inject.Named;
import javax.inject.Singleton;

import twitter4j.Twitter;

@Singleton
public interface ApplicationComponent {

  @ApplicationContext
  Context getContext();

  Application getApplication();

  Urls getUrls();

  TwimmageUser getTwimmageUser();

  Twitter getTwitter();

  @Named("loggedInTwitter")
  Twitter getLoggedInTwitter();

  // TODO should go in own module
  Requests getRequests();

  ImageDownloader getImageDownloader();

  // -----------------------------------------------------------------------------------------------
  // To be implemented by apps
  // -----------------------------------------------------------------------------------------------

  ActivityUtil.Options getActivityUtilOptions();

  TemplatesProvider getTemplatesProvider();

  BackgroundsProvider getBackgroundsProvider();

  Class<? extends com.jeffpalm.tweettoimage.ListActivity> getListActivityClass();

  Class<? extends com.jeffpalm.tweettoimage.SearchActivity> getSearchActivityClass();

  Class<? extends com.jeffpalm.tweettoimage.EditActivity> getEditActivityClass();

  Class<? extends com.jeffpalm.tweettoimage.MainActivity> getMainActivityClass();

  Class<? extends com.jeffpalm.tweettoimage.ShareActivity> getShareActivityClass();

  Class<? extends com.jeffpalm.tweettoimage.DebugActivity> getDebugActivityClass();
}