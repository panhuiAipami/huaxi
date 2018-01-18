package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.spriteapp.booklibrary.constant.Constant;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Administrator on 2017/12/27.
 */

public class GlideUtils {
    public static void loadImage(ImageView imageView, String url, Context context) {
        if (url == null) return;
        if (!url.startsWith("http")) url = Constant.IMG_URL + url;
        Glide.with(context)
                .load(url)
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

    /**
     * 加载本地图片
     *
     * @param v
     * @param url
     */
    public static void loadLocalImage(Context context, final ImageView v, String url) {
        if (TextUtils.isEmpty(url))
            return;
        Glide.with(context).load(url).into(v);

    }
}
