package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;

public class ChangePasswordModifyActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack;
    private EditText etNewPassword, etRePassword, etVerificationCode;
    private Button btSubmit;
    private TextView tvGetVerification;

    private String phonenum;
    private String imageVerificationToken;//验证图片验证码返回的token
    private String verificationCodeToken;//验证码token
    private String usertype;
    private TextView mTvCodeErr;
    private TextView mTvPasswordErr;
    private TextView mTvPasswordAgainErr;
    private ImageView mIvPasswordClear;
    private ImageView mIvPasswordAgainClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_modify);
        phonenum = getIntent().getStringExtra("phonenum");
        imageVerificationToken = getIntent().getStringExtra("token");
        usertype = getIntent().getStringExtra("usertype");
        initView();
        initEvent();
        startCountDown();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
        ivBack = (ImageView) findViewById(R.id.change_password2_back_imageview);
        etNewPassword = (EditText) findViewById(R.id.change_password2_newpassword_edittext);
        etRePassword = (EditText) findViewById(R.id.change_password2_repassword_edittext);
        etVerificationCode = (EditText) findViewById(R.id.change_password2_verification_edittext);
        btSubmit = (Button) findViewById(R.id.change_password2_changepassword_button);
        tvGetVerification = (TextView) findViewById(R.id.change_password2_getverification_textview);
        mTvCodeErr = (TextView) findViewById(R.id.tv_change_code_err);
        mTvPasswordErr = (TextView) findViewById(R.id.tv_change_password_err);
        mTvPasswordAgainErr = (TextView) findViewById(R.id.tv_change_password_again_err);
        mIvPasswordClear = (ImageView) findViewById(R.id.iv_change_password_clear);
        mIvPasswordAgainClear = (ImageView) findViewById(R.id.iv_change_password_again_clear);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        mIvPasswordAgainClear.setOnClickListener(this);
        mIvPasswordClear.setOnClickListener(this);
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvStatusChangele();
                if (s.length() > 0) {
                    mIvPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPasswordClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        etRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvStatusChangele();
                if (s.length() > 0) {
                    mIvPasswordAgainClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPasswordAgainClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        etVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvStatusChangele();
            }
        });

        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
                if (length > 0 && hasFocus) {
                    mIvPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPasswordClear.setVisibility(View.INVISIBLE);
                }
            }
        });

        etRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
                if (length > 0 && hasFocus) {
                    mIvPasswordAgainClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPasswordAgainClear.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivBack.getId()) {
            finish();
        } else if (v.getId() == tvGetVerification.getId()) {
            sendPhoneVerification();
        } else if (v.getId() == btSubmit.getId()) {
            validateCode();
        }

        switch (v.getId()) {
            case R.id.iv_change_password_clear:
                etNewPassword.setText("");
                break;
            case R.id.iv_change_password_again_clear:
                etRePassword.setText("");
                break;

            default:
                break;
        }
    }

    //验证验证码
    private void validateCode() {
        String npwd = etNewPassword.getText().toString();
        String rpwd = etRePassword.getText().toString();
        final String verificationcode = etVerificationCode.getText().toString();

        if (!verificationcode.matches("^\\d{6}$")) {
            if (StringUtils.isBlank(verificationcode)) {
                mTvCodeErr.setText(getString(R.string.changepassword_verification_null));
            } else {
                mTvCodeErr.setText(R.string.changepassword_verification_err);
            }
            mTvCodeErr.setVisibility(View.VISIBLE);
            etVerificationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
            return;
        }
        if (StringUtils.isBlank(npwd)) {
            mTvPasswordErr.setText(getString(R.string.changepassword_pwd_is_blank));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.matches("^[a-zA-Z0-9]{6,12}$")) {
            mTvPasswordErr.setText(getString(R.string.changepassword_pwd_is_error));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.equals(rpwd)) {
            if (StringUtils.isBlank(rpwd)) {
                mTvPasswordAgainErr.setText(getString(R.string.changepassword_twopwd_is_null));
            } else {
                mTvPasswordAgainErr.setText(getString(R.string.changepassword_twopwd_is_not_same));
            }
            mTvPasswordAgainErr.setVisibility(View.VISIBLE);
            return;
        }


        dialogLoading = ViewUtils.showProgressDialog(ChangePasswordModifyActivity.this);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_usertype", usertype);
        map.put("u_vcode", verificationcode);
        map.put("u_action", "reset");
        map.put("u_step", "modifyverify");
        PostRequest request = new PostRequest(URLConstants.RESET_PASSWORD, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response==" + response.toString());
                dialogLoading.cancel();
                if (!ResponseHelper.isSuccess(response)) {
                    int errorId = ResponseHelper.getErrorId(response);
                    String errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_SMSCODE.getMsg();
                    if (XSNetEnum._VDATAERROR_MODPASSWORD_NOT_LOGIN.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_NOT_LOGIN.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_SMSCODE_NOT_MATCH.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_SMSCODE_NOT_MATCH.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_PARAMS_NOT_MATCH.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_PARAMS_NOT_MATCH.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_ACCOUNT_NOT_EXIST.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_ACCOUNT_NOT_EXIST.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_GET_USERINFO_FAILED.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_GET_USERINFO_FAILED.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_PHONE_NOT_EXIST.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_PHONE_NOT_EXIST.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_USER_TYPE.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_USER_TYPE.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_SMSCODE.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_SMSCODE.getMsg();
                    }
                    mTvCodeErr.setText(errorMsg);
                    mTvCodeErr.setVisibility(View.VISIBLE);
                    return;
                }
                verificationCodeToken = ResponseHelper.getVdata(response).optString("token");
                if (verificationcode != null) {
                    ViewUtils.toastShort(getString(R.string.code_format_success));
                    modifyPassword();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTvCodeErr.setText(getString(R.string.network_server_error));
                mTvCodeErr.setVisibility(View.VISIBLE);
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    private void modifyPassword() {
        String npwd = etNewPassword.getText().toString();
        String rpwd = etRePassword.getText().toString();
        if (StringUtils.isBlank(npwd)) {
            mTvPasswordErr.setText(getString(R.string.changepassword_pwd_is_blank));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.matches("^[a-zA-Z0-9]{6,12}$")) {
            mTvPasswordErr.setText(getString(R.string.changepassword_pwd_is_blank));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.equals(rpwd)) {
            mTvPasswordAgainErr.setText(getString(R.string.changepassword_pwd_is_blank));
            mTvPasswordAgainErr.setVisibility(View.VISIBLE);
            return;
        }

        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_s", verificationCodeToken);
        map.put("u_newpwd", npwd);
        map.put("u_newpwd2", rpwd);
        map.put("u_action", "reset");
        map.put("u_step", "modify");
        map.put("u_usertype", usertype);
        PostRequest request = new PostRequest(URLConstants.RESET_PASSWORD, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (ResponseHelper.isSuccess(response)) {
                    LogUtils.debug("resposne==" + response.toString());
                    ViewUtils.toastShort(getString(R.string.changepassword_changesuccess));
                    Intent intent = new Intent(ChangePasswordModifyActivity.this, MyInfoActivity.class);
                    startActivity(intent);
                } else {
                    int errorId = ResponseHelper.getErrorId(response);
                    String errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_FAILED.getMsg();
                    if (XSNetEnum._VDATAERROR_MODPASSWORD_NOT_LOGIN.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_NOT_LOGIN.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_SMSCODE_NOT_MATCH.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_SMSCODE_NOT_MATCH.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_PARAMS_NOT_MATCH_2.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_PARAMS_NOT_MATCH_2.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_ACCOUNT_NOT_EXIST.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_ACCOUNT_NOT_EXIST.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_GET_USERINFO_FAILED.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_GET_USERINFO_FAILED.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_FAILED.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_FAILED.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_GET_USERINFO_FAILED_2.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_GET_USERINFO_FAILED_2.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_PHONE_NOT_EXIST.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_PHONE_NOT_EXIST.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_USER_TYPE.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_USER_TYPE.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_FAILED_2.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_FAILED_2.getMsg();
                    } else if (XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_SMSCODE_2.getCode() == errorId) {
                        errorMsg = XSNetEnum._VDATAERROR_MODPASSWORD_INVALID_SMSCODE_2.getMsg();
                    }
                    toast(errorMsg);
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    private void sendPhoneVerification() {
        startCountDown();
        //2发送验证码
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phonenum);
        map.put("u_usetype", "7");
        PostRequest request = new PostRequest(URLConstants.SEND_VERIFICATION_CODE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_CODE.getCode() || XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_ACTIVE.getCode() == errorid || XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getCode() == errorid) {
                    mTvCodeErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getMsg());
                    mTvCodeErr.setVisibility(View.VISIBLE);
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    return;
                }
                ViewUtils.toastShort(getString(R.string.forgotpassword_sendsuccess));
                imageVerificationToken = ResponseHelper.getVdata(response).optString("token");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTvCodeErr.setText(getString(R.string.network_server_error));
                mTvCodeErr.setVisibility(View.VISIBLE);
            }
        }, map);
        RequestQueueManager.addRequest(request);

    }

    CountDownTimer timer;

    public void startCountDown() {
        if (timer != null) {
            timer.onFinish();
        }
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int) millisUntilFinished / 1000;
                tvGetVerification.setText(sec + " s");
                tvGetVerification.setSelected(false);//表示正在数数
                tvGetVerification.setTextColor(getResources().getColor(R.color.get_verification_code_color));
            }

            @Override
            public void onFinish() {
                tvGetVerification.setText(getString(R.string.get));
                tvGetVerification.setClickable(true);
                tvGetVerification.setSelected(true);
                tvGetVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
            }
        };
        tvGetVerification.setClickable(false);
        timer.start();
    }


    private void tvStatusChangele() {
        etVerificationCode.setTextColor(getResources().getColor(R.color.c05_themes_color));
        etNewPassword.setTextColor(getResources().getColor(R.color.c05_themes_color));
        etRePassword.setTextColor(getResources().getColor(R.color.c05_themes_color));
        mTvCodeErr.setVisibility(View.INVISIBLE);
        mTvPasswordErr.setVisibility(View.INVISIBLE);
        mTvPasswordAgainErr.setVisibility(View.INVISIBLE);
    }
}
