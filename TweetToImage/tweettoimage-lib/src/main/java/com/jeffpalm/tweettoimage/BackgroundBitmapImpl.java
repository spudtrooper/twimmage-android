package com.jeffpalm.tweettoimage;

import android.graphics.Bitmap;

public final class BackgroundBitmapImpl implements BackgroundBitmap {
  private final Bitmap bitmap;

  public BackgroundBitmapImpl(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  @Override
  public Bitmap getBitmap() {
    return bitmap;
  }
}
