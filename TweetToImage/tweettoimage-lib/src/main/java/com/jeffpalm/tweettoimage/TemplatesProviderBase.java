package com.jeffpalm.tweettoimage;

import java.util.List;

abstract class TemplatesProviderBase implements TemplatesProvider {

  @Override
  public final List<Template> getTemplates() {
    return getTemplatesSync().getTemplates();
  }

  @Override
  public final Template getDefaultTemplate() {
    return getTemplatesSync().getDefault();
  }

  @Override
  public boolean isEmpty() {
    return getTemplates().isEmpty();
  }
}
