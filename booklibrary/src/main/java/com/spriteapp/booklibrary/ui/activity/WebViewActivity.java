package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.callback.WebViewCallback;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.enumeration.PayResultEnum;
import com.spriteapp.booklibrary.enumeration.UpdateCommentEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.model.AddBookModel;
import com.spriteapp.booklibrary.model.PayResult;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.PayResponse;
import com.spriteapp.booklibrary.ui.presenter.WebViewPresenter;
import com.spriteapp.booklibrary.ui.view.WebViewView;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.WebViewUtil;
import com.spriteapp.booklibrary.widget.ReaderWebView;

import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class WebViewActivity extends TitleActivity implements WebViewView {

    private static final String TAG = "WebViewActivity";
    public static final String LOAD_URL_TAG = "url";
    public static final String IS_H5_PAY_TAG = "isH5PayTag";
    public static final String IS_READ = "isRead";
    private ReaderWebView mWebView;
    private String mUrl;
    private Context mContext;
    private WebViewPresenter mPresenter;
    private static final int SDK_PAY_FLAG = 1;
    private boolean isH5Pay;
    private String WXURL = "";
    private int IsRead = 0;
    private TextView book_reader_title_textView;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {

                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(WebViewActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(UpdaterPayEnum.UPDATE_PAY_RESULT);
                        if (isH5Pay) {
                            EventBus.getDefault().post(PayResultEnum.SUCCESS);
                            finish();
                            return;
                        }
                        mWebView.reload();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(WebViewActivity.this, "支付失败,请重试", Toast.LENGTH_SHORT).show();
                        if (isH5Pay) {
                            EventBus.getDefault().post(PayResultEnum.FAILED);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };
    private String bookId;

    @Override
    public void initData() {
        mContext = this;
        EventBus.getDefault().register(this);
        mPresenter = new WebViewPresenter();
        mPresenter.attachView(this);
        getDataFromTopPage();
        if (StringUtil.isEmpty(mUrl)) {
            return;
        }
        if (!AppUtil.isNetAvailable(this)) {
            return;
        }
        mWebView.setWebChromeClient(mChromeClient);
//        mWebView.setWebViewClient(mClient);//试一试
        mWebView.loadPage(mUrl, mClient);
        Log.d("murl", mUrl);
        WebViewUtil.getInstance().setWebViewCallback(mWebViewCallback);
    }

    private void getDataFromTopPage() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mUrl = intent.getStringExtra(LOAD_URL_TAG);
        isH5Pay = intent.getBooleanExtra(IS_H5_PAY_TAG, false);
        IsRead = intent.getIntExtra(IS_READ, 0);
        if (IsRead == 1 && mUrl != null && !mUrl.isEmpty()) {
            Uri uri = Uri.parse(mUrl);
            bookId = uri.getQueryParameter(WebConstant.BOOK_ID_QUERY);
        }
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        mWebView = (ReaderWebView) findViewById(R.id.book_reader_web_view);
//        book_reader_title_textView = (TextView) findViewById(R.id.book_reader_title_textView);
//        book_reader_title_textView.setMaxEms(10);
//        mLeftLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (IsRead == 1) {
//                    if (IsRead == 1 && bookId != null && !bookId.isEmpty()) {
//                        BookDetailResponse bookDetailResponse = new BookDetailResponse();
//                        bookDetailResponse.setBook_id(Integer.parseInt(bookId));
//                        bookDetailResponse.setChapter_id(0);
//                        ActivityUtil.toReadActivity(WebViewActivity.this, bookDetailResponse, true);
//                        return;
//                    }
//                } else if (IsRead == 0) {
//                    finish();
//                }
//            }
//        });
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_web_view, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    WebViewClient mClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            dismissDialog();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url == null) return false;
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    Log.d("webViewUrl", "url000===" + url);
                    if (url.startsWith("https://statecheck.swiftpass.cn/pay/wappay") || url.startsWith("http://statecheck.swiftpass.cn/pay/wappay")) {
                        WXURL = url;
                    }
                    return true;
                } else {
                    Log.d("webViewUrl", "url111===" + url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    if (url.startsWith("weixin://")) {
                        if (WXURL != null && !WXURL.isEmpty())
                            view.loadUrl(WXURL);
//                        view.goBack();
                    }
                    return true;
                }
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return false;
            }
        }

    };

    WebChromeClient mChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            book_reader_title_textView.setMaxEms(10);
//            book_reader_title_textView.setText(title);
            setTitle(title);
        }
    };

    private WebViewCallback mWebViewCallback = new WebViewCallback() {
        @Override
        public void getWeChatPay(String productId) {//微信回调
            mPresenter.requestWeChatPay(productId);
        }

        @Override
        public void getAliPay(String productId) {
            mPresenter.requestAliPay(productId);
        }

        @Override
        public void addBookToShelf(int bookId) {
            AddBookModel model = new AddBookModel();
            model.setBookId(bookId);
            EventBus.getDefault().post(model);
        }

        @Override
        public void freeRead(int bookId, int chapterId) {
            //销毁readActivity
            if (IsRead == 0) {
                Log.d("jump_book", "jump");
                BookDetailResponse detail = new BookDetailResponse();
                Log.d("bookdetails", "bookId===" + bookId);
                detail.setBook_id(bookId);
                detail.setChapter_id(chapterId);
                ActivityUtil.toReadActivity(mContext, detail);
                finish();
            } else {
                Log.d("jump_book", "finish");
                finish();
            }


        }
    };

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
//        if (IsRead == 1 && bookId != null && !bookId.isEmpty()) {
//            BookDetailResponse bookDetailResponse = new BookDetailResponse();
//            bookDetailResponse.setBook_id(Integer.parseInt(bookId));
//            bookDetailResponse.setChapter_id(0);
//            ActivityUtil.toReadActivity(this, bookDetailResponse, true);
//            finish();
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    public void onError(Throwable t) {
        disMissProgress();
    }

    @Override
    public void setData(Base<Void> result) {
    }

    @Override
    public void showNetWorkProgress() {
        showDialog();
    }

    @Override
    public void disMissProgress() {
        dismissDialog();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public void setAliPayResult(PayResponse result) {
        if (result == null) {
            return;
        }
        final String orderInfo = result.getPay_str();
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(WebViewActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public void setWechatPayResult(PayResponse result) {
        //微信支付
        if (result == null) {
            return;
        }
        final String orderInfo = result.getPay_str();
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(WebViewActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    public void onEventMainThread(UpdateCommentEnum commentEnum) {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

}
