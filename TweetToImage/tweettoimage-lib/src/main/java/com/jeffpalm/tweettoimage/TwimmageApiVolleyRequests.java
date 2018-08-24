package com.jeffpalm.tweettoimage;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.ImmutableMap;
import com.jeffpalm.tweettoimage.api.Requests;
import com.jeffpalm.tweettoimage.di.ApplicationContext;
import com.jeffpalm.tweettoimage.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Inject;

public final class TwimmageApiVolleyRequests implements Requests {

  private final static int TIMEOUT_MILLIS = 5 * 1000;
  private final static int MAX_RETRIES = 3;
  private final static float BACKOFF_MULTIPLIER = .2f;

  private final Log log = new Log(this);

  private final Context context;
  private final RequestQueue requestQueue;

  @Inject
  public TwimmageApiVolleyRequests(@ApplicationContext Context context) {
    this(context, Volley.newRequestQueue(context));
  }

  private TwimmageApiVolleyRequests(Context context, RequestQueue requestQueue) {
    this.context = context;
    this.requestQueue = requestQueue;
  }

  @Override
  public void request(String url,
                      Map<String, String> params,
                      RequestMethod requestMethod,
                      SuccessCallback success,
                      ErrorCallback errorHandler) {
    StringRequest request = new StringRequest(requestMethod == RequestMethod.POST ? Request
        .Method.POST : Request.Method.GET,
        url,
        response -> {
          log.d("onResponse");
          try {
            //success.handle(new JSONObject(parseNetworkResponse(response)));
            success.handle(new JSONObject(response));
          } catch (JSONException e) {
            errorHandler.handle(e);
            log.e(e, "Creating JSON from response: %s", response);
          }
        },
        error -> errorHandler.handle(error)) {
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        return ImmutableMap.<String, String>builder().putAll(super.getHeaders()).put("Content-Type",
            "application/x-www-form-urlencoded").build();
      }

      @Override
      protected Map<String, String> getParams() {
        return params;
      }
    };
    request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MILLIS, MAX_RETRIES, BACKOFF_MULTIPLIER));
    requestQueue.add(request);
  }

  private String parseNetworkResponse(NetworkResponse response) {
    String parsed;
    try {
      parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
    } catch (UnsupportedEncodingException e) {
      log.e(e, "parsing response");
      parsed = new String(response.data);
    }
    return parsed;
  }
}
