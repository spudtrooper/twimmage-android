package com.jeffpalm.tweettoimage.util;

public interface ConsumerWithErrorHandler<T> extends Consumer<T> {
  void handleError(Throwable t);
}