package com.spriteapp.booklibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */

public class CommunityFragment extends BaseFragment {
    private View mView;
    private ImageView search_btn, to_send;//搜索按钮
    private ViewPager viewPager;
    private SlidingTabLayout tabLayout;
    private SquareFragment followFragment;
    private SquareFragment squareFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] mTitles = {"广场"};
    private int page = 1;

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
        search_btn = (ImageView) mView.findViewById(R.id.search_btn);
        to_send = (ImageView) mView.findViewById(R.id.to_send);
        viewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        tabLayout = (SlidingTabLayout) mView.findViewById(R.id.tabLayout);

        listener();
        initFragment();

    }

    public void listener() {
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("搜索");
//                if (!AppUtil.isLogin(getActivity())) {
//                    return;
//                }
//                ActivityUtil.toCreateDynamicActivity(getActivity());
            }
        });
        to_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppUtil.isLogin(getActivity())) {
                    return;
                }
                ActivityUtil.toCreateDynamicActivity(getActivity());
            }
        });
    }

    public void initFragment() {
//        followFragment = new SquareFragment();
        squareFragment = new SquareFragment();
        //添加fragmnet集合
//        fragmentList.add(followFragment);
        fragmentList.add(squareFragment);
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putInt("follow", 0);
        bundle2.putInt("follow", 1);
//        followFragment.setArguments(bundle2);
        squareFragment.setArguments(bundle1);
        //viewpager缓存页面
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new HomePageTabAdapter(getChildFragmentManager(), fragmentList));
        tabLayout.setViewPager(viewPager, mTitles);
//        viewPager.setCurrentItem(1);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentList.get(0).onActivityResult(requestCode, resultCode, data);
//        fragmentList.get(1).onActivityResult(requestCode, resultCode, data);

    }
}
