package com.tools.commonlibs.common;

import android.app.Application;


/**
 * @author taoyf
 * @time 2015年1月8日
 */
public class CommonApp extends BaseApplication {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static Application getInstance() {
        return instance;
    }


}
