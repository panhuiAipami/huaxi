package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.TaskBean;

/**
 * Created by userfirst on 2018/3/13.
 */

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private TaskBean taskBean;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == 0) {//任务标题
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new TaskAdapter.TitleViewHolder(convertView);
        } else {//任务详情
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new TaskAdapter.TaskViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return taskBean == null ? 0 : 0;
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {

        public TitleViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder {

        public TaskViewHolder(View itemView) {
            super(itemView);
        }
    }
}
