package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.api.Status;

public interface SelectedInfo {
  Template getTemplate();

  byte[] getImageBytes();

  Status getStatus();
}
