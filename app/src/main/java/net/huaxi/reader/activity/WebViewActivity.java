package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.view.WebView;

/**
 * @author panhui
 * 任务h5界面
 */

public class WebViewActivity extends BaseActivity implements  IWeiboHandler.Response{
    public final static String WEB_LOAD_URL = "web_load_url";
    WebView webView;
    String url ;
    MyHandler handler;


    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refresh();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到view视图窗口
        Window window = getActivity().getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(Color.parseColor("#212A3C"));
        handler = new MyHandler();
        AppContext.appContext.setHandler(handler);
        setContentView(R.layout.activity_web_view);
        webView = (WebView) findViewById(R.id.webView);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        url = intent.getStringExtra(WEB_LOAD_URL);
        Log.e("initData","--url = ---"+url);

        if(NetUtils.checkNet() != NetType.TYPE_NONE){
            webView.loadUrl(url);
        }
        webView.addJavascriptInterface(new JavaScript(this, webView), JavaScript.NAME);
        getRefresh();

    }


    public void getRefresh() {
        webView.setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                LogUtils.debug("刷新结束");
//                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
//                webView.setVisibility(View.GONE);

            }
        });
    }


    public static void  goToWebView(Activity a,String url){
        Intent intent = new Intent();
        intent.setClass(a,WebViewActivity.class);
        intent.putExtra(WEB_LOAD_URL,url);
        a.startActivity(intent);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public  void refresh(){
        webView.reload();  //刷新
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (AppContext.getLoginHelper() != null) {
                AppContext.getLoginHelper().ActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (AppContext.getLoginHelper() != null) {
            AppContext.getLoginHelper().NewIntent(intent,this);
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if (AppContext.getLoginHelper() != null) {
            AppContext.getLoginHelper().onResponse(baseResponse);
        }
    }
}
