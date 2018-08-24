package com.jeffpalm.tweettoimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeffpalm.tweettoimage.api.TwitterUser;
import com.jeffpalm.tweettoimage.di.ActivityContext;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.ImageDownloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

final class TwitterUserArrayAdapter extends BaseAdapter {
  private final Context context;
  private final ImageDownloader imageDownloader;
  private final List<TwitterUser> users = new ArrayList<>();

  private Comparator<TwitterUser> comparator;

  @Inject
  TwitterUserArrayAdapter(@ActivityContext Context context, ImageDownloader imageDownloader) {
    this.context = context;
    this.imageDownloader = imageDownloader;
  }

  void setComparator(Comparator<TwitterUser> comparator) {
    this.comparator = Assert.notNull(comparator);
    Collections.sort(users, comparator);
    notifyDataSetChanged();
  }

  void addStatuses(List<TwitterUser> users) {
    List<TwitterUser> toAdd = new ArrayList<>();
    for (TwitterUser u : users) {
      if (!containsScreenName(u.getScreenName())) {
        toAdd.add(u);
      }
    }
    this.users.addAll(toAdd);
    if (comparator != null) {
      Collections.sort(this.users, comparator);
    }
    notifyDataSetChanged();
  }

  private boolean containsScreenName(String screenName) {
    for (TwitterUser u : users) {
      if (screenName.equals(u.getScreenName())) {
        return true;
      }
    }
    return false;
  }

  void reset() {
    users.clear();
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return users.size();
  }

  @Override
  public Object getItem(int i) {
    return get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
        .LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.twitter_user_row, parent, false);
    TwitterUser user = get(i);
    TextView userNameText = ActivityUtil.findViewById(rowView, R.id.userNameText);
    TextView screenNameText = ActivityUtil.findViewById(rowView, R.id.screenNameText);
    TextView userDescriptionText = ActivityUtil.findViewById(rowView, R.id.userDescriptionText);
    ImageView userImageView = ActivityUtil.findViewById(rowView, R.id.userImageView);
    // Hide the image view while we download the user image.
    userImageView.setVisibility(View.INVISIBLE);
    userNameText.setText(user.getName());
    screenNameText.setText(context.getString(R.string.screen_name_template, user.getScreenName()));
    userDescriptionText.setText(user.getDescription());
    imageDownloader.download(user.getOriginalProfileImageURL(), userImageView);
    return rowView;
  }

  TwitterUser get(int index) {
    return users.get(index);
  }
}
