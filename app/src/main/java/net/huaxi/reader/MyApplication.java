package net.huaxi.reader;

import android.app.Application;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;

import net.huaxi.reader.callback.CallBack;
import net.huaxi.reader.http.OKhttpRequest;
import net.huaxi.reader.http.UrlUtils;
import net.huaxi.reader.utils.LoginHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/15.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;
    private LoginActivity.MyHandler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        UMShareAPI.get(this);
        PlatformConfig.setWeixin(LoginHelper.WX_APP_ID, LoginHelper.WX_AppSecert);
        PlatformConfig.setQQZone(LoginHelper.QQLOGIN_APP_ID, LoginHelper.QQLOGIN_APP_ID);
        PlatformConfig.setSinaWeibo(LoginHelper.WB_APP_KEY, LoginHelper.WB_APP_SECRET, LoginHelper.WB_REDIRECT_URL);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        try {
            mPushAgent.register(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    IUmengRegisterCallback callback = new IUmengRegisterCallback() {
        @Override
        public void onSuccess(String deviceToken) {
            Log.d("deviceToken", deviceToken);
            Map<String, String> map = new HashMap<String, String>();
            map.put("device_token", deviceToken);
            map.put("platform_type", "2");
            new OKhttpRequest().get("push_get", UrlUtils.PUSH_GET, map, new CallBack() {
                @Override
                public void fail(String actionName, Object object) {

                }

                @Override
                public void success(String actionName, Object object) {

                }
            });
        }

        @Override
        public void onFailure(String s, String s1) {

        }
    };
    public static void umengNotificClick(String push_id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("push_id", push_id);
        new OKhttpRequest().get("push_call", UrlUtils.PUSH_CALL, map, new CallBack() {
            @Override
            public void fail(String actionName, Object object) {

            }

            @Override
            public void success(String actionName, Object object) {

            }
        });
    }


    public static MyApplication getInstance() {
        return mInstance;
    }

    public static void setInstance(MyApplication mInstance) {
        MyApplication.mInstance = mInstance;
    }

    public LoginActivity.MyHandler getHandler() {
        return handler;
    }

    public void setHandler(LoginActivity.MyHandler handler) {
        this.handler = handler;
    }
}
