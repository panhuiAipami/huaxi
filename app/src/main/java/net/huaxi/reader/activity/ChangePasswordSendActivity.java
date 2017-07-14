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
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.https.ResponseHelper;

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;

public class ChangePasswordSendActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private EditText etUsername;
    private Button btSubmit;

    private String phoneNum;
    private TextView tvErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_send);
        initView();
        initEvent();
        phoneNum = getString(R.string.changepassword_phone_num_lable) + getIntent().getStringExtra("phonenum");
        if (!StringUtils.isBlank(phoneNum)) {
            etUsername.setText(phoneNum);
            etUsername.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btSubmit.setClickable(true);
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.change_password1_back_imageview);
        etUsername = (EditText) findViewById(R.id.change_password1_phonenum_edittext);
        btSubmit = (Button) findViewById(R.id.change_password1_next_button);
        tvErr = (TextView) findViewById(R.id.change_password_err);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        etUsername.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etUsername.setTextColor(getResources().getColor(R.color.c05_themes_color));
                tvErr.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == ivBack.getId()) {
            finish();
        } else if (v.getId() == btSubmit.getId()) {
            sendPhoneVerification();
            tvErr.setVisibility(View.INVISIBLE);
        }
    }

    private void sendPhoneVerification() {
        btSubmit.setClickable(false);
        LogUtils.debug("btSubmit click false");
        final String phoneNum = etUsername.getText().toString();
//        if (StringUtils.isBlank(phoneNum)) {
//            ViewUtils.toastShort("手机号码不能为空");
//            return;
//        } else if (!phoneNum.matches("^((13[0-9])|14[57]|(15[^4,\\D])|17[678]|(18[0-9]))\\d{8}$")) {
//            ViewUtils.toastShort("手机号码格式错误");
//            return;
//        }
        //2发送验证码
        dialogLoading = ViewUtils.showProgressDialog(ChangePasswordSendActivity.this);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
//        map.put("u_name", phoneNum);
        map.put("u_usetype", "7");
        PostRequest request = new PostRequest(URLConstants.SEND_VERIFICATION_CODE, new Response
                .Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialogLoading.cancel();
                if (!ResponseHelper.isSuccess(response)) {
                    int errorid = ResponseHelper.getErrorId(response);
                    if (XSNetEnum._VDATAERRORCODE_ERROR_PARAM.getCode() == errorid) {
                        tvErr.setText("找不到数据了(" + errorid + ")");
                        tvErr.setVisibility(View.VISIBLE);
                    } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_CODE.getCode() || XSNetEnum._VDATAERRORCODE_ERROR_EMAIL_SEND_ACTIVE.getCode() == errorid || XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getCode() == errorid) {
                        tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_MOBILE_SEND_CODE.getMsg());
                        tvErr.setVisibility(View.VISIBLE);
                    } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_SEND_SMS.getCode()) {
                        tvErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_SEND_SMS.getMsg());
                        tvErr.setVisibility(View.VISIBLE);
                    }
                    btSubmit.setClickable(true);
                    LogUtils.debug("btSubmit click true errorid===" + errorid);
                    return;
                }
                ViewUtils.toastShort(getString(R.string.forgotpassword_sendsuccess));
                Intent intent = new Intent(ChangePasswordSendActivity.this,
                        ChangePasswordModifyActivity.class);
                intent.putExtra("phonenum", phoneNum);
                if (ResponseHelper.getVdata(response) != null) {
                    intent.putExtra("token", ResponseHelper.getVdata(response).optString("token"));
                    intent.putExtra("usertype", "2");
                    startActivity(intent);
                }
                btSubmit.setClickable(true);
                LogUtils.debug("btSubmit click true errorid===" + ResponseHelper.getErrorId(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvErr.setText(getString(R.string.network_server_error));
                tvErr.setVisibility(View.VISIBLE);
                btSubmit.setClickable(true);
                LogUtils.debug("btSubmit click true Error");
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }
}
