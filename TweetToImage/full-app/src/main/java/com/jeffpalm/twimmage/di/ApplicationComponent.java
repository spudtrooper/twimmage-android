package com.jeffpalm.twimmage.di;

import com.jeffpalm.tweettoimage.AppBindingModule;
import com.jeffpalm.tweettoimage.ApplicationModule;
import com.jeffpalm.twimmage.CheckTwitterService;
import com.jeffpalm.twimmage.TwimmageApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {com.jeffpalm.twimmage.ApplicationModule.class, ApplicationModule.class,
    AppBindingModule.class})
public interface ApplicationComponent extends com.jeffpalm.tweettoimage.di.ApplicationComponent {
  void inject(TwimmageApplication application);

  void inject(CheckTwitterService service);
}