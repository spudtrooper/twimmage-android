package com.jeffpalm.twimmage;

import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@RunWith(AndroidJUnit4.class)
public class CheckTwitterDatabaseHelperTest {

  private CheckTwitterDatabaseHelper helper;

  @Before
  public void setUp() {
    getTargetContext().deleteDatabase(CheckTwitterDatabaseHelper.DATABASE_NAME);
    helper = new CheckTwitterDatabaseHelper(getTargetContext());
  }

  @After
  public void tearDown() {
    helper.close();
  }

  @Test
  public void insert() {
    helper.insert(1);
    assertTrue(helper.exists(1));
  }

  @Test
  public void existsFalse() {
    assertFalse(helper.exists(1));
  }

  @Test
  public void clear() {
    helper.insert(1);
    assertTrue(helper.exists(1));

    helper.clear();
    assertFalse(helper.exists(1));
  }

  @Test
  public void getTwitterNotifications() {
    helper.insert(1);
    helper.insert(2);
    helper.insert(3);

    assertTrue(helper.exists(1));
    assertTrue(helper.exists(2));
    assertTrue(helper.exists(3));

    assertThat(helper.getTwitterNotifications(),
        containsInAnyOrder(matchesTwitterNotification(1),
            matchesTwitterNotification(2),
            matchesTwitterNotification(3)));
  }

  private Matcher<TwitterNotification> matchesTwitterNotification(final long statusId) {
    return new BaseMatcher<TwitterNotification>() {

      @Override
      public boolean matches(Object item) {
        TwitterNotification tn = (TwitterNotification) item;
        return tn.getStatusId() == statusId;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("should have a statusId of ").appendValue(statusId);
      }
    };
  }
}
