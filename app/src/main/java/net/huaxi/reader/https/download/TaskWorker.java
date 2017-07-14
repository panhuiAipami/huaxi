package net.huaxi.reader.https.download;

import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.statistic.ReportUtils;

import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;

/**
 * 网络下载Worker
 */
public class TaskWorker implements Runnable {

    private int progress = 0;
    private boolean isCanceled = false;
    private Task mTask;
    private Object dispatcherListener;   //唤醒线程池，添加新任务。
    private TaskManager taskManager;
    private TaskStateEnum state = TaskStateEnum.PREPARED;
    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int READ_TIMEOUT = 30 * 1000;
    protected static final int BLOCK = 2 * 1024;
    private String httpUrl;
    InputStream bis = null;
    HttpURLConnection urlConnection = null;
    private long mCurrentSize = 0;
    private long totalSize = 0;
    private boolean hasRelease = false;
    public static final String TYPE_WML = "text/vnd.wap.wml";
    public static final String TYPE_WMLC = "application/vnd.wap.wmlc";

    public TaskWorker(Task task, TaskManager manager, Object obj) {
        this.mTask = task;
        this.taskManager = manager;
        this.dispatcherListener = obj;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    @Override
    public void run() {
        download(mTask);
    }

    public void download(Task task) {
        System.err.println("=========开始下载=======" + task.getName());
        postStateChange(TaskStateEnum.STARTED, 0, TaskErrorEnum.TASK_SUCCESS.getCode());
        if (NetType.TYPE_NONE.equals(NetUtils.checkNet())) {
            postStateChange(TaskStateEnum.FAILED, 0, TaskErrorEnum.TASK_NETWORK_UNAVAILABLE.getCode());
            return;
        }
        FileOutputStream _fileoutput = null;
        String fileName = task.getDownloadDirectory() + task.getName() + "." + task.getSuffix();
        File _file = new File(fileName + ".tmp");
        if (!_file.getParentFile().exists()) {
            _file.getParentFile().mkdirs();
        }
        try {
            httpUrl = task.getUrl();
            urlConnection = prepareConnection(new URL(httpUrl));
            bis = urlConnection.getInputStream();
            if (bis == null) {
                postStateChange(TaskStateEnum.FAILED, 0, TaskErrorEnum.TASK_NETWORK_PARSE_URL_FAILED.getCode());
                return;
            }
            if (totalSize <= 0) {
                postStateChange(TaskStateEnum.FAILED, 0, TaskErrorEnum.TASK_NETWORK_NUKONWN.getCode());
                ReportUtils.reportError(new Throwable("网络下载文件，Header返回的ContentLength未负值"));
                return;
            }
            _fileoutput = new FileOutputStream(_file);
            byte[] buffer = new byte[BLOCK];
            int readNumber = 0;
            int retryTimes = 0;
            if (_file.exists()) {  //禁止其他文件访问.
                _file.setReadable(false);
                _file.setWritable(true, true);
                _file.setExecutable(false);
            }
            while (bis != null) {
                if (isCanceled()) {
                    if (_file.exists()) {
                        _file.delete();
                    }
                    postStateChange(TaskStateEnum.CANCELED, progress, TaskErrorEnum.TASK_USER_CANCEL.getCode());
                    break;
                }
                if (!_file.exists()) {
                    postStateChange(TaskStateEnum.CANCELED, progress, TaskErrorEnum.TASK_NETWORK_LOCAL_FILE_DELETED.getCode());
                    _file.delete();
                    break;
                }
                try {
                    readNumber = bis.read(buffer);
                    if (readNumber == -1) {
                        break;
                    }
                    retryTimes = 0;
                } catch (IOException e) {
                    if (retryTimes >= 1) {
                        throw e;
                    }
                    retryTimes++;
                    bis.close();
                    urlConnection.disconnect();
                    urlConnection = prepareConnection(new URL(httpUrl));
                    bis = urlConnection.getInputStream();
                    continue;
                }
                _fileoutput.write(buffer, 0, readNumber);
                _fileoutput.flush();
//                String temp = new String(buffer, "UTF-8");
//                LogUtils.debug("temp str = " + temp);
                mCurrentSize += readNumber;
                try {
                    progress = (int) (100 * mCurrentSize / totalSize);
                } catch (Exception e) {
                    postStateChange(TaskStateEnum.FAILED, progress, TaskErrorEnum.TASK_NETWORK_NUKONWN.getCode());
                    e.printStackTrace();
                }
                if (progress >= 100) {
                    break;
                }
                postStateChange(TaskStateEnum.LOADING, progress, TaskErrorEnum.TASK_SUCCESS.getCode());
            }
            /*防止文件本身有问题，导致没有反馈*/
            File newfile = new File(fileName);
            if (newfile.exists()) {
                newfile.delete();
            }
            _file.renameTo(newfile);
            if(!isCanceled){
                postStateChange(TaskStateEnum.FINISHED, progress, TaskErrorEnum.TASK_SUCCESS.getCode());
            }

        } catch (MalformedURLException e) {
            postStateChange(TaskStateEnum.FAILED, progress, TaskErrorEnum.TASK_NETWORK_PARSE_URL_FAILED.getCode());
            if (_file.exists()) {
                _file.delete();
            }
        } catch (FileNotFoundException e) {
            postStateChange(TaskStateEnum.FAILED, progress, TaskErrorEnum.TASK_NETWORK_FILE_NOT_FOUNDED.getCode());
            if (_file.exists()) {
                _file.delete();
            }
        } catch (IOException e) {
            postStateChange(TaskStateEnum.FAILED, progress, TaskErrorEnum.TASK_NETWORK_CONNECTIONTIMEOUT.getCode());
            if (_file.exists()) {
                _file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (_file.exists()) {
                _file.setReadable(true);
                _file.setWritable(true);
                _file.setExecutable(true);
            }
            try {
                _fileoutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            releaseResources(bis);
        }
    }

    @SuppressWarnings("deprecation")
    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        // Prior to Froyo, HttpURLConnection had some frustrating bugs. In
        // particular, calling close() on a readable InputStream could poison
        // the connection pool.
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private HttpURLConnection prepareConnection(URL url) throws IOException {
        LogUtils.info("start to prepare url:" + url);
        disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = openConnection(url);
        if (null == urlConnection) {
            throw new IOException("Connection cannot be established to : " + url.toString());
        }
        // 请求服务器不要Gzip压缩,不然读取不到内容.
        urlConnection.setRequestProperty("Accept", String.format(Constants.SERVER_ACCEPT, "1.0.0"));
        String cookie = SharePrefHelper.getCookie();
        if (!StringUtils.isBlank(cookie) && !cookie.equals("-1")) {
            urlConnection.setRequestProperty("Cookie", SharePrefHelper.getCookie());
        }
        urlConnection.setRequestProperty("Accept-Encoding", "identity");
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);

        // 1. HTTP Server does not support resumable downloading.
        // 2. 416 Requested Range Not Satisfiable
        // 3. 301 Moved Permanently. Shall read Location from header
        // 4. 404 Not Found
        int resCode = urlConnection.getResponseCode();
        LogUtils.debug("Response Code = " + resCode);
        switch (resCode) {
            case HttpURLConnection.HTTP_OK:
                String wapCotentType = urlConnection.getContentType();
                if (wapCotentType != null && ((wapCotentType.indexOf(TYPE_WML) != -1 || wapCotentType.indexOf(TYPE_WMLC) != -1))) {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                    urlConnection = prepareConnection(url);
                }
                LogUtils.debug("getContentLength() = " + (totalSize = urlConnection.getContentLength()));
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
                LogUtils.debug("301 Moved permanently");
            case HttpURLConnection.HTTP_MOVED_TEMP:
                LogUtils.debug("302: Moved temporarily");
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
                urlConnection = prepareConnection(url);
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                LogUtils.error("http responsecode = " + HttpURLConnection.HTTP_NOT_FOUND);
                break;
            default:
                throw new IOException("HTTP Response Code: " + resCode);
        }
        LogUtils.debug("Finish to prepare connection");
        return urlConnection;
    }

    private void releaseResources(InputStream bis) {
        if (hasRelease) {
            return;
        }
        LogUtils.debug("Releasing resources");
        try {
            if (null != bis) {
                bis.close();
            }
        } catch (IOException e) {
            LogUtils.debug(e.getMessage());
        }
        hasRelease = true;
        LogUtils.debug("Resources released");
    }

    public static HttpURLConnection openConnection(URL arul) throws IOException {
        HttpURLConnection urlConnection = null;
        urlConnection = (HttpURLConnection) arul.openConnection();
        return urlConnection;
    }

    public void registerStateChangeListener(ITaskStateChangeListener listener) {
        if (mTask != null) {
            mTask.setListener(listener);
        }
    }

    //屏蔽下载进度的提醒。只发送成功和失败.
    public void postStateChange(TaskStateEnum state, int progress, int errorcode) {
        if (TaskStateEnum.FINISHED == state || TaskStateEnum.FAILED == state || TaskStateEnum.CANCELED == state ) {
            if (mTask != null && mTask.getListener() != null && mTask != null) {
                mTask.setState(state);
                mTask.setProgress(progress);
                mTask.setErrorCode(errorcode);
                mTask.getListener().onStateChanged(mTask);
            }
            if (taskManager != null) {
                    taskManager.removeTask(mTask);
                }
                try {
                    dispatcherListener.notifyAll();
            } catch (Exception e) {
            }
        }else if(TaskStateEnum.LOADING == state){
            try {
                if (mTask != null && mTask.getListener() != null && mTask != null) {
                    mTask.setState(state);
                    mTask.setProgress(progress);
                    mTask.setErrorCode(errorcode);
                    mTask.getListener().onStateChanged(mTask);
                }
            } catch (Exception e) {
            }
        }

    }

}
