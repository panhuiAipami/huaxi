package net.huaxi.reader.common;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.tencent.bugly.crashreport.CrashReport;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.BuildConfig;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.db.model.UserTable;

/**
 * 用户管理类.
 *
 * @author taoyf
 */
public class UserHelper {
    private static UserHelper instance;
    private static UserTable userTable;
    private static User user;

    public static UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }
        return instance;
    }

    private UserHelper() {
    }

    public User getUser() {
        if (user == null) {
            user = SharePrefHelper.getCurLoginUser();
        }
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            return;
        }
        if (user.getUmid() != null) {
            if(!BuildConfig.LOG_DEBUG) {
                CrashReport.setUserId(user.getUmid());
            }
            SharePrefHelper.setCurLoginUser(user);
            synCookies(AppContext.getInstance(), URLConstants.PAY_URL);
        }
        UserHelper.user = user;
    }

    public static void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String cookie = SharePrefHelper.getCookie();
        String[] cs = cookie.split(";");
        for (String c : cs) {
            cookieManager.setCookie(url, c + "");//cookies是在HttpClient中获得的cookie
            LogUtils.debug("SharePrefHelper.getCookie()==" + c + "");
        }
        CookieSyncManager.getInstance().sync();
    }


    /**
     * 获取当前用户的UserID，
     *
     * @return
     */
    public String getUserId() {
        //如果当前没有登录用户，返回一个默认值代替.
        return getUser().getUmid();
    }

    public static String getToken() {
        return "token";
    }

    public boolean logout() {
        userTable = null;
        user = null;
        SharePrefHelper.setCookie("");
        SharePrefHelper.setCurLoginUser(null);
        UmengHelper.getInstance().removeAlias();
        return false;
    }

    public boolean isLogin() {
        if (StringUtils.isBlank(SharePrefHelper.getCookie()) || "-1".equals(SharePrefHelper.getCookie())) {
            return false;
        }
        return true;
    }
}
