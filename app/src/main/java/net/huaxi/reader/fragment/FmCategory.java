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
//    @BindView(R.id.store_webview)
//    WebView mStoreWebview;
//    @BindView(R.id.store_refresh)
//    SwipeRefreshLayout mStoreRefresh;
//    @BindView(R.id.category_error)
//    LinearLayout mNeterror;

    private LinearLayout fm_store1_error;
    private int mSexClassify;
    //错误布局
    private LinearLayout fg_net_error;
    private WebView mStoreWebview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_store1, container, false);
        fg_net_error= (LinearLayout) view.findViewById(R.id.fg_net_error);
        mStoreWebview= (WebView) view.findViewById(R.id.fg_cate_webview);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fm_store1,null);
        initView();
        initData();
        ButterKnife.bind(this, view);
        //监听事件
//        mStoreRefresh.setOnRefreshListener(this);
        //刷新动画的颜色
//        mStoreRefresh.setColorSchemeResources(R.color.c01_themes_color);
    }

    private void initData() {
        //再添加一个判断网络状态，如果没网则WebView不加载数据
        if(NetUtils.checkNet() != NetType.TYPE_NONE){
            mStoreWebview.loadUrl(URLConstants.H5PAGE_SELECTION);
        }
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
//        fg_net_error= (LinearLayout) view.findViewById(R.id.fg_net_error);
//        mStoreWebview= (WebView) view.findViewById(R.id.store1_webview);
//        mNeterror= (LinearLayout) view.findViewById(R.id.category_error);
        if(NetUtils.checkNet() == NetType.TYPE_NONE){
            //当前无网络
            mStoreWebview.setVisibility(View.GONE);
            fg_net_error.setVisibility(View.VISIBLE);
        }else {
            mStoreWebview.setVisibility(View.VISIBLE);
            fg_net_error.setVisibility(View.GONE);
        }
        fg_net_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetUtils.checkNet() == NetType.TYPE_NONE){

                    mStoreWebview.setVisibility(View.GONE);
                    fg_net_error.setVisibility(View.VISIBLE);
                }else {
                    mStoreWebview.setVisibility(View.VISIBLE);
                    fg_net_error.setVisibility(View.GONE);
//                    initData();
//                    mStoreWebview.loadUrl(URLConstants.H5PAGE_WEEKLY);
                    //调用刷新的方法
                    onRefresh();
                }
            }
        });
//        mStoreRefresh.setOnRefreshListener(this);
//        mStoreRefresh.setColorSchemeResources(R.color.c01_themes_color);
        webSettings(mStoreWebview);
        mStoreWebview.setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                LogUtils.debug("刷新结束");
//                mStoreRefresh.setRefreshing(false);
                fg_net_error.setVisibility(View.GONE);
                mStoreWebview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
//                mStoreRefresh.setRefreshing(false);
                fg_net_error.setVisibility(View.VISIBLE);
                mStoreWebview.setVisibility(View.GONE);
//                mStoreWebview.loadUrl("javascript:document.body.innerHTML=\"\"");
            }
        });
    }
}
