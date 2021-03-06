package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.UpdateCommentEnum;
import com.spriteapp.booklibrary.ui.presenter.PublishCommentPresenter;
import com.spriteapp.booklibrary.ui.view.PublishCommentView;
import com.spriteapp.booklibrary.util.InputUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import de.greenrobot.event.EventBus;

/**
 * 添加书评
 * Created by kuangxiaoguo on 2017/7/25.
 */

public class PublishCommentActivity extends TitleActivity implements PublishCommentView {

    public static final String BOOK_ID_TAG = "bookIdTag";
    private EditText mCommentEditText,mCommentEditTitle;
    private com.spriteapp.booklibrary.widget.RatingBar score_bar;
    private TextView mSendTextView,comment_score;
    private PublishCommentPresenter mPresenter;
    private int mBookId;
    private float rCount;

    @Override
    public void initData() {
        setTitle("发表评论");
        addRightView();
        Intent intent = getIntent();
        mBookId = intent.getIntExtra(BOOK_ID_TAG, 0);
        mPresenter = new PublishCommentPresenter();
        mPresenter.attachView(this);
        addListener();
    }

    private void addListener() {
        mSendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mCommentEditText.getText().toString();
                if (StringUtil.isEmpty(content) || StringUtil.isEmpty(content.trim())) {
                    ToastUtil.showSingleToast("请输入评论内容");
                    return;
                }
                String title = mCommentEditTitle.getText().toString();
                mPresenter.sendComment(mBookId,title, content,rCount);
            }
        });
        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.showSoftInput(PublishCommentActivity.this, mCommentEditText);
            }
        });

        score_bar.setOnRatingChangeListener(new com.spriteapp.booklibrary.widget.RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                rCount = ratingCount;
                setCommentSore();
            }
        });
    }

    public void setCommentSore(){
        String text =  "不好！";
        switch ((int) rCount){
            case 1:
                text = "不好！";
                break;
            case 2:
                text = "一般！";
                break;
            case 3:
                text = "凑合！";
                break;
            case 4:
                text = "不错哟！";
                break;
            case 5:
                text = "强烈推荐";
                break;
        }
        comment_score.setText(text);
    }

    private void addRightView() {
        View rightView = LayoutInflater.from(this).inflate(R.layout.book_reader_common_right_text_view, null);
        mSendTextView = (TextView) rightView.findViewById(R.id.book_reader_common_right_text_view);
        mRightLayout.addView(rightView);
        mSendTextView.setText("发送");
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_publish_comment, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        mCommentEditTitle = (EditText) findViewById(R.id.book_reader_comment_edit_title);
        mCommentEditText = (EditText) findViewById(R.id.book_reader_comment_edit_text);
        score_bar = (com.spriteapp.booklibrary.widget.RatingBar) findViewById(R.id.ratingBar);
        comment_score = (TextView) findViewById(R.id.comment_score);
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void setData(Base<Void> result) {
        finish();
        EventBus.getDefault().post(UpdateCommentEnum.UPDATE_COMMENT);
    }

    @Override
    public void showNetWorkProgress() {
        showDialog();
    }

    @Override
    public void disMissProgress() {
        dismissDialog();
    }

    @Override
    public Context getMyContext() {
        return this;
    }
}
