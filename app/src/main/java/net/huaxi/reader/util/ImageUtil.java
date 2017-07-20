package net.huaxi.reader.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.common.AppContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ZMW on 2016/4/22.
 */
public class ImageUtil {

    public static void getImageListener(final Activity context, final String url, final BitmapCallBackListener callBackListener1) {
        LogUtils.debug("ImgarUtil:"+url);
        if (!checkContext(context) || !checkUrl(url)) {
            return;
        }

        EasyTask.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = Glide.with(context).load(url).asBitmap().into(300, 300).get(5, TimeUnit.SECONDS);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBackListener1.callBack(bitmap);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void loadImage(Context context, String url, ImageView view, int default_imgid) {
            Glide.with(AppContext.context()).load(url).dontAnimate().crossFade(500).error(default_imgid).into(view);
    }

    public static void loadImageNotFade(Context context, String url, ImageView view, int default_imgid) {
        Glide.with(AppContext.context()).load(url).error(default_imgid).into(view);
    }

    public interface BitmapCallBackListener {
        void callBack(Bitmap bitmap);
    }

    private static boolean checkContext(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing()) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkUrl(String url) {
        return StringUtils.isNotBlank(url);
    }
}
