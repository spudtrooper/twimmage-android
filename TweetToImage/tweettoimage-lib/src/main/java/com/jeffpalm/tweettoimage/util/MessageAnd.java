package com.jeffpalm.tweettoimage.util;

public class MessageAnd<T> {
  private final T value;
  private final String message;

  private MessageAnd(T value, String message) {
    this.value = value;
    this.message = message;
  }

  public static MessageAnd<Boolean> ofTrue(String message) {
    return new MessageAnd<>(true, message);
  }

  public static MessageAnd<Boolean> ofTrue() {
    return new MessageAnd<>(true, "");
  }

  public static MessageAnd<Boolean> ofFalse(String message) {
    return new MessageAnd<>(false, message);
  }

  public T getValue() {
    return value;
  }

  public String getMessage() {
    return message;
  }
}
