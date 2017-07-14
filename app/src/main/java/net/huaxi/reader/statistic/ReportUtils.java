package net.huaxi.reader.statistic;

import android.content.Context;
import net.huaxi.reader.BuildConfig;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.Utility;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.PhoneUtils;

/**
 * 日志上报工具
 * 
 * @author taoyf
 * @time 2015年3月30日
 */
public class ReportUtils {

	/**
	 * 初始化SDK.
	 * 
	 * @author taoyf
	 * @time 2015年3月28日
	 * @param context
	 */
	public static void initSDK(Context context) {
		/* bugly初始化 */
		UserStrategy strategy = new UserStrategy(context); // App的策略Bean
        strategy.setAppChannel(Utility.getChannel());// 设置渠道
        strategy.setAppVersion(PhoneUtils.getVersionName()); // 版本号
        strategy.setAppReportDelay(5000);// 设置SDK处理延时，毫秒
        strategy.setDeviceID(PhoneUtils.getPhoneImei(context));
        // 初始化buglySDK
        if (!BuildConfig.LOG_DEBUG) {
            CrashReport.initCrashReport(context, Utility.getBuglyAppId(), false, strategy);  //默认关闭debug模式.
            CrashReport.setUserId(UserHelper.getInstance().getUserId()); // 设置登录用户
        }

	}

	/**
	 * 测试
	 * 
	 * @author taoyf
	 * @time 2015年3月28日
	 */
	public static void testJavaCrash() {
		CrashReport.testJavaCrash();
	}


	/**
	 * 设置场景标签
	 * @param tag
	 */
	public static void setUserSceneTag(int tag){
		try {
            if (!BuildConfig.LOG_DEBUG) {
                CrashReport.setUserSceneTag(AppContext.getInstance(), tag); // 上报后的Crash会显示该标签
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上报异常日志.（禁用UMENG上传异常日志）
	 * 
	 * @author taoyf
	 * @time 2015年3月30日
	 * @param t
	 */
	public static void reportError(Throwable t) {
		try {
            LogUtils.error(t.getMessage(),t);
			if (!BuildConfig.LOG_DEBUG) {
                CrashReport.postCatchedException(t);
			}

		} catch (Exception e) {
			LogUtils.error(e.getMessage(), e);
		}

		try {
			//// TODO: 16/2/25 由于代码混淆后，友盟需要上传mapping.txt,所以我们只采用自动上传mapping.txt的bugly上传日志。
//			MobclickAgent.reportError(CommonApp.getInstance(), t);
		} catch (Exception e) {
			LogUtils.error(e.getMessage(), e);

		}

	}
}
