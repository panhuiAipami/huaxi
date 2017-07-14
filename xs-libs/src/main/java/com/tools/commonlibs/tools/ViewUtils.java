package com.tools.commonlibs.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.dialog.LoadingDialog;

public class ViewUtils {


    public static void toastShort(String msg) {
        Context context = CommonApp.getInstance();
        toastDialog(context, msg, Toast.LENGTH_SHORT);
    }


    public static void toastLong(String msg) {
        Context context = CommonApp.getInstance();
        toastDialog(context, msg, Toast.LENGTH_LONG);
    }


    private static Toast toast;

    public static void toastDialog(Context ctx, String msg, int duration) {
        if (msg != null && !msg.equalsIgnoreCase("")) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(ctx, msg, duration);
            toast.show();
        }
    }

    /**
     * 应用应用显示区域
     * <p/>
     * 注: 注意显示时机onWindowFocusChanged
     *
     * @param act
     * @return
     */
    public static Rect getAppRect(Activity act) {
        Rect rc = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rc);

        return rc;
    }

    /**
     * 创建背景透明的Dialog;
     *
     * @param context
     * @return
     */
    public static Dialog showProgressDialog(Context context) {
        return showProgressDialog(context, null);
    }

    /**
     * 创建背景透明的Dialog;
     *
     * @param context
     * @param text
     * @return
     */
    public static Dialog showProgressDialog(Context context, String text) {
        Dialog dialog = new LoadingDialog(context, text);
        dialog.show();
        return dialog;
    }

    /**
     * 创建背景暗淡的Dialog;
     *
     * @param context
     * @return
     */
    public static Dialog showProgressDimDialog(Context context) {
        return showProgressDimDialog(context, null);
    }

    /**
     * 创建背景暗淡的Dialog;
     *
     * @param context
     * @param text
     * @return
     */
    public static Dialog showProgressDimDialog(Context context, String text) {
        Dialog dialog = new LoadingDialog(context, text, true, false);
        dialog.show();
        return dialog;
    }


    /**
     * view的显示和隐藏方法，在5.0版本有圆形动画
     *
     * @param view
     * @param visibility
     */
    public static void setViewVisbility(final View view, final int visibility) {
            view.setVisibility(visibility);
    }

}
