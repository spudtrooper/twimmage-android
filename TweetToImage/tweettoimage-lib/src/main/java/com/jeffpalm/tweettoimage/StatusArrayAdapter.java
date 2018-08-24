package com.jeffpalm.tweettoimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.di.ActivityContext;
import com.jeffpalm.tweettoimage.util.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * This adapter supports all a user's tweets, but also allows adding high
 * priority ones -- e.g. shared tweets. These will always show up first in
 * the list.
 */
final class StatusArrayAdapter extends BaseAdapter {
  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM" + " d, y");

  private final Context context;
  private final List<Status> highPriorityStatuses = new ArrayList<>();
  private final List<Status> statuses = new ArrayList<>();
  private Comparator<Status> comparator;

  @Inject
  StatusArrayAdapter(@ActivityContext Context context) {
    this.context = context;
  }

  void setComparator(Comparator<Status> comparator) {
    this.comparator = Assert.notNull(comparator);
    Collections.sort(statuses, comparator);
    notifyDataSetChanged();
  }

  void addHighPriorityStatus(Status status) {
    highPriorityStatuses.add(status);
    notifyDataSetChanged();
  }

  void addStatuses(List<Status> statuses) {
    this.statuses.addAll(statuses);
    if (comparator != null) {
      Collections.sort(this.statuses, comparator);
    }
    notifyDataSetChanged();
  }

  void reset() {
    statuses.clear();
    highPriorityStatuses.clear();
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return highPriorityStatuses.size() + statuses.size();
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
    View rowView = inflater.inflate(R.layout.tweet_row, parent, false);
    Status s = get(i);
    TextView agoText = ActivityUtil.findViewById(rowView, R.id.ago_text);
    TextView dateText = ActivityUtil.findViewById(rowView, R.id.date_text);
    TextView tweetText = ActivityUtil.findViewById(rowView, R.id.tweet_text);
    TextView retweetsText = ActivityUtil.findViewById(rowView, R.id.retweets_text);
    TextView favoritesText = ActivityUtil.findViewById(rowView, R.id.favorites_text);
    agoText.setText(TwitterUtil.getTimeString(context, s.getCreatedAt()));
    dateText.setText(DATE_FORMAT.format(s.getCreatedAt()));
    tweetText.setText(s.getText());
    retweetsText.setText(String.valueOf(s.getRetweetCount()));
    favoritesText.setText(String.valueOf(s.getFavoriteCount()));
    return rowView;
  }

  Status get(int index) {
    return index < highPriorityStatuses.size() ? highPriorityStatuses.get(index) : statuses.get(
        highPriorityStatuses.size() + index);
  }
}
