package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.ui.adapter.SquareImageAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SquareDetailsActivity extends TitleActivity {
    private ImageView user_head, more, image1, image2;
    private TextView user_name, send_time, user_speak, read_num, support_num, comment_num;
    private RecyclerView image_recyclerview;
    private LinearLayout image_layout, comment_layout, item_layout;
    private ImageView head1, head2, head3;
    private TextView comment1, comment2;
    private View line;
    private int square_id;
    private int page = 1;
    private SwipeRefreshLayout swipe_comment;
    private RecyclerView recycler_view_comment;


    @Override
    public void initData() {
        setTitle("帖子详情");
        Intent intent = getIntent();
        square_id = intent.getIntExtra(ActivityUtil.SQUAREID, 0);
        getDetails();
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
        image_recyclerview = (RecyclerView) findViewById(R.id.image_recyclerview);
        image_layout = (LinearLayout) findViewById(R.id.image_layout);
        comment_layout = (LinearLayout) findViewById(R.id.comment_layout);
        item_layout = (LinearLayout) findViewById(R.id.item_layout);
        comment1 = (TextView) findViewById(R.id.comment1);
        comment2 = (TextView) findViewById(R.id.comment2);
        line = findViewById(R.id.line);
        //评论列表
        recycler_view_comment = (RecyclerView) findViewById(R.id.recycler_view_comment);
        swipe_comment = (SwipeRefreshLayout) findViewById(R.id.swipe_comment);

    }

    public void getDetails() {//获取帖子详情
        BookApi.getInstance()
                .service
                .square_detail(page, square_id)
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
                            if (squareBeanBase != null) {
                                setData(squareBeanBase.getData());//填充界面
                            }


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

    public void setData(SquareBean squareBean) {//顶部帖子详情填充
        if (squareBean == null) return;
        if (squareBean.getPic_url() != null) {
            if (squareBean.getPic_url().size() == 1) {//一张图片
                image2.setVisibility(View.GONE);
                image_recyclerview.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(image1, squareBean.getPic_url().get(0), this);
            } else if (squareBean.getPic_url().size() == 2) {//两张图片
                image2.setVisibility(View.GONE);
                image_recyclerview.setVisibility(View.GONE);
                image1.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(image1, squareBean.getPic_url().get(0), this);
                GlideUtils.loadImage(image2, squareBean.getPic_url().get(1), this);
            } else if (squareBean.getPic_url().size() > 2) {//两张图片以上
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
                image_recyclerview.setVisibility(View.VISIBLE);
                image_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
}
