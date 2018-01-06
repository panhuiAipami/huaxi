package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 */

public class SquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int IMAGE0 = 0;//没有图片
    private final int IMAGE1 = 1;//一张图片
    private final int IMAGE2 = 2;//两张图片
    private final int IMAGE3 = 3;//三张及以上图片
    private Context context;
    private List<String> list;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == IMAGE0) {//左边文字
            convertView = LayoutInflater.from(context).inflate(R.layout.follow_item_layout, parent, false);
            return new SquareViewHolder(convertView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SquareViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE0;
    }

    public class SquareViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_head, more, image1, image2;
        private TextView user_name, send_time, user_speak, read_num, support_num, comment_num;
        private RecyclerView image_recyclerview;
        private LinearLayout image_layout;

        public SquareViewHolder(View itemView) {
            super(itemView);
            //图片
            user_head = (ImageView) itemView.findViewById(R.id.user_head);
            more = (ImageView) itemView.findViewById(R.id.more);
            image1 = (ImageView) itemView.findViewById(R.id.image1);
            image2 = (ImageView) itemView.findViewById(R.id.image2);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            send_time = (TextView) itemView.findViewById(R.id.send_time);
            user_speak = (TextView) itemView.findViewById(R.id.user_speak);
            read_num = (TextView) itemView.findViewById(R.id.read_num);
            support_num = (TextView) itemView.findViewById(R.id.support_num);
            comment_num = (TextView) itemView.findViewById(R.id.comment_num);
            image_recyclerview = (RecyclerView) itemView.findViewById(R.id.image_recyclerview);
            image_layout = (LinearLayout) itemView.findViewById(R.id.image_layout);
        }
    }
}
