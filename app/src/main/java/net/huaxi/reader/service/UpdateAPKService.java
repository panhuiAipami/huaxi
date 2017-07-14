package net.huaxi.reader.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.common.BroadCastConstant;

import net.huaxi.reader.R;

public class UpdateAPKService extends Service {

	// 标题
	//	private int titleId = 0;
	// 文件存储
	private String downloadFile = "";
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
		LogUtils.info("启动下载   :" + url);

		// 创建文件
		downloadFile = intent.getStringExtra("urlfile");

		//获取是否需要广播
		needBroadcast = intent.getBooleanExtra("needBroadcase", false);
		updateIntent = new Intent(this, UpdateAPKService.class);
		try {
			updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			//状态栏提醒内容
			this.updateNotification = new Notification(R.mipmap.logo_xs, "小说阅读网提醒", System.currentTimeMillis());
			updatePendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, RemoteViews.class), 0);

			//状态栏提醒内容
			updateNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.progress);
			updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, 0, false);
			updateNotification.contentView.setTextViewText(R.id.textView1, "0%");

			updateNotification.contentIntent = updatePendingIntent;

			// 发出通知
			updateNotificationManager.notify(0, updateNotification);
		} catch (Exception ex) {
			LogUtils.error(ex.getMessage(), ex);
		}
		// 开启一个新的线程下载
		new Thread(new UpdateRunnable()).start();
	}

	private class UpdateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {

				sendBroadcase(1, 0);

				if (downloadUpdateFile(url, downloadFile)) {
					// 下载成功
					updateHandler.sendMessage(message);
					sendBroadcase(3, 100);
				} else {//// 下载失败
					message.what = DOWNLOAD_FAIL;
					updateHandler.sendMessage(message);
				}
			} catch (Exception e) {
				LogUtils.error(e.getMessage(), e);
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
			intent.setAction(BroadCastConstant.ACTION_DOWNLOAD);
			intent.putExtra("step", step);
			intent.putExtra("process", process);
			sendBroadcast(intent);
		}
	}

	/**
	 * 下载文件
	 * @author taoyf
	 * @time 2016年8月5日
	 * @param downloadUrl
	 * @return	true:成功，false:失败。
	 * @throws Exception
	 */
	public boolean downloadUpdateFile(String downloadUrl, String filepath) throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;
		boolean result = false;
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		File temp = new File(filepath + ".tmp");
		if (temp.getParentFile().isDirectory()) {
			temp.getParentFile().mkdirs();
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
			fos = new FileOutputStream(temp, false);
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
						LogUtils.error(ex.getMessage(), ex);
					}
				}
			}
			temp.renameTo(new File(filepath));
			temp.delete();
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
				new File(filepath).delete();
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
				Uri uri = Uri.fromFile(new File(downloadFile));
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				startActivity(installIntent);

				try {
					updateNotificationManager.cancel(0);
				} catch (Exception ex) {
					LogUtils.error(ex.getMessage(), ex);
				}
				stopService(updateIntent);
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				try {
                    Notification _notification = new Notification.Builder(UpdateAPKService.this)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("下载失败")
                            .setSmallIcon(R.mipmap.logo_xs)
                            .build();
					updateNotificationManager.notify(0, _notification);
				} catch (Exception ex) {
					LogUtils.error(ex.getMessage(), ex);
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
