package net.huaxi.reader.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.PhoneUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.util.EncodeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by ZMW on 2015/12/14.
 * 公共参数
 */
public class CommonUtils {

    public static final String COMMOUNTUTILS_SR = "1";

    /**
     * 获取GET公共参数
     *
     * @return
     */
    public static String getPublicGetArgs() {
        String result = "";
        result += "&sr=" + EncodeUtils.encodeString_UTF8(COMMOUNTUTILS_SR);
        result += "&aver=" + EncodeUtils.encodeString_UTF8(PhoneUtils.getVersionName() + "." + PhoneUtils.getVersionCode());//软件版本号
        result += "&sver=" + EncodeUtils.encodeString_UTF8(PhoneUtils.getSystemRelease());//手机系统版本号
        result += "&pmod=" + EncodeUtils.encodeString_UTF8(PhoneUtils.getPhoneModel());//手机型号
        result += "&reso=" + EncodeUtils.encodeString_UTF8(getScreenPix());//手机分辨率
        result += "&mobop=" + EncodeUtils.encodeString_UTF8(getSIMOperator());//运营商（CT:中国电信， CMCC:中国移动， CU:中国联通）
        result += "&im=" + EncodeUtils.encodeString_UTF8(PhoneUtils.getPhoneImei(AppContext.getInstance()));//移动设备国际识别码
        String nstat = NetUtils.checkNet().getDesc();
        if (!StringUtils.isBlank(nstat)) {
            if ("WIFI网络".equals(nstat)) {
                nstat = "WiFi";
            } else if ("2G手机网络".equals(nstat)) {
                nstat = "2G";
            } else if ("3G或其它手机网络".equals(nstat)) {
                nstat = "3G";
            } else {
                nstat = "";
            }
        }
        result += "&nstat=" + EncodeUtils.encodeString_UTF8(nstat); //网络状态(2G, 3G, 4G, WiFi)
        result += "&apc=" + EncodeUtils.encodeString_UTF8(getChannel());//app的渠道
        result += "&mad=" + EncodeUtils.encodeString_UTF8(PhoneUtils.getMacAddress(AppContext
                .getInstance()));//mac地址
        result += "&sla=" + EncodeUtils.encodeString_UTF8("0");//系统语言
        result += "&stz=" + EncodeUtils.encodeString_UTF8("" + TimeZone.getDefault().getRawOffset() / 60000);  //时区
        result += "&apid=" + EncodeUtils.encodeString_UTF8(PhoneUtils.getPackageName());
        result += "&apnm="+EncodeUtils.encodeString_UTF8("九域文学网");
        result += "&channel_type=" + EncodeUtils.encodeString_UTF8(String.valueOf(SharePrefHelper.getSexClassify()));
        return result;
    }

    /**
     * 获取Post公共参数
     *
     * @return
     */
    public static Map<String, String> getPublicPostArgs() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("sr", COMMOUNTUTILS_SR);
        //软件版本号
        if (!StringUtils.isBlank(PhoneUtils.getVersionName())) {
            map.put("aver", PhoneUtils.getVersionName() + "." + PhoneUtils.getVersionCode());
        }
        //手机系统版本号
        if (!StringUtils.isBlank(PhoneUtils.getSystemRelease())) {
            map.put("sver", PhoneUtils.getSystemRelease());
        }
        //手机型号
        if (!StringUtils.isBlank(PhoneUtils.getPhoneModel())) {
            map.put("pmod", PhoneUtils.getPhoneModel());
        }
        //手机分辨率
        if (!StringUtils.isBlank(getScreenPix())) {
            map.put("reso", getScreenPix());
        }
        //运营商（CT:中国电信， CMCC:中国移动， CU:中国联通）
        if (!StringUtils.isBlank(getSIMOperator())) {
            map.put("mobop", getSIMOperator());
        }
        //移动设备国际识别码
        if (!StringUtils.isBlank(PhoneUtils.getPhoneImei(AppContext.getInstance()))) {
            map.put("im", PhoneUtils.getPhoneImei(AppContext.getInstance()));
        }
        //网络状态(2G, 3G, 4G, WiFi)
        String nstat = NetUtils.checkNet().getDesc();
        if (!StringUtils.isBlank(nstat)) {
            if ("WIFI网络".equals(nstat)) {
                nstat = "WiFi";
            } else if ("2G手机网络".equals(nstat)) {
                nstat = "2G";
            } else if ("3G或其它手机网络".equals(nstat)) {
                nstat = "3G";
            } else {
                nstat = "";
            }
        }
        if (!StringUtils.isBlank(nstat)) {
            map.put("nstat", nstat);
        }
        //app的渠道
        if (!StringUtils.isBlank(getChannel())) {
            map.put("apc", getChannel());
        }
        //mac地址
        if (!StringUtils.isBlank(PhoneUtils.getMacAddress(AppContext.getInstance()))) {
            map.put("mad", PhoneUtils.getMacAddress(AppContext.getInstance()));
        }
        //系统语言
        map.put("apc", "0");
        //时区
        map.put("stz", "" + TimeZone.getDefault().getRawOffset() / 60000);
        map.put("apid", PhoneUtils.getPackageName());
        map.put("apnm", "小说阅读网畅读版");
        map.put("channel_type",EncodeUtils.encodeString_UTF8(String.valueOf(SharePrefHelper.getSexClassify())));
        return map;
    }


    private static String getChannel() {
        Context ctx = AppContext.getInstance();
        try {
            ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName
                    (), PackageManager.GET_META_DATA);
            String channel = info.metaData.getString("UMENG_CHANNEL");

            return channel;

        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 获取手机卡类型，移动、联通、电信
     *
     * @return
     */
    private static String getSIMOperator() {
        Context ctx = AppContext.getInstance();
        if (ctx != null) {
            TelephonyManager iPhoneManager = (TelephonyManager) ctx.getSystemService(Context
                    .TELEPHONY_SERVICE);
            String iNumeric = iPhoneManager.getSimOperator();
            if (iNumeric != null && iNumeric.length() > 0) {
                if (iNumeric.equals("46000") || iNumeric.equals("46002") || iNumeric.equals
                        ("46007")) // 中国移动
                    return "CMCC";
                else if (iNumeric.equals("46001")) // 中国联通
                    return "CU";
                else if (iNumeric.equals("46003")) // 中国电信
                    return "CT";
            }
        }
        return null;
    }

    //获取屏幕大小
    public static String getScreenPix() {
        Context ctx = AppContext.getInstance();
        if (ctx == null) {
            return null;
        }

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) ctx.getApplicationContext().getSystemService
                (Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);

        return dm.widthPixels + "x" + dm.heightPixels;
    }





}
