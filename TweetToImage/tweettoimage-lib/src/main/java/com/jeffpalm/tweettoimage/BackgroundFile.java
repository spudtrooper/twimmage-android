package com.jeffpalm.tweettoimage;

import android.content.Context;

public interface BackgroundFile extends Background {
  String getFileName();

  void getPreviewImage(Context context, BitmapCallback callback);

  void getLargeImage(Context context, BitmapCallback callback);
}