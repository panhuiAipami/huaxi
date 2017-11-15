package com.spriteapp.booklibrary.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.widget.ReaderWebView;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class BookstoreFragment extends BaseFragment {

    private static final String TAG = "BookstoreFragment";
    private ReaderWebView mWebView;
    private SwipeRefreshLayout mSwipeLayout;

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
        mWebView.loadPage(Constant.BOOK_STORE_URL);
    }

    private void setAttrsForSwipeLayout() {
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(Constant.BOOK_STORE_URL);
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
