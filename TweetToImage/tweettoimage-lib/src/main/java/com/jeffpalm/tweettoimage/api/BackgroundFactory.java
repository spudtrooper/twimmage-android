package com.jeffpalm.tweettoimage.api;

import android.graphics.Bitmap;

public interface BackgroundFactory {
  BackgroundFile newBackgroundFile(String fileName);

  BackgroundColor newBackgroundColor(int color);

  BackgroundBitmap newBackgroundBitmap(Bitmap bitmap);
}
