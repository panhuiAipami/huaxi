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
    private static Bitmap bitmapDay, bitmapNight, defaultbitmap;

    public static Bitmap getThemeDrawable(int theme, int bg_color) {
//        Bitmap bmp = Bitmap.createBitmap(ScreenUtil.getScreenWidth(), ScreenUtil.getScreenHeight(), Bitmap.Config.RGB_565);//原为ARGB_8888改为RGB_565
        switch (theme) {
            case NORMAL:
                if (bitmapDay == null)
                    bitmapDay = Bitmap.createBitmap(ScreenUtil.getScreenWidth(), ScreenUtil.getScreenHeight(), Bitmap.Config.RGB_565);
                bitmapDay.eraseColor(ContextCompat.getColor(AppUtil.getAppContext(), bg_color != 0 ? bg_color : R.color.book_reader_read_day_background));
                return bitmapDay;
            case NIGHT://夜间模式
                if (bitmapNight == null)
                    bitmapNight = Bitmap.createBitmap(ScreenUtil.getScreenWidth(), ScreenUtil.getScreenHeight(), Bitmap.Config.RGB_565);
                bitmapNight.eraseColor(ContextCompat.getColor(AppUtil.getAppContext(), R.color.book_reader_read_night_background));
                return bitmapNight;
            default:
                break;
        }
        if (defaultbitmap == null)
            defaultbitmap = Bitmap.createBitmap(ScreenUtil.getScreenWidth(), ScreenUtil.getScreenHeight(), Bitmap.Config.RGB_565);
        defaultbitmap.eraseColor(ContextCompat.getColor(AppUtil.getAppContext(), R.color.book_reader_read_day_background));
        return defaultbitmap;
    }

    public static void isNull() {
        if (bitmapDay != null) {
            bitmapDay.recycle();
            bitmapDay = null;
        }

        if (bitmapNight != null) {
            bitmapNight.recycle();
            bitmapNight = null;
        }

        if (defaultbitmap != null) {
            defaultbitmap.recycle();
            defaultbitmap = null;
        }

    }
}
