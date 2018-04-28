package com.spriteapp.booklibrary.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kuangxiaoguo on 2017/7/20.
 */

public class TimeUtil {

    private static final String TAG = "TimeUtil";

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

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
    /**
     * 返回文字描述的日期
     *
     * @param time
     * @return
     */
    public static String getTimeFormatText(long time) {
        if (time == 0)
            return "刚刚";
        Date date = new Date(time);
        if (date == null) {
            return "";
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
//        if (diff > year) {
//            r = (diff / year);
//            return r + "年前";
//        }
//        if (diff > month) {
//            r = (diff / month);
//            return r + "个月前";
//        }
        if (diff > day) {
            r = (diff / day);

            if (r == 1) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                return "昨天 " + format.format(date);
            } else {
                Date currentTime = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy");
                SimpleDateFormat current = new SimpleDateFormat("yyyy");
                if (current.format(currentTime).equals(format.format(date))) {
                    SimpleDateFormat formatMonth = new SimpleDateFormat("MM-dd HH:mm");
                    return formatMonth.format(date);
                } else {
                    SimpleDateFormat formatMonth = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    return formatMonth.format(date);
                }
            }
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }


    public static String getDateHour(long time) {
        SimpleDateFormat ft = new SimpleDateFormat("hh");
        Date dd = new Date(time);
        return ft.format(dd);
    }
    public static String getDateMinute(long time) {
        SimpleDateFormat ft = new SimpleDateFormat("mm");
        Date dd = new Date(time);
        return ft.format(dd);
    }
    public static String getDateSecond(long time) {
        SimpleDateFormat ft = new SimpleDateFormat("ss");
        Date dd = new Date(time);
        return ft.format(dd);
    }







}
