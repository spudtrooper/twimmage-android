package com.jeffpalm.tweettoimage.prefs;

public interface Preferences {

  // TOOD: These should be Preferences, too.
  String PREF_TWEET_SORT_TYPE = "pref_tweet_sort_type";
  String DEFAULT_TEMPLATE = "pref_default_template";
  Preference<Boolean> SHARE_AFTER_DOWNLOAD = PreferenceImpl.of("pref_share_after_download", true);
  Preference<Boolean> INCREMENTAL_SEARCH = PreferenceImpl.of("pref_incremental_search", true);
  Preference<Boolean> NOTIFICATIONS_ENABLED = PreferenceImpl.of("pref_notifications_enabled",
      false);
  Preference<Integer> NOTIFICATIONS_MIN_RETWEETS = PreferenceImpl.of(
      "pref_notifications_min_retweets",
      100);
  Preference<Integer> NOTIFICATIONS_MIN_FAVORITES = PreferenceImpl.of(
      "pref_notifications_min_favorites",
      100);

  interface Preference<T> {
    String getKey();

    T getDefaultValue();
  }
}

