package com.jeffpalm.tweettoimage.widget;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.MoreObjects;
import com.jeffpalm.tweettoimage.Background;
import com.jeffpalm.tweettoimage.HasSelectedInfo;
import com.jeffpalm.tweettoimage.ImageRequestor;
import com.jeffpalm.tweettoimage.SelectedInfo;
import com.jeffpalm.tweettoimage.Template;
import com.jeffpalm.tweettoimage.TemplatesProvider;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.di.PerActivity;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Consumer;
import com.jeffpalm.tweettoimage.util.ImageDownloader;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.StatusAndTemplateKey;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@PerActivity
public final class TemplatedImagePagerAdapter extends PagerAdapter implements HasSelectedInfo {
  private final Log log = new Log(this);
  private final Activity context;
  private final TemplatesProvider templatesProvider;
  private final ImageRequestor imageRequestor;
  private final ImageDownloader imageDownloader;
  private final State state = new State();
  private final AtomicBoolean haveNotifiedOnReadyListeners = new AtomicBoolean(false);
  private ViewCreationCallback viewCreationCallback;
  private HasSelectedPosition hasSelectedPosition;
  private SetsCurrentPosition setsCurrentPosition;
  private OnReadyListener onReadyListener;

  @Inject
  TemplatedImagePagerAdapter(Activity context,
                             TemplatesProvider templatesProvider,
                             ImageRequestor imageRequestor,
                             ImageDownloader imageDownloader) {
    this.context = context;
    this.templatesProvider = templatesProvider;
    this.imageRequestor = imageRequestor;
    this.imageDownloader = imageDownloader;
    templatesProvider.addLoadListener(this::notifyDataSetChanged);
  }

  public void setOnReadyListener(OnReadyListener onReadyListener) {
    this.onReadyListener = onReadyListener;
    if (isReady()) {
      onReadyListener.onReady();
    } else {
      templatesProvider.addLoadListener(this::maybeNotifyOnReadyListener);
    }
  }

  private boolean isReady() {
    return !templatesProvider.isEmpty() && state.status != null;
  }

  public void setViewCreationCallback(@Nullable ViewCreationCallback viewCreationCallback) {
    this.viewCreationCallback = viewCreationCallback;
  }

  public void setHasSelectedPosition(@Nullable HasSelectedPosition hasSelectedPosition) {
    this.hasSelectedPosition = hasSelectedPosition;
  }

  public void setSetsCurrentPosition(@Nullable SetsCurrentPosition setsCurrentPosition) {
    this.setsCurrentPosition = setsCurrentPosition;
  }

  public void setStatus(StatusAndTemplateKey statusAndTemplateKey) {
    Assert.notNull(setsCurrentPosition);
    int pos = getTemplatePosition(statusAndTemplateKey.getTemplateKey());
    Assert.that(pos >= 0);
    setsCurrentPosition.setSelectedPosition(pos);
    setStatusInternal(statusAndTemplateKey.getStatus());
  }

  private int getTemplatePosition(String templateKey) {
    List<Template> templates = templatesProvider.getTemplates();
    for (int i = 0; i < templates.size(); i++) {
      Template t = templates.get(i);
      if (t.getKey().equals(templateKey)) {
        return i;
      }
    }
    return -1;
  }

  private void setStatusInternal(Status status) {
    Assert.notNull(status);
    log.d("setStatus for status %s : %s", status.getUser().getScreenName(), status.getText());
    state.status = status;
    notifyDataSetChanged();
    maybeNotifyOnReadyListener();
  }

  private void maybeNotifyOnReadyListener() {
    if (isReady() && !haveNotifiedOnReadyListeners.getAndSet(true)) {
      log.d("Notifying on ready listeners");
      onReadyListener.onReady();
    }
  }

  public void setBackground(Background background) {
    log.d("setBackground for background %s", background);
    state.background = background;
    notifyDataSetChanged();
  }

  @Override
  public void getSelectedInfo(Consumer<SelectedInfo> callback) {
    int position = hasSelectedPosition.getSelectedPosition();
    final Template template = templatesProvider.getTemplates().get(position);
    imageRequestor.requestImage(state.status, template, state.background, (s, r) -> {
      SelectedInfo selInfo = new SelectedInfoImpl(template, r.getImageBytes(), s);
      callback.accept(selInfo);
    });
  }

  @Override
  public int getCount() {
    // This won't be called until we have all the templates, so it's safe to
    // call this
    // synchronously.
    return templatesProvider.getTemplates().size();
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    Template template = templatesProvider.getTemplates().get(position);
    TwitterPreviewView previewView = new TwitterPreviewView(context, template);
    previewView.setTag(new Tag(position, true));
    Status status = state.status;
    if (status != null) {
      previewView.setNameText(status.getUser().getName());
      previewView.setScreenNameText(status.getUser().getScreenName());
      previewView.setStatusText(status.getText());
      previewView.setCreatedAtText(status.getCreatedAt().toString());
      if (previewView.getUserImageView() != null) {
        imageDownloader.download(status.getUser().getOriginalProfileImageURL(),
            previewView.getUserImageView());
      }
    }
    if (viewCreationCallback != null) {
      viewCreationCallback.handleViewCreation(previewView);
    }
    container.addView(previewView);
    return previewView;
  }

  /**
   * Destroy the item from the {@link android.support.v4.view.ViewPager}. In
   * our case this is
   * simply removing the
   * {@link View}.
   */
  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  /**
   * @return true if the value returned from
   * {@link #instantiateItem(ViewGroup, int)} is the
   * same object as the {@link View} added to the
   * {@link android.support.v4.view.ViewPager}.
   */
  @Override
  public boolean isViewFromObject(View view, Object object) {
    return object == view;
  }

  @Override
  public int getItemPosition(Object object) {
    // TODO(jpalm): Fix this or else the images aren't reloaded.
    //    ImageView v = (ImageView) object;
    //    Tag tag = (Tag) v.getTag();
    //    return tag.isLoaded ? tag.position : POSITION_NONE;
    return POSITION_NONE;
  }

  // Returns the page title for the top indicator
  @Override
  public CharSequence getPageTitle(int position) {
    return "";
  }

  public Status getStatus() {
    return state.status;
  }

  public void setStatus(Status status) {
    setStatusInternal(status);
  }
  // http://www.tothenew.com/blog/updating-viewpager-with-new-data-dynamically/

  public interface ViewCreationCallback {
    void handleViewCreation(View v);
  }

  public interface HasSelectedPosition {
    int getSelectedPosition();
  }

  public interface SetsCurrentPosition {
    void setSelectedPosition(int position);
  }

  public interface OnReadyListener {
    void onReady();
  }

  private final static class Tag {
    final int position;
    final boolean isLoaded;

    Tag(int position, boolean isLoaded) {
      this.position = position;
      this.isLoaded = isLoaded;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("position", position).add("isLoaded",
          isLoaded).toString();
    }
  }

  private static final class SelectedInfoImpl implements SelectedInfo {
    private final Template template;
    private final byte[] imageBytes;
    private final Status status;

    SelectedInfoImpl(Template template, byte[] imageBytes, Status status) {
      this.template = template;
      this.imageBytes = imageBytes;
      this.status = status;
    }

    @Override
    public Template getTemplate() {
      return template;
    }

    @Override
    public byte[] getImageBytes() {
      return imageBytes;
    }

    @Override
    public Status getStatus() {
      return status;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("template", template).add("imageBytes.length",
          imageBytes.length).toString();
    }
  }

  private final static class State {
    Status status;
    Background background;

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("status", status).add("background",
          background).toString();
    }
  }
}