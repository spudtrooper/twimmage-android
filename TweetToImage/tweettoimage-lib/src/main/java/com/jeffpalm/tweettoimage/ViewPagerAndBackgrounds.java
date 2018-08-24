package com.jeffpalm.tweettoimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;

import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Consumer;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.widget.BackgroundsWithProgress;
import com.jeffpalm.tweettoimage.widget.TemplatedImagePagerAdapter;
import com.jeffpalm.tweettoimage.widget.ViewPagerWithProgress;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Encapsulates the controls required to interact with a ViewPager and row of
 * backgrounds.
 */
final class ViewPagerAndBackgrounds {

  private final static int PICK_IMAGE_REQUEST = 1;
  private final Log log = new Log(this);
  private final ShowImagePickerDialog showImagePickerDialog;

  private final Activity context;
  private final ActivityUtil activityUtil;
  private final BackgroundsProvider backgroundsProvider;
  private final TemplatesProvider templatesProvider;
  private final TemplatedImagePagerAdapter pagerAdapter;

  @Inject
  ViewPagerAndBackgrounds(Activity context,
                          TemplatesProvider templatesProvider,
                          TemplatedImagePagerAdapter pagerAdapter,
                          ActivityUtil activityUtil,
                          BackgroundsProvider backgroundsProvider) {
    this.context = context;
    this.activityUtil = activityUtil;
    this.templatesProvider = templatesProvider;
    this.pagerAdapter = pagerAdapter;
    this.backgroundsProvider = backgroundsProvider;
    this.showImagePickerDialog = new ShowImagePickerDialog(context, PICK_IMAGE_REQUEST);
  }

  public void setUp(ViewPagerWithProgress viewPagerWithProgress,
                    BackgroundsWithProgress backgroundsWithProgress,
                    Consumer<Background> callback) {
    backgroundsWithProgress.setBitmapProvider(showImagePickerDialog);
    ViewPager viewPager = viewPagerWithProgress.getViewPager();
    viewPager.setAdapter(pagerAdapter);
    backgroundsProvider.getBackgrounds(backgrounds -> {
      backgroundsWithProgress.showBackgrounds(backgrounds, background -> {
        activityUtil.setBackground(viewPager, background);
        pagerAdapter.setBackground(background);
        callback.accept(background);
      });
    });
  }

  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != PICK_IMAGE_REQUEST || resultCode != Activity.RESULT_OK || data == null ||
        data.getData() == null) {
      return false;
    }
    try {
      Assert.notNull(showImagePickerDialog.getConsumer());
      Uri filePath = data.getData();

      Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
      showImagePickerDialog.getConsumer().accept(bitmap);
      return true;
    } catch (IOException e) {
      log.e(e, "converting image");
    }
    return false;
  }
}
