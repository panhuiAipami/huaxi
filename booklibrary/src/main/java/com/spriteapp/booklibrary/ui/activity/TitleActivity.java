package com.spriteapp.booklibrary.ui.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public abstract class TitleActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TitleActivity";

    ImageView mBackImageView;
    TextView mTitleTextView;
    LinearLayout mLeftLayout;
    LinearLayout mRightLayout;
    LinearLayout mCenterLayout;
    FrameLayout mContainerLayout;
    RelativeLayout mTitleLayout;
    View mLineView;

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_activity_title;
    }

    @Override
    public void findViewId() {
        mBackImageView = (ImageView) findViewById(R.id.book_reader_back_imageView);
        mTitleTextView = (TextView) findViewById(R.id.book_reader_title_textView);
        mLeftLayout = (LinearLayout) findViewById(R.id.book_reader_left_layout);
        mRightLayout = (LinearLayout) findViewById(R.id.book_reader_right_layout);
        mContainerLayout = (FrameLayout) findViewById(R.id.book_reader_container_layout);
        mCenterLayout = (LinearLayout) findViewById(R.id.book_reader_center_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.book_reader_title_layout);
        mLineView = findViewById(R.id.book_reader_title_line_view);
        addContentView();
        mLeftLayout.setOnClickListener(this);
    }

    @Override
    public void configViews() {
        HuaXiConfig config = HuaXiSDK.getInstance().getConfig();
        int titleBackground = config.getTitleBackground();
        if (titleBackground != 0) {
            mTitleLayout.setBackgroundColor(titleBackground);
        }
        int titleColor = config.getTitleColor();
        if (titleColor != 0) {
            mTitleTextView.setTextColor(titleColor);
        }
        int backImageResource = config.getBackImageResource();
        if (backImageResource != 0) {
            mBackImageView.setImageResource(backImageResource);
        }
    }

    public void setTitle(int titleId) {
        mTitleTextView.setText(getResources().getString(titleId));
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public abstract void addContentView();

    @Override
    public void onClick(View v) {
        if (v == mLeftLayout) {
            finish();
        }
    }
}
