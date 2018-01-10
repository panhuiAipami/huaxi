package com.spriteapp.booklibrary.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/1/8.
 */

public class URecyclerView extends RecyclerView {
    private int STARTLOADMOREPOS = 5;//最后第几个显示开始加载数据
    private LoadingListener mLoadingListener;

    public URecyclerView(Context context) {
        this(context, null);
    }

    public URecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public URecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        onScrollRefreshOrLoadMore();//滑动加载
    }

    public void onScrollRefreshOrLoadMore() {
        this.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if ((totalItemCount - 1) - lastVisibleItem < STARTLOADMOREPOS && isSlidingToLast) {
                        //加载更多功能的代码
                        if (mLoadingListener != null) {
                            mLoadingListener.onLoadMore();//加载更多
                        }

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                //大于0表示正在向右滚动
                // 小于等于0表示停止或向左滚动
                isSlidingToLast = dy > 0;
            }
        });
    }

    /**
     * @param lastVisibleItem 设置最后第几个item开始加载数据
     */
    public void setStartLoadMorePos(int lastVisibleItem) {
        STARTLOADMOREPOS = lastVisibleItem;

    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {
        void onLoadMore();
    }
}
