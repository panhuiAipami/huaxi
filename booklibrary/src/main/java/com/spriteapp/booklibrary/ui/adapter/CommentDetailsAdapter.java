package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.CommentReply;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

/**
 * Created by Administrator on 2018/1/11.
 */

public class CommentDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int COMMENTPOS = 0;
    private Activity context;
    private List<CommentReply> list;

    public CommentDetailsAdapter(Activity context, List<CommentReply> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == COMMENTPOS) {//评论
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
            return new CommentViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
            return new CommentViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentViewHolder) {
            CommentReply commentReply = list.get(position);
            if (commentReply == null) return;
            CommentViewHolder commentViewHolder = (CommentDetailsAdapter.CommentViewHolder) holder;
            GlideUtils.loadImage(commentViewHolder.user_head, commentReply.getUser_avatar(), context);
            commentViewHolder.user_name.setText(commentReply.getUsername());
            commentViewHolder.user_speak.setText(commentReply.getContent());
            commentViewHolder.send_time.setText(TimeUtil.getTimeFormatText(Long.parseLong(commentReply.getAddtime()+"000")));
            commentViewHolder.support_num.setText(Util.getFloat(commentReply.getSupportnum()));
        }
    }

    @Override
    public int getItemCount() {//类表长度
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {//定义类型
        return COMMENTPOS;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_head;
        private LinearLayout comment_layout;
        private TextView user_name, user_label, build_num, send_time, support_num;
        private TextView user_speak, reply_comment1, reply_comment2;

        public CommentViewHolder(View itemView) {
            super(itemView);
            user_head = (ImageView) itemView.findViewById(R.id.user_head);
            comment_layout = (LinearLayout) itemView.findViewById(R.id.comment_layout);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_label = (TextView) itemView.findViewById(R.id.user_label);
            build_num = (TextView) itemView.findViewById(R.id.build_num);
            send_time = (TextView) itemView.findViewById(R.id.send_time);
            support_num = (TextView) itemView.findViewById(R.id.support_num);
            user_speak = (TextView) itemView.findViewById(R.id.user_speak);
            reply_comment1 = (TextView) itemView.findViewById(R.id.reply_comment1);
            reply_comment2 = (TextView) itemView.findViewById(R.id.reply_comment2);
        }
    }
}
