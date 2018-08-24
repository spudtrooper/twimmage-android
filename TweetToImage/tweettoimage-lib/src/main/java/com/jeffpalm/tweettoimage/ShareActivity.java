package com.jeffpalm.tweettoimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.prefs.Preferences;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.MessageAnd;
import com.jeffpalm.tweettoimage.util.StatusAndTemplateKey;
import com.jeffpalm.tweettoimage.widget.BackgroundsWithProgress;
import com.jeffpalm.tweettoimage.widget.TemplatedImagePagerAdapter;
import com.jeffpalm.tweettoimage.widget.ViewPagerWithProgress;

import javax.annotation.Nullable;
import javax.inject.Inject;

import twitter4j.Twitter;

public abstract class ShareActivity extends BaseAppCompatActivity {
  private final Log log = new Log(this);

  @Inject SharingController sharingController;
  @Inject TemplatesProvider templatesProvider;
  @Inject ImageRequestor imageRequestor;
  @Inject TemplatedImagePagerAdapter pagerAdapter;
  @Inject WriterController writerController;
  @Inject TwimmageUser twimmageUser;
  @Inject ActivityUtil activityUtil;
  @Inject LoginController loginController;
  @Inject Twitter twitter;
  @Inject ViewPagerAndBackgrounds viewPagerAndBackgrounds;

  private TextView clickToShareText;
  private View progressContainer;
  private View imageContainer;
  private ViewPager viewPager;

  @Override
  protected final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share);
    activityUtil.onCreate();

    clickToShareText = activityUtil.findViewById(R.id.click_to_share_text);
    progressContainer = activityUtil.findViewById(R.id.progress_container);
    imageContainer = activityUtil.findViewById(R.id.image_container);

    progressContainer.setVisibility(View.VISIBLE);
    imageContainer.setVisibility(View.INVISIBLE);

    ViewPagerWithProgress viewPagerWithProgress = activityUtil.findViewById(R.id
        .view_pager_and_progress_container);
    viewPager = viewPagerWithProgress.getViewPager();

    BackgroundsWithProgress backgroundsWithProgress = activityUtil.findViewById(R.id.backgrounds);

    pagerAdapter.setViewCreationCallback(v -> {
      v.setOnClickListener(view -> tryToWriteToStorageAndDownload());
      v.setOnLongClickListener(view -> {
        editImage();
        return true;
      });
    });
    pagerAdapter.setHasSelectedPosition(() -> viewPager.getCurrentItem());
    pagerAdapter.setSetsCurrentPosition(pos -> viewPager.setCurrentItem(pos));

    // Render the page adapter after the templates/status has loaded.
    pagerAdapter.setOnReadyListener(viewPagerWithProgress::showViewPager);

    activityUtil.listenTo(writerController);

    FloatingActionButton fab = activityUtil.findViewById(R.id.fab);
    fab.setOnClickListener(view -> tryToWriteToStorageAndShare());

    // Add background actions.
    viewPagerAndBackgrounds.setUp(viewPagerWithProgress,
        backgroundsWithProgress,
        this::handleBackgroundClick);

    Status status = getStatusToShowOnCreate();
    if (status != null) {
      showStatus(status, false /* share */);
    } else {
      MessageAnd<Boolean> shared = sharingController.maybeHandleShareIntent(new SharingController
          .Callback() {

        @Override
        public void handleShare(Status status) {
          showStatus(status, true /* share */);
        }

        @Override
        public void handleShare(String user, long tweetId) {
          maybeDownloadTweetImage(user, tweetId);
        }
      });
      if (!shared.getValue()) {
        Toast.makeText(getApplicationContext(),
            String.format("Could not share this message on twimmage. Reason: " + "%s",
                shared.getMessage()),
            Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    viewPagerAndBackgrounds.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public final void onRequestPermissionsResult(int requestCode,
                                               String permissions[],
                                               int[] grantResults) {
    writerController.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void tryToWriteToStorageAndDownload() {
    writerController.download();
  }

  private void editImage() {
    log.d("Edit status: %s", pagerAdapter.getStatus());
    Intent intent = new Intent(this, EditActivity.class);
    intent.putExtra(EditActivity.EXTRA_STATUS_AND_TEMPLATE_KEY,
        StatusAndTemplateKey.newInstance(pagerAdapter.getStatus(), getSelectedTemplate()));
    startActivity(intent);
  }

  private void tryToWriteToStorageAndShare() {
    writerController.share();
  }

  /**
   * Override this method to force a status to show on create.
   */
  protected @Nullable
  Status getStatusToShowOnCreate() {
    return null;
  }

  private void showStatus(Status status, boolean share) {
    pagerAdapter.setStatus(status);
    clickToShareText.setVisibility(View.VISIBLE);
    progressContainer.setVisibility(View.INVISIBLE);
    imageContainer.setVisibility(View.VISIBLE);
    if (share) {
      tryToWriteToStorageAndShare();
    }
  }

  private void maybeDownloadTweetImage(String user, long tweetId) {
    if (activityUtil.getBooleanPreference(Preferences.SHARE_AFTER_DOWNLOAD)) {
      downloadTweetImage(user, tweetId);
    }
  }

  private Template getSelectedTemplate() {
    return templatesProvider.getTemplates().get(viewPager.getCurrentItem());
  }

  private void downloadTweetImage(String user, long id) {
    log.d("Download tweet image for user %s and id %d", user, id);
    new ShowStatusTask(twitter) {
      @Override
      protected void onPostExecute(com.jeffpalm.tweettoimage.api.Status status) {
        super.onPostExecute(status);
        showStatus(status, true /* share */);
      }
    }.execute(id);
  }

  private void handleBackgroundClick(Background background) {
    log.d("Use background: %s", background);
    activityUtil.setBackground(viewPager, background);
    pagerAdapter.setBackground(background);
  }
}
