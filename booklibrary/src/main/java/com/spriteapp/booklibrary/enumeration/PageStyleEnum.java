package com.spriteapp.booklibrary.enumeration;

/**
 * Created by kuangxiaoguo on 2017/7/19.
 */

public enum PageStyleEnum {

    DEFAULT_STYLE(0),
    FLIP_STYLE(1);

    private int value;

    PageStyleEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
