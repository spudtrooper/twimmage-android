package com.jeffpalm.tweettoimage.api;

import com.google.common.base.MoreObjects;

import java.util.List;

final class GetTemplatesResultImpl implements GetTemplatesResult {

  private final List<Template> templates;
  private final Template defaultTemplate;

  public GetTemplatesResultImpl(List<Template> templates, Template defaultTemplate) {
    this.templates = templates;
    this.defaultTemplate = defaultTemplate;
  }

  public static Template newTemplateInstance(String key) {
    return new TemplateImpl(key);
  }

  @Override
  public List<Template> getTemplates() {
    return templates;
  }

  @Override
  public Template getDefault() {
    return defaultTemplate;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("templates", templates).add("defaultTemplate",
        defaultTemplate).toString();
  }

  private final static class TemplateImpl implements Template {

    private final String key;

    TemplateImpl(String key) {
      this.key = key;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("key", key).toString();
    }
  }
}
