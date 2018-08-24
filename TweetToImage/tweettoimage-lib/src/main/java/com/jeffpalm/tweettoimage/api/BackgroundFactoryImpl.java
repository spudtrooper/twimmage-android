package com.jeffpalm.tweettoimage.api;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

final class BackgroundFactoryImpl implements BackgroundFactory {

  @Override
  public BackgroundFile newBackgroundFile(String fileName) {
    return new BackgroundFileImpl(fileName);
  }

  @Override
  public BackgroundColor newBackgroundColor(int color) {
    return new BackgroundColorImpl(color);
  }

  @Override
  public BackgroundBitmap newBackgroundBitmap(Bitmap bitmap) {
    return new BackgroundBitmapImpl(bitmap);
  }

  private final static class BackgroundFileImpl implements BackgroundFile {

    private final String fileName;

    private BackgroundFileImpl(String fileName) {
      this.fileName = fileName;
    }

    @Override
    public String getFileName() {
      return fileName;
    }
  }

  private final static class BackgroundColorImpl implements BackgroundColor {

    private final int color;

    private BackgroundColorImpl(int color) {
      this.color = color;
    }

    @Override
    public int getColor() {
      return color;
    }
  }

  private final static class BackgroundBitmapImpl implements BackgroundBitmap {

    private final Bitmap bitmap;

    private BackgroundBitmapImpl(Bitmap bitmap) {
      this.bitmap = bitmap;
    }

    @Override
    public String getBitmapString() {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] imageBytes = baos.toByteArray();
      String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
      return encodedImage;
    }
  }
}
