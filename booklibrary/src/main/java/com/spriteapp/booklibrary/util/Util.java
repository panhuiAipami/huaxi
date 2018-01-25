package com.spriteapp.booklibrary.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.store.AppUpDateModel;
import com.spriteapp.booklibrary.ui.dialog.AppUpdateDialog;
import com.spriteapp.booklibrary.widget.photoview.PictureSelector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.spriteapp.booklibrary.util.GlideUtils.IMGHEIGHT;
import static com.spriteapp.booklibrary.util.GlideUtils.IMGWIDTH;

/**
 * 工具类
 */
public class Util {

    private static final double EARTH_RADIUS = 6378137;
    private static long lastClickTime = 0;


    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }


    /**
     * dip转像素
     *
     * @param context
     * @param dip
     * @return
     */
    public static int formatDipToPx(Context context, int dip) {
        DisplayMetrics dm = new DisplayMetrics();
        ((BaseActivity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int dip1 = (int) Math.ceil(dip * dm.density);
        return dip1;
    }

    /**
     * px 转 Dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int formatPxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * md5转换
     *
     * @param plainText
     * @return
     */
    public static String Md5(String plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 获取应用的版本号
     *
     * @return
     */
    public static int getVersionCode() {
        int code = 0;
        try {
            PackageInfo info = AppUtil.getAppContext().getPackageManager().getPackageInfo(AppUtil.getAppContext().getPackageName(), 0);
            code = info.versionCode;
            return code;
        } catch (Exception e) {
        }
        return code;

    }

    /**
     * 获取应用的版本名字
     *
     * @return
     */
    public static String getVersionName() {
        String name = null;
        try {
            PackageInfo info = AppUtil.getAppContext().getPackageManager().getPackageInfo(AppUtil.getAppContext().getPackageName(), 0);
            name = info.versionName;
            return name;
        } catch (Exception e) {
        }
        return name;

    }

    /**
     * 是否是手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhone(String phoneNumber) {
        Pattern p = Pattern.compile("^[1][2-9]\\d{9}");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    public static boolean isPhoneTwo(String phoneNumber) {
        Pattern p = Pattern
                .compile("^((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        Pattern p = Pattern
                .compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 匹配是否是中文
     *
     * @return
     */
    public static boolean isChinese(String chinese) {
        Pattern p = Pattern.compile("[\u4E00-\u9FFF]{2,10}");
        Matcher m = p.matcher(chinese);
        return m.matches();
    }

    /**
     * 是否是空
     *
     * @param str
     * @return
     */
    public static Boolean isNull(String str) {
        String regStartSpace = "^[　 ]*";
        String regEndSpace = "[　 ]*$";
        String strDelSpace = str.replaceAll(regStartSpace, "").replaceAll(
                regEndSpace, "");
        if (strDelSpace.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 匹配分行信息 1-10 汉字
     *
     * @param str
     * @return
     */
    public static Boolean isBranchBank(String str) {
        Pattern p = Pattern.compile("[\u4E00-\u9FA5]{1,10}");
        Matcher m = p.matcher(str);
        return m.matches();

    }

    public static boolean isQQ(String QQNumber) {
        Pattern p = Pattern.compile("^[1-9]\\d{4,9}$");
        Matcher m = p.matcher(QQNumber);
        return m.matches();
    }

    public static boolean isPassword(String password) {
        Pattern p = Pattern.compile("[0-9]{6,10}");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private static final String ID_CARD_PATTERN = "[0-9]{6}(1[9]|2[0])\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\\d{3}(\\d|\\w)";

    public static boolean isIDCARD(String ID) {
        Pattern p = Pattern.compile(ID_CARD_PATTERN);
        Matcher m = p.matcher(ID);
        return m.matches();
    }

    public static String getDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        Date dd = new Date();
        return ft.format(dd);
    }

    public static long getQuot(String time1, String time2) {
        long quot = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date1 = ft.parse(time1);
            Date date2 = ft.parse(time2);
            quot = date1.getTime() - date2.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot;
    }

    /**
     * 图片加载优化
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeInitialSampleSize(BitmapFactory.Options options,
                                               int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 微信唯一标识
     *
     * @param type
     * @return
     */
    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 图片转为灰色
     *
     * @param bmSrc
     * @return
     */
    public static Bitmap bitmap2Gray(Bitmap bmSrc) {
        int width, height;
        height = bmSrc.getHeight();
        width = bmSrc.getWidth();
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);

        return bmpGray;
    }

    /**
     * 去除所有空格制表符 换行
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 圆角矩形
     *
     * @param image 源图片
     *              圆角的大小
     * @return 圆角图片
     */
    public static Bitmap createFramedPhoto(Bitmap image, int rat) {
        int x = image.getWidth();
        int y = image.getHeight();
        float outerRadiusRat;
        if (rat == 0)
            outerRadiusRat = 20;
        else
            outerRadiusRat = rat;

        // 根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(x, y, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, x, y);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, x, y);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    /***
     * 计算百分比
     *
     * @param y
     * @param z
     * @return
     */
    public static double myPercent(double y, double z) {
        String baifenbi = "";// 接受百分比的值
        String baifenbiTemp = "";// 接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy / baiz;
        NumberFormat nf = NumberFormat.getPercentInstance(); // 注释掉的也是一种方法
        nf.setMinimumFractionDigits(2); // 保留到小数点后几位
        DecimalFormat df1 = new DecimalFormat("##%"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        baifenbiTemp = nf.format(fen);

        baifenbi = df1.format(fen);
        double temp = 0;
        try {
            temp = Double.parseDouble(baifenbi.replace("%", "").trim());
        } catch (Exception e) {
            // TODO: handle exception
            temp = 0;
        }
        return temp;
    }

    /***
     * 小数点后两位
     * @return
     */
    public static String myPercentDecimal(double d) {
        DecimalFormat df1 = new DecimalFormat("0.00"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        String result = df1.format(d);

        return result;
    }

    /***
     * 计算百分比
     *
     * @param y
     * @param z
     * @return
     */
    public static String myPercentDecimalTwo(double y, double z) {
        String baifenbi = "";// 接受百分比的值
        String baifenbiTemp = "";// 接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy / baiz;
        NumberFormat nf = NumberFormat.getPercentInstance(); // 注释掉的也是一种方法
        nf.setMinimumFractionDigits(2); // 保留到小数点后几位
        DecimalFormat df1 = new DecimalFormat("##%"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        baifenbiTemp = nf.format(fen);

        return baifenbiTemp;
    }

    public static BitmapFactory.Options options;

    /**
     * 计算剩余时间 小时天
     *
     * @param time
     * @return
     */
    public static String getQuot(long time) {
        long dd = 0;
        long hh = 0;
        long mm = 0;
        long ss = 0;
        dd = time / 1000 / (60 * 60 * 24);
        hh = time / 1000 / (60 * 60);
        mm = time / 1000 / 60;
        ss = time / 1000;
        if (dd != 0) {
            return dd + "天";
        } else if (hh != 0) {
            return hh + "小时";
        } else if (mm != 0) {
            return mm + "分钟";
        } else if (ss != 0) {
            return 1 + "分钟";
        }
        String strTime = dd + "天" + hh + "时" + mm + "分" + ss + "秒";
        return strTime;

    }

    /**
     * 计算剩余时间 小时
     *
     * @param time
     * @return
     */
    public static String getHour(long time) {
        long dd = 0;
        long hh = 0;
        long mm = 0;
        long ss = 0;
        // dd = time / 1000 / (60 * 60 * 24);
        hh = time / 1000 / (60 * 60);
        mm = time / 1000 / 60;
        ss = time / 1000;
        if (dd != 0) {
            return dd + "天";
        } else if (hh != 0) {
            return hh + "小时";
        } else if (mm != 0) {
            return mm + "分钟";
        } else if (ss != 0) {
            return 1 + "分钟";
        }
        String strTime = dd + "天" + hh + "时" + mm + "分" + ss + "秒";
        return strTime;

    }

    // 四舍五入
    public static int StringToRounding(String value) {
        // BigDecimal temp = null;
        // try {
        // temp = new BigDecimal(value + "").setScale(0,
        // BigDecimal.ROUND_HALF_UP);
        // } catch (Exception e) {
        // temp = temp = new BigDecimal("1").setScale(0,
        // BigDecimal.ROUND_HALF_UP);
        // }

        Double temp1 = 0.0;
        int temp2 = 0;
        try {
            temp1 = Double.parseDouble(value);
            temp2 = (int) Math.ceil(temp1);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return temp2;
    }

    /**
     * 加0
     *
     * @param temp
     * @return
     */
    public static String addZeros(int temp) {
        // TODO Auto-generated method stub
        if (temp <= 0) {
            return "00";
        }
        if (temp < 10) {
            return "0" + temp;
        }
        return temp + "";
    }

    /**
     * 生成一个15位的随机数订单号
     *
     * @return
     */
    public static String creatRequestId() {
        // TODO Auto-generated method stub
        String id = System.currentTimeMillis() + "";
        if (id.equals("") || id.length() < 12)
            id = "88888888888";

        if (id.length() > 15) {
            id = id.substring(0, 15);
        } else if (id.length() < 15) {
            int tmep = 15 - id.length();
            id = id + getRandomCodeStr(tmep);
        }
        return id;
    }

    /**
     * 随机四位
     *
     * @param length
     * @return
     */
    public static String getRandomCodeStr(Integer length) {
        Set<Integer> set = getRandomNumber(length);
        // 使用迭代器
        Iterator<Integer> iterator = set.iterator();
        // 临时记录数据
        String temp = "";
        while (iterator.hasNext()) {
            temp += iterator.next();
        }
        return temp;
    }

    /**
     * 获取一个四位随机数，并且四位数不重复
     *
     * @return Set<Integer>
     */
    private static Set<Integer> getRandomNumber(Integer length) {
        // 使用SET以此保证写入的数据不重复
        Set<Integer> set = new HashSet<Integer>();
        // 随机数
        Random random = new Random();

        while (set.size() < length) {
            // nextInt返回一个伪随机数，它是取自此随机数生成器序列的、在 0（包括）
            // 和指定值（不包括）之间均匀分布的 int 值。
            set.add(random.nextInt(10));
        }
        return set;
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return
     */
    public static boolean getNetConnectState(Context context) {
        boolean netConnect = false;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info.isConnected() || infoM.isConnected()) {
            netConnect = true;
        }
        return netConnect;
    }

    /**
     * 判断是否WIFI状态
     *
     * @param context
     * @return
     */
    public static boolean getNetWorkIsWIFI(Context context) {
        boolean netConnect = false;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo infoM = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info.isConnected()) {
            netConnect = true;
        }
        return netConnect;
    }

    /**
     * 去零计算
     *
     * @param temp1
     * @return
     */
    public static String stringProcessing(String temp1) {
        // TODO Auto-generated method stub
        if (temp1.trim().equals("") || temp1.trim().equals("0.0")
                || Double.parseDouble(temp1.trim()) == 0) {
            return "0.00";
        } else {
            double amountCount;
            try {
                amountCount = Double.parseDouble(temp1);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                amountCount = 0;
            }
            String temp = new DecimalFormat(".00")
                    .format(amountCount);
            if (temp.equals("0") || temp.equals("0.0") || temp.equals("0.00")
                    || temp.equals(".00") || temp.equals(".0")) {
                temp = "";
            }
            if (temp.indexOf(".") == 0) {
                temp = temp.replace(".", "0.");
            }
            return temp;
        }
    }

    public static double add(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.add(b2).doubleValue();

    }

    public static double sub(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.subtract(b2).doubleValue();

    }

    public static double mul(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.multiply(b2).doubleValue();

    }

    private static final int DEF_DIV_SCALE = 10;

    public static double div(double d1, double d2) {

        return div(d1, d2, DEF_DIV_SCALE);

    }

    public static double div(double d1, double d2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }


    /**
     * 判断字符串是否超长
     *
     * @param result 字符串的长度
     * @param max    最大长度
     * @return
     */
    public static synchronized boolean strIsBeyond(String result, int max) {
        if (!TextUtils.isEmpty(result)) {
            int length = result.length();
            float is_beyond = 0;
            char[] cc = new char[1];
            int strLength = 0;
            String str = null;
            char result_item;
            for (int i = 0; i < length; i++) {
                result_item = result.charAt(i);
                cc[0] = result_item;
                str = new String(cc);
                strLength = str.getBytes().length;
                switch (strLength) {
                    case 3: {// 一个中文
                        is_beyond += 2;
                        if (is_beyond > max) {
                            return false;
                        }
                        break;
                    }
                    case 2: {// 一个字母
                        is_beyond += 1;
                        if (is_beyond > max) {
                            return false;
                        }
                        break;
                    }
                    case 1: {// 一个符号 或者 数字
                        is_beyond += 1;
                        if (is_beyond > max) {
                            return false;
                        }
                        break;
                    }

                    default:
                        break;
                }
            }
        }
        return true;
    }

    private static Toast sToast = null;

    public static void setToast(Toast toast) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
    }

    public static void cancelToast(Toast toast) {
        if (toast != null)
            toast.cancel();
        toast = null;
    }

    /**
     * 圆角处理
     *
     * @param bitmap
     * @param roundPX
     * @return
     */
    public static Bitmap getRCB(Bitmap bitmap, float roundPX) // RCB means
    // Rounded
    // Corner Bitmap
    {
        Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(dstbmp);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return dstbmp;
    }

    /**
     * 根据月日计算星座
     *
     * @param month
     * @param day
     * @return
     */
    public static String getAstro(int month, int day) {
        String[] astro = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
                "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"};
        int[] arr = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};// 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < arr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return astro[index];
    }

    /**
     * 防重复点击
     *
     * @return true 表示两次点击时间小于500毫秒 false表示两次点击时间大于500毫秒
     */
    public synchronized static boolean isFastClick() {
        return isFastClick(200);
    }


    /**
     * 防重复点击
     *
     * @return true 表示两次点击时间小于500毫秒 false表示两次点击时间大于500毫秒
     */
    public synchronized static boolean isFastClick(int t) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < t) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * Bitmap转byte
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap == null) {// quality
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);// 压缩位图
        return baos.toByteArray();// 创建分配字节数组
    }

    /**
     * 从字节数组解码位图
     *
     * @param data
     * @return
     */
    public static Bitmap getBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static List<Size> getResolutionList(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }

    public static class ResolutionComparator implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            if (lhs.height != rhs.height)
                return lhs.height - rhs.height;
            else
                return lhs.width - rhs.width;
        }

    }

    /**
     * 时间对比
     *
     * @param startDate 现在的时间
     * @param showTime  显示今天时间
     * @return
     */
    public static String twoDateDistance(Date startDate, String showTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        Date endDate = null;
        String nowTime = formatter.format(currentTime);
        try {
            endDate = formatter.parse(nowTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String str = null;
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        // if (timeLong<60*1000) {
        // if(timeLong/1000 < 60){
        // str = "刚刚";
        // }
        // return str;
        // }
        // else if (timeLong<60*60*1000){
        // timeLong = timeLong/1000 /60;
        // if(timeLong < 60 ){
        // str = timeLong+"分钟之前";
        // }
        // return str;
        // }
        if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            if (timeLong < 24) {
                str = showTime;
            }
            return str;
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            if (timeLong >= 1 && timeLong < 2) {
                str = "昨天" + showTime;
            } else if (timeLong >= 2 && timeLong < 3) {
                str = "前天" + showTime;
            } else if (timeLong >= 3) {
                str = formatter.format(startDate);
            }
            return str;
        }
        // else if (timeLong<60*60*24*1000*7*4){
        // timeLong = timeLong/1000/ 60 / 60 / 24/7;
        // return timeLong + "周前";
        // }
        // else {
        // Date currentTime = new Date();
        // return formatter.format(currentTime);
        // }
        return str;

    }

    /**
     * 设置静态地图地址
     *
     * @param longitude
     * @param latitude
     * @return
     */
    public static String getLocationUrl(double longitude, double latitude) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://api.map.baidu.com/staticimage?");
        sb.append("width=300&height=200&center=");
        sb.append(longitude);
        sb.append(",");
        sb.append(latitude);
        sb.append("&zoom=15&markers=");
        sb.append(longitude);
        sb.append(",");
        sb.append(latitude);
        sb.append("&markerStyles=-1,");
        sb.append("http://beimei.zhaofangla.cn/static/de_tencent.png,-1,0,0");
        return sb.toString();
    }

    /**
     * 判断应用是否在前台
     *
     * @param context
     * @param
     * @return
     */
    public static boolean isAppOnBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    // 后台
                    return true;
                } else {// 前台
                    return false;
                }
            }
        }
        return false;
    }

    public static SpannableString getsp(String str) {
        // 创建一个 SpannableString对象
        SpannableString sp = new SpannableString(str);
        // 设置超链接
        // sp.setSpan(new URLSpan("http://www.baidu.com"), 5, 7,
        // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置高亮样式一
        sp.setSpan(new ForegroundColorSpan(Color.RED), 2, str.length() - 4,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置高亮样式二
        // sp.setSpan(new
        // ForegroundColorSpan(Color.YELLOW),20,24,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        // 设置斜体
        // sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 27,
        // 29, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return sp;

    }

    /**
     * 根据int获取drawable
     *
     * @param i
     * @return
     */
    public static Drawable getDrawable(int i) {
        return AppUtil.getAppContext().getResources().getDrawable(i);

    }

    public static String getNowDate() {
        Date d = new Date();//获取时间
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  EEEE");//转换格式
        return sdf.format(d);
    }

    public synchronized static String getid() {
        String id = null;
        TelephonyManager TelephonyMgr = (TelephonyManager) AppUtil.getAppContext().getSystemService(TELEPHONY_SERVICE);
        if (TelephonyMgr.getDeviceId() != null)
            id = TelephonyMgr.getDeviceId();
        else {
            if (TextUtils.isEmpty(PreferenceHelper.getString("imei", ""))) {
                id = Settings.Secure.getString(AppUtil.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                PreferenceHelper.putString("imei", id);
            } else {
                id = PreferenceHelper.getString("imei", "");
            }
        }
        return id;

    }

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static UUID uuid;

    public synchronized static UUID getUUid() {
        if (uuid == null) {
            final SharedPreferences prefs = AppUtil.getAppContext().getSharedPreferences(PREFS_FILE, 0);
            final String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (id != null) {
                // Use the ids previously computed and stored in the prefs file
                uuid = UUID.fromString(id);
            } else {
                final String androidId = Settings.Secure.getString(AppUtil.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                // Use the Android ID unless it's broken, in which case fallback on deviceId,
                // unless it's not available, then fallback on a random number which we store
                // to a prefs file
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } else {
                        final String deviceId = ((TelephonyManager) AppUtil.getAppContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                        uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                // Write the value out to the prefs file
                prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
            }
        }
        return uuid;
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
//        deleteDir(new File(Constants.SDPath_cache));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件大小
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static String getBookStatus(int status) {
        String text = null;
        switch (status) {
            case 1:
                text = "连载中";
                break;
            case 2:
                text = "已完结";
                break;
            case 10:
                text = "停止上架";
                break;
            case 11:
                text = "后台录入中";
                break;
            case 12:
                text = "已删除";
                break;
            case 21:
                text = "用户录入中";
                break;
            case 22:
                text = "审核中";
                break;
            default:
                text = "连载中";
                break;
        }
        return text;
    }

    //此方法，如果显示则隐藏，如果隐藏则显示
    public static void hintKbOne() {
        InputMethodManager imm = (InputMethodManager) AppUtil.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    public static void hintKbTwo(Activity act) {
        InputMethodManager imm = (InputMethodManager) AppUtil.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(act.getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }


    /**
     * 获取application中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx) {
        if (ctx == null) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }


    /***
     * 版本更新dialog
     * @param actionName
     * @param appUpDateModel
     */
    public static void chechForUpdata(String actionName, AppUpDateModel appUpDateModel, final Activity a, boolean isHint) {
        if (actionName.equals("checkForUpdates")) {
            try {
                String vn = appUpDateModel.getVersion();
                String desc = appUpDateModel.getContent();
                int versionName;
                if (vn.contains(".")) {
                    versionName = Integer.parseInt(vn.replace(".", ""));
                } else {
                    versionName = Integer.parseInt(vn);
                }
//                String versionName1 = Util.getVersionName();
//                Log.d("version", versionName1);
                int localVersion = Integer.parseInt(Util.getVersionName().replace(".", "").replace("-debug", ""));
                Log.d("daxiao", "versionName===" + versionName + "localVersion===" + localVersion);
                if (versionName > localVersion) {

                    final String url = appUpDateModel.getUrl();

                    AppUpdateDialog dialog = new AppUpdateDialog(a, desc, url);
                    dialog.show();

                } else {
                    if (isHint)
                        ToastUtil.showSingleToast("当前已是最新版本");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 小数四舍五入
     *
     * @param f
     * @return
     */
    public static int ROUND_HALF_UP(float f) {
        BigDecimal b = new BigDecimal(f);
        int i = b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        return i;
    }


    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9

------------------------------------------------
13(老)号段：130、131、132、133、134、135、136、137、138、139
14(新)号段：145、147
15(新)号段：150、151、152、153、154、155、156、157、158、159
17(新)号段：170、171、173、175、176、177、178
18(3G)号段：180、181、182、183、184、185、186、187、188、189
    */
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static String numberAddK(int num) {
        if (num > 1000) {
            return (num / 1000) + "k";
        } else return num + "";
    }

    public static AnimationDrawable drawable = null;

    /**
     * convert dp to its equivalent px
     * <p>
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {//修改tablayout的下划线长度
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String getFloat(int num) {//评论点赞超过一千加k
        if (num >= 10000) {
            float sum = (((float) num) / 10000);
            DecimalFormat decimalFormat = new DecimalFormat(".0");
            return decimalFormat.format(sum) + "万";
        }
        return num + "";
    }

    /**
     * 截断输出日志
     *
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            Log.e(tag, msg);
        } else {
            while (msg.length() > segmentSize) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, msg);// 打印剩余日志
        }
    }

    public static void setTextStrColor(TextView textView, String numContent, String speak) {
        if (textView != null && numContent != null && !numContent.isEmpty() && speak != null && !speak.isEmpty()) {
            int num = numContent.length();
            int speakNum = speak.length();
            if (num <= speakNum) return;
            int startPos = num - speakNum;
            SpannableString spannableString = new SpannableString(numContent);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8083a0")), startPos, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        }

    }

    public static List<LocalMedia> getPreViewPath(List<String> pic_url,Map<String,Integer> map) {
        List<LocalMedia> medias = new ArrayList<>();
        for (int i = 0; i < pic_url.size(); i++) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(pic_url.get(i));
            if (PictureMimeType.isImageGif(pic_url.get(i))) {
                localMedia.setCompressed(false);
                localMedia.setPictureType("image/gif");
            }
            if(map!=null&&map.size()!=0){
                localMedia.setWidth(map.get(IMGWIDTH));
                localMedia.setHeight(map.get(IMGHEIGHT));
                Log.d("localMedia", "设置宽高"+"宽==="+map.get(IMGWIDTH)+"高==="+map.get(IMGHEIGHT));
            }
            medias.add(localMedia);
        }
        return medias;
    }

    /**
     * 图片放大
     *
     * @param
     */
    public static void ImageClick(View v, final List<String> url, final Map<String,Integer> map, final int position, final Activity context) {
        if (url == null) return;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(context).externalPicturePreview(position, getPreViewPath(url,map));
            }
        });
    }

    public static List<LocalMedia> getPreViewPath(List<String> pic_url) {
        List<LocalMedia> medias = new ArrayList<>();
        for (int i = 0; i < pic_url.size(); i++) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(pic_url.get(i));
            medias.add(localMedia);
        }
        return medias;
    }
    /**
     * 图片放大
     *
     * @param
     */
    public static void ImageClick(View v, final List<String> url, final int position, final Activity context) {
        if (url == null) return;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(context).externalPicturePreview(position, getPreViewPath(url));
            }
        });
    }

    public static void getUserInfo() {
        try {
            Log.d("userInfo", "用户信息");
            if (!AppUtil.isLogin())
                return;
            BookApi.getInstance().
                    service
                    .getUserBean(Constant.JSON_TYPE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Base<UserBean>>() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Base<UserBean> userModelBase) {
                            if (userModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                                Log.d("userInfo", userModelBase.getData().toString());
                                UserBean user = userModelBase.getData();

                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getString(String str) {
        if (str == null) return "";
        if (str != null && str.isEmpty()) return str;
        if (!str.isEmpty() && str.length() < 3)
            return str;
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(Double.parseDouble(str));
    }

}
