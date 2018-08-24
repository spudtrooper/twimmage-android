package com.jeffpalm.twimmage;

import com.google.common.base.MoreObjects;

final class TwitterNotification {
  final static String COLUMN_ID = "_id";
  final static String COLUMN_STATUS_ID = "statusId";
  final static String COLUMN_TIMESTAMP = "timestamp";
  final static String TABLE_NAME = "TwitterNotifications";
  final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " integer "
      + "PRIMARY KEY," + COLUMN_STATUS_ID + " " + "integer," + COLUMN_TIMESTAMP + " DATETIME " +
      "DEFAULT " + "CURRENT_TIMESTAMP" + ");";
  static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME + " ORDER " + "BY" + " " +
      COLUMN_ID + " " + "DESC";

  private final long id;
  private final long statusId;
  private final String timestamp;

  TwitterNotification(int id, long statusId, String timestamp) {
    this.id = id;
    this.statusId = statusId;
    this.timestamp = timestamp;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public long getStatusId() {
    return statusId;
  }

  public long getId() {
    return id;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("statusId", statusId).add("timestamp",
        timestamp).toString();
  }
}
