package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.NewBookStoreResponse;
import com.spriteapp.booklibrary.ui.adapter.NewBookStoreAdapter;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;

/**
 * Created by Administrator on 2018/1/26.
 */

public class NewNativeBookStoreFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private View mView;
    private SwipeRefreshLayout mRefresh;
    private URecyclerView mRecyclerView;
    private List<NewBookStoreResponse> mBookStoreResponseList = new ArrayList<>();
    private NewBookStoreAdapter mAdapter;
    private LinearLayoutManager manager;

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
        initList();
        getHttp();

    }

    public void initList() {
        manager = new LinearLayoutManager(getActivity());
        mAdapter = new NewBookStoreAdapter(getActivity(), mBookStoreResponseList, SharedPreferencesUtil.getInstance().getInt(SEX));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(manager);
        mRefresh.setColorSchemeResources(R.color.square_comment_selector);
        mRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void lazyLoad() {

    }

    //得到书城列表
    public void getHttp() {
        Log.d("getHttp", "执行获取书城数据");
        BookApi.getInstance()
                .service
                .book_store(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewBookStoreResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull NewBookStoreResponse bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                mBookStoreResponseList.clear();
                                mBookStoreResponseList.add(bookStoreResponse);
                                mAdapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {
        if (!NetworkUtil.isAvailable(getActivity()))
            mRefresh.setRefreshing(false);
        getHttp();
    }

    public void reLoadH5() {
        onRefresh();
    }
}
