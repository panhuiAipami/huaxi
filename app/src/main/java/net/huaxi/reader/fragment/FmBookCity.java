package net.huaxi.reader.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.SearchActivity;
import net.huaxi.reader.adapter.AdapterBookCityWebView;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.ViewPager;
import net.huaxi.reader.view.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

//书城的fragment
public class FmBookCity extends BaseFragment {
    List<String> titles = new ArrayList<String>();//几个网页的title
    List<String> urls = new ArrayList<String>();//几个网页的url
    @BindView(R.id.fc_store_error)
    LinearLayout fc_store_error;
    private ViewPager vpCity;
    private AdapterBookCityWebView adapterWebView;
    private ImageView ivSearch;
    private SmartTabLayout smartTabLayout;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_city, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("FmBookCity.onResume");
        UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.BOOKINFO_SHARE);
    }


    private void initView(View view) {
        if(NetUtils.checkNet() == NetType.TYPE_NONE){
            //当前无网络
            smartTabLayout.setVisibility(View.GONE);
            vpCity.setVisibility(View.GONE);
            fc_store_error.setVisibility(View.VISIBLE);
        }else {
            smartTabLayout.setVisibility(View.VISIBLE);
            vpCity.setVisibility(View.VISIBLE);
            fc_store_error.setVisibility(View.GONE);
        }
        vpCity = (ViewPager) view.findViewById(R.id.city_viewpager);
        vpCity.setOffscreenPageLimit(4);
        ivSearch = (ImageView) view.findViewById(R.id.city_title_right_imageview);
        smartTabLayout = (SmartTabLayout) view.findViewById(R.id.city_tab_layout);

    }

    private void initData() {
        titles.add("精选");
        titles.add("优惠");
        titles.add("新书");
        titles.add("会员");
        titles.add("排行");
        urls.add(URLConstants.H5PAGE_SELECTION + CommonUtils.getPublicGetArgs());
        urls.add(URLConstants.H5PAGE_FREE + CommonUtils.getPublicGetArgs());
        urls.add(URLConstants.H5PAGE_NEW_BOOK + CommonUtils.getPublicGetArgs());
        urls.add(URLConstants.H5PAGE_MEMBER + CommonUtils.getPublicGetArgs());
        urls.add(URLConstants.H5PAGE_RANKING + CommonUtils.getPublicGetArgs());

        //这几步可以迟一点加载
        initWebViewCache();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadweb(false);
        }
    }

    private void initWebViewCache() {
        Map<Integer, CityPageHolder> WebViewCache = new HashMap<Integer, CityPageHolder>();
        for (int i = 0; i < titles.size(); i++) {
            CityPageHolder holder = initCityHolder(i);
            WebViewCache.put(i, holder);
        }
        adapterWebView = new AdapterBookCityWebView(getActivity(), WebViewCache);
        vpCity.setAdapter(adapterWebView);
        smartTabLayout.setViewPager(vpCity);
    }

    private void initEvent() {
        vpCity.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadweb(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                UMEventAnalyze.countEvent(getContext(), UMEventAnalyze.BOOKCITY_SEACHER);
            }
        });
    }

    /**
     * 加载网页数据的方法
     *
     * @param needLoad 是否需要强制加载
     */
    private void loadweb(boolean needLoad) {
        if (adapterWebView == null || vpCity == null || adapterWebView.getWebViewCache() == null) {
            return;
        }
        CityPageHolder holder = adapterWebView.getWebViewCache().get(vpCity.getCurrentItem());
        if (needLoad || !holder.isHasLoad()) {
            LogUtils.debug("loadurl==" + holder.getUrl());
            holder.getWebView().loadUrl(holder.getUrl());
        }
    }

    @SuppressLint("AddJavascriptInterface")
    private CityPageHolder initCityHolder(int i) {
        final CityPageHolder holder = new CityPageHolder();
        holder.setPageTitle(titles.get(i));
        holder.setUrl(urls.get(i));
        LogUtils.debug(urls.get(i));
        holder.setItemView(LayoutInflater.from(getContext()).inflate(R.layout.page_item_city, null));
        holder.setRlNetError(holder.getItemView().findViewById(R.id
                .city_item_neterror_layout));
        final SwipeRefreshLayout frame = (SwipeRefreshLayout) holder.getItemView().findViewById(R.id
                .city_item_ptrFrameLayout);
        frame.setColorSchemeResources(R.color.c01_themes_color);

        frame.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                holder.getWebView().loadUrl(holder.getUrl());
            }
        });
        holder.setLayout(frame);
        vpCity.setChildLayout(frame);
        LogUtils.debug("关闭硬件加速" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            frame.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        WebView webView = (WebView) holder.getItemView().findViewById(R.id.city_item_webview);
        webSettings(webView);
        holder.setWebView(webView);


        vpCity.setWebView(holder.getWebView());
        holder.getWebView().setLoadListener(new WebView.WebViewLoadingListener() {
            @Override
            public void onFinished(android.webkit.WebView view, String url) {
                LogUtils.debug("刷新结束");
                holder.getLayout().setRefreshing(false);
                holder.setHasLoad(true);
            }

            @Override
            public void onError() {
                holder.getLayout().setRefreshing(false);
                holder.setHasLoad(false);
                holder.getRlNetError().setVisibility(View.VISIBLE);
                holder.getWebView().loadUrl("javascript:document.body.innerHTML=\"\"");
            }
        });

        holder.getRlNetError().setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetUtils.checkNetworkUnobstructed()) {
                            ViewUtils.toastShort(getString(R.string.not_available_network));
                            return;
                        }
                        if (!holder.getLayout().isRefreshing()) {
                            holder.getLayout().post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.getLayout().setRefreshing(true);
                                }
                            });
                            holder.getRlNetError().setVisibility(View.GONE);
                        }
                    }
                });
        return holder;
    }

    /**
     * 设置缓存
     *
     * @param webView
     */
    private void webSettings(WebView webView) {
        webView.addJavascriptInterface(new JavaScript(getActivity(), webView), JavaScript.NAME);
    }

    public class CityPageHolder {
        private View itemView;
        private SwipeRefreshLayout layout;
        private WebView webView;
        private String url;
        private boolean hasLoad;
        private View rlNetError;
        private String pageTitle;

        public String getPageTitle() {
            return pageTitle;
        }

        public void setPageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
        }

        public View getRlNetError() {
            return rlNetError;
        }

        public void setRlNetError(View rlNetError) {
            this.rlNetError = rlNetError;
        }

        public boolean isHasLoad() {
            return hasLoad;
        }

        public void setHasLoad(boolean hasLoad) {
            this.hasLoad = hasLoad;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public SwipeRefreshLayout getLayout() {
            return layout;
        }

        public void setLayout(SwipeRefreshLayout layout) {
            this.layout = layout;
        }

        public WebView getWebView() {
            return webView;
        }

        public void setWebView(WebView webView) {
            this.webView = webView;
        }

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }
    }

}
