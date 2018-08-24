package com.jeffpalm.twimmage.di;


import com.jeffpalm.tweettoimage.ActivityModule;
import com.jeffpalm.tweettoimage.AppBindingModule;
import com.jeffpalm.tweettoimage.di.PerActivity;
import com.jeffpalm.twimmage.DebugActivity;
import com.jeffpalm.twimmage.EditActivity;
import com.jeffpalm.twimmage.ListActivity;
import com.jeffpalm.twimmage.MainActivity;
import com.jeffpalm.twimmage.SearchActivity;
import com.jeffpalm.twimmage.ShareActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,
           modules = {ActivityModule.class, AppBindingModule.class})
public interface ActivityComponent extends com.jeffpalm.tweettoimage.di.ActivityComponent {

  void inject(ListActivity listActivity);

  void inject(ShareActivity shareActivity);

  void inject(EditActivity editActivity);

  void inject(MainActivity mainActivity);

  void inject(SearchActivity searchActivity);

  void inject(DebugActivity debugActivity);

}