package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.RankAdapter;
import com.spriteapp.booklibrary.ui.presenter.RankContentPresenter;
import com.spriteapp.booklibrary.ui.view.RankView;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.ui.fragment.ChoiceFragment.ARG_COLUMN_COUNT;

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
    private LinearLayout null_layout;
    private TextView miaoshu;
    private int sex;

    public RankListFragment() {
    }

    public static RankListFragment newInstance(int interval, int columnCount) {
        Bundle bundle = new Bundle();
        bundle.putInt("interval", interval);
        bundle.putInt(ARG_COLUMN_COUNT, columnCount);
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
            interval = bundle.getInt("interval");
            sex = bundle.getInt(ARG_COLUMN_COUNT);
        }
        getData();
    }

    @Override
    public void findViewId() {
        swipe_refresh = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipe_refresh);
        null_layout = (LinearLayout) mParentView.findViewById(R.id.null_layout);
        miaoshu = (TextView) mParentView.findViewById(R.id.miaoshu);
        miaoshu.setText("竟然没有加载出来");
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        RecyclerView recyclerView = (RecyclerView) mParentView.findViewById(R.id.list);
        adapter = new RankAdapter(lists, getActivity());
        recyclerView.setAdapter(adapter);
        goneOrShow();

    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onError(Throwable t) {
        if (swipe_refresh != null)
            swipe_refresh.setRefreshing(false);
    }

    public void goneOrShow() {
        if (lists.size() == 0) {
            null_layout.setVisibility(View.VISIBLE);
        } else {
            null_layout.setVisibility(View.GONE);
        }
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
        goneOrShow();
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
     *
     * @param interval
     */
    public void sortRefresh(int type, int interval) {
        this.type = type;
        this.interval = interval;
        getData();
    }

    /**
     * 周  月  总
     *
     * @param interval
     */
    public void timeRefresh(int interval) {
        page = 1;
        this.interval = interval;
        getData();
    }

    public void getData() {
        if (presenter != null)
            presenter.requestGetData(type, interval, sex);
    }
}
