package net.huaxi.reader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.util.ImageUtil;

import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Pattern;

import net.huaxi.reader.R;

public class ForgotPasswordSendActivity extends BaseActivity implements View.OnClickListener {

    EditText etPhoneNum, etVerification;
    Button btVerification;
    ImageView ivBack, ivPhotoVerfication;
    private String imgUrl;
    private String genenry;
    private Pattern phonePattern = Pattern.compile("^((13[0-9])|14[57]|(15[^4,\\D])|17[0-9]|" +
            "(18[0-9]))\\d{8}$");
    private TextView mTvPhoneErr;
    private TextView mTvCodeErr;
    private ImageView mIvClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_send);
        initview();
        initEvent();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btVerification.setEnabled(true);
    }

    private void initview() {
        etPhoneNum = (EditText) findViewById(R.id.forgot_password1_phonenum_edittext);
        btVerification = (Button) findViewById(R.id.forgot_password1_verification_button);
        ivBack = (ImageView) findViewById(R.id.forgot_password1_back_imageview);
        etVerification = (EditText) findViewById(R.id.forgot_password1_photo_verification_edittext);
        ivPhotoVerfication = (ImageView) findViewById(R.id.forgot_password1_photo_verification_imageview);
        mTvPhoneErr = (TextView) findViewById(R.id.tv_forgot_phone_err);
        mTvCodeErr = (TextView) findViewById(R.id.tv_forgot_code_err);
        mIvClear = (ImageView) findViewById(R.id.iv_forgot_clear);

    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        btVerification.setOnClickListener(this);
        ivPhotoVerfication.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textStatusChange();
            }
        });
        etVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textStatusChange();
            }
        });
    }

    private void textStatusChange() {
        mTvPhoneErr.setVisibility(View.INVISIBLE);
        mTvCodeErr.setVisibility(View.INVISIBLE);
        etVerification.setTextColor(getResources().getColor(R.color.c05_themes_color));
        etPhoneNum.setTextColor(getResources().getColor(R.color.c05_themes_color));
    }


    private void initData() {
        PostRequest request = new PostRequest(URLConstants.GET_VERIFICATION_PHONE, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne==" + response.toString());
                if (!ResponseHelper.isSuccess(response)) {
                    return;
                }
                imgUrl = ResponseHelper.getVdata(response).optString("imgurl");
                genenry = ResponseHelper.getVdata(response).optString("genenry");
                ImageUtil.loadImage(ForgotPasswordSendActivity.this, imgUrl, ivPhotoVerfication, R.mipmap
                        .forgetpassword_verification_refresh);
                ImageUtil.getImageListener(ForgotPasswordSendActivity.this, imgUrl, new ImageUtil.BitmapCallBackListener() {
                    @Override
                    public void callBack(Bitmap bitmap) {
                        if (bitmap == null) {
                            ivPhotoVerfication.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            ivPhotoVerfication.setImageResource(R.mipmap.forgetpassword_verification_refresh);
                        } else {
                            ivPhotoVerfication.setScaleType(ImageView.ScaleType.FIT_XY);
                            ivPhotoVerfication.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getString(R.string.network_server_error));
            }
        }, CommonUtils.getPublicPostArgs());
        RequestQueueManager.addRequest(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_forgot_clear:
                etPhoneNum.setText("");
                break;
            case R.id.forgot_password1_back_imageview:
                finish();
                break;
            case R.id.forgot_password1_verification_button:
                //发送验证码
                sendPhotoVerification();
                break;
            case R.id.forgot_password1_photo_verification_imageview:
                initData();
                break;

            default:
                break;
        }
    }


    //发送图片验证码
    private void sendPhotoVerification() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            ViewUtils.toastShort(getString(R.string.not_available_network));
            return;
        }
        //1,验证手机号码
        final String phoneNum = etPhoneNum.getText().toString();
        if (StringUtils.isBlank(phoneNum)) {
            mTvPhoneErr.setText(getString(R.string.phonenum_is_blank));
            mTvPhoneErr.setVisibility(View.VISIBLE);
            return;
        }
        if (StringUtils.isBlank(phoneNum) || !phonePattern.matcher(phoneNum).matches()) {
            mTvPhoneErr.setText(getString(R.string.phonenum_is_error));
            mTvPhoneErr.setVisibility(View.VISIBLE);
            return;
        }
        //2,图片验证码
        final String verificationCode = etVerification.getText().toString();
        if (StringUtils.isBlank(verificationCode)) {
            mTvCodeErr.setText(getString(R.string.photo_verification_is_blank));
            mTvCodeErr.setVisibility(View.VISIBLE);
            etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
            return;
        }
        if (StringUtils.isBlank(verificationCode) || !verificationCode.matches("^\\d{4}$")) {
            mTvCodeErr.setText(getString(R.string.photo_verification_is_error));
            mTvCodeErr.setVisibility(View.VISIBLE);
            etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
            return;
        }
        //3验证是是否初始化验证码
        if (StringUtils.isBlank(genenry)) {
            mTvCodeErr.setText(getString(R.string.photo_verification_is_overdue));
            mTvCodeErr.setVisibility(View.VISIBLE);
            etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
            return;
        }
        dialogLoading = ViewUtils.showProgressDialog(ForgotPasswordSendActivity.this);
        //3发送请求
        btVerification.setEnabled(false);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_name", phoneNum);
        map.put("u_action", "reset");
        map.put("u_step", "apply");
        map.put("u_vcode", verificationCode);
        map.put("u_genenry", genenry);
        map.put("u_usertype", "2");
        PostRequest request = new PostRequest(URLConstants.RESET_PASSWORD, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response==" + response.toString());
                btVerification.setEnabled(true);
                dialogLoading.cancel();
                int errorid = ResponseHelper.getErrorId(response);
                String msg = "";
                if (!ResponseHelper.isSuccess(response)) {
                    if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_PARAMS_NOT_MATCH.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_PARAMS_NOT_MATCH.getMsg();
                        mTvPhoneErr.setText(msg);
                        mTvPhoneErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_RESETFAIL.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_RESETFAIL.getMsg();
                        mTvPhoneErr.setText(msg);
                        mTvPhoneErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_NOTREGISTER.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_NOTREGISTER.getMsg();
                        mTvPhoneErr.setText(msg);
                        mTvPhoneErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_NOT_PHONENUM.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_NOT_PHONENUM.getMsg();
                        mTvPhoneErr.setText(msg);
                        mTvPhoneErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_NOT_SUPPORT.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_NOT_SUPPORT.getMsg();
                        mTvPhoneErr.setText(msg);
                        mTvPhoneErr.setVisibility(View.VISIBLE);
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_PLEASE_INPUT_RIGHT_VERIFICODE.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_PLEASE_INPUT_RIGHT_VERIFICODE.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                        etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    } else if (XSNetEnum._VDATAERROR_FORGOTPASSWORD_PLEASE_VERIFICODE_ERROR.getCode() == errorid) {
                        msg = XSNetEnum._VDATAERROR_FORGOTPASSWORD_PLEASE_VERIFICODE_ERROR.getMsg();
                        mTvCodeErr.setText(msg);
                        mTvCodeErr.setVisibility(View.VISIBLE);
                        etVerification.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    }
                    return;
                }

                ViewUtils.toastShort(getResources().getString(R.string.forgotpassword_sendsuccess));
                Intent intent = new Intent(ForgotPasswordSendActivity.this, ForgotPasswordPhoneGetActivity.class);
                intent.putExtra("phonenum", phoneNum);
                intent.putExtra("token", ResponseHelper.getVdata(response).optString("token"));
                intent.putExtra("usertype", "2");
                intent.putExtra("genenry", genenry);
                intent.putExtra("verificationCode", verificationCode);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btVerification.setEnabled(true);
                ViewUtils.toastShort(getString(R.string.network_server_error));
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }
}
