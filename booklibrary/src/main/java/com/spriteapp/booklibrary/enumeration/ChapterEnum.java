package com.spriteapp.booklibrary.enumeration;

/**
 * Created by kuangxiaoguo on 2017/7/18.
 */

public enum ChapterEnum {

    /**
     * 12007	付费章节需要订阅,已登录，但是没有订阅	failure
     * 12009	付费章节订阅时需要登录，未登录状态	failure
     * 12010    付费章节订阅时余额不足
     */
    //11002未登录

    UN_SUBSCRIBER(12007),
    UN_LOGIN(12009),
    BALANCE_SHORT(12010),

    USER_UN_LOGIN(11002),

    CHAPTER_IS_VIP(1),

    DO_NOT_NEED_BUY(0),
    NEED_BUY(1),

    //显示付费章节dialog，余额充足可自动扣费
    AUTO_BUY(0),
    //显示余额不足dialog
    TO_PAY_PAGE(1),

    //章节未读
    NOT_READ(0),
    //章节已读
    HAS_READ(1);

    private int code;

    ChapterEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
