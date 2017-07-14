package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;

public class RegisterGetAccountActivity extends BaseActivity implements View.OnClickListener {
    ImageView ivBack;
    EditText etVerifacationCode, etNewPassword, etRePassword;
    TextView tvGetVerifacationCode;
    Button btSubmit;

    private String phoneNum;
    private Object verifacationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_get_account);
        phoneNum = getIntent().getStringExtra("phoneNum");
        initView();
        initEvent();
        startCountDown();
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
                tvGetVerifacationCode.setText(sec + " s");
                tvGetVerifacationCode.setSelected(false);//表示正在数数
                tvGetVerifacationCode.setTextColor(getResources().getColor(R.color.get_verification_code_color));
            }

            @Override
            public void onFinish() {
                tvGetVerifacationCode.setText(R.string.get);
                tvGetVerifacationCode.setClickable(true);
                tvGetVerifacationCode.setSelected(true);
                tvGetVerifacationCode.setTextColor(getResources().getColor(R.color.c01_themes_color));
            }
        };
        tvGetVerifacationCode.setClickable(false);
        timer.start();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.register2_back_imageview);
        etVerifacationCode = (EditText) findViewById(R.id.register2_verification_edittext);
        etNewPassword = (EditText) findViewById(R.id.register2_newpassword_edittext);
        etRePassword = (EditText) findViewById(R.id.register2_repassword_edittext);
        tvGetVerifacationCode = (TextView) findViewById(R.id.register2_getverification_textview);
        btSubmit = (Button) findViewById(R.id.register2_submit_button);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        tvGetVerifacationCode.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ivBack.getId() == v.getId()) {
            finish();
        } else if (tvGetVerifacationCode.getId() == v.getId()) {
            getVerifacationCode();
        } else if (btSubmit.getId() == v.getId()) {
            submit();
        }
    }

    private void submit() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            ViewUtils.toastShort(getString(R.string.not_available_network));
            return;
        }
        //1验证
        String npwd = etNewPassword.getText().toString();
        String rpwd = etRePassword.getText().toString();
        if (StringUtils.isBlank(npwd)) {
            ViewUtils.toastShort(getString(R.string.changepassword_pwd_is_blank));
            return;
        }
        if (!npwd.matches("^[a-zA-Z0-9]{6,12}$")) {
            ViewUtils.toastShort(getString(R.string.changepassword_pwd_is_error));
            return;
        }
        if (!npwd.equals(rpwd)) {
            ViewUtils.toastShort(getString(R.string.changepassword_twopwd_is_not_same));
            return;
        }
        String vcode = etVerifacationCode.getText().toString();
        if (StringUtils.isBlank(vcode)) {
            ViewUtils.toastShort(getString(R.string.register_virify_code_is_blank));
        }
        if (!vcode.matches("^\\d{6}$")) {
            ViewUtils.toastShort(getString(R.string.register_virify_code_is_error));
        }
        dialogLoading = ViewUtils.showProgressDialog(RegisterGetAccountActivity.this);
        //2发送
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phoneNum);
        map.put("u_pwd", npwd);
        map.put("u_pwd2", rpwd);
        map.put("u_vcode", vcode);
        map.put("u_ctype", "1");
        PostRequest request = new PostRequest(URLConstants.USER_REGISTER, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response==" + response.toString());
                dialogLoading.cancel();
                int errorid = ResponseHelper.getErrorId(response);

                if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_VERIFY.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_VERIFY.getMsg());
                    return;
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_REGISTER_PARAM.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_REGISTER_PARAM.getMsg());
                    return;
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_PHONE_NUM.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_PHONE_NUM.getMsg());
                    return;
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_PHONE_NUM.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_PHONE_NUM.getMsg());
                    return;
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_VERIFY_UNKNOWN.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_VERIFY_UNKNOWN.getMsg());
                    return;
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_VERIFY_INVALID.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_VERIFY_INVALID.getMsg());
                    return;
                } else if (errorid == XSNetEnum._VADATAERROR_REGISTER_PLEASE_INPUT_RIGHT_VERIFICATION_CODE.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._VADATAERROR_REGISTER_PLEASE_INPUT_RIGHT_VERIFICATION_CODE.getMsg());
                    return;
                } else if (!ResponseHelper.isSuccess(response)) {
                    ViewUtils.toastShort(getString(R.string.register_error));
                    return;
                }
                UMEventAnalyze.countEvent(RegisterGetAccountActivity.this, UMEventAnalyze.LOGIN_REGISTER_SEECCES);
                ViewUtils.toastShort(getString(R.string.register_sucess));
                startActivity(new Intent(RegisterGetAccountActivity.this, MainActivity.class));
                try {
                    JSONObject data = ResponseHelper.getVdata(response);
                    String cookie;
                    if (data.has("cookie_str")) {
                        cookie = data.getString("cookie_str");
                        SharePrefHelper.setCookie(cookie);
                    }
                    if (data.has("u_mid")) {
                        String uid = data.getString("u_mid");
                        User u = new User();
                        u.setUmid(uid);
                        UserHelper.getInstance().setUser(u);
                    }
                    SharePrefHelper.setCurLoginUserBookshelfUpdateTime(0);
                } catch (JSONException e) {
                    ViewUtils.toastShort(XSErrorEnum.ERRORCODE_ERROR_SERVER_DATA.getMsg() +
                            "(" + XSErrorEnum.ERRORCODE_ERROR_SERVER_DATA.getCode() + ")");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(XSErrorEnum.ERRORCODE_ERROR_NET.getMsg() +
                        "(" + XSErrorEnum.ERRORCODE_ERROR_NET.getCode() + ")");
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }


    public void getVerifacationCode() {
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGetVerifacationCode.setText("" + (int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvGetVerifacationCode.setText(getString(R.string.get));
                tvGetVerifacationCode.setClickable(true);
            }
        };
        tvGetVerifacationCode.setClickable(false);
        timer.start();
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phoneNum);
        map.put("u_usetype", "1");
        PostRequest request = new PostRequest(URLConstants.SEND_VERIFICATION_CODE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne==" + response.toString());
                if (!ResponseHelper.isSuccess(response)) {
                    int errorid = ResponseHelper.getErrorId(response);
                    ViewUtils.toastShort(getString(R.string.forgotpassword_senderror) + "(" + errorid + ")");
                    return;
                }
                ViewUtils.toastShort(getString(R.string.forgotpassword_sendsuccess));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getString(R.string.network_server_error));
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }
}
