package com.jeffpalm.tweettoimage.util;

import android.widget.ImageView;

public interface ImageDownloader {
  void download(String url, ImageView... imageViews);
}
