package net.huaxi.reader.book;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.Utility;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.huaxi.reader.R;

/**
 * 阅读页底部View
 *
 * @remark 电量、时间、进度比例
 */
public class BookContentBottomView extends View {

    private Activity mActivity;
    /**
     * 画笔
     */
    private Paint mPaint;
    // 屏幕宽
    public int screenWidth;
    // 屏幕高
    public int screenHeight;

    private int viewH;

    /**
     * 边宽度(单位dip)
     */
    private int sideWidth;
    /**
     * 左边缘的距离
     */
    private int paddingLeft;
    /**
     * 右边缘的距离
     */
    private int paddingRight;
    /**
     * 时间
     */
    private String time = "";
    /**
     * loading title
     */
    private String title = "";
    /**
     * loading tip
     */
    private String tip = "";
    /**
     * loading bookname
     */
    private String bookname = "";
    /**
     * 电量百分比
     */
    private float batteryPercent = 0.0f;
    /**
     * 阅读进度比例
     */
    private float readPercent = 0f;
    /**
     * 时间、百分比字体大小
     */
    private int textSize;

    private int batteryWidth; // 绘制宽度
    private int batteryHeight;// 电池高度
    private int paddingBottom; // 距离底部距离
    private int timeTextViewMarginLeft;// 时间与电池的间隔
    private int batteryHeadWidth;// 电池头部的宽度
    private int batteryBorderWidth;// 电池边框的宽度
    private int timeTextViewX;
    private int batteryViewX;
    /**
     * 是否注册过监听时间的Recevier
     */
    private boolean mHasRegisterTimeReceiver;
    /**
     * 是否注册过监听电量的Recevier
     */
    private boolean mHasRegisterBatteryReceiver;

    // 减少对象的创建
    private SimpleDateFormat mDateFormatter = new SimpleDateFormat("HH:mm");

    private DecimalFormat PERCENT_FORMAT1 = new DecimalFormat("#0.00");

    private Date mCurDate = new Date();

    private BroadcastReceiver mBatteryInfoReceiver;

    private BookContentSettings mSettings;

    public BookContentBottomView(Context context) {
        this(context, null);
    }

    public BookContentBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BookContentBottomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mActivity = (Activity) context;
        init();
    }

    private void init() {
        mSettings = BookContentSettings.getInstance();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);

        screenWidth = Utility.getScreenWidth();
        screenHeight = Utility.getScreenHeight();
        viewH = screenHeight - mStatusBarHeight;
        // viewH = screenHeight-
        // 计算书边的宽度
//        if (Constant.ISSHOWBOOKSIDE) {
//        sideWidth = getResources().getDimensionPixelSize(R.dimen.page_right_side);
//        } else {
        sideWidth = 0;
//        }
        paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_padding_left);
        paddingRight = getResources().getDimensionPixelSize(R.dimen.page_padding_right);
        textSize = getResources().getDimensionPixelSize(R.dimen.page_text_size);
        batteryHeight = getResources().getDimensionPixelSize(R.dimen.page_battery_border_height);
        batteryWidth = getResources().getDimensionPixelSize(R.dimen.page_battery_border_width);
        paddingBottom = getResources().getDimensionPixelSize(R.dimen.page_padding_bottom);
        timeTextViewMarginLeft = getResources().getDimensionPixelSize(R.dimen.page_time_text_margin_left);
        batteryHeadWidth = getResources().getDimensionPixelSize(R.dimen.page_battery_head_width);
        batteryBorderWidth = getResources().getDimensionPixelSize(R.dimen.page_battery_border_stroke_width);
        timeTextViewX = paddingLeft + batteryWidth + batteryHeadWidth + timeTextViewMarginLeft;
    }


    float density;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screenWidth = mSettings.getScreenWidth();
        screenHeight = mSettings.getScreenHeight();
        viewH = screenHeight - mStatusBarHeight;
        mPaint.setTextSize(textSize);
        mPaint.setColor(Constants.READ_OTHER_COLORS[BookContentSettings.getInstance().getTheme()]);
        mPaint.setTypeface(Typeface.DEFAULT);
        int textWidth = (int) mPaint.measureText(this.time);
        batteryViewX = paddingLeft + textWidth + timeTextViewMarginLeft;
        drawPercent(canvas);// 绘制百分比
        drawTime(canvas);// 当前时间.
        drawBattery(canvas);// 绘制电量

//        drawLoading(canvas);// 绘制loading
    }

    /**
     * 设置时间
     *
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 绘制当前时间
     *
     * @param canvas
     */
    public void drawTime(Canvas canvas) {
//        canvas.drawText(time, timeTextViewX, viewH - paddingBottom, mPaint);
        canvas.drawText(time, paddingLeft, viewH - paddingBottom, mPaint);
//        canvas.drawLine(0, viewH - paddingBottom, screenWidth, viewH - paddingBottom, mPaint);
    }


    /**
     * 绘制电量
     *
     * @param canvas
     */
    public void drawBattery(Canvas canvas) {

        // mPaint.setColor(Constant.READ_OTHER_COLORS[PayTheme.getCurrentTheme(mActivity)]);
        // mPaint.setColor(0xff8a8a8a);
        // 绘制电池边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(batteryBorderWidth);
        canvas.drawRect(batteryViewX, viewH - paddingBottom - batteryHeight, batteryViewX + batteryWidth, viewH - paddingBottom, mPaint);

        float percentWidth = (batteryWidth - batteryBorderWidth * 2) * (batteryPercent / 100);
        mPaint.setStyle(Paint.Style.FILL); // 设置填满画笔
        // 绘制电池剩余电量
        canvas.drawRect(batteryViewX + batteryBorderWidth, viewH - paddingBottom - batteryHeight + batteryBorderWidth, batteryViewX +
                batteryBorderWidth + percentWidth, viewH - paddingBottom - batteryBorderWidth, mPaint);
        // 绘制电池头
        canvas.drawRect(batteryViewX + batteryWidth + batteryBorderWidth, viewH - paddingBottom - (batteryHeight + batteryHeadWidth) / 2,
                batteryViewX +
                        batteryWidth + batteryHeadWidth, viewH - paddingBottom - (batteryHeight - batteryHeadWidth) / 2, mPaint);
    }

    /**
     * 设置阅读进度比例
     *
     * @param readPer
     */
    public void setReadPercent(float readPer) {
        readPercent = readPer * 100;
    }

    /**
     * 绘制百分比
     *
     * @param canvas
     */
    public void drawPercent(Canvas canvas) {
        if (readPercent >= 0) {
            canvas.restore();
            String percentStr = PERCENT_FORMAT1.format(readPercent);
            percentStr = (percentStr.equals("100.00") ? "100" : percentStr) + "%";
            int mPercentWidth = (int) mPaint.measureText(percentStr) + sideWidth;
            canvas.drawText(percentStr, screenWidth - mPercentWidth - paddingRight, viewH - paddingBottom, mPaint);
        }
    }

    /**
     * 设置电量百分比
     *
     * @param batteryPercent
     */
    public void setBatteryPercent(float batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    // 展示时间
    private void showTime() {
        mCurDate.setTime(System.currentTimeMillis());
        String currentTime = mDateFormatter.format(mCurDate);
        setTime(currentTime);
        postInvalidate();
    }

    // 时间修改
    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showTime();
        }
    };

    public void startUpdateTime() {
        showTime();
        // 注册时间的监听
        if (!mHasRegisterTimeReceiver) {
            IntentFilter timeFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
            getContext().registerReceiver(mTimeReceiver, timeFilter);
            mHasRegisterTimeReceiver = true;
        }
    }

    public void startUpdateBattery() {
        if (!mHasRegisterBatteryReceiver) {
            mBatteryInfoReceiver = new mBatteryInfoReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            getContext().registerReceiver(mBatteryInfoReceiver, filter);
            mHasRegisterBatteryReceiver = true;
        }
    }

    /**
     * 电量接收广播
     */
    private class mBatteryInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) { // 重写onReceiver方法
            int current = intent.getExtras().getInt("level", 0); // 获得当前电量
            int total = intent.getExtras().getInt("scale", 100); // 获得总电量
            float batteryPercent = current * 100 / total;
            setBatteryPercent(batteryPercent);
            postInvalidate();
        }
    }

    public void stop() {
        if (mHasRegisterBatteryReceiver) getContext().unregisterReceiver(mBatteryInfoReceiver);
        if (mHasRegisterTimeReceiver) getContext().unregisterReceiver(mTimeReceiver);
        mHasRegisterTimeReceiver = false;
        mHasRegisterBatteryReceiver = false;
    }

    private int mStatusBarHeight;

    /**
     * 设置通知栏高度
     *
     * @param statusBarHeight
     */
    public void setStatusBarHeight(int statusBarHeight) {
        mStatusBarHeight = statusBarHeight;
    }

    public void resetReadPercent(float readPer) {
        setReadPercent(readPer);
        invalidate();
    }

    public void refresh() {
        invalidate();
    }
}
