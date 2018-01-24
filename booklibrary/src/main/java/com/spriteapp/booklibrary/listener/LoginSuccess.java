package com.spriteapp.booklibrary.listener;

/**
 * Created by Administrator on 2018/1/22.
 */

public interface LoginSuccess {
    /**
     * @param state 1为登录,2为退出登录
     */
    void loginState(int state);//登录状态

}
