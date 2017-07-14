package net.huaxi.reader.util.listener;

import android.app.Activity;
import android.graphics.Color;
import android.view.WindowManager;

import com.tools.commonlibs.activity.BaseActivity;

/**
 * Created by Saud on 16/1/4.
 */
public class ScreenUtils {

    public static void fullScreen(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(lp);
    }


    public static void full(Activity activity, boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

            activity.getWindow().setAttributes(lp);
            ((BaseActivity) activity).tintManager.setStatusBarTintEnabled(false);

        } else {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attr);
            ((BaseActivity) activity).tintManager.setStatusBarAlpha(1f);
            ((BaseActivity) activity).tintManager.setStatusBarTintEnabled(true);
            ((BaseActivity) activity).tintManager.setStatusBarTintColor(Color.parseColor("#EE000000"));
        }
    }

}
