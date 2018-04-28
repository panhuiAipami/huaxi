package com.spriteapp.booklibrary.ui.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.zhouwei.mzbanner.holder.MZViewHolder;

/**
 * Created by panhui on 2017/12/22.
 */

public class BannerViewHolder implements MZViewHolder<String> {
    private ImageView mImageView;


    @Override
    public View createView(Context context) {
        // 返回页面布局
        View view = LayoutInflater.from(context).inflate(R.layout.banner_layout, null);
        mImageView = (ImageView) view.findViewById(R.id.banner_image);
        return view;
    }

    @Override
    public void onBind(Context context, int position, String data) {
        // 数据绑定
        GlideUtils.loadImage(mImageView, data, context);
    }
}
