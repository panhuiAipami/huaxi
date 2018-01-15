package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.CommentDetailsBean;
import com.spriteapp.booklibrary.model.CommentReply;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.ui.adapter.CommentDetailsAdapter;
import com.spriteapp.booklibrary.ui.adapter.SquareImageAdapter;
import com.spriteapp.booklibrary.ui.dialog.FollowPop;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SquareDetailsActivity extends TitleActivity implements CommentDetailsAdapter.OnItemClickListener {
    public static final int PLATFORM_ID = 1;//社区接口添加来区分花溪与嘎吱
    private String ACT = "default";//获取评论的act
    private ImageView user_head, more, image1, image2;
    private TextView user_name, send_time, user_speak, read_num, support_num, comment_num;
    private RecyclerView image_recyclerview;
    private LinearLayout image_layout, comment_layout, item_layout;
    private ImageView head1, head2, head3;
    private TextView comment1, comment2;
    private View line;
    private int square_id;
    private int page = 0;
    private int comment_page = 0;
    private RecyclerView recycler_view_comment;
    private NestedScrollView scroll_view;
    private FollowPop followPop;
    private SquareBean squareBean;
    private LinearLayout bottom_send;
    private EditText send_edit;
    private ImageView yuan_share, yuan_support;
    private LinearLayout square_layout;//最大的layout
    private TextView follow_btn, default_comment, new_comment, hot_comment;
    private List<TextView> textViews = new ArrayList<>();
    private List<CommentReply> commentList = new ArrayList<>();
    private CommentDetailsAdapter adapter;
    private LinearLayoutManager manager;
    private int type = 1;//默认为1,帖子评论,2为评论回复
    private int comment_id = 0, user_id = 0;


    @Override
    public void initData() {
        setTitle("帖子详情");
        Intent intent = getIntent();
        square_id = intent.getIntExtra(ActivityUtil.SQUAREID, 0);
        initList();
        getDetails();
        listener();
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_square_details, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() {
        super.findViewId();
        user_head = (ImageView) findViewById(R.id.user_head);
        head1 = (ImageView) findViewById(R.id.head1);
        head2 = (ImageView) findViewById(R.id.head2);
        head3 = (ImageView) findViewById(R.id.head3);
        more = (ImageView) findViewById(R.id.more);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        user_name = (TextView) findViewById(R.id.user_name);
        send_time = (TextView) findViewById(R.id.send_time);
        user_speak = (TextView) findViewById(R.id.user_speak);
        read_num = (TextView) findViewById(R.id.read_num);
        support_num = (TextView) findViewById(R.id.support_num);
        comment_num = (TextView) findViewById(R.id.comment_num);
        follow_btn = (TextView) findViewById(R.id.follow_btn);
        image_recyclerview = (RecyclerView) findViewById(R.id.image_recyclerview);
        scroll_view = (NestedScrollView) findViewById(R.id.scroll_view);
        image_layout = (LinearLayout) findViewById(R.id.image_layout);
        comment_layout = (LinearLayout) findViewById(R.id.comment_layout);
        item_layout = (LinearLayout) findViewById(R.id.item_layout);
        square_layout = (LinearLayout) findViewById(R.id.square_layout);
        item_layout.setVisibility(View.GONE);
        comment1 = (TextView) findViewById(R.id.comment1);
        comment2 = (TextView) findViewById(R.id.comment2);
        line = findViewById(R.id.line);
        //评论列表
        recycler_view_comment = (RecyclerView) findViewById(R.id.recycler_view_comment);
        recycler_view_comment.setNestedScrollingEnabled(false);//禁止滑动
        //隐藏的bottom
        bottom_send = (LinearLayout) findViewById(R.id.bottom_send);
        send_edit = (EditText) findViewById(R.id.send_edit);
        yuan_share = (ImageView) findViewById(R.id.yuan_share);
        yuan_support = (ImageView) findViewById(R.id.yuan_support);
        default_comment = (TextView) findViewById(R.id.default_comment);
        new_comment = (TextView) findViewById(R.id.new_comment);
        hot_comment = (TextView) findViewById(R.id.hot_comment);
        textViews.add(default_comment);
        textViews.add(new_comment);
        textViews.add(hot_comment);
    }

    public void initList() {
        manager = new LinearLayoutManager(this);
        recycler_view_comment.setLayoutManager(manager);
        adapter = new CommentDetailsAdapter(this, commentList);
        recycler_view_comment.setAdapter(adapter);
    }

    public void listener() {
        adapter.setOnItemClickListener(this);
        more.setOnClickListener(this);
        support_num.setOnClickListener(this);
        comment_num.setOnClickListener(this);
        square_layout.setOnClickListener(this);
        yuan_share.setOnClickListener(this);
        yuan_support.setOnClickListener(this);
        follow_btn.setOnClickListener(this);
        default_comment.setOnClickListener(this);
        new_comment.setOnClickListener(this);
        hot_comment.setOnClickListener(this);
        send_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == more) {//更多
            followPop = new FollowPop(this, more);
        } else if (v == support_num) {//点赞
            if (squareBean != null) {
                goSupport(support_num, squareBean);
            }
        } else if (v == comment_num) {//评论
            if (squareBean != null && bottom_send.getVisibility() == View.GONE) {
                showCommentView();//帖子回复
            }
        } else if (v == square_layout) {//隐藏comment
            if (bottom_send.getVisibility() == View.VISIBLE)
                bottom_send.setVisibility(View.GONE);
        } else if (v == yuan_share) {
            if (!AppUtil.isLogin(this)) return;
            String content = send_edit.getText().toString().trim();
            if (content.isEmpty()) {
                ToastUtil.showToast("请输入内容");
                return;
            }
            if (type == 1) {//评论
                sendComment(content);
            } else if (type == 2) {//回复
                sendCommentReply(content);
            }

        } else if (v == yuan_support) {

        } else if (v == default_comment) {//默认评论
            setTextColor(0, "default");
        } else if (v == new_comment) {//最新评论
            setTextColor(1, "new");
        } else if (v == hot_comment) {//热门评论
            setTextColor(2, "hot");
        }
    }

    public void setTextColor(int pos, String act) {
        for (int i = 0; i < textViews.size(); i++) {
            if (i == pos) {
                textViews.get(i).setEnabled(false);
            } else {
                textViews.get(i).setEnabled(true);
            }
        }
        if (pos == 0) {
            textViews.get(0).setText("默认\n●");
            textViews.get(1).setText("最新\n");
            textViews.get(2).setText("热门\n");
        }
        if (pos == 1) {
            textViews.get(0).setText("默认\n");
            textViews.get(1).setText("最新\n●");
            textViews.get(2).setText("热门\n");
        }
        if (pos == 2) {
            textViews.get(0).setText("默认\n");
            textViews.get(1).setText("最新\n");
            textViews.get(2).setText("热门\n●");
        }
        ACT = act;
        getCommentDetails();
    }

    public void getDetails() {//获取帖子详情
        showDialog();
        BookApi.getInstance()
                .service
                .square_detail(page, square_id, PLATFORM_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<SquareBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<SquareBean> squareBeanBase) {
                        int resultCode = squareBeanBase.getCode();
                        if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                            if (squareBeanBase != null && squareBeanBase.getData() != null) {
                                squareBean = squareBeanBase.getData();
                                setData(squareBeanBase.getData());//填充界面
                                if (squareBeanBase.getData().getCommentReply() != null && squareBeanBase.getData().getCommentReply().size() != 0) {
                                    if (commentList.size() != 0) commentList.clear();
                                    commentList.addAll(squareBeanBase.getData().getCommentReply());
                                    adapter.notifyDataSetChanged();
                                }
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

    public void sendComment(String content) {//添加评论
        showDialog();
        BookApi.getInstance()
                .service
                .square_addcomment(content, square_id, PLATFORM_ID)
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
                            send_edit.setText("");
                            bottom_send.setVisibility(View.GONE);
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

    public void sendCommentReply(String content) {
        showDialog();
        BookApi.getInstance()
                .service
                .square_addcomment(content, square_id, comment_id, user_id, PLATFORM_ID)
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
                            send_edit.setText("");
                            bottom_send.setVisibility(View.GONE);
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

    public void getCommentDetails() {
        showDialog();
        BookApi.getInstance()
                .service
                .square_detail(comment_page, square_id, ACT, PLATFORM_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<CommentDetailsBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<CommentDetailsBean> commentDetailsBean) {
                        int resultCode = commentDetailsBean.getCode();
                        if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                            if (commentDetailsBean != null && commentDetailsBean.getData() != null && commentDetailsBean.getData().getCommentList() != null && commentDetailsBean.getData().getCommentList().size() != 0) {
                                if (comment_page == 0) commentList.clear();//刷新则清空集合
                                commentList.addAll(commentDetailsBean.getData().getCommentList());
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

    public void setData(SquareBean squareBean) {//顶部帖子详情填充
        if (squareBean == null) return;
        item_layout.setVisibility(View.VISIBLE);//加载出来显示布局
        Log.d("setData","setData");
        if (squareBean.getPic_url() != null) {
            if (squareBean.getPic_url().size() == 1) {//一张图片
                image2.setVisibility(View.INVISIBLE);
                image_recyclerview.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(image1, squareBean.getPic_url().get(0), this);
                Util.ImageClick(image1, squareBean.getPic_url(), 0, this);
            } else if (squareBean.getPic_url().size() == 2) {//两张图片
                image2.setVisibility(View.VISIBLE);
                image_recyclerview.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(image1, squareBean.getPic_url().get(0), this);
                GlideUtils.loadImage(image2, squareBean.getPic_url().get(1), this);
                Util.ImageClick(image1, squareBean.getPic_url(), 0, this);
                Util.ImageClick(image2, squareBean.getPic_url(), 1, this);
            } else if (squareBean.getPic_url().size() > 2) {//两张图片以上
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
                image_recyclerview.setVisibility(View.VISIBLE);
                image_recyclerview.setLayoutManager(new GridLayoutManager(this, squareBean.getPic_url().size() == 4 ? 2 : 3));
                image_recyclerview.setAdapter(new SquareImageAdapter(this, squareBean.getPic_url()));//嵌套列表
            } else {//没有图片
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
                image_recyclerview.setVisibility(View.GONE);
            }
        }
        GlideUtils.loadImage(user_head, squareBean.getUser_avatar(), this);
        user_name.setText(squareBean.getUsername());
        send_time.setText(TimeUtil.getTimeFormatText(Long.parseLong(squareBean.getAddtime() + "000")) + "  来自" + squareBean.getLocation());
        user_speak.setText(squareBean.getSubject());
        read_num.setText(Util.getFloat(squareBean.getReadnum()) + "次预览");
        comment_num.setText(Util.getFloat(squareBean.getCommentnum()));
        support_num.setText(Util.getFloat(squareBean.getSupportnum()));
        goSupport(support_num, squareBean);//点赞
        if (squareBean.getReadhistory() != null) {
            List<ImageView> imageView = new ArrayList<>();
            imageView.add(head1);
            imageView.add(head2);
            imageView.add(head3);
            if (squareBean.getReadhistory().size() == 1) {//一个人浏览
                head1.setVisibility(View.VISIBLE);
                head2.setVisibility(View.GONE);
                head3.setVisibility(View.GONE);
                GlideUtils.loadImage(head1, squareBean.getReadhistory().get(0).getUser_avatar(), this);
            } else if (squareBean.getReadhistory().size() == 2) {//两个人浏览
                head1.setVisibility(View.VISIBLE);
                head2.setVisibility(View.VISIBLE);
                head3.setVisibility(View.GONE);
                GlideUtils.loadImage(head1, squareBean.getReadhistory().get(0).getUser_avatar(), this);
                GlideUtils.loadImage(head2, squareBean.getReadhistory().get(1).getUser_avatar(), this);
            } else if (squareBean.getReadhistory().size() > 2) {//两个人以上浏览
                head1.setVisibility(View.VISIBLE);
                head2.setVisibility(View.VISIBLE);
                head3.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(head1, squareBean.getReadhistory().get(0).getUser_avatar(), this);
                GlideUtils.loadImage(head2, squareBean.getReadhistory().get(1).getUser_avatar(), this);
                GlideUtils.loadImage(head3, squareBean.getReadhistory().get(2).getUser_avatar(), this);
            } else {//没有人浏览
                head1.setVisibility(View.GONE);
                head2.setVisibility(View.GONE);
                head3.setVisibility(View.GONE);
            }
        }

    }

    public void goSupport(final TextView view, final SquareBean squareBean) {//点赞
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!AppUtil.isLogin(SquareDetailsActivity.this)) {
                    return;
                }
                BookApi.getInstance()
                        .service
                        .square_actcmt(squareBean.getId(), "supportnum", PLATFORM_ID)
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
                                    ToastUtil.showToast("点赞成功");
                                    view.setEnabled(false);//图标颜色
                                    squareBean.setSupportnum(squareBean.getSupportnum() + 1);//改变数量
                                    view.setText(squareBean.getSupportnum() + "");
                                } else if (resultCode == ApiCodeEnum.FAILURE.getValue()) {//失败
                                    ToastUtil.showToast("点赞失败");
                                } else if (resultCode == ApiCodeEnum.EVER.getValue()) {//曾经点过
                                    ToastUtil.showToast("您已经点过");
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    public void onScrollRefreshOrLoadMore() {
        scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View childView = v.getChildAt(0);
                if (childView.getMeasuredHeight() <= scrollY + scroll_view.getHeight()) {
                    Log.d("firstPage", "滑动到底部");
                    if (page > page) {
                        getCommentDetails();
                    }
                }

            }
        });

    }


    @Override
    public void setOnItemClickListener(int postion, CommentReply commentReply) {//评论回复
        ToastUtil.showToast("回复");
        if (commentReply == null) return;
        type = 2;
        showCommentView(commentReply);
    }

    public void showCommentView() {//帖子评论
        type = 1;
        showCommentView(null);

    }

    /**
     * @param commentReply 评论实体类
     */
    public void showCommentView(CommentReply commentReply) {
        bottom_send.setVisibility(View.VISIBLE);
        if (type == 2) {
            send_edit.setHint("回复:" + commentReply.getUsername());
            comment_id = commentReply.getId();
            user_id = commentReply.getReplyto();
        }

    }
}
