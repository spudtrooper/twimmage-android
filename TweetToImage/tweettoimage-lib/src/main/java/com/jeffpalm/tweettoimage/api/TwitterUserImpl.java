package com.jeffpalm.tweettoimage.api;

import android.os.Parcel;

import com.google.common.base.MoreObjects;

public final class TwitterUserImpl extends TwitterUserBase implements TwitterUser,
    TwitterUser.Mutable {
  public final static Creator<TwitterUser> CREATOR = new Creator<TwitterUser>() {

    @Override
    public TwitterUser createFromParcel(Parcel in) {
      return new TwitterUserImpl(in);
    }

    @Override
    public TwitterUser[] newArray(int size) {
      return new TwitterUser[size];
    }
  };

  private TwitterUserImpl(Parcel in) {
    super(in);
  }

  public TwitterUserImpl(String name,
                         String screenName,
                         String originalProfileImageURL,
                         String description) {
    super(name, screenName, originalProfileImageURL, description);
  }

  @Override
  public Mutable toMutable() {
    return this;
  }

  // Mutable interface

  @Override
  public Mutable setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Mutable setScreenName(String screenName) {
    this.screenName = screenName;
    return this;
  }

  @Override
  public Mutable setOriginalProfileImageURL(String originalProfileImageURL) {
    this.originalProfileImageURL = originalProfileImageURL;
    return this;
  }

  @Override
  public Mutable setDescription(String description) {
    this.description = description;
    return this;
  }

  @Override
  public TwitterUser freeze() {
    return this;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
