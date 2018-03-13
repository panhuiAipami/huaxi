package com.spriteapp.booklibrary.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 绑定手机号activity
 */
public class BindPhoneActivity extends TitleActivity {
    private final int phone_length = 11;
    private EditText phone_edit, code_edit;
    private TextView bind_phone, get_code;
    private String phone = null;
    private String code = null;
    private int count_down = 59;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (count_down == 0) {
                        get_code.setEnabled(true);
                        get_code.setText("重新获取");
                        count_down = 59;
                    } else if (count_down > 0) {
                        count_down--;
                        get_code.setText(count_down + "s");
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
            }

        }
    };


    @Override
    public void initData() throws Exception {
        setTitle("绑定手机号");
        listener();

    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        phone_edit = (EditText) findViewById(R.id.phone_edit);
        code_edit = (EditText) findViewById(R.id.code_edit);
        bind_phone = (TextView) findViewById(R.id.bind_phone);
        get_code = (TextView) findViewById(R.id.get_code);
    }

    public void listener() {
        bind_phone.setOnClickListener(this);
        get_code.setOnClickListener(this);
        phone_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone_edit.getText().toString().trim().length() == phone_length) {
                    phone = phone_edit.getText().toString().trim();
                    get_code.setEnabled(true);
                } else {
                    phone = null;
                    get_code.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(code_edit.getText().toString().trim())) {
                    code = code_edit.getText().toString().trim();
                    bind_phone.setEnabled(true);
                } else {
                    code = null;
                    bind_phone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_bind_phone, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == get_code) {//获取验证码
            getCode("", "captcha");


        } else if (v == bind_phone) {//完成绑定
            if (!TextUtils.isEmpty(phone) && phone.length() == phone_length) {
                getCode(code, "activate");
            } else {
                ToastUtil.showToast("请输入正确的手机号");
            }

        }
    }

    /**
     * 得到验证码或者完成绑定
     */
    public void getCode(final String captcha, final String u_action) {
        if (!NetworkUtil.isAvailable(this)) return;
        if (TextUtils.isEmpty(phone)) return;
        BookApi.getInstance()
                .service
                .user_mobile(phone, captcha, u_action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (u_action.equals("captcha")) {
                                    get_code.setEnabled(false);
                                    handler.sendEmptyMessage(0);
                                }
                                if (!TextUtils.isEmpty(bookStoreResponse.getMessage())) {
                                    ToastUtil.showToast(bookStoreResponse.getMessage());
                                    if (u_action.equals("activate"))
                                        finish();
                                }

                            } else {
                                if (!TextUtils.isEmpty(bookStoreResponse.getMessage())) {
                                    ToastUtil.showToast(bookStoreResponse.getMessage());
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
