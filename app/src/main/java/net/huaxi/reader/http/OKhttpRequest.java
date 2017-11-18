package net.huaxi.reader.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import net.huaxi.reader.bean.BaseResponse;
import net.huaxi.reader.bean.UserInfo;
import net.huaxi.reader.callback.CallBack;
import net.huaxi.reader.callback.HttpActionHandle;
import net.huaxi.reader.utils.MLog;
import net.huaxi.reader.utils.ToastUtil;
import net.huaxi.reader.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.IdentityHashMap;
import java.util.Map;


/***
 * 请求封装类
 */
public class OKhttpRequest {
    private HttpActionHandle actionHandle;

    public OKhttpRequest(HttpActionHandle actionHandle) {
        this.actionHandle = actionHandle;

    }

    public OKhttpRequest() {
    }

    /***
     * get请求，结果回调到Activity
     * @param action 同一页面多个请求区分(可为null)
     * @param url 请求接口
     * @param params 参数(可为null)
     */
    public void get(final String action, String url, Map<String, String> params) {
        String URL = url;
        if (!url.startsWith("http")) {
            URL = UrlUtils.BASE_URL + url;
        }
        if (params == null) {
            params = new IdentityHashMap<>();
        }
        MLog.i(action + "---get-->请求参数", URL + params.toString());
        OkHttpUtils.get()
                .url(URL)
                .params(params)
                .headers(headParmas(action.equals("")))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                actionHandle.handleActionError(action, e);
            }

            @Override
            public void onResponse(String response) {
                MLog.i(action + "请求结果-->", response.toString());
                try {
                    BaseResponse baseResponse = new Gson().fromJson(response, BaseResponse.class);
                    if (baseResponse.isSuccess()) {
                        actionHandle.handleActionSuccess(action, response);
                    } else {
                        //TODO 错误提示
                        if (!TextUtils.isEmpty(baseResponse.getMessage()))
                            ToastUtil.showLong(baseResponse.getMessage());
                        actionHandle.handleActionError(action, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    actionHandle.handleActionError(action, response);
                }
            }
        });

    }

    /***
     * get请求,结果回调到Activity
     * @param t 实体类
     * @param action 同一页面多个请求区分(可为null)
     * @param url 请求接口
     * @param params 参数(可为null)
     */
    public <T> void get(final Class<T> t, final String action, String url, Map<String, String> params) {
        String URL = url;
        if (!url.startsWith("http")) {
            URL = UrlUtils.BASE_URL + url;
        }
        if (params == null) {
            params = new IdentityHashMap<>();
        }
        MLog.i(action + "---get-->请求参数", URL + params.toString());
        OkHttpUtils.get()
                .url(URL)
                .params(params)
                .headers(headParmas())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                actionHandle.handleActionError(action, e);
            }

            @Override
            public void onResponse(String response) {
                MLog.i(action + "请求结果-->", response);
                try {
                    BaseResponse baseResponse = new Gson().fromJson(response, BaseResponse.class);
                    if (baseResponse.isSuccess()) {
                        actionHandle.handleActionSuccess(action, new Gson().fromJson(response, t));
                    } else {
                        //TODO 错误提示
                        actionHandle.handleActionError(action, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    actionHandle.handleActionError(action, response);
                }
            }
        });
    }


    /***
     * get请求,请求结果回调到调用处
     * @param t 实体类
     * @param action 同一页面多个请求区分(可为null)
     * @param url 请求接口
     * @param params 参数(可为null)
     */
    public <T> void get(final Class<T> t, final String action, String url, Map<String, String> params, final CallBack result) {
        String URL = UrlUtils.BASE_URL + url;
        if (params == null) {
            params = new IdentityHashMap<>();
        }
        MLog.i(action + "---get-->请求参数", URL + params.toString());
        OkHttpUtils.get()
                .url(URL)
                .params(params)
                .headers(headParmas())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                result.fail(action, e);
            }

            @Override
            public void onResponse(String response) {
                MLog.i(action + "请求结果-->", response);
                try {
                    BaseResponse baseResponse = new Gson().fromJson(response, BaseResponse.class);
                    if (baseResponse.isSuccess()) {
                        result.success(action, new Gson().fromJson(response, t));
                    } else {
                        //TODO 错误提示
                        result.fail(action, new Gson().fromJson(response, t));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.fail(action, e);
                }
            }
        });
    }

    /***
     * get请求,请求结果回调到调用处
     * @param action 同一页面多个请求区分(可为null)
     * @param url 请求接口
     * @param params 参数(可为null)
     */
    public void get(final String action, String url, Map<String, String> params, final CallBack result) {
        String URL = null;
        if (action.equals("login")) {
            URL = UrlUtils.BASE_URL + url;
        } else {
            URL = UrlUtils.BASE_URL + url;
        }
        if (params == null) {
            params = new IdentityHashMap<>();
        }
        MLog.i(action + "---get-->请求参数", URL + params.toString());
        OkHttpUtils.get()
                .url(URL)
                .params(params)
                .headers(headParmas())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String token = response.header("token", "");
                String resule = response.body().string();
                if (action.equals("login") && !TextUtils.isEmpty(token)) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(resule);
                        BaseResponse baseResponse = new Gson().fromJson(resule, BaseResponse.class);
                        if (baseResponse.isSuccess()) {
                            UserInfo user = new Gson().fromJson(obj.getString("data"), UserInfo.class);
                            user.setToken(token);
                            user.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return resule;
            }

            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                result.fail(action, e);
            }

            @Override
            public void onResponse(Object response) {
                MLog.i(action + "请求结果-->", response.toString());
                try {
                    BaseResponse baseResponse = new Gson().fromJson(response.toString(), BaseResponse.class);
                    if (baseResponse.isSuccess()) {
                        result.success(action, response);
                    } else {
                        //TODO 错误提示
                        result.fail(action, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.fail(action, response);
                }
            }
        });
    }

    /**
     * post请求，结果回调到Activity
     *
     * @param t
     * @param action
     * @param params
     * @param <T>
     */
    public <T> void post(final Class<T> t, final String action, String url, Map<String, String> params) {
        String URL = url;
        if (!url.startsWith("http")) {
            URL = UrlUtils.BASE_URL + url;
        }

        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        MLog.i(action + "---post-->请求", URL + params.toString());
        postFormBuilder
                .url(URL)
                .params(params)
                .headers(headParmas(false))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        e.printStackTrace();
                        actionHandle.handleActionError(action, e);
                    }

                    @Override
                    public void onResponse(String response) {
                        MLog.i(action + "请求结果-->", response);
                        try {
                            BaseResponse baseResponse = new Gson().fromJson(response, BaseResponse.class);
                            if (baseResponse.isSuccess()) {
                                actionHandle.handleActionSuccess(action, new Gson().fromJson(response, t));
                            } else {
                                //TODO 错误提示
                                actionHandle.handleActionError(action, response);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            actionHandle.handleActionError(action, e);
                        }
                    }
                });
    }


    /**
     * post请求，结果回调到Activity
     *
     * @param t
     * @param action
     * @param params
     * @param <T>
     */
    public <T> void post(final Class<T> t, final String action, String url, Map<String, String> params, final CallBack c) {
        String URL = url;
        if (!url.startsWith("http")) {
            URL = UrlUtils.BASE_URL + url;
        }

        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        MLog.i(action + "---post-->请求", URL + params.toString());
        postFormBuilder
                .url(URL)
                .params(params)
                .headers(headParmas(false))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        e.printStackTrace();
                        c.fail(action, e);
                    }

                    @Override
                    public void onResponse(String response) {
                        MLog.i(action + "请求结果-->", response);
                        try {
                            BaseResponse baseResponse = new Gson().fromJson(response, BaseResponse.class);
                            if (baseResponse.isSuccess()) {
                                c.success(action, new Gson().fromJson(response, t));
                            } else {
                                //TODO 错误提示
                                c.fail(action, baseResponse);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            c.fail(action, e);
                        }
                    }
                });
    }


    /**
     * 头部参数
     *
     * @return
     */
    public Map<String, String> headParmas() {
        return headParmas(false);
    }

    /**
     * 头部参数
     *
     * @return
     */
    public Map<String, String> headParmas(boolean tokenName) {
        Map<String, String> map;
        map = new IdentityHashMap<>();
        String token = UserInfo.getInstance().getToken();
        map.put("client-id", "40");
        map.put("timestamp", System.currentTimeMillis() / 1000 + "");
        map.put("version", Util.getVersionName());
        map.put("sign", sign());
        map.put("sn", Util.getUUid().toString());
        map.put(tokenName ? "access_token" : "access_token", token == null ? "" : token);
        map.put("imei", Util.getid());
        map.put("mac", Util.getMacAddr());
        map.put("os", android.os.Build.VERSION.RELEASE);
        MLog.d("headParmas", "----->" + map.toString());
        return map;
    }


    /**
     * 签名
     *
     * @return
     */
    public String sign() {//http%3A%2F%2Fgazhi.hxdrive.net%2F
        String url = "http://gazhi.hxdrive.net/";
        StringBuilder bd = new StringBuilder();
        try {
            bd.append("client_id=40");
            bd.append("&timestamp=" + System.currentTimeMillis() / 1000 + "");
            bd.append("&url=" + URLEncoder.encode(url, "UTF-8"));
            bd.append("&version=" + Util.getVersionName());
            bd.append("bpgbwtj6xlhdlz8g8jtlo5i11ljdr4548sxz2l");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Util.Md5(bd.toString());
    }
}