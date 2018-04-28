package com.spriteapp.booklibrary.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;

import com.spriteapp.booklibrary.model.StoreBean;

import java.util.List;

/**
 * Created by Administrator on 2017/10/8.
 */

public class HomePageTabAdapter extends FragmentStatePagerAdapter {
    private List<StoreBean> titles;
    private List<String> title;
    private List<Fragment> fragments;
    public RecyclerView comment_listView;
    //    private LinearLayout no_comment_layout;
    private int type = 0;

    public HomePageTabAdapter(FragmentManager fm, List<StoreBean> titles, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    public HomePageTabAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments, int type) {
        super(fm);
        this.title = titles;
        this.fragments = fragments;
        this.type = type;
    }

    public HomePageTabAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (type == 0)
            return titles != null ? titles.get(position).getName() : "";
        else
            return title != null ? title.get(position) : "";
    }

}
