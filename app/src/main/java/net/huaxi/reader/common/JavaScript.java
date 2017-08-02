package net.huaxi.reader.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.BookDetailActivity;
import net.huaxi.reader.activity.ClassifyActivity;
import net.huaxi.reader.activity.LoginActivity;
import net.huaxi.reader.activity.RegisterSendActivity;
import net.huaxi.reader.activity.SimpleShareWebViewActivity;
import net.huaxi.reader.activity.SimpleWebViewActivity;
import net.huaxi.reader.bean.BookDetailBean;
import net.huaxi.reader.bean.CatalogBean;
import net.huaxi.reader.bean.ClassifyDataBean;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.util.EncodeUtils;
import net.huaxi.reader.util.LoginHelper;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.WebView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZMW on 2015/12/14.
 */
public class JavaScript {
    public static final String NAME = "jiuyuu";
    private Activity activity;
    private WebView webView;
    private Handler handler;
    private final int CHANNEL_WEIXIN = 2;
    private final int CHANNEL_ALIPAYY = 1;
    private final int CHANNEL_QQWALLET = 4;

    public interface JavaScriptStateCallBack {
        void StateCallBack(boolean b);
    }

    JavaScriptStateCallBack callBack;

    public JavaScript(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    public JavaScript(Activity activity, WebView webView, JavaScriptStateCallBack callBack) {
        this.activity = activity;
        this.webView = webView;
        this.callBack = callBack;
    }

    public JavaScript(Activity activity, WebView webView, Handler handler) {
        this.activity = activity;
        this.webView = webView;
        this.handler = handler;
    }


    /**
     *
     * @param ct 充值渠道类型
     *            PAY_CHANNEL_ALIPAY	= 1;     const PAY_CHANNEL_WECHAT	= 2;    const
     *            PAY_CHANNEL_APPLE		= 3;
     * @param pt 充值产品类型
     * @param money 充值金额
     * @param body
     */
    /**
     * @param ct          充值渠道类型 PAY_CHANNEL_ALIPAY	= 1;     const PAY_CHANNEL_WECHAT	= 2;
     *                    const PAY_CHANNEL_APPLE		= 3;
     * @param pt          充值产品类型
     * @param nProductSId 苹果用的sid
     * @param money       充值金额
         * @param body     显示
     * @param checksum    校验和
     */
    @JavascriptInterface
    public void pay(String ct, String pt, String nProductSId, String money, String body, String
            checksum) {
        LogUtils.debug("调用第三方支付");
        LogUtils.debug("pt==" + pt);
        LogUtils.debug("ct==" + ct);
        LogUtils.debug("money==" + money);
        LogUtils.debug("body==" + body);

        float fee = 0;
        int channeltype = 0;
        try {
            fee = Float.parseFloat(money);
            channeltype = Integer.parseInt(ct);
        } catch (Exception e) {
        }
        if (fee != 0 && channeltype == CHANNEL_WEIXIN) {
            if (body.contains("阅读币")) {//充值
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.RECHARGE_WEIXIN);//微信支付
//                System.out.println("微信--阅读币");
            } else {
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.VIP_WEIXIN);//微信支付
//                System.out.println("微信--会员");
            }
            IWXAPI mWeixinAPI = WXAPIFactory.createWXAPI(activity, LoginHelper.WX_APP_ID, false);
            if (!mWeixinAPI.isWXAppSupportAPI()) {
                ViewUtils.toastShort(activity.getString(R.string.not_install_wx_client));
                return;
            }
            Dialog dialog = ViewUtils.showProgressDialog(activity);
            PayUtils.WxPay(activity, money, body, pt, checksum, dialog);
            return;
        } else if (fee != 0 && channeltype == CHANNEL_ALIPAYY) {
            if (body.contains("阅读币")) {//充值
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.RECHARGE_ALIPAY);//支付宝支付
//                System.out.println("支付宝--阅读币");
            } else {
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.VIP_ALIPAY);//支付宝支付
//                System.out.println("支付--会员");
            }
            Dialog dialog = ViewUtils.showProgressDialog(activity);
            LogUtils.debug("Alipay_here 0");
            PayUtils.AliPay(activity, money, body, pt, checksum, dialog);
            return;
        } else if (fee != 0 && channeltype == CHANNEL_QQWALLET) {
            if (body.contains("阅读币")) {//充值
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.RECHARGE_QQ);//支付宝支付
            } else {
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.VIP_QQ);//支付宝支付
            }
            Dialog dialog = ViewUtils.showProgressDialog(activity);
            PayUtils.QQPay(activity, money, body, pt, checksum, dialog);
            return;
        }
        LogUtils.debug("调用js参数错误");
    }


    @JavascriptInterface
    public void detail(String bookid) {
        LogUtils.debug("detail..bookid==" + bookid);
//        Intent intent = new Intent(activity, BookDetailActivity.class);
        Intent intent = new Intent(activity, BookDetailActivity.class);
        intent.putExtra("bookid", bookid);
        activity.startActivity(intent);
        UMEventAnalyze.countEvent(activity, UMEventAnalyze.BOOKCITY_START_BOOKINFO);
    }

    /**
     * 去书城二级web页
     * @param url   web页的url
     * @param title web页顶title部显示
     */
    @JavascriptInterface
    public void goreading(String url, String title) {
        LogUtils.debug("goreading...url==" + url + ":title==" + title);
        Intent intent = new Intent(activity, SimpleWebViewActivity.class);
        intent.putExtra("weburl", url);
        intent.putExtra("webtitle", title);
        intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_CLASSIFY);
        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void umstatistics(String eventid) {
        if (eventid != null) {
            UMEventAnalyze.countEvent(activity, eventid);
        }
    }

    /**
     * 监听页面状态
     *
     * @param isShow
     */
    @JavascriptInterface
    public void setmenustatus(final boolean isShow) {
        LogUtils.debug("setmenustatus ===" + isShow);
        if (handler != null) {
            Message msg = new Message();
            msg.what = SimpleWebViewActivity.WEBVIEW_NAVIGATIONBAR_STATE;
            msg.obj = isShow;
            handler.sendMessage(msg);
        }
    }

    @JavascriptInterface
    public void goback() {
        if (handler != null) {
            handler.sendEmptyMessage(SimpleWebViewActivity.WEBVIEW_GOBACK);
        }
    }


    private static final String NATIVE_PAGE_LOGIN = "login";
    private static final String NATIVE_PAGE_REGISTER = "register";
    private static final String NATIVE_PAGE_RECHARGE = "recharge";
    private static final String NATIVE_PAGE_VIP = "vip";
    private static final String NATIVE_PAGE_READING = "reading";

    @JavascriptInterface
    public void goNaitvePage(String action, final String var) {
        if (StringUtils.isBlank(action)) {
            return;
        }
        switch (action) {
            case NATIVE_PAGE_LOGIN:
                activity.startActivity(new Intent(activity, LoginActivity.class));
                break;
            case NATIVE_PAGE_REGISTER:
                activity.startActivity(new Intent(activity, RegisterSendActivity.class));
                break;
            case NATIVE_PAGE_RECHARGE:
                if (UserHelper.getInstance().isLogin()) {
                    Intent intent1 = new Intent(activity, SimpleWebViewActivity.class);
                    intent1.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_RECHARGE);
                    activity.startActivity(intent1);
                } else {
                    ViewUtils.toastShort("请先登录再充值");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
                break;
            case NATIVE_PAGE_VIP:
                if (UserHelper.getInstance().isLogin()) {
                    Intent intent2 = new Intent(activity, SimpleWebViewActivity.class);
                    intent2.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_VIP);
                    activity.startActivity(intent2);
                } else {
                    ViewUtils.toastShort("请先登录再包月");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
                break;
            case NATIVE_PAGE_READING:
                if (StringUtils.isBlank(var)) {
                    return;
                } else {
                    LogUtils.debug("here.........");
                    if (BookDao.getInstance().hasKey(var) == 1) {
                        EnterBookContent.openBookContent(activity, var);
                        return;
                    }
                    GetRequest request = new GetRequest(String.format(URLConstants.GET_BOOKDETAIL,EncodeUtils.encodeString_UTF8(var) + CommonUtils.getPublicGetArgs()
                    ), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    LogUtils.debug("response==" + response.toString());
                                    if (!ResponseHelper.isSuccess(response)) {
                                        return;
                                    }

                                    JSONObject jsonObject = ResponseHelper.getVdata(response);
                                    if (jsonObject != null) {
                                        Type type = new TypeToken<ArrayList<BookDetailBean>>() {
                                        }.getType();
                                        List<BookTable> bookTables = new Gson().fromJson(jsonObject
                                                .optJSONArray("list").toString(), type);
                                        if (bookTables != null && bookTables.size() > 0) {
                                            BookTable bookDetail = bookTables.get(0);
                                            bookDetail.setBookDesc(bookDetail.getBookDesc().replaceAll("\\t", ""));
                                            if (bookDetail != null && BookDao.getInstance().hasKey(bookDetail.getBookId()) == 0) {
                                                BookDao.getInstance().addBook(bookDetail);
                                            }
                                            LogUtils.debug("there.....");
                                            EnterBookContent.openBookContent(activity, var);
                                        } else {
                                        }
                                    } else {
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    RequestQueueManager.addRequest(request);
                }
                break;
            default:
                break;
        }
    }

    @JavascriptInterface
    public void openActivity(String classname) {
        try {
            Class cla = Class.forName(classname);
            Intent intent = new Intent(activity, cla);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void goShareWebview(String title, String weburl, String imgurl, String desc) {
        LogUtils.debug("goShareWebview");
        Intent intent = new Intent(activity, SimpleShareWebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("weburl", weburl);
        intent.putExtra("imgurl", imgurl);
        intent.putExtra("desc", desc);
        activity.startActivity(intent);
    }

    @JavascriptInterface
    public boolean hotclassify(String cateId, String cateTitle, String jsonData) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ClassifyDataBean>>() {
        }.getType();
        List<ClassifyDataBean> classityList = gson.fromJson(jsonData, type);
        if (classityList != null) {
            CatalogBean bean = new CatalogBean();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < classityList.size(); i++) {
                ClassifyDataBean classifyDataBean = classityList.get(i);
                map.put(classifyDataBean.getCat_mid(), classifyDataBean.getCat_name());
            }
            bean.setName(cateTitle);
            bean.setId(cateId);
            bean.setSubclass(map);
            Intent intent = new Intent(activity, ClassifyActivity.class);
            intent.putExtra("obj", bean);
            activity.startActivity(intent);
        }

        return false;
    }


}


