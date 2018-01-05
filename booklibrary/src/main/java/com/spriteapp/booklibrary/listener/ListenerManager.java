package com.spriteapp.booklibrary.listener;

/**
 * Created by Administrator on 2018/1/4.
 */

public class ListenerManager {
    public static ListenerManager instance;
    private ReadActivityFinish readActivityFinish;

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
}
