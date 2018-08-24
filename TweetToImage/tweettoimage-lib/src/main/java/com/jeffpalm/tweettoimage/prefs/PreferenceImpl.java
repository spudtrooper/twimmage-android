package com.jeffpalm.tweettoimage.prefs;

import com.google.common.base.MoreObjects;

final class PreferenceImpl<T> implements Preferences.Preference {
  private final String key;
  private final T defaultValue;

  private PreferenceImpl(String key, T defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  static <T> Preferences.Preference<T> of(String key, T defaultValue) {
    return new PreferenceImpl(key, defaultValue);
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
