package com.jeffpalm.tweettoimage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TwitterUtilTest {

  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm:ss yyyy-MM-DD",
      Locale.getDefault());

  @Test
  public void getAmountString() {
    Context context = InstrumentationRegistry.getTargetContext();
    assertEquals("13", TwitterUtil.getAmountString(context, 13));
  }

  @Test
  public void getTimeString() {
    testGetTimeString("13:00:00 2018-01-01", "13:00:00 2018-01-01", "0s");
    testGetTimeString("13:00:00 2018-01-01", "13:00:02 2018-01-01", "2s");
    testGetTimeString("13:00:00 2018-01-01", "13:02:02 2018-01-01", "2m");
    testGetTimeString("13:00:00 2018-01-01", "15:02:02 2018-01-01", "2h");
    testGetTimeString("13:00:00 2018-01-01", "15:02:02 2018-01-03", "2d");
    testGetTimeString("13:00:00 2018-01-01", "15:02:02 2020-01-03", "2y");
  }

  private void testGetTimeString(String subjectDateString, String nowDateString, String expected) {
    Context context = InstrumentationRegistry.getTargetContext();
    Date subject = parseDate(subjectDateString);
    Date now = parseDate(nowDateString);
    assertEquals(expected, TwitterUtil.getTimeString(context, subject, now));
  }

  private Date parseDate(String dateString) {
    try {
      return DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      org.junit.Assert.fail("Couldn't parse date " + dateString);
      return null;
    }
  }
}