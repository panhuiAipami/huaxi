package net.huaxi.reader.activity;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UmengHelper;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.dialog.LoginVarnDailog;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.thread.SwitchUserMigrateTask;
import net.huaxi.reader.util.LoginHelper;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.huaxi.reader.R.id.login_back_imageview;


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
    private ImageView login_back_imageview1;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Intent intent = getIntent();
        boolean flag = intent.getBooleanExtra("flag", false);
        login_back_imageview1 = (ImageView) findViewById(login_back_imageview);
        if (flag) {
            login_back_imageview1.setVisibility(View.GONE);
        } else {
            login_back_imageview1.setVisibility(View.VISIBLE);
        }
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
                etUsername.setTextColor(getResources().getColor(R.color.c05_themes_color));
                etPassword.setTextColor(getResources().getColor(R.color.c05_themes_color));
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
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
                etUsername.setTextColor(getResources().getColor(R.color.c05_themes_color));
                etPassword.setTextColor(getResources().getColor(R.color.c05_themes_color));
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
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
        etPassword = (EditText) findViewById(R.id.login_password_edittext);
        etUsername = (EditText) findViewById(R.id.login_username_edittext);
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


    Dialog dialogLoading = null;

    @OnClick({login_back_imageview, R.id.login_login_button, R.id.login_dialog_weixin_img})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case login_back_imageview://返回
                onKeyBack();
                break;
//            case R.id.login_dialog_qq_img:
//                getHelperInstance().QQLogin(listener);
//                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_QQ);
//                break;
            case R.id.login_dialog_weixin_img:
                dialogLoading = ViewUtils.showProgressDimDialog(this);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_WX);
                getHelperInstance().WxLogin();
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

                if (StringUtils.isBlank(name)) {
                    ViewUtils.toastShort("请输入正确的账号");
                } else if (StringUtils.isBlank(pwd)) {
                    ViewUtils.toastShort("请输入正确的密码");
                } else {
                    login(name, pwd);
                }

                break;
//            case R.id.login_forgetpassword_textview:
//                UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_FORGET);
//                intent = new Intent(LoginActivity.this, ForgotPasswordSendActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.login_register_textview:
//                intent = new Intent(LoginActivity.this, RegisterSendActivity.class);
//                startActivity(intent);
//                UMEventAnalyze.countEvent(LoginActivity.this, UMEventAnalyze.LOGIN_REGISTER);
//                break;
//            case R.id.login_instructions:
//                intent = new Intent(LoginActivity.this, SimpleWebViewActivity.class);
//                intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_LOGIN_INSTRUCTIONS);
//                startActivity(intent);
//                break;
        }
    }


    /***
     * 账号密码登录
     * @param name
     * @param pwd
     */
    private void login(String name, String pwd) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            return;
        }
        btLogin.setEnabled(false);
        Map<String, String> map = new HashMap<String, String>();
        map.put("account", name);
        map.put("password", pwd);
        map.putAll(CommonUtils.getPublicPostArgs());
        dialogLoading = ViewUtils.showProgressDialog(LoginActivity.this);
        Request request = new PostRequest(URLConstants.PHONE_LOGIN, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                btLogin.setEnabled(true);
                dialogLoading.cancel();
                try {
                    JSONObject data = new JSONObject(response.toString());
                    int code = data.getInt("code");
                    if (code == 10000) {
                        ViewUtils.toastShort(getString(R.string.login_success));
                        setCookieAndUid(response);
                        finish();
                    } else {//data.get("message").toString()
                        ViewUtils.toastShort("账号或密码错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.cancel();
                    btLogin.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogLoading.cancel();
                btLogin.setEnabled(true);
                ViewUtils.toastShort(error.getMessage());
            }
        }, map, "1.1");
        request.setShouldCache(false);
        RequestQueueManager.addRequest(request);
    }

    private void setCookieAndUid(JSONObject response) {

        SharePrefHelper.setCurLoginUserBookshelfUpdateTime(0);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.loginHelper != null) {
            this.loginHelper.ActivityResult(requestCode, resultCode, data);
        }
    }


    private void showVarnDialog() {
        LoginVarnDailog giveCoinsDailog = new LoginVarnDailog(this);
        if (!giveCoinsDailog.isShowing()) {
            giveCoinsDailog.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialogLoading != null){
            dialogLoading.cancel();
            dialogLoading = null;
        }
    }
}
