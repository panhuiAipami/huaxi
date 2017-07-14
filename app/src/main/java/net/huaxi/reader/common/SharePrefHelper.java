package net.huaxi.reader.common;

import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.db.model.BookTable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 存储客户端公用信息(阅读模块有单独的类定义)
 *
 * @author ZMW
 */
public class SharePrefHelper {
    private static final String USER_PREF = "user_pref";
    //用户id
    private static final String user_id = "user_id";

//    public static void setUserId(String userId) {
//        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
//        editor.putString(user_id, userId);
//        editor.commit();
//    }
//
//    public static String getUserId() {
//        String result = null;
//        result = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(user_id,
//                Constants.DEFAULT_USERID);
//        return result;
//    }

    //用户cookie
    private static final String user_cookie = "user_cookie";

    public static void setCookie(String cookie) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putString(user_cookie, cookie);
        editor.commit();
    }

    public static String getCookie() {
        String result = null;
        result = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(user_cookie,
                "-1");
        return result;
    }

    //记录当前登录的用户
    private static final String CUR_LOGIN_USER = "cur_login_user";

    public static void setCurLoginUser(User user) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        if (user == null) {
            editor.putString(CUR_LOGIN_USER, "");
        } else {
            editor.putString(CUR_LOGIN_USER, new Gson().toJson(user));
        }
        editor.commit();
    }

    public static User getCurLoginUser() {
        User user;
        try {
            String result = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(CUR_LOGIN_USER, "");
            user = new Gson().fromJson(result, User.class);
            if (user==null) {
                user=new User();
            }
            return user;
        } catch (Exception e) {
            user = new User();
            return user;
        }
    }

    //当前用户的书籍更新时间
    private static final String CUR_LOGIN_USER_BOOKSHELF_UPDATE_TIME = "bookshelf_update_time";

    public static void setCurLoginUserBookshelfUpdateTime(long l) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putLong(CUR_LOGIN_USER_BOOKSHELF_UPDATE_TIME, l);
        editor.commit();
    }

    public static long getCurLoginUserBookshelfUpdateTime() {
        long result = 0;
        result = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getLong("CUR_LOGIN_USER_BOOKSHELF_UPDATE_TIME", 0);
        return result;
    }


    /**
     * 是否接收通知
     */
    private static final String RECEIVE_INFORMATION = "receive_information";

    public static void setReceiveInfomation(boolean isOpen) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putBoolean(RECEIVE_INFORMATION, isOpen);
        editor.commit();
    }

    public static boolean getReceiveInfomation() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getBoolean
                (RECEIVE_INFORMATION, true);
    }

    /**
     * 是否自动订阅
     */
    private static final String AUTO_SUBSCRIPTION = "auto_subscription";

    public static void setAutoSubscription(boolean auto) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putBoolean(AUTO_SUBSCRIPTION, auto);
        editor.commit();
    }

    public static boolean getAutoSubscription() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getBoolean(AUTO_SUBSCRIPTION, false);
    }

    /**
     * 书架删除时未同步成功的bmid
     */
    private static final String BOOKSHELF_DEL_FAIL_BIDS = "bookshelf_del_fail_ids";

    public static void setBookshelfDelFailBids(String bid) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putString(BOOKSHELF_DEL_FAIL_BIDS, getBookshelfDelFailBids() + "," + bid);
        editor.commit();
    }

    public static void clearBookshelfDelFailBids() {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putString(BOOKSHELF_DEL_FAIL_BIDS, "");
        editor.commit();
    }

    public static String getBookshelfDelFailBids() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(BOOKSHELF_DEL_FAIL_BIDS, "");
    }

    public static String[] getBookshelfDelFailBidArray() {
        return getBookshelfDelFailBids().split(",");
    }

    /**
     * 是否是第一次使用app
     */
    private static final String IS_FIRST_START = "is_first_start";

    public static void setIsFirstStart(boolean f) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putBoolean(IS_FIRST_START, f);
        editor.commit();
    }

    public static boolean getIsFirstStart() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getBoolean(IS_FIRST_START, true);
    }

    /**
     * 最后一次更新app
     */
    private static final String LAST_UPDATE_APK_TIME = "last_update_apk_time";

    public static void setLastUpdateApkTime(long time) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putLong(LAST_UPDATE_APK_TIME, time);
        editor.commit();
    }

    public static long getLastUpdateApkTime() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getLong(LAST_UPDATE_APK_TIME, 0);
    }

    /**
     * 是否隐藏最近阅读记录
     */
    private static final String NEED_RECENTLY_RECORD = "hide_lastread_record";

    public static void setNeedRecentlyRecord(boolean hide) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putBoolean(NEED_RECENTLY_RECORD, hide);
        editor.commit();
    }

    public static boolean getNeedRecentlyRecord() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getBoolean(NEED_RECENTLY_RECORD, true);
    }

    /**
     * 默认添加的书籍
     */

    public static final String DEFAULT_SHELF_BOOK = "default_shelf_book";

    public static void setDefaultShelfBook(List<BookTable> bookTableList) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putString(DEFAULT_SHELF_BOOK, new Gson().toJson(bookTableList));
        editor.commit();
    }

    public static List<BookTable> getDefaultShelfBook() {
        List<BookTable> result = new ArrayList<BookTable>();
        String json = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(DEFAULT_SHELF_BOOK, null);
        if (json != null) {
            Type type = new TypeToken<List<BookTable>>() {
            }.getType();
            result = new Gson().fromJson(json, type);
            if (result == null) {
                result = new ArrayList<BookTable>();
            }
        }
        return result;
    }


    /**
     * 最后阅读未正确退出的书籍ID
     */
    public static final String LAST_NOT_EXACT_FINISHED = "last_not_exact_finished";

    public static void setLastNotExactFinished(String bookId) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putString(LAST_NOT_EXACT_FINISHED, bookId);
        editor.commit();
    }

    public static String getLastNotExactFinished() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(LAST_NOT_EXACT_FINISHED, null);
    }

    public static final String SPLASH_RESPONSE = "splash_response";

    public static void setSplashResponse(String response) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putString(SPLASH_RESPONSE, response);
        editor.commit();
    }

    public static String getSplashResponse() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getString(SPLASH_RESPONSE, null);
    }


    /**
     * 是否初始化男女分类
     */
    private static final String IS_INIT_SEX_CLASSIFY = "is_init_sex_classify";

    public static void setIsInitSexClassify(boolean value) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putBoolean(IS_INIT_SEX_CLASSIFY, value);
        editor.commit();
    }

    public static boolean isInitSexClassify() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getBoolean(IS_INIT_SEX_CLASSIFY, false);
    }

    private static final String SEX_CLASSIFY = "sex_classify";      //客户端男女分类

    /**
     * 设置男女分类
     *
     * @param sex 1.女；2.男。
     */
    public static void setSexClassify(int sex) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putInt(SEX_CLASSIFY, sex);
        editor.commit();
    }

    public static int getSexClassify() {
        return AppContext.getInstance().getSharedPreferences(USER_PREF, 0).getInt(SEX_CLASSIFY, 1);
    }


    /**
     * 记录是否在阅读历史页面弹出帮助。
     */
    private static final String SHOW_READ_RECORD_TOAST = "show_readed_record_toast";

    public static void setHasShowReadRecordToast(boolean result) {
        Editor editor = AppContext.getInstance().getSharedPreferences(USER_PREF, 0).edit();
        editor.putBoolean(SHOW_READ_RECORD_TOAST, result);
        editor.commit();
    }

    public static boolean hasShowReadRecordToast() {
        return AppContext.context().getSharedPreferences(USER_PREF, 0).getBoolean(SHOW_READ_RECORD_TOAST, false);
    }


}
