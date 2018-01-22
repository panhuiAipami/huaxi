package com.spriteapp.booklibrary.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.widget.loading.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment{

    private static final String TAG = "BaseFragment";
    protected View mParentView;
    protected FragmentActivity mActivity;
    protected Context mContext;
    protected boolean isVisible;
    protected boolean mHasLoadedOnce = false;
    protected boolean isPrepared = false;
    private CustomDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mParentView = inflater.inflate(getLayoutResId(), container, false);
        mContext = getContext();
        isPrepared = true;
        return mParentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewId();
        lazyLoad();
        configViews();
    }

    public abstract int getLayoutResId();

    public abstract void initData();

    public abstract void configViews();

    public abstract void findViewId();

    protected abstract void lazyLoad();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = getUserVisibleHint();
        if (getUserVisibleHint()) {
            lazyLoad();
        }
    }

    public void showDialog() {
        if (dialog == null) {
            dialog = CustomDialog.instance(getActivity());
            dialog.setCancelable(true);
        }
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getActivity().getLocalClassName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getActivity().getLocalClassName());
    }
}
