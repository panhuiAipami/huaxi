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
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.ResponseHelper;

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.https.PostRequest;

/**
 * 通过手机号修改新密码
 */
public class ForgotPasswordPhoneGetActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack;
    private EditText etNewPassword, etRePassword, etVerificationCode;
    private Button btSubmit;
    private TextView tvGetVerification;

    private String phonenum;
    private String imageVerificationToken;//验证图片验证码返回的touken
    private String verificationCodeToken;//验证码token
    private String usertype;
    private String genenry;
    private String verificationCode;
    private TextView mTvCodeErr;
    private TextView mTvPasswordErr;
    private TextView mTvPasswordAgainErr;
    private ImageView mIvPasswordClear;
    private ImageView mIvPasswordAgainClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_get);
        phonenum = getIntent().getStringExtra("phonenum");
        imageVerificationToken = getIntent().getStringExtra("token");
        usertype = getIntent().getStringExtra("usertype");
        genenry = getIntent().getStringExtra("genenry");
        verificationCode = getIntent().getStringExtra("verificationCode");
        intiView();
        initEvent();
        startCountDown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btSubmit.setEnabled(true);
        tvGetVerification.setSelected(true);
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
                tvGetVerification.setText(R.string.get);
                tvGetVerification.setClickable(true);
                tvGetVerification.setSelected(true);
                tvGetVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
            }
        };
        tvGetVerification.setClickable(false);
        timer.start();
    }

    private void intiView() {
        ivBack = (ImageView) findViewById(R.id.forgot_password2_back_imageview);
        etNewPassword = (EditText) findViewById(R.id.forgot_password2_newpassword_edittext);
        etRePassword = (EditText) findViewById(R.id.forgot_password2_repassword_edittext);
        btSubmit = (Button) findViewById(R.id.forgot_password2_changepassword_button);
        etVerificationCode = (EditText) findViewById(R.id.forgot_password2_verification_edittext);
        tvGetVerification = (TextView) findViewById(R.id.forgot_password2_getverification_textview);
        mTvCodeErr = (TextView) findViewById(R.id.tv_change_code_err);
        mTvPasswordErr = (TextView) findViewById(R.id.tv_change_password_err);
        mTvPasswordAgainErr = (TextView) findViewById(R.id.tv_change_password_again_err);
        mIvPasswordClear = (ImageView) findViewById(R.id.iv_change_password_clear);
        mIvPasswordAgainClear = (ImageView) findViewById(R.id.iv_change_password_again_clear);

    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
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

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_change_password_clear:
                etNewPassword.setText("");
                break;
            case R.id.iv_change_password_again_clear:
                etRePassword.setText("");
                break;
            case R.id.forgot_password2_back_imageview:
                finish();
                break;
            case R.id.forgot_password2_changepassword_button:
                verifycode();
                break;
            case R.id.forgot_password2_getverification_textview:
                sendVerification();
                break;
            default:
                break;
        }
    }

    private void sendVerification() {
        startCountDown();
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phonenum);
        map.put("u_action", "reset");
        map.put("u_step", "apply");
        map.put("u_vcode", verificationCode);
        map.put("u_genenry", genenry);
        map.put("u_usertype", "2");
        PostRequest request = new PostRequest(URLConstants.RESET_PASSWORD, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response==" + response.toString());
                if (!ResponseHelper.isSuccess(response)) {
                    int errorid = ResponseHelper.getErrorId(response);
                    ViewUtils.toastShort(getString(R.string.forgot_send_fali) + "(" + errorid + ")");
                    return;
                }
                imageVerificationToken = ResponseHelper.getVdata(response).optString("token");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getString(R.string.network_server_error));
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    private void verifycode() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            ViewUtils.toastShort(getString(R.string.not_available_network));
            return;
        }
        final String verificationcode = etVerificationCode.getText().toString();
        if (!verificationcode.matches("^\\d{6}$")) {
            if (StringUtils.isBlank(verificationcode)) {
                mTvCodeErr.setText(getString(R.string.code_format_null));
            } else {
                mTvCodeErr.setText(getString(R.string.code_format_error));
            }
            mTvCodeErr.setVisibility(View.VISIBLE);
            etVerificationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
            return;
        }

        String npwd = etNewPassword.getText().toString();
        String rpwd = etRePassword.getText().toString();

        if (StringUtils.isBlank(npwd)) {
            mTvPasswordErr.setText(getString(R.string.forgot_password_can_not_empty));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.matches("^[a-zA-Z0-9]{6,12}$")) {
            mTvPasswordErr.setText(getString(R.string.changepassword_pwd_is_error));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.equals(rpwd)) {
            if ("".equals(rpwd)) {
                mTvPasswordAgainErr.setText(getString(R.string.forgot_password_null));
            } else {
                mTvPasswordAgainErr.setText(getString(R.string.forgot_password_not_same));
            }
            mTvPasswordAgainErr.setVisibility(View.VISIBLE);
            return;
        }

        dialogLoading = ViewUtils.showProgressDialog(ForgotPasswordPhoneGetActivity.this);
        btSubmit.setEnabled(false);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phonenum);
        map.put("u_usertype", usertype);
        map.put("u_vcode", verificationcode);
        map.put("u_s", imageVerificationToken);
        map.put("u_action", "reset");
        map.put("u_step", "verify");
        PostRequest request = new PostRequest(URLConstants.RESET_PASSWORD, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogLoading.cancel();
                LogUtils.debug("response==" + response.toString());
                btSubmit.setEnabled(true);
                int errorid = ResponseHelper.getErrorId(response);
                String msg = "";
                if (!ResponseHelper.isSuccess(response)) {
                    if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_PARAMS_NOT_MATCH.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_PARAMS_NOT_MATCH.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_NOT_EXSIT.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_NOT_EXSIT.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_NOT_RESPONSE.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_NOT_RESPONSE.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_NOT_USER_DETAIL.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_NOT_USER_DETAIL.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_USERNAME_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_USERNAME_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_USERTYPE_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_USERTYPE_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_VERIFICODE_TIMEOUT.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_VERIFICODE_TIMEOUT.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                        etVerificationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD2_VERIFICODE_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD2_VERIFICODE_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                        etVerificationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    }

                    return;
                }
                verificationCodeToken = ResponseHelper.getVdata(response).optString("token");
                if (verificationcode != null) {
                    ViewUtils.toastShort(getString(R.string.code_format_success));
                    changePassword();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getString(R.string.network_server_error));
                btSubmit.setEnabled(true);
            }
        }, map);
        RequestQueueManager.addRequest(request);

    }

    private void changePassword() {
        String npwd = etNewPassword.getText().toString();
        String rpwd = etRePassword.getText().toString();

        if (StringUtils.isBlank(npwd)) {
            mTvPasswordErr.setText(getString(R.string.forgot_password_can_not_empty));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.matches("^\\S{6,16}$")) {
            mTvPasswordErr.setText(getString(R.string.changepassword_pwd_is_error));
            mTvPasswordErr.setVisibility(View.VISIBLE);
            return;
        }
        if (!npwd.equals(rpwd)) {
            mTvPasswordAgainErr.setText(getString(R.string.forgot_password_not_same));
            mTvPasswordAgainErr.setVisibility(View.VISIBLE);
            return;
        }
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phonenum);
        map.put("u_s", verificationCodeToken);
        map.put("u_newpwd", npwd);
        map.put("u_newpwd2", rpwd);
        map.put("u_action", "reset");
        map.put("u_step", "execute");
        map.put("u_usertype", "2");
        PostRequest request = new PostRequest(URLConstants.RESET_PASSWORD, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogLoading.cancel();
                if (ResponseHelper.isSuccess(response)) {
                    LogUtils.debug("resposne==" + response.toString());
                    ViewUtils.toastShort(getString(R.string.forgotpassword_changesuccess));
                    Intent intent = new Intent(ForgotPasswordPhoneGetActivity.this, LoginActivity
                            .class);
                    startActivity(intent);
                } else {
                    int errorid = ResponseHelper.getErrorId(response);
                    String msg = "";
                    if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PARAMS_NOT_MATCH.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PARAMS_NOT_MATCH.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_NOT_REGISTER.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_NOT_REGISTER.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_APPLY_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_APPLY_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_NOT_USER_DETAIL.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_NOT_USER_DETAIL.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_USERNAME_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_USERNAME_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_USERTYPE_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_USERTYPE_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PASSWORD_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PASSWORD_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PLEASE_INPUT_SMAME_PASSWORD.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PLEASE_INPUT_SMAME_PASSWORD.getMsg();
                        mTvPasswordErr.setText(msg);
                        mTvPasswordErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_VERIFICODE_TIMEOUT.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_VERIFICODE_TIMEOUT.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                        etVerificationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PLEASE_INPUT_RIGHT_VERIFICATION_CODE.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD3_PLEASE_INPUT_RIGHT_VERIFICATION_CODE.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                        etVerificationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getString(R.string.not_available_network));
            }
        }, map);
        RequestQueueManager.addRequest(request);
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
