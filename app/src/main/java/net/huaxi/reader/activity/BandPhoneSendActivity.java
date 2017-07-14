package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
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

public class BandPhoneSendActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private EditText etPhone;
    private Button btSubmit;
    private TextView tvTitle;
    String usetype;
    String phoneNum;
    private TextView tvErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usetype = getIntent().getStringExtra("usetype");
        phoneNum = getIntent().getStringExtra("phonenum");
        setContentView(R.layout.activity_band_phone_send);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btSubmit.setClickable(true);
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.band_phone1_back_imageview);
        etPhone = (EditText) findViewById(R.id.band_phone1_phonenum_edittext);
        btSubmit = (Button) findViewById(R.id.band_phone1_next_button);
        tvTitle = (TextView) findViewById(R.id.band_phone1_title_textview);
        tvErr = (TextView) findViewById(R.id.login_username_err);
        if (null != usetype && usetype.equals("5")) {
            tvTitle.setText(R.string.band_band_phone);
        } else {
            etPhone.setText(getString(R.string.band_band_phone_colon) + phoneNum);
            etPhone.setEnabled(false);
            tvTitle.setText(R.string.band_unband_phone);
        }
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvErr.setVisibility(View.INVISIBLE);
                etPhone.setTextColor(getResources().getColor(R.color.c05_themes_color));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (ivBack.getId() == v.getId()) {
            finish();
        } else if (btSubmit.getId() == v.getId()) {
            tvErr.setVisibility(View.INVISIBLE);
            getMessageVerification();

        }
    }

    public void getMessageVerification() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            tvErr.setText(getString(R.string.not_available_network));
            tvErr.setVisibility(View.VISIBLE);
            return;
        }
        btSubmit.setClickable(false);
        final String phoneNum = etPhone.getText().toString().trim();
        if ("5".equals(usetype))
            if (StringUtils.isBlank(phoneNum) || !phoneNum.matches("^((13[0-9])|14[57]|(15[^4,\\D])|17[678]|" +
                    "(18[0-9]))\\d{8}$")) {
                tvErr.setText(getString(R.string.phonenum_is_error));
                etPhone.setTextColor(getResources().getColor(R.color.c01_themes_color));
                tvErr.setVisibility(View.VISIBLE);
                btSubmit.setClickable(true);
                return;
            }
        dialogLoading = ViewUtils.showProgressDialog(BandPhoneSendActivity.this);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phoneNum);
        map.put("u_usetype", usetype);
        PostRequest request = new PostRequest(URLConstants.SEND_VERIFICATION_CODE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne==" + response.toString());
                dialogLoading.cancel();
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_CODE.getCode() || XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_ACTIVE.getCode() == errorid || XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getCode() == errorid) {
                    tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getMsg());
                    etPhone.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    tvErr.setVisibility(View.VISIBLE);
                    btSubmit.setClickable(true);
                    return;
                }
                if (XSNetEnum._VDATAERRORCODE_ERROR_BIND_MOBILE_REPEAT.getCode() == errorid) {
                    tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_BIND_MOBILE_REPEAT.getMsg());
                    etPhone.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    tvErr.setVisibility(View.VISIBLE);
                    btSubmit.setClickable(true);
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    tvErr.setText(getString(R.string.forgotpassword_senderror) + "(" + errorid + ")");
                    tvErr.setVisibility(View.VISIBLE);
                    btSubmit.setClickable(true);
                    return;
                }

                ViewUtils.toastShort(getString(R.string.forgotpassword_sendsuccess));
                Intent intent = new Intent(BandPhoneSendActivity.this, BandPhoneBandActivity.class);
                intent.putExtra("phoneNum", phoneNum);
                if (ResponseHelper.getVdata(response) != null) {
                    intent.putExtra("token", ResponseHelper.getVdata(response).optString("token"));
                }
                intent.putExtra("usetype", usetype);
                startActivity(intent);
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
}
