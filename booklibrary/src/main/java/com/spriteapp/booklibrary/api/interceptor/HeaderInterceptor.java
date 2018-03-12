package com.spriteapp.booklibrary.api.interceptor;

import android.os.Build;
import android.support.annotation.NonNull;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.SignUtil;
import com.spriteapp.booklibrary.util.Util;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;

/**
 * Created by kuangxiaoguo on 2017/7/10.
 */

public class HeaderInterceptor implements Interceptor {
    private static String UserToken = "";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .addHeader(Constant.USER_AGENT_KEY, AppUtil.getUserAgent())
                .addHeader(SignConstant.HEADER_CLIENT_ID, HuaXiSDK.getInstance().getClientId())
                .addHeader(SignConstant.TIMESTAMP_KEY, String.valueOf(SignUtil.getCurrentTime()))
                .addHeader(SignConstant.VERSION_KEY, Constant.VERSION)
                .addHeader(SignConstant.SIGN_KEY, SignUtil.createSign(Util.getVersionName()))
                .addHeader(SignConstant.SN_KEY, AppUtil.getHeaderSnValue())
                .addHeader(SignConstant.OS, android.os.Build.VERSION.RELEASE)
                .addHeader(SignConstant.MAC, Util.getMacAddr())
                .addHeader(SignConstant.IMEI, Util.getid())

                .addHeader(SignConstant.SYSTEM, "Android")//操作系统
                .addHeader(SignConstant.SYSTEM_VERSION, android.os.Build.VERSION.RELEASE)//系统版本
                .addHeader(SignConstant.DEVICE, android.os.Build.MODEL)//手机型号
                .addHeader(SignConstant.LOCATION, Locale.getDefault().getCountry())//获取地区
                .addHeader(SignConstant.LANGUAGE, Locale.getDefault().getLanguage())//获取语言
                .addHeader(SignConstant.NSTAT, NetworkUtil.getNetWorkTypeName(AppUtil.getAppContext()))//网络类型
                .addHeader(SignConstant.PIXEL, ScreenUtil.getScreenHeight() + " " + ScreenUtil.getScreenWidth())//屏幕分辨率
                .addHeader(SignConstant.G_GENDER, SharedPreferencesUtil.getInstance().getInt(SEX, 0) + "")
                .addHeader(SignConstant.TOKEN_KEY, SharedPreferencesUtil.getInstance()
                        .getString(SignConstant.HUA_XI_TOKEN_KEY))
                .build();
//        Log.d("versionname", "versionname===" + Util.getVersionName());
//        HuaXiSDK.getInstance().toWXPay(null, "token" + SharedPreferencesUtil.getInstance()
//                .getString(SignConstant.HUA_XI_TOKEN_KEY));
        return chain.proceed(request);
    }
}
