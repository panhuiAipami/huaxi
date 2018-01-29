package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.model.ChoiceBean;
import com.spriteapp.booklibrary.ui.adapter.ChoiceAdapter;
import com.spriteapp.booklibrary.ui.presenter.RankContentPresenter;
import com.spriteapp.booklibrary.ui.view.RankView;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行
 */
public class RankFragment extends BaseFragment implements RankView{
    private RankContentPresenter presenter = new RankContentPresenter(this);
    private SwipeRefreshLayout swipe_refresh;
    private List<ChoiceBean> lists = new ArrayList<>();

    public RankFragment() {
    }

    public static RankFragment newInstance() {
        RankFragment fragment = new RankFragment();
        return fragment;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.choice_fragment_item_list;
    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        swipe_refresh = (SwipeRefreshLayout) mParentView.findViewById(R.id.swipe_refresh);
        RecyclerView recyclerView = (RecyclerView) mParentView.findViewById(R.id.list);
        ChoiceAdapter adapter = new ChoiceAdapter(lists, getActivity());
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void lazyLoad() {
        presenter.requestGetData();
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void setData(Base<List<ChoiceBean>> result) {

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
}
