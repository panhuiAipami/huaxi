package com.spriteapp.booklibrary.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.ui.dialog.DelPop;
import com.spriteapp.booklibrary.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.ui.adapter.BookShelfAdapter.ISSHU;

/**
 * Created by userfirst on 2018/4/20.
 */

public class OutsideBookShelfFragment extends BaseFragment {
    private ViewPager viewPager;
    private HomePageTabAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private SlidingTabLayout mTabLayout_1;
    private String[] mTitles = {"收藏", "最近阅读", "你可能喜欢"};
    private BookshelfFragment bookshelfFragment1, bookshelfFragment2, bookshelfFragment3;
    private ChoiceFragment choiceFragment1, choiceFragment2;
    private DetailsStoreFragment detailsStoreFragment1, detailsStoreFragment2;
    private TextView titleView1;
    private TextView titleView2;
    private TextView titleView3;
    private LinearLayout sex_layout;
    private ImageView search_btn;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void configViews() {
        initFragment();
        listener();
    }


    @Override
    public void findViewId() {
        mTabLayout_1 = (SlidingTabLayout) mParentView.findViewById(R.id.mTabLayout_1);
        viewPager = (ViewPager) mParentView.findViewById(R.id.viewPager);
        sex_layout = (LinearLayout) mParentView.findViewById(R.id.sex_item);
        search_btn = (ImageView) mParentView.findViewById(R.id.search_btn);
        mTabLayout_1.setPadding(ScreenUtil.dpToPxInt(20), 0, ScreenUtil.dpToPxInt(20), 0);
    }

    @Override
    protected void lazyLoad() {

    }

    public static boolean isGrid = true;

    private void listener() {
        search_btn.setImageResource(R.mipmap.edit_store_icon);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                final DelPop delPop = new DelPop(getActivity(), search_btn, isGrid ? "列表模式" : "书封模式", isGrid ? R.mipmap.edit_list_icon : R.mipmap.edit_grid_icon, currentItem);
                delPop.getList_text().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchList(isGrid ? 9 : 3);
                        isGrid = !isGrid;
                        delPop.dismiss();
                    }
                });
                delPop.getDel_text().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setDel();
                        delPop.dismiss();
                    }
                });

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    titleView1.setTextSize(17);
                    titleView2.setTextSize(15);
                    titleView3.setTextSize(15);
                } else if (position == 1) {
                    titleView1.setTextSize(15);
                    titleView2.setTextSize(17);
                    titleView3.setTextSize(15);
                } else if (position == 2) {
                    titleView1.setTextSize(15);
                    titleView2.setTextSize(15);
                    titleView3.setTextSize(17);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initFragment() {
        sex_layout.setVisibility(View.GONE);
        bookshelfFragment1 = new BookshelfFragment();
//        bookshelfFragment2 = new BookshelfFragment();
//        bookshelfFragment3 = new BookshelfFragment();
//        choiceFragment1 = ChoiceFragment.newInstance(0);
//        choiceFragment2 = ChoiceFragment.newInstance(0);
        detailsStoreFragment1 = DetailsStoreFragment.newInstance(0);
        detailsStoreFragment2 = DetailsStoreFragment.newInstance(1);
        fragmentList.add(bookshelfFragment1);
        fragmentList.add(detailsStoreFragment1);
        fragmentList.add(detailsStoreFragment2);
        viewPager.setOffscreenPageLimit(3);
        adapter = new HomePageTabAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        mTabLayout_1.setViewPager(viewPager, mTitles);
        titleView1 = mTabLayout_1.getTitleView(0);
        titleView2 = mTabLayout_1.getTitleView(1);
        titleView3 = mTabLayout_1.getTitleView(2);
        titleView1.setTextSize(18);
        titleView2.setTextSize(16);
        titleView3.setTextSize(16);

    }

    private void switchList(int flag) {//切换列表
        if (bookshelfFragment1 != null) {

            if (flag == 9) {
                ISSHU = true;
            } else {
                ISSHU = false;
            }
            bookshelfFragment1.setRecyclerViewMode(flag);
        }
        if (detailsStoreFragment1 != null) {
            detailsStoreFragment1.setRecyclerViewMode(flag);
        }
        if (detailsStoreFragment2 != null) {
            detailsStoreFragment2.setRecyclerViewMode(flag);
        }

    }

    public void setDel() {
        bookshelfFragment1.setDel();
    }


    //以下方法暂时不管
    public void refreshSort() {
        bookshelfFragment1.refreshSort();
    }

    public void setFinish(int v) {
        bookshelfFragment1.setFinish(v);
    }

    public int getIs_del1() {
        return bookshelfFragment1.getIs_del1();
    }
}
