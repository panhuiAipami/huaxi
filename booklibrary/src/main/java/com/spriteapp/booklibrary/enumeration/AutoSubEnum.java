package com.spriteapp.booklibrary.enumeration;

/**
 * 章节是否自动订阅
 * Created by kuangxiaoguo on 2017/7/19.
 */

public enum AutoSubEnum {

    NOT_AUTO_SUB(0),
    AUTO_SUB(1);

    private int value;

    AutoSubEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
