package com.spriteapp.booklibrary.listener;

/**
 * Created by Administrator on 2018/1/4.
 */

public class ListenerManager {
    public static ListenerManager instance;
    private ReadActivityFinish readActivityFinish;
    private LoginSuccess loginSuccess;
    private DelBookShelf delBookShelf;

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
}
