package com.jeffpalm.tweettoimage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.jeffpalm.tweettoimage.api.TwitterUser;

import javax.inject.Inject;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public final class LoginController {

  private static final String PREF_KEY_ACCESS_SECRET = "accessSecret";
  private static final String PREF_KEY_ACCESS_TOKEN = "accessToken";

  private final TwimmageUser twimmageUser;
  private final Application application;

  @Inject
  LoginController(TwimmageUser twimmageUser, Application application) {
    this.twimmageUser = twimmageUser;
    this.application = application;
  }

  /**
   * Exposed for the application.
   */
  public static SharedPreferences getLoginSharedPreferences(Application application) {
    return application.getSharedPreferences("login", Context.MODE_PRIVATE);
  }

  public void loginUser(User user) {
    TwitterUser twitterUser = Twitter4jTwimmageAdapter.createInstance(user);
    twimmageUser.setTwitterUser(twitterUser);
    TwimmageUserImpl.Persister.save(twimmageUser, getLoginSharedPreferences());
  }

  private SharedPreferences getLoginSharedPreferences() {
    return getLoginSharedPreferences(application);
  }

  public void logoutUser() {
    twimmageUser.setTwitterUser(null);
    TwimmageUserImpl.Persister.save(twimmageUser, getLoginSharedPreferences());
    SharedPreferences prefs = getLoginSharedPreferences();
    SharedPreferences.Editor editor = prefs.edit();
    editor.remove(PREF_KEY_ACCESS_TOKEN);
    editor.remove(PREF_KEY_ACCESS_SECRET);
    editor.commit();
  }

  public void setAccessCredentials(AccessToken accessToken) {
    SharedPreferences prefs = getLoginSharedPreferences();
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PREF_KEY_ACCESS_TOKEN, accessToken.getToken());
    editor.putString(PREF_KEY_ACCESS_SECRET, accessToken.getTokenSecret());
    editor.apply();
  }

  private String getTwitterAccessToken(boolean forceLoggedInCredentials) {
    if (forceLoggedInCredentials || twimmageUser.isLoggedIn()) {
      SharedPreferences prefs = getLoginSharedPreferences();
      return prefs.getString(PREF_KEY_ACCESS_TOKEN, "");
    } else {
      return BuildConfig.TwitterAccessToken;
    }
  }

  private String getTwitterAccessSecret(boolean forceLoggedInCredentials) {
    if (forceLoggedInCredentials || twimmageUser.isLoggedIn()) {
      SharedPreferences prefs = getLoginSharedPreferences();
      return prefs.getString(PREF_KEY_ACCESS_SECRET, "");
    } else {
      return BuildConfig.TwitterAccessTokenSecret;
    }
  }

  public Twitter getTwitter() {
    return getTwitter(true);
  }

  public Twitter getTwitter(boolean forceLoggedInCredentials) {
    Configuration configuration = new ConfigurationBuilder().setOAuthConsumerKey(BuildConfig
        .TwitterConsumerKey).setOAuthConsumerSecret(
        BuildConfig.TwitterConsumerSecret).setOAuthAccessToken(getTwitterAccessToken(
        forceLoggedInCredentials)).setOAuthAccessTokenSecret(getTwitterAccessSecret(
        forceLoggedInCredentials)).setTweetModeExtended(true).build();
    TwitterFactory tf = new TwitterFactory(configuration);
    return tf.getInstance();
  }
}
