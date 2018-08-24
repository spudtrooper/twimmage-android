package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.util.Consumer;

public interface HasSelectedInfo {
  void getSelectedInfo(Consumer<SelectedInfo> callback);
}
