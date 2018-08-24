package com.jeffpalm.tweettoimage.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.MoreObjects;

import java.util.Date;

public final class StatusImpl implements Status, Status.Mutable {

  public final static Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {

    @Override
    public Status createFromParcel(Parcel in) {
      return new StatusImpl(in);
    }

    @Override
    public Status[] newArray(int size) {
      return new Status[size];
    }
  };

  private long id;
  private String text;
  private Date createdAt;
  private int favoriteCount;
  private int retweetCount;
  private TwitterUser user;

  private StatusImpl(Parcel in) {
    readFromParcel(in);
  }

  public StatusImpl(long id,
                    String text,
                    Date createdAt,
                    int favoriteCount,
                    int retweetCount,
                    TwitterUser user) {
    this.id = id;
    this.text = text;
    this.createdAt = createdAt;
    this.favoriteCount = favoriteCount;
    this.retweetCount = retweetCount;
    this.user = user;
  }

  private void readFromParcel(Parcel in) {
    // TODO
    // user = Assert.notNull(in.readParcelable(getClass().getClassLoader()));
    user = new TwitterUserImpl(in.readString(), in.readString(), in.readString(), in.readString());
    createdAt = (Date) in.readSerializable();
    id = in.readLong();
    text = in.readString();
    favoriteCount = in.readInt();
    retweetCount = in.readInt();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }

  @Override
  public Mutable setText(String text) {
    this.text = text;
    return this;
  }

  @Override
  public Mutable setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  @Override
  public Mutable setFavoriteCount(int favoriteCount) {
    this.favoriteCount = favoriteCount;
    return this;
  }

  @Override
  public Mutable setRetweetCount(int retweetCount) {
    this.retweetCount = retweetCount;
    return this;

  }

  @Override
  public Mutable setUser(TwitterUser user) {
    this.user = user;
    return this;
  }

  @Override
  public Status freeze() {
    return this;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    // TODO: Fix this
    // user.writeToParcel(out, flags);
    out.writeString(user.getName());
    out.writeString(user.getScreenName());
    out.writeString(user.getOriginalProfileImageURL());
    out.writeString(user.getDescription());
    out.writeSerializable(getCreatedAt());
    out.writeLong(getId());
    out.writeString(getText());
    out.writeInt(getFavoriteCount());
    out.writeInt(getRetweetCount());
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public Date getCreatedAt() {
    return createdAt;
  }

  @Override
  public int getFavoriteCount() {
    return favoriteCount;
  }

  @Override
  public int getRetweetCount() {
    return retweetCount;
  }

  @Override
  public TwitterUser getUser() {
    return user;
  }

  @Override
  public Mutable toMutable() {
    return this;
  }
}
