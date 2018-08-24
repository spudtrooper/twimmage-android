package com.jeffpalm.tweettoimage.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.R;
import com.jeffpalm.tweettoimage.Template;
import com.jeffpalm.tweettoimage.TwitterUtil;
import com.jeffpalm.tweettoimage.util.Log;

public class TwitterPreviewView extends LinearLayout {
  private final Log log = new Log(this);

  private TextView nameText;
  private TextView screenNameText;
  private TextView statusText;
  private TextView createdAtText;
  private ImageView userImageView;

  public TwitterPreviewView(Context context, Template template) {
    super(context);
    createView(getViewIdFromTemplate(template));
  }

  public TwitterPreviewView(Context context, AttributeSet attrs) {
    super(context, attrs);
    createView(R.layout.preview_view_twitter);
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
        R.styleable.TwitterPreviewView,
        0,
        0);
    String name = null;
    String screenName = null;
    String status = null;
    String createdAt = null;
    try {
      name = a.getString(R.styleable.TwitterPreviewView_name);
      screenName = a.getString(R.styleable.TwitterPreviewView_screenName);
      status = a.getString(R.styleable.TwitterPreviewView_status);
      createdAt = a.getString(R.styleable.TwitterPreviewView_createdAt);
    } finally {
      a.recycle();
    }
    if (!Strings.isNullOrEmpty(name)) {
      setNameText(name);
    }
    if (!Strings.isNullOrEmpty(screenName)) {
      setScreenNameText(screenName);
    }
    if (!Strings.isNullOrEmpty(status)) {
      setStatusText(status);
    }
    if (!Strings.isNullOrEmpty(createdAt)) {
      setCreatedAtText(createdAt);
    }
  }

  private void createView(int viewId) {
    View view = LayoutInflater.from(getContext()).inflate(viewId, this);
    nameText = view.findViewById(R.id.nameText);
    screenNameText = view.findViewById(R.id.screenNameText);
    statusText = view.findViewById(R.id.statusText);
    createdAtText = view.findViewById(R.id.createdAtText);
    userImageView = view.findViewById(R.id.imageView);
  }

  private int getViewIdFromTemplate(Template template) {
    switch (template.getKey()) {
      case "twitter":
        return R.layout.preview_view_twitter;
      case "courier":
        return R.layout.preview_view_courier;
      case "courierInverted":
        return R.layout.preview_view_courier_inverted;
      case "plain":
        return R.layout.preview_view_plain;
      case "elegant":
        return R.layout.preview_view_elegant;
      case "elegantBottom":
        return R.layout.preview_view_elegant_bottom;
      case "studioSimpatico":
        return R.layout.preview_view_studio_simpatico;
      default:
        log.e("Using twitter view for template %s", template);
        return R.layout.preview_view_twitter;
    }
  }

  public void setNameText(String name) {
    if (nameText != null) {
      nameText.setText(name);
    }
  }

  public void setScreenNameText(String screenName) {
    if (screenNameText != null) {
      screenNameText.setText(getContext().getString(R.string.screen_name_template, screenName));
    }
  }

  public void setStatusText(String status) {
    if (statusText != null) {
      statusText.setText(status);
    }
  }

  public void setCreatedAtText(String createdAt) {
    if (createdAtText != null) {
      createdAtText.setText(TwitterUtil.formatDate(createdAt));
    }
  }

  public ImageView getUserImageView() {
    return userImageView;
  }

  public void setUserImageView(Bitmap imageView) {
    if (userImageView != null) {
      userImageView.setImageBitmap(imageView);
    }
  }

}
