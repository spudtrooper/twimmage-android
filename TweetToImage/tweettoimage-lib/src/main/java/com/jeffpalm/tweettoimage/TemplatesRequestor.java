package com.jeffpalm.tweettoimage;

import com.google.common.collect.ImmutableList;
import com.jeffpalm.tweettoimage.api.Api;
import com.jeffpalm.tweettoimage.api.GetTemplatesResult;
import com.jeffpalm.tweettoimage.util.Consumer;
import com.jeffpalm.tweettoimage.util.Log;

import java.util.List;

import javax.inject.Inject;

public final class TemplatesRequestor {

  private final Log log = new Log(this);
  private final Api api;

  @Inject
  public TemplatesRequestor(Api api) {
    this.api = api;
  }

  static Templates newDefaultInstance() {
    Template defaultTemplate = new TemplateImpl("twitter");
    return new TemplatesImpl(defaultTemplate, ImmutableList.of(defaultTemplate));
  }

  public void requestTemplates(final Consumer<Templates> callback) {
    api.getTemplates(new Api.GetTemplatesHandler() {
      @Override
      public void onSuccess(GetTemplatesResult result) {
        callback.accept(createTemplates(result));
      }

      @Override
      public void onError(Throwable error) {
        log.e(error, "Requesting templates");
      }
    });
  }

  private Templates createTemplates(GetTemplatesResult result) {
    ImmutableList.Builder<Template> templatesBuilder = ImmutableList.builder();
    for (GetTemplatesResult.Template template : result.getTemplates()) {
      templatesBuilder.add(new TemplateImpl(template.getKey()));
    }
    Template defaultTemplate = new TemplateImpl(result.getDefault().getKey());
    return new TemplatesImpl(defaultTemplate, templatesBuilder.build());
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
  }

  private final static class TemplatesImpl implements Templates {
    private final Template defaultTemplate;
    private final List<Template> templates;

    private TemplatesImpl(Template defaultTemplate, List<Template> templates) {
      this.defaultTemplate = defaultTemplate;
      this.templates = templates;
    }

    @Override
    public Template getDefault() {
      return defaultTemplate;
    }

    @Override
    public List<Template> getTemplates() {
      return templates;
    }
  }
}
