package net.huaxi.reader.common;

import android.app.Notification;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.message.Log;
import com.umeng.message.MiPushRegistar;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.statistic.StatisticUtils;

import net.huaxi.reader.util.LoginHelper;

public class AppContext extends CommonApp {
	private static String ALIFEEDBACK_APPKEY="23318538";
	private static LoginHelper loginHelper;
	public static LoginHelper getLoginHelper (){
		return loginHelper;
	}
	public static void setLoginHelper( LoginHelper helper){
		loginHelper=helper;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		
		ReportUtils.initSDK(getInstance());

		StatisticUtils.initSDK(getInstance());

		initUmengMessage();
		initAliFeedBack();
		MobclickAgent.openActivityDurationTrack(false);
	}

	private void initAliFeedBack() {
		//匿名反馈初始化方式
		FeedbackAPI.initAnnoy(getInstance(), ALIFEEDBACK_APPKEY);
		//openim账号初始化方式
//		FeedbackAPI.initOpenImAccount(getInstance(),ALIFEEDBACK_APPKEY, String uid, String pw)
	}

	private void initUmengMessage(){
		PushAgent mPushAgent=PushAgent.getInstance(this);
		mPushAgent.setDebugMode(true);

		UmengMessageHandler messageHandler = new UmengMessageHandler(){
			@Override
			public void dealWithCustomMessage(Context context, UMessage uMessage) {
				Log.e("uTag", "dealWithCustomMessage");
				super.dealWithCustomMessage(context, uMessage);
			}
			@Override
			public void dealWithNotificationMessage(Context context, UMessage uMessage){
				LogUtils.debug("dealWithNotificationMessage");
				if(SharePrefHelper.getReceiveInfomation()){
					LogUtils.debug("dealWithNotificationMessage send");
					super.dealWithNotificationMessage(context,uMessage);
				}
			}

			@Override
			public Notification getNotification(Context context, UMessage uMessage) {
				Log.e("uTag", "getNotification");
				return super.getNotification(context, uMessage);
			}
		};
		mPushAgent.setMessageHandler(messageHandler);

		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 * 参考集成文档的1.6.2
		 * http://dev.umeng.com/push/android/integration#1_6_2
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
			@Override
			public void dealWithCustomAction(Context context, UMessage msg) {
				Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
			}
		};
		//使用自定义的NotificationHandler，来结合友盟统计处理消息通知
		//参考http://bbs.umeng.com/thread-11112-1-1.html
		//CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
		mPushAgent.setNotificationClickHandler(notificationClickHandler);

		if (MiPushRegistar.checkDevice(this)) {
			MiPushRegistar.register(this, "2882303761517400865", "5501740053865");
		}
	}
}
