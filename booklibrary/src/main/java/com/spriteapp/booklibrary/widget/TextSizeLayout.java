package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.TextSizeCallback;

/**
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class TextSizeLayout extends LinearLayout {

    private Context mContext;
    private TextView mSubTextView;
    private TextView mPlusTextView;
    private TextSizeView mTextSizeView;
    private int mTextSize;
    private TextSizeCallback mCallBack;
    private View mView;

    public TextSizeLayout(Context context) {
        this(context, null);
    }

    public TextSizeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSizeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.book_reader_text_size_layout, null);
        addView(mView);
        mSubTextView = (TextView) mView.findViewById(R.id.book_reader_sub_text_view);
        mPlusTextView = (TextView) mView.findViewById(R.id.book_reader_plus_text_view);
        mTextSizeView = (TextSizeView) mView.findViewById(R.id.book_reader_text_size_view);
        setListener();
    }

    public void changeMode(boolean isNight) {
        mView.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background
                : R.color.book_reader_white);
        mTextSizeView.changeMode(isNight);
        mPlusTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_bottom_text_night_selector
                : R.color.book_reader_bottom_text_selector));
        mSubTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_bottom_text_night_selector
                : R.color.book_reader_bottom_text_selector));
    }

    private void setListener() {
        mSubTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextSize = mTextSizeView.subTextSize();
                if (mCallBack != null) {
                    mCallBack.sendTextSize(mTextSize);
                }
            }
        });
        mPlusTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextSize = mTextSizeView.plusTextSize();
                if (mCallBack != null) {
                    mCallBack.sendTextSize(mTextSize);
                }
            }
        });
    }

    public void setCallBack(TextSizeCallback mCallBack) {
        this.mCallBack = mCallBack;
        mTextSizeView.setCallBack(mCallBack);
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setPosition(int position) {
        mTextSizeView.setPosition(position);
    }

}
