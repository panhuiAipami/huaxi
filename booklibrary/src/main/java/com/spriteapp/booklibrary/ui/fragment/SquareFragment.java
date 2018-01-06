package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.widget.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2018/1/5.
 */

public class SquareFragment extends BaseFragment {
    private View mView;
    private XRecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.book_reader_fragment_native_book_store, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_fragment_native_book_store;
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {

    }

    @Override
    protected void lazyLoad() {

    }
}
