package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Created by kuangxiaoguo on 2017/3/24.
 */

public class DeviceUtil {

    public static String getDeviceId(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getDeviceId();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取 MAC 地址
     */
    public static String getMacAddress(Context context) {
        String mac = "000000000000";
        //wifi mac地址
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = wifi.getConnectionInfo();
        if (info == null) {
            return mac;
        }
        mac = info.getMacAddress();
        return mac;
    }

}
