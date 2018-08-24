package com.jeffpalm.tweettoimage.util;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextWatcherAdapter implements TextWatcher {
  private final Log log = new Log(this);

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    log.d("beforeTextChanged start=%d count=%d after=%d", start, count, after);
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    log.d("onTextChanged s=%s start=%d before=%d count=%d", s, start, before, count);

  }

  @Override
  public void afterTextChanged(Editable s) {
    log.d("afterTextChanged");
  }
}
