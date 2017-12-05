package com.spriteapp.booklibrary.api.interceptor;

import android.support.annotation.NonNull;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.SignUtil;
import com.spriteapp.booklibrary.util.Util;

import java.io.IOException;

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
                .addHeader(Constant.USER_AGENT_KEY, AppUtil.getUserAgent())
                .addHeader(SignConstant.HEADER_CLIENT_ID, HuaXiSDK.getInstance().getClientId())
                .addHeader(SignConstant.TIMESTAMP_KEY, String.valueOf(SignUtil.getCurrentTime()))
                .addHeader(SignConstant.VERSION_KEY, Constant.VERSION)
                .addHeader(SignConstant.SIGN_KEY, SignUtil.createSign(Util.getVersionName()))
                .addHeader(SignConstant.SN_KEY, AppUtil.getHeaderSnValue())
                .addHeader(SignConstant.OS, android.os.Build.VERSION.RELEASE)
                .addHeader(SignConstant.MAC, Util.getMacAddr())
                .addHeader(SignConstant.IMEI, Util.getid())
                .addHeader(SignConstant.TOKEN_KEY, SharedPreferencesUtil.getInstance()
                        .getString(SignConstant.HUA_XI_TOKEN_KEY))
                .build();
//        Log.d("versionname", "versionname===" + Util.getVersionName());
        return chain.proceed(request);
    }
}
