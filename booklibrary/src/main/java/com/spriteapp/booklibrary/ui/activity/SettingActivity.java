package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.AutoSubEnum;
import com.spriteapp.booklibrary.enumeration.PageStyleEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.ui.presenter.LoginOutPresenter;
import com.spriteapp.booklibrary.ui.view.LoginOutView;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideCacheUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by kuangxiaoguo on 2017/7/18.
 */

public class SettingActivity extends TitleActivity implements LoginOutView {

    private static final String TAG = "SettingActivity";
    private RelativeLayout mClearCacheLayout;
    private TextView mCacheTextView;
    private Switch mAutoBuySwitch;
    private Switch mPageModeSwitch;
    private Button mLoginOutButton;
    private LoginOutPresenter mLoginOutPresenter;

    @Override
    public void initData() {
        setTitle("设置");
        getCacheSize();
        mLoginOutPresenter = new LoginOutPresenter();
        mLoginOutPresenter.attachView(this);
        mLoginOutButton.setVisibility(AppUtil.isLogin() ? View.VISIBLE : View.GONE);
    }

    private void getCacheSize() {
        String cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
        mCacheTextView.setText(cacheSize);
    }

    @Override
    public void configViews() {
        super.configViews();
        boolean isAutoSub = SharedPreferencesUtil.getInstance()
                .getInt(Constant.IS_BOOK_AUTO_SUB) == AutoSubEnum.AUTO_SUB.getValue();
        boolean pageStyle = SharedPreferencesUtil.getInstance()
                .getInt(Constant.PAGE_CHANGE_STYLE) == PageStyleEnum.FLIP_STYLE.getValue();
        mAutoBuySwitch.setChecked(isAutoSub);
        mPageModeSwitch.setChecked(pageStyle);
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_setting, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() {
        super.findViewId();
        mClearCacheLayout = (RelativeLayout) findViewById(R.id.book_reader_clear_cache_layout);
        mCacheTextView = (TextView) findViewById(R.id.book_reader_cache_text_view);
        mAutoBuySwitch = (Switch) findViewById(R.id.book_reader_auto_buy_switch);
        mPageModeSwitch = (Switch) findViewById(R.id.book_reader_page_mode_switch);
        mLoginOutButton = (Button) findViewById(R.id.book_reader_login_out_button);
        setListener();
    }

    private void setListener() {
        mClearCacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlideCacheUtil.getInstance().clearImageAllCache(SettingActivity.this);
                ToastUtil.showSingleToast("清理缓存成功");
                mCacheTextView.setVisibility(View.GONE);
            }
        });
        mAutoBuySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putInt(Constant.IS_BOOK_AUTO_SUB,
                        isChecked ? AutoSubEnum.AUTO_SUB.getValue()
                                : AutoSubEnum.NOT_AUTO_SUB.getValue());
            }
        });
        mPageModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putInt(Constant.PAGE_CHANGE_STYLE,
                        isChecked ? PageStyleEnum.FLIP_STYLE.getValue()
                                : PageStyleEnum.DEFAULT_STYLE.getValue());
            }
        });
        mLoginOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginOutDialog();
            }
        });
    }

    private void showLoginOutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.book_reader_prompt))
                .setMessage(getResources().getString(R.string.book_reader_sure_to_login_out))
                .setNegativeButton(getResources().getString(R.string.book_reader_cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.book_reader_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLoginOutPresenter.loginOut();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void setData(Base<Void> result) {
        AppUtil.loginOut();
        finish();
    }

    @Override
    public void showNetWorkProgress() {
        showDialog();
    }

    @Override
    public void disMissProgress() {
        dismissDialog();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginOutPresenter != null) {
            mLoginOutPresenter.detachView();
        }
    }
}
