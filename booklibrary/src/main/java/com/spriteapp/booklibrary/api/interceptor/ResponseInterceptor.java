package com.spriteapp.booklibrary.api.interceptor;

import android.support.annotation.NonNull;

import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.enumeration.LoginModeEnum;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class ResponseInterceptor implements Interceptor {

    private static final String TAG = "ResponseInterceptor";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        boolean hasToken = url.contains(LoginModeEnum.CHANNEL_LOGIN.getValue())
                || url.contains(LoginModeEnum.WECHAT_LOGIN.getValue())
                || url.contains(LoginModeEnum.QQ_LOGIN.getValue())
                || url.contains(LoginModeEnum.WEIBO_LOGIN.getValue());
        if (hasToken) {
            String token = response.header(SignConstant.TOKEN_KEY);
            SharedPreferencesUtil.getInstance().putString(SignConstant.HUA_XI_TOKEN_KEY, token);
        }
        return response;
    }
}
