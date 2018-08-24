package com.jeffpalm.tweettoimage.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Strings;

public final class PreferencesUtil {

  private PreferencesUtil() {
  }

  public static boolean getBooleanPreference(Context context,
                                             Preferences.Preference<Boolean> pref) {
    return getSharedPreferences(context).getBoolean(pref.getKey(), pref.getDefaultValue());
  }

  public static SharedPreferences getSharedPreferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
  }

  public static int getIntPreference(Context context, Preferences.Preference<Integer> pref) {
    return getSharedPreferences(context).getInt(pref.getKey(), pref.getDefaultValue());
  }

  public static int getIntFromStringPreference(Context context,
                                               Preferences.Preference<Integer> pref) {
    String val = getSharedPreferences(context).getString(pref.getKey(), "");
    return Strings.isNullOrEmpty(val) ? pref.getDefaultValue() : Integer.parseInt(val);
  }
}
