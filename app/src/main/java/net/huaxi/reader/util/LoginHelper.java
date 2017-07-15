package net.huaxi.reader.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.AppManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ZMW on 2015/12/3.
 */
public class LoginHelper {

    //腾讯appid
    public static final String QQLOGIN_APP_ID = "1105023260";//真
//    public static final String QQLOGIN_APP_ID = "100703379";//测试用

    public static final String WX_APP_ID = "wxdbd49a9d87fa9a19";
    public static final String WX_APP_PARTNERID = "1298708001";
    //微博相关
    public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
            "follow_app_official_microblog," + "invitation_write";
    public static final String WB_APP_KEY = "1966433292";
    public static final String WB_REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    public static final String WB_APP_SECRET = "b45a6b2f095a3ca108da3234d430dbb3";
    public static String LOGINSUCCESSTEXT = "授权成功";
    public static String LOGINCANCLETEXT = "关闭授权";
    public static String LOGINFAILEDTEXT = "授权失败";
    String WX_AppSecert = "a2498b938e917fcbdd3c043278e02f25";
    private Activity activity;
    private RequestQueue mQueue;
    // QQ登录相关
    private Tencent mTencent;
    private QQToken qqToken;
    private UserInfo qq_user_info;
    private IUiListener qqAuthListener;
    //	private IUiListener qqUserInfoListener;
    private String qqUUID;
    //微信相关
    private IWXAPI WXApi;
    //	private SendAuth.Req weixinReq;
    private String weixinToken;
    private String weixinUUID;
    private SsoHandler mSsoHandler;
    private AuthInfo mWeiBoAuth;
    private WeiboAuthListener weiboAuthListener;
    //	private RequestListener webiboUserInfoListener;
    private Oauth2AccessToken weiboToken;
    //		private UsersAPI weibo_user_info;
    private String weiboUUID;
    private IWeiboShareAPI mWeiboShareAPI;
    //登录回调接口
    private HelpListener listener;
    private IOpenApi openApi;

    public LoginHelper(Activity activity) {
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
    }

    ;

    private static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : (type + System
                .currentTimeMillis());
    }

    public void QQLogin() {
        QQLogin(null);
    }

    //qq登录
    public void QQLogin(HelpListener listener) {
        this.listener = listener;
        if (mTencent == null) {
            mTencent = Tencent.createInstance(QQLOGIN_APP_ID, this.activity);
            qqToken = mTencent.getQQToken();
            qq_user_info = new UserInfo(this.activity, qqToken);
        }
        qqAuthListener = new QQBaseUiListener();
        mTencent.login(this.activity, "all", qqAuthListener);
    }

    private void getQQUserDetail(String token) {

        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("access_token", EncodeUtils.encodeString_UTF8(token));
        map.put("u_ctype", EncodeUtils.encodeString_UTF8("1"));
        map.put("u_keep", EncodeUtils.encodeString_UTF8("1"));
        PostRequest request = new PostRequest(URLConstants.QQ_LOGIN, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtils.debug("respnse==" + response.toString());
                        if (!ResponseHelper.isSuccess(response)) {
                            LogUtils.debug("登录失败");
                            ViewUtils.toastShort("本次第三方登录失败");
                            activity.finish();
                            return;
                        }
                        LogUtils.debug("登录成功");
                        ViewUtils.toastShort("登录成功");
                        setCookieAndUid(response);
                        if (listener != null) {
                            listener.onSuccessComplete();
                        } else
                            activity.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort("本次第三方登录失败");
                activity.finish();
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    public void ActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.debug("resultCode=" + resultCode);
        LogUtils.debug("requestCode=" + requestCode);

        // System.out.println("data="+data.toString());

        //腾讯回调
        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqAuthListener);
        }
        //微博回调
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    //微信登录
    public void WxLogin() {

        WXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID, true);
        WXApi.registerApp(WX_APP_ID);

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_deme_test";
        if (WXApi.isWXAppInstalled()) {
            WXApi.sendReq(req);
        } else {
            ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_install_wx_client));
        }
    }

    //微博登录
    public void WbLogin() {
        System.out.println("wblogin..................");
        if (mWeiBoAuth == null) {
            mWeiBoAuth = new AuthInfo(activity, WB_APP_KEY, WB_REDIRECT_URL, WB_SCOPE);
        }
        if (null == mSsoHandler && mWeiBoAuth != null) {
            mSsoHandler = new SsoHandler(activity, mWeiBoAuth);
        }
        //		webiboUserInfoListener = new LogOutRequestListener();
        weiboAuthListener = new AuthListener();
        mSsoHandler.authorize(weiboAuthListener);
    }
    //微博登出监听
    //	private class LogOutRequestListener implements RequestListener {
    //		@Override
    //		public void onComplete(String response) {
    //			if (!TextUtils.isEmpty(response)) {
    //				try {
    //					JSONObject obj = new JSONObject(response);
    //					String value = obj.getString("result");
    //					if ("true".equalsIgnoreCase(value)) {
    //
    //					}
    //				} catch (JSONException e) {
    //					e.printStackTrace();
    //				}
    //			}
    //		}
    //
    //		@Override
    //		public void onWeiboException(WeiboException e) {
    //		}
    //	}

    public void NewIntent(Intent intent, IWeiboHandler.Response response) {
        mWeiboShareAPI.handleWeiboResponse(intent, response);
    }

    public void getAccessToken(int errCode, String code) {
        if (errCode == 0) {
            //登录成功
            //url   https://api.weixin.qq
            // .com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type
            // =authorization_code
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID +
                    "&secret=" + WX_AppSecert + "&code=" + code
                    + "&grant_type=authorization_code";
            LogUtils.debug("weixinLogin"+url);
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Volley返回结果
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("access_token")) {
                            weixinToken = (String) json.get("access_token");
                            LogUtils.debug("weixinLogin--token "+weixinToken);
                            weixinUUID = json.getString("openid");
                            LogUtils.debug("openid-----> "+weixinUUID);
                            getUserID(weixinUUID, LoginType.weixin);
                            ViewUtils.toastShort(LOGINSUCCESSTEXT);
                        } else {
                            //返回结果异常
                            LogUtils.error("getWinxinAccessToken Error1");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //请求失败
                    ViewUtils.toastShort(LOGINFAILEDTEXT);
                }
            });
            mQueue.add(request);
        } else {
            //微信登录失败的处理
            LogUtils.error("getWinxinAccessToken Error2");
        }
    }

    //微信刷新token,code 用于getAccessToken方法获取token
    public void refreshToken() {
        if (weixinToken == null || weixinToken.equals("")) {
            return;
        }
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + WX_APP_ID +
                "&grant_type=refresh_token&refresh_token="
                + weixinToken;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("access_token")) {
                        weixinToken = (String) json.get("access_token");
                        weixinUUID = json.getString("openid");
                        getUserID(weixinUUID, LoginType.weixin);
                    }
                } catch (JSONException e) {
                    LogUtils.error("refreshToken Error..." + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.error("refreshToken Error");
            }
        });
        mQueue.add(request);
    }

    //判断微信的token是否可用,获取token

    public void getWeiXinToken(final int errCode, final String code) {

        if (errCode != 0) {
            //微信登录失败的处理
            LogUtils.error("微信的errCode不为0");
            ViewUtils.toastShort("授权失败");
            return;
        }
        getWeixin(code);

        if (1 == 1) {
            return;
        }
        //先从本地获取token
        if (weixinToken == null || weixinToken.equals("")) {
            getAccessToken(errCode, code);
            LogUtils.debug("----------------------weixinToken-------------------");
            LogUtils.debug(weixinToken);
            return;
        }


        String url = "https://api.weixin.qq.com/sns/auth?access_token=" + weixinToken +
                "&openid=" + WX_APP_ID;

        LogUtils.debug("weixinlogin URL"+url);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("errcode")) {
                        int errCode = json.getInt("errcode");
                        if (errCode == 0) {
                            refreshToken();
                            LogUtils.debug("weixinlogin 5");
                            return;
                        }
                    }
                    getAccessToken(errCode, code);
                    LogUtils.debug("weixinlogin 6");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getAccessToken(errCode, code);
            }
        });
        mQueue.add(request);
    }

    private void getWeixin(String code) {
        LogUtils.debug("开始获取微信信息");

        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("code", code);
        map.put("u_ctype", "1");
        map.put("u_keep", "1");
        //http://account.xs.cn/api/wechatapplogin
        PostRequest request = new PostRequest(URLConstants.WX_LOGIN, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtils.debug(response.toString());

                        if (!ResponseHelper.isSuccess(response)) {
                            ViewUtils.toastShort("本次第三方登录失败1");
//                            ViewUtils.toastShort("1");
                            return;
                        }

                        ViewUtils.toastShort("登录成功");
                        setCookieAndUid(response);

                        if (listener != null)
                        AppManager.getAppManager().finishActivity(activity);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort("本次第三方登录失败");
                ViewUtils.toastShort("2");
                ReportUtils.reportError(new Throwable(error.getMessage()));
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    private void setCookieAndUid(JSONObject response) {

        SharePrefHelper.setCurLoginUserBookshelfUpdateTime(0);
        try {
            JSONObject data = ResponseHelper.getVdata(response);
            String cookie;
            if (data.has("cookie_str")) {
               cookie = data.getString("cookie_str");
               SharePrefHelper.setCookie(cookie);
            }
            if (data.has("u_mid")) {
                String uid = data.getString("u_mid");
                User u = new User();
                u.setUmid(uid);
                UserHelper.getInstance().setUser(u);
            }
        } catch (Exception e) {
        }
    }

    //qq空间分享
    public void shareToQzone(String httpUrl, String iconUrl, String title, String description) {
        if (httpUrl == null || title == null || description == null) {
            LogUtils.debug("微信分享参数问题");
            return;
        }
        if (mTencent == null) {
            mTencent = Tencent.createInstance(QQLOGIN_APP_ID, activity);
//				qqToken = mTencent.getQQToken();
//				qq_user_info = new UserInfo(this.activity, qqToken);
        }
        //分享类型
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare
                .SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, httpUrl);//必填

        ArrayList<String> a = new ArrayList<String>();
        a.add(iconUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, a);
        if (qqAuthListener == null) {
            qqAuthListener = new QQBaseUiListener();
        }
        System.out.println(" targetUrl>>>>>>>>>>>" + httpUrl);

        mTencent.shareToQzone(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                LogUtils.debug("分享完成");
                ViewUtils.toastShort("分享完成");
            }

            @Override
            public void onError(UiError uiError) {
                LogUtils.debug("分享失败");
                ViewUtils.toastShort("分享失败");
            }

            @Override
            public void onCancel() {
                LogUtils.debug("分享关闭");
                ViewUtils.toastShort("分享关闭");
            }
        });
    }

    //微信分享相关 isToFriend true 为分享到朋友 ，false 为分享到朋友圈
    public void shareWxWebPage(String httpUrl, boolean isToFriend, int iconRes, String title,
                               String description) {
        if (httpUrl == null || title == null || description == null) {
            LogUtils.debug("微信分享参数问题");
            return;
        }
        if (WXApi == null) {
            WXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID);
            WXApi.registerApp(WX_APP_ID);
        }
        if (WXApi != null && WXApi.isWXAppInstalled()) {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), iconRes);
            shareWxWebPage(httpUrl, isToFriend, icon, title, description);
        } else {
            ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_install_wx_client));
        }
    }

    public void shareWxWebPage(final String httpUrl, final boolean isToFriend, final String iconUrl, final String title,
                               final String description) {
        if (httpUrl == null || title == null || description == null) {
            LogUtils.debug("微信分享参数问题");
            return;
        }
        if (WXApi == null) {
            WXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID);
            WXApi.registerApp(WX_APP_ID);
        }
        if (WXApi != null && WXApi.isWXAppInstalled()) {
            new Thread(
            ) {
                @Override
                public void run() {
                    super.run();
                    try {
                        Bitmap b = Glide.with(activity).load(iconUrl).asBitmap().into(50, 50).get(5, TimeUnit.SECONDS);
                        shareWxWebPage(httpUrl, isToFriend, b, title, description);
                    } catch (TimeoutException e) {
                        ViewUtils.toastShort("图片分享失败");
                    } catch (InterruptedException e) {
                        ViewUtils.toastShort("图片分享失败");
                    } catch (ExecutionException e) {
                        ViewUtils.toastShort("图片分享失败");
                    }
                }
            }.start();

        } else {
            ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_install_wx_client));
        }
    }

    private void shareWxWebPage(String httpUrl, boolean isToFriend, Bitmap icon, String title,
                                String description) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.thumbData = bmpToByteArray(icon);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = !isToFriend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req
                .WXSceneSession;

        WXApi.sendReq(req);
    }

    public boolean isWXLogin() {
        return WXApi != null;
    }

    /**
     * 是否安装客户端
     *
     * @return
     */
    public boolean isInstallWXClient() {
        boolean installed = false;
        if (WXApi == null) {
            WXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID);
            WXApi.registerApp(WX_APP_ID);
        }
        if (WXApi.isWXAppInstalled()) {
            installed = true;
        }
        return installed;

    }


    /**
     * 客户端是否支持API
     *
     * @return
     */
    public boolean isSupportWXAPI() {
        boolean isSupported = false;
        if (WXApi == null) {
            WXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID);
            WXApi.registerApp(WX_APP_ID);
        }
        if (WXApi.isWXAppSupportAPI()) {
            isSupported = true;
        }
        return isSupported;
    }

    //微博分享
    public void shareWb(String httpUrl, int iconRes, String title, String description) {
        if (mWeiboShareAPI == null) {
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, LoginHelper.WB_APP_KEY);
        }
        mWeiboShareAPI.registerApp();
//        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
//            ViewUtils.toastShort("安装微博后才能分享");
//        }
        //分享
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(description);
        weiboMessage.mediaObject = getWebpageObj(httpUrl, iconRes, title, description);
        weiboMessage.imageObject = getImageObj(iconRes);
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
        LogUtils.debug("supportApi==" + supportApi);
        AuthInfo authInfo = new AuthInfo(activity, WB_APP_KEY, WB_REDIRECT_URL, WB_SCOPE);
        String token = "";
        if (weiboToken != null) {
            token = weiboToken.getToken();
        }

        mWeiboShareAPI.sendRequest(activity, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                ViewUtils.toastShort("分享成功");
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void shareWb(final String httpUrl, final Bitmap bitmap, final String title, final String description) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(description);
        weiboMessage.mediaObject = getWebpageObj(httpUrl, bitmap, title, description);
        weiboMessage.imageObject = getImageObj(bitmap);
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
        AuthInfo authInfo = new AuthInfo(activity, WB_APP_KEY, WB_REDIRECT_URL, WB_SCOPE);
        String token = "";
//        if (weiboToken != null) {
//            token = weiboToken.getToken();
//        }
//        mWeiboShareAPI.sendRequest(activity,request);

        mWeiboShareAPI.sendRequest(activity, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                ViewUtils.toastShort("分享成功");
            }

            @Override
            public void onWeiboException(WeiboException e) {
                System.out.println("分享失败-----" + e);
            }

            @Override
            public void onCancel() {
                System.out.println("分享---onCancel");
            }
        });
    }

    public void shareWb(final String httpUrl, final String iconUrl, final String title, final String description) {
        if (mWeiboShareAPI == null) {
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, LoginHelper.WB_APP_KEY);
        }
        mWeiboShareAPI.registerApp();
        int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
        LogUtils.debug("supportApi==" + supportApi);
        new Thread(
        ) {
            @Override
            public void run() {
                super.run();
                try {
                    Bitmap b = Glide.with(activity).load(iconUrl).asBitmap().into(300, 300).get(5, TimeUnit.SECONDS);
                    shareWb(httpUrl, b, title, description);
                } catch (TimeoutException e) {
                    ViewUtils.toastShort("图片分享失败");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    ViewUtils.toastShort("图片分享失败");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    ViewUtils.toastShort("图片分享失败");
                    e.printStackTrace();
                }
            }
        }.start();


    }

    public void WBOnResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ViewUtils.toastLong("分享成功");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ViewUtils.toastLong("取消分享");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                ViewUtils.toastLong("分享失败");
                break;
        }
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(int iconRes) {
        ImageObject imageObject = new ImageObject();
        //        设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), iconRes);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    private ImageObject getImageObj(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(String httpUrl, int iconRes, String title, String
            description) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = com.sina.weibo.sdk.utils.Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), iconRes);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = httpUrl;
        mediaObject.defaultText = "什么连接";
        return mediaObject;
    }

    private WebpageObject getWebpageObj(String httpUrl, Bitmap bitmap, String title, String
            description) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = com.sina.weibo.sdk.utils.Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = httpUrl;
        mediaObject.defaultText = "什么连接";
        return mediaObject;
    }

    //获取用户信息
    public void getUserID(String uuid, LoginType type) {
    }

    public void setListener(HelpListener listener) {
        this.listener = listener;
    }

    public enum LoginType {
        qq(1), weibo(2), weixin(3);
        private int value;

        LoginType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public interface HelpListener {
        void onSuccessComplete();
    }

    class QQBaseUiListener implements IUiListener {
        @Override
        public void onError(UiError e) {
            ViewUtils.toastShort(LOGINFAILEDTEXT);
            ReportUtils.reportError(new Throwable(e.errorMessage));
        }

        @Override
        public void onCancel() {
            ViewUtils.toastShort(LOGINCANCLETEXT);
        }

        @Override
        public void onComplete(Object arg0) {
            ViewUtils.toastShort(LOGINSUCCESSTEXT);
            JSONObject json = (JSONObject) arg0;
            try {
                getQQUserDetail(json.getString("access_token"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!json.isNull("openid")) {
                try {
                    qqUUID = json.getString("openid");
                    getUserID(qqUUID, LoginType.qq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //微博登录监听
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            ViewUtils.toastShort(LOGINSUCCESSTEXT);
            // 从 Bundle 中解析 Token
            weiboToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
//			String phoneNum = weiboToken.getPhoneNum();
            if (weiboToken.isSessionValid()) {
                // 显示 Token
                //            	weiboToken.getToken()
                // 保存 Token 到 SharedPreferences
                //登录成功
                weiboUUID = weiboToken.getUid();
                getUserID(weiboUUID, LoginType.weibo);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ViewUtils.toastShort(LOGINFAILEDTEXT);
        }

        @Override
        public void onCancel() {
            ViewUtils.toastShort(LOGINCANCLETEXT);
        }
    }
}
