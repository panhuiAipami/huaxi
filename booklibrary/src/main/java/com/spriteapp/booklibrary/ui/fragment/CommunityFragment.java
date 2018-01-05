package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.util.Util;

/**
 * Created by Administrator on 2018/1/3.
 */

public class CommunityFragment extends BaseFragment {
    private View mView;
    private TextView tab_one, tab_two;//title标题
    private ImageView search_btn;//搜索按钮
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.community_layout, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.community_layout;
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        tab_one = (TextView) mView.findViewById(R.id.tab_one);
        tab_two = (TextView) mView.findViewById(R.id.tab_one);
        search_btn = (ImageView) mView.findViewById(R.id.search_btn);
        viewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) mView.findViewById(R.id.tabLayout);
        tabLayout.post(new Runnable() {//修改下划线的长度
            @Override
            public void run() {
                Util.setIndicator(tabLayout, Util.dp2px(getActivity(), 19), Util.dp2px(getActivity(), 19));
            }
        });
    }

    @Override
    protected void lazyLoad() {

    }
}
