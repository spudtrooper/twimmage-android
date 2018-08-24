package com.jeffpalm.tweettoimage.api;

import android.os.Parcelable;

public interface TwitterUser extends Parcelable {
  String getScreenName();

  String getName();

  String getOriginalProfileImageURL();

  String getDescription();

  Mutable toMutable();

  interface Mutable extends TwitterUser {
    Mutable setName(String s);

    Mutable setScreenName(String s);

    Mutable setOriginalProfileImageURL(String s);

    Mutable setDescription(String description);

    TwitterUser freeze();
  }
}
