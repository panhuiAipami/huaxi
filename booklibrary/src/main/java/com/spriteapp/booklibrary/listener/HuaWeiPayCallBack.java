package com.spriteapp.booklibrary.listener;

/**
 * Created by userfirst on 2018/3/1.
 */

public interface HuaWeiPayCallBack {
    /**
     * @param productPrice 价格
     * @param orderId
     * @param key
     */
    void info(double productPrice, String orderId, String key);
}
