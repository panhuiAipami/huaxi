package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * 九宫格图片适配器
 */
public class PhotoSelectedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MAX_COUNTS = 9;
    private Activity context;
    private List<LocalMedia> list = new ArrayList<>();

    public PhotoSelectedListAdapter(Activity context) {
        this.context = context;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        convertView = LayoutInflater.from(context).inflate(R.layout.add_photo_item, parent, false);
        return new MyViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            if (list.size() < 9 && position == list.size()) {
                //最后一个是添加
                myViewHolder.delete_img.setVisibility(View.GONE);
                myViewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                myViewHolder.imageView.setImageResource(R.mipmap.add_img);
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PictureSelector.create(context)
                                .openGallery(PictureMimeType.ofImage())
                                .selectionMedia(list)
                                .compress(true)
                                .isGif(true)
                                .maxSelectNum(MAX_COUNTS)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                    }
                });
            } else {
                myViewHolder.delete_img.setVisibility(View.VISIBLE);
                myViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideUtils.loadLocalImage(context, myViewHolder.imageView, list.get(position).getCompressPath());
                //点击单个预览
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PictureSelector.create(context).externalPicturePreview(position, list);
                    }
                });
            }


            //删除按钮
            myViewHolder.delete_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(0, list.size());


                }
            });
        }
    }

    @Override
    public int getItemCount() {
//        if (list != null && list.size() == 0) return 0;
        return list == null ? 0 : list.size() < MAX_COUNTS ? list.size() + 1 : MAX_COUNTS;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView delete_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            delete_img = (ImageView) itemView.findViewById(R.id.delete_img);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.height = (BaseActivity.deviceWidth - Util.dp2px(context, 30)) / 3;
            imageView.setLayoutParams(layoutParams);
        }
    }
}
