package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.model.ChoiceBean;
import com.spriteapp.booklibrary.ui.adapter.ChoiceAdapter;
import com.spriteapp.booklibrary.ui.presenter.ChoiceContentPresenter;
import com.spriteapp.booklibrary.ui.view.ChoiceView;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 精选
 */
public class ChoiceFragment extends BaseFragment implements ChoiceView, URecyclerView.LoadingListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private List<ChoiceBean> lists = new ArrayList<>();
    private SwipeRefreshLayout swipe_refresh;
    private ChoiceContentPresenter contentPresenter = new ChoiceContentPresenter(this);
    private ChoiceAdapter adapter;
    int page = 1;
    int lastPage = 1;
    private LinearLayout null_layout;
    private TextView miaoshu;

    public ChoiceFragment() {
    }

    public static ChoiceFragment newInstance(int columnCount) {
        ChoiceFragment fragment = new ChoiceFragment();
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
            int mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
    }

    @Override
    public void findViewId() {
        swipe_refresh = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipe_refresh);
        null_layout = (LinearLayout) mParentView.findViewById(R.id.null_layout);
        miaoshu = (TextView) mParentView.findViewById(R.id.miaoshu);
        miaoshu.setText("竟然没有加载出来");
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        URecyclerView recyclerView = (URecyclerView) mParentView.findViewById(R.id.list);
        recyclerView.setLoadingListener(this);
        adapter = new ChoiceAdapter(lists, getActivity());
        recyclerView.setAdapter(adapter);
        goneOrShow();


    }

    public void goneOrShow() {
        if (lists.size() == 0) {
            null_layout.setVisibility(View.VISIBLE);
        } else {
            null_layout.setVisibility(View.GONE);
        }
    }

    public void onRefreshData() {
        page = 1;
        lazyLoad();
    }


    @Override
    protected void lazyLoad() {
        lastPage = page;
        contentPresenter.requestGetData(page);
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void setData(Base<List<ChoiceBean>> result) {
        if (page == 1)
            lists.clear();

        if (result != null && result.getData() != null && result.getData().size() > 0) {
            page++;
            lists.addAll(result.getData());
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
        goneOrShow();
    }

    @Override
    public void showNetWorkProgress() {

    }

    @Override
    public void disMissProgress() {
        swipe_refresh.setRefreshing(false);

    }

    @Override
    public Context getMyContext() {
        return null;
    }

    @Override
    public void onLoadMore() {
        if (page > lastPage) {
            lazyLoad();
        }
    }
}
