package net.huaxi.reader.model.version;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.tools.commonlibs.tools.LogUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.AppVersion;
import net.huaxi.reader.util.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by ZMW on 2016/5/30.
 */
public class AppVersionService extends Service {
    private AppVersionHelper helper;
    AppVersion appVersion;
    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    private PendingIntent updatePendingIntent;

    private Intent updateIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        initAppVersionHelper();
        if (intent == null) {
            return;
        }

        appVersion = (AppVersion) intent.getSerializableExtra("appversion");
        updateIntent = new Intent(this, AppVersionService.class);
        try {
            updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //状态栏提醒内容
            this.updateNotification = new Notification(R.mipmap.logo_xs, "花溪小说网提醒", System.currentTimeMillis());
            updatePendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, RemoteViews.class), 0);// TODO: 2016/5/30 研究

            //状态栏提醒内容
            updateNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.download_progress);
            updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, 0, false);
            updateNotification.contentView.setTextViewText(R.id.textView1, "进度" + 0 + "%");

            updateNotification.contentIntent = updatePendingIntent;

            // 发出通知
            updateNotificationManager.notify(0, updateNotification);

        } catch (Exception ex) {
            LogUtils.error(ex.getMessage(), ex);
        }

        helper.download(appVersion);
    }

    private void initAppVersionHelper() {
        helper = new AppVersionHelper(this);
        helper.setDownloadListener(new AppDownLoadListenrBase() {
            int downloadCount=0;
            @Override
            public void finsh() {
                super.finsh();
                LogUtils.debug("下载结束");
                EventBus.getDefault().post(new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_APPVERSIONSERVICE, EventBusUtil
                        .EVENTTYPE_VERSIONSERVICE_DOWNLOAD_COMPLETE));
            }

            @Override
            public void downloadProgress(float f) {
                super.downloadProgress(f);

                if(downloadCount < f){
                    downloadCount++;
                    LogUtils.debug("下载进度：" + f);
                    EventBusUtil.EventBean eventBean = new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_APPVERSIONSERVICE, EventBusUtil
                            .EVENTTYPE_VERSIONSERVICE_DOWNLOAD_PROGRESS);
                    eventBean.setDesc(Float.toString(f));
                    EventBus.getDefault().post(eventBean);
                }
            }

            @Override
            public void error(String desc) {
                LogUtils.debug("desc==" + desc);
                super.error(desc);
                EventBus.getDefault().post(new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_APPVERSIONSERVICE, EventBusUtil
                        .EVENTTYPE_VERSIONSERVICE_DOWNLOAD_FILE));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        helper.stop();
        if(!isComplete){
            try {
                updateNotification.contentView.setTextViewText(R.id.textView1, "下载关闭");
                updateNotificationManager.notify(0, updateNotification);
            } catch (Exception ex) {
                LogUtils.error(ex.getMessage(), ex);
            }
        }
    }
    private boolean isComplete=false;


    @Subscribe
    public void onEvent(EventBusUtil.EventBean bean) {
        if (bean != null && bean.getModle() == EventBusUtil.EVENTMODEL_APPVERSIONSERVICE) {
            switch (bean.getType()) {
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_COMPLETE:
                    // TODO: 2016/5/30 完成下载
                    isComplete=true;
                    stopService(updateIntent);
                    updateNotification.contentView.setTextViewText(R.id.textView1, "下载完成");
                    updateNotificationManager.cancel(0);
                    break;
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_FILE:
                    // TODO: 2016/5/30 下载失败
                    // 下载失败
                    try {
                        updateNotification.contentView.setTextViewText(R.id.textView1, "下载失败");
                        updateNotificationManager.notify(0, updateNotification);
                    } catch (Exception ex) {
                        LogUtils.error(ex.getMessage(), ex);
                    }

                    break;
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_PROGRESS:
                    // TODO: 2016/5/30 下载中
                    float progress = Float.parseFloat(bean.getDesc());
                    updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, (int) (progress), false);// TODO: 2016/5/30
                    //TODO 改成true试试
                    updateNotification.contentView.setTextViewText(R.id.textView1, "进度" + (int) (progress) + "%");
                    updateNotification.contentIntent = updatePendingIntent;
                    updateNotificationManager.notify(0, updateNotification);
                    break;
            }
        }
    }


}
