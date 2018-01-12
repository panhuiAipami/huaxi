package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class SquareImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int IMGPOS = 0;
    private Activity context;
    private List<String> list;

    public SquareImageAdapter(Activity context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == IMGPOS) {//左边文字
            convertView = LayoutInflater.from(context).inflate(R.layout.square_image_item, parent, false);
            return new ImageViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.square_image_item, parent, false);
            return new ImageViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            final ImageViewHolder viewHolder = (ImageViewHolder) holder;
            if (list.get(position).isEmpty()) return;
            int height;
            if (list.size() == 4) {
                height = (BaseActivity.deviceWidth - (Util.dp2px(context, 32))) / 2;
            } else {
                height = (BaseActivity.deviceWidth - (Util.dp2px(context, 38))) / 3;
            }
            ViewGroup.LayoutParams layoutParams = viewHolder.imageView.getLayoutParams();
            layoutParams.height = height;
            viewHolder.imageView.setLayoutParams(layoutParams);
            GlideUtils.loadImage(viewHolder.imageView, list.get(position), context);
            Util.ImageClick(viewHolder.imageView, list, position, context);//查看大图

        }

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return IMGPOS;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.square_item_image);
        }
    }
}
