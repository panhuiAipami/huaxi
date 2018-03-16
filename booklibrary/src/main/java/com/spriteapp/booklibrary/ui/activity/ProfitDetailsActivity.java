package com.spriteapp.booklibrary.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.ui.fragment.ApprenticeFragment;
import com.spriteapp.booklibrary.ui.fragment.ProfixDetailsFragment;

import java.util.ArrayList;
import java.util.List;

public class ProfitDetailsActivity extends TitleActivity {
    public static final String Profit_TYPE = "profit_type";
    private SlidingTabLayout tab_layout;
    private ViewPager view_pager;
    private ProfixDetailsFragment alipayFragment, wechatFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] mTitles = {"收益", "提现"};
    private HomePageTabAdapter adapter;


    @Override
    public void initData() throws Exception {
        setTitle("收益明细");
        initFragment();
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_apprentice_list, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        tab_layout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
    }

    private void initFragment() {
        alipayFragment = new ProfixDetailsFragment();
        wechatFragment = new ProfixDetailsFragment();
        Bundle bundle = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle.putInt(Profit_TYPE, 0);
        bundle2.putInt(Profit_TYPE, 1);
        alipayFragment.setArguments(bundle);
        wechatFragment.setArguments(bundle2);
        fragmentList.add(alipayFragment);
        fragmentList.add(wechatFragment);
        adapter = new HomePageTabAdapter(getSupportFragmentManager(), fragmentList);
        view_pager.setAdapter(adapter);
        tab_layout.setViewPager(view_pager, mTitles);


    }
}
