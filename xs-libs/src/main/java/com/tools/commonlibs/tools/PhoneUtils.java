package com.tools.commonlibs.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.tools.commonlibs.common.CommonApp;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

public class PhoneUtils {
    private static final String EMPTYH_IMEI = "000000000000000";
    private static final int MY_READ_PHONE_STATE = 1;

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        try {
            return URLEncoder.encode(android.os.Build.MODEL, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 获取手机机器码
     * <p/>
     * IMEI国际移动 设备 身份码
     *
     * @param ctx
     * @return
     */
    public static String getPhoneImei(Context ctx) {
        String imei = null;
        try {
            TelephonyManager telMan = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            if (telMan != null && StringUtils.isBlank(imei)) {// 优先获得得到imei（所有的设备都可以返回）
                imei = telMan.getDeviceId();
            }
            if (telMan != null && StringUtils.isBlank(imei)) {// imei获得失败，获得sim（所有的GSM设备可以返回，CDMA返回空）
                imei = telMan.getSimSerialNumber();
            }
            if (telMan != null && StringUtils.isBlank(imei)) {// 以前失败，根据一定策略生成15位imei
                StringBuilder sb = new StringBuilder();
                sb.append("XS");
                sb.append(Build.BOARD.length() % 10);// 1
                sb.append(Build.BRAND.length() % 10);// 2
                sb.append(Build.CPU_ABI.length() % 10);// 3
                sb.append(Build.DEVICE.length() % 10);// 4
                sb.append(Build.DISPLAY.length() % 10);// 5
                sb.append(Build.HOST.length() % 10);// 6
                sb.append(Build.ID.length() % 10);// 7
                sb.append(Build.MANUFACTURER.length() % 10);// 8
                sb.append(Build.MODEL.length() % 10);// 9
                sb.append(Build.PRODUCT.length() % 10);// 10
                sb.append(Build.TAGS.length() % 10);// 11
                sb.append(Build.TYPE.length() % 10);// 12
                sb.append(Build.USER.length() % 10);// 13

                imei = sb.toString();
            }

            if (StringUtils.isBlank(imei)) {// 如果还是获得失败时返回固定值（基本不可能）

                imei = EMPTYH_IMEI;

            }

            return imei;

        } catch (Throwable e) {
            LogUtils.error(e.getMessage(), e);
        }

        return EMPTYH_IMEI;// 出现异常时返回固定imei

    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddress(Context ctx) {
        String mac = null;
        if (ctx != null) {
            try {
                WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    mac = info.getMacAddress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mac;

    }

    /**
     * 获取屏幕大小
     *
     * @param ctx
     * @return
     */
    public static int[] getScreenPix(Context ctx) {
        if (ctx == null) {
            return null;
        }
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int[] pixs = {dm.widthPixels, dm.heightPixels};

        return pixs;
    }

    /**
     * 系统应用版本号
     *
     * @return
     */
    public static int getSystemVersionCode() {
        int version = 0;
        try {
            version = android.os.Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            LogUtils.error(e.getMessage(), e);
        }
        return version;
    }

    /**
     * 添加快捷方式到桌面 要点：
     * <p/>
     * 1.给Intent指定action="com.android.launcher.INSTALL_SHORTCUT"
     * <p/>
     * 2.给定义为Intent.EXTRA_SHORTCUT_INENT的Intent设置与安装时一致的action(必须要有)
     * <p/>
     * 3.添加权限:com.android.launcher.permission.INSTALL_SHORTCUT
     */

    public static void addShortcutToDesktop(Activity act, int iconResourceId, int appNameResourceId) {

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        // 不允许重建

        shortcut.putExtra("duplicate", false);

        // 设置名字

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, act.getString(appNameResourceId));

        // 设置图标
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(act, iconResourceId));

        // 设置意图和快捷方式关联程序

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(act, act.getClass()).setAction(Intent.ACTION_MAIN));

        // 发送广播

        act.sendBroadcast(shortcut);

    }

    /**
     * 解析出短信
     *
     * @param intent 从onReceive方法的参数
     * @return
     */
    public static final SmsMessage[] getMessagesFromIntent(Intent intent) {

        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++)

        {

            pduObjs[i] = (byte[]) messages[i];

        }

        byte[][] pdus = new byte[pduObjs.length][];

        int pduCount = pdus.length;

        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++)

        {

            pdus[i] = pduObjs[i];

            msgs[i] = SmsMessage.createFromPdu(pdus[i]);

        }

        return msgs;

    }

    // 得到当前运行的Activity名
    public static String getCurActivity(Activity act) {
        ActivityManager manager = (ActivityManager) act.getSystemService(Activity.ACTIVITY_SERVICE);
        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String shortClassName = info.topActivity.getShortClassName(); // 类名

        return shortClassName;
    }

    // 判断是否是飞行模式
    @SuppressWarnings("deprecation")
    public static boolean getAirplaneMode(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System
                .AIRPLANE_MODE_ON, 0);
        return (isAirplaneMode == 1) ? true : false;
    }

    /**
     * 得到包信息
     */
    public static PackageInfo getPackageInfo(Context ctx, String packageName) {
        PackageManager manager = ctx.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);

        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);

            if (pI.packageName.equalsIgnoreCase(packageName))
                return pI;
        }

        return null;
    }

    public static String getSignature(Activity act, String pagekeName) {
        String pkgname = pagekeName;
        boolean isEmpty = TextUtils.isEmpty(pkgname);

        if (isEmpty) {
            return StringUtils.EMPTY;
        } else {
            try {
                /** 通过包管理器获得指定包名包含签名的包信息 **/
                PackageInfo packageInfo = act.getPackageManager().getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
                /******* 通过返回的包信息获得签名数组 *******/
                Signature[] signatures = packageInfo.signatures;
                /******* 循环遍历签名数组拼接应用签名 *******/
                StringBuilder builder = new StringBuilder();
                for (Signature signature : signatures) {
                    builder.append(signature.toCharsString());
                }
                /************** 得到应用签名 **************/
                return builder.toString();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return StringUtils.EMPTY;
    }

    // android系统版本号
    public static String getSystemRelease() {
        String release = android.os.Build.VERSION.RELEASE;
        return release;

    }

    /**
     * 获取应用的版本名
     *
     * @return
     */
    public static String getVersionName() {
        String code = null;
        try {
            PackageInfo info = CommonApp.getInstance().getPackageManager().getPackageInfo(CommonApp.getInstance().getPackageName(), 0);
            code = String.valueOf(info.versionName);
            return code;
        } catch (Exception e) {
        }
        return code;

    }

    /**
     * 获取应用的版本号
     *
     * @return
     */
    public static int getVersionCode() {
        int code = 0;
        try {
            PackageInfo info = CommonApp.getInstance().getPackageManager().getPackageInfo(CommonApp.getInstance().getPackageName(), 0);
            code = info.versionCode;
            return code;
        } catch (Exception e) {
        }
        return code;

    }

    /**
     * 获取应用包名
     *
     * @return
     */
    public static String getPackageName() {

        return CommonApp.getInstance().getPackageName();

    }

    /**
     * 检测该所对应的应用是否存在
     *
     * @param className
     * @return
     */

    public static boolean checkClass(String className) {

        if (StringUtils.isBlank(className))
            return false;

        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }

    }

    /**
     * 获取android机器的IP地址
     *
     * @return
     * @author taoyf
     * @time 2015年4月10日
     */
    public static String getIpAddress() {
        String ip = "127.0.0.1";
        try {
            // 获取所有本地可用网络接口信息，然后返回枚举中的元素
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements(); ) {
                // 返回枚举本地网络接口的下一个元素
                NetworkInterface networkInterface = enumeration.nextElement();
                // 获取IP地址信息，然后返回枚举中的元素
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    // 返回枚举集合中的下一个IP地址信息
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    // 如果该地址不为回环地址
                    if (!inetAddress.isLoopbackAddress()) {
                        // 显示出主机IP地址
                        ip = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return ip;
    }

}
