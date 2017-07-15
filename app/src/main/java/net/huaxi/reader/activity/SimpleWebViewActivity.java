package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import com.umeng.analytics.MobclickAgent;

import net.huaxi.reader.R;
import net.huaxi.reader.common.BroadCastConstant;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.WebView;

/**
 * Created by ZMW on 2015/12/21.
 * 简单的webview,左边是返回按钮，右边是文字
 */
public class SimpleWebViewActivity extends BaseActivity {
    public static final String WEBTYPE = "webtype";
    //充值的网页
    public static final String WEBTYPE_RECHARGE = "recharge";
    //会员的网页
    public static final String WEBTYPE_VIP = "vip";
    //分类2级页面
    public static final String WEBTYPE_CLASSIFY = "classify";
    //用户许可协议页面
    public static final String WEBTYPE_USER_PROTOCOL = "user_protocol";
    //回馈奖励活动
    public static final String WEBTYPE_REWARDS = "rewards";
    //帮助中心
    public static final String WEBTYPE_HELP_CENTER = "help_center";
    //书架页面跳的活动页面
    public static final String WEBTYPE_ACTIVITY = "activity";
    //旧账号登录须知
    public static final String WEBTYPE_LOGIN_INSTRUCTIONS = "login_instructions";
    //执行javascript返回
    public static final int WEBVIEW_NAVIGATIONBAR_STATE = 10001;//网页状态是否有弹出框
    public static final int WEBVIEW_GOBACK = 10002;//回到过去页面
    //执行网页跳转
    public static final int Pay_coins_successfully = 1;//支付成功
    public static final int Pay_hire_successfully = 2;//会员成功
    public static final int Pay_coins_failed = 3;//支付失败
    public static final int Pay_hire_failed = 4;//会员失败
    String weburl = "javascript:document.body.innerHTML=\"\"";
    String type;
    SwipeRefreshLayout ptrFrameLayout;
    WebView webView;
    ImageView ivBack;
    TextView tvCenter;
    TextView tvRight;
    View rlNetError;//网络错误
    private int pageCount = 1;//加载网页数
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WEBVIEW_NAVIGATIONBAR_STATE:
                    boolean b = (Boolean) msg.obj;
                    if (b) {
                        webView.loadUrl("javascript:payfunc_hide_menu()");
                    } else {
                        finishActivity();
                    }
                    break;
                case WEBVIEW_GOBACK:
                    doChargeKeyBack();
                    break;
                default:
                    break;
            }
        }
    };
    //有关微信跳转发送的广播
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastConstant.WXPAYCALLBACK == intent.getAction() || BroadCastConstant.QQPAYCALLBACK == intent.getAction()) {
                mSuccess = intent.getIntExtra("success", 0);
                if (mSuccess == 1) {
                    if (WEBTYPE_VIP.equals(type)) {
                        setWebView(Pay_hire_successfully);
                    } else if (WEBTYPE_RECHARGE.equals(type)) {
                        setWebView(Pay_coins_successfully);
                    }
                } else {
                    if (WEBTYPE_VIP.equals(type)) {
                        setWebView(Pay_hire_failed);
                    } else if (WEBTYPE_RECHARGE.equals(type)) {
                        setWebView(Pay_coins_failed);
                    }
                }
            }
        }
    };
    private int mSuccess;

    public static void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String cookie = SharePrefHelper.getCookie();
        String[] cs = cookie.split(";");
        for (String c : cs) {
            cookieManager.setCookie(url, c + "");//cookies是在HttpClient中获得的cookie
            LogUtils.debug("SharePrefHelper.getCookie()==" + c + "");
        }
        CookieSyncManager.getInstance().sync();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNeedStatistics(false);
        //得到view视图窗口
        Window window = getActivity().getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.c01_themes_color));
        setContentView(R.layout.activity_simpleweb);
        initView();
        initCustom();
        if (weburl == null) {
            return;
        }
        webView.loadUrl(weburl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName() + type);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName() + type);
    }

    private void initCustom() {
        type = getIntent().getStringExtra(WEBTYPE);

        if (StringUtils.isBlank(type)) {
            return;
        }
        if (WEBTYPE_RECHARGE.equals(type)) {
            tvCenter.setVisibility(View.GONE);
            tvRight.setText("充值记录");
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SimpleWebViewActivity.this, RechargeRecordActivity.class);
                    startActivity(intent);
                    UMEventAnalyze.countEvent(SimpleWebViewActivity.this, UMEventAnalyze.RECHARGE_HISTORY);
                }
            });
            weburl = URLConstants.H5PAGE_CHARGE; //+CommonUtils.getPublicGetArgs();
            UMEventAnalyze.countEvent(this, UMEventAnalyze.RECHARGE_PAGE);//充值页面
        } else if (WEBTYPE_VIP.equals(type)) {
            tvRight.setVisibility(View.GONE);
            tvCenter.setText("会员");
            tvCenter.setVisibility(View.VISIBLE);
            weburl = URLConstants.H5PAGE_VIP + CommonUtils.getPublicGetArgs();
            UMEventAnalyze.countEvent(this, UMEventAnalyze.VIP_PAGE);//会员页面
        } else if (WEBTYPE_CLASSIFY.equals(type)) {
            tvRight.setVisibility(View.GONE);
            String fenlei = getIntent().getStringExtra("webtitle") == null ? "" : getIntent()
                    .getStringExtra("webtitle");
            tvCenter.setText(fenlei);
            tvCenter.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) findViewById(R.id.simpleweb_right_search_imageview);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SimpleWebViewActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            });
            weburl = getIntent().getStringExtra("weburl") == null ? "" : getIntent()
                    .getStringExtra("weburl");
            if (weburl != null) {
                if (weburl.indexOf("?") > 0) {
                    weburl += CommonUtils.getPublicGetArgs();
                } else {
                    weburl += "?1=1" + CommonUtils.getPublicGetArgs();
                }
            }
        } else if (WEBTYPE_USER_PROTOCOL.equals(type)) {
            tvRight.setVisibility(View.GONE);
            tvCenter.setText(R.string.user_privacy_protection_agreement);
            tvCenter.setVisibility(View.VISIBLE);
            weburl = "file:///android_asset/protocol.html";
            ptrFrameLayout.setEnabled(false);
        } else if (WEBTYPE_REWARDS.equals(type)) {
            tvRight.setVisibility(View.GONE);
            tvCenter.setText("老用户须知");
            tvCenter.setVisibility(View.VISIBLE);
            weburl = "file:///android_asset/rewards/rewards.html";
            ptrFrameLayout.setEnabled(false);
        } else if (WEBTYPE_HELP_CENTER.equals(type)) {
            tvRight.setVisibility(View.GONE);
            tvCenter.setText("帮助中心");
            tvCenter.setVisibility(View.VISIBLE);
            weburl = URLConstants.H5PAGE_HELP + CommonUtils.getPublicGetArgs();
            ptrFrameLayout.setEnabled(false);
        } else if (WEBTYPE_ACTIVITY.equals(type)) {
            tvRight.setVisibility(View.GONE);
            String title = getIntent().getStringExtra("webtitle") == null ? "" : getIntent()
                    .getStringExtra("webtitle");
            tvCenter.setText(title);
            tvCenter.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
            weburl = getIntent().getStringExtra("weburl") == null ? "" : getIntent()
                    .getStringExtra("weburl");

        } else if (WEBTYPE_LOGIN_INSTRUCTIONS.equals(type)) {
            tvRight.setVisibility(View.GONE);
            tvCenter.setText("旧账号登录须知");
            tvCenter.setVisibility(View.VISIBLE);
            weburl = "file:///android_asset/login/login_instructions.html";
            ptrFrameLayout.setEnabled(false);
        }
        LogUtils.debug("weburl==" + weburl);
        synCookies(this, URLConstants.PAY_URL);
    }

    private void initView() {
        ptrFrameLayout = (SwipeRefreshLayout) findViewById(R.id.simpleweb_ptrFrameLayout);
        ptrFrameLayout.setColorSchemeResources(R.color.c01_themes_color);
        ptrFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(weburl);
            }
        });

        webView = (WebView) findViewById(R.id.simpleweb_webview);
        webView.addJavascriptInterface(new JavaScript(SimpleWebViewActivity.this, webView, handler),
                JavaScript.NAME);
        webView.setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                rlNetError.setVisibility(View.GONE);
                ptrFrameLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                ptrFrameLayout.setRefreshing(false);
                rlNetError.setVisibility(View.VISIBLE);
                webView.loadUrl("javascript:document.body.innerHTML=\"\"");
            }
        });
        ptrFrameLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        ivBack = (ImageView) findViewById(R.id.simpleweb_back_imageview);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyBack();
            }
        });
        tvCenter = (TextView) findViewById(R.id.simpleweb_center_textview);
        tvRight = (TextView) findViewById(R.id.simpleweb_right_textview);
        rlNetError = findViewById(R.id.simpleweb_neterror_layout);
        rlNetError.setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtils.checkNetworkUnobstructed()) {
                    ViewUtils.toastShort(getString(R.string.not_available_network));
                    return;
                }
                if (!ptrFrameLayout.isRefreshing()) {
                    ptrFrameLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            ptrFrameLayout.setRefreshing(true);
                        }
                    });
                    rlNetError.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onKeyBack() {

        switch (type) {
            case WEBTYPE_RECHARGE:
            case WEBTYPE_VIP:
                doChargeKeyBack();
                break;
            case WEBTYPE_CLASSIFY:
                finishActivity();
                break;
            default:
                if (webView.canGoBack()) {
                    webView.goBack();
                    return;
                } else {
                    finishActivity();
                }
                break;
        }
    }


    private void finishActivity() {
        finish();
        if (WEBTYPE_VIP.equals(type)) {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.VIP_CANCEL);
        } else if (WEBTYPE_RECHARGE.equals(type)) {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.RECHARGE_CANCEL);
        }
    }


    //充值的返回按钮
    private void doChargeKeyBack() {
        //1判断我是在那个网页
        if (pageCount == 1) {
            finishActivity();
            webView.loadUrl("javascript:payfunc_is_menu_shown()");
        } else {
            webView.goBack();
            if ((WEBTYPE_VIP.equals(type) || WEBTYPE_RECHARGE.equals(type)) && mSuccess == 1) {
                webView.reload();
            }
            pageCount = 1;
            ptrFrameLayout.setEnabled(true);
        }
    }

    public void setWebView(int paystate) {
        switch (paystate) {
            case Pay_coins_successfully:
               //webView.loadUrl("http://pay.xs.cn/view/product/paysuccess/coins?1=1" + CommonUtils.getPublicGetArgs());
                webView.loadUrl("https://api.hxdrive.net/pay/paysuccess?1=1" + CommonUtils.getPublicGetArgs());
                ptrFrameLayout.setEnabled(false);
                pageCount = 2;
                break;
            case Pay_hire_successfully:
                //webView.loadUrl("http://pay.xs.cn/view/product/paysuccess/hire?1=1" + CommonUtils.getPublicGetArgs());
               webView.loadUrl("https://api.hxdrive.net/pay/paysuccess?1=1" + CommonUtils.getPublicGetArgs());
                ptrFrameLayout.setEnabled(false);
                pageCount = 2;
                break;
            case Pay_coins_failed:
               // webView.loadUrl("http://pay.xs.cn/view/product/payfail/coins?1=1" + CommonUtils.getPublicGetArgs());
                webView.loadUrl("https://api.hxdrive.net/pay/payfailed?1=1" + CommonUtils.getPublicGetArgs());
                ptrFrameLayout.setEnabled(false);
                pageCount = 2;
                break;
            case Pay_hire_failed:
                //webView.loadUrl("http://pay.xs.cn/view/product/payfail/hire?1=1" + CommonUtils.getPublicGetArgs());
                webView.loadUrl("https://api.hxdrive.net/pay/payfailed?1=1" + CommonUtils.getPublicGetArgs());
                ptrFrameLayout.setEnabled(false);
                pageCount = 2;
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mReceiver, new IntentFilter(BroadCastConstant.WXPAYCALLBACK));
        registerReceiver(mReceiver, new IntentFilter(BroadCastConstant.QQPAYCALLBACK));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

}
