package com.jeffpalm.tweettoimage.api;

import java.util.List;

public interface GetTemplatesResult {
  List<Template> getTemplates();

  Template getDefault();

  interface Template {
    String getKey();
  }
}
