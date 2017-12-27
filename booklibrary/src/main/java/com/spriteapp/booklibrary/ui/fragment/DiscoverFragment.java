package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;

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
import static com.spriteapp.booklibrary.ui.fragment.HomePageFragment.syTitles;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class DiscoverFragment extends BaseFragment {

    private ReaderWebView mWebView;
    private int type;
    private List<StoreBean> storeBeen = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.book_readerfragment_discover;
    }

    @Override
    public void initData() {
        if (!AppUtil.isNetAvailable(getContext())) {
            return;
        }
        Bundle bundle = getArguments();
        type = bundle.getInt(FRAGMENTTYPE, 0);
        try {
            CateBean.Top_menu top_menu = FileHelper.readObjectFromJsonFile(getActivity(), Constant.PathTitle, CateBean.Top_menu.class);
            if (top_menu != null && top_menu.getChosen() != null && top_menu.getChosen().size() != 0) {
                storeBeen.addAll(top_menu.getChosen());
            } else {
                if (syTitles != null && syTitles.size() != 0) {
                    storeBeen.addAll(syTitles);
                }
            }
            if (storeBeen.size() == 0) {
                storeBeen.add(new StoreBean(Constant.BOOK_WEEKLY_URL, "精选"));
                storeBeen.add(new StoreBean(Constant.BOOK_WEEKLY_URL, "排行"));
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
