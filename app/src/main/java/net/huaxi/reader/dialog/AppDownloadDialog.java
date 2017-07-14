package net.huaxi.reader.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;
import net.huaxi.reader.common.BroadCastConstant;
import net.huaxi.reader.service.UpdateAPKService;

/**
 * function:    APP下载进度弹窗
 * author:      ryantao
 * create:      16/8/22
 * modtime:     16/8/22
 */
public class AppDownloadDialog extends BaseDialog {


    Activity mActivity;
    TextView textProgress;
    ProgressBar mProgressbar;
    private String url;
    private String progressFormat = "";
    private String fileName;

    public AppDownloadDialog(Activity activity, String url,String filename) {
        mActivity = activity;
        this.url = url;
        this.fileName = filename;
        initDialog(activity, null, R.layout.dialog_download, TYPE_CENTER, true);
        //		mDialog.setCanceledOnTouchOutside(false);
        //		mDialog.setCancelable(false);
        textProgress = (TextView) mDialog.findViewById(R.id.content);
        progressFormat = activity.getString(R.string.download_progress);
        textProgress.setText(String.format(progressFormat, 0));
        mProgressbar = (ProgressBar) mDialog.findViewById(R.id.progressbar);
        mProgressbar.setProgress(0);
        initListener();
        startDownLoadService();
    }

    private void initListener() {

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mActivity != null) {
                    mActivity.unregisterReceiver(receiver);
                }
            }
        });
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (mActivity != null) {
                    mActivity.registerReceiver(receiver, new IntentFilter(BroadCastConstant.ACTION_DOWNLOAD));
                }
            }
        });

    }

    private void startDownLoadService() {
        //String url = "http://192.168.10.16/jiuyuu_debug_v1.0.0.10000_20170412_000.apk";
        Intent intent = new Intent(mActivity, UpdateAPKService.class);
        intent.putExtra("url", url);
        //多传一个参数指定文件路径，为了识别新客户端是否被下载
        intent.putExtra("urlfile",fileName);
        intent.putExtra("needBroadcase", true);
        mActivity.startService(intent);
    }

    private int step = 0;
    private int process = 0;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                step = intent.getIntExtra("step", 0);
                process = intent.getIntExtra("process", 0);
                switch (step) {
                    case 1:
                        //启动了下载
                        textProgress.setText(String.format(progressFormat, process));
                        mProgressbar.setProgress(process);
                        break;
                    case 2:
                        //下载中
                        textProgress.setText(String.format(progressFormat, process));
                        mProgressbar.setProgress(process);
                        break;
                    case 3:
                        //下载
                        textProgress.setText(String.format(progressFormat, process));
                        mProgressbar.setProgress(process);
                        mDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
    };


}
