package com.spriteapp.booklibrary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.CateBean;
import com.spriteapp.booklibrary.model.StoreBean;
import com.spriteapp.booklibrary.model.TabBar;
import com.spriteapp.booklibrary.model.store.AppUpDateModel;
import com.spriteapp.booklibrary.ui.fragment.BookshelfFragment;
import com.spriteapp.booklibrary.ui.fragment.HomePageFragment;
import com.spriteapp.booklibrary.ui.fragment.MeFragment;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.fragment.HomePageFragment.FRAGMENTTYPE;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class HomeActivity extends TitleActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    public static final String SEX = "sex";
    private static final int BOOKSHELF_POSITION = 0;
    private static final int DISCOVER_POSITION = 1;
//    private static final int COMMUNITY_POSITION = 2;
    private static final int BOOKSTORE_POSITION = 2;
    private static final int ME_POSITION = 3;
    private static final int TOP_BAR_HEIGHT = 47;
    ViewPager mHomeViewPager;
    LinearLayout mBookshelfLayout;
    LinearLayout mDiscoverLayout;
    LinearLayout mBookstoreLayout;
    LinearLayout mMeLayout;
    LinearLayout mCommunityLayout;
    private List<Fragment> mFragmentList;
    private ViewPagerAdapter mAdapter;
    private Context mContext;
    public static Context libContent;
    public static Activity libActivity;
    private List<StoreBean> sy = new ArrayList<>();
    private List<StoreBean> shu = new ArrayList<>();
    private HomePageFragment homePageFragment1;
    private HomePageFragment homePageFragment2;
    private ImageView image_one, image_two, image_three, image_four, image_five;
    private TextView text_one, text_two, text_three, text_four, text_five;
    private LinearLayout icon_layout;
    private View icon_line;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Drawable drawable = (Drawable) msg.obj;
            switch (msg.what) {
                case 0:
                    image_one.setImageDrawable(drawable);
                    break;
                case 1:
                    image_two.setImageDrawable(drawable);
                    break;
                case 2:
                    image_three.setImageDrawable(drawable);
                    break;
                case 3:
                    image_four.setImageDrawable(drawable);
                    break;
            }
        }
    };

    @Override
    public void initData() {
        mContext = this;
        int fisrtSex = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
        if (fisrtSex == 0) {
            SharedPreferencesUtil.getInstance().putInt(SEX, HuaXiSDK.getInstance().getSex());
        }
        setTitle("精选");
//        addFreeTextView();
        addSearchView();
        initFragment();
        setAdapter();
        setListener();
        appUpdate();
        addFlag();
        libContent = getApplicationContext();//获取lib上下文
        libActivity = this;
//        requestPermissions();
    }

    public void getUserInfo() {
        Util.getUserInfo();
    }

    public void addFlag() {
        BookApi.getInstance()
                .service
                .app_cate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<CateBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<CateBean> appUpDateModelBase) {
                        Log.d("cate11", appUpDateModelBase.toString() + "yyy");
                        if (appUpDateModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
//                            String name = appUpDateModelBase.getData().toString();
//                            Log.d("cate11", name + "哈g哈");
//                            Gson gson = new Gson();
//                            String s = gson.toJson(appUpDateModelBase.getData());
//                            Log.d("cate1111", s + "哈g哈");
                            if (appUpDateModelBase.getData() != null) {
                                if (appUpDateModelBase.getData().getHello_messages() != null && !appUpDateModelBase.getData().getHello_messages().isEmpty()) {
                                    ToastUtil.showLongToast(appUpDateModelBase.getData().getHello_messages());
                                }
                                if (appUpDateModelBase.getData().getTop_menu() != null && appUpDateModelBase.getData().getTop_menu().getStore() != null && appUpDateModelBase.getData().getTop_menu().getStore().size() != 0) {
                                    sy.addAll(appUpDateModelBase.getData().getTop_menu().getStore());
                                    if (homePageFragment1 != null) {
                                        homePageFragment1.getHttpListData(appUpDateModelBase.getData().getTop_menu());
                                    }
                                    if (homePageFragment2 != null) {
                                        homePageFragment2.getHttpListData(appUpDateModelBase.getData().getTop_menu());
                                    }
                                }
                                if (appUpDateModelBase.getData().getTop_menu() != null && appUpDateModelBase.getData().getTop_menu().getChosen() != null && appUpDateModelBase.getData().getTop_menu().getChosen().size() != 0) {
                                    shu.addAll(appUpDateModelBase.getData().getTop_menu().getChosen());
                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.PathTitle, appUpDateModelBase.getData().getTop_menu());
                                }
                                if (appUpDateModelBase.getData().getTabbar() != null) {
                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.NAVIGATION, appUpDateModelBase.getData().getTabbar());
                                }
                                if (appUpDateModelBase.getData().getSplashscreen() != null && !appUpDateModelBase.getData().getSplashscreen().isEmpty()) {
                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.START_PAGE, appUpDateModelBase.getData().getSplashscreen());
                                }
                            }

                        }


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("cate11", "失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void appUpdate() {
        BookApi.getInstance()
                .service
                .app_Upate(com.spriteapp.booklibrary.util.Util.getAppMetaData(HomeActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<AppUpDateModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<AppUpDateModel> appUpDateModelBase) {
                        Log.d("update11", appUpDateModelBase.toString() + "yyy");
                        if (appUpDateModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            String name = appUpDateModelBase.getData().toString();
//                            Log.d("update11", name + "哈g哈");
                            Util.chechForUpdata("checkForUpdates", appUpDateModelBase.getData(), HomeActivity.this, false);
                        }


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("update11", "失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setListener() {
        mHomeViewPager.addOnPageChangeListener(new ViewPagerListener());
        mBookshelfLayout.setOnClickListener(this);
        mDiscoverLayout.setOnClickListener(this);
        mBookstoreLayout.setOnClickListener(this);
        mMeLayout.setOnClickListener(this);
        mCommunityLayout.setOnClickListener(this);
    }

    private void setAdapter() {
        mHomeViewPager.setOffscreenPageLimit(4);//viewpager缓存
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mHomeViewPager.setAdapter(mAdapter);
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
//        mFragmentList.add(new BookshelfFragment());
//        mFragmentList.add(new NativeBookStoreFragment());
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        homePageFragment1 = new HomePageFragment();
        homePageFragment2 = new HomePageFragment();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putInt(FRAGMENTTYPE, 1);
        bundle2.putInt(FRAGMENTTYPE, 2);
        homePageFragment1.setArguments(bundle1);
        homePageFragment2.setArguments(bundle2);
        mFragmentList.add(homePageFragment1);
        mFragmentList.add(homePageFragment2);
//        mFragmentList.add(new DiscoverFragment());
//        mFragmentList.add(new CommunityFragment());//社区分类
        mFragmentList.add(new BookshelfFragment());
        mFragmentList.add(new MeFragment());
    }

    @Override
    public void configViews() {
        super.configViews();
        gone(mBackImageView);
        gone(mTitleLayout);
        setViewpagerTopMargin(0);
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_book, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() {
        super.findViewId();
        mHomeViewPager = (ViewPager) findViewById(R.id.book_reader_home_view_pager);
        mBookshelfLayout = (LinearLayout) findViewById(R.id.book_reader_bookshelf_layout);
        mDiscoverLayout = (LinearLayout) findViewById(R.id.book_reader_discover_layout);
        mBookstoreLayout = (LinearLayout) findViewById(R.id.book_reader_bookstore_layout);
        icon_layout = (LinearLayout) findViewById(R.id.icon_layout);
        icon_line = findViewById(R.id.icon_line);
        mMeLayout = (LinearLayout) findViewById(R.id.book_reader_me_layout);
        mCommunityLayout = (LinearLayout) findViewById(R.id.book_reader_community_layout);
        image_one = (ImageView) findViewById(R.id.image_one);
        image_two = (ImageView) findViewById(R.id.image_two);
        image_three = (ImageView) findViewById(R.id.image_three);
        image_four = (ImageView) findViewById(R.id.image_four);
        image_five = (ImageView) findViewById(R.id.image_five);
        text_one = (TextView) findViewById(R.id.text_one);
        text_two = (TextView) findViewById(R.id.text_two);
        text_three = (TextView) findViewById(R.id.text_three);
        text_four = (TextView) findViewById(R.id.text_four);
        text_five = (TextView) findViewById(R.id.text_five);
        setIcon();
        mBookshelfLayout.setSelected(true);
    }

    public void setIcon() {
        List<TextView> textViewList = new ArrayList<>();
        List<ImageView> imageViewList = new ArrayList<>();
        textViewList.add(text_one);
        textViewList.add(text_two);
        textViewList.add(text_three);
        textViewList.add(text_four);
        imageViewList.add(image_one);
        imageViewList.add(image_two);
        imageViewList.add(image_three);
        imageViewList.add(image_four);
        try {
            TabBar tabBar = FileHelper.readObjectFromJsonFile(this, Constant.NAVIGATION, TabBar.class);
            if (AppUtil.isNetAvailable(this)) {
                if (tabBar != null && tabBar.getColor() != null && !tabBar.getColor().isEmpty() && tabBar.getColor_on() != null && tabBar.getBackground_color() != null && !tabBar.getBackground_color().isEmpty() && tabBar.getBorder_style() != null && !tabBar.getBorder_style().isEmpty() && !tabBar.getColor_on().isEmpty() && tabBar.getLists() != null && tabBar.getLists().size() >= 4) {
                    Log.d("textViewColor", "进入修改颜色的方法");
                    icon_layout.setBackgroundColor(Color.parseColor("#" + tabBar.getBackground_color()));
                    icon_line.setBackgroundColor(Color.parseColor("#" + tabBar.getBorder_style()));
                    setTextColor(tabBar, tabBar.getColor(), tabBar.getColor_on(), textViewList);
                }
                if (tabBar != null && tabBar.getLists() != null && tabBar.getLists().size() >= 4) {
                    Log.d("textViewColor", "进入修改icon的方法");
                    setImageIcon(imageViewList, tabBar);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextColor(TabBar tab, String normalColor, String seletorColor, List<TextView> text) {
        for (int i = 0; i < text.size(); i++) {
            Log.d("textViewColor", tab.getLists().get(i).getText());
            int[] colors = new int[]{Color.parseColor("#" + normalColor), Color.parseColor("#" + seletorColor), Color.parseColor("#" + normalColor)};
            int[][] states = new int[3][];
            states[0] = new int[]{-android.R.attr.state_selected};
            states[1] = new int[]{android.R.attr.state_selected};
            states[2] = new int[]{};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            text.get(i).setTextColor(colorStateList);//修改文本颜色
            text.get(i).setText(tab.getLists().get(i).getText());//修改文本
            Log.d("textViewColor", tab.getLists().get(i).getText());
        }
    }

    public void setImageIcon(final List<ImageView> image, final TabBar tab) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int dpi = dm.densityDpi;
                int icon_type = dpi > 320 ? 3 : 2;
                for (int i = 0; i < image.size(); i++) {
                    //修改底部图标
                    StateListDrawable drawable = new StateListDrawable();
                    drawable.addState(new int[]{android.R.attr.state_selected}, GlideUtils.loadImageFromNetwork(icon_type == 2 ? tab.getLists().get(i).getIcon_on().get_$2x() : tab.getLists().get(i).getIcon_on().get_$3x()));
                    drawable.addState(new int[]{-android.R.attr.state_pressed}, GlideUtils.loadImageFromNetwork(icon_type == 2 ? tab.getLists().get(i).getIcon().get_$2x() : tab.getLists().get(i).getIcon().get_$3x()));
                    Message message = new Message();
                    message.obj = drawable;
                    message.what = i;
                    handler.sendMessage(message);
                    Log.d("textViewColor", tab.getLists().get(i).getIcon_on().get_$2x());
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v == mBookshelfLayout) {
            mHomeViewPager.setCurrentItem(BOOKSHELF_POSITION);
            setSelectView(BOOKSHELF_POSITION);
        } else if (v == mDiscoverLayout) {
            mHomeViewPager.setCurrentItem(DISCOVER_POSITION);
            setSelectView(DISCOVER_POSITION);
        } else if (v == mBookstoreLayout) {
            mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
            setSelectView(BOOKSTORE_POSITION);
        } else if (v == mMeLayout) {
            mHomeViewPager.setCurrentItem(ME_POSITION);
            setSelectView(ME_POSITION);
        }
    }

    private void setSelectView(int position) {//view的选中状态
        switch (position) {
            case BOOKSHELF_POSITION:
                mBookshelfLayout.setSelected(true);
                setSelectFalse(mDiscoverLayout, mBookstoreLayout, mMeLayout, mCommunityLayout);
                break;
            case DISCOVER_POSITION:
                mDiscoverLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mBookstoreLayout, mMeLayout, mCommunityLayout);
                break;
            case BOOKSTORE_POSITION:
                mBookstoreLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mMeLayout, mCommunityLayout);
                break;
            case ME_POSITION:
                mMeLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mBookstoreLayout, mCommunityLayout);
                break;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {


        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    private class ViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case BOOKSHELF_POSITION:
//                    addFreeTextView();
                    addSearchView();
                    setTitle(R.string.book_reader_book_selector);
                    setSelectView(BOOKSHELF_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
                case DISCOVER_POSITION:
                    mRightLayout.removeAllViews();
                    setTitle(R.string.book_reader_bookstore);
                    addSearchView();
                    setSelectView(DISCOVER_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
                case BOOKSTORE_POSITION:
                    setTitle(R.string.book_reader_bookshelf);
//                    addSearchView();
                    addFreeTextView();
                    setSelectView(BOOKSTORE_POSITION);
                    visible(mTitleLayout);
                    setViewpagerTopMargin(TOP_BAR_HEIGHT);
                    break;
                case ME_POSITION:
                    setTitle(R.string.book_reader_me);
                    mRightLayout.removeAllViews();
                    setSelectView(ME_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void setViewpagerTopMargin(int topMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHomeViewPager.getLayoutParams();
        params.topMargin = ScreenUtil.dpToPxInt(topMargin);
        mHomeViewPager.setLayoutParams(params);
    }

    private void addSearchView() {
        mRightLayout.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.book_reader_book_store_search_layout, null);
        mRightLayout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toWebViewActivity(mContext, Constant.BOOK_SEARCH_URL);
            }
        });
    }

    private void addFreeTextView() {
        mRightLayout.removeAllViews();
        TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.book_reader_free_text_layout, null);
        int rightTitleColor = HuaXiSDK.getInstance().getConfig().getRightTitleColor();
        if (rightTitleColor != 0) {
            view.setTextColor(rightTitleColor);
        }
        mRightLayout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppUtil.isLogin()) {
                    HuaXiSDK.getInstance().toLoginPage(mContext);
                    return;
                }
                ActivityUtil.toWebViewActivity(mContext, Constant.CHECK_IN_URL);
            }
        });
    }

    @Override
    public void onBackPressed() {
        BookshelfFragment shelfFragment = getShelfFragment();
        if (shelfFragment != null && shelfFragment.isDeleteBook()) {
            shelfFragment.setDeleteBook();
            return;
        }
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && !(currentFragment instanceof BookshelfFragment)) {
            mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
            setSelectView(BOOKSTORE_POSITION);
            return;
        }
        super.onBackPressed();
    }

    public Fragment getCurrentFragment() {
        if (CollectionUtil.isEmpty(mFragmentList)) {
            return null;
        }
        int currentItem = mHomeViewPager.getCurrentItem();
        if (currentItem < 0 || currentItem >= mFragmentList.size()) {
            return null;
        }
        return mFragmentList.get(currentItem);
    }

    public BookshelfFragment getShelfFragment() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof BookshelfFragment) {
            return (BookshelfFragment) currentFragment;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragmentList.get(2).onActivityResult(requestCode, resultCode, data);//将返回传给社区Fragment
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
    }

    public void requestPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);// 读sd卡
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);// 写sd卡
        permissions.add(Manifest.permission.READ_PHONE_STATE);// 允许程序访问电话状态
        doRequestPermissions(new PermissionRequestObj(permissions) {

            public void callback(boolean allGranted, List<String> list, TitleActivity.PermissionRequestObj pro) {
                if (allGranted) {

                }
            }
        });
    }
}
