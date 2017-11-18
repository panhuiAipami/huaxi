package com.spriteapp.booklibrary.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.util.Constants;
import com.spriteapp.booklibrary.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateAPKService extends Service {
    // 文件存储
    private File updateFile = null;
    // 下载文件的存放路径
    File updateDir;
    private String url = null;
    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    private PendingIntent updatePendingIntent;
    // 通知栏跳转Intent
    private Intent updateIntent = null;

    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;
    public volatile boolean isRunning = false;
    private boolean needBroadcast = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (isRunning) {
            return;
        }
        isRunning = true;
        if (intent == null)
            return;

        // 获取传值
        url = intent.getStringExtra("url");

        // 创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            // 组合下载地址
            updateDir = new File(Constants.SDPath_updata_app);
        } else {
            //files目录
            updateDir = getFilesDir();
        }
        // 拼凑下载文件文件名称
        updateFile = new File(updateDir.getPath(), Util.getVersionName() + ".apk");

        //获取是否需要广播
        needBroadcast = intent.getBooleanExtra("needBroadcase", false);
        updateIntent = new Intent(this, UpdateAPKService.class);
        try {
            updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //状态栏提醒内容
            this.updateNotification = new Notification(R.mipmap.huaxi_icon, "花溪小说", System.currentTimeMillis());
            updatePendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, RemoteViews.class), 0);

            //状态栏提醒内容
            updateNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.progress);
            updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, 0, false);
            updateNotification.contentView.setTextViewText(R.id.textView1, "0%");

            updateNotification.contentIntent = updatePendingIntent;

            // 发出通知
            updateNotificationManager.notify(0, updateNotification);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//		MLog.i("启动下载   :", url);
        // 开启一个新的线程下载
        new Thread(new UpdateRunnable()).start();
    }

    private class UpdateRunnable implements Runnable {
        Message message = updateHandler.obtainMessage();

        public void run() {
            message.what = DOWNLOAD_COMPLETE;
            try {

                sendBroadcase(1, 0);

                if (downloadUpdateFile(url, updateFile)) {
                    // 下载成功
                    updateHandler.sendMessage(message);
                    sendBroadcase(3, 100);
                } else {//// 下载失败
                    message.what = DOWNLOAD_FAIL;
                    updateHandler.sendMessage(message);
                }
            } catch (Exception e) {
                message.what = DOWNLOAD_FAIL;
                // 下载失败
                updateHandler.sendMessage(message);
            } finally {
                isRunning = false;
            }
        }
    }

    //发送相应的广播,下载时如果需要下载状态可以通过接受广播
    private void sendBroadcase(int step, int process) {
        if (needBroadcast) {
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_DOWNLOAD);
            intent.putExtra("step", step);
            intent.putExtra("process", process);
            sendBroadcast(intent);
        }
    }

    /**
     * 下载文件
     *
     * @param downloadUrl
     * @return true:成功，false:失败。
     * @throws Exception
     * @author taoyf
     * @time 2016年8月5日
     */
    public boolean downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;
        boolean result = false;
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;
        // 文件目录是否存在
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }
        // 文件是否存在
        if (!updateFile.exists()) {
            updateFile.createNewFile();
        }
        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(20000);
            httpConnection.setReadTimeout(120000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize; // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
                    downloadCount += 1;
                    try {
                        updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, (int) totalSize * 100 / updateTotalSize, false);
                        updateNotification.contentView.setTextViewText(R.id.textView1, (int) totalSize * 100 / updateTotalSize + "%");
                        updateNotification.contentIntent = updatePendingIntent;
                        updateNotificationManager.notify(0, updateNotification);
                        sendBroadcase(2, (int) totalSize * 100 / updateTotalSize);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
            result = updateTotalSize > 0 && updateTotalSize == totalSize;
            if (!result) { //下载失败或者为下载完成
//				new File(filepath).delete();
            }
        }
        return result;
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:
                    // 点击安装PendingIntent
                    Uri uri;
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d("updateFile",updateFile.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(HomeActivity.libContent, "huaxi", updateFile);
                        Log.d("update_uri",uri.toString());
                    } else {
                        uri = Uri.fromFile(updateFile);
                    }
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    startActivity(installIntent);

                    try {
                        updateNotificationManager.cancel(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    stopService(updateIntent);
                    break;
                case DOWNLOAD_FAIL:
                    // 下载失败
                    try {
                        Notification _notification = new Notification.Builder(UpdateAPKService.this)
                                .setContentTitle("花溪小说")
                                .setContentText("下载失败")
                                .setSmallIcon(R.mipmap.huaxi_icon)
                                .build();
                        updateNotificationManager.notify(0, _notification);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    stopService(updateIntent);
                    break;
                default:
                    stopService(updateIntent);
                    break;
            }
        }
    };
}
