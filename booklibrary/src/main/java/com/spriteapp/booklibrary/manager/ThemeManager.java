package com.spriteapp.booklibrary.manager;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;

/**
 * Created by kuangxiaoguo on 2017/7/17.
 */

public class ThemeManager {

    public static final int NORMAL = 0;
    public static final int NIGHT = 1;

    public static Bitmap getThemeDrawable(int theme) {
        Bitmap bmp = Bitmap.createBitmap(ScreenUtil.getScreenWidth(), ScreenUtil.getScreenHeight(), Bitmap.Config.ARGB_8888);
        switch (theme) {
            case NORMAL:
                bmp.eraseColor(AppUtil.getAppContext().getResources().getColor(R.color.book_reader_read_day_background));
                break;
            case NIGHT:
                bmp.eraseColor(AppUtil.getAppContext().getResources().getColor(R.color.book_reader_read_night_background));
                break;
            default:
                break;
        }
        return bmp;
    }
}
