package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.util.Consumer;
import com.jeffpalm.tweettoimage.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public final class RemoteTemplatesProvider extends TemplatesProviderBase implements
    TemplatesProvider {

  private final Log log = new Log(this);

  private final TemplatesRequestor templatesRequestor;
  private final AtomicBoolean inRequest = new AtomicBoolean(false);
  private final List<LoadListener> loadListeners = new ArrayList<>();
  private Templates cachedTemplates;

  @Inject
  public RemoteTemplatesProvider(TemplatesRequestor templatesRequestor) {
    this.templatesRequestor = templatesRequestor;
  }

  @Override
  public void addLoadListener(LoadListener loadListener) {
    if (cachedTemplates == null) {
      loadListeners.add(loadListener);
    }
  }

  @Override
  public void getTemplates(Consumer<Templates> callback) {
    if (cachedTemplates != null) {
      callback.accept(cachedTemplates);
      return;
    }
    if (!inRequest.getAndSet(true)) {
      templatesRequestor.requestTemplates(templates -> {
        cachedTemplates = templates;
        callback.accept(cachedTemplates);
        inRequest.set(false);
        notifyAndClearLoadListeners();
      });
    }
  }

  @Override
  public void clear() {
    cachedTemplates = null;
  }

  @Override
  public Templates getTemplatesSync() {
    if (cachedTemplates != null) {
      return cachedTemplates;
    }
    if (!inRequest.getAndSet(true)) {
      templatesRequestor.requestTemplates(templates -> {
        cachedTemplates = templates;
        notifyAndClearLoadListeners();
      });
    }
    return TemplatesRequestor.newDefaultInstance();
  }

  @Override
  public boolean isLoaded() {
    return cachedTemplates != null;
  }

  private void notifyAndClearLoadListeners() {
    log.d("Notifying %d load listeners", loadListeners.size());
    for (LoadListener loadListener : loadListeners) {
      loadListener.onLoad();
    }
    loadListeners.clear();
  }
}
