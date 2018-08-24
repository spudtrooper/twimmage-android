package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.util.Consumer;

import javax.inject.Inject;

public final class SimpleTemplatesProvider extends TemplatesProviderBase implements
    TemplatesProvider {

  private final Templates templates = TemplatesRequestor.newDefaultInstance();

  @Inject
  public SimpleTemplatesProvider() {
  }

  @Override
  public void addLoadListener(LoadListener loadListener) {
    // no op
  }

  @Override
  public void getTemplates(Consumer<Templates> callback) {
    callback.accept(templates);
  }

  @Override
  public void clear() {
    // no op
  }

  @Override
  public Templates getTemplatesSync() {
    return templates;
  }

  @Override
  public boolean isLoaded() {
    return true;
  }
}
