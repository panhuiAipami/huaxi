package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.ui.adapter.SquareAdapter;
import com.spriteapp.booklibrary.widget.xrecyclerview.XRecyclerView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/5.
 */

public class SquareFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private View mView;
    private XRecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayoutManager linearLayoutManager;
    private SquareAdapter adapter;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.new_square_layout, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.new_square_layout;
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        recyclerView = (XRecyclerView) mView.findViewById(R.id.square_recycler_view);
        swipe_refresh = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener(this);
        setHttp();
    }

    @Override
    protected void lazyLoad() {

    }

    public void setHttp() {//加载数据
        BookApi.getInstance()
                .service
                .square_index(page + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<SquareBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<SquareBean> squareBeanBase) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {//刷新
        page = 1;
        setHttp();
    }
}
