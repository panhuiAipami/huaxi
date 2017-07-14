package net.huaxi.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.view.AllDisplayGridView;
import net.huaxi.reader.activity.ClassifyActivity;
import net.huaxi.reader.activity.SearchActivity;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.huaxi.reader.R;

import net.huaxi.reader.adapter.AdapterMainCatalog;
import net.huaxi.reader.bean.CatalogBean;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.https.GetRequest;


/**
 * function:    分类页面
 * author:      ryantao
 * create:      16/7/18
 * modtime:     16/7/18
 */
public class FmCategory extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    ImageView backBtn;
    ImageView otherBtn;
    TextView title, firstHeaderTitle, lastHeaderTitle;

//    RecyclerView mRecyclerView;
//
//    RecyclerView mRecyclerViewWomen;

    AllDisplayGridView mRecyclerView;

    AllDisplayGridView mRecyclerViewWomen;

//    AdapterCatalog mAdapterCatalog;
//
//    AdapterCatalog mAdapterCatalogWomen;

    AdapterMainCatalog mAdapterCatalog;
    AdapterMainCatalog mAdapterCatalogWomen;

    SwipeRefreshLayout refreshLayout;

    List<CatalogBean> mCatalogBeanListFirst;

    List<CatalogBean> mCatalogBeanListLast;

    LinearLayout errorLayout;

    ScrollView mScrollView;

    private boolean isLoadSuccess = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_category, container, false);
        init(view);
//        loadData();
        onRefresh();
        return view;
    }


    @Override
    public void onResume() {
        UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.CLASSIFY_PAGE);
        super.onResume();
    }

    /**
     * 初始化控件
     */
    private void init(View view) {
        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);
        backBtn = (ImageView) view.findViewById(R.id.toolbar_layout_back);
        backBtn.setVisibility(View.GONE);
        otherBtn = (ImageView) view.findViewById(R.id.toolbar_layout_other);
        otherBtn.setVisibility(View.VISIBLE);
        otherBtn.setImageResource(R.mipmap.city_search_press);
        otherBtn.setBackgroundResource(R.drawable.selector_common_red_background);
        title = (TextView) view.findViewById(R.id.toolbar_layout_title);
        title.setText(CommonApp.getInstance().getString(R.string.book_catalog));
        lastHeaderTitle = (TextView) view.findViewById(R.id.catalog_header_last).findViewById(R.id.catalog_header_title);
        lastHeaderTitle.setVisibility(View.GONE);
        firstHeaderTitle = (TextView) view.findViewById(R.id.catalog_header_first).findViewById(R.id.catalog_header_title);
        firstHeaderTitle.setVisibility(View.GONE);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.catalog_item_ptrFrameLayout);
        refreshLayout.setColorSchemeResources(R.color.c01_themes_color);
        errorLayout = (LinearLayout) view.findViewById(R.id.city_item_neterror_layout);
        errorLayout.setOnClickListener(this);
        otherBtn.setOnClickListener(this);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.catalog_expand_recycler);
//        mRecyclerViewWomen = (RecyclerView) view.findViewById(R.id.catalog_expand_recycler_women);
        mRecyclerView = (AllDisplayGridView) view.findViewById(R.id.catalog_expand_recycler_first);
        mRecyclerViewWomen = (AllDisplayGridView) view.findViewById(R.id.catalog_expand_recycler_last);
//        mRecyclerView.setLayoutManager(new ScrollGridLayoutManager(getActivity(), 2));
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(getContext().getResources().getDimensionPixelSize(R.dimen
// .category_space)));
//        mRecyclerViewWomen.setLayoutManager(new ScrollGridLayoutManager(getActivity(), 2));
//        mRecyclerViewWomen.addItemDecoration(new SpacesItemDecoration(getContext().getResources().getDimensionPixelSize(R.dimen
// .category_space)));
        mCatalogBeanListFirst = new ArrayList<CatalogBean>();
        mCatalogBeanListLast = new ArrayList<CatalogBean>();
//        mAdapterCatalog = new AdapterCatalog(getContext(), mCatalogBeanList);
//        mAdapterCatalogWomen = new AdapterCatalog(getContext(), mCatalogBeanListLast);
        mAdapterCatalog = new AdapterMainCatalog(getContext(), mCatalogBeanListFirst);
        mAdapterCatalogWomen = new AdapterMainCatalog(getContext(), mCatalogBeanListLast);
        mRecyclerView.setAdapter(mAdapterCatalog);
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CatalogBean bean = (CatalogBean) parent.getItemAtPosition(position);
                String type = "";
                if (bean != null) {
                    Intent it = new Intent(getActivity(), ClassifyActivity.class);
                    it.putExtra("obj", bean);
                    getActivity().startActivity(it);
                    type = bean.getName();
                }

                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.CLASSIFY_PARENT_BOY, type);

            }
        });
        mRecyclerViewWomen.setAdapter(mAdapterCatalogWomen);
        mRecyclerViewWomen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CatalogBean bean = (CatalogBean) parent.getItemAtPosition(position);
                String type = "";
                if (bean != null) {
                    Intent it = new Intent(getActivity(), ClassifyActivity.class);
                    it.putExtra("obj", bean);
                    getActivity().startActivity(it);
                    type = bean.getName();
                }
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.CLASSIFY_PARENT_GIRL, type);
            }
        });
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        loadData();
    }


    /**
     * 加载数据
     */
    private void loadData() {
        String url = URLConstants.NATIVE_CATALOG_URL + CommonUtils.getPublicGetArgs();
        GetRequest _request = new GetRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug(response.toString());
                int errorId = ResponseHelper.getErrorId(response);
                if (errorId == XSErrorEnum.SUCCESS.getCode()) {
                    final JSONObject vData = ResponseHelper.getVdata(response);
                    parseData(vData);
                }
                showList();
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
                showError(true);
            }
        }, "2.0");
        RequestQueueManager.addRequest(_request);
    }

    private void parseData(JSONObject vData) {
        try {
            System.err.println("isMainThread3 = " + (Thread.currentThread() == Looper.getMainLooper().getThread()));
            if (vData != null) {

                JSONObject listData = vData.getJSONObject("list");
                Type type = new TypeToken<Map<String, Map<String, CatalogBean>>>() {
                }.getType();
                Map<String, Map<String, CatalogBean>> allMap = new Gson().fromJson(listData.toString(), type);
                int count = 1;
                if (allMap != null && allMap.size() > 0) {
                    for (String key : allMap.keySet()) {
                        if (StringUtils.isNotEmpty(key) && allMap.get(key) != null) {
                            String title = "man".equals(key) ? CommonApp.getInstance().getString(R.string.boy) : CommonApp.getInstance()
                                    .getString(R.string.girl);
                            if (count == 1) {
                                count++;
                                firstHeaderTitle.setText(title);
                                List<CatalogBean> _beanList = new ArrayList<CatalogBean>();
                                mCatalogBeanListFirst.clear();
                                Map<String, CatalogBean> _beanMap = allMap.get(key);
                                if (_beanMap != null && _beanMap.size() > 0) {
                                    for (String key2 : _beanMap.keySet()) {
                                        CatalogBean _bean = _beanMap.get(key2);
                                        _bean.setId(key2);
                                        _beanList.add(_bean);
                                    }
                                    mCatalogBeanListFirst.addAll(_beanList);
                                }

                            } else {
                                lastHeaderTitle.setText(title);
                                mCatalogBeanListLast.clear();
                                List<CatalogBean> _beanList2 = new ArrayList<CatalogBean>();
                                Map<String, CatalogBean> _beanMap2 = allMap.get(key);
                                if (_beanMap2 != null && _beanMap2.size() > 0) {
                                    for (String key3 : _beanMap2.keySet()) {
                                        CatalogBean _bean = _beanMap2.get(key3);
                                        _bean.setId(key3);
                                        _beanList2.add(_bean);
                                    }
                                    mCatalogBeanListLast.addAll(_beanList2);
                                }
                            }
                        }
                    }
                }

            }
        } catch (JSONException e) {
            ReportUtils.reportError(e);
        }
    }

    private void showList() {
        boolean b1 = mCatalogBeanListFirst != null && mCatalogBeanListFirst.size() > 0;
        boolean b2 = mCatalogBeanListLast != null && mCatalogBeanListLast.size() > 0;
        showFirstPart(b1);
        showLastPart(b2);
        showError(!b1 && !b2);
    }

    private void showFirstPart(boolean visible) {
        if (visible) {
            mAdapterCatalog.notifyDataSetChanged();
            firstHeaderTitle.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            firstHeaderTitle.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void showLastPart(boolean visible) {
        if (visible) {
            mAdapterCatalogWomen.notifyDataSetChanged();
            lastHeaderTitle.setVisibility(View.VISIBLE);
            mRecyclerViewWomen.setVisibility(View.VISIBLE);
        } else {
            lastHeaderTitle.setVisibility(View.GONE);
            mRecyclerViewWomen.setVisibility(View.GONE);
        }
    }

    private void showError(boolean visible) {
        if (visible) {
            errorLayout.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoadSuccess) {
            loadData();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_layout_other:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.CLASSIFY_PARENT_SEARCH);
                break;

            case R.id.city_item_neterror_layout:
                loadData();
                break;

            default:
                break;
        }
    }
}
