package com.jeffpalm.tweettoimage.util;

public final class Assert {

  private Assert() {
  }

  public static <T> void notNull(T value, String template, Object... objs) {
    assertThat(value != null, template, objs);
  }

  private static void assertThat(boolean expr, String template, Object... objs) {
    if (template == null) {
      if (!expr) {
        throw new AssertionError();
      }
    } else {
      if (!expr) {
        throw new AssertionError(String.format(template, objs));
      }
    }
  }

  public static void that(boolean expr, String template, Object... objs) {
    assertThat(expr, template, objs);
  }

  public static void that(boolean expr) {
    assertThat(expr, null);
  }

  public static <T> T notNull(T value) {
    assertThat(value != null, null);
    return value;
  }

  public static void eq(int expected, int actual) {
    assertThat(expected == actual, "%d != %d", expected, actual);
  }

  public static Object eq(Object expected, Object actual) {
    assertThat(expected == actual, "%d != %d", expected, actual);
    return actual;
  }

  public static <T> void isNull(T value) {
    assertThat(value == null, null);
  }
}
