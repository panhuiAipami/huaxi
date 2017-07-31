package net.huaxi.reader.thread;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.android.volley.toolbox.RequestFuture;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.PhoneUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.Utils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.AppVersion;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.dialog.AppDownloadDialog;
import net.huaxi.reader.dialog.CommonDailog;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.model.version.AppVersionHelper;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 检查更新APP异步任务
 * taoyingfeng
 * 2016/2/19.
 */
public class AppCheckUpdateTask extends EasyTask<Activity, Void, Void, AppVersion> {
    private int build;
    private boolean flag=false;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public AppCheckUpdateTask(Activity caller) {
        super(caller);
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    public AppVersion doInBackground(Void... params) {
        return checkVersion();
    }

    @Override
    public void onPostExecute(final AppVersion appVersion) {
        super.onPostExecute(appVersion);
//        Log.i("wwww", "onPostExecute: "+appVersion.toString());
        LogUtils.debug("getVersion—>"+PhoneUtils.getVersionCode());
        if(appVersion != null){
            build=Integer.parseInt(appVersion.getBuild());
        }

        if (appVersion != null && build > PhoneUtils.getVersionCode()&&!appVersion.getVersionName().equals(PhoneUtils.getVersionName())) {
            CommonDailog _dailog = new CommonDailog(caller,
                    AppContext.getInstance().getString(R.string.version_update),
                    getContent(appVersion),
                    AppContext.getInstance().getString(R.string.update_ok),
                    AppContext.getInstance().getString(R.string.update_cancel));
            if (appVersion.isOptional()) {

                _dailog.dismissCancel();
                _dailog.setCanceledOnTouchOutside(false);_dailog.setCancelable(false);
            }
            _dailog.setCommonDialogListener(new CommonDailog.CommonDialogListener() {
                @Override
                public void ok(Dialog dialog) {
                    if(!NetUtils.checkNetworkUnobstructed()){
                        caller.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_available_network));
                            }
                        });
                        return;
                    }
                    String urlString = "";
                    LogUtils.debug("here 12");
                    LogUtils.debug("here 121"+ appVersion.getUrls());
                    if (appVersion.getUrls() != null && appVersion.getUrls().size() > 0) {
                        for (String url : appVersion.getUrls()) {
                            if (StringUtils.isNotEmpty(url)) {
                                urlString = url;
                                LogUtils.debug("urlString--->"+url);
                                break;
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(urlString)) {
                        AppDownloadDialog _appDownloadDialog = new AppDownloadDialog(caller, urlString,appVersion.getFilePath());
                        _appDownloadDialog.show();
                    }else{
                        Utils.gotoMarket(caller, PhoneUtils.getPackageName());
                    }
                    dialog.dismiss();
                }

                @Override
                public void cancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            _dailog.show();
        }
        if(appVersion.getVersionName().equals(PhoneUtils.getVersionName())){
            if(flag){
                Toast.makeText(AppContext.context(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
//                Log.i("TTT", "onPostExecute: 当前已是最新版本");
                flag=false;
            }
        }
    }

    private AppVersion checkVersion(){
//        Log.i("jiejie", "checkVersion: 方法被调用");
        AppVersion appVersion= null;
        String url = URLConstants.APP_CHECK_UPDATE_URL;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        GetRequest request = new GetRequest(url,future,future);
        RequestQueueManager.addRequest(request);
        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
//            Log.i("eeee", "checkVersion: "+response.toString());
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


    private String getContent(AppVersion appVersion){
        StringBuffer sb = new StringBuffer();
        if (appVersion != null) {
            sb.append("版本：" + appVersion.getVersionName() + "\n");
            if (appVersion.getFeatures() != null && appVersion.getFeatures().size() > 0) {
                for (int i = 0; i < appVersion.getFeatures().size(); i++) {
                    sb.append((i+1)+"、" + appVersion.getFeatures().get(i)+"\n");
                }
            }
        }
        LogUtils.debug("version---->"+sb.toString());
        return sb.toString();

    }

}
