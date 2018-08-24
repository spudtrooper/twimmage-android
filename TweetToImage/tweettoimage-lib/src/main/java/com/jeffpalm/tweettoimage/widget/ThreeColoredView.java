package com.jeffpalm.tweettoimage.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.jeffpalm.tweettoimage.R;
// https://stackoverflow.com/questions/29890557/two-colors-background-android
// ?utm_medium=organic
// &utm_source=google_rich_qa&utm_campaign=google_rich_qa

/**
 * Created by Bojan on 27.4.2015.
 */
public class ThreeColoredView extends View {

  private final int defaultTopColor = 0xFFFF0000;
  private final int defaultMiddleColor = 0xFF00FF00;
  private final int defaultBottomColor = 0xFF0000FF;
  private int measuredWidth, measuredHeight;
  private Paint topPaint;
  private Paint middlePaint;
  private Paint bottomPaint;
  private int topHeight = 40;
  private int middleHeight = 20;


  public ThreeColoredView(Context context) {
    super(context);
    init(context, null, 0);
  }

  public ThreeColoredView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public ThreeColoredView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attributeSet, int style) {
    TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
        R.styleable.ThreeColoredView,
        style,
        style);
    int topColor = typedArray.getColor(R.styleable.ThreeColoredView_topColor, defaultTopColor);
    int middleColor = typedArray.getColor(R.styleable.ThreeColoredView_middleColor,
        defaultMiddleColor);
    int bottomColor = typedArray.getColor(R.styleable.ThreeColoredView_bottomColor,
        defaultBottomColor);
    topHeight = typedArray.getInteger(R.styleable.ThreeColoredView_topColorHeightPercent, 40);
    middleHeight = typedArray.getInteger(R.styleable.ThreeColoredView_middleColorHeightPercent, 20);
    typedArray.recycle();
    topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    topPaint.setStyle(Paint.Style.FILL);
    topPaint.setColor(topColor);
    middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    middlePaint.setStyle(Paint.Style.FILL);
    middlePaint.setColor(middleColor);
    bottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    bottomPaint.setStyle(Paint.Style.FILL);
    bottomPaint.setColor(bottomColor);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    float topHeightY = measuredHeight * 0.01f * topHeight;
    float middleHeightY = topHeightY + measuredHeight * 0.01f * middleHeight;
    canvas.drawRect(0, 0, measuredWidth, topHeightY, topPaint);
    canvas.drawRect(0, topHeightY, measuredWidth, middleHeightY, middlePaint);
    canvas.drawRect(0, middleHeightY, measuredWidth, measuredHeight, bottomPaint);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    measuredHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    measuredWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    setMeasuredDimension(measuredWidth, measuredHeight);
  }
}