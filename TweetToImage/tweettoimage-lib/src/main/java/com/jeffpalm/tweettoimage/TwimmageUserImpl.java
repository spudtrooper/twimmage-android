package com.jeffpalm.tweettoimage;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.api.ImmutableTwitterUser;
import com.jeffpalm.tweettoimage.api.TwitterUser;
import com.jeffpalm.tweettoimage.api.TwitterUserImpl;

final class TwimmageUserImpl implements TwimmageUser {

  private final static TwitterUser EMPTY_TWITTER_USER = new ImmutableTwitterUser("", "", "", "");
  private TwitterUser twitterUser;

  private TwimmageUserImpl() {
  }

  @Override
  public TwitterUser getTwitterUser() {
    return twitterUser;
  }

  @Override
  public void setTwitterUser(@Nullable TwitterUser twitterUser) {
    this.twitterUser = twitterUser != null ? twitterUser : EMPTY_TWITTER_USER;
  }

  @Override
  public boolean isLoggedIn() {
    return !Strings.isNullOrEmpty(twitterUser.getName());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }

  final static class Persister {
    private final static String TWITTER_USER_NAME_KEY = "twitterUserName";
    private final static String TWITTER_USER_SCREEN_NAME_KEY = "twitterUserScreenName";
    private final static String TWITTER_USER_PROFILE_URL_KEY = "twitterUserProfileUrl";
    private final static String TWITTER_USER_DESCRIPTION_KEY = "twitterUserDescription";

    public static TwimmageUser readFromPreferences(SharedPreferences prefs) {
      TwitterUser twitterUser = read(prefs);
      TwimmageUserImpl res = new TwimmageUserImpl();
      res.setTwitterUser(twitterUser);
      return res;
    }

    private static TwitterUser read(SharedPreferences prefs) {
      String name = prefs.getString(TWITTER_USER_NAME_KEY, "");
      String screenName = prefs.getString(TWITTER_USER_SCREEN_NAME_KEY, "");
      String profileUrl = prefs.getString(TWITTER_USER_PROFILE_URL_KEY, "");
      String description = prefs.getString(TWITTER_USER_DESCRIPTION_KEY, "");
      if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(screenName)) {
        return EMPTY_TWITTER_USER;
      }
      return new TwitterUserImpl(name, screenName, profileUrl, description);
    }

    public static void save(TwimmageUser user, SharedPreferences prefs) {
      SharedPreferences.Editor edit = prefs.edit();
      TwitterUser twitterUser = user.getTwitterUser();
      if (twitterUser != null) {
        edit.putString(TWITTER_USER_NAME_KEY, twitterUser.getName());
        edit.putString(TWITTER_USER_SCREEN_NAME_KEY, twitterUser.getScreenName());
        edit.putString(TWITTER_USER_PROFILE_URL_KEY, twitterUser.getOriginalProfileImageURL());
        edit.putString(TWITTER_USER_DESCRIPTION_KEY, twitterUser.getDescription());
      } else {
        edit.remove(TWITTER_USER_NAME_KEY);
        edit.remove(TWITTER_USER_SCREEN_NAME_KEY);
        edit.remove(TWITTER_USER_PROFILE_URL_KEY);
        edit.remove(TWITTER_USER_DESCRIPTION_KEY);
      }
      edit.commit();
    }
  }
}
