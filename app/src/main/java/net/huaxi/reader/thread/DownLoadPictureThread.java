package net.huaxi.reader.thread;

import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.MD5Utils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.bean.SplashBean;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.download.Task;
import net.huaxi.reader.https.download.TaskStateEnum;
import net.huaxi.reader.model.SplashShowHelper;
import net.huaxi.reader.statistic.ReportUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.download.TaskManagerDelegate;
import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;

/**
 * Created by ZMW on 2016/4/5.
 * 下载图片类
 */
public class DownLoadPictureThread extends Thread {
    String downloadPath = Constants.XSREADER_SPLASH_IMGCACHE;//在这个文件下，文件名为路径地址的md5编码

    @Override
    public void run() {
        RequestFuture<JSONObject> future=RequestFuture.newFuture();
        GetRequest request=new GetRequest(URLConstants.APP_SPLASH_URL+ CommonUtils.getPublicGetArgs(),future,null);
        RequestQueueManager.getInstance().add(request);
        try {
            JSONObject json=future.get(10, TimeUnit.SECONDS);
            LogUtils.debug("splash=responsejson==="+json.toString());
            if(ResponseHelper.isSuccess(json)){
                JSONObject info=ResponseHelper.getVdata(json).optJSONObject("info");
                if(info==null){
                    return;
                }
                JSONArray list=info.optJSONArray("list");
                if(list!=null){
                    SharePrefHelper.setSplashResponse(json.toString());
                    Type type= new TypeToken<List<SplashBean>>(){}.getType();
                    List<SplashBean> ll=new Gson().fromJson(list.toString(),type);
                    download(ll);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    //若存在下载，若不存在不下载
    public void download(List<SplashBean> beans) {
        alldownlaod=beans.size();
        for (SplashBean b : beans) {
            String url=b.getUrl();
            String filename = MD5Utils.MD5(url);
            File file = new File(downloadPath + filename + "." + StringUtils.getImgUrlExt(url));
            if(file.exists()){
                alldownlaod--;
                if(alldownlaod==0){
                    new SplashShowHelper(AppContext.getInstance().getApplicationContext(),null).getBitmapUrl();
                }
                continue;
            }
            singleDownload(url);
        }
    }

    private void singleDownload(final String url) {
        final String filename = MD5Utils.MD5(url);
        String subfix = StringUtils.getImgUrlExt(url);
        Task task = new Task(filename, filename, url, subfix, downloadPath, new ITaskStateChangeListener() {
            @Override
            public void onStateChanged(Task task) {
                if (task.getState() == TaskStateEnum.FAILED) {
                    ReportUtils.reportError(new Throwable("splash=闪屏下载失败，filename:" + filename + ",url:"+url));
                    LogUtils.debug("splash=闪屏下载失败，filename:" + filename + ",url:"+url);
                } else {
                    LogUtils.debug("splash=闪屏下载成功，filename:" + filename + ",url:"+url);
                    alldownlaod--;
                    if(alldownlaod==0){
                        new SplashShowHelper(AppContext.getInstance().getApplicationContext(),null).getBitmapUrl();
                    }
                }
            }
        });
        TaskManagerDelegate.startTask(task);
    }
    private int alldownlaod;
}
