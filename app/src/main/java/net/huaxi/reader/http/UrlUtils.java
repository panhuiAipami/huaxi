package net.huaxi.reader.http;

/**
 * Created by Administrator on 2017/11/15.
 */

public class UrlUtils {
    //true测试环境，上线需改成false
    public static final boolean IS_DEV = true;
    //打华为包需为true，否则为false（华为渠道需要用华为支付）
    public static final boolean CHANNEL_IS_HUAWEI = true;
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
}
