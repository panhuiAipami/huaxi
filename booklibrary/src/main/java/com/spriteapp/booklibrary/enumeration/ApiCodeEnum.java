package com.spriteapp.booklibrary.enumeration;

/**
 * Created by kuangxiaoguo on 2017/7/20.
 */

public enum ApiCodeEnum {

    SUCCESS(10000);

    private int value;

    ApiCodeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
