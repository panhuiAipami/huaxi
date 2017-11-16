package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.ui.fragment.BookshelfFragment;
import com.spriteapp.booklibrary.ui.fragment.BookstoreFragment;
import com.spriteapp.booklibrary.ui.fragment.DiscoverFragment;
import com.spriteapp.booklibrary.ui.fragment.MeFragment;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class HomeActivity extends TitleActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
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

    @Override
    public void initData() {
        mContext = this;
        setTitle("书架");
        addFreeTextView();
        initFragment();
        setAdapter();
        setListener();
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
        mFragmentList.add(new BookshelfFragment());
//        mFragmentList.add(new NativeBookStoreFragment());
        mFragmentList.add(new DiscoverFragment());
        mFragmentList.add(new BookstoreFragment());
        mFragmentList.add(new MeFragment());
    }

    @Override
    public void configViews() {
        super.configViews();
        gone(mBackImageView);
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
                    addFreeTextView();
                    setTitle(R.string.book_reader_bookshelf);
                    setSelectView(BOOKSHELF_POSITION);
                    visible(mTitleLayout);
                    setViewpagerTopMargin(TOP_BAR_HEIGHT);
                    break;
                case DISCOVER_POSITION:
                    mRightLayout.removeAllViews();
                    setTitle(R.string.book_reader_discover);
                    setSelectView(DISCOVER_POSITION);
                    visible(mTitleLayout);
                    setViewpagerTopMargin(TOP_BAR_HEIGHT);
                    break;
                case BOOKSTORE_POSITION:
                    setTitle(R.string.book_reader_bookstore);
                    addSearchView();
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
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    break;
            }
        }
    }
}
