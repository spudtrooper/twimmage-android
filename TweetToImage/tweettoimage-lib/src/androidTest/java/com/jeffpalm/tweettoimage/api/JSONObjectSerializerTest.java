package com.jeffpalm.tweettoimage.api;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class JSONObjectSerializerTest {

  private JSONObjectSerializer serializer;

  @Before
  public void setUp() {
    serializer = new JSONObjectSerializer();
  }

  @Test
  public void serialize() throws JSONException {
    JSONObject o = new JSONObject("{a: 1}");
    assertEquals("{\n \"a\": 1\n}", serializer.serialize(o));
  }

  @Test
  public void deserialize() throws JSONException {
    JSONObject o = serializer.deserialize("{a: 1}");
    assertEquals(1, o.getInt("a"));
  }
}
