package com.spriteapp.booklibrary.api.interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.SignUtil;
import com.spriteapp.booklibrary.util.Util;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kuangxiaoguo on 2017/7/10.
 */

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .addHeader(SignConstant.HEADER_CLIENT_ID, "40")
                .addHeader(SignConstant.MAC, Util.getMacAddr())
                .addHeader(SignConstant.IMEI, Util.getid())
                .addHeader(SignConstant.OS, android.os.Build.VERSION.RELEASE)
                .addHeader(SignConstant.TIMESTAMP_KEY, String.valueOf(SignUtil.getCurrentTime()))
                .addHeader(SignConstant.VERSION_KEY, Util.getVersionName())
                .addHeader(SignConstant.SIGN_KEY, sign())
                .addHeader(SignConstant.SN_KEY, AppUtil.getHeaderSnValue())
                .addHeader(SignConstant.TOKEN_KEY, SharedPreferencesUtil.getInstance()
                        .getString(SignConstant.HUA_XI_TOKEN_KEY))
                .build();

        Log.d("header", "USER_AGENT_KEY===" + AppUtil.getUserAgent() +
                "token" + SharedPreferencesUtil.getInstance().getString(SignConstant.HUA_XI_TOKEN_KEY));
        return chain.proceed(request);
    }

    /**
     * 签名
     *
     * @return
     */
    public String sign() {//http%3A%2F%2Fgazhi.hxdrive.net%2F
        String url = "http://s.hxdrive.net/";
        StringBuilder bd = new StringBuilder();
        try {
            bd.append("client_id=40");
            bd.append("&timestamp=" + System.currentTimeMillis() / 1000 + "");
            bd.append("&url=" + URLEncoder.encode(url, "UTF-8"));
            bd.append("&version=" + Util.getVersionName());
            bd.append("fygopf7cixub8cpkh1oruik2byt2ykvkh81sy6");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Util.Md5(bd.toString());
    }
}
