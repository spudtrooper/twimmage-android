package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.widget.TemplatedImagePagerAdapter;

import dagger.Binds;
import dagger.Module;

@Module
public interface AppBindingModule {
  @Binds
  ImageRequestor provideImageRequestor(ImageRequestorImpl impl);

  @Binds
  HasSelectedInfo provideHasSelectedInfo(TemplatedImagePagerAdapter adapter);
}
