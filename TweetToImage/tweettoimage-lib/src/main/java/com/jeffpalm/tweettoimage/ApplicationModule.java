package com.jeffpalm.tweettoimage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.jeffpalm.tweettoimage.api.Api;
import com.jeffpalm.tweettoimage.api.Requests;
import com.jeffpalm.tweettoimage.api.Urls;
import com.jeffpalm.tweettoimage.di.ApplicationContext;
import com.jeffpalm.tweettoimage.util.ImageDownloader;
import com.jeffpalm.tweettoimage.util.PicassoImageDownloader;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import twitter4j.Twitter;

@Module
public class ApplicationModule {
  private final Application mApplication;

  private TwimmageUser twimmageUser;

  public ApplicationModule(Application app) {
    mApplication = app;
  }

  @Provides
  @ApplicationContext
  Context provideContext() {
    return mApplication;
  }

  @Provides
  Application provideApplication() {
    return mApplication;
  }

  @Provides
  @Singleton
  ImageDownloader providesImageDownloader() {
    return new PicassoImageDownloader();
  }

  @Provides
  @Singleton
  Urls provideUrls() {
    if (BuildConfig.DEBUG) {
      return new DevUrls();
    } else {
      return new ProdUrls();
    }
  }

  @Provides
  TwimmageUser provideTwimmageUser() {
    if (twimmageUser == null) {
      twimmageUser = TwimmageUserImpl.Persister.readFromPreferences(getLoginSharedPreferences());
    }
    return twimmageUser;
  }

  private SharedPreferences getLoginSharedPreferences() {
    return mApplication.getSharedPreferences("login", Context.MODE_PRIVATE);
  }

  @Provides
  @Named("loginSharedPreferences")
  SharedPreferences provideLoginSharedPreferences() {
    return getLoginSharedPreferences();
  }

  @Provides
  Twitter provideTwitter(LoginController loginController) {
    return loginController.getTwitter(false);
  }

  @Provides
  @Named("loggedInTwitter")
  Twitter provideLoggedInTwitter(LoginController loginController) {
    return loginController.getTwitter(true);
  }

  @Provides
  @Singleton
  Requests provideRequests(@ApplicationContext Context context) {
    return new TwimmageApiVolleyRequests(context);
  }

  @Provides
  @Singleton
  BackgroundsRequestor provideBackgroundsRequestor(Urls urls,
                                                   Api api,
                                                   @ApplicationContext Context context) {
    return new BackgroundsRequestor(urls, api, context);
  }

  @Provides
  @Singleton
  BackgroundsProvider provideBackgroundsProvider(BackgroundsRequestor requestor) {
    return new BackgroundsProvider(requestor);
  }

  @Provides
  @Singleton
  TemplatesRequestor provideTemplatesRequestor(Api api) {
    return new TemplatesRequestor(api);
  }
}