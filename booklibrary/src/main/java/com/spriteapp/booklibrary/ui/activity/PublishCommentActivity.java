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
 * Created by kuangxiaoguo on 2017/7/25.
 */

public class PublishCommentActivity extends TitleActivity implements PublishCommentView {

    public static final String BOOK_ID_TAG = "bookIdTag";
    private EditText mCommentEditText;
    private TextView mSendTextView;
    private PublishCommentPresenter mPresenter;
    private int mBookId;

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
                mPresenter.sendComment(mBookId, content);
            }
        });
        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.showSoftInput(PublishCommentActivity.this, mCommentEditText);
            }
        });
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
        mCommentEditText = (EditText) findViewById(R.id.book_reader_comment_edit_text);
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
