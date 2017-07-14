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

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;

public class BandPhoneBandActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private TextView tvGetVerification, tvTitle;
    private EditText etVerification;
    private Button btSubmit;

    private String messageToken;
    private String phoneNum;
    private String usetype;//5绑定，6解绑
    private TextView tvErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_phone_band);
        messageToken = getIntent().getStringExtra("token");
        phoneNum = getIntent().getStringExtra("phoneNum");
        usetype = getIntent().getStringExtra("usetype");
        initView();
        initEvent();
        if ("5".equals(usetype)) {
            tvTitle.setText(R.string.band_bandphone);
        } else {
            tvTitle.setText(R.string.band_unbandphone);
        }
        startCountDown();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.band_phone2_back_imageview);
        tvGetVerification = (TextView) findViewById(R.id.band_phone2_getverification_textview);
        btSubmit = (Button) findViewById(R.id.band_phone2_submit_button);
        etVerification = (EditText) findViewById(R.id.band_phone2_verification_edittext);
        tvTitle = (TextView) findViewById(R.id.band_phone2_title_textview);
        tvErr = (TextView) findViewById(R.id.login_username_err);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        etVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvErr.setVisibility(View.INVISIBLE);
                etVerification.setTextColor(getResources().getColor(R.color.c05_themes_color));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (ivBack.getId() == v.getId()) {
            finish();
        } else if (tvGetVerification.getId() == v.getId()) {
            sendVerification();
        } else if (btSubmit.getId() == v.getId()) {
            verifyCode();
        }
    }

    private void verifyCode() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            tvErr.setText(getString(R.string.not_available_network));
            tvErr.setVisibility(View.VISIBLE);
            return;
        }
        btSubmit.setClickable(false);
        final String verificationcode = etVerification.getText().toString();
        if (StringUtils.isBlank(verificationcode)) {
            tvErr.setText(getString(R.string.register_virify_code_is_blank));
            tvErr.setVisibility(View.VISIBLE);
            btSubmit.setClickable(true);
            return;
        } else if (!verificationcode.matches("^\\d{6}$")) {
            etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
            tvErr.setText(getString(R.string.register_virify_code_is_error));
            tvErr.setVisibility(View.VISIBLE);
            btSubmit.setClickable(true);
            return;
        }
        dialogLoading = ViewUtils.showProgressDialog(BandPhoneBandActivity.this);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_action", (usetype != null && usetype.equals("5")) ? "bind" : "unbind");
        map.put("u_btype", "2");
        map.put("u_name", phoneNum);
        map.put("u_vcode", verificationcode);
        PostRequest request = new PostRequest(URLConstants.BIND_USER, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogLoading.cancel();
                if (!ResponseHelper.isSuccess(response)) {
                    int errorid = ResponseHelper.getErrorId(response);
                    if (XSNetEnum._VDATAERRORCODE_ERROR_BIND_INFO_REPEAT.getCode() == errorid) {
                        tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_BIND_INFO_REPEAT.getMsg());
                        tvErr.setVisibility(View.VISIBLE);
                        btSubmit.setClickable(true);
                        return;
                    }
                    if (XSNetEnum._VDATAERRORCODE_ERROR_VERIFY.getCode() == errorid) {
                        etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
                        tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_VERIFY.getMsg());
                        tvErr.setVisibility(View.VISIBLE);
                        btSubmit.setClickable(true);
                        return;
                    }
                    if (usetype != null && usetype.equals("5")) {
                        tvErr.setText(getString(R.string.band_banderror));
                        tvErr.setVisibility(View.VISIBLE);
                    } else {
                        tvErr.setText(getString(R.string.band_unbanderror));
                        tvErr.setVisibility(View.VISIBLE);
                    }
                    btSubmit.setClickable(true);
                    return;
                }

                if (usetype != null && usetype.equals("5")) {
                    ViewUtils.toastShort(getString(R.string.band_bandsuccess));
                } else {
                    ViewUtils.toastShort(getString(R.string.band_unbandsuccess));
                }
                btSubmit.setClickable(true);
                startActivity(new Intent(BandPhoneBandActivity.this, MyInfoActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvErr.setText(getString(R.string.network_server_error));
                tvErr.setVisibility(View.VISIBLE);
                btSubmit.setClickable(true);
            }
        }, map);

        RequestQueueManager.addRequest(request);
    }

    private void sendVerification() {
        startCountDown();
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phoneNum);
        map.put("u_usetype", usetype);
        PostRequest request = new PostRequest(URLConstants.SEND_VERIFICATION_CODE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne==" + response.toString());
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_CODE.getCode() || XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_ACTIVE.getCode() == errorid || XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getCode() == errorid) {
                    tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getMsg());
                    tvErr.setVisibility(View.VISIBLE);
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    tvErr.setText(getString(R.string.network_server_error) + "(" + errorid + ")");
                    tvErr.setVisibility(View.VISIBLE);
                    return;
                }
                ViewUtils.toastShort(getString(R.string.forgotpassword_sendsuccess));
                messageToken = ResponseHelper.getVdata(response).optString("token");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvErr.setText(getString(R.string.network_server_error));
                tvErr.setVisibility(View.VISIBLE);
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
}
