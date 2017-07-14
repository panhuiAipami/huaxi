package net.huaxi.reader.model;

import android.app.Activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.book.FileConstant;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.download.Task;
import net.huaxi.reader.https.download.TaskStateEnum;
import net.huaxi.reader.statistic.ReportUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.appinterface.ChapterDownloadListener;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.https.download.TaskManagerDelegate;
import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;

/**
 * Created by Saud on 16/1/18.
 */
public class DownloadBuyHelp extends Thread {
    private final String bookId;
    private final List<String> chapterIds;
    private final Activity activity;
    private String ids = "";
    private long time;
    private ChapterDownloadListener downloadListener;
    private int getNum = 0;
    private Task task;
    private JSONArray jsonArray;
    private List<Task> tasks = new ArrayList<>();
    private String common = "";

    public DownloadBuyHelp(Activity activity, List<String> chapterIds, String bookId) {

        this.activity = activity;
        this.chapterIds = chapterIds;
        this.bookId = bookId;
    }

    public void setDownloadListener(ChapterDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }


    @Override
    public void run() {
        initChapterId();
        postDownloadList();
    }

    private void initChapterId() {
        for (int i = 0; i < chapterIds.size(); i++) {
            if (i == chapterIds.size() - 1) {
                ids += chapterIds.get(i);
            } else {
                ids += chapterIds.get(i) + ",";
            }
        }
    }

    /**
     * post请求
     */
    private void postDownloadList() {
        time = System.currentTimeMillis();
        LogUtils.debug("bookId=" + bookId);
        LogUtils.debug("chapterId=" + ids);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("bookid", bookId);
        map.put("chapterid", ids);
        PostRequest request = new PostRequest(URLConstants.POST_DOWNLOAD_CHAPTER, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response====" + response.toString());
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSNetEnum._DC_CODE_ERROR_PAY_NOT_ENOUGH_COINS.getCode()) {
                    if (downloadListener != null) {
                        downloadListener.needCoin(errorid);
                    }
                    LogUtils.debug("阅读币不足");
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_NOT_LOGIN.getCode()) {
                    LogUtils.debug("需要登录");
                    if (downloadListener != null) {
                        downloadListener.needLogin();
                    }
                } else if (errorid == XSNetEnum._DC_CODE_ERROR_READ_CHAPTER_NO_CPT.getCode()) {
                    LogUtils.debug("请求错误=" + errorid);
                    if (downloadListener != null) {
                        downloadListener.dataError();
                    }
                } else if (ResponseHelper.isSuccess(response)) {
                    LogUtils.debug("请求成功");
                    if (downloadListener != null) {
                        downloadListener.success();
                    }
                    JSONObject jsonObject = ResponseHelper.getVdata(response);
                    if (jsonObject != null) {
                        try {
                            jsonArray = jsonObject.getJSONArray(XSKEY.KEY_LIST);
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = (JSONObject) jsonArray.opt(i);
                                    getDownloadChapterText(json);
                                }
                            }
                        } catch (JSONException e) {
                            downloadListener.dataError();
                            e.printStackTrace();
                        }
                    }
                } else if (errorid == XSNetEnum._DC_CODE_ERROR_NEED_PAY.getCode()) {
                    LogUtils.debug("需要对章节进行付费");
                    if (downloadListener != null) {
                        downloadListener.needLogin();
                    }
                } else {
                    if (downloadListener != null) {
                        downloadListener.error();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (downloadListener != null) {
                    downloadListener.error();
                }
                ViewUtils.toastShort(activity.getResources().getString(R.string.network_server_error));
                ReportUtils.reportError(new Throwable(error.toString()));
            }
        }, map, "1.2");
        RequestQueueManager.addRequest(request);
    }


    /**
     * get下载章节
     *
     * @param jsonObject
     */
    private void getDownloadChapterText(JSONObject jsonObject) {

        String requrl = jsonObject.optString("requrl");
        String bookId = jsonObject.optString("bookid");
        String chapterid = jsonObject.optString("chapterid");
        String suffix = FileConstant.XSREADER_FILE_SUFFIX;
        final String downloadPath = Utility.getBookRootPath() + bookId + File.separator;

        task = new Task(chapterid, chapterid, requrl, suffix, downloadPath, new ITaskStateChangeListener() {
            @Override
            public void onStateChanged(Task task) {
                if (task.getState() == TaskStateEnum.LOADING) {

                    return;
                }
                getNum++;
                //下载成功的

                if (task.getState() == TaskStateEnum.FINISHED && task.getProgress() == 100) {
//                    || task.getState() == TaskStateEnum.CANCELED || task.getState() == TaskStateEnum.FAILED

                    if (downloadListener != null) {
                        downloadListener.downloadOneFinish(task.getId());
                    }
                } else {
                    //下载失败的
                    if (downloadListener != null) {
                        downloadListener.downloadOneError(task.getId());
                    }
                    LogUtils.debug("下载失败");
                }
                //完成了所有的下载任务
                if (getNum == chapterIds.size()) {//下次成功10个或者下载了最后一个，回到一个，防止过度调用
                    if (downloadListener != null) {
                        downloadListener.downloadAllFinish(task.getErrorCode());
                        tasks.clear();
                    }
                }
            }
        });
        TaskManagerDelegate.startTask(task);
        tasks.add(task);
    }


    /**
     * 停止下载
     */
    public void stopDownload() {

        if (tasks == null || tasks.size() == 0) {
            return;
        }
        for (Task task : tasks) {
            TaskManagerDelegate.stopTask(task);
        }

        tasks.clear();
    }

    /**
     * 是否有下载任务
     *
     * @return
     */
    public boolean isDownloading() {
        if (tasks != null) {
            return !tasks.isEmpty();
        } else {
            return false;
        }
    }
}
