package com.spriteapp.booklibrary.config;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.spriteapp.booklibrary.enumeration.LoginStateEnum;
import com.spriteapp.booklibrary.enumeration.UpdateNightMode;
import com.spriteapp.booklibrary.manager.NightModeManager;
import com.spriteapp.booklibrary.model.RegisterModel;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.FileUtils;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by kuangxiaoguo on 2017/7/25.
 */

public class HuaXiSDK {

    private static final String TAG = "HuaXiSDK";
    private static HuaXiSDK mHuaXiSDK;
    private HuaXiConfig mConfig;
    private RegisterModel mRegisterModel;
    public static LoginStateEnum mLoginState;

    private HuaXiSDK() {
    }

    public static HuaXiSDK getInstance() {
        if (mHuaXiSDK == null) {
            synchronized (HuaXiSDK.class) {
                if (mHuaXiSDK == null) {
                    mHuaXiSDK = new HuaXiSDK();
                }
            }
        }
        return mHuaXiSDK;
    }

    public synchronized void init(HuaXiConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("HuaXiConfig不能为null");
        } else {
            this.mConfig = config;
            initSaveDir();
            changeMode(mConfig.isNightMode);
        }
    }

    private void initSaveDir() {
        AppUtil.init(mConfig.context);
        FileUtils.createRootPath(mConfig.context);
        SharedPreferencesUtil.init(mConfig.context.getApplicationContext(),
                mConfig.context.getPackageName() + "hua_xi_preference", Context.MODE_PRIVATE);
//        android.util.Log.d("inittoken",mConfig.context.getPackageName() + "hua_xi_preference");
    }

    public void toLoginPage(Context context) {
        if (mConfig.channelListener == null) {
            throw new NullPointerException("HuaXiConfig ChannelListener不能为空");
        }
        if (mLoginState == LoginStateEnum.LOADING) {
            ToastUtil.showSingleToast("登录中");
            return;
        }
        mConfig.channelListener.toLoginPage(context);
    }
    public void toWXPay(WeChatBean response,String push_id) {
        if (mConfig.channelListener == null) {
            throw new NullPointerException("HuaXiConfig ChannelListener不能为空");
        }
//        if (mLoginState == LoginStateEnum.LOADING) {
//            ToastUtil.showSingleToast("登录中");
//            return;
//        }
        mConfig.channelListener.toWXPay(response,push_id);
    }

    public void showShareDialog(Context context, BookDetailResponse shareDetail, boolean isNightMode,int type) {
        if (mConfig.channelListener == null) {
            throw new NullPointerException("HuaXiConfig ChannelListener不能为空");
        }
        mConfig.channelListener.showShareDialog(context, shareDetail, isNightMode,type);
    }

    public int getChannelId() {
        return mConfig.channelId;
    }

    public void syncLoginStatus(RegisterModel registerModel) {
        mLoginState = AppUtil.isLogin() ? LoginStateEnum.SUCCESS : LoginStateEnum.UN_LOGIN;
        if (registerModel == null) {
            return;
        }
        if (TextUtils.isEmpty(registerModel.getUserName()) ||
                TextUtils.isEmpty(registerModel.getUserId())) {
            Log.d(TAG, "syncLoginStatus: 用户名或id不能为空");
            return;
        }
        registerUser(registerModel);
    }

    private void registerUser(RegisterModel registerModel) {
        mRegisterModel = registerModel;
        EventBus.getDefault().post(mRegisterModel);
    }

    public RegisterModel getRegisterModel() {
        return mRegisterModel;
    }

    public void setRegisterModelNull() {
        mRegisterModel = null;
    }

    public String getSignSecret() {
        if (mConfig == null) {
            throw new NullPointerException("HuaXiConfig 不能为空");
        }
        return mConfig.signSecret;
    }

    public String getClientId() {
        if (mConfig == null) {
            throw new NullPointerException("HuaXiConfig 不能为空");
        }
        return String.valueOf(mConfig.clientId);
    }
    public int getSex() {
        if (mConfig == null) {
            throw new NullPointerException("HuaXiConfig 不能为空");
        }
        return mConfig.sex;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mConfig == null) {
            throw new NullPointerException("HuaXiConfig 不能为空");
        }
        if (mConfig.resultListener == null) {
            return;
        }
        mConfig.resultListener.onActivityResult(requestCode, resultCode, data);
    }

    public HuaXiConfig getConfig() {
        if (mConfig == null) {
            throw new NullPointerException("HuaXiConfig 不能为空");
        }
        return mConfig;
    }

    public void changeMode(boolean isNight) {
        NightModeManager.setStoreTheme(isNight);
        EventBus.getDefault().post(UpdateNightMode.UPDATE_NIGHT_MODE);
    }

    public void loginOut() {
        AppUtil.loginOut();
    }
}