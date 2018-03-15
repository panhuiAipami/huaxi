package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by userfirst on 2018/3/8.
 */

public class InvitationCodeDialog extends BaseDialog {
    private Activity context;
    private ImageView colse_img, remind;
    private RelativeLayout code_layout;
    private EditText edit_code;
    private TextView finish_text;
    private String code_num;

    public InvitationCodeDialog(final Activity context) {
        this.context = context;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init() throws Exception {
        initDialog(context, null, R.layout.invitation_code_layout, TYPE_CENTER, true);
        code_layout = (RelativeLayout) mDialog.findViewById(R.id.code_layout);
        colse_img = (ImageView) mDialog.findViewById(R.id.colse_img);
        edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
        finish_text = (TextView) mDialog.findViewById(R.id.finish_text);
        remind = (ImageView) mDialog.findViewById(R.id.remind);
//        ViewGroup.LayoutParams layoutParams = code_layout.getLayoutParams();
//        layoutParams.height = (int) (layoutParams.width * 1.3);
//        code_layout.setLayoutParams(layoutParams);
//        Log.d("InvitationCodeDialog", "height===" + layoutParams.width * 1.3 + "width===" + layoutParams.width);
        initListener();
        mDialog.show();
    }

    public void initListener() throws Exception {
        colse_img.setOnClickListener(clickListener);
        finish_text.setOnClickListener(clickListener);
        remind.setOnClickListener(clickListener);
        edit_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code_num = edit_code.getText().toString().trim();
                if (TextUtils.isEmpty(code_num)) {
                    finish_text.setEnabled(false);
                } else {
                    finish_text.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == colse_img) {
                dismiss();
            } else if (v == finish_text) {
                if (!TextUtils.isEmpty(code_num)) {
                }
                sendCode();
            } else if (remind == remind) {
                Log.d("remind", "remind");
            }
        }
    };

    public void sendCode() {//发送邀请码
        try {
            if (AppUtil.isNetAvailable(context)) {
                BookApi.getInstance()
                        .service
                        .invite_activate(code_num)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseTwo>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull BaseTwo bookStoreResponse) {
                                if (bookStoreResponse != null) {
                                    int resultCode = bookStoreResponse.getCode();
                                    if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                        if (!TextUtils.isEmpty(bookStoreResponse.getMessage())) {
                                            ToastUtil.showToast(bookStoreResponse.getMessage());
                                            dismiss();//成功之后dismiss掉dialog
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }
}
