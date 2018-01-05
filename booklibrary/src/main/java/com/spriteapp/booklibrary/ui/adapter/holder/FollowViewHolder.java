package com.spriteapp.booklibrary.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.spriteapp.booklibrary.ui.adapter.FollowAdapter;

/**
 * Created by Administrator on 2018/1/3.
 */

public class FollowViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private FollowAdapter adapter;

    public FollowViewHolder(View itemView, Context context, FollowAdapter adapter) {
        super(itemView);
        this.context = context;
        this.adapter = adapter;
    }
}
