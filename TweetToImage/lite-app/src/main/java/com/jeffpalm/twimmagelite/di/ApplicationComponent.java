package com.jeffpalm.twimmagelite.di;

import com.jeffpalm.tweettoimage.AppBindingModule;
import com.jeffpalm.tweettoimage.ApplicationModule;
import com.jeffpalm.twimmagelite.TwimmageLiteApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {com.jeffpalm.twimmagelite.ApplicationModule.class,
    ApplicationModule.class, AppBindingModule.class})
public interface ApplicationComponent extends com.jeffpalm.tweettoimage.di
    .ApplicationComponent {

  void inject(TwimmageLiteApplication application);
}