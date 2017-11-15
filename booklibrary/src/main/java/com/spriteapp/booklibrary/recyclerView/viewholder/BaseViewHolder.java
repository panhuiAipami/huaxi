package com.spriteapp.booklibrary.recyclerView.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private View mItemView;
    private SparseArray<View> views;

    protected BaseViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        //  初始化存储view的集合
        views = new SparseArray<View>();
    }

    /**
     * 获取当前item的view
     */
    public View getViews(int resId) {
        View view = views.get(resId);
        if (view == null) {
            view = mItemView;
            views.put(resId, mItemView);
        }
        return view;
    }

    /**
     * 绑定ItemView 的数据
     */
    public abstract void bindViewData(T data);
}
