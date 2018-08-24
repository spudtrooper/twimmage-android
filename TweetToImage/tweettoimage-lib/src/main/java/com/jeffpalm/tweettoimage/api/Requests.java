package com.jeffpalm.tweettoimage.api;

import org.json.JSONObject;

import java.util.Map;

public interface Requests {
  void request(String url,
               Map<String, String> params,
               RequestMethod requestMethod,
               SuccessCallback success,
               ErrorCallback error);

  enum RequestMethod {
    GET, POST
  }

  interface SuccessCallback {
    void handle(JSONObject response);
  }

  interface ErrorCallback {
    void handle(Throwable error);
  }
}
