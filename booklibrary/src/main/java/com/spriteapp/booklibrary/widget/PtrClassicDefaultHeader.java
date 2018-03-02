package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by hzh on 2016/5/5.
 */
public class PtrClassicDefaultHeader extends FrameLayout implements PtrUIHandler
{
    private static final String TAG = "cn.hebut.PtrClassicDefaultHeader";

    private int mRotateAniTime = 150;

    private TextView mTitleView;
    private TextView mLastUpdateTimeView;
    private View mProgressBar;
    private View mRotateView;

    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String KEY_SharedPreferences = "hebut_ptr_classic_last_update";

    private String mLastUpdateTimeKey;
    private long mLastUpdateTime = -1;
    private boolean mShoulShowLastUpdate = false;

    private LastUpdateTimeUpdater mLastUpdateTimeUpdater = new LastUpdateTimeUpdater();

    public PtrClassicDefaultHeader(Context context)
    {
        super(context);
        initViews(null);
    }

    public PtrClassicDefaultHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initViews(attrs);
    }

    public PtrClassicDefaultHeader(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs)
    {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrClassicDefaultHeader, 0, 0);
        if(arr != null) {
            mRotateAniTime = arr.getInt(R.styleable.PtrClassicDefaultHeader_ptr_rotate_ani_time, mRotateAniTime);
            arr.recycle();
        }

        View header = LayoutInflater.from(getContext()).inflate(R.layout.hebut_ptr_classic_default_header, this);
        mTitleView = (TextView) header.findViewById(R.id.ptr_classic_header_text_title);
        mLastUpdateTimeView = (TextView) header.findViewById(R.id.ptr_classic_header_text_last_update);
        mRotateView = header.findViewById(R.id.ptr_classic_header_rotate_view);
        mProgressBar = header.findViewById(R.id.ptr_classic_header_progress_bar);

        //初始化rotate anim
        buildAnimation();

        resetView();
    }

    private void buildAnimation()
    {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,  RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setFillAfter(true);
    }

    public void setLastUpdateTimeKey(String key)
    {
        mLastUpdateTimeKey = key;
    }

    private void resetView()
    {
        mProgressBar.setVisibility(INVISIBLE);
        hideRotateView();
    }

    private void hideRotateView()
    {
        mRotateView.clearAnimation();
        mRotateView.setVisibility(INVISIBLE);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame)
    {
        resetView();

        mShoulShowLastUpdate = false;
        tryUpdateLastUpdateTime();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame)
    {
        mShoulShowLastUpdate = true;
        tryUpdateLastUpdateTime();
        mLastUpdateTimeUpdater.start();

        mProgressBar.setVisibility(INVISIBLE);
        mRotateView.setVisibility(VISIBLE);
        mTitleView.setText(R.string.hebut_ptr_pull_down);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame)
    {
        hideRotateView();
        mProgressBar.setVisibility(VISIBLE);
        mTitleView.setVisibility(VISIBLE);
        mTitleView.setText(R.string.hebut_ptr_updating);

        mShoulShowLastUpdate = false;
        tryUpdateLastUpdateTime();
        mLastUpdateTimeUpdater.stop();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame)
    {
        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);
        mTitleView.setVisibility(VISIBLE);
        mTitleView.setText(R.string.hebut_ptr_update_complete);

        //update last update time
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(KEY_SharedPreferences, 0);
        if(!TextUtils.isEmpty(mLastUpdateTimeKey)) {
            mLastUpdateTime = new Date().getTime();
            sharedPreferences.edit().putLong(mLastUpdateTimeKey, mLastUpdateTime).commit();
        }
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator)
    {
        final int offsetToRefresh = ptrIndicator.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPos();

        if(currentPos < offsetToRefresh && lastPos >= offsetToRefresh) {
            if(isUnderTouch && status == frame.PTR_STATUS_PREPARE) {
                crossRotateLineFromBottomUnderTouch();
            }
        } else if(currentPos > offsetToRefresh && lastPos <= offsetToRefresh) {
            if(isUnderTouch && status == frame.PTR_STATUS_PREPARE) {
                crossRotateLineFromTopUnderTouch();
            }
        }
    }

    private void crossRotateLineFromTopUnderTouch()
    {
        mTitleView.setVisibility(VISIBLE);
        mTitleView.setText(R.string.hebut_ptr_release_to_refresh);
        //anim
        if(mRotateView != null) {
            mRotateView.clearAnimation();
            mRotateView.startAnimation(mFlipAnimation);
        }
    }

    private void crossRotateLineFromBottomUnderTouch()
    {
        mTitleView.setVisibility(VISIBLE);
        mTitleView.setText(R.string.hebut_ptr_pull_down);
        //anim
        if(mRotateView != null) {
            mRotateView.clearAnimation();
            mRotateView.startAnimation(mReverseFlipAnimation);
        }
    }

    private void tryUpdateLastUpdateTime()
    {
        if(TextUtils.isEmpty(mLastUpdateTimeKey) || !mShoulShowLastUpdate) {
            mLastUpdateTimeView.setVisibility(GONE);
        } else {
            String time = getLastUpdateTime();
            if(TextUtils.isEmpty(time)) {
                mLastUpdateTimeView.setVisibility(GONE);
            } else {
                mLastUpdateTimeView.setVisibility(VISIBLE);
                mLastUpdateTimeView.setText(time);
            }
        }
    }

    public String getLastUpdateTime()
    {
        if(mLastUpdateTime == -1 && !TextUtils.isEmpty(mLastUpdateTimeKey)) {
            mLastUpdateTime = getContext().getSharedPreferences(KEY_SharedPreferences, 0).getLong(mLastUpdateTimeKey, -1);
        }
        if(mLastUpdateTime == -1) {
            return null;
        }
        long diffs = new Date().getTime() - mLastUpdateTime;
        int seconds = (int) (diffs / 1000);
        if(diffs < 0 || seconds < 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getString(R.string.hebut_ptr_last_update));
        if(seconds < 60) {
            sb.append(seconds + getContext().getString(R.string.hebut_ptr_seconds_ago));
        } else {
            int minutes = seconds / 60;
            if (minutes < 60) {
                sb.append(minutes + getContext().getString(R.string.hebut_ptr_minutes_ago));
            } else {
                int hours = minutes / 60;
                if(hours > 24) {
                    Date date = new Date(mLastUpdateTime);
                    sb.append(sdf.format(date));
                } else {
                    sb.append(hours + getContext().getString(R.string.hebut_ptr_hours_ago));
                }
            }
        }

        return sb.toString();
    }

    private class LastUpdateTimeUpdater implements Runnable
    {
        private boolean mIsRunning;

        @Override
        public void run()
        {
            if(!TextUtils.isEmpty(mLastUpdateTimeKey)) {
                tryUpdateLastUpdateTime();
                if(mIsRunning) {
                    postDelayed(this, 1000);
                }
            }
        }

        public void stop()
        {
            mIsRunning = false;
            removeCallbacks(this);
        }

        public void start()
        {
            mIsRunning = true;
            run();
        }
    }
}
