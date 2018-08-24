package com.jeffpalm.tweettoimage;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction().replace(android.R.id.content,
          new MyPreferenceFragment()).commit();
    }
  }

  public static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
      addPreferencesFromResource(R.xml.preferences);
    }
  }
}
