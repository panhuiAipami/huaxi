package com.spriteapp.booklibrary.ui.activity;

import android.view.View;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;


/**
 * 充值
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public int getLayoutResId() throws Exception {
        return R.layout.activity_recharge;
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void configViews() throws Exception {

    }

    public void findViewId() throws Exception {
        findViewById(R.id.image_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.image_back) {
            finish();
        }
    }
}
