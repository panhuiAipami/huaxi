package net.huaxi.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.activity.SearchActivity;
import net.huaxi.reader.common.URLConstants;

import butterknife.ButterKnife;
import butterknife.OnClick;
import net.huaxi.reader.R;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.view.WebView;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

//话题fragment
public class FmGambitPaihang extends BaseFragment {

    private PtrFrameLayout frameLayout;
    private WebView webView;
    private View rlNetError;
    private String url = URLConstants.H5PAGE_GAMBIT + CommonUtils.getPublicGetArgs();
    private boolean loadSuccess;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_gambit, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !loadSuccess) {
            webView.loadUrl(url);
            loadSuccess = true;
        }
    }

    private void initView(View view) {
        rlNetError = view.findViewById(R.id.city_item_neterror_layout);
        frameLayout = (PtrFrameLayout) view.findViewById(R.id.gambit_item_ptrFrameLayout);
        frameLayout.setLoadingMinTime(1000);
        frameLayout.setDurationToCloseHeader(100);
        frameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                webView.loadUrl(url);
            }
        });
        webView = (WebView) view.findViewById(R.id.gambit_item_webview);
        webView.addJavascriptInterface(new JavaScript(getActivity(), webView),
                JavaScript.NAME);
        webView.setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                LogUtils.debug("刷新结束");
                frameLayout.refreshComplete();
                rlNetError.setVisibility(View.GONE);
                loadSuccess = true;
            }

            @Override
            public void onError() {
                frameLayout.refreshComplete();
                rlNetError.setVisibility(View.VISIBLE);
                webView.loadUrl("javascript:document.body.innerHTML=\"\"");
                loadSuccess = false;
            }
        });


    }


    @OnClick({R.id.city_title_right_imageview, R.id.city_item_neterror_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_title_right_imageview:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
//                UMEventAnalyze.countEvent(getContext(),UMEventAnalyze.SEARCH_BOOK_CLASSFY_BUTTON);
                break;
            case R.id.city_item_neterror_layout:
                if (!NetUtils.checkNetworkUnobstructed()) {
                    ViewUtils.toastShort(getString(R.string.not_available_network));
                    return;
                }
                if (!frameLayout.isAutoRefresh()) {
                    frameLayout.autoRefresh();
                    rlNetError.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
