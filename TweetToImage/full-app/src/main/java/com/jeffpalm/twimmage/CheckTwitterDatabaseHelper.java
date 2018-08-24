package com.jeffpalm.twimmage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

import com.jeffpalm.tweettoimage.di.ApplicationContext;
import com.jeffpalm.tweettoimage.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

final class CheckTwitterDatabaseHelper extends SQLiteOpenHelper {
  @VisibleForTesting static final String DATABASE_NAME = "twitter_notifications";
  private static final int DATABASE_VERSION = 5;
  private final Log log = new Log(this);

  @Inject
  public CheckTwitterDatabaseHelper(@ApplicationContext Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void insert(long statusId) {
    log.d("Inserting statusId=%d", statusId);
    SQLiteDatabase db = getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(TwitterNotification.COLUMN_STATUS_ID, statusId);
    long id = db.insert(TwitterNotification.TABLE_NAME, null, values);
    log.d("id=%s after insert of statusId=%d", id, statusId);
    db.close();
  }

  public boolean exists(long statusId) {
    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TwitterNotification.TABLE_NAME + " WHERE " +
            TwitterNotification.COLUMN_STATUS_ID + " = " + statusId + " LIMIT 1",
        null);
    try {
      return cursor.getCount() == 1;
    } finally {
      cursor.close();
    }
  }

  public List<TwitterNotification> getTwitterNotifications() {
    SQLiteDatabase database = getWritableDatabase();
    Cursor cursor = database.rawQuery(TwitterNotification.SELECT_ALL, null);
    return buildTwitterNotification(cursor);
  }

  private List<TwitterNotification> buildTwitterNotification(Cursor cursor) {
    List<TwitterNotification> twitterNotifications = new ArrayList<>();
    if (isCursorPopulated(cursor)) {
      do {
        String id = cursor.getString(cursor.getColumnIndex(TwitterNotification.COLUMN_ID));
        String statusId = cursor.getString(cursor.getColumnIndex(TwitterNotification
            .COLUMN_STATUS_ID));
        String timestamp = cursor.getString(cursor.getColumnIndex(TwitterNotification
            .COLUMN_TIMESTAMP));
        TwitterNotification tn = new TwitterNotification(Integer.parseInt(id),
            Long.parseLong(statusId),
            timestamp);
        twitterNotifications.add(tn);
      } while (cursor.moveToNext());
    }
    return twitterNotifications;
  }

  private boolean isCursorPopulated(Cursor cursor) {
    return cursor != null && cursor.moveToFirst();
  }

  public void clear() {
    log.d("Clearing table %s", TwitterNotification.TABLE_NAME);
    SQLiteDatabase db = getWritableDatabase();
    db.delete(TwitterNotification.TABLE_NAME, "1", new String[0]);
    db.close();
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(TwitterNotification.CREATE_TABLE);
  }


  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TwitterNotification.TABLE_NAME);
    onCreate(db);
  }


}
