package net.huaxi.reader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.NetUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.WebView;

import java.util.HashMap;
import java.util.Map;


public class ComplimentaryrecordActivity extends AppCompatActivity {
    private WebView mWebView;
    private ImageView mImage;
    private LinearLayout net_error;
    private RelativeLayout complimentary_loading_layout;
    //周刊
    private int mSexClassify;
    private boolean flag=false;
    //错误布局
    private LinearLayout fm_net_error;
    private WebView mStoreWebview;
    //无网络图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complimentaryrecord);
        fm_net_error= (LinearLayout)findViewById(R.id.fm_net_error);
        mStoreWebview= (WebView)findViewById(R.id.store_webview);
        mImage= (ImageView) findViewById(R.id.complimentary_record_back_imageview);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();
    }
//
//    private void initData() {
//        complimentary_loading_layout.setVisibility(View.VISIBLE);
////        if(NetUtils.checkNet() == NetType.TYPE_NONE){
////            net_error.setVisibility(View.VISIBLE);
////            mWebView.setVisibility(View.GONE);
////        }else {
////            mWebView.setVisibility(View.VISIBLE);
////            net_error.setVisibility(View.GONE);
////
////        }
//        mWebView.loadUrl(URLConstants.H5PAGE_SELECTION);
//        mWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
////                view.loadUrl(URLConstants.H5PAGE_SELECTION);
//                return true;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
//                handler.proceed();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                complimentary_loading_layout.setVisibility(View.GONE);
//            }
//        });
////        mWebView.setWebChromeClient(new WebChromeClient(){});
//    }
//
//    private void initView() {
//        complimentary_loading_layout= (RelativeLayout) findViewById(R.id.complimentary_loading_layout);
//        mWebView= (WebView) findViewById(R.id.complimentary_webView);
//        net_error= (LinearLayout) findViewById(R.id.complimentary_record_net_layout);
//
//        mImage= (ImageView) findViewById(R.id.complimentary_record_back_imageview);
//        mImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }



    private void initData() {
        //再添加一个判断网络状态，如果没网则WebView不加载数据
        if(NetUtils.checkNet() != NetType.TYPE_NONE){
            String cookie = SharePrefHelper.getCookie();
            Map<String,String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("usertoken",cookie);
            String s = String.format("http://api.hxdrive.net/user/givelog?format=html") + CommonUtils
                    .getPublicGetArgs();
            mStoreWebview.loadUrl(s,extraHeaders);
        }

        mSexClassify = SharePrefHelper.getSexClassify();
        if (mSexClassify == 1) {//女
            UMEventAnalyze.countEvent(this, UMEventAnalyze.BOOKCITY_GIRL);
        } else if (mSexClassify == 2) {//男
            UMEventAnalyze.countEvent(this, UMEventAnalyze.BOOKCITY_BOY);
        }
    }



    /**
     * 设置缓存
     *
     * @param webView
     */
    private void webSettings(WebView webView) {
        webView.addJavascriptInterface(new JavaScript(ComplimentaryrecordActivity.this, webView), JavaScript.NAME);

    }

    private void initView() {
        if(NetUtils.checkNet() == NetType.TYPE_NONE){
            //当前无网络
            mStoreWebview.setVisibility(View.GONE);
            fm_net_error.setVisibility(View.VISIBLE);
        }else {
            mStoreWebview.setVisibility(View.VISIBLE);
            fm_net_error.setVisibility(View.GONE);
        }
//        mStoreRefresh.setOnRefreshListener(this);
//        mStoreRefresh.setColorSchemeResources(R.color.c01_themes_color);
        webSettings(mStoreWebview);
//        getRefresh();
        fm_net_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(NetUtils.checkNet() == NetType.TYPE_NONE){
                    fm_net_error.setVisibility(View.VISIBLE);
                    mStoreWebview.setVisibility(View.GONE);
                }else {
                    fm_net_error.setVisibility(View.GONE);
                    mStoreWebview.setVisibility(View.VISIBLE);

                    //调用刷新的方法

                }


            }
        });
    }
//    public void getRefresh(){
//        mStoreWebview.setLoadListener(new WebView.WebViewLoadingListener() {
//            @Override
//            public void onFinished(android.webkit.WebView view, String url) {
//                LogUtils.debug("刷新结束");
////            mStoreRefresh.setRefreshing(false);
//                fm_net_error.setVisibility(View.GONE);
//                mStoreWebview.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onError() {
////            mStoreRefresh.setRefreshing(false);
//                mStoreWebview.setVisibility(View.GONE);
//                fm_net_error.setVisibility(View.VISIBLE);
//
////            mStoreWebview.loadUrl("javascript:document.body.innerHTML=\"\"");
//                //*********************************************************************************//
//            }
//        });
//    }


}
