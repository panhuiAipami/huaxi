package net.huaxi.reader;

import android.app.Application;
import android.app.Notification;
import android.content.Context;

import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;

import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.callback.CallBack;
import net.huaxi.reader.callback.ShareBeanCallBack;
import net.huaxi.reader.callback.ShareResult;
import net.huaxi.reader.http.OKhttpRequest;
import net.huaxi.reader.http.UrlUtils;
import net.huaxi.reader.utils.LoginHelper;
import net.huaxi.reader.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/15.
 */

public class MyApplication extends Application implements ShareResult {
    private static MyApplication mInstance;
    private LoginActivity.MyHandler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mInstance = this;
            UMShareAPI.get(this);
            PlatformConfig.setWeixin(LoginHelper.WX_APP_ID, LoginHelper.WX_AppSecert);
            PlatformConfig.setQQZone(LoginHelper.QQLOGIN_APP_ID, LoginHelper.QQLOGIN_APP_ID);
            PlatformConfig.setSinaWeibo(LoginHelper.WB_APP_KEY, LoginHelper.WB_APP_SECRET, LoginHelper.WB_REDIRECT_URL);
            PushAgent mPushAgent = PushAgent.getInstance(this);
            //注册推送服务，每次调用register方法都会回调该接口
            mPushAgent.register(callback);
//            mPushAgent.setNotificationClickHandler(notificationClickHandler);
            mPushAgent.setMessageHandler(messageHandler);
            net.huaxi.reader.callback.ListenerManager.getInstance().setResult(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferencesUtil.init(MyApplication.getInstance(),
                MyApplication.getInstance().getPackageName() + "hua_xi_preference", Context.MODE_PRIVATE);
        android.util.Log.d("inittoken", MyApplication.getInstance().getPackageName() + "hua_xi_preference");
    }

    UmengMessageHandler messageHandler = new UmengMessageHandler() {
        @Override
        public Notification getNotification(Context context, UMessage msg) {

            switch (msg.builder_id) {
                default:
                    android.util.Log.d("getNotification", "通知点击");
                    //默认为0，若填写的builder_id并不存在，也使用默认。
                    return super.getNotification(context, msg);
            }
        }

        @Override
        public void setPrevMessage(UMessage uMessage) {

            android.util.Log.d("getNotification", "通知点击777");
            if (ListenerManager.getInstance().getReadActivityFinish() != null) {//销毁readActivity
                ListenerManager.getInstance().getReadActivityFinish().setActivityFinish();
            }
            super.setPrevMessage(uMessage);
        }
    };

    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
//            Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            android.util.Log.d("dealWithCustomAction", "通知点击");
//            ToastUtil.showToast("通知点击");
        }
    };
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


    @Override
    public void success() {
        ToastUtil.showShort("分享成功");
    }

    @Override
    public void cancel() {
        ToastUtil.showShort("分享取消");
    }

    @Override
    public void faild() {
        ToastUtil.showShort("分享失败");
    }
}
