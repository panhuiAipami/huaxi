package com.spriteapp.booklibrary.ui.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.ui.dialog.RankSortPop;

import java.util.ArrayList;
import java.util.List;


/**
 * 排行
 */
public class RankFragment extends BaseFragment {
    private ViewPager viewPager;
    private RankSortPop rankSortPop;
    private TextView ranking_switch_button;
    private SlidingTabLayout mTabLayout;
    private HomePageTabAdapter adapter;
    private List<Fragment> fragments = new ArrayList<>();
    private String[] mTitles = {"周榜", "月榜", "总榜"};
    private RankListFragment rankListFragment1, rankListFragment2, rankListFragment3;
    List<TextPaint> list = new ArrayList<>();

    public RankFragment() {
    }

    public static RankFragment newInstance() {
        RankFragment fragment = new RankFragment();
        return fragment;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.rank_fragment;
    }

    @Override
    public void configViews() {
        rankListFragment1 = RankListFragment.newInstance(1);
        rankListFragment2 = RankListFragment.newInstance(2);
        rankListFragment3 = RankListFragment.newInstance(3);
        fragments.add(rankListFragment1);
        fragments.add(rankListFragment2);
        fragments.add(rankListFragment3);

        adapter = new HomePageTabAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        mTabLayout.setViewPager(viewPager, mTitles);
        TextView titleView1 = mTabLayout.getTitleView(0);
        TextView titleView2 = mTabLayout.getTitleView(1);
        TextView titleView3 = mTabLayout.getTitleView(2);
        TextPaint paint1 = titleView1.getPaint();
        TextPaint paint2 = titleView2.getPaint();
        TextPaint paint3 = titleView3.getPaint();
        list.add(paint1);
        list.add(paint2);
        list.add(paint3);
        setTextStyle(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTextStyle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setTextStyle(int pos) {
        Log.d("setTextStyle","pos");
        for (int i = 0; i < list.size(); i++) {
            if (pos == i){
                list.get(i).setFakeBoldText(true);
                Log.d("setTextStyle","true");
            }

            else
                list.get(i).setFakeBoldText(false);
        }
    }

    @Override
    public void findViewId() {
        viewPager = (ViewPager) mParentView.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        ranking_switch_button = (TextView) mParentView.findViewById(R.id.ranking_switch_button);
        mTabLayout = (SlidingTabLayout) mParentView.findViewById(R.id.mTabLayout);

        final Drawable d1 = getResources().getDrawable(R.mipmap.top_jiao);
        final Drawable d2 = getResources().getDrawable(R.mipmap.bottom_jiao);

        ranking_switch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rankSortPop == null) {
                    rankSortPop = new RankSortPop(getActivity(), v);
                } else {
                    rankSortPop.showAsDropDown(v);
                }
                d1.setBounds(0, 0, d1.getMinimumWidth(), d1.getMinimumHeight());
                ranking_switch_button.setCompoundDrawablesWithIntrinsicBounds(null, null, d1, null);
                rankSortPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        d2.setBounds(0, 0, d2.getMinimumWidth(), d2.getMinimumHeight());
                        ranking_switch_button.setCompoundDrawablesWithIntrinsicBounds(null, null, d2, null);
                    }
                });

                rankSortPop.setSortOnItemClickListener(new RankSortPop.OnItemClickListener() {
                    @Override
                    public void refresh(int interval, String name) {
                        ranking_switch_button.setText(name);

                        if (rankListFragment1 != null) {
                            rankListFragment1.sortRefresh(interval, 1);
                        }
                        if (rankListFragment2 != null) {
                            rankListFragment2.sortRefresh(interval, 2);
                        }
                        if (rankListFragment3 != null) {
                            rankListFragment3.sortRefresh(interval, 3);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void lazyLoad() {

    }

    public void onRefreshData() {
        if (rankListFragment1 != null) {
            rankListFragment1.timeRefresh(1);
        }
        if (rankListFragment2 != null) {
            rankListFragment2.timeRefresh(2);
        }
        if (rankListFragment3 != null) {
            rankListFragment3.timeRefresh(3);
        }
    }
}

