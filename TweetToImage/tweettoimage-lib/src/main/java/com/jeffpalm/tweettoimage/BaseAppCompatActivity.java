package com.jeffpalm.tweettoimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

abstract class BaseAppCompatActivity extends AppCompatActivity {

  @Inject TOSManager tosManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    configure();
    checkTos();
  }

  protected abstract void configure();

  private void checkTos() {
    tosManager.checkTos(this, () -> {
    }, () -> finish());
  }
}
