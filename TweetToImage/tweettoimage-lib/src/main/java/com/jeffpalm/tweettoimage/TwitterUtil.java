package com.jeffpalm.tweettoimage;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TwitterUtil {

  private static final String TAG = TwitterUtil.class.getCanonicalName();

  // Sat Jun 23 15:30:06 GMT-06:00 2018
  private final static SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat(
      "EEE MMM d HH:mm:ss zzz yyyy",
      Locale.getDefault());
  private final static SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat(
      "h:mm a - d MMM yyyy",
      Locale.getDefault());

  private TwitterUtil() {
  }

  public static String getTimeString(Context c, Date subject) {
    Date now = new Date(System.currentTimeMillis());
    return getTimeString(c, subject, now);
  }

  @VisibleForTesting
  static String getTimeString(Context c, Date subject, Date now) {
    long diffMillis = now.getTime() - subject.getTime();
    long diffSeconds = diffMillis / 1000;
    long diffMinutes = diffSeconds / 60;
    long diffHours = diffMinutes / 60;
    long diffDays = diffHours / 24;
    long diffYears = diffDays / 365;
    Log.d(TAG,
        "date=" + subject + " diffMillis=" + diffMillis + " diffSeconds=" + diffSeconds + " " +
            "diffMinutes=" + diffMinutes + " diffHours=" + diffHours + " diffDays=" + diffDays +
            "diffYears=" + diffYears);
    if (diffMinutes < 1) {
      return c.getString(R.string.time_seconds, diffSeconds);
    }
    if (diffHours < 1) {
      return c.getString(R.string.time_minutes, diffMinutes);
    }
    if (diffDays < 1) {
      return c.getString(R.string.time_hours, diffHours);
    }
    if (diffYears < 1) {
      return c.getString(R.string.time_days, diffDays);
    }
    return c.getString(R.string.time_years, diffYears);
  }

  public static String getAmountString(Context c, int amount) {
    if (amount < 1e3) {
      return c.getString(R.string.amount_lt_e3, amount);
    }
    if (amount < 1e6) {
      return c.getString(R.string.amount_lt_e6, amount / 1000);
    }
    if (amount < 1e9) {
      return c.getString(R.string.amount_lt_e9, amount / 1000 / 1000);
    }
    return c.getString(R.string.amount_lt_e12, amount / 1000 / 1000 / 1000);
  }

  public static String formatDate(String dateString) {
    try {
      Date date = INPUT_DATE_FORMAT.parse(dateString);
      return OUTPUT_DATE_FORMAT.format(date);
    } catch (ParseException e) {
      Log.e(TAG, String.format("Parsing date[%s]", dateString), e);
    }
    return dateString;
  }
}
