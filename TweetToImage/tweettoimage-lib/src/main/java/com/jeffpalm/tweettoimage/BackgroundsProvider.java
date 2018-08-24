package com.jeffpalm.tweettoimage;

import com.google.common.collect.ImmutableList;
import com.jeffpalm.tweettoimage.util.Consumer;

import java.util.List;

import javax.inject.Inject;

public final class BackgroundsProvider {
  private final BackgroundsRequestor backgroundsRequestor;
  private Backgrounds cachedBackgrounds;

  @Inject
  BackgroundsProvider(BackgroundsRequestor backgroundsRequestor) {
    this.backgroundsRequestor = backgroundsRequestor;
  }

  public void getBackgrounds(Consumer<Backgrounds> callback) {
    if (cachedBackgrounds != null) {
      callback.accept(cachedBackgrounds);
      return;
    }
    backgroundsRequestor.requestDownloads(backgrounds -> {
      cachedBackgrounds = new BackgroundsImpl(ImmutableList.copyOf(backgrounds));
      callback.accept(cachedBackgrounds);
    });
  }

  public void clear() {
    cachedBackgrounds = null;
  }

  public interface Backgrounds {
    Background getDefault();

    List<Background> getCustoms();
  }

  private final static class BackgroundsImpl implements Backgrounds {
    private final Background defaultBackground = new Background() {};
    private final List<Background> customBackgrounds;

    private BackgroundsImpl(List<Background> customBackgrounds) {
      this.customBackgrounds = customBackgrounds;
    }

    @Override
    public Background getDefault() {
      return defaultBackground;
    }

    @Override
    public List<Background> getCustoms() {
      return customBackgrounds;
    }
  }
}
