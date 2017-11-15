package com.spriteapp.booklibrary.enumeration;

/**
 * Created by kuangxiaoguo on 2017/7/20.
 */

public enum BookEnum {

    //未加入书架
    NOT_ADD_SHELF(0),
    //加入书架
    ADD_SHELF(1),

    //10000 我的书籍
    MY_SHELF_DATA(10000),
    //12011	获取书架时显示推荐
    RECOMMEND_DATA(12011),

    //书已完结
    BOOK_FINISH_TAG(1),

    MY_BOOK(0),
    RECOMMEND_BOOK(1);

    private int value;

    BookEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
