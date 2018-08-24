package com.jeffpalm.tweettoimage.api;

import com.jeffpalm.tweettoimage.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

final class JSONObjectSerializer {

  private final Log log = new Log(this);

  public @Nullable
  String serialize(JSONObject response) {
    try {
      return response.toString(1);
    } catch (JSONException e) {
      log.e(e, "serializing: %s", response);
    }
    return null;
  }

  public @Nullable
  JSONObject deserialize(String serialized) {
    try {
      return new JSONObject(serialized);
    } catch (JSONException e) {
      log.e(e, "deserializing: %s", serialized);
    }
    return null;
  }
}
