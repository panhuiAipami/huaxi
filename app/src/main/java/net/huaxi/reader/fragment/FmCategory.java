package net.huaxi.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.SearchActivity;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * function:    书城页面
 * author:      ryantao
 * create:      16/7/18
 * modtime:     16/7/18
 */

/**
 * @Description: [新版书城的fragment]
 * @Author: [Saud]
 * @CreateDate: [16/8/2 13:52]
 * @UpDate: [16/8/2 13:52]
 * @Version: [v1.0]
 */
public class FmCategory extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.store_webview)
    WebView mStoreWebview;
    @BindView(R.id.store_refresh)
    SwipeRefreshLayout mStoreRefresh;
    @BindView(R.id.store_neterror)
    LinearLayout mNeterror;

    private LinearLayout fm_store1_error;
    private int mSexClassify;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fm_store1, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=getActivity().getLayoutInflater().inflate(R.layout.fm_store,null);
        ButterKnife.bind(this, view);
        //监听事件
//        mStoreRefresh.setOnRefreshListener(this);
        //刷新动画的颜色
        mStoreRefresh.setColorSchemeResources(R.color.c01_themes_color);
    }

    private void initData() {
        mStoreWebview.loadUrl(URLConstants.H5PAGE_SELECTION);
        mSexClassify = SharePrefHelper.getSexClassify();
        if (mSexClassify == 1) {//女
            UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.BOOKCITY_GIRL);
        } else if (mSexClassify == 2) {//男
            UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.BOOKCITY_BOY);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&(mSexClassify!= SharePrefHelper.getSexClassify())) {
            initData();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @OnClick(R.id.store_search)
    public void onClick() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
        UMEventAnalyze.countEvent(getContext(), UMEventAnalyze.BOOKCITY_SEACHER);
    }

    /**
     * 设置缓存
     *
     * @param webView
     */
    private void webSettings(WebView webView) {
        webView.addJavascriptInterface(new JavaScript(getActivity(), webView), JavaScript.NAME);

    }

    private void initView() {
        fm_store1_error= (LinearLayout) view.findViewById(R.id.fm_store1_error);
        if(NetUtils.checkNet() == NetType.TYPE_NONE){
            //当前无网络
            mStoreWebview.setVisibility(View.GONE);
            fm_store1_error.setVisibility(View.VISIBLE);
        }else {
            mStoreWebview.setVisibility(View.VISIBLE);
            fm_store1_error.setVisibility(View.GONE);
        }
        mStoreRefresh.setOnRefreshListener(this);
        mStoreRefresh.setColorSchemeResources(R.color.c01_themes_color);
        webSettings(mStoreWebview);
        mStoreWebview.setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                LogUtils.debug("刷新结束");
                mStoreRefresh.setRefreshing(false);
                mNeterror.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                mStoreRefresh.setRefreshing(false);
                mNeterror.setVisibility(View.VISIBLE);
                mStoreWebview.loadUrl("javascript:document.body.innerHTML=\"\"");
            }
        });
    }
}
