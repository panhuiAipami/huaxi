package com.spriteapp.booklibrary;

import android.app.Application;

/**
 * Created by Administrator on 2017/11/15.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static void setInstance(MyApplication mInstance) {
        MyApplication.mInstance = mInstance;
    }

}
