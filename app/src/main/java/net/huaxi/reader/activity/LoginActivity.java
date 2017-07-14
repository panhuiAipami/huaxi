package net.huaxi.reader.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.Utils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.dialog.LoginVarnDailog;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.thread.SwitchUserMigrateTask;
import net.huaxi.reader.util.EventBusUtil;
import net.huaxi.reader.util.UMEventAnalyze;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import net.huaxi.reader.R;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.UmengHelper;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.util.LoginHelper;

/**
 * Created by ZMW on 2015/12/15.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    LoginHelper.HelpListener listener = new LoginHelper.HelpListener() {
        @Override
        public void onSuccessComplete() {
            UmengHelper.getInstance().addAlias();
            new SwitchUserMigrateTask(LoginActivity.this).execute();
            UMEventAnalyze.countEvent(LoginActivity.this, UMEventAnalyze.LOGIN_SUCCESS);
        }
    };
    private Button btLogin;
    private EditText etUsername, etPassword;
    //正则表达式
//    private Pattern phonePattern = Pattern.compile("^((13[0-9])|14[57]|(15[^4,\\D])|17[0-9]|" +
//            "(18[0-9]))\\d{8}$");
//    private Pattern passwordPattern = Pattern.compile("^\\S{6,12}$");
    //登录需要
    private LoginHelper loginHelper;
    @BindView(R.id.login_password_clear)
    View mIvPasswordClear;
    @BindView(R.id.login_username_clear)
    View mIvUsernameClear;
    @BindView(R.id.login_password_err)
    TextView mTvPasswordErr;
    @BindView(R.id.login_username_err)
    TextView mTvUsernaneErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        tintManager.setStatusBarTintEnabled(false);
        initView();
        initListener();
        ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_ACCOUNT);
    }

    private void initListener() {
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvUsernaneErr.setVisibility(View.INVISIBLE);
                mTvPasswordErr.setVisibility(View.INVISIBLE);
                etUsername.setTextColor(getResources().getColor(R.color.c05_themes_color));
                etPassword.setTextColor(getResources().getColor(R.color.c05_themes_color));
                if (s.length() > 0) {
                    mIvUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    mIvUsernameClear.setVisibility(View.INVISIBLE);
                }
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
                if (length > 0 && hasFocus) {
                    mIvUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    mIvUsernameClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvPasswordErr.setVisibility(View.INVISIBLE);
                mTvUsernaneErr.setVisibility(View.INVISIBLE);
                etUsername.setTextColor(getResources().getColor(R.color.c05_themes_color));
                etPassword.setTextColor(getResources().getColor(R.color.c05_themes_color));
                if (s.length() > 0) {
                    mIvPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPasswordClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        btLogin.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        btLogin = (Button) findViewById(R.id.login_login_button);
        btLogin.setVisibility(View.GONE);
        etPassword = (EditText) findViewById(R.id.login_password_edittext);
        etPassword.setVisibility(View.GONE);
        etUsername = (EditText) findViewById(R.id.login_username_edittext);
        etUsername.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.login_title_status_layout);
            ViewGroup.LayoutParams params = statusLayout.getLayoutParams();
            params.height = Utils.getStatusBarHeight();
            statusLayout.setLayoutParams(params);
            statusLayout.invalidate();
        }

    }

    @Override
    public void onKeyBack() {
        UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_BACK);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String num = getIntent().getStringExtra("phonenum");
        LogUtils.debug("num==" + num);
        if (StringUtils.isNotBlank(num)) {
            etUsername.setText(num);
            etPassword.setText("");
        }
    }

    public LoginHelper getHelperInstance() {
        if (loginHelper == null) {
            loginHelper = new LoginHelper(LoginActivity.this);
            AppContext.setLoginHelper(loginHelper);
            loginHelper.setListener(listener);
        }
        return loginHelper;
    }

    @OnClick({R.id.login_back_imageview, R.id.login_dialog_qq_img,
            R.id.login_dialog_weixin_img, R.id.login_login_button,
            R.id.login_forgetpassword_textview, R.id.login_register_textview,
            R.id.login_instructions, R.id.login_username_clear, R.id.login_password_clear})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.login_back_imageview:
                onKeyBack();
                break;
            case R.id.login_dialog_qq_img:
                getHelperInstance().QQLogin(listener);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_QQ);
                break;
            case R.id.login_dialog_weixin_img:
                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_WX);
                getHelperInstance().WxLogin();
                break;
            case R.id.login_username_clear:
                etUsername.setText("");
                break;
            case R.id.login_password_clear:
                etPassword.setText("");
                break;
            case R.id.login_login_button:
//                Matcher phoneMatcher = phonePattern.matcher(etUsername.getText().toString());
//                Matcher pwdMatcher = passwordPattern.matcher(etPassword.getText().toString());
//                if (phoneMatcher.matches()) {
//匹配
//                login(name, pwd);
                //                } else {
//                    ViewUtils.toastShort(getString(R.string.login_username_is_phonenum));
//                }
                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_SUBMIT);
                String name = etUsername.getText().toString().trim();
                String pwd = etPassword.getText().toString().trim();
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(pwd)) {
                    login(name, pwd);
                }
                if (StringUtils.isBlank(name)) {
                    mTvUsernaneErr.setText(getString(R.string.login_user_name_null));
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                }
                if (StringUtils.isBlank(pwd)) {
                    mTvPasswordErr.setText(getString(R.string.login_user_password_null));
                    mTvPasswordErr.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.login_forgetpassword_textview:
                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_FORGET);
                intent = new Intent(LoginActivity.this, ForgotPasswordSendActivity.class);
                startActivity(intent);
                break;
            case R.id.login_register_textview:
                intent = new Intent(LoginActivity.this, RegisterSendActivity.class);
                startActivity(intent);
                UMEventAnalyze.countEvent(LoginActivity.this, UMEventAnalyze.LOGIN_REGISTER);
                break;
            case R.id.login_instructions:
                intent = new Intent(LoginActivity.this, SimpleWebViewActivity.class);
                intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_LOGIN_INSTRUCTIONS);
                startActivity(intent);
                break;
        }
    }


    private void login(String name, String pwd) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            mTvUsernaneErr.setText(getString(R.string.not_available_network));
            mTvUsernaneErr.setVisibility(View.VISIBLE);
            return;
        }
        btLogin.setEnabled(false);
        Map<String, String> map = new HashMap<String, String>();
        map.put("u_name", name);
        map.put("u_pwd", pwd);
        map.put("u_ctype", "1");
        map.put("u_keep", "1");
        map.putAll(CommonUtils.getPublicPostArgs());
        dialogLoading = ViewUtils.showProgressDialog(LoginActivity.this);
        Request request = new PostRequest(URLConstants.PHONE_LOGIN, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                btLogin.setEnabled(true);
                dialogLoading.cancel();
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_FAIL.getCode()) {
                    JSONObject vdata = ResponseHelper.getVdata(response);
                    JSONObject locker = vdata.optJSONObject("locker");
                    if (locker != null) {
                        int times = locker.optInt("cli_try_times");
                        int unLocker = locker.optInt("cli_unlock_time");
                        //time==0的时候被锁定，10次错误机会
                        if (times != 0 && times <= 7) {
                            //弹出提示对话框
                            showVarnDialog();
                        } else {
                            //
                        }
                    }
                    mTvPasswordErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_FAIL.getMsg());
                    mTvPasswordErr.setVisibility(View.VISIBLE);
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_ACCOUNT_LOCKED.getCode() == errorid) {
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_ACCOUNT_LOCKED.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_ACCOUNT_LOCKED2.getCode() == errorid) {
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_ACCOUNT_LOCKED2.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_USER_NOT_EXIST.getCode() == errorid) {
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_USER_NOT_EXIST.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_PARAM.getCode() == errorid) {
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_PARAM.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_ACTIVE.getCode() == errorid) {
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_ACTIVE.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_USERNAME.getCode() == errorid) {
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_USERNAME.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_PASSWORD.getCode() == errorid) {
                    mTvPasswordErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_PASSWORD.getMsg());
                    mTvPasswordErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_ERR_USER.getCode() == errorid) {
                    mTvPasswordErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_ERR_USER.getMsg());
                    mTvPasswordErr.setVisibility(View.VISIBLE);
                    return;
                } else if (XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_LOCK.getCode() == errorid) {
                    mTvUsernaneErr.setText(XSNetEnum._VDATAERRORCODE_ERROR_LOGIN_LOCK.getMsg());
                    mTvUsernaneErr.setVisibility(View.VISIBLE);
                    etUsername.setTextColor(getResources().getColor(R.color.c01_themes_color));
                    return;
                } else if (!ResponseHelper.isSuccess(response)) {
                    mTvUsernaneErr.setText(getString(R.string.login_fail));
                    mTvUsernaneErr.setVisibility(View.VISIBLE);

                    return;
                }


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
                    //发送通知，在MainActivity中实现登录。
                    if (data.has("first_login")) {
                        boolean isFirst = data.getBoolean("first_login");
                        if (data.has("login_coins") && isFirst) {
                            String loginCoins = data.getString("login_coins");
                            EventBusUtil.EventBean eventBean = new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_GIVE_COINS, EventBusUtil.EVENTTYPE_GIVE_COINS);
                            eventBean.setDesc(loginCoins);
                            EventBus.getDefault().post(eventBean);
                        }
                    }

                    SharePrefHelper.setCurLoginUserBookshelfUpdateTime(0);
                    dialogLoading.cancel();
                    listener.onSuccessComplete();
                } catch (JSONException e) {
                    if (dialogLoading != null && dialogLoading.isShowing()) {
                        dialogLoading.cancel();
                        finish();
                    }
                }
                ViewUtils.toastShort(getString(R.string.login_success));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogLoading.cancel();
                btLogin.setEnabled(true);
                mTvUsernaneErr.setText(getString(R.string.not_available_network));
                mTvUsernaneErr.setVisibility(View.VISIBLE);
            }
        }, map, "1.1");
        request.setShouldCache(false);
        RequestQueueManager.addRequest(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.loginHelper != null) {
            LogUtils.debug("requestcode==" + requestCode);//11101
            LogUtils.debug("resultCode==" + resultCode);//0,-1成功
            this.loginHelper.ActivityResult(requestCode, resultCode, data);
        }
    }


    private void showVarnDialog() {
        LoginVarnDailog giveCoinsDailog = new LoginVarnDailog(this);
        if (!giveCoinsDailog.isShowing()) {
            giveCoinsDailog.show();
        }
    }

}
