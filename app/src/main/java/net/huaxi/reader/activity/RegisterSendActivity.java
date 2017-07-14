package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import net.huaxi.reader.dialog.RegisterVarnDailog;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;

public class RegisterSendActivity extends BaseActivity implements View.OnClickListener {

    private EditText etPhoneNum;
    private ImageView ivBack;
    private Button btNext;
    private TextView tvProtocl;
    private TextView mTvPhoneErr;
    private ImageView mIvPhoneClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_send);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSoftInput();
        btNext.setEnabled(true);
    }

    private void initView() {
        etPhoneNum = (EditText) findViewById(R.id.register1_phonenum_edittext);
        ivBack = (ImageView) findViewById(R.id.register1_back_imageview);
        btNext = (Button) findViewById(R.id.register1_next_button);
        tvProtocl = (TextView) findViewById(R.id.register1_submit_protocl_textview);
        mTvPhoneErr = (TextView) findViewById(R.id.tv_register_phone_err);
        mIvPhoneClear = (ImageView) findViewById(R.id.iv_rigister_phone_clear);

    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        btNext.setOnClickListener(this);
        tvProtocl.setOnClickListener(this);
        mIvPhoneClear.setOnClickListener(this);
        etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etPhoneNum.setTextColor(getResources().getColor(R.color.c05_themes_color));
                mTvPhoneErr.setVisibility(View.INVISIBLE);
                if (s.length() > 0) {
                    mIvPhoneClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPhoneClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        etPhoneNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText=(EditText) v;
                int length = editText.getText().toString().length();
                if (length>0&&hasFocus){
                    mIvPhoneClear.setVisibility(View.VISIBLE);
                }else {
                    mIvPhoneClear.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivBack.getId()) {
            finish();
        } else if (v.getId() == btNext.getId()) {
            //手机发送验证码
            sendVerificationCode();
            UMEventAnalyze.countEvent(this,UMEventAnalyze.LOGIN_REGISTER_SEND);
        } else if (tvProtocl.getId() == v.getId()) {
            UMEventAnalyze.countEvent(this,UMEventAnalyze.LOGIN_REGISTER_PERMISSION);
            Intent intent = new Intent(RegisterSendActivity.this, SimpleWebViewActivity.class);
            intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_USER_PROTOCOL);
            startActivity(intent);
        }
        switch (v.getId()) {
            case R.id.iv_rigister_phone_clear:
                etPhoneNum.setText("");
                break;

            default:
                break;
        }
    }

    private void sendVerificationCode() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            ViewUtils.toastShort(getString(R.string.not_available_network));
            return;
        }
        final String phoneNum = etPhoneNum.getText().toString();
        if (StringUtils.isBlank(phoneNum) || !phoneNum.matches("^((13[0-9])|14[57]|(15[^4,\\D])|17[0-9]|" +
                "(18[0-9]))\\d{8}$")) {
            if ("".equals(phoneNum)||phoneNum.length()==0){
                mTvPhoneErr.setText(getString(R.string.register_phone));
            }else {
                mTvPhoneErr.setText(getString(R.string.register_phone_error));
            }
            etPhoneNum.setTextColor(getResources().getColor(R.color.c01_themes_color));
            mTvPhoneErr.setVisibility(View.VISIBLE);
            return;
        }
        UMEventAnalyze.countEvent(RegisterSendActivity.this, UMEventAnalyze.REGISTER_SEND_MESSAGE);
        btNext.setEnabled(false);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phoneNum);
        map.put("u_usetype", "1");
        dialogLoading = ViewUtils.showProgressDialog(RegisterSendActivity.this);
        PostRequest request = new PostRequest(URLConstants.SEND_VERIFICATION_CODE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne==" + response.toString());
                btNext.setEnabled(true);
                dialogLoading.cancel();
                int errorid = ResponseHelper.getErrorId(response);
                if (XSNetEnum._VDATAERRORCODE_ERROR_USER_EXIST.getCode() == errorid) {
                    new RegisterVarnDailog(RegisterSendActivity.this, etPhoneNum.getText().toString()).show();
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    mTvPhoneErr.setText(getString(R.string.forgotpassword_senderror));
                    etPhoneNum.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    mTvPhoneErr.setVisibility(View.VISIBLE);
                    return;
                }
                ViewUtils.toastShort(getString(R.string.forgotpassword_sendsuccess));
                Intent intent = new Intent(RegisterSendActivity.this, RegisterGetAccountActivity
                        .class);
                intent.putExtra("phoneNum", phoneNum);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btNext.setEnabled(true);
                ViewUtils.toastShort(getString(R.string.network_server_error));
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }

    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPhoneNum, InputMethodManager.SHOW_FORCED);
    }
}
