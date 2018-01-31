package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.CommentDetailsBean;
import com.spriteapp.booklibrary.model.CommentReply;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.ui.adapter.CommentDetailsAdapter;
import com.spriteapp.booklibrary.ui.adapter.SquareImageAdapter;
import com.spriteapp.booklibrary.ui.dialog.CommentDialog;
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
    private int last_comment_page = 0;
    private int comment_page = 0;
    private RecyclerView recycler_view_comment;
    private NestedScrollView scroll_view;
    private FollowPop followPop;
    private SquareBean squareBean;
    //    private LinearLayout bottom_send;
//    private EditText send_edit;
//    private ImageView yuan_share, yuan_support;
    private LinearLayout square_layout, title_name_layout;//最大的layout
    private TextView follow_btn, default_comment, new_comment, hot_comment;
    private List<TextView> textViews = new ArrayList<>();
    private List<CommentReply> commentList = new ArrayList<>();
    private CommentDetailsAdapter adapter;
    private LinearLayoutManager manager;
    private int type = 1;//默认为1,帖子评论,2为评论回复
    private int comment_id = 0, user_id = 0, pos = -1;
    private InputMethodManager imm;
    private CommentDialog commentDialog;
    private int IsLookComment;
    private int item_height = 0;


    @Override
    public void initData() {
        setTitle("帖子详情");
        Intent intent = getIntent();
        square_id = intent.getIntExtra(ActivityUtil.SQUAREID, 0);
        IsLookComment = intent.getIntExtra(ActivityUtil.ISLOOKCOMMENT, 0);
        Log.d("getIntExtra", "square_id===" + square_id);
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
    public void findViewId() throws Exception {
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
        title_name_layout = (LinearLayout) findViewById(R.id.title_name_layout);
        item_layout.setVisibility(View.GONE);
        comment1 = (TextView) findViewById(R.id.comment1);
        comment2 = (TextView) findViewById(R.id.comment2);
        line = findViewById(R.id.line);
        //评论列表
        recycler_view_comment = (RecyclerView) findViewById(R.id.recycler_view_comment);
        recycler_view_comment.setNestedScrollingEnabled(false);//禁止滑动
        //隐藏的bottom
//        bottom_send = (LinearLayout) findViewById(R.id.bottom_send);
//        send_edit = (EditText) findViewById(R.id.send_edit);
//        yuan_share = (ImageView) findViewById(R.id.yuan_share);
//        yuan_support = (ImageView) findViewById(R.id.yuan_support);
        default_comment = (TextView) findViewById(R.id.default_comment);
        new_comment = (TextView) findViewById(R.id.new_comment);
        hot_comment = (TextView) findViewById(R.id.hot_comment);
        textViews.add(default_comment);
        textViews.add(new_comment);
        textViews.add(hot_comment);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 初始化recycler_view
     */
    public void initList() {
        manager = new LinearLayoutManager(this);
        recycler_view_comment.setLayoutManager(manager);
        adapter = new CommentDetailsAdapter(this, commentList);
        recycler_view_comment.setAdapter(adapter);
    }

    /**
     * 监听事件
     */
    public void listener() {
        commentDialog = new CommentDialog(this);
        adapter.setOnItemClickListener(this);
        more.setOnClickListener(this);
        support_num.setOnClickListener(this);
        comment_num.setOnClickListener(this);
        square_layout.setOnClickListener(this);
//        yuan_share.setOnClickListener(this);
//        yuan_support.setOnClickListener(this);
        follow_btn.setOnClickListener(this);
        default_comment.setOnClickListener(this);
        new_comment.setOnClickListener(this);
        hot_comment.setOnClickListener(this);
        onScrollRefreshOrLoadMore();
    }

    public void setCommentTitleGoneOrShow() {
//        if (commentList.size() == 0) {
//            title_name_layout.setVisibility(View.GONE);
//        } else {
//            title_name_layout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == more) {//更多
//            followPop = new FollowPop(this, more);
        } else if (v == support_num) {//点赞
            if (squareBean != null) {
                goSupport(support_num, squareBean);
            }
        } else if (v == comment_num) {//评论
            showCommentView();

        } else if (v == square_layout) {//隐藏comment

        } else if (v == default_comment) {//默认评论
            setTextColor(0, "default");
        } else if (v == new_comment) {//最新评论
            setTextColor(1, "new");
        } else if (v == hot_comment) {//热门评论
            setTextColor(2, "hot");
        }
    }

    /**
     * 切换评论标题颜色
     */
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
            textViews.get(1).setText("最新\n ");
            textViews.get(2).setText("热门\n ");
        }
        if (pos == 1) {
            textViews.get(0).setText("默认\n ");
            textViews.get(1).setText("最新\n●");
            textViews.get(2).setText("热门\n ");
        }
        if (pos == 2) {
            textViews.get(0).setText("默认\n ");
            textViews.get(1).setText("最新\n ");
            textViews.get(2).setText("热门\n●");
        }
        ACT = act;
        comment_page = 0;
        last_comment_page = 0;
        getCommentDetails();
    }

    /**
     * 获取帖子详情
     */
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
                                    comment_page++;
                                    commentList.addAll(squareBeanBase.getData().getCommentReply());
                                    adapter.notifyDataSetChanged();
                                }
                                setCommentTitleGoneOrShow();
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

    /**
     * 发送评论(暂不引用)
     */
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

    /**
     * 发送评论和回复
     */
    public void sendCommentReply(final String content) {
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
                            if (pos != -1) {//本地手动添加回复
                                CommentReply.ReplayBean.DataBean bean = new CommentReply.ReplayBean.DataBean();
                                if (UserBean.getInstance().getUser_nickname() == null) {
                                    bean.setUsername("未知");
//                                    Util.getUserInfo();
                                } else {
                                    bean.setUsername(UserBean.getInstance().getUser_nickname());
                                }

                                bean.setContent(content);
                                Log.d("commentReply", "pos===" + pos + "username===" + UserBean.getInstance().getUser_nickname() + "content===" + content);
                                commentList.get(pos).getReplay().setTotal(commentList.get(pos).getReplay().getTotal() + 1);
                                commentList.get(pos).getReplay().getData().add(0, bean);
                                adapter.notifyItemChanged(pos);
                            } else if (pos == -1) {//刷新评论
                                refreshComment();
                            }
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

    /**
     * 刷新评论列表
     */
    public void refreshComment() {
        comment_page = 0;
        last_comment_page = 0;
        getCommentDetails();
    }

    /**
     * 获取书籍详情
     */
    public void getCommentDetails() {
        showDialog();
        BookApi.getInstance()
                .service
                .square_detailpage(comment_page, square_id, ACT, PLATFORM_ID)
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
                                comment_page++;
                                commentList.addAll(commentDetailsBean.getData().getCommentList());
                                adapter.notifyDataSetChanged();
                                Log.d("notifyDataSetChanged", "改变集合");
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

    /**
     * 顶部帖子详情填充
     */
    public void setData(SquareBean squareBean) {
        if (squareBean == null) return;
//        if (IsLookComment != 2)
        item_layout.setVisibility(View.VISIBLE);//加载出来显示布局
        Log.d("setData", "setData");
        if (squareBean.getPic_url() != null) {
            if (squareBean.getPic_url().size() == 1) {//一张图片
                image2.setVisibility(View.GONE);
                image_recyclerview.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);
                GlideUtils.loadAndGetImage(image1, squareBean.getPic_url().get(0), squareBean.getPic_url(), 0, this);
//                GlideUtils.loadImage(image1, squareBean.getPic_url().get(0), this);
//                Util.ImageClick(image1, squareBean.getPic_url(), 0, this);
            } else if (squareBean.getPic_url().size() == 2) {//两张图片
                image2.setVisibility(View.VISIBLE);
                image_recyclerview.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);

                GlideUtils.loadAndGetImage(image1, squareBean.getPic_url().get(0), squareBean.getPic_url(), 0, this);
                GlideUtils.loadAndGetImage(image2, squareBean.getPic_url().get(1), squareBean.getPic_url(), 1, this);
//                GlideUtils.loadImage(image1, squareBean.getPic_url().get(0), this);
//                GlideUtils.loadImage(image2, squareBean.getPic_url().get(1), this);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) image1.getLayoutParams();
                layoutParams.height = (BaseActivity.deviceWidth - Util.dp2px(this, 30)) / 3;
                layoutParams.width = layoutParams.height;
                layoutParams.rightMargin = Util.dp2px(this, 5);
                image1.setLayoutParams(layoutParams);
                image2.setLayoutParams(layoutParams);

//                Util.ImageClick(image1, squareBean.getPic_url(), 0, this);
//                Util.ImageClick(image2, squareBean.getPic_url(), 1, this);
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
        boolean isSuppor;
        if (squareBean.getIs_support() == 1)
            isSuppor = false;
        else
            isSuppor = true;

        support_num.setEnabled(isSuppor);
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
        if (IsLookComment == 2) {
            item_height = user_speak.getMeasuredHeight();
            Log.d("item_height", "item_height===" + item_height);
            scroll_view.scrollTo(0, 1000);
        }


    }

    /**
     * 点赞
     */
    public void goSupport(final TextView view, final SquareBean squareBean) {
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

    /**
     * scroll_view滑到底部加载
     */
    public void onScrollRefreshOrLoadMore() {
        scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View childView = v.getChildAt(0);
//                if (scrollY <= oldScrollY && IsLookComment == 2 && item_layout.getVisibility() == View.GONE) {
//                    Log.d("firstPage", "显示布局");
//                    item_layout.setVisibility(View.VISIBLE);
//                }
                if (childView.getMeasuredHeight() <= scrollY + scroll_view.getHeight()) {
                    Log.d("firstPage", "滑动到底部");
                    if (comment_page > last_comment_page) {
                        loadComment();
                    }
                }

            }
        });

    }

    /**
     * 调用加载更多评论方法
     */
    public void loadComment() {
        last_comment_page = comment_page;
        getCommentDetails();
    }


    @Override
    public void setOnItemClickListener(int postion, CommentReply commentReply) {//评论回复
        if (commentReply == null) return;
        type = 2;
        pos = postion;
        showCommentView(commentReply);
    }

    /**
     * 帖子评论
     */
    public void showCommentView() {
        type = 1;
        pos = -1;
        comment_id = 0;
        user_id = 0;
        showCommentView(null);

    }


    /**
     * @param commentReply 评论实体类
     */
    public void showCommentView(final CommentReply commentReply) {
//        bottom_send.setVisibility(View.VISIBLE);
        if (!AppUtil.isLogin(this)) return;
        if (commentDialog == null) return;
        commentDialog.show();
        if (type == 2) {
            commentDialog.setUserName("回复:" + commentReply.getUsername());
            comment_id = commentReply.getId();
            user_id = commentReply.getReplyto();
        } else {
            commentDialog.setUserName("回复");
        }
        commentDialog.getSendText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//发送按钮
                sendBtn();
            }
        });
        commentDialog.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendBtn();
                }
                return true;
            }
        });
    }

    public void sendBtn() {
        if (!AppUtil.isLogin(SquareDetailsActivity.this)) return;
        String content = commentDialog.getContent();
        if (content.isEmpty()) {
            ToastUtil.showToast("请输入内容");
            return;
        }
        commentDialog.clearText();
        commentDialog.dismiss();
        if (type == 1) {//评论
            sendCommentReply(content);
        } else if (type == 2) {//回复
            sendCommentReply(content);
        }
    }


}
