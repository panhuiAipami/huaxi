package net.huaxi.reader.thread;

import android.app.Activity;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.RequestFuture;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.PhoneUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.bean.AppVersion;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.model.version.AppVersionHelper;
import net.huaxi.reader.model.version.AppVersionService;
import net.huaxi.reader.util.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ZMW on 2016/6/2.
 */
public class AppUpdateTask extends EasyTask<Activity, Void, Void, AppVersion> {
    MaterialDialog progressDeterminateDialog;//显示下在进度的dialog
    MaterialDialog downloadDialog;//是否下载的dialog
    MaterialDialog installDialog;//是否安装的dialog
    Intent downloadIntent;//去下载服务的意图
    private boolean forceUpdate;//是否强制更新
    public AppUpdateTask(Activity activity) {
        super(activity);
    }

    @Override
    public void onPreExecute() {
        EventBus.getDefault().register(this);
        super.onPreExecute();
    }


    @Override
    public void onPostExecute(AppVersion appVersion) {
        super.onPostExecute(appVersion);
        forceUpdate=appVersion.isOptional();
        int build=Integer.parseInt(appVersion.getBuild());
        if (new File(appVersion.getFilePath()).exists()) {
            if(build!= PhoneUtils.getVersionCode()){
                initProgressDialog();
            }else{
                ViewUtils.toastShort("已为最新");
            }
        } else {
            downloadIntent = new Intent(caller, AppVersionService.class);
            downloadIntent.putExtra("appversion", appVersion);
            caller.startService(downloadIntent);
            initDownlaodDialog();
        }


    }

    private void unregeter(){
        EventBus.getDefault().unregister(this);
    }

    @Override
    public AppVersion doInBackground(Void... params) {
        AppVersion appVersion= null;
//        String url = "http://172.16.0.192/h1/2";

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        GetRequest request = new GetRequest(URLConstants.APP_CHECK_UPDATE_URL,future,future);
        RequestQueueManager.addRequest(request);
        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
            appVersion= AppVersionHelper.parseToAppVersion(response);
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    @Subscribe
    public void onEvent(EventBusUtil.EventBean bean) {
        if (bean != null && bean.getModle() == EventBusUtil.EVENTMODEL_APPVERSIONSERVICE) {
            switch (bean.getType()) {
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_COMPLETE:
                    LogUtils.debug("下载完成");
                    unregeter();
                    break;
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_FILE:
                    LogUtils.debug("下载失败");
                    // 下载失败
                    unregeter();
                    break;
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_PROGRESS:
                    float progress = Float.parseFloat(bean.getDesc());
                    LogUtils.debug("下载中:"+progress);
                    break;
            }
        }
    }



    private void initProgressDialog(){
        closeOtherDialog();
    }
    private void initDownlaodDialog(){
        closeOtherDialog();
        if(forceUpdate){

        }

    }
    private void initInstallDialog(){
        closeOtherDialog();
    }

    private void closeOtherDialog(){
        if(progressDeterminateDialog!=null && progressDeterminateDialog.isShowing()){
            progressDeterminateDialog.cancel();
        }
        if(installDialog!=null && installDialog.isShowing()){
            installDialog.cancel();
        }
        if(downloadDialog!=null && downloadDialog.isShowing()){
            downloadDialog.cancel();
        }
    }


}
