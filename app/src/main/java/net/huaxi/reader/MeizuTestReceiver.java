package net.huaxi.reader;

import android.content.Context;
import android.util.Log;

import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.umeng.message.meizu.UmengMeizuPushReceiver;

import java.util.Arrays;

/**
 * Created by panhui on 2018/5/23.
 */

public class MeizuTestReceiver extends UmengMeizuPushReceiver {
    @Override
    //接收服务器推送的透传消息
    public void onMessage(Context context, String s) {
        Log.i("bqt", "魅族【onMessage】" + s);
    }

    @Override
    //注册。调用PushManager.register(context方法后，会在此回调注册状态应用在接受返回的pushid
    public void onRegister(Context context, String pushid) {
        Log.i("bqt", "魅族【onRegister】" + pushid);
    }

    @Override
    //取消注册。调用PushManager.unRegister(context）方法后，会在此回调反注册状态
    public void onUnRegister(Context context, boolean b) {
        Log.i("bqt", "魅族【onUnRegister】" + b);
    }

    @Override
    //设置通知栏小图标。重要！详情参考应用小图标自定设置
    public void onUpdateNotificationBuilder(PushNotificationBuilder builder) {
        Log.i("bqt", "魅族【onUpdateNotificationBuilder】" + builder.getmNotificationsound() + "  "
                + builder.getmLargIcon() + "  " + builder.getmNotificationDefaults() + "  " + builder.getmNotificationFlags() + "  "
                + builder.getmStatusbarIcon() + "  " + Arrays.toString(builder.getmVibratePattern()));
        builder.setmStatusbarIcon(R.drawable.stat_sys_third_app_notify);
    }

    @Override
    //检查通知栏和透传消息开关状态回调
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
        Log.i("bqt", "魅族【onPushStatus】" + pushSwitchStatus.toString());
    }

    @Override
    //调用新版订阅PushManager.register(context,appId,appKey)回调
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        Log.i("bqt", "魅族【onRegisterStatus】" + registerStatus.toString());
    }

    @Override
    //新版反订阅回调
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
        Log.i("bqt", "魅族【onUnRegisterStatus】" + unRegisterStatus.toString());
    }

    @Override
    //标签回调
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
        Log.i("bqt", "魅族【onSubTagsStatus】" + subTagsStatus.toString());
    }

    @Override
    //别名回调
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        Log.i("bqt", "魅族【onSubAliasStatus】" + subAliasStatus.toString());
    }

}
