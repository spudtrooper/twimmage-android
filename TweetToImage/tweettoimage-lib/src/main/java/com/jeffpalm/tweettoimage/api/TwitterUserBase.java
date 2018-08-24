package com.jeffpalm.tweettoimage.api;

import android.os.Parcel;

import com.jeffpalm.tweettoimage.util.Log;

abstract class TwitterUserBase implements TwitterUser {
  private final Log log = new Log(this);

  String name;
  String screenName;
  String originalProfileImageURL;
  String description;

  TwitterUserBase(Parcel in) {
    readFromParcel(in);
  }

  TwitterUserBase(String name,
                  String screenName,
                  String originalProfileImageURL,
                  String description) {
    this.name = name;
    this.screenName = screenName;
    this.originalProfileImageURL = originalProfileImageURL;
    this.description = description;
  }

  private void readFromParcel(Parcel in) {
    name = in.readString();
    screenName = in.readString();
    originalProfileImageURL = in.readString();
    description = in.readString();
  }

  @Override
  public final String getScreenName() {
    return screenName;
  }

  @Override
  public final String getName() {
    return name;
  }

  @Override
  public final String getOriginalProfileImageURL() {
    return originalProfileImageURL;
  }
  // Parcelable interface

  @Override
  public final String getDescription() {
    return description;
  }

  @Override
  public final int describeContents() {
    return 0;
  }

  @Override
  public final void writeToParcel(Parcel out, int i) {
    log.d("writeToParcel: %s", this);
    out.writeString(name);
    out.writeString(screenName);
    out.writeString(originalProfileImageURL);
    out.writeString(description);
  }
}
