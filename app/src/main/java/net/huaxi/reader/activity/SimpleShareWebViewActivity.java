package net.huaxi.reader.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.dialog.BookContentShareDialog;
import net.huaxi.reader.util.LoginHelper;
import net.huaxi.reader.view.WebView;

import net.huaxi.reader.R;

public class SimpleShareWebViewActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvWebViewTitle;
    private ImageView ivShare, ivBack;
    private SwipeRefreshLayout freshlayout;
    private WebView webView;
    private View networkErrorLayout;
    private RelativeLayout rlShare;

    private String title, weburl, imgurl, desc;
    private PopupWindow sharePopupWindow;
    private ImageView ivShareWeibo, ivShareWeixin, ivShareQQ, ivSharePengyouquan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_share_web_view);
        initView();
        initData();
        initEvent();

    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        rlShare.setOnClickListener(this);
    }

    private void initView() {
        tvWebViewTitle = (TextView) findViewById(R.id.simple_webview_share_title_textview);
        ivBack = (ImageView) findViewById(R.id.simple_webview_share_back_imageview);
        ivShare = (ImageView) findViewById(R.id.simple_webview_share_imageview);
        rlShare = (RelativeLayout) findViewById(R.id.simple_webview_share_imageview_layout);
        freshlayout = (SwipeRefreshLayout) findViewById(R.id.simple_webview_share_ptrframeLayout);
        freshlayout.setColorSchemeResources(R.color.c01_themes_color);
        webView = (WebView) findViewById(R.id.simple_webview_share_webview);
        networkErrorLayout = findViewById(R.id.simple_webview_share_neterror_layout);
        freshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(weburl);
            }
        });
        webView.setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                networkErrorLayout.setVisibility(View.GONE);
                freshlayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                freshlayout.setRefreshing(false);
                networkErrorLayout.setVisibility(View.VISIBLE);
                webView.loadUrl("javascript:document.body.innerHTML=\"\"");
            }
        });
        webView.addJavascriptInterface(new JavaScript(SimpleShareWebViewActivity.this, webView), JavaScript.NAME);
        networkErrorLayout.setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtils.checkNetworkUnobstructed()) {
                    ViewUtils.toastShort(getString(R.string.not_available_network));
                    return;
                }
                if (!freshlayout.isRefreshing()) {
                    freshlayout.post(new Runnable() {
                        @Override
                        public void run() {
                            freshlayout.setRefreshing(true);
                        }
                    });
                    networkErrorLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData() {
        title = getIntent().getStringExtra("title");
        weburl = getIntent().getStringExtra("weburl");
        imgurl = getIntent().getStringExtra("imgurl");
        desc = getIntent().getStringExtra("desc");
        if (StringUtils.isNotBlank(title)) {
            tvWebViewTitle.setText(title);
        }
        webView.loadUrl(weburl);
    }

    @Override
    public void onClick(View v) {
        if (ivShare.getId() == v.getId() || rlShare.getId() == v.getId()) {
//            if (sharePopupWindow == null) {
//                sharePopupWindow = initShareView();
//            }
//            sharePopupWindow.showAsDropDown(ivShare, 0, Utils.dip2px(this, 20));
            ShareBean shareBean = new ShareBean();
            shareBean.setShareUrl(weburl);
            shareBean.setImgUrl(imgurl);
            shareBean.setTitle(title);
            shareBean.setDesc(desc);
            new BookContentShareDialog(SimpleShareWebViewActivity.this, shareBean).show();
        } else if (ivBack.getId() == v.getId()) {
            finish();
        } else if (ivShareWeixin.getId() == v.getId()) {
            getLoginHelper().shareWxWebPage(weburl, true, imgurl,
                    title, desc);
            if (sharePopupWindow != null) {
                sharePopupWindow.dismiss();
            }
        } else if (ivSharePengyouquan.getId() == v.getId()) {
            getLoginHelper().shareWxWebPage(weburl, false, imgurl,
                    title, desc);
            if (sharePopupWindow != null) {
                sharePopupWindow.dismiss();
            }
        } else if (ivShareQQ.getId() == v.getId()) {
            getLoginHelper().shareToQzone(weburl, imgurl, title, desc);
            if (sharePopupWindow != null) {
                sharePopupWindow.dismiss();
            }
        } else if (ivShareWeibo.getId() == v.getId()) {
            getLoginHelper().shareWb(weburl, imgurl, title, desc);
            if (sharePopupWindow != null) {
                sharePopupWindow.dismiss();
            }
        }
    }

    private PopupWindow initShareView() {
        View view = LayoutInflater.from(SimpleShareWebViewActivity.this).inflate(R.layout
                .dialog_detail_share, null);
        ivSharePengyouquan = (ImageView) view.findViewById(R.id.dialog_share_penyouquan);
        ivShareQQ = (ImageView) view.findViewById(R.id.dialog_share_qqkongjian);
        ivShareWeibo = (ImageView) view.findViewById(R.id.dialog_share_weibo);
        ivShareWeixin = (ImageView) view.findViewById(R.id.dialog_share_weixin);
        ivShareWeixin.setOnClickListener(this);
        ivShareQQ.setOnClickListener(this);
        ivShareWeibo.setOnClickListener(this);
        ivSharePengyouquan.setOnClickListener(this);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.ShareDialogTheme);
        return popupWindow;
    }


    LoginHelper loginHelper;

    private LoginHelper getLoginHelper() {
        if (loginHelper == null) {
            loginHelper = new LoginHelper(this);
            AppContext.setLoginHelper(loginHelper);
        }
        return loginHelper;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.loginHelper != null) {
            this.loginHelper.ActivityResult(requestCode, resultCode, data);
        }
    }


}
