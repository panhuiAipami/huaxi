package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.SignUtil;
import com.spriteapp.booklibrary.util.WebViewUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class ReaderWebView extends WebView {

    private WebViewClient mClient;

    public ReaderWebView(Context context) {
        super(context);
        init();
    }

    public ReaderWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReaderWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= 21) {//5.0之后的https图片不显示
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public void setClient(final WebViewClient client) {
        mClient = new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (client != null) {
                    client.shouldOverrideUrlLoading(view, url);
                }
                return WebViewUtil.getInstance().getJumpUrl(getContext(), url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (client != null) {
                    client.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (client != null) {
                    client.onPageFinished(view, url);
                }
            }
        };

        setWebViewClient(mClient);
    }

    public void loadPage(String url) {
        loadPage(url, null);
    }

    public void loadPage(String url, WebViewClient client) {
        setClient(client);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(SignConstant.HEADER_CLIENT_ID, HuaXiSDK.getInstance().getClientId());
        headerMap.put(SignConstant.TIMESTAMP_KEY, String.valueOf(SignUtil.getCurrentTime()));
        headerMap.put(SignConstant.VERSION_KEY, Constant.VERSION);
        headerMap.put(SignConstant.SIGN_KEY, SignUtil.createSign(Constant.VERSION));
        headerMap.put(SignConstant.SN_KEY, AppUtil.getHeaderSnValue());
        headerMap.put(SignConstant.TOKEN_KEY, SharedPreferencesUtil.getInstance()
                .getString(SignConstant.HUA_XI_TOKEN_KEY));
        Log.d("userToken", "token===" + SharedPreferencesUtil.getInstance()
                .getString(SignConstant.HUA_XI_TOKEN_KEY));
        Log.d("userToken", "sn===" + AppUtil.getHeaderSnValue());
        Log.d("userToken", "url===" + url);
        loadUrl(url, headerMap);
    }

}
