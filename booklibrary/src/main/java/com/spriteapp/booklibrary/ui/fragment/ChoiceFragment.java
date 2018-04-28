package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.model.ChoiceBean;
import com.spriteapp.booklibrary.ui.adapter.ChoiceAdapter;
import com.spriteapp.booklibrary.ui.presenter.ChoiceContentPresenter;
import com.spriteapp.booklibrary.ui.view.ChoiceView;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;

/**
 * 精选
 */
public class ChoiceFragment extends BaseFragment implements ChoiceView, URecyclerView.LoadingListener {

    public static final String ARG_COLUMN_COUNT = "column-count";
    private List<ChoiceBean> lists = new ArrayList<>();
    private SwipeRefreshLayout swipe_refresh;
    private RelativeLayout big_layout;
    private ChoiceContentPresenter contentPresenter = new ChoiceContentPresenter(this);
    private ChoiceAdapter adapter;
    int page = 1;
    int lastPage = 1;
    private int sex = 0;
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
            Log.d("mColumnCount", "执行mColumnCount===" + mColumnCount);
            if (mColumnCount == 0) {
                sex = 1;
            } else {
                sex = 2;
            }
        }

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        getWeekData();
    }

    @Override
    public void findViewId() {
        swipe_refresh = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipe_refresh);
        null_layout = (LinearLayout) mParentView.findViewById(R.id.null_layout);
        big_layout = (RelativeLayout) mParentView.findViewById(R.id.big_layout);
        miaoshu = (TextView) mParentView.findViewById(R.id.miaoshu);
        miaoshu.setText("竟然没有加载出来");
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        URecyclerView recyclerView = (URecyclerView) mParentView.findViewById(R.id.list);
        recyclerView.setStartLoadMorePos(2);
        recyclerView.setLoadingListener(this);
        adapter = new ChoiceAdapter(lists, getActivity());
        recyclerView.setAdapter(adapter);
        goneOrShow();


    }

    @Override
    protected void lazyLoad() {

    }

    public void goneOrShow() {
        if (null_layout != null) {
            if (lists.size() == 0) {
                null_layout.setVisibility(View.VISIBLE);
                big_layout.setBackgroundColor(getResources().getColor(R.color.app_background));
            } else {
                null_layout.setVisibility(View.GONE);
                big_layout.setBackgroundColor(getResources().getColor(R.color.pop_back));
            }
        }
    }

    public void onRefreshData() {
        page = 1;
        getWeekData();
    }


    protected void getWeekData() {
        lastPage = page;
        contentPresenter.requestGetData(page, sex);
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
            getWeekData();
        }
    }
}
