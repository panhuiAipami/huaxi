/**
 *
 */
package com.tools.commonlibs.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tools.commonlibs.common.AppManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

/**
 * Activity基类.
 *
 * @author taoyf
 * @time 2015年1月8日
 */
public class BaseActivity extends FragmentActivity {
    public SystemBarTintManager tintManager;//状态栏管理工具
    public static Dialog dialogLoading;
    public static final int MESSAGE_SHOWDIALOG = 300001;
    public static final int MESSAGE_HIDEDIALOG = 300002;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setNeedStatistics(true);
        AppManager.getAppManager().addActivity(this);
        tintManager = new SystemBarTintManager(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //得到view视图窗口
            Window window = getActivity().getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
//            window.setStatusBarColor(Color.parseColor("#212A3C"));

            //        //此处可以重新指定状态栏颜色
//        tintManager.setStatusBarAlpha(1f);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor("#212A3C"));
        }
        initUmengMessage();



    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public Activity getActivity() {
        return this;
    }

    public Handler getHandler() {
        return mHandler;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handlerOwnMessage(msg);
        }

    };

    /**
     * 处理自定义Message(子类继承实现)
     *
     * @param msg
     */
    public void handlerOwnMessage(Message msg) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if(needStatistics){
                MobclickAgent.onPageStart(getClass().getName());
                LogUtils.debug("UmengPageTrack 开始页面"+getClass().getName());
            }
            MobclickAgent.onResume(getActivity());
//			StatService.onResume(getActivity());
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }

    private boolean needStatistics=true;

    public boolean isNeedStatistics() {
        return needStatistics;
    }

    public void setNeedStatistics(boolean needStatistics) {
        this.needStatistics = needStatistics;
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if(needStatistics){
                LogUtils.debug("UmengPageTrack 结束页面"+getClass().getName());
                MobclickAgent.onPageEnd(getClass().getName());
            }
            MobclickAgent.onPause(getActivity());
//			StatService.onResume(getActivity());
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.debug("onDestroy() = " + BaseActivity.class.getName());
        try {
            //TODO:注销RequestQueueManager.cancelAll()
//			RequestQueueManager.cancelAll(BaseActivity.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onKeyBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击back键，处理方法。
     */
    public void onKeyBack() {
        finish();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {if (!StringUtils.isEmpty(message)) {
                    ViewUtils.toastShort(message);
                }
            }
        });
    }

    private void initUmengMessage() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        mPushAgent.enable();
        PushAgent.getInstance(BaseActivity.this).onAppStart();
        String device_token = UmengRegistrar.getRegistrationId(BaseActivity.this);
        LogUtils.debug("device_token====" + device_token);

    }
}
