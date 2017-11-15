package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.ScreenUtil;

import java.text.DecimalFormat;

/**
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class ReadProgressLayout extends LinearLayout {

    private static final String TAG = "ReadProgressLayout";
    private static final int SUB_WHAT = 165;
    private static final int PLUS_WHAT = 273;
    private static final int PADDING_VALUE = 10;
    private Context mContext;
    private ImageView mLeftImageView;
    private ImageView mRightImageView;
    private SeekBar mSeekBar;
    private int mProgress;
    private PositionCallback mCallback;
    private int count;
    private TextView mTitleTextView;
    private TextView mPercentTextView;
    private RelativeLayout mTopLayout;
    private int mLeftPosition;
    private int mRightPosition;
    private int mTopWidth;
    private int mPaddingValue;
    private int mSeekBarWidth;
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private ImageView mTriangleImageView;
    private RelativeLayout mBottomLayout;
    private LinearLayout mTitleLayout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUB_WHAT:
                    if (mProgress > 0) {
                        mProgress -= 1;
                        mHandler.sendEmptyMessage(SUB_WHAT);
                    }
                    break;
                case PLUS_WHAT:
                    if (mProgress < 100) {
                        mProgress += 1;
                        mHandler.sendEmptyMessage(PLUS_WHAT);
                    }
                    break;
                default:
                    break;
            }
            mSeekBar.setProgress(mProgress);
        }
    };
    private View mView;

    public ReadProgressLayout(Context context) {
        this(context, null);
    }

    public ReadProgressLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadProgressLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        mPaddingValue = ScreenUtil.dpToPxInt(PADDING_VALUE);
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.book_reader_read_progress_layout, null);
        mLeftImageView = (ImageView) mView.findViewById(R.id.book_reader_sub_image_view);
        mRightImageView = (ImageView) mView.findViewById(R.id.book_reader_add_image_view);
        mSeekBar = (SeekBar) mView.findViewById(R.id.book_reader_progress_seek_bar);
        mTitleTextView = (TextView) mView.findViewById(R.id.book_reader_title_text_view);
        mPercentTextView = (TextView) mView.findViewById(R.id.book_reader_percent_text_view);
        mTopLayout = (RelativeLayout) mView.findViewById(R.id.book_reader_top_layout);
        mView.setOnClickListener(null);
        mTriangleImageView = (ImageView) mView.findViewById(R.id.book_reader_triangle_image_view);
        mBottomLayout = (RelativeLayout) mView.findViewById(R.id.book_reader_progress_bottom_layout);
        mTitleLayout = (LinearLayout) mView.findViewById(R.id.book_reader_name_layout);
        addView(mView);
        setListener();
    }

    public void changeMode(boolean isNight) {
        mTitleLayout.setBackgroundResource(isNight ? R.drawable.book_reader_title_night_shape
                : R.drawable.book_reader_title_shape);
        mBottomLayout.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background
                : R.color.book_reader_white);
        mTitleTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_main_night_color
                : R.color.book_reader_main_color));
        mPercentTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_main_night_color
                : R.color.book_reader_main_color));
        mSeekBar.setThumb(getResources().getDrawable(isNight ?
                R.drawable.book_reader_read_ball_night_selector
                : R.drawable.book_reader_read_ball_selector));
        // TODO: 2017/7/18 修改SeekBar属性
//        mSeekBar.setProgressDrawable(getResources().getDrawable(isNight
//                ? R.drawable.book_reader_read_progress_night_layer
//                : R.drawable.book_reader_read_progress_layer));
        mTriangleImageView.setImageResource(isNight ? R.drawable.book_reader_triangle_night_image
                : R.drawable.book_reader_triangle_day_image);
        mLeftImageView.setImageResource(isNight ? R.drawable.book_reader_progress_sub_night_selector
                : R.drawable.book_reader_progress_sub_selector);
        mRightImageView.setImageResource(isNight ? R.drawable.book_reader_progress_add_night_selector
                : R.drawable.book_reader_progress_add_selector);
    }

    public void setCount(int count) {
        this.count = count;
    }

    private void setListener() {
        setOnTouchListener(mLeftImageView, SUB_WHAT);
        setOnTouchListener(mRightImageView, PLUS_WHAT);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                int position = (mSeekBarWidth * progress) / 100 + mLeftPosition;
                setLayoutPosition(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                calculatePosition();
            }
        });
    }

    public void setPercent(float percent) {
        mPercentTextView.setText(decimalFormat.format(percent) + "%");
    }

    public void setProgress(float progress) {
        mProgress = (int) progress;
        mSeekBar.setProgress(mProgress);
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    private void setOnTouchListener(View view, final int what) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.sendEmptyMessage(what);
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.removeMessages(what);
                        calculatePosition();
                        break;
                }
                return true;
            }
        });
    }

    private void calculatePosition() {
        int position = (int) ((float) mProgress / 100 * count);
        if (mCallback != null) {
            mCallback.sendPosition(position);
        }
    }

    public void setCallback(PositionCallback mCallback) {
        this.mCallback = mCallback;
    }

    public interface PositionCallback {
        void sendPosition(int position);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLeftPosition = mSeekBar.getLeft();
        mRightPosition = mSeekBar.getRight();
        mTopWidth = mTopLayout.getWidth();
        mSeekBarWidth = mRightPosition - mLeftPosition;
        int position = (mSeekBarWidth * mProgress) / 100 + mLeftPosition;
        setLayoutPosition(position);
    }

    private void setLayoutPosition(int leftPosition) {
        mPaddingValue = 0;
        mTopLayout.layout(leftPosition - mTopWidth / 2 + mPaddingValue, mTopLayout.getTop(),
                leftPosition + mTopWidth / 2 + mPaddingValue, mTopLayout.getBottom());
    }
}
