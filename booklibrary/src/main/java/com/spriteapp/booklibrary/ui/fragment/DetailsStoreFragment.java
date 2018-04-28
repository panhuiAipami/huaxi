package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.StoreDetailsAdapter;
import com.spriteapp.booklibrary.util.Constants;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.fragment.ChoiceFragment.ARG_COLUMN_COUNT;
import static com.spriteapp.booklibrary.util.ActivityUtil.FRAGMENT_SEX;

/**
 * Created by userfirst on 2018/4/17.
 */

public class DetailsStoreFragment extends BaseFragment {
    private int columnCount;
    private URecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayout null_layout;
    private RelativeLayout big_layout;
    private TextView miaoshu;
    private List<BookDetailResponse> list = new ArrayList<>();
    private StoreDetailsAdapter adapter;


    public static DetailsStoreFragment newInstance(int columnCount) {
        DetailsStoreFragment fragment = new DetailsStoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.choice_fragment_item_list;
    }

    @Override
    public void configViews() {
        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        if (columnCount == 1)
            getLike();
        else
            getHistory();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && columnCount == 0)
            getHistory();
    }

    @Override
    public void findViewId() {
        recyclerView = (URecyclerView) mParentView.findViewById(R.id.list);
        swipe_refresh = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipe_refresh);
        null_layout = (LinearLayout) mParentView.findViewById(R.id.null_layout);
        big_layout = (RelativeLayout) mParentView.findViewById(R.id.big_layout);
        big_layout.setBackgroundColor(getResources().getColor(R.color.app_background));
        big_layout.setPadding(-ScreenUtil.dpToPxInt(10), 0, 0, 0);
        miaoshu = (TextView) mParentView.findViewById(R.id.miaoshu);
        miaoshu.setText("竟然没有数据!");
        adapter = new StoreDetailsAdapter(getActivity(), list, 3, 2);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter);
        swipe_refresh.setEnabled(false);
        goneOrShow();
    }

    @Override
    protected void lazyLoad() {

    }

    private void goneOrShow() {
        if (list.size() == 0)
            null_layout.setVisibility(View.VISIBLE);
        else
            null_layout.setVisibility(View.GONE);

    }

    private void getLike() {//猜你喜欢
        if (!NetworkUtil.isAvailable(getActivity())) return;
        BookApi.getInstance()
                .service
                .book_searchrecommend(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookStoreResponse.getData() != null && bookStoreResponse.getData().size() != 0) {
                                    list.clear();
                                    list.addAll(bookStoreResponse.getData());
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

                    }
                });
    }

    private void getHistory() {//阅读记录
        if (!NetworkUtil.isAvailable(getActivity())) return;
        BookApi.getInstance()
                .service
                .user_readhistory(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookStoreResponse.getData() != null && bookStoreResponse.getData().size() != 0) {
                                    list.clear();
                                    list.addAll(bookStoreResponse.getData());
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

                    }
                });
    }

    public void setRecyclerViewMode(final int num) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 9);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return num;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.notifyDataSetChanged();
    }

}
