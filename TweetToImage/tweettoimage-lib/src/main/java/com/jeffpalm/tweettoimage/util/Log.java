package com.jeffpalm.tweettoimage.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Log {
  private final String tag;

  public Log(Object o) {
    this(o.getClass());
  }

  private Log(Class cls) {
    this(cls.getCanonicalName());
  }

  public Log(String tag) {
    this.tag = tag;
  }

  private static String toString(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  public void d(String template, Object... args) {
    android.util.Log.d(tag, String.format(template, args));
  }

  public void e(String template, Object... args) {
    android.util.Log.e(tag, String.format(template, args));
  }

  public void w(String template, Object... args) {
    android.util.Log.w(tag, String.format(template, args));
  }

  public void i(String template, Object... args) {
    android.util.Log.i(tag, String.format(template, args));
  }

  public void e(Throwable e, String template, Object... args) {
    android.util.Log.e(tag, String.format(template, args), e);
    android.util.Log.e(tag, toString(e), e);
  }

  public void e(String msg, Throwable e) {
    android.util.Log.e(tag, msg, e);
    android.util.Log.e(tag, toString(e), e);
  }
}