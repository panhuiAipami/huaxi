package com.spriteapp.booklibrary.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;
import static com.spriteapp.booklibrary.ui.fragment.HomePageFragment.FRAGMENTTYPE;

/**
 * 首页（精选和排行）
 */
public class HomeFragment extends BaseFragment {

    private PopupWindow popupWindow;
    private LinearLayout sex_layout;
    private CheckBox boy_or_girl;
    private TextView selector_man, selector_woman;
    private View view;
    private ViewPager viewPager;
    private HomePageTabAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private SlidingTabLayout mTabLayout_1;
    private String[] mTitles = {"精选", "排行"};
    private ChoiceFragment choiceFragment;
    private int reload;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void configViews() {

    }

    @Override
    public void initData() {
        super.initData();
        reload = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
        if (reload == 1) {
            boy_or_girl.setChecked(true);
        } else {
            boy_or_girl.setChecked(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && boy_or_girl != null) {
            int load = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
            if (reload != load) {
                setCheck(true);
                reload = load;
            } else {
                setCheck(false);
            }

        }
    }

    @Override
    public void findViewId() {
        mTabLayout_1 = (SlidingTabLayout) view.findViewById(R.id.mTabLayout_1);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        sex_layout = (LinearLayout) view.findViewById(R.id.sex_item);
        boy_or_girl = (CheckBox) view.findViewById(R.id.boy_or_girl);

        choiceFragment = ChoiceFragment.newInstance(0);

        DiscoverFragment discoverFragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENTTYPE, 2);
        discoverFragment.setArguments(bundle);

        fragmentList.add(choiceFragment);
        fragmentList.add(RankFragment.newInstance());
        adapter = new HomePageTabAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        mTabLayout_1.setViewPager(viewPager, mTitles);

        listener();
    }

    @Override
    protected void lazyLoad() {

    }

    public void listener() {
        sex_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopWindow(sex_layout);
            }
        });

        view.findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {//搜索
            @Override
            public void onClick(View v) {
                ActivityUtil.toWebViewActivity(getActivity(), Constant.BOOK_SEARCH_URL);
            }
        });
    }

    public void showpopWindow(View v) {
        final ImageView top_bottom = (ImageView) view.findViewById(R.id.top_bottom);
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
        if (sex == 1) {
            boy_or_girl.setChecked(true);
            boy_or_girl.setText("男生");
            if (isReLoad)
                choiceFragment.lazyLoad();
            if (selector_man != null)
                selector_man.setBackgroundResource(R.color.pop_back);
        } else if (sex == 2) {
            boy_or_girl.setChecked(false);
            boy_or_girl.setText("女生");
            if (isReLoad)
                choiceFragment.lazyLoad();
            if (selector_woman != null)
                selector_woman.setBackgroundResource(R.color.pop_back);
        }else{

        }
    }

}
