package com.spriteapp.booklibrary.enumeration;

/**
 * Created by kuangxiaoguo on 2017/7/20.
 */

public enum ApiCodeEnum {

    SUCCESS(10000),
    //点赞,阅读数失败
    FAILURE(10001),
    //点赞,阅读曾经点过或阅读过
    EVER(10002),
    //无发帖权限
    NOAUTHORITY(10005),
    //未登录
    NOLOGIN(11002);
    private int value;


    ApiCodeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
