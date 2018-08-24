package com.jeffpalm.tweettoimage;

import com.jeffpalm.tweettoimage.util.Consumer;

import java.util.List;

public interface TemplatesProvider {
  /**
   * Adds a lost listener if the cache templates have not been loaded yet. Is
   * not loaded, once
   * loaded this listener will be called as ASAP the new templates are loaded.
   *
   * @param loadListener
   */
  void addLoadListener(LoadListener loadListener);

  void getTemplates(Consumer<Templates> callback);

  void clear();

  List<Template> getTemplates();

  Templates getTemplatesSync();

  Template getDefaultTemplate();

  boolean isLoaded();

  boolean isEmpty();

  interface LoadListener {
    void onLoad();
  }
}
