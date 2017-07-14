package net.huaxi.reader.model.version;

import android.content.Context;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.bean.AppVersion;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.download.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.https.download.TaskManagerDelegate;
import net.huaxi.reader.https.download.TaskStateEnum;
import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;

/**
 * Created by ZMW on 2016/5/26.
 */
public class AppVersionHelper {

    private Context context;
    private AppDownloadListener downloadListener;
    public final static String downloadPath =
            AppContext.getInstance().getExternalCacheDir().getAbsolutePath() + File.separator;
    private Task task;


    public void setDownloadListener(AppDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public AppVersionHelper(Context context) {
        this.context = context;
    }

    private AppVersion downloadAppVersion;

    public void download(AppVersion appVersion) {
        downloadAppVersion = appVersion;
        if (appVersion == null) {
            downloadListener.error("appVersion is null");
            return;
        } else if (appVersion.getUrls()==null || appVersion.getUrls().size() == 0) {
            LogUtils.debug("appVersion.getUrls"+appVersion.getUrls());
            LogUtils.debug("appVersion.getUrls.size"+appVersion.getUrls().size());
            downloadListener.error("urls size is empty");
            return;
        }
        LogUtils.debug("download--->"+1);
        downloadListener.start();
        LogUtils.debug("download--->"+2);

        downloadSingle(downloadAppVersion, 0);
    }

    private AppDownloadListener localDownloadListener = new AppDownLoadListenrBase() {

        @Override
        public void finsh() {
            if (downloadListener != null)
                downloadListener.finsh();
        }

        @Override
        public void downloadProgress(float f) {
            if (downloadListener != null)
                downloadListener.downloadProgress(f);
        }

        @Override
        public void error(String desc) {
            int index = Integer.parseInt(desc);
            index++;
            if (index >= downloadAppVersion.getUrls().size() || StringUtils.isBlank(downloadAppVersion.getUrls().get(index))) {
                downloadListener.error("download error");
                return;
            }
            downloadSingle(downloadAppVersion, index);
        }
    };

    private void downloadSingle(AppVersion appVersion, final int index) {
        if (index >= appVersion.getUrls().size() || StringUtils.isBlank(appVersion.getUrls().get(index))) {
            return;
        }
        final String filename = appVersion.getVersionName()+appVersion.getBuild();
        final String subfix = "apk";

        String url = appVersion.getUrls().get(index);
        LogUtils.debug("downloadpath=="+downloadPath);
        task = new Task(filename, filename, url, subfix, downloadPath, new ITaskStateChangeListener() {
            @Override
            public void onStateChanged(Task task) {
                if (task.getState() == TaskStateEnum.LOADING) {
                    if (localDownloadListener != null)
                        localDownloadListener.downloadProgress(task.getProgress());
                } else if (task.getState() == TaskStateEnum.FINISHED) {
                    if (localDownloadListener != null)
                        localDownloadListener.finsh();
                } else if (task.getState() == TaskStateEnum.FAILED ) {
                    localDownloadListener.error(index + "");
                }
            }
        });
        TaskManagerDelegate.startTask(task);
        if (localDownloadListener != null)
            localDownloadListener.start();
    }

    public void stop(){
        if(downloadAppVersion!=null){
            for(String url:downloadAppVersion.getUrls()){
                Task task=TaskManagerDelegate.findTask(url);
                if(task==null){
                    continue;
                }
                TaskManagerDelegate.stopTask(task);
            }
        }
    }

    public static  AppVersion parseToAppVersion(JSONObject response){
        AppVersion appVersion=new AppVersion();
        try{
            int errorId = ResponseHelper.getErrorId(response);
            if(XSErrorEnum.SUCCESS.getCode() == errorId){
            JSONObject vdata = ResponseHelper.getVdata(response);
            if (vdata != null) {
                JSONObject infoJson = vdata.getJSONObject("info");

                if (infoJson != null) {

                    appVersion.setVersionName(infoJson.optString("version"));
                    appVersion.setBuild(infoJson.optInt("build"));
                    appVersion.setUptime(infoJson.optString("uptime"));
                    appVersion.setIsOptional(infoJson.optBoolean("optional"));
                    JSONObject featureJson = infoJson.getJSONObject("feature");
                    if (featureJson != null) {
                        String charset = featureJson.optString("charset","utf-8");
                        JSONObject txtJson = featureJson.optJSONObject("text");
                        if (txtJson != null) {
                            JSONArray array = txtJson.optJSONArray("chs");
                            if(array != null && array.length() > 0){
                                List<String> chs = new ArrayList<String>();
                                for (int i = 0; i < array.length(); i++) {
                                    String content = array.get(i).toString();
                                    if (StringUtils.isNotEmpty(content)) {
                                        chs.add(content);
                                    }
                                }
                                appVersion.setFeatures(chs);
                            }
                        }
                    }
                    JSONArray urlJson = infoJson.optJSONArray("url");
                    if (urlJson != null && urlJson.length() > 0) {

                        List<String>  urls = new ArrayList<String>();
                        for (int i = 0; i < urlJson.length() ; i++) {
                            String theurl = urlJson.get(i).toString();
                            if (StringUtils.isNotEmpty(theurl)) {
                                urls.add(theurl);
                            }
                        }
                        appVersion.setUrls(urls);
                    }
                }
            }
        }
    }catch (JSONException e){
        e.printStackTrace();
    }
        return appVersion;
    }

}
