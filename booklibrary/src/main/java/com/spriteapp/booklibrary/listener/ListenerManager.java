package com.spriteapp.booklibrary.listener;

/**
 * Created by Administrator on 2018/1/4.
 */

public class ListenerManager {
    public static ListenerManager instance;
    private ReadActivityFinish readActivityFinish;//阅读完成
    private LoginSuccess loginSuccess;//登录成功
    private DelBookShelf delBookShelf;//删除书架接口
    private HuaWeiPayCallBack huaWeiPayCallBack;//华为支付回调
    private GotoHomePage gotoHomePage;//去首页精选

    public static ListenerManager getInstance() {
        if (instance == null) {
            instance = new ListenerManager();
        }
        return instance;
    }

    public ReadActivityFinish getReadActivityFinish() {
        return readActivityFinish;
    }

    public void setReadActivityFinish(ReadActivityFinish readActivityFinish) {
        this.readActivityFinish = readActivityFinish;
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
