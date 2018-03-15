package com.spriteapp.booklibrary.ui.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

public class MangerAlipayActivity extends TitleActivity {
    public static final String ALIPAY_ACCOUNT = "alipay_account";
    public static final String ALIPAY_NAME = "alipay_name";
    private EditText edit_account, edit_name;
    private TextView tixian_zhifubao;


    @Override
    public void initData() throws Exception {
        setTitle("管理支付宝");
        listener();

    }


    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_manger_alipay, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_name = (EditText) findViewById(R.id.edit_name);
        tixian_zhifubao = (TextView) findViewById(R.id.tixian_zhifubao);
    }

    private void listener() {
        tixian_zhifubao.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == tixian_zhifubao) {
            if (TextUtils.isEmpty(edit_account.getText().toString().trim())) {
                ToastUtil.showToast("请输入支付宝账号");
            } else if (TextUtils.isEmpty(edit_name.getText().toString().trim())) {
                ToastUtil.showToast("请输入支付宝姓名");
            } else {
                SharedPreferencesUtil.getInstance().putString(ALIPAY_ACCOUNT, edit_account.getText().toString().trim());
                SharedPreferencesUtil.getInstance().putString(ALIPAY_NAME, edit_name.getText().toString().trim());
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
