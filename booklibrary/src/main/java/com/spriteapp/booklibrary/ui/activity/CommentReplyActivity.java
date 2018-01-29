package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.ui.adapter.CommentReplyAdapter;
import com.spriteapp.booklibrary.ui.adapter.CommentReplyBean;
import com.spriteapp.booklibrary.ui.dialog.CommentDialog;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity.PLATFORM_ID;
import static com.spriteapp.booklibrary.util.ActivityUtil.COMMENT_ID;
import static com.spriteapp.booklibrary.util.ActivityUtil.REPLYTITLE;
import static com.spriteapp.booklibrary.util.ActivityUtil.SQUAREID;
import static com.spriteapp.booklibrary.util.ActivityUtil.USER_ID;

public class CommentReplyActivity extends TitleActivity {

    private int titleNum = 0;
    private RecyclerView comment_reply_list;
    private LinearLayout comment_bottom;
    private TextView hint_text;
    private ImageView send_img;
    private List<CommentReplyBean> reply = new ArrayList<>();
    private int squareid = 0;
    private int comment_id = 0;
    private int user_id = 0;
    private CommentReplyAdapter adapter;
    private CommentDialog commentDialog;


    @Override
    public void initData() {
        setTitle("回复");
        Intent intent = getIntent();
        titleNum = intent.getIntExtra(REPLYTITLE, 0);
        squareid = intent.getIntExtra(SQUAREID, 0);
        comment_id = intent.getIntExtra(COMMENT_ID, 0);
        user_id = intent.getIntExtra(USER_ID, 0);
        if (titleNum != 0)
            setTitle(titleNum + "条回复");
        initList();
        getReply();
        listener();

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_comment_reply, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        comment_reply_list = (RecyclerView) findViewById(R.id.comment_reply_list);
        comment_bottom = (LinearLayout) findViewById(R.id.comment_bottom);
        hint_text = (TextView) findViewById(R.id.hint_text);
        send_img = (ImageView) findViewById(R.id.send_img);
    }

    public void initList() {
        comment_reply_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentReplyAdapter(this, reply);
        comment_reply_list.setAdapter(adapter);
    }

    public void getReply() {
        showDialog();
        BookApi.getInstance()
                .service
                .square_replypage(comment_id, squareid, 0, titleNum == 0 ? 50 : titleNum, PLATFORM_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<CommentReplyBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<CommentReplyBean> commentReplyBean) {
                        int resultCode = commentReplyBean.getCode();
                        if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                            if (commentReplyBean != null && commentReplyBean.getData() != null) {
                                Log.d("commentReplyBean", "刷新适配器");
                                reply.add(commentReplyBean.getData());
                                adapter.notifyDataSetChanged();
                            }


                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });
    }

    public void listener() {
        commentDialog = new CommentDialog(this);
        comment_bottom.setOnClickListener(this);
//        hint_text.setOnClickListener(this);
//        send_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == comment_bottom) {
            if (!AppUtil.isLogin(this)) return;
            toComment();
        }
    }

    public void toComment() {
        if (commentDialog != null) {
            commentDialog.show();
            commentDialog.getSendText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//发送按钮
                    sendBtn();
                }
            });
            commentDialog.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND)
                        sendBtn();
                    return true;
                }
            });
        }
    }

    public void sendBtn() {
        if (!AppUtil.isLogin(CommentReplyActivity.this)) return;
        String content = commentDialog.getContent();
        if (content.isEmpty()) {
            ToastUtil.showToast("请输入内容");
            return;
        }
        commentDialog.clearText();
        commentDialog.dismiss();
        toCommentHttp(content);
    }

    public void toCommentHttp(final String content) {
        showDialog();
        BookApi.getInstance()
                .service
                .square_addcomment(content, squareid, comment_id, user_id, PLATFORM_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base squareBeanBase) {
                        int resultCode = squareBeanBase.getCode();
                        if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                            ToastUtil.showToast("评论成功");
                            getReply();
//                            CommentReply bean = new CommentReply();
//                            if (UserBean.getInstance().getUser_nickname() == null) {
//                                bean.setUsername("未知");
//                                Util.getUserInfo();
//                            } else {
//                                bean.setUsername(UserBean.getInstance().getUser_nickname());
//                            }
//                            bean.setContent(content);
//                            if (reply != null) {
//                                reply.get(0).getReplydata().getData().add(0, bean);
//                                adapter.notifyDataSetChanged();
//                            }

                        } else if (resultCode == ApiCodeEnum.FAILURE.getValue()) {//失败
                            ToastUtil.showToast("评论失败");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });
    }
}
