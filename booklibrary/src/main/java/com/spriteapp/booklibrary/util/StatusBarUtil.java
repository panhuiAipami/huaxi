package com.spriteapp.booklibrary.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.spriteapp.booklibrary.manager.SystemBarTintManager;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;

/**
 * Created by kuangxiaoguo on 2017/8/3.
 */

public class StatusBarUtil {

    public static void setWindowStatusBarColor(Activity activity) {
        int color = 0;
        //这些Activity需要沉浸式
        if (activity instanceof ReadActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//4.0以上,6.0以下
                color = Color.TRANSPARENT;//透明状态栏
                setTranslucentStatus(activity,true);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(color);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0,透明状态栏
                color = Color.TRANSPARENT;//透明状态栏
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                activity.getWindow().setStatusBarColor(color);
            }
        } else {//不需要沉浸式
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//4.0以上,6.0以下
//                setTranslucentStatus(activity,true);
//                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//                tintManager.setStatusBarTintEnabled(true);
//                //4.4~5.0白色字体，灰色背景
//                color = R.color.seven_white;//灰色状态栏
//                tintManager.setStatusBarTintResource(color);// 状态栏栏所需颜色Color.TRANSPARENT
//
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0灰色字体，白色背景
//                if (android.os.Build.MANUFACTURER.equals(StatusBarSet.XIAOMI)) {
//                    StatusBarSet.setMiuiStatusBarDarkMode(activity, true);
//                } else if (android.os.Build.MANUFACTURER.equals(StatusBarSet.MEIZU)) {
//                    StatusBarSet.setMeizuStatusBarDarkIcon(activity, true);
//                }
//                activity.getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.c13_themes_color));
//            }
        }
    }

    private static void setTranslucentStatus(Activity a,boolean on) {
        Window win = a.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
