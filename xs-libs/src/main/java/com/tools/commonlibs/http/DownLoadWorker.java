package com.tools.commonlibs.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tools.commonlibs.tools.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;


/**
 * http网络下载
 * 
 * @author taoyf
 * @time 2015年6月19日
 */
public class DownLoadWorker {

    private Context mContext;
    private String httpUrl;

    InputStream bis = null;
    HttpURLConnection urlConnection = null;

    private int progress = 0;
    private long currentSize = 0;
    private long totalSize = 0;
    private boolean hasRelease = false;

    private static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int READ_TIMEOUT = 30 * 1000;
    protected static final int BLOCK = 2 * 1024;

    public static final String TYPE_WML = "text/vnd.wap.wml";
    public static final String TYPE_WMLC = "application/vnd.wap.wmlc";

    public DownLoadWorker(Activity act, String url) {
        this.httpUrl = url;
        this.mContext = act;
    }

    public boolean isPause() {
        return false;
    }

    public String run() {
        StringBuilder sb = new StringBuilder();
        try {
            urlConnection = prepareConnection(new URL(httpUrl));
            bis = urlConnection.getInputStream();
            if (bis == null) {
                return null;
            }
            byte[] buffer = new byte[BLOCK];
            int readNumber = 0;
            int retryTimes = 0;
            while (!isPause() && bis != null) {
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
                String temp = new String(buffer, "UTF-8");
                LogUtils.debug("temp str = " + temp);
                sb.append(temp);
                currentSize += readNumber;
                try {
                    progress = (int) (100 * currentSize / totalSize);
                    LogUtils.debug(" load progress = " + progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress >= 100) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseResources(bis);
        }
        return sb.toString();
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
        // Log.v(getTag() + "prepareConnection", "start to prepare url:" + url);
        disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = openConnection(url, mContext);

        if (null == urlConnection) {
            throw new IOException("Connection cannot be established to : " + url.toString());
        }
        // 请求服务器不要Gzip压缩,不然读取不到内容.
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
        case HttpURLConnection.HTTP_PARTIAL:
            LogUtils.debug("");
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
        case REQUESTED_RANGE_NOT_SATISFIABLE:
            // TODO [Question] How to handle this scenario
            // Go through
        default:
            throw new IOException("HTTP Response Code: " + resCode);
        }
        LogUtils.debug("Finish to prepare connection");
        return urlConnection;
    }

    public static HttpURLConnection openConnection(URL arul, Context context) throws IOException {
        // InetSocketAddress pxy = Http.getProxy(context);
        HttpURLConnection urlConnection = null;
        // if (pxy == null) {
        urlConnection = (HttpURLConnection) arul.openConnection();
        // } else {
        // Proxy p = new Proxy(Proxy.Type.HTTP, pxy);
        // urlConnection = (HttpURLConnection) arul.openConnection(p);
        // }
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
}
