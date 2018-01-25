package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页（精选和排行）
 */
public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;
    private ViewPager viewPager;
    private HomePageTabAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private SlidingTabLayout mTabLayout_1;
    private String[] mTitles = {"精选", "排行"};

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mTabLayout_1 = (SlidingTabLayout) view.findViewById(R.id.mTabLayout_1);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        Bundle bundle1 = new Bundle();
        bundle1.putInt(HomePageFragment.FRAGMENTTYPE, 1);
        DiscoverFragment discoverFragment = new DiscoverFragment();
        discoverFragment.setArguments(bundle1);

        fragmentList.add(discoverFragment);
        fragmentList.add(new PersonCenterFragment());
        adapter = new HomePageTabAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        mTabLayout_1.setViewPager(viewPager, mTitles);
        return view;
    }


}
