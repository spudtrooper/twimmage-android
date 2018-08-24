package com.jeffpalm.tweettoimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Octa
 */
public final class LoginToTwitterActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_to_twitter);
    Intent intent = getIntent();
    String mUrl = intent.getStringExtra(MainActivity.EXTRA_AUTH_URL_KEY);
    WebView webView = findViewById(R.id.webViewLoginToTwitter);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(new LoginToTwitterWebViewClient());
    webView.loadUrl(mUrl);
  }

  private class LoginToTwitterWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.startsWith(getString(R.string.twitter_callback_url))) {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_CALLBACK_URL_KEY, url);
        setResult(Activity.RESULT_OK, intent);
        finish();
      }
      return false;
    }
  }
}