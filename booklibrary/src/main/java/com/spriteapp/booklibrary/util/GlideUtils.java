package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Administrator on 2017/12/27.
 */

public class GlideUtils {
    public static void loadImage(ImageView imageView, String url, Context context) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .into(imageView);
    }
    public static Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (drawable == null) {
            new Throwable(new NullPointerException("drawable is null"));
        }
        return drawable;
    }
}
