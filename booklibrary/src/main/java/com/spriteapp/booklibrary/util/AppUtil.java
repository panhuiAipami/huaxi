package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.ChapterDb;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.enumeration.LoginStateEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import de.greenrobot.event.EventBus;

import static com.spriteapp.booklibrary.util.Util.PREFS_DEVICE_ID;
import static com.spriteapp.booklibrary.util.Util.PREFS_FILE;

public class AppUtil {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getAppContext() {
        return mContext;
    }

    /**
     * @return 返回HTTP发送请求的User Agent信息
     */
    public static String getUserAgent() {
        return " HuaxiReader" + "/" + Constant.VERSION + " (" + android.os.Build.MODEL + ") android/"
                + android.os.Build.VERSION.RELEASE;
    }

    /**
     * 带有Toast的网络判断
     * 如果不需要可调用 {@link NetworkUtil#isAvailable(Context)}
     */
    public static boolean isNetAvailable(Context context) {
        if (!NetworkUtil.isAvailable(context)) {
            ToastUtil.showSingleToast(R.string.please_check_network_info);
            return false;
        }
        return true;
    }

    protected static UUID uuid;

    /**
     * 获取请求头sn
     */
    public static String getHeaderSnValue() {
//        String snValue = DeviceUtil.getDeviceId(mContext);
//        if (StringUtil.isEmpty(snValue)) {
//            snValue = DeviceUtil.getMacAddress(mContext);
//        }
//        return MD5Util.encryptMD5(snValue);
        if (uuid == null) {
            final SharedPreferences prefs = AppUtil.getAppContext().getSharedPreferences(PREFS_FILE, 0);
            final String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (id != null) {
                // Use the ids previously computed and stored in the prefs file
                uuid = UUID.fromString(id);
            } else {
                final String androidId = Settings.Secure.getString(AppUtil.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                // Use the Android ID unless it's broken, in which case fallback on deviceId,
                // unless it's not available, then fallback on a random number which we store
                // to a prefs file
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } else {
                        final String deviceId = ((TelephonyManager) AppUtil.getAppContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                        uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                // Write the value out to the prefs file
                prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
            }
        }
        return uuid.toString();
    }

    public static boolean isLogin() {
        String token = SharedPreferencesUtil.getInstance()
                .getString(SignConstant.HUA_XI_TOKEN_KEY);
        return !StringUtil.isEmpty(token);
    }

    public static boolean isLogin(Context context) {
        String token = SharedPreferencesUtil.getInstance()
                .getString(SignConstant.HUA_XI_TOKEN_KEY);
        if (!StringUtil.isEmpty(token)) {
            return true;
        } else {//直接跳登录页面
            HuaXiSDK.getInstance().toLoginPage(context);
            return false;
        }
    }

    public static String getToken() {
        return SharedPreferencesUtil.getInstance().getString(SignConstant.HUA_XI_TOKEN_KEY);
    }

    public static void loginOut() {
        clearToken();
        clearBookInfo();
        HuaXiSDK.getInstance().setRegisterModelNull();
        clearShare();
        HuaXiSDK.mLoginState = LoginStateEnum.UN_LOGIN;
        EventBus.getDefault().post(UpdaterPayEnum.UPDATE_LOGIN_OUT);
    }

    private static void clearShare() {
        SharedPreferencesUtil.getInstance().removeAll();
    }

    public static void clearToken() {
        SharedPreferencesUtil.getInstance().putString(SignConstant.HUA_XI_TOKEN_KEY, "");
    }

    public static void clearBookInfo() {
        BookDb bookDb = new BookDb(mContext);
        ContentDb contentDb = new ContentDb(mContext);
        ChapterDb chapterDb = new ChapterDb(mContext);
        bookDb.deleteDb();
        contentDb.deleteDb();
        chapterDb.deleteDb();
    }

}
