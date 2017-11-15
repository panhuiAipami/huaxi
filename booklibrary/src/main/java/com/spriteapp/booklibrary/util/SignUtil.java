package com.spriteapp.booklibrary.util;

import android.net.Uri;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.SignConstant;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class SignUtil {

    private static long currentTime;

    /**
     * 生成签名
     *
     * @param version 客户端版本
     */
    public static String createSign(String version) {
        Map<String, String> signMap = new TreeMap<>();
        signMap.put(SignConstant.CLIENT_ID_KEY, HuaXiSDK.getInstance().getClientId());
        signMap.put(SignConstant.URL_KEY, Uri.encode(Constant.BASE_URL));
        signMap.put(SignConstant.TIMESTAMP_KEY, String.valueOf(currentTime));
        signMap.put(SignConstant.VERSION_KEY, version);
        String signValue = getSignValue(signMap);
        return MD5Util.encryptMD5(signValue + HuaXiSDK.getInstance().getSignSecret());
    }

    /**
     * 拼接map中的key和value
     */
    private static String getSignValue(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String contactValue = iterator.hasNext() ? "&" : "";
            builder.append(next.getKey()).append("=").append(next.getValue()).append(contactValue);
        }
        return builder.toString();
    }

    public static long getCurrentTime() {
        currentTime = System.currentTimeMillis() / 1000;
        return currentTime;
    }
}
