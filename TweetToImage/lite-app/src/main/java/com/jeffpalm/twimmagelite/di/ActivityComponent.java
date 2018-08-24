package com.jeffpalm.twimmagelite.di;

import com.jeffpalm.tweettoimage.ActivityModule;
import com.jeffpalm.tweettoimage.AppBindingModule;
import com.jeffpalm.tweettoimage.di.PerActivity;
import com.jeffpalm.twimmagelite.EditActivity;
import com.jeffpalm.twimmagelite.SearchActivity;
import com.jeffpalm.twimmagelite.ShareActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,
           modules = {ActivityModule.class, AppBindingModule.class})
public interface ActivityComponent extends com.jeffpalm.tweettoimage.di
    .ActivityComponent {

  void inject(ShareActivity shareActivity);

  void inject(EditActivity editActivity);

  void inject(SearchActivity searchActivity);
}