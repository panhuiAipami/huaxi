package net.huaxi.reader.model;

import android.app.Activity;
import android.content.Intent;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import net.huaxi.reader.util.UMEventAnalyze;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.SimpleWebViewActivity;

/**
 * Created by Saud on 16/1/29.
 */
public class DownloadDailogHelp {


    private Activity activity;
    private MaterialDialog progressDailog;
    private MaterialDialog buyDailog;
    private MaterialDialog progressDeterminateDialog;
    private MaterialDialog downloadErrorDiolog;

    public DownloadDailogHelp(Activity activity) {
        this.activity = activity;
    }

    /**
     * 显示加载进度条
     *
     * @param horizontal
     */
    public void showProgressDialog(boolean horizontal) {
        progressDailog = new MaterialDialog.Builder(activity)
                .content(R.string.download_please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontal)
                .show();

    }

    /**
     * 关闭加载进度条
     */
    public void dismissProgressDialog() {
        if (progressDailog != null) {
            progressDailog.dismiss();
        }
    }


    /**
     * 显示提示购买
     */
    public void showBuyDialog(int errorid) {

        buyDailog = new MaterialDialog.Builder(activity)
                .content(activity.getString(R.string.need_more_coin)+"("+errorid+")")
                .positiveText(R.string.go_bug)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //去购买页面
                        Intent intent = new Intent(activity, SimpleWebViewActivity.class);
                        intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_RECHARGE);
                        activity.startActivity(intent);
                        UMEventAnalyze.countEvent(activity, UMEventAnalyze.DOWNLOAD_RECHARGE);
                    }
                })
                .show();
    }

    /**
     * 关闭购买提示对话框
     */
    public void dismissBuyDialog() {
        if (buyDailog != null) {
            buyDailog.dismiss();
        }
    }


    /**
     * 显示下载进度的对话框
     */
    public MaterialDialog showProgressDeterminateDialog(int max) {
        progressDeterminateDialog = new MaterialDialog.Builder(activity)
                .content(R.string.download_please_waiting)
                .progress(false, max, true)
                .cancelable(false)
//                .positiveText(R.string.download_in_background)
                .negativeText(R.string.download_cancel)
                .show();
        return progressDeterminateDialog;
    }

    /**
     * 关闭下载进度的对话框
     */
    public void dissmissProgressDeterminateDialog() {
        if (progressDeterminateDialog != null) {
            progressDeterminateDialog.dismiss();
        }

    }

    /**
     * 关闭下载进度的对话框
     */
    public void showProgressDeterminateDialog() {
        if (progressDeterminateDialog != null) {
            progressDeterminateDialog.show();
        }

    }


    /**
     * 显示下载失败信息
     */
    public MaterialDialog showDownloadErrorDiolog(String content) {
        downloadErrorDiolog = new MaterialDialog.Builder(activity)
                .content(content)
                .positiveText(R.string.download_try_again)
                .negativeText(R.string.cancel)
                .show();
        return downloadErrorDiolog;
    }

    /**
     * 关闭购买提示对话框
     */
    public void dismissDownloadErrorDiolog() {
        if (downloadErrorDiolog != null) {
            downloadErrorDiolog.dismiss();
        }
    }

}
