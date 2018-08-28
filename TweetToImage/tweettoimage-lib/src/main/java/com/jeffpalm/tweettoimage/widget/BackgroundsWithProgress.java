package com.jeffpalm.tweettoimage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.jeffpalm.tweettoimage.ActivityUtil;
import com.jeffpalm.tweettoimage.Background;
import com.jeffpalm.tweettoimage.BackgroundBitmapImpl;
import com.jeffpalm.tweettoimage.BackgroundColorImpl;
import com.jeffpalm.tweettoimage.BackgroundFile;
import com.jeffpalm.tweettoimage.BackgroundsProvider;
import com.jeffpalm.tweettoimage.R;
import com.jeffpalm.tweettoimage.util.ColorUtils;
import com.jeffpalm.tweettoimage.util.Consumer;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.ViewUtil;

/**
 * Wraps a Horizontal Scroll with templates and progress bar.
 */
public final class BackgroundsWithProgress extends LinearLayout {

  private final static int BACKGROUND_PREVIEW_TEXT_SIZE = 10;
  private final Log log = new Log(this);
  private BitmapProvider bitmapProvider;
  private ProgressBar progressBar;
  private HorizontalScrollView scrollView;
  private ViewGroup templatesContent;

  public BackgroundsWithProgress(Context context) {
    super(context);
    init();
  }

  public BackgroundsWithProgress(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BackgroundsWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void setBitmapProvider(BitmapProvider bitmapProvider) {
    this.bitmapProvider = bitmapProvider;
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.backgrounds_with_progress, this, true);
    setOrientation(LinearLayout.HORIZONTAL);
    setBackgroundResource(android.R.color.white);
    // This needs to be 2 for the progress bar to show up.
    setWeightSum(2);

    progressBar = ActivityUtil.findViewById(this, R.id.backgrounds_progress);
    scrollView = ActivityUtil.findViewById(this, R.id.backgrounds_container);
    templatesContent = ActivityUtil.findViewById(this, R.id.backgrounds_content);

    showProgress();
  }

  private void showProgress() {
    progressBar.setVisibility(View.VISIBLE);
    scrollView.setVisibility(View.INVISIBLE);
    templatesContent.setVisibility(View.INVISIBLE);
  }

  private void hideProgress() {
    progressBar.setVisibility(View.GONE);
    scrollView.setVisibility(View.VISIBLE);
    templatesContent.setVisibility(View.VISIBLE);
  }

  /**
   * Shows and clears existing backgrounds if present.
   */
  public void showBackgrounds(BackgroundsProvider.Backgrounds backgrounds,
                              Consumer<Background> callback) {
    hideProgress();
    clearExistingBackgrounds();
    new BackgroundButtonWrapper(callback).show(backgrounds);
  }

  private void clearExistingBackgrounds() {
    ViewGroup backgroundsContent = findViewById(R.id.backgrounds_content);
    backgroundsContent.removeAllViews();
  }

  public interface BitmapProvider {
    void provide(Consumer<Bitmap> consumer);
  }

  private final class BackgroundButtonWrapper {
    private final Consumer<Background> callback;

    private Integer selectedColor;

    BackgroundButtonWrapper(Consumer<Background> callback) {
      this.callback = callback;
    }

    public void show(BackgroundsProvider.Backgrounds backgrounds) {
      ViewGroup backgroundsContent = findViewById(R.id.backgrounds_content);
      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context
          .LAYOUT_INFLATER_SERVICE);

      // Create the no background button.
      createNoBackgroundButton(backgroundsContent, inflater, backgrounds.getDefault());

      // Create the color button.
      createColorBackgroundButton(backgroundsContent, inflater);

      // Create the custom background button.
      if (bitmapProvider != null) {
        createCustomBackgroundButton(backgroundsContent, inflater);
      }

      // Create backgrounds for each custom background.
      for (final Background background : backgrounds.getCustoms()) {
        if (background instanceof BackgroundFile) {
          createBackgroundButton(backgroundsContent, inflater, (BackgroundFile) background);
        } else {
          log.w("Cannot handle background of type: %s : %s",
              background.getClass().getName(),
              background);
        }
      }
    }

    private void createNoBackgroundButton(ViewGroup backgroundsContent,
                                          LayoutInflater inflater,
                                          Background background) {
      ViewGroup backgroundViewGroup = (ViewGroup) inflater.inflate(R.layout
              .background_preview_button,
          null,
          false);
      Button backgroundButton = ActivityUtil.findViewById(backgroundViewGroup, R.id.button);
      backgroundButton.setText(R.string.none);
      backgroundButton.setTextSize(BACKGROUND_PREVIEW_TEXT_SIZE);
      backgroundButton.setTextColor(getContext().getResources().getColor(android.R.color
          .primary_text_light));
      backgroundButton.setBackground(ViewUtil.getRoundedBitmapDrawable(getContext(),
          R.drawable.background_preview_no_background_background));
      backgroundsContent.addView(backgroundViewGroup);
      // TODO: Remove listeners.
      backgroundButton.setOnClickListener(v -> callback.accept(background));
    }

    private void createColorBackgroundButton(ViewGroup backgroundsContent,
                                             LayoutInflater inflater) {
      ViewGroup backgroundViewGroup = (ViewGroup) inflater.inflate(R.layout
              .background_preview_button,
          null,
          false);
      Button backgroundButton = ActivityUtil.findViewById(backgroundViewGroup, R.id.button);
      backgroundButton.setText(R.string.custom);
      backgroundButton.setTextSize(BACKGROUND_PREVIEW_TEXT_SIZE);
      backgroundButton.setBackground(new BitmapDrawable(getContext().getResources(),
          ViewUtil.getRoundedCornerBitmap(getContext(),
              BitmapFactory.decodeResource(getContext().getResources(), R.drawable.rainbox_dots))));
      backgroundsContent.addView(backgroundViewGroup);
      // TODO: Remove listeners.
      backgroundButton.setOnClickListener(v -> showColorPickerDialog(backgroundButton));
    }

    private void createCustomBackgroundButton(ViewGroup backgroundsContent,
                                              LayoutInflater inflater) {
      ViewGroup backgroundViewGroup = (ViewGroup) inflater.inflate(R.layout
              .background_preview_button,
          null,
          false);
      Button backgroundButton = ActivityUtil.findViewById(backgroundViewGroup, R.id.button);
      backgroundButton.setBackground(ViewUtil.getRoundedBitmapDrawable(getContext(),
          R.drawable.ic_upload));
      backgroundsContent.addView(backgroundViewGroup);
      // TODO: Remove listeners.
      backgroundButton.setOnClickListener(v -> showFilePickerDialog(backgroundButton));
    }

    private void createBackgroundButton(ViewGroup backgroundsContent,
                                        LayoutInflater inflater,
                                        BackgroundFile background) {
      ViewGroup backgroundViewGroup = (ViewGroup) inflater.inflate(R.layout
              .background_preview_button,
          null,
          false);
      View backgroundButton = ActivityUtil.findViewById(backgroundViewGroup, R.id.button);
      background.getPreviewImage(getContext(),
          image -> backgroundButton.setBackground(new BitmapDrawable(getContext().getResources(),
              ViewUtil.getRoundedCornerBitmap(getContext(), image))));
      backgroundsContent.addView(backgroundViewGroup);
      // TODO: Remove listeners.
      backgroundButton.setOnClickListener(v -> callback.accept(background));
    }

    private void showFilePickerDialog(final Button backgroundButton) {
      log.d("showFilePickerDialog");
      bitmapProvider.provide(bitmap -> {
        log.d("bitmap: %s", bitmap);
        // TODO: Maybe change the upload button.
        callback.accept(new BackgroundBitmapImpl(bitmap));
      });
    }

    private void showColorPickerDialog(final Button backgroundButton) {
      log.d("showColorPickerDialog");
      int initialColor = selectedColor != null ? selectedColor : 0xFFFFFFFF;
      ColorPickerDialogBuilder.with(getContext()).setTitle("Choose color").initialColor
          (initialColor).wheelType(
          ColorPickerView.WHEEL_TYPE.FLOWER).density(12).setPositiveButton(R.string.ok,
          (dialog, selectedColor, allColors) -> handleColorSelectionClick(backgroundButton,
              selectedColor))
          // Need this call to show the negative button
          .setNegativeButton(R.string.cancel,
              (dialog, which) -> log.d("Cancel custom color change")).build().show();
    }

    private void handleColorSelectionClick(Button backgroundButton, int color) {
      log.d("handleColorSelectionClick: %d", color);
      this.selectedColor = color;
      setUpColorBackgroundPreviewButton(backgroundButton, color);
      callback.accept(new BackgroundColorImpl(color));
    }

    private void setUpColorBackgroundPreviewButton(Button backgroundButton, int color) {
      GradientDrawable shape = new GradientDrawable();
      shape.setShape(GradientDrawable.RECTANGLE);
      final float bpx = ViewUtil.BACKGROUND_PREVIEW_CORNER_RADIUS_PX;
      shape.setCornerRadii(new float[]{bpx, bpx, bpx, bpx, bpx, bpx, bpx, bpx});
      shape.setColor(color);
      shape.setStroke(3, getContext().getResources().getColor(R.color.background_preview_border));
      backgroundButton.setBackground(shape);
      backgroundButton.setTextColor(ColorUtils.getComplimentGrayColor(color));
    }
  }
}