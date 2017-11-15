package com.spriteapp.booklibrary.enumeration;

/**
 * 获取token时登录方式判断
 * Created by kuangxiaoguo on 2017/7/13.
 */

public enum LoginModeEnum {

    CHANNEL_LOGIN("/login_channel"),
    WECHAT_LOGIN("/login_wechat"),
    QQ_LOGIN("/login_qq"),
    WEIBO_LOGIN("/login_weibo");

    private String value;

    LoginModeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
