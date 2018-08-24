package com.jeffpalm.tweettoimage.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeffpalm.tweettoimage.ActivityUtil;
import com.jeffpalm.tweettoimage.R;

/**
 * Wraps a ViewPager and progress bar.
 */
public final class ViewPagerWithProgress extends LinearLayout {

  private ViewPager viewPager;
  private ProgressBar progressBar;
  private TextView progressText;
  private ViewGroup progressContainer;

  public ViewPagerWithProgress(Context context) {
    super(context);
    init();
  }

  public ViewPagerWithProgress(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ViewPagerWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.view_pager_with_progress, this, true);
    setOrientation(LinearLayout.VERTICAL);
    setWeightSum(1);

    viewPager = ActivityUtil.findViewById(this, R.id.view_pager);
    progressBar = ActivityUtil.findViewById(this, R.id.view_pager_progress);
    progressText = ActivityUtil.findViewById(this, R.id.view_pager_progress_text);
    progressContainer = ActivityUtil.findViewById(this, R.id.view_pager_container);

    progressBar.setVisibility(View.VISIBLE);
    progressContainer.setVisibility(View.VISIBLE);
    progressText.setText("");
    progressText.setVisibility(View.VISIBLE);
    viewPager.setVisibility(View.INVISIBLE);
  }

  public ViewPager getViewPager() {
    return viewPager;
  }

  public void showViewPager() {
    progressContainer.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    progressText.setVisibility(View.GONE);
    viewPager.setVisibility(View.VISIBLE);
  }

  public void setStatus(CharSequence status) {
    progressText.setText(status);
  }
}
