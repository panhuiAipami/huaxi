package com.spriteapp.booklibrary.ui.fragment;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.widget.ReaderWebView;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class DiscoverFragment extends BaseFragment {

    private ReaderWebView mWebView;

    @Override
    public int getLayoutResId() {
        return R.layout.book_readerfragment_discover;
    }

    @Override
    public void initData() {
        if (!AppUtil.isNetAvailable(getContext())) {
            return;
        }
        mWebView.loadPage(Constant.BOOK_WEEKLY_URL);
    }

    @Override
    public void configViews() {
    }

    @Override
    public void findViewId() {
        mWebView = (ReaderWebView) mParentView.findViewById(R.id.book_reader_discover_web_view);
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
