package net.huaxi.reader;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import net.huaxi.reader.utils.LoginHelper;

/**
 * Created by Administrator on 2017/11/15.
 */

public class MyApplication extends com.spriteapp.booklibrary.MyApplication {
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
