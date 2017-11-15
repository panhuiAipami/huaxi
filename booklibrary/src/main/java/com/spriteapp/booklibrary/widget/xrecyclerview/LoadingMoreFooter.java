package com.spriteapp.booklibrary.widget.xrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;


public class LoadingMoreFooter extends LinearLayout {

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;
    private String loadingHint;
    private String noMoreHint;
    private String loadingDoneHint;

    private View mContentChildView;
    private View mProgressBar;
    private TextView mTextView;

    public LoadingMoreFooter(Context context) {
        super(context);
        initView(context);
    }

    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setLoadingHint(String hint) {
        loadingHint = hint;
    }

    public void setNoMoreHint(String hint) {
        noMoreHint = hint;
    }

    public void setLoadingDoneHint(String hint) {
        loadingDoneHint = hint;
    }

    public void initView(Context context) {
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.book_reader_recyclerview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mProgressBar = moreView.findViewById(R.id.listview_foot_progress);
        mTextView = (TextView) moreView.findViewById(R.id.listview_foot_more);
        loadingHint = "正在刷新...";
        noMoreHint = "没有更多了";
        loadingDoneHint = "加载完成";
    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                mProgressBar.setVisibility(VISIBLE);
                mTextView.setText(loadingHint);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                mTextView.setText(loadingDoneHint);
                this.setVisibility(View.INVISIBLE);
                break;
            case STATE_NOMORE:
                mTextView.setText(noMoreHint);
                mProgressBar.setVisibility(GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }

}
