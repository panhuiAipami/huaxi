package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.RankAdapter;
import com.spriteapp.booklibrary.ui.presenter.RankContentPresenter;
import com.spriteapp.booklibrary.ui.view.RankView;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行的周/月/总
 */
public class RankListFragment extends BaseFragment implements RankView, SwipeRefreshLayout.OnRefreshListener {
    private RankContentPresenter presenter = new RankContentPresenter(this);
    private SwipeRefreshLayout swipe_refresh;
    private RankAdapter adapter;
    private List<BookDetailResponse> lists = new ArrayList<>();
    int type = 1;//1:热销榜 2:人气榜 3:评论榜 4: 更新榜 默认1
    int interval = 1;//1:周榜 2:月榜 3:总榜 默认1
    private int page = 1;

    public RankListFragment() {
    }

    public static RankListFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        RankListFragment fragment = new RankListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.choice_fragment_item_list;
    }

    @Override
    public void configViews() {
        swipe_refresh.setOnRefreshListener(this);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            type = bundle.getInt("type");
        }
        getData();
    }

    @Override
    public void findViewId() {
        swipe_refresh = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        RecyclerView recyclerView = (RecyclerView) mParentView.findViewById(R.id.list);
        adapter = new RankAdapter(lists, getActivity());
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onError(Throwable t) {
        if (swipe_refresh != null)
            swipe_refresh.setRefreshing(false);
    }

    @Override
    public void setData(Base<List<BookDetailResponse>> result) {
        if (swipe_refresh != null)
            swipe_refresh.setRefreshing(false);
        lists.clear();

        if (result != null && result.getData() != null && result.getData().size() > 0) {
            page++;
            lists.addAll(result.getData());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showNetWorkProgress() {

    }

    @Override
    public void disMissProgress() {

    }

    @Override
    public Context getMyContext() {
        return null;
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    /**
     * 热销  人气  评论 更新榜
     * @param interval
     */
    public void sortRefresh(int interval) {
        this.interval = interval;
        getData();
    }
    /**
     * 周  月  总
     * @param type
     */
    public void timeRefresh(int type) {
        this.type = type;
        getData();
    }

    public void getData() {
        if (presenter != null)
            presenter.requestGetData(type, interval);
    }
}
