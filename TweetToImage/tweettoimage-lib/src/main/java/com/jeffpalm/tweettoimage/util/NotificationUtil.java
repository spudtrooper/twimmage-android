package com.jeffpalm.tweettoimage.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.jeffpalm.tweettoimage.R;
import com.jeffpalm.tweettoimage.api.Status;

import javax.inject.Inject;

public final class NotificationUtil {

  private static final String NOTIFICATION_CHANNEL_ID = "123123";

  @Inject
  NotificationUtil() {
  }

  public void createNotification(Context context,
                                 int notificationId,
                                 Status status,
                                 String contentText,
                                 Intent intent) {
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
        NOTIFICATION_CHANNEL_ID).setSmallIcon(R.drawable.twimmage).setContentTitle(context
        .getString(
        R.string.screen_name_template,
        status.getUser().getScreenName())).setContentText(contentText).setPriority(
        NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = context.getString(R.string.channel_name);
      String description = context.getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
          name,
          importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.notify(notificationId, builder.build());
  }
}
