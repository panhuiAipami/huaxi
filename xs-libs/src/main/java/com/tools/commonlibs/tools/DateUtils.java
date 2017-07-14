package com.tools.commonlibs.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 日期工具类
 */
public class DateUtils {
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat format_str = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat format_hm = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat format_str2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
	// 时间
	public static long SECOND = 1000;// 秒
	public static long MINUTE = SECOND * 60;// 分
	public static long HOUR = MINUTE * 60;// 时
	public static long DAY = HOUR * 24;// 天

	public static String format(Date date) {
		return format.format(date);
	}

	public static String format2(Date date) {
		return format_str2.format(date);
	}

	public static String formatHM(Date date) {
		return format_hm.format(date);
	}

	public static String format(long time) {
		if (String.valueOf(time).length() == 10)
			time = time * 1000;
		return format(new Date(time));
	}

	public static String getCurrentTime() {
		try {
			Date date = new Date();
			return format_str.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0000000000";
	}

	public static String formatStr2(long time) {
		try {
			if (String.valueOf(time).length() == 10)
                time = time * 1000;
			return format2(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static long getTime(String dateStr) {

		try {
			Date date = format.parse(dateStr);

			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static String now() {
		return format.format(new Date());
	}

	/**
	 * 是否是同一天
	 * 
	 * @return
	 */
	public static boolean isSameDay(long data1, long data2) {
		try {
			Date date1 = new Date(data1);
			Date date2 = new Date(data2);

			return isSameDay(date1, date2);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * 是否是同一天
	 * 
	 * @return
	 */
	public static boolean isSameDay(String dateStr1, String dateStr2) {
		try {
			Date date1 = format.parse(dateStr1);
			Date date2 = format.parse(dateStr2);

			return isSameDay(date1, date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * 是否是同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		} else {
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date1);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);
			return isSameDay(cal1, cal2);
		}
	}

	/**
	 * time以内
	 * 
	 * @param dateStr
	 * @return
	 */
	public static boolean isInside(String dateStr, long time) {

		long last = DateUtils.getTime(dateStr);
		long now = System.currentTimeMillis();
		if ((now - last) < time)
			return true;

		return false;
	}

	/**
	 * 一小时以外
	 * 
	 * @param dateStr
	 * @return
	 */
	public static boolean isOutHour(String dateStr, long time) {
		long last = DateUtils.getTime(dateStr);
		long now = System.currentTimeMillis();
		if ((now - last) >= time)
			return true;

		return false;
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null)
			throw new IllegalArgumentException("The date must not be null");
		else
			return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
					&& cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
	}

	public static boolean after(Date date) {
		long time = date.getTime();
		long now = System.currentTimeMillis();
		if (now > time)
			return true;

		return false;
	}

	/**
	 * 定时任务，精确到秒
	 * 
	 * @param runnable
	 *            执行的任务
	 * @param hour
	 *            时（24小时制）
	 * @param minute
	 *            分
	 * @param second
	 *            秒
	 */
	public static void schedule(final Runnable runnable, final int hour, final int minute, final int second) {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);

		schedule(runnable, cal.getTime());
	}

	/**
	 * 定时任务
	 * 
	 * @param runnable
	 *            执行的任务
	 * @param date
	 *            执行的时间
	 */
	public static Timer schedule(final Runnable runnable, final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		Timer mTimer = new Timer(false);// 非守候线程
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				Calendar now = Calendar.getInstance();
				now.add(Calendar.MINUTE, -1);// 允许1分钟误差

				// 首次执行时间过了首次不执行
				if (now.after(cal)) {
					LogUtils.info("时间过了首次不执行");
					return;
				}

				runnable.run();// 执行操作
			}
		};

		mTimer.schedule(timerTask, cal.getTime());

		return mTimer;
	}

	public static Timer schedule(final Runnable runnable, final Date date, long period) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar now = Calendar.getInstance();
		Date d = cal.getTime();
		if (now.after(cal)) {
			d = addDay(d, 1);
		}
		Timer mTimer = new Timer(false);// 非守候线程
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				runnable.run();// 执行操作
			}
		};

		mTimer.scheduleAtFixedRate(timerTask, d, period);

		return mTimer;
	}

	// 增加或减少天数
	public static Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}

	/**
	 * 得到当前时间戳
	 * 
	 * @return
	 */
	public static String getStrTimestamp() {

		return getStrTimestamp(10);

	}

	/**
	 * 得到当前时间戳
	 * 
	 * @return
	 */
	public static String getStrTimestamp(int maxLength) {
		String t = String.valueOf(System.currentTimeMillis());
		t = t.substring(0, Math.min(maxLength, t.length()));

		return t;

	}

	/**
	 * 得到当前时间戳
	 * 
	 * @return
	 */
	public static long getTimestamp() {

		return getTimestamp(10);

	}

	/**
	 * 得到当前时间戳
	 * 
	 * @return
	 */
	public static long getTimestamp(int maxLength) {
		String t = String.valueOf(System.currentTimeMillis());
		t = t.substring(0, Math.min(maxLength, t.length()));
		return Long.valueOf(t);
	}

	/**
	 * 获取当前0区时间
	 * @return
	 */
	public static long getNowTimeStamp(){
		Calendar calendar = Calendar.getInstance();
		// 获取当前时区下日期时间对应的时间戳
		long unixTime = calendar.getTimeInMillis();
		// 获取标准格林尼治时间下日期时间对应的时间戳
		long unixTimeGMT = unixTime;
		return unixTimeGMT;
	}

	/**
	 * 根据0区时间获取当前时间戳
	 * @return
	 */
	public static long getGMT0TimeStamp(long time){
		return time + TimeZone.getDefault().getRawOffset();
	}


	/**
	 * 获取更改时区后的日期
	 *
	 * @param date    日期
	 * @param oldZone 旧时区对象
	 * @param newZone 新时区对象
	 * @return 日期
	 */
	public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
		Date dateTmp = null;
		if (date != null) {
			int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
			dateTmp = new Date(date.getTime() - timeOffset);
		}
		return dateTmp;
	}


	/**
	 * 默认是本地时区进行格式化
	 * @param time
	 * @param format
	 * @return
	 */
	public static String simpleDateFormat(Date time,String format){
		String result=null;
		SimpleDateFormat fm=new SimpleDateFormat(format);
		result =fm.format(time);
		return result;
	}

	/**
	 * 对时间进行格式化，根据时间返回不同的时间区段“刚刚” “11：23”等等
	 *
	 * @param commentTime
	 * @return
	 */
	public static String timeFormat(String commentTime) {
		String interval = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = format.parse(commentTime);
			long createtime = date.getTime();

			long time = Math.abs(System.currentTimeMillis() - createtime); // 得出的时间间隔是毫秒

			if (time / 1000 < 60 && time / 1000 >= 0) {
				// 如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
				interval = "刚刚";

			} else if (time / 1000 >= 60 && time / 1000 / 60 < 60) {

				interval = (int) (time / 1000 / 60) + "分钟前";

			} else if (time / 1000 / 60 >= 60 && time / 1000 / 60 / 60 < 24) {
				// 如果时间间隔小于24小时则显示多少小时前
//                int h = (int) (time / 1000 / 60 / 60);// 得出的时间间隔的单位是小时
//                interval = h + "小时前";
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				interval = sdf.format(createtime);

			} else {
				// 大于24小时，则显示正常的时间，但是不显示秒
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				interval = sdf.format(createtime);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return interval;
	}


}
