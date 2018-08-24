package com.jeffpalm.twimmagetest.di;

import com.jeffpalm.tweettoimage.AppBindingModule;
import com.jeffpalm.tweettoimage.ApplicationModule;
import com.jeffpalm.twimmagetest.TwimmageTestApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {com.jeffpalm.twimmagetest.ApplicationModule.class, ApplicationModule.class,
    AppBindingModule.class})
public interface ApplicationComponent extends com.jeffpalm.tweettoimage.di.ApplicationComponent {

  void inject(TwimmageTestApplication application);
}