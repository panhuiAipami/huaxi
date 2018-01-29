package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/26.
 */

public class NewNativeBookStoreFragment extends BaseFragment {
    private View mView;
    private SwipeRefreshLayout mRefresh;
    private URecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.book_store_layout, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.book_store_layout;
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        mRefresh = (SwipeRefreshLayout) mView.findViewById(R.id.refresh);
        mRecyclerView = (URecyclerView) mView.findViewById(R.id.recyclerView);
    }

    @Override
    protected void lazyLoad() {

    }

    //得到书城列表
    public void getHttp() {
        BookApi.getInstance()
                .service
                .book_store(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<BookStoreResponse>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<BookStoreResponse> squareBeanBase) {
                        if (squareBeanBase != null) {
                            int resultCode = squareBeanBase.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mRefresh.setRefreshing(false);
                    }
                });
    }
}
