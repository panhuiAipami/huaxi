package net.huaxi.reader.http;

/**
 * Created by Administrator on 2017/11/15.
 */

public class UrlUtils {
    //true测试环境，上线需改成false
    public static final boolean IS_DEV = true;
    //域名
    public static final String BASE_URL = "https://s.hxdrive.net/";
    /**
     * 微信登录
     */
    public static final String LOGIN_WECHAT = "login_wechat";
    /**
     * 手机号注册
     */
    public static final String LOGIN_REGISTER = "login_register";
    /**
     * 手机号登录
     */
    public static final String LOGIN_ACCOUNT = "login_account";
    /**
     * 手机号登录
     */
    public static final String HWLOGIN = "login_huawei";
    /**
     * 上报推送token
     */
    public static final String PUSH_GET = "push_token";
    /**
     * 上报推送id
     */
    public static final String PUSH_CALL = "push_call";
}
