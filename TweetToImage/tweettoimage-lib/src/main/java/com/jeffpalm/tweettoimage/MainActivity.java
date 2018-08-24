package com.jeffpalm.tweettoimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffpalm.tweettoimage.util.Log;

import javax.inject.Inject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Octa
 */
public abstract class MainActivity extends BaseAppCompatActivity {

  public final static String EXTRA_LOGOUT = "logout";
  public final static String EXTRA_LOGIN = "login";
  public static final String EXTRA_CALLBACK_URL_KEY = "extra.callbackUrlKey";
  public static final String EXTRA_AUTH_URL_KEY = "extra.authUrlKey";

  private final static int LOGIN_TO_TWITTER = 1;

  private final Log log = new Log(this);
  @Inject TwimmageUser twimmageUser;
  @Inject LoginController loginController;
  @Inject ActivityUtil activityUtil;
  @Inject Class<? extends ListActivity> listActivityClass;
  private Twitter twitter;
  private Button loginBtn;
  private Button searchBtn;
  private TextView welcomeLabel;
  private ProgressDialog progress;

  @Override
  protected final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    activityUtil.onCreate();
    welcomeLabel = findViewById(R.id.welcome_label);
    loginBtn = activityUtil.findViewById(R.id.login_button);
    loginBtn.setOnClickListener(view -> {
      if (isTwitterLoggedInAlready()) {
        logout();
      } else {
        login();
      }
    });
    searchBtn = activityUtil.findViewById(R.id.search_button);
    searchBtn.setOnClickListener(view -> {
      search();
    });
    if (getIntent().getBooleanExtra(EXTRA_LOGOUT, false)) {
      log.d("Logout");
      logout();
    } else if (getIntent().getBooleanExtra(EXTRA_LOGIN, false)) {
      log.d("Login");
      login();
    } else {
      if (isTwitterLoggedInAlready()) {
        updateLoggedInUI(twimmageUser.getTwitterUser().getName());
      }
    }
  }

  /**
   * Check user already logged in your application using twitter Login flag is
   * fetched from Shared Preferences
   */
  private boolean isTwitterLoggedInAlready() {
    return twimmageUser.isLoggedIn();
  }

  /**
   * Clear prefs and logout
   */
  private void logout() {
    if (twitter != null) {
      twitter.setOAuthAccessToken(null);
    }
    welcomeLabel.setText("");
    loginBtn.setText(R.string.tw_login_hint);
    Toast.makeText(MainActivity.this, getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
    loginController.logoutUser();
  }

  private void login() {
    new TwitterLoginTask().execute();
  }

  private void search() {
    activityUtil.startSearchActivity();
  }

  private void updateLoggedInUI(String username) {
    welcomeLabel.setText(String.format(getString(R.string.welcome), username));
    loginBtn.setText(R.string.tw_logout_hint);
    Intent intent = new Intent(this, listActivityClass);
    startActivity(intent);

  }

  @Override
  public final void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LOGIN_TO_TWITTER) {
      if (resultCode == Activity.RESULT_OK) {
        getAccessToken(data.getStringExtra(MainActivity.EXTRA_CALLBACK_URL_KEY));
      }
    }
  }

  private void getAccessToken(final String callbackUrl) {
    Uri uri = Uri.parse(callbackUrl);
    String verifier = uri.getQueryParameter("oauth_verifier");
    GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
    getAccessTokenTask.execute(verifier);
  }

  private void launchLoginWebView(RequestToken requestToken) {
    Intent intent = new Intent(this, LoginToTwitterActivity.class);
    intent.putExtra(EXTRA_AUTH_URL_KEY, requestToken.getAuthenticationURL());
    startActivityForResult(intent, LOGIN_TO_TWITTER);
  }

  private class TwitterLoginTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      // Check if already logged in
      if (!isTwitterLoggedInAlready()) {
        Configuration configuration = new ConfigurationBuilder().setOAuthConsumerKey(BuildConfig
            .TwitterConsumerKey).setOAuthConsumerSecret(
            BuildConfig.TwitterConsumerSecret).build();
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        try {
          RequestToken requestToken = twitter.getOAuthRequestToken(getString(R.string
              .twitter_callback_url));
          launchLoginWebView(requestToken);

        } catch (TwitterException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    /**
     * After completing background task Dismiss the progress dialog and show
     * the data in UI
     */
    @Override
    protected void onPostExecute(Void result) {
      if (isTwitterLoggedInAlready()) {
        log.w(String.format(getString(R.string.already_logged),
            twimmageUser.getTwitterUser().getName()));
        welcomeLabel.setText(String.format(getString(R.string.welcome),
            twimmageUser.getTwitterUser().getName()));
        loginBtn.setText(R.string.tw_logout_hint);
      }
    }
  }

  private class GetAccessTokenTask extends AsyncTask<String, Void, User> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = ProgressDialog.show(MainActivity.this,
          getString(R.string.loading_title),
          getString(R.string.login_dialog_message),
          true);
    }

    @Override
    protected User doInBackground(String... strings) {
      String verifier = strings[0];
      User user = null;
      try {
        AccessToken accessToken = twitter.getOAuthAccessToken(verifier);
        // store the access token and access token secret in application
        // preferences
        loginController.setAccessCredentials(accessToken);
        log.d("Twitter OAuth Token: " + accessToken.getToken());
        // Getting user details from twitter
        // For now i am getting his name only
        user = twitter.showUser(accessToken.getUserId());

      } catch (Exception e) {
        e.printStackTrace();
      }
      return user;
    }

    @Override
    protected void onPostExecute(User user) {
      super.onPostExecute(user);
      if (user != null) {
        progress.dismiss();
        Toast.makeText(MainActivity.this,
            String.format(getString(R.string.welcome), user.getName()),
            Toast.LENGTH_SHORT).show();
        updateLoggedInUI(user.getName());
        loginController.loginUser(user);
      }
    }
  }
}