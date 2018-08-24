package com.jeffpalm.tweettoimage;

import android.app.Activity;
import android.content.Intent;

import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.MessageAnd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public final class SharingController {
  public final static String EXTRA_STATUS = "extra.status";
  private final Log log = new Log(this);
  private final Activity activity;

  @Inject
  SharingController(Activity activity) {
    this.activity = activity;
  }

  public MessageAnd<Boolean> maybeHandleShareIntent(Callback callback) {
    Intent intent = activity.getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (intent.hasExtra(EXTRA_STATUS)) {
      Status status = intent.getParcelableExtra(EXTRA_STATUS);
      Assert.notNull(status);
      callback.handleShare(status);
      return MessageAnd.ofTrue();
    }

    // This is the intent from the share dialog, i.e. other apps.
    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        handleSendText(intent, callback);
        return MessageAnd.ofTrue();
      } else {
        log.d("Don't know how to handle action=%s type=%s", action, type);
        return MessageAnd.ofFalse(String.format("Don't know how to handle action=%s type=%s",
            action,
            type));
      }
    } else {
      log.d("Don't know how to handle action %s", Strings.nullToEmpty(action));
      return MessageAnd.ofFalse(String.format("Don't know how to handle action %s",
          Strings.nullToEmpty(action)));
    }
  }

  private void handleSendText(Intent intent, Callback callback) {
    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    log.d("sharedText: %s", sharedText);
    if (!Strings.isNullOrEmpty(sharedText)) {
      // Example string: Check out @Vilkomerson’s Tweet: https://twitter
      // .com/Vilkomerson/status/1002196669566472192?s=09
      Pattern pattern = Pattern.compile(".*https://twitter\\.com/(\\w+)" + "/status/" + "(\\d+).*");
      Matcher matcher = pattern.matcher(sharedText);
      if (matcher.find()) {
        String user = matcher.group(1);
        long id = Long.parseLong(matcher.group(2));
        callback.handleShare(user, id);
      } else {
        log.d("Couldn't parse sharedText: %s", sharedText);
      }
    } else {
      log.d("Empty sharedText");
    }
  }

  public interface Callback {
    void handleShare(Status status);

    void handleShare(String user, long tweetId);
  }
}
