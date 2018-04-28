package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.ui.fragment.RankFragment;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.util.ActivityUtil.FRAGMENT_SEX;
import static com.spriteapp.booklibrary.util.ActivityUtil.FRAGMENT_TYPE;

/**
 * 排行activity
 */
public class NestingActivity extends TitleActivity {
    private ViewPager nestingViewPager;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int sex;


    @Override
    public void initData() throws Exception {
        setTitle("排行");
        initFragment();

    }


    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_nesting, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        nestingViewPager = (ViewPager) findViewById(R.id.nestingViewPager);
    }

    private void initFragment() {
        Intent intent = getIntent();
        int count = intent.getIntExtra(FRAGMENT_TYPE, 0);
        sex = intent.getIntExtra(FRAGMENT_SEX, 0);
        switch (count) {
            case 0:
                break;
            case 1:
                getRankingFragment();
                break;
        }

    }

    private void getRankingFragment() {//得到排行fragment
        RankFragment rankFragment = RankFragment.newInstance(sex);
        fragmentList.add(rankFragment);
        nestingViewPager.setOffscreenPageLimit(fragmentList.size());
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        nestingViewPager.setAdapter(adapter);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {


        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
