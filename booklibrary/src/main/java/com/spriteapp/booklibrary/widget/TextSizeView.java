package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.TextSizeCallback;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;

/**
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class TextSizeView extends View {

    private static final String TAG = "TextSizeView";
    public static final int DEFAULT_TEXT_SIZE = 17;
    private static final float DEFAULT_WIDTH = 260;
    private static final float DEFAULT_HEIGHT = 47;
    private static final int DEFAULT_BALL_RADIUS = 8;
    private static final int START_VALUE = 0;
    private static final int MAX_COUNT = 7;
    private Paint mBallPaint;
    private Paint mLeftPaint;
    private Paint mRightPaint;
    private Paint mBallLinePaint;
    private int mBallColor;
    private float mBallRadius;
    private int mStartValue;
    private TouchEnum state;
    private float downX;
    private int mWidth;
    private int endValue;
    private RectF mRectF;
    private int mLeftColor;
    private int mRightColor;
    private int mDivideWidth;
    private int mTextSize = DEFAULT_TEXT_SIZE;
    private int position;
    private TextSizeCallback mCallBack;
    private static final int SUB_VALUE = 2;
    private final int mSubValue;

    public TextSizeView(Context context) {
        this(context, null);
    }

    public TextSizeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSubValue = ScreenUtil.dpToPxInt(SUB_VALUE);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextSizeView, defStyleAttr, 0);
        mBallColor = typedArray.getColor(R.styleable.TextSizeView_ball_color, Color.GREEN);
        mLeftColor = typedArray.getColor(R.styleable.TextSizeView_left_color, Color.BLUE);
        mRightColor = typedArray.getColor(R.styleable.TextSizeView_right_color, Color.LTGRAY);
        mBallRadius = typedArray.getDimension(R.styleable.TextSizeView_ball_radius, dp2px(DEFAULT_BALL_RADIUS));
        mStartValue = (int) typedArray.getDimension(R.styleable.TextSizeView_start_value, dp2px(START_VALUE));
        typedArray.recycle();
        initPaint();
    }

    private void initPaint() {
        mBallPaint = new Paint();
        mLeftPaint = new Paint();
        mRightPaint = new Paint();
        initPaint(mBallPaint, mBallColor);
        initBallLinePaint();
        initPaint(mLeftPaint, mLeftColor);
        initPaint(mRightPaint, mRightColor);
        mLeftPaint.setStrokeWidth(dp2px(1));
        mRightPaint.setStrokeWidth(dp2px(1));
    }

    private void initBallLinePaint() {
        mBallLinePaint = new Paint();
        mBallLinePaint.setAntiAlias(true);
        mBallLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBallLinePaint.setStrokeCap(Paint.Cap.BUTT);
        mBallLinePaint.setColor(mLeftColor);
    }

    private void initPaint(Paint paint, int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.BUTT);
    }

    public void changeMode(boolean isNight) {
        mBallLinePaint.setColor(getResources().getColor(isNight ? R.color.book_reader_main_night_color
                : R.color.book_reader_main_color));
        mRightPaint.setColor(getResources().getColor(isNight ? R.color.book_reader_common_text_color
                : R.color.book_reader_divide_line_color));
        mBallPaint.setColor(getResources().getColor(isNight ? R.color.book_reader_read_bottom_night_background
                : R.color.book_reader_white));
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                position = 0;
                downX = event.getX();
                /**
                 * 点击小球
                 */
                if (downX > mStartValue && downX < mStartValue + 2 * mBallRadius) {
                    state = TouchEnum.TOUCH_BALL;
                } else {
                    /**
                     * 点击位置位于最左边小于球的直径处
                     */
                    if (downX < 2 * mBallRadius) {
                        downX = 0;
                    } else if (downX > endValue) {
                        /**
                         * 点击位置位于最右边
                         */
                        downX = endValue;
                    }
                    /**
                     * 使得mStartValue为我们按下的值刷新view
                     * 并把state的状态置为TouchEnum.TOUCH_BALL
                     */
                    mStartValue = (int) downX;
                    state = TouchEnum.TOUCH_BALL;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == TouchEnum.TOUCH_BALL) {
                    float moveX = event.getX();
                    float moveDistance = moveX - downX;
                    mStartValue += moveDistance;
                    if (mStartValue < 0) {
                        mStartValue = 0;
                    } else if (mStartValue > endValue) {
                        mStartValue = endValue;
                    }
                    invalidate();
                    /**
                     * 每刷新一次view之后把moveX的值赋给downX，这样在下次又重新获取moveX，两者相减就是我们滑动的距离。
                     */
                    downX = moveX;
                }
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                getUpPosition((int) upX);
                break;
        }
        return true;
    }

    public void setCallBack(TextSizeCallback mCallBack) {
        this.mCallBack = mCallBack;
    }

    private void getUpPosition(int upX) {
        position = upX / mDivideWidth;
        int moveValue = Math.abs(upX - mDivideWidth * position);
        position = moveValue > mDivideWidth / 2 ? ++position : position;
        if (position == MAX_COUNT) {
            position--;
        }
        mStartValue = 0;
        invalidate();
        setTextSize();
        if (mCallBack != null) {
            mCallBack.sendTextSize(mTextSize);
        }
    }

    public int plusTextSize() {
        if (position == MAX_COUNT - 1) {
            return mTextSize;
        }
        position++;
        setTextSize();
        invalidate();
        return mTextSize;
    }

    public int subTextSize() {
        if (position == 0) {
            return mTextSize;
        }
        position--;
        setTextSize();
        invalidate();
        return mTextSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        endValue = (int) (mWidth - 2 * mBallRadius);
        mRectF = new RectF(0, 0, mWidth, h);
        mDivideWidth = (int) ((mWidth - 2 * mBallRadius - 2 * mSubValue) / 6);
    }

    public void setPosition(int position) {
        this.position = position;
        mTextSize = DEFAULT_TEXT_SIZE;
        mTextSize = (int) (mTextSize + 1.5f * position);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int verticalCenter = getHeight() / 2;
        canvas.drawLine(mBallRadius + mSubValue, verticalCenter, mWidth - mBallRadius - mSubValue, verticalCenter, mRightPaint);
        for (int i = 0; i < MAX_COUNT; i++) {
            canvas.drawLine(mDivideWidth * i + mBallRadius + mSubValue, verticalCenter - ScreenUtil.dpToPxInt(5),
                    mDivideWidth * i + mBallRadius + mSubValue,
                    verticalCenter + ScreenUtil.dpToPxInt(5), mRightPaint);
        }
        float leftPosition = position * mDivideWidth + mStartValue + mBallRadius;
        canvas.drawCircle(leftPosition + mSubValue, verticalCenter, mBallRadius + dp2px(1), mBallLinePaint);
        canvas.drawCircle(leftPosition + mSubValue, verticalCenter, mBallRadius, mBallPaint);
    }

    public void setTextSize() {
        mTextSize = DEFAULT_TEXT_SIZE;
        mTextSize = (int) (mTextSize + 1.5f * position);
        SharedPreferencesUtil.getInstance().putInt(Constant.READ_TEXT_SIZE_POSITION, position);
    }

    public int getTextSize() {
        return mTextSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_WIDTH, getResources().getDisplayMetrics());
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT, getResources().getDisplayMetrics());
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    public enum TouchEnum {
        TOUCH_BALL, TOUCH_LEFT, TOUCH_RIGHT
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

}
