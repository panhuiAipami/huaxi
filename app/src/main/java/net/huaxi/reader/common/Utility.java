package net.huaxi.reader.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.huaxi.reader.R;

public class Utility {

    private static final String reg = "^\\s*([0-9]+\\|)|([【第]\\s*[０-９0-9零一二三四五六七八九十百千万]+\\s*[】书首集卷回章节部]+)";
    private static WindowManager mWindowManager = null;
    private static int SCREEN_WIDTH = 0;
    private static int SCREEN_HEIGHT = 0;
    private static float SCREEN_DENSITY = 0;

    /**
     * 渠道
     */
    public static String getChannel() {
        String channel = "xs";
        try {
            ApplicationInfo appInfo = CommonApp.getInstance().getPackageManager().getApplicationInfo(
                    CommonApp.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * 渠道
     */
    public static String getUmeng_APPKey() {
        String appkey = "xs";
        try {
            ApplicationInfo appInfo = CommonApp.getInstance().getPackageManager().getApplicationInfo(
                    CommonApp.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            appkey = appInfo.metaData.getString("UMENG_APPKEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appkey;
    }

    /**
     * Bugly的应用ID
     *
     * @return buglyid
     */
    public static String getBuglyAppId() {
        return AppContext.getInstance().getString(R.string.appid_bugly);
//        try {
//            ApplicationInfo appInfo = CommonApp.getInstance().getPackageManager().getApplicationInfo(
//            		CommonApp.getInstance().getPackageName(),PackageManager.GET_META_DATA);
//            appid = appInfo.metaData.getString("appid_bugly");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return int
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return int
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public static String getSystemUa(Context context) {
        try {
            return System.getProperty("http.agent");
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return "";

    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 获取屏幕密度
     */

    public static float getDensity() {
        if (SCREEN_DENSITY == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager(CommonApp.context()).getDefaultDisplay().getMetrics(dm);
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
            SCREEN_DENSITY = dm.density;
        }
        return SCREEN_DENSITY;
    }

    /**
     * 屏幕的宽
     */
    public static int getScreenWidth() {
        if (SCREEN_WIDTH == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager(CommonApp.context()).getDefaultDisplay().getMetrics(dm);
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
            SCREEN_DENSITY = dm.density;
        }
        return SCREEN_WIDTH;

    }

    /**
     * 屏幕的高
     */
    public static int getScreenHeight() {
        if (SCREEN_HEIGHT == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager(CommonApp.context()).getDefaultDisplay().getMetrics(dm);
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
            SCREEN_DENSITY = dm.density;
        }
        return SCREEN_HEIGHT;

    }

    public static String getDisplay() {
        int h = getScreenHeight();
        int w = getScreenWidth();
        if (w > h) {
            return h + "x" + w;
        } else {
            return w + "x" + h;
        }
    }

    /**
     * 返回网络请求的Header
     *
     * @param context
     * @param serverVersion 服务端接口版本号.
     * @return
     */
    public static Map<String, String> getNetHeader(Context context, String serverVersion) {
        Map<String, String> header = new HashMap<String, String>();
//        header.put("app-version",PhoneUtils.getVersionCode()+"");
//        header.put("channel", Utility.getChannel());
//        header.put("os-version", PhoneUtils.getSystemRelease());
//        header.put("resolution", getDisplay());
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Accept", String.format(Constants.SERVER_ACCEPT, serverVersion));
        String cookie = SharePrefHelper.getCookie();
        if (!StringUtils.isBlank(cookie) && !cookie.equals("-1")) {
            header.put("userToken", SharePrefHelper.getCookie());
        }
        return header;
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public static String getPath(Activity act, Uri uri) {
        if (TextUtils.isEmpty(uri.getAuthority()) || act == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = act.managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 获取当前用户书籍根目录
     *
     * @return /sdcard/xsreader/book/001/
     */
    public static String getBookRootPath() {
        return Constants.XSREADER_BOOK + UserHelper.getInstance().getUserId() + File.separator;
    }

    public static String getCacheRootPath() {
        return Constants.XSREADER_TEMP + "cache" + File.separator;
    }

    /**
     * 获取章节文件下载的全路径.
     */
    private static String getChapterDownloadFile(String bookId, String chapterId, String fileSuffix) {
        String path = Utility.getBookRootPath() + bookId + File.separator + chapterId + "." + fileSuffix;
        LogUtils.debug("chapterfilename = " + path);
        return path;
    }

    /**
     * 获取章节在线缓存路径
     */
    private static String getCacheRootPath(String bookId, String chapterId, String fileSuffix) {
        String path = getCacheRootPath() + bookId + File.separator + chapterId + "." + fileSuffix;
        LogUtils.debug("chapterfilename = " + path);
        return path;
    }

    /**
     * 获取存在章节本地缓存的路径。
     *
     * @param bookId
     * @param chapterId
     * @param fileSuffix
     * @return
     */
    public static String getChapterFilePath(String bookId, String chapterId, String fileSuffix) {
        String path = getChapterDownloadFile(bookId, chapterId, fileSuffix);
        if (new File(path).exists()) {
            return path;
        } else {
            return getCacheRootPath(bookId, chapterId, fileSuffix);
        }
    }

    /**
     * 获取字体高度
     */
    public static int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    public static int getFontDescent(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.abs(fm.descent) + 1;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String formatScene(String scene) {
        double value = 0;
        if (StringUtils.isNotEmpty(scene)) {
            value = Double.valueOf(scene);
        }
        DecimalFormat df = new DecimalFormat("######0.0");
        return  df.format(value);
    }

    public static String formatOnePoint(double value) {
        DecimalFormat df = new DecimalFormat("######0");
        return df.format(value);
    }

    /**
     * 判断是否是标题
     *
     * @remark 用于排版时处理一行文字是否是标题
     */
    public static boolean isTitle(String s) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(s);
        if (s.length() < 30 && matcher.find()) {
            return true;
        }
        return false;
    }

    public static <V> ArrayList<V> randomList(ArrayList<V> sorceList) {

        if (sorceList == null || sorceList.isEmpty()) {
            return sorceList;
        }
        ArrayList<V> randomList = new ArrayList<>(sorceList.size());
        do {
            int randomIndex = Math.abs(new Random().nextInt(sorceList.size()));
            randomList.add(sorceList.remove(randomIndex));
        } while (sorceList.size() > 0);

        return randomList;
    }

}
