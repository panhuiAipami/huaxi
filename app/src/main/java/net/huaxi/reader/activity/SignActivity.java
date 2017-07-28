package net.huaxi.reader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.NetUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.WebView;

import java.util.HashMap;
import java.util.Map;

public class SignActivity extends AppCompatActivity {
    private WebView mWebView;
    private ImageView mImageView;
    private LinearLayout net_error;
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
        setContentView(R.layout.sign);
        fm_net_error= (LinearLayout)findViewById(R.id.fm_net_error);
        mStoreWebview= (WebView)findViewById(R.id.store_webview);
        mImageView= (ImageView) findViewById(R.id.sign_record_back_imageview);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        initData();
    }

//    private void initData() {
//        if(NetUtils.checkNet() == NetType.TYPE_NONE){
//            net_error.setVisibility(View.VISIBLE);
//            mWebView.setVisibility(View.GONE);
//        }else {
//            mWebView.setVisibility(View.VISIBLE);
//            net_error.setVisibility(View.GONE);
//        }
//        mWebView.loadUrl(URLConstants.H5PAGE_SELECTION);
//    }
//
//    private void initView() {
//        net_error= (LinearLayout) findViewById(R.id.sign_record_net_layout);
//        mWebView= (WebView) findViewById(R.id.sign_webView);
//        mImageView= (ImageView) findViewById(R.id.sign_record_back_imageview);
//        mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        webSettings(mWebView);
//        mWebView.setLoadListener(new WebView.WebViewLoadingListener() {
//            @Override
//            public void onFinished(android.webkit.WebView view, String url) {
//                LogUtils.debug("刷新结束");
////                mStoreRefresh.setRefreshing(false);
//                net_error.setVisibility(View.GONE);
//                mWebView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onError() {
////                mStoreRefresh.setRefreshing(false);
//                net_error.setVisibility(View.VISIBLE);
//                mWebView.setVisibility(View.GONE);
////                mStoreWebview.loadUrl("javascript:document.body.innerHTML=\"\"");
//            }
//        });
//    }
//    /**
//     * 设置缓存
//     *
//     * @param webView
//     */
//    private void webSettings(WebView webView) {
//        webView.addJavascriptInterface(new JavaScript(SignActivity.this, webView), JavaScript.NAME);
//
//    }


    private void initData() {
        //再添加一个判断网络状态，如果没网则WebView不加载数据
        if(NetUtils.checkNet() != NetType.TYPE_NONE){
            String cookie = SharePrefHelper.getCookie();
            Map<String,String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("usertoken",cookie);
            mStoreWebview.loadUrl( "http://api.hxdrive.net/user/signin?format=html",extraHeaders);
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
        webView.addJavascriptInterface(new JavaScript(SignActivity.this, webView), JavaScript.NAME);

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
}
