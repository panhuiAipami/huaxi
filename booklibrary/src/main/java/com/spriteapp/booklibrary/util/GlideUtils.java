package com.spriteapp.booklibrary.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.spriteapp.booklibrary.constant.Constant;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/27.
 */

public class GlideUtils {
    private boolean flag = false;
    public static final String IMGWIDTH = "width";
    public static final String IMGHEIGHT = "height";

    public static void loadImage(ImageView imageView, String url, Context context) {
        if (url == null) return;
        if (!url.startsWith("http")) url = Constant.IMG_URL + url;
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, int url, Context context) {
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

    public static void loadAndGetImage(final ImageView view, String url, final List<String> squareBean, final int pos, final Activity context) {
        final Map<String, Integer> sizeMap = new HashMap<>();
        if (url == null) return;
        if (!url.startsWith("http")) url = Constant.IMG_URL + url;
        //获取图片真正的宽高
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions() {
                }).asBitmap().load(url).listener(new RequestListener<Bitmap>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                sizeMap.put(IMGWIDTH, resource.getWidth());
                sizeMap.put(IMGHEIGHT, resource.getHeight());
                Util.ImageClick(view, squareBean, sizeMap, pos, context);
//                int height = Util.px2dip(context, resource.getHeight());
//                if (height < 400) {
//                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                    layoutParams.height = height;
//                    view.setLayoutParams(layoutParams);
//                }
                return false;
            }
        }).thumbnail(0.1f).into(view);
        return;
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
