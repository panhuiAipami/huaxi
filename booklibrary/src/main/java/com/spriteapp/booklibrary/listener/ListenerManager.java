package com.spriteapp.booklibrary.listener;

import com.spriteapp.booklibrary.ui.dialog.MyPopupWindow;

/**
 * Created by Administrator on 2018/1/4.
 */

public class ListenerManager {
    public static ListenerManager instance;
    private LoginSuccess loginSuccess;//登录成功
    private DelBookShelf delBookShelf;//删除书架接口
    private HuaWeiPayCallBack huaWeiPayCallBack;//华为支付回调
    private GotoHomePage gotoHomePage;//去首页精选
    private MyPopupWindow.OnButtonClick onButtonClick;


    public static ListenerManager getInstance() {
        if (instance == null) {
            instance = new ListenerManager();
        }
        return instance;
    }

    public MyPopupWindow.OnButtonClick getOnButtonClick() {
        return onButtonClick;
    }

    public void setOnButtonClick(MyPopupWindow.OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
    }

    public LoginSuccess getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(LoginSuccess loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public DelBookShelf getDelBookShelf() {
        return delBookShelf;
    }

    public void setDelBookShelf(DelBookShelf delBookShelf) {
        this.delBookShelf = delBookShelf;
    }

    public HuaWeiPayCallBack getHuaWeiPayCallBack() {
        return huaWeiPayCallBack;
    }

    public void setHuaWeiPayCallBack(HuaWeiPayCallBack huaWeiPayCallBack) {
        this.huaWeiPayCallBack = huaWeiPayCallBack;
    }

    public GotoHomePage getGotoHomePage() {
        return gotoHomePage;
    }

    public void setGotoHomePage(GotoHomePage gotoHomePage) {
        this.gotoHomePage = gotoHomePage;
    }
}
