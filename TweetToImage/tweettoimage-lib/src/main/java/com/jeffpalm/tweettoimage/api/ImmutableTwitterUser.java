package com.jeffpalm.tweettoimage.api;

import android.os.Parcel;

public final class ImmutableTwitterUser extends TwitterUserBase implements TwitterUser {
  public final static Creator<TwitterUser> CREATOR = new Creator<TwitterUser>() {

    @Override
    public TwitterUser createFromParcel(Parcel in) {
      return new ImmutableTwitterUser(in);
    }

    @Override
    public TwitterUser[] newArray(int size) {
      return new TwitterUser[size];
    }
  };

  private ImmutableTwitterUser(Parcel in) {
    super(in);
  }

  public ImmutableTwitterUser(String name,
                              String screenName,
                              String originalProfileImageURL,
                              String description) {
    super(name, screenName, originalProfileImageURL, description);
  }

  @Override
  public Mutable toMutable() {
    return new TwitterUserImpl(name, screenName, originalProfileImageURL, description);
  }
}
