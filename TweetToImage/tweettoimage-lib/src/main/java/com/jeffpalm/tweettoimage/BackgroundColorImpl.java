package com.jeffpalm.tweettoimage;

public final class BackgroundColorImpl implements BackgroundColor {

  private final int color;

  public BackgroundColorImpl(int color) {
    this.color = color;
  }

  @Override
  public int getColor() {
    return color;
  }
}
