package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.model.CateBean;
import com.spriteapp.booklibrary.model.StoreBean;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.widget.ReaderWebView;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.ui.fragment.HomePageFragment.FRAGMENTTYPE;
import static com.spriteapp.booklibrary.ui.fragment.HomePageFragment.shuTitles;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class BookstoreFragment extends BaseFragment {

    private static final String TAG = "BookstoreFragment";
    private ReaderWebView mWebView;
    private SwipeRefreshLayout mSwipeLayout;
    private int type;
    private List<StoreBean> storeBeen = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_fragment_bookstore;
    }

    @Override
    public void initData() {
        mWebView.setWebChromeClient(mChromeClient);
        setAttrsForSwipeLayout();
        if (!AppUtil.isNetAvailable(getContext())) {
            return;
        }
        Bundle bundle = getArguments();
        type = bundle.getInt(FRAGMENTTYPE, 0);
        try {
            CateBean.Top_menu top_menu = FileHelper.readObjectFromJsonFile(getActivity(), Constant.PathTitle, CateBean.Top_menu.class);
            if (top_menu != null && top_menu.getStore() != null && top_menu.getStore().size() != 0) {
                storeBeen.addAll(top_menu.getStore());
            } else {
                if (shuTitles != null && shuTitles.size() != 0) {
                    storeBeen.addAll(shuTitles);
                }
            }
            if (storeBeen.size() == 0) {
                storeBeen.add(new StoreBean(Constant.BOOK_STORE_URL, "书城"));
                storeBeen.add(new StoreBean(Constant.BOOK_STORE_URL, "类别"));
            }
        } catch (Exception e) {
            e.toString();
        }
        if (type == 1) {
            if (storeBeen.size() >= 1)
                mWebView.loadPage(storeBeen.get(0).getUrl());
        } else {
            if (storeBeen.size() >= 2)
                mWebView.loadPage(storeBeen.get(1).getUrl());
        }
//        mWebView.loadPage(Constant.BOOK_STORE_URL);
    }

    public void reLoadH5() {
        if (mWebView != null) {
            if (type == 1) {
                if (storeBeen.size() >= 1)
                    mWebView.loadPage(storeBeen.get(0).getUrl());
            } else {
                if (storeBeen.size() >= 2)
                    mWebView.loadPage(storeBeen.get(1).getUrl());
            }

        }

//            mWebView.reload();
    }

    private void setAttrsForSwipeLayout() {
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (type == 1) {
                    mWebView.loadUrl(storeBeen.get(0).getUrl());
                } else {
                    mWebView.loadUrl(storeBeen.get(1).getUrl());
                }

            }
        });
    }

    WebChromeClient mChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);
                }
            }
        }
    };

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        mWebView = (ReaderWebView) mParentView.findViewById(R.id.book_reader_book_store_web_view);
        mSwipeLayout = (SwipeRefreshLayout) mParentView.findViewById(R.id.book_reader_swipe_layout);
    }

    @Override
    protected void lazyLoad() {
        if (mHasLoadedOnce || !isPrepared) {
            return;
        }
        mHasLoadedOnce = true;
        initData();
    }
}
