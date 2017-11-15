package com.spriteapp.booklibrary.util;

/**
 * 屏幕亮度工具类
 */
public class ScreenUtil {

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        return AppUtil.getAppContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        return AppUtil.getAppContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 将dp转换成px
     */
    public static float dpToPx(float dp) {
        return dp * AppUtil.getAppContext().getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(float dp) {
        return (int) (dpToPx(dp) + 0.5f);
    }

    /**
     * 将px转换成dp
     */
    public static float pxToDp(float px) {
        return px / AppUtil.getAppContext().getResources().getDisplayMetrics().density;
    }

    public static int pxToDpInt(float px) {
        return (int) (pxToDp(px) + 0.5f);
    }

    /**
     * 将px值转换为sp值
     */
    public static float pxToSp(float pxValue) {
        return pxValue / AppUtil.getAppContext().getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 将sp值转换为px值
     */
    public static float spToPx(float spValue) {
        return spValue * AppUtil.getAppContext().getResources().getDisplayMetrics().scaledDensity;
    }

}
