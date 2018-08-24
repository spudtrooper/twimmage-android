package com.jeffpalm.tweettoimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.text.TextWatcher;
import android.widget.ImageView;

import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.ImageDownloader;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.StatusAndTemplateKey;
import com.jeffpalm.tweettoimage.util.TextWatcherAdapter;
import com.jeffpalm.tweettoimage.widget.BackgroundsWithProgress;
import com.jeffpalm.tweettoimage.widget.ClearableEditText;
import com.jeffpalm.tweettoimage.widget.TemplatedImagePagerAdapter;
import com.jeffpalm.tweettoimage.widget.ViewPagerWithProgress;

import javax.inject.Inject;

public abstract class EditActivity extends BaseAppCompatActivity {

  public final static String EXTRA_STATUS_AND_TEMPLATE_KEY = "extraStatusAndTempalateKey";
  public final static String EXTRA_SELECT_ALL = "extraSelectAll";
  private final Log log = new Log(this);
  private final State state = new State();
  @Inject SharingController sharingController;
  @Inject TemplatesProvider templatesProvider;
  @Inject TemplatedImagePagerAdapter pagerAdapter;
  @Inject WriterController writerController;
  @Inject ActivityUtil activityUtil;
  @Inject TwimmageUser twimmageUser;
  @Inject ImageDownloader imageDownloader;
  @Inject ViewPagerAndBackgrounds viewPagerAndBackgrounds;
  private ViewPager viewPager;
  private ClearableEditText tweetText;
  private final TextWatcher textWatcher = new TextWatcherAdapter() {
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      super.onTextChanged(s, start, before, count);
      updateImage();
    }
  };

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    viewPagerAndBackgrounds.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults) {
    writerController.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit);
    activityUtil.onCreate();
    tweetText = activityUtil.findViewById(R.id.tweet_text);

    ViewPagerWithProgress viewPagerWithProgress = activityUtil.findViewById(R.id
        .view_pager_and_progress_container);
    viewPager = viewPagerWithProgress.getViewPager();

    BackgroundsWithProgress backgroundsWithProgress = activityUtil.findViewById(R.id.backgrounds);
    ImageView tweetImageView = activityUtil.findViewById(R.id.tweet_image);

    pagerAdapter.setViewCreationCallback(v -> v.setOnClickListener(view ->
        tryToWriteToStorageAndDownload()));
    pagerAdapter.setHasSelectedPosition(() -> viewPager.getCurrentItem());
    pagerAdapter.setSetsCurrentPosition(pos -> viewPager.setCurrentItem(pos));

    // Render the page adapter after the templates/status has loaded.
    pagerAdapter.setOnReadyListener(viewPagerWithProgress::showViewPager);

    StatusAndTemplateKey statusAndTemplateKey = getIntent().getParcelableExtra(
        EXTRA_STATUS_AND_TEMPLATE_KEY);
    Assert.notNull(statusAndTemplateKey);
    state.status = statusAndTemplateKey.getStatus();
    pagerAdapter.setStatus(statusAndTemplateKey);
    tweetText.setText("");
    String text = statusAndTemplateKey.getStatus().getText();
    if (!Strings.isNullOrEmpty(text)) {
      tweetText.setText(text);
    }

    // Add background actions.
    viewPagerAndBackgrounds.setUp(viewPagerWithProgress,
        backgroundsWithProgress,
        this::handleBackgroundClick);

    // Call addTextChangedListener after setText, so that we don't respond to
    // the initial calls to setText.
    tweetText.addTextChangedListener(textWatcher);
    imageDownloader.download(statusAndTemplateKey.getStatus().getUser()
            .getOriginalProfileImageURL(),
        tweetImageView);
    activityUtil.listenTo(writerController);
    FloatingActionButton fab = activityUtil.findViewById(R.id.fab);
    fab.setOnClickListener(view -> tryToWriteToStorageAndShare());
    tweetText.setTopDrawable(R.drawable.ic_remove);
  }

  private void tryToWriteToStorageAndDownload() {
    writerController.download();
  }

  private void tryToWriteToStorageAndShare() {
    writerController.share();
  }

  private void handleBackgroundClick(Background background) {
    log.d("Use background: %s", background);
    state.background = background;
    updatePagerAdapter();
  }

  private void updatePagerAdapter() {
    pagerAdapter.setStatus(state.status);
    pagerAdapter.setBackground(state.background); // TODO: Consolidate setters
  }

  @Override
  public final void onDestroy() {
    super.onDestroy();
    tweetText.removeTextChangedListener(textWatcher);
  }

  private void updateImage() {
    state.status = state.status.toMutable().setText(getTweetText()).freeze();
    updatePagerAdapter();
  }

  private String getTweetText() {
    return Strings.nullToEmpty(tweetText.getText().toString());
  }

  private final static class State {
    Status status;
    Background background;
  }
}