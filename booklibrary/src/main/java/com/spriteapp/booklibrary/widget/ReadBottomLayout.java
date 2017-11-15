package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.ScreenUtil;

/**
 * Created by kuangxiaoguo on 2017/7/17.
 */

public class ReadBottomLayout extends LinearLayout {

    private View mView;
    private ImageView mChapterImageView;
    private ImageView mProgressImageView;
    private ImageView mModeImageView;
    private ImageView mTextImageView;
    private TextView mChapterTextView;
    private TextView mProgressTextView;
    private TextView mModeTextView;
    private TextView mTextTextView;

    public ReadBottomLayout(Context context) {
        this(context, null);
    }

    public ReadBottomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadBottomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mView = LayoutInflater.from(context)
                .inflate(R.layout.book_reader_book_operation_bottom_view, null);
        addView(mView);
        LinearLayout.LayoutParams params = (LayoutParams) mView.getLayoutParams();
        params.width = ScreenUtil.getScreenWidth();
        mView.setLayoutParams(params);
        findId();
    }

    private void findId() {
        mChapterImageView = (ImageView) findViewById(R.id.book_reader_chapter_image_view);
        mProgressImageView = (ImageView) findViewById(R.id.book_reader_progress_image_view);
        mModeImageView = (ImageView) findViewById(R.id.book_reader_mode_image_view);
        mTextImageView = (ImageView) findViewById(R.id.book_reader_text_image_view);
        mChapterTextView = (TextView) findViewById(R.id.book_reader_chapter_text_view);
        mProgressTextView = (TextView) findViewById(R.id.book_reader_progress_text_view);
        mModeTextView = (TextView) findViewById(R.id.book_reader_mode_text_view);
        mTextTextView = (TextView) findViewById(R.id.book_reader_text_text_view);
    }

    public void changeMode(boolean isNight) {
        mChapterImageView.setImageResource(isNight ? R.drawable.book_reader_chapter_night_selector :
                R.drawable.book_reader_chapter_selector);
        mProgressImageView.setImageResource(isNight ? R.drawable.book_reader_progress_night_selector :
                R.drawable.book_reader_progress_selector);
        mModeImageView.setImageResource(isNight ? R.drawable.book_reader_mode_night_selector :
                R.drawable.book_reader_mode_selector);
        mTextImageView.setImageResource(isNight ? R.drawable.book_reader_text_size_night_selector :
                R.drawable.book_reader_text_size_selector);
        mView.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background
                : R.color.book_reader_white);

        mChapterTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_bottom_text_night_selector :
                R.color.book_reader_bottom_text_selector));
        mProgressTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_bottom_text_night_selector :
                R.color.book_reader_bottom_text_selector));
        mModeTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_bottom_text_night_selector :
                R.color.book_reader_bottom_text_selector));
        mTextTextView.setTextColor(getResources().getColorStateList(isNight ?
                R.color.book_reader_bottom_text_night_selector :
                R.color.book_reader_bottom_text_selector));
        mModeTextView.setText(isNight ? "日间模式" : "夜间模式");
    }

    public void setColor(boolean isNight) {
        mView.setBackgroundResource(isNight ? R.color.book_reader_white : R.color.book_reader_black);
    }
}
