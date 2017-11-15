package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.callback.WebViewCallback;
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
    public static final String LOAD_URL_TAG = "LoadUrlTag";
    public static final String IS_H5_PAY_TAG = "isH5PayTag";
    private ReaderWebView mWebView;
    private String mUrl;
    private Context mContext;
    private WebViewPresenter mPresenter;
    private static final int SDK_PAY_FLAG = 1;
    private boolean isH5Pay;

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
        mWebView.loadPage(mUrl, mClient);
        WebViewUtil.getInstance().setWebViewCallback(mWebViewCallback);
    }

    private void getDataFromTopPage() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mUrl = intent.getStringExtra(LOAD_URL_TAG);
        isH5Pay = intent.getBooleanExtra(IS_H5_PAY_TAG, false);
    }

    @Override
    public void findViewId() {
        super.findViewId();
        mWebView = (ReaderWebView) findViewById(R.id.book_reader_web_view);
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

    };

    WebChromeClient mChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    };

    private WebViewCallback mWebViewCallback = new WebViewCallback() {
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
            BookDetailResponse detail = new BookDetailResponse();
            detail.setBook_id(bookId);
            detail.setChapter_id(chapterId);
            ActivityUtil.toReadActivity(mContext, detail);
        }
    };

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
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
