package com.spriteapp.booklibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.util.StatusBarUtil;
import com.spriteapp.booklibrary.widget.loading.CustomDialog;


public abstract class BaseActivity extends AppCompatActivity {

    private CustomDialog dialog;//进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        setStatusBarColor();
        findViewId();
        initData();
        configViews();
    }

    private void setStatusBarColor() {
        HuaXiConfig config = HuaXiSDK.getInstance().getConfig();
        int statusBarColor = config.getStatusBarColor();
        if (statusBarColor == 0) {
            statusBarColor = getResources().getColor(R.color.book_reader_black);
        }
        StatusBarUtil.setWindowStatusBarColor(this, statusBarColor);
    }

    public abstract int getLayoutResId();

    public abstract void initData();

    public abstract void configViews();

    public abstract void findViewId();

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void setSelectFalse(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setSelected(false);
                }
            }
        }
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HuaXiSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    public void showDialog() {
        if (dialog == null) {
            dialog = CustomDialog.instance(this);
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
}
