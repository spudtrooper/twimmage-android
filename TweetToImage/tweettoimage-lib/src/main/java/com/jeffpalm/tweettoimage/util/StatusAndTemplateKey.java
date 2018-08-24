package com.jeffpalm.tweettoimage.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.jeffpalm.tweettoimage.Template;
import com.jeffpalm.tweettoimage.api.Status;

public final class StatusAndTemplateKey implements Parcelable {
  public final static Creator<StatusAndTemplateKey> CREATOR = new Creator<StatusAndTemplateKey>() {

    @Override
    public StatusAndTemplateKey createFromParcel(Parcel in) {
      return new StatusAndTemplateKey(in);
    }

    @Override
    public StatusAndTemplateKey[] newArray(int size) {
      return new StatusAndTemplateKey[size];
    }
  };
  private Status status;
  private String templateKey;

  private StatusAndTemplateKey(Parcel in) {
    readFromParcel(in);
  }

  private StatusAndTemplateKey(Status status, String templateKey) {
    this.status = status;
    this.templateKey = templateKey;
  }

  @VisibleForTesting
  public static StatusAndTemplateKey newInstanceForTesting(Status status, String templateKey) {
    return new StatusAndTemplateKey(status, templateKey);
  }

  public static StatusAndTemplateKey newInstance(Status status, Template template) {
    return new StatusAndTemplateKey(status, template.getKey());
  }

  private void readFromParcel(Parcel in) {
    status = in.readParcelable(getClass().getClassLoader());
    templateKey = in.readString();
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
  // Parcelable interface

  public String getTemplateKey() {
    return templateKey;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeParcelable(status, flags);
    out.writeString(templateKey);
  }
}




