package net.huaxi.reader.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author way
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
    public static String getTimeDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getTextTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return format.format(new Date(time));
    }

    public static String getYearAndMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        return format.format(new Date(time));
    }


    public static String getMonthAndDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd  HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 跟服务器时间一致的当前时间
     *
     * @param time
     * @return
     */
    public static String getNowTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    /**
     * 获取格式如：2010年0月0日  10：00
     *
     * @param time
     * @return
     */
    public static String getTimeFormat(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long millionSeconds = 0;
        try {
            millionSeconds = format.parse(time).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getTextTime(millionSeconds);
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getMonthAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd");
        return format.format(new Date(time));
    }

    public static String getMinAndS(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                result = getTime(timesamp);
                break;
        }

        return result;
    }

    /**
     * @param mss
     * @return 该秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author yanngfu
     */
    public static String formatDuring(long mss) {
        String days = mss / (60 * 60 * 24) + "";
        String hours = (mss % (60 * 60 * 24)) / (60 * 60) + "";
        hours = Integer.parseInt(hours) + Integer.parseInt(days) * 24 + "";
        String minutes = (mss % (60 * 60)) / (60) + "";
        String seconds = (mss % (60)) + "";
        if (Integer.parseInt(hours) < 10) {
            hours = "0" + hours;
        }
        if (Integer.parseInt(minutes) < 10) {
            minutes = "0" + minutes;
        }
        if (Integer.parseInt(seconds) < 10) {
            seconds = "0" + seconds;
        }
        return hours + " : " + minutes + " : " + seconds;
    }


    /**
     * @param mss 要转换的秒数
     * @return String 几点几小时
     * @author panhui
     */
    public static String formatHour(long mss) {
        float days = mss / (60 * 60 * 24);
        float hours = (mss % (60 * 60 * 24)) / (60 * 60);
        hours = hours + days * 24;
        float minutes = (mss % (60 * 60)) / (60);
        float l = hours + minutes / 60;
        return new BigDecimal(l).setScale(1, BigDecimal.ROUND_DOWN) + "";
    }


    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }

    /**
     * 秒转换成界面上显示的时间
     *
     * @param time 时间
     */
    public static String getRunningTime(int time) throws Exception {
        String date = null;
        int h = 0;
        int d = 0;
        int s = 0;

        String h1;
        String d1;
        String s1;
        int temp = time % 3600;
        if (time > 3600) {
            h = time / 3600;
            if (temp != 0) {
                if (temp >= 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = time / 60;
            if (time % 60 != 0) {
                s = time % 60;
            }
        }
        if (h < 10) {
            h1 = "0" + h;
        } else {
            h1 = "" + h;
        }
        if (d < 10) {
            d1 = "0" + d;
        } else {
            d1 = "" + d;
        }
        if (s < 10) {
            s1 = "0" + s;
        } else {
            s1 = "" + s;
        }
        return date = h1 + ":" + d1 + ":" + s1;

    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    /*将字符串转为时间戳*/
    public static String getStringToDate(String time) {
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime() / 1000 + "";
    }
}
