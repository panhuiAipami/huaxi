package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

/**
 * Created by kuangxiaoguo on 2017/7/21.
 */

public class ReadRightTitleLayout extends LinearLayout {

    private View mView;
    private Context mContext;
    private ImageView mBuyImageView;
    private ImageView mAddShelfImageView;
    private ImageView mCommentImageView;
    private ImageView mHomeImageView;
    private ImageView mRewardImageView;
    private ImageView mMoreImageView;
    private ReadTitleListener mTitleListener;
    private TextView read_book_title;

    public ReadRightTitleLayout(Context context) {
        this(context, null);
    }

    public ReadRightTitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadRightTitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.book_reader_read_title_layout, null);
        addView(mView);
        mBuyImageView = (ImageView) mView.findViewById(R.id.book_reader_buy_image_view);
        mAddShelfImageView = (ImageView) mView.findViewById(R.id.book_reader_add_shelf_image_view);
        mCommentImageView = (ImageView) mView.findViewById(R.id.book_reader_comment_image_view);
        mHomeImageView = (ImageView) mView.findViewById(R.id.book_reader_home_image_view);
        mRewardImageView = (ImageView) mView.findViewById(R.id.book_reader_reward_image_view);
        mMoreImageView = (ImageView) mView.findViewById(R.id.book_reader_more_image_view);
        read_book_title = (TextView) mView.findViewById(R.id.read_book_title);
        setListener();
    }

    private void setListener() {
        click(mBuyImageView);
        click(mAddShelfImageView);
        click(mCommentImageView);
        click(mRewardImageView);
        click(mMoreImageView);
        click(mHomeImageView);
    }

    private void click(final View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleListener == null) {
                    return;
                }
                if (view == mBuyImageView) {
                    mTitleListener.clickBuy();
                } else if (view == mAddShelfImageView) {//下载
                    mTitleListener.clickAddShelf();
                } else if (view == mCommentImageView) {//评论
                    mTitleListener.clickComment();
                } else if (view == mRewardImageView) {
                    mTitleListener.clickReward();
                } else if (view == mMoreImageView) {
                    mTitleListener.clickMore();
                } else if (view == mHomeImageView) {
                    mTitleListener.clickHome();
                }
            }
        });
    }

    public void changeMode(boolean isNight) {
        mBuyImageView.setImageResource(isNight ? R.drawable.book_reader_buy_night_selector
                : R.drawable.book_reader_buy_day_selector);

        mAddShelfImageView.setImageResource(isNight ? R.drawable.book_reader_download_day_selector
                : R.drawable.book_reader_download_day_selector);

        mRewardImageView.setImageResource(isNight ? R.drawable.book_reader_reward_night_selector
                : R.drawable.book_reader_reward_day_selector);

        mMoreImageView.setImageResource(isNight ? R.drawable.book_reader_more_night_selector
                : R.drawable.book_reader_more_day_selector);

        mHomeImageView.setImageResource(isNight ? R.drawable.book_reader_home_night_selector
                : R.drawable.book_reader_home_day_selector);
    }

    public interface ReadTitleListener {
        void clickBuy();

        void clickAddShelf();

        void clickComment();

        void clickReward();

        void clickMore();

        void clickHome();
    }

    public void setAddShelfViewState(boolean hasAddShelf) {
//        if (mAddShelfImageView == null) {
//            return;
//        }
//        mAddShelfImageView.setVisibility(hasAddShelf ? GONE : VISIBLE);
    }

    public void setTitleListener(ReadTitleListener mTitleListener) {
        this.mTitleListener = mTitleListener;
    }

    public void setBuyImageState(boolean show) {//购买按钮
        mBuyImageView.setVisibility(show ? GONE : GONE);
    }

    public void setTitleName(String name) {
        if (name != null)
            read_book_title.setText(name);
    }
}
