package net.huaxi.reader.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.Utils;

import net.huaxi.reader.R;

/**
 * Created by ZMW on 2015/12/8.
 */
public class WebView extends android.webkit.WebView {
    private static final int WEBVIEW_TIME_OUT = 1;

    WebViewLoadingListener loadingListener;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WEBVIEW_TIME_OUT:
                    stopLoading();
                    break;
            }
        }
    };
    private ProgressBar progressBar;

    public WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        setVerticalScrollBarEnabled(false);
        //加载进度条
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color));
        int width = LayoutParams.MATCH_PARENT;
        int height = Utils.dip2px(context, 2);
        progressBar.setLayoutParams(new LayoutParams(width, height, 0, 0));
        addView(progressBar);

        // 配置WebView
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setLoadsImagesAutomatically(true);
        getSettings().setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);// 提高渲染的优先级

        //设置默认的字体大小
        getSettings().setDefaultFontSize(16);
        //是内容与手机适配
        getSettings().setUseWideViewPort(true);
        //能够的调用JavaScript代码
        getSettings().setDefaultTextEncodingName("utf-8");

        initMixedContentMode();

        setWebViewClient(new WebViewClient() {
            /**
             * 开始
             * @param view
             * @param url
             * @param favicon
             */
            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            /**
             出现ssl错误
             *
             */
            @Override
            public void onReceivedSslError(android.webkit.WebView view, SslErrorHandler handler,
                                           SslError error) {
//                super.onReceivedSslError(view, handler, error);
                LogUtils.debug("onReceivedSslError");
                // android 4.1 无法使用https访问支付页面，和会员页面。修改webview接收所有网络证书
                handler.proceed();
                if (loadingListener != null && urlType == HTTPSTATE) {
                    loadingListener.onError();
                    handler.removeMessages(WEBVIEW_TIME_OUT);
                }
            }

            /**
             * 错误
             * @param view
             * @param request
             * @param error
             */
            @Override
            public void onReceivedError(android.webkit.WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogUtils.debug("onReceivedError");
                if (loadingListener != null && urlType == HTTPSTATE) {
                    loadingListener.onError();
                    handler.removeMessages(WEBVIEW_TIME_OUT);
                }
            }

            /**
             * 加载结束
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                getSettings().setBlockNetworkImage(false);
                if (loadingListener != null) {
                    if (NetUtils.checkNetworkUnobstructed()) {
                        loadingListener.onFinished(view, url);
                    } else {
                        if (!url.equals("data:text/html,chromewebdata") && urlType == HTTPSTATE) {
                            LogUtils.debug("onPageFinishedError");
                            loadingListener.onError();
                        } else {
                            LogUtils.debug("无网情况下加载空页面");
                        }
                    }
                    handler.removeMessages(WEBVIEW_TIME_OUT);
                }
            }

            /**
             * 连接跳转
             * @param view
             * @param url
             * @return
             */
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                if (url.indexOf("tel:") < 0)
                    view.loadUrl(url);// 点击连接从自身打开
                LogUtils.debug("url===" + url);
                return true;
            }
        });
        setWebChromeClient(new WebChromeClient() {
            /**
             * js的alert
             * @param view
             * @param url
             * @param message
             * @param result
             * @return
             */
            @Override
            public boolean onJsAlert(android.webkit.WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(android.webkit.WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            /**
             * 进度变了
             * @param view
             * @param newProgress
             */
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
//                try {
//                    CrashReport.setJavascriptMonitor(view, true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                if (newProgress == 100) {
                    progressBar.setVisibility(GONE);
                } else {
                    if (progressBar.getVisibility() == GONE)
                        progressBar.setVisibility(VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });


    }

    private int urlType = 0;
    private final int HTTPSTATE = 0;
    private final int FILESTATE = 1;

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        LogUtils.debug("loadurl==" + url);
        if (!url.startsWith("file:")) {
            Message message = Message.obtain();
            message.what = WEBVIEW_TIME_OUT;
            handler.sendMessageDelayed(message, 10000);
            urlType = HTTPSTATE;
        } else {
            urlType = FILESTATE;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
//        lp.x = l;
//        lp.y = t;
//        progressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setLoadListener(WebViewLoadingListener listener) {
        this.loadingListener = listener;
    }

    @SuppressLint("NewApi")
    private void initMixedContentMode() {
        try {
            //主要解决https和http混合内容请求问题。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }

    public interface WebViewLoadingListener {
        void onFinished(android.webkit.WebView view,
                        String url);

        void onError();
    }

}
