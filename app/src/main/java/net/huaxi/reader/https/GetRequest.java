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
import java.util.Map;

public class GetRequest extends Request<JSONObject> {

    private Listener<JSONObject> mListener;
    private String serverVersion = "1.0";

    /**
     * Get请求（不需要关心header问题）
     *
     * @param url           网址
     * @param listener      正确回调监听
     * @param errorListener 错误回调监听
     */
    public GetRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        LogUtils.info("get request = " + url);
        mListener = listener;
        this.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
    }
    /**
     * Get请求（不需要关心header问题）
     *
     * @param url           网址
     * @param listener      正确回调监听
     * @param errorListener 错误回调监听
     * @param serverVersion 服务端接口版本号
     */
    public GetRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener, String serverVersion) {
        super(Request.Method.GET, url, errorListener);
        this.serverVersion = serverVersion;
        LogUtils.info("-----request_url------->" + url);
        mListener = listener;
        this.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
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
            LogUtils.debug("----volley_response---->" + jsonString);
            String errorid = new JSONObject(jsonString).optString("errorid");
            if (!errorid.equals("0")) {
                LogUtils.debug("请求失败:" + errorid);
            }
            return Response.success(new JSONObject(jsonString), HttpHeaderParser
                    .parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }


}
