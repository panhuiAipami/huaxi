package com.spriteapp.booklibrary.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.model.CateBean;
import com.spriteapp.booklibrary.model.StoreBean;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;

/**
 * Created by Administrator on 2017/12/20.
 */

public class HomePageFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private LinearLayout sex_layout;
    private CheckBox boy_or_girl;
    private TextView tab_one, tab_two, tab_three;
    private ImageView search_btn, top_bottom;
    private ViewPager viewPager;
    private SlidingTabLayout tabLayout;
    public static List<StoreBean> syTitles = new ArrayList<>();
    public static List<StoreBean> shuTitles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private int type;
    public static final String FRAGMENTTYPE = "type";
    PopupWindow popupWindow;
    private TextView selector_man, selector_woman;
    private DiscoverFragment discoverFragment1;
    private DiscoverFragment discoverFragment2;
    private BookstoreFragment bookstoreFragment1;
    private BookstoreFragment bookstoreFragment2;
    private int reload;
    private HomePageTabAdapter adapter1;
    private HomePageTabAdapter adapter2;
    ChoiceFragment choiceFragment;
    private NewNativeBookStoreFragment bookStoreFragment, womanBookStoreFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_layout, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.tab_layout;
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            type = bundle.getInt(FRAGMENTTYPE, 0);
        }
        sex_layout = (LinearLayout) mView.findViewById(R.id.sex_item);
        boy_or_girl = (CheckBox) mView.findViewById(R.id.boy_or_girl);
        tab_one = (TextView) mView.findViewById(R.id.tab_one);
        tab_two = (TextView) mView.findViewById(R.id.tab_two);
        tab_three = (TextView) mView.findViewById(R.id.tab_three);
        search_btn = (ImageView) mView.findViewById(R.id.search_btn);
        top_bottom = (ImageView) mView.findViewById(R.id.top_bottom);
        viewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        tabLayout = (SlidingTabLayout) mView.findViewById(R.id.tabLayout);
        sex_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopWindow(sex_layout);
            }
        });
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
        tab_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {//搜索
            @Override
            public void onClick(View v) {
//                ActivityUtil.toWebViewActivity(getActivity(), Constant.BOOK_SEARCH_URL);
                ActivityUtil.toSearchActivity(getActivity());
            }

        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                setColor(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initFragment();
        reload = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
        Log.d("reload1", "reload===" + reload);
        if (reload == 1) {
            boy_or_girl.setChecked(true);
        } else {
            boy_or_girl.setChecked(false);
        }
        setCheck(false);
    }

    public void showpopWindow(View v) {
        top_bottom.setEnabled(false);
        View layout = View.inflate(getActivity(), R.layout.boy_or_girl, null);
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.pop_back));
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(v, 30, 0, Gravity.LEFT);
        viewClick(layout);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                top_bottom.setEnabled(true);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && boy_or_girl != null) {
            int load = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
            Log.d("reload1", "reload22===" + reload);
            if (reload != load) {
                setCheck(true);
                reload = load;
            } else {
                setCheck(false);
            }

        }
    }

    public void viewClick(View view) {
        setCheck(false);
        selector_man = (TextView) view.findViewById(R.id.selector_man);
        selector_woman = (TextView) view.findViewById(R.id.selector_woman);
        selector_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.getInstance().putInt(SEX, 1);
                setCheck(true);
                popupWindow.dismiss();
            }
        });
        selector_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.getInstance().putInt(SEX, 2);
                setCheck(true);
                popupWindow.dismiss();
            }
        });
    }

    public void setCheck(boolean isReLoad) {
        int sex = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
        if (sex == 0) {

        } else if (sex == 1) {
            boy_or_girl.setChecked(true);
            boy_or_girl.setText("男生");
            if (isReLoad)
                setData();
            if (selector_man != null)
                selector_man.setBackgroundResource(R.color.pop_back);
        } else if (sex == 2) {
            boy_or_girl.setChecked(false);
            boy_or_girl.setText("女生");
            if (isReLoad)
                setData();
            if (selector_woman != null)
                selector_woman.setBackgroundResource(R.color.pop_back);
        }
    }

//    public void addListData() {
//        CateBean.Top_menu top_menu = FileHelper.readObjectFromJsonFile(getActivity(), Constant.PathTitle, CateBean.Top_menu.class);
//        if (top_menu != null && top_menu.getStore() != null && top_menu.getChosen() != null) {
//            if (top_menu.getChosen().size() != 0) {
//                syTitles.clear();
//                syTitles.addAll(top_menu.getChosen());
//            }
//            if (top_menu.getStore().size() != 0) {
//                shuTitles.clear();
//                shuTitles.addAll(top_menu.getStore());
//            }
//        }
//    }

//    public void getHttpListData(CateBean.Top_menu top_menu) {
//        if (top_menu != null && top_menu.getStore() != null && top_menu.getChosen() != null) {
//            if (top_menu.getChosen().size() != 0) {
//                syTitles.clear();
//                syTitles.addAll(top_menu.getChosen());
//            }
//            if (top_menu.getStore().size() != 0) {
//                shuTitles.clear();
//                shuTitles.addAll(top_menu.getStore());
//            }
//            try {
//                CateBean.Top_menu menu = FileHelper.readObjectFromJsonFile(getActivity(), Constant.PathTitle, CateBean.Top_menu.class);
//                if (menu == null) {
//                    if (top_menu.getChosen().size() != 0) {
//                        syTitles.clear();
//                        syTitles.addAll(top_menu.getChosen());
//                    }
//                    if (top_menu.getStore().size() != 0) {
//                        shuTitles.clear();
//                        shuTitles.addAll(top_menu.getStore());
//                    }
//                    initFragment();
//                    Log.d("fragment", "重新加载");
//                }
//            } catch (Exception e) {
//
//            }
//        }
//    }

    public void setData() {
        if (choiceFragment != null) {
            choiceFragment.onRefreshData();
        }

        if (discoverFragment1 != null) {
            discoverFragment1.reLoadH5();
            Log.d("h5h5", "discoverFragment1重新加载");
        }
        if (discoverFragment2 != null) {
            discoverFragment2.reLoadH5();
            Log.d("h5h5", "discoverFragment2重新加载");
        }
        if (bookStoreFragment != null) {
            bookStoreFragment.reLoadH5();
            Log.d("h5h5", "bookstoreFragment1重新加载");
        }
        if (bookstoreFragment2 != null) {
            bookstoreFragment2.reLoadH5();
            Log.d("h5h5", "bookstoreFragment2重新加载");
        }

    }

//    public void setColor(int pos) {//选中字体颜色修改
//
//    }

    @Override
    protected void lazyLoad() {
    }

    public void initFragment() {


        shuTitles.add(new StoreBean("", "男生"));
        shuTitles.add(new StoreBean("", "女生"));
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putInt(FRAGMENTTYPE, 1);
        bundle2.putInt(FRAGMENTTYPE, 2);
        switch (type) {
            case 1://精选

            case 2://书城
                bookStoreFragment = new NewNativeBookStoreFragment();
                womanBookStoreFragment = new NewNativeBookStoreFragment();
                fragments.add(bookStoreFragment);
                fragments.add(womanBookStoreFragment);

                break;
        }

        if (type == 1) {


        } else if (type == 2) {
            Log.d("shutitles", "type==" + shuTitles.size());

            viewPager.setOffscreenPageLimit(shuTitles.size());
            if (adapter2 == null) {
                adapter2 = new HomePageTabAdapter(getChildFragmentManager(), shuTitles, fragments);
            }
            adapter2.notifyDataSetChanged();
            viewPager.setAdapter(adapter2);
        }
        tabLayout.setViewPager(viewPager);
        if (type == 2 && shuTitles.size() > 1) {
            tabLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.sex_item:
//                break;
//        }
    }
}
