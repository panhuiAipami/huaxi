package com.spriteapp.booklibrary.util;

/**
 * Created by kuangxiaoguo on 2017/7/20.
 */

public class TimeUtil {

    private static final String TAG = "TimeUtil";

    /**
     * 获取传入时间和当前时间的间隔
     *
     * @return 间隔（h)
     */
    public static int getTimeInterval(long time) {
        long currentTime = System.currentTimeMillis() / 1000;
        long interval = currentTime - time;
        return (int) (interval / (60 * 60));
    }
}
