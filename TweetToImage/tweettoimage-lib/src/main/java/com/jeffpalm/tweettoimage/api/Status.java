package com.jeffpalm.tweettoimage.api;

import android.os.Parcelable;

import java.util.Date;

public interface Status extends Parcelable {
  long getId();

  String getText();

  Date getCreatedAt();

  int getFavoriteCount();

  int getRetweetCount();

  TwitterUser getUser();

  Mutable toMutable();

  interface Mutable extends Status {
    Mutable setText(String v);

    Mutable setCreatedAt(Date v);

    Mutable setFavoriteCount(int v);

    Mutable setRetweetCount(int v);

    Mutable setUser(TwitterUser v);

    Status freeze();
  }
}
