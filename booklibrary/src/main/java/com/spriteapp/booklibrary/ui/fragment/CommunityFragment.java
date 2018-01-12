package com.spriteapp.booklibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */

public class CommunityFragment extends BaseFragment {
    private View mView;
    private TextView tab_one, tab_two;//title标题
    private ImageView search_btn;//搜索按钮
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FollowFragment followFragment;
    private SquareFragment squareFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<TextView> textViewList = new ArrayList<>();
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
        tab_one = (TextView) mView.findViewById(R.id.tab_one);
        tab_two = (TextView) mView.findViewById(R.id.tab_two);
        search_btn = (ImageView) mView.findViewById(R.id.search_btn);
        viewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) mView.findViewById(R.id.tabLayout);
        tabLayout.post(new Runnable() {//修改下划线的长度
            @Override
            public void run() {
                Util.setIndicator(tabLayout, 40, 40);
            }
        });
        listener();
        initFragment();
    }

    public void listener() {
        tab_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        tab_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
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
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setColor(position);//修改标题颜色
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void initFragment() {
        //添加titleName集合
        textViewList.add(tab_one);
        textViewList.add(tab_two);
        //添加标题
        titles.add("关注");
        titles.add("广场");
        tab_one.setText(titles.get(0));
        tab_two.setText(titles.get(1));
        //实例化fragment
        followFragment = new FollowFragment();
        squareFragment = new SquareFragment();
        //添加fragmnet集合
        fragmentList.add(followFragment);
        fragmentList.add(squareFragment);
        //viewpager缓存页面
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new HomePageTabAdapter(getChildFragmentManager(), titles, fragmentList, 1));
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    protected void lazyLoad() {

    }

    public void setColor(int pos) {//选中字体颜色修改
        for (int i = 0; i < titles.size(); i++) {
            if (pos == i) {
                textViewList.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.book_reader_black));
                textViewList.get(i).setTextSize(18);
            } else {
                textViewList.get(i).setTextColor(ContextCompat.getColor(getActivity(), R.color.book_reader_half_black));
                textViewList.get(i).setTextSize(16);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentList.get(0).onActivityResult(requestCode, resultCode, data);
        fragmentList.get(1).onActivityResult(requestCode, resultCode, data);

    }
}
