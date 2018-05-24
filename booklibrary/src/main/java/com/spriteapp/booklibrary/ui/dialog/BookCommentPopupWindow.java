package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.model.BookCommentBean;
import com.spriteapp.booklibrary.model.BookCommentReplyBean;
import com.spriteapp.booklibrary.ui.adapter.CommentReplyAdapter;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.widget.readview.BubbleRelativeLayout;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 每一段的评论
 * Created by panhui on 2018/5/4.
 */

public class BookCommentPopupWindow extends PopupWindow implements URecyclerView.LoadingListener {
    private Activity mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    private BubbleRelativeLayout bubbleRelative;
    private URecyclerView recycler_book_comment;
    private CommentReplyAdapter adapter;
    private List<BookCommentBean> data = new ArrayList<>();
    private BookCommentReplyBean commentReplyBean = new BookCommentReplyBean();
    private int book_id, chapter_id, pid;
    private String selectText;
    long start_time = 0, stop_time = 0;

    public BookCommentPopupWindow(Activity context, int book_id, int chapter_id, int pid,String selectText) {
        super(context);
        this.mContext = context;
        this.book_id = book_id;
        this.chapter_id = chapter_id;
        this.pid = pid;
        this.selectText = selectText;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = mInflater.inflate(R.layout.comment_pop_window_layout, null);

        setContentView(mContentView);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        backgroundAlpha(0.8f);
//        setAnimationStyle(R.style.MyPopupWindow);
        //设置背景只有设置了这个才可以点击外边和BACK消失
        setBackgroundDrawable(new ColorDrawable());
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {

                    return true;
                }
                //不是点击外部
                return false;
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });

        initView();
        getChapterCommentNum();
    }


    public void show(View parent, int x, int y) {
        BubbleRelativeLayout.BubbleLegOrientation orientation;
        //窗口显示在屏幕下半部分
        if (y < BaseActivity.deviceHeight / 2) {
            orientation = BubbleRelativeLayout.BubbleLegOrientation.TOP;
            y += ScreenUtil.dpToPxInt(10);

        } else {//在屏幕上半部分
            orientation = BubbleRelativeLayout.BubbleLegOrientation.BOTTOM;
            y -= (getMeasureHeight()+ScreenUtil.dpToPxInt(2));
        }
        bubbleRelative.setBubbleParams(orientation, x - ScreenUtil.dpToPxInt(5)); // 设置气泡布局方向及尖角偏移

        showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
    }


    private void initView() {
        bubbleRelative = (BubbleRelativeLayout) mContentView.findViewById(R.id.bubbleRelative);
        recycler_book_comment = (URecyclerView) mContentView.findViewById(R.id.recycler_book_comment);
        recycler_book_comment.setLoadingListener(this);

        adapter = new CommentReplyAdapter(mContext, commentReplyBean, 3);
        recycler_book_comment.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_book_comment.setAdapter(adapter);

        mContentView.findViewById(R.id.write_book_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isLogin(mContext)) {
                    new BookCommentDialog(mContext, selectText, pid).show();
                    dismiss();
                }
            }
        });
    }

    /**
     * 获取章节评论数和评论
     */
    public void getChapterCommentNum() {
        stop_time = start_time;
        BookApi.getInstance().service.get_chapter_comment_content(book_id, chapter_id, pid, "parent", start_time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookCommentBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Base<List<BookCommentBean>> listBase) {
                        if (listBase != null && listBase.getData() != null && listBase.getData().size() > 0) {
                            if (start_time == 0)
                                data.clear();
                            data.addAll(listBase.getData());
                            commentReplyBean.setLists(data);
                            adapter.notifyDataSetChanged();
                            start_time = data.get(data.size() - 1).getComment_datetime();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onLoadMore() {
        if (stop_time != start_time)
            getChapterCommentNum();
    }


    /**
     * 测量高度
     *
     * @return
     */
    public int getMeasureHeight() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popHeight = getContentView().getMeasuredHeight();
        return popHeight;
    }

    // 设置屏幕透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0~1.0
        mContext.getWindow().setAttributes(lp); //act 是上下文context

    }
}
