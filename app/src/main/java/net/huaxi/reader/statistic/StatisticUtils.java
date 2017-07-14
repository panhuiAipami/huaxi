package net.huaxi.reader.statistic;

import android.content.Context;
import net.huaxi.reader.BuildConfig;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.Utility;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 统计工具
 * 
 * @author taoyf
 * @time 2015年3月30日
 */
public class StatisticUtils {

	/**
	 * 初始化(必须在软件启动是执行)
	 * 
	 * @author taoyf
	 * @time 2015年3月30日
	 * @param context
	 */
	public static void initSDK(Context context) {
		/* umeng初始化 */
		AnalyticsConfig.setAppkey(Utility.getUmeng_APPKey());
		AnalyticsConfig.setChannel(Utility.getChannel());
		AnalyticsConfig.enableEncrypt(true);
		MobclickAgent.setDebugMode(BuildConfig.DEBUG);
		MobclickAgent.setSessionContinueMillis(Constants.SESSIONCONTINUEMILLIS); // 来自定义这个间隔
	}

	/**
	 * 页面启动统计
	 * 
	 * @author taoyf
	 * @time 2015年3月30日
	 * @param name
	 */
	public static void onPageStart(String name) {
		MobclickAgent.onPageStart(name);
	}

	/**
	 * 页面结束统计
	 * 
	 * @author taoyf
	 * @time 2015年3月30日
	 * @param name
	 */
	public static void onPageEnd(String name) {
		MobclickAgent.onPageEnd(name);
	}

	/**
	 * 统计按钮被点击次数
	 * 
	 * @author taoyf
	 * @time 2015年4月1日
	 * @param eventid
	 * @param value
	 */
	public static void trackButtonEvent(String eventid, String value) {

    }

}
