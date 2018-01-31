package com.spriteapp.booklibrary.callback;

import android.content.Context;

import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.loader.ImageLoaderInterface;

/**
 * Created by panhui on 2017/10/21.
 */

public abstract class RoundImageLoader implements ImageLoaderInterface<RoundedImageView> {

    @Override
    public RoundedImageView createImageView(Context context) {
        RoundedImageView imageView = new RoundedImageView(context);
        return imageView;
    }

}
