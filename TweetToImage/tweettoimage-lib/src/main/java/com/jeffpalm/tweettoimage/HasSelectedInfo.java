package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.util.ConsumerWithErrorHandler;

public interface HasSelectedInfo {
  void getSelectedInfo(ConsumerWithErrorHandler<SelectedInfo> callback);
}
