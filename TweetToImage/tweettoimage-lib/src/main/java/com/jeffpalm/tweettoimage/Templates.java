package com.jeffpalm.tweettoimage;

import java.util.List;

interface Templates {
  Template getDefault();

  List<Template> getTemplates();
}
