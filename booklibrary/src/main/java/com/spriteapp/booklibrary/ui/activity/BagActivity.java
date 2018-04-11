package com.spriteapp.booklibrary.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.MyApprenticeBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.BagAdapter;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BagActivity extends TitleActivity implements SwipeRefreshLayout.OnRefreshListener, URecyclerView.LoadingListener {
    private URecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayout null_layout;
    private TextView miaoshu;
    private List<BookDetailResponse> list = new ArrayList<>();
    private BagAdapter adapter;
    private int page = 1;
    private int lastPage = 0;


    @Override
    public void initData() throws Exception {
        setTitle("书包");
        getData();
        goneOrShow();
    }


    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_bag, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        recyclerView = (URecyclerView) findViewById(R.id.recyclerView);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        null_layout = (LinearLayout) findViewById(R.id.null_layout);
        miaoshu = (TextView) findViewById(R.id.miaoshu);
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        swipe_refresh.setOnRefreshListener(this);
        recyclerView.setLoadingListener(this);
        miaoshu.setText("竟然没有加载出来");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new BagAdapter(this, list);
    }

    private void goneOrShow() {
        if (list.size() == 0) {
            null_layout.setVisibility(View.VISIBLE);
        } else {
            null_layout.setVisibility(View.GONE);
        }
    }

    private void getData() {
        if (!NetworkUtil.isAvailable(this)) return;
        BookApi.getInstance()
                .service
                .user_novelpackage(Constant.JSON_TYPE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> detailsResponse) {
                        if (detailsResponse != null) {
                            int resultCode = detailsResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (detailsResponse.getData() != null && detailsResponse.getData().size() != 0) {
                                    if (page == 1) list.clear();
                                    page++;
                                    list.addAll(detailsResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                        swipe_refresh.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        Log.d("bag_refresh", "开始刷新");
        page = 1;
        lastPage = 1;
        getData();
    }

    @Override
    public void onLoadMore() {
        if (page > lastPage) {
            lastPage = page;
            getData();
        }
    }
}
