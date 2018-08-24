package com.jeffpalm.tweettoimage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.jeffpalm.tweettoimage.R;

public final class ViewUtil {

  public final static int BACKGROUND_PREVIEW_CORNER_RADIUS_PX = 12;

  private ViewUtil() {
  }

  public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, int resId) {
    return getRoundedBitmapDrawable(context,
        BitmapFactory.decodeResource(context.getResources(), resId));
  }

  public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, Bitmap image) {
    return RoundedBitmapDrawableFactory.create(context.getResources(), image);
  }

  /**
   * From: https://ruibm.com/2009/06/16/rounded-corner-bitmaps-on-android/
   * License: Apache License – Version 2 (http://www.apache
   * .org/licenses/LICENSE-2.0.html)
   */
  public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
        bitmap.getHeight(),
        Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    Paint paint = new Paint();
    Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    RectF rectF = new RectF(rect);

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(context.getResources().getColor(R.color.background_preview_border));
    paint.setStrokeWidth(12);
    canvas.drawRoundRect(rectF,
        BACKGROUND_PREVIEW_CORNER_RADIUS_PX,
        BACKGROUND_PREVIEW_CORNER_RADIUS_PX,
        paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }
}
