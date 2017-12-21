package com.spriteapp.booklibrary.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.spriteapp.booklibrary.model.store.AppUpDateModel;
import com.spriteapp.booklibrary.ui.fragment.BookshelfFragment;
import com.spriteapp.booklibrary.ui.fragment.HomePageFragment;
import com.spriteapp.booklibrary.ui.fragment.MeFragment;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
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
    private static final int BOOKSTORE_POSITION = 2;
    private static final int ME_POSITION = 3;
    private static final int TOP_BAR_HEIGHT = 47;
    ViewPager mHomeViewPager;
    LinearLayout mBookshelfLayout;
    LinearLayout mDiscoverLayout;
    LinearLayout mBookstoreLayout;
    LinearLayout mMeLayout;
    private List<Fragment> mFragmentList;
    private ViewPagerAdapter mAdapter;
    private Context mContext;
    public static Context libContent;
    private List<StoreBean> sy = new ArrayList<>();
    private List<StoreBean> shu = new ArrayList<>();
    private HomePageFragment homePageFragment1;
    private HomePageFragment homePageFragment2;

    @Override
    public void initData() {
        mContext = this;
        int fisrtSex=SharedPreferencesUtil.getInstance().getInt(SEX,0);
        if(fisrtSex==0){
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
//        requestPermissions();
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
                            if (appUpDateModelBase.getData() != null && appUpDateModelBase.getData().getTop_menu() != null && appUpDateModelBase.getData().getTop_menu().getStore() != null && appUpDateModelBase.getData().getTop_menu().getStore().size() != 0) {
                                sy.addAll(appUpDateModelBase.getData().getTop_menu().getStore());
                                if (homePageFragment1 != null) {
                                    homePageFragment1.getHttpListData(appUpDateModelBase.getData().getTop_menu());
                                }
                                if (homePageFragment2 != null) {
                                    homePageFragment2.getHttpListData(appUpDateModelBase.getData().getTop_menu());
                                }
                            }
                            if (appUpDateModelBase.getData() != null && appUpDateModelBase.getData().getTop_menu() != null && appUpDateModelBase.getData().getTop_menu().getChosen() != null && appUpDateModelBase.getData().getTop_menu().getChosen().size() != 0) {
                                shu.addAll(appUpDateModelBase.getData().getTop_menu().getChosen());
                                FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.PathTitle, appUpDateModelBase.getData().getTop_menu());
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
    }

    private void setAdapter() {
        mHomeViewPager.setOffscreenPageLimit(3);
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
//        mFragmentList.add(new BookstoreFragment());
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
        mMeLayout = (LinearLayout) findViewById(R.id.book_reader_me_layout);
        mBookshelfLayout.setSelected(true);
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

    private void setSelectView(int position) {
        switch (position) {
            case BOOKSHELF_POSITION:
                mBookshelfLayout.setSelected(true);
                setSelectFalse(mDiscoverLayout, mBookstoreLayout, mMeLayout);
                break;
            case DISCOVER_POSITION:
                mDiscoverLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mBookstoreLayout, mMeLayout);
                break;
            case BOOKSTORE_POSITION:
                mBookstoreLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mMeLayout);
                break;
            case ME_POSITION:
                mMeLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mBookstoreLayout);
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
            mHomeViewPager.setCurrentItem(BOOKSHELF_POSITION);
            setSelectView(BOOKSHELF_POSITION);
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
