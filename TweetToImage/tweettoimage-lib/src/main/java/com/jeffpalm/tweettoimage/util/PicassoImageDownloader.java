package com.jeffpalm.tweettoimage.util;

import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public final class PicassoImageDownloader implements ImageDownloader {
  @Override
  public void download(String url, ImageView... imageViews) {
    for (ImageView imageView : imageViews) {
      imageView.setImageBitmap(null);
      imageView.setVisibility(View.VISIBLE);
      Picasso.get().load(url).into(imageView);
    }
  }
}
