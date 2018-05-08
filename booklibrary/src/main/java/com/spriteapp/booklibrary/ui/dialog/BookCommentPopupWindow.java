package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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

public class BookCommentPopupWindow extends PopupWindow {
    private Activity mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    private BubbleRelativeLayout bubbleRelative;
    private URecyclerView recycler_book_comment;
    private CommentReplyAdapter adapter;
    private List<BookCommentBean> data = new ArrayList<>();
    private BookCommentReplyBean commentReplyBean = new BookCommentReplyBean();
    private int book_id, chapter_id, pid;

    public BookCommentPopupWindow(Activity context, int book_id, int chapter_id, int pid) {
        super(context);
        this.mContext = context;
        this.book_id = book_id;
        this.chapter_id = chapter_id;
        this.pid = pid;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = mInflater.inflate(R.layout.comment_pop_window_layout, null);

        setContentView(mContentView);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
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
        initView();
        getChapterCommentNum();
    }


    public void show(View parent, int x, int y) {
        BubbleRelativeLayout.BubbleLegOrientation orientation;
        //窗口显示在屏幕下半部分
        if (y < BaseActivity.deviceHeight / 2) {
            orientation = BubbleRelativeLayout.BubbleLegOrientation.TOP;
            y += ScreenUtil.dpToPxInt(10);
            //在屏幕上半部分
        } else {
            orientation = BubbleRelativeLayout.BubbleLegOrientation.BOTTOM;
            y -= getMeasureHeight();
        }
        bubbleRelative.setBubbleParams(orientation, x - ScreenUtil.dpToPxInt(25)); // 设置气泡布局方向及尖角偏移

        showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
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


    private void initView() {
        bubbleRelative = (BubbleRelativeLayout) mContentView.findViewById(R.id.bubbleRelative);
        recycler_book_comment = (URecyclerView) mContentView.findViewById(R.id.recycler_book_comment);

        adapter = new CommentReplyAdapter(mContext, commentReplyBean, 3);
        recycler_book_comment.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_book_comment.setAdapter(adapter);
    }

    /**
     * 获取章节评论数和评论
     */
    public void getChapterCommentNum() {
        BookApi.getInstance().service.get_chapter_comment_content(book_id, chapter_id, pid, "parent", 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookCommentBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Base<List<BookCommentBean>> listBase) {
                        if (listBase != null) {
                            data.addAll(listBase.getData());
                            commentReplyBean.setLists(data);
                            adapter.notifyDataSetChanged();
                        }
                        Log.e("onNext", "----onNext-------" + listBase.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "----onError-------" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
