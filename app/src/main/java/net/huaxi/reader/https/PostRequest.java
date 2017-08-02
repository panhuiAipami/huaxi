package net.huaxi.reader.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PostRequest extends Request<JSONObject> {
    private Map<String, String> mMap;
    private Listener<JSONObject> mListener;
    private String serverVersion = "1.0";



    /**
     * Post请求（不需要关心header）
     *
     * @param url           网址
     * @param listener      正确回调监听
     * @param errorListener 错误回调监听
     * @param map           (提交的参数)
     * @param outTime       超时时间
     */
    public PostRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener,
                       Map<String, String> map,int outTime) {
        super(Request.Method.POST, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
        LogUtils.info("post request = " + url);
        mListener = listener;
        mMap = map;
        this.setRetryPolicy(new DefaultRetryPolicy(outTime * 1000, DefaultRetryPolicy
                .DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
    }




    /**
     * Post请求（不需要关心header）
     *
     * @param url           网址
     * @param listener      正确回调监听
     * @param errorListener 错误回调监听
     * @param map           (提交的参数)
     */
    public PostRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener,
                       Map<String, String> map) {
        super(Request.Method.POST, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
        LogUtils.info("post request = " + url+"?"+map.toString());
        mListener = listener;
        mMap = map;
        this.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, DefaultRetryPolicy
                .DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
    }


    /**
     * Post请求（不需要关心header）
     *
     * @param url           网址
     * @param listener      正确回调监听
     * @param errorListener 错误回调监听
     * @param map           (提交的参数)
     * @param serverVersion 服务端接口版本号
     */
    public PostRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener,
                       Map<String, String> map, String serverVersion) {
        super(Request.Method.POST, url, errorListener);
        this.serverVersion = serverVersion;
        setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
        LogUtils.info("post request = " + url);
        mListener = listener;
        mMap = map;
        this.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, DefaultRetryPolicy
                .DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        LogUtils.debug("参数===============");
        for (String k : mMap.keySet()) {
            LogUtils.debug(k + ":" + mMap.get(k));
        }
        return mMap;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> map = Utility.getNetHeader(CommonApp.getInstance(), serverVersion);
        LogUtils.debug("头参数===============↓");
        for (String k : map.keySet()) {
            LogUtils.debug(k + ":" + map.get(k));
        }
        LogUtils.debug("头参数===============↑");
        return map;
    }

    //此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response
                    .headers));
            LogUtils.debug("volley_response_headers=====start");
                Set<String> strings=response.headers.keySet();
                Iterator<String> iterator=strings.iterator();
                while (iterator.hasNext()){
                    String key=iterator.next();
                    LogUtils.debug(key+":"+response.headers.get(key));
                }
            LogUtils.debug("volley_response_headers=====end");
            LogUtils.debug("volley_response_data====" + jsonString);
            String errorid = new JSONObject(jsonString).optString("errorid");
            if (!errorid.equals("0")) {
                LogUtils.debug("请求失败:" + errorid);
            }
            return Response.success(new JSONObject(jsonString), HttpHeaderParser
                    .parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            LogUtils.debug("volley_response_error.....");
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            LogUtils.debug("volley_response_error.....");
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

}
