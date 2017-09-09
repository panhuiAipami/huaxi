package net.huaxi.reader.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdStatusCodes;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;
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

    public  int REQUEST_SIGN_IN_UNLOGIN = 0;//登陆
    public  int REQUEST_SIGN_IN_AUTH = 1;//需要授权


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

        Drawable drawable;
        TextView login_dialog_weixin_img = (TextView) findViewById(R.id.login_dialog_weixin_img);
        if(Constants.CHANNEL_IS_HUAWEI){
            login_dialog_weixin_img.setText("华为登录");
            drawable= ContextCompat.getDrawable(this,R.mipmap.huawei_login);
        }else{
            login_dialog_weixin_img.setText("微信登录");
            drawable= ContextCompat.getDrawable(this,R.mipmap.login_weixin);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        login_dialog_weixin_img.setCompoundDrawables(null,drawable,null,null);
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
            case R.id.login_dialog_weixin_img://微信登陆
                if(Constants.CHANNEL_IS_HUAWEI){
                  signInHuawei();
                }else {
                    dialogLoading = ViewUtils.showProgressDimDialog(this);
                    UMEventAnalyze.countEvent(this, UMEventAnalyze.LOGIN_WX);
                    getHelperInstance().WxLogin();
                }
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



    /***
     * 华为登录
     * @param openid
     * @param nickname
     * @param avatar
     */
    private void huaweiLogin(String openid,String nickname,String avatar) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            return;
        }
        btLogin.setEnabled(false);
        Map<String, String> map = new HashMap<String, String>();
        map.put("openid", openid);
        map.put("nickname", nickname);
        map.put("avatar", avatar);
        map.putAll(CommonUtils.getPublicPostArgs());
        dialogLoading = ViewUtils.showProgressDialog(LoginActivity.this);
        Request request = new PostRequest(URLConstants.HUAWEI_LOGIN, new Response
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


    /**
     * 登录帐号，开发者可以直接参照该方法写法
     */
    private void signInHuawei() {
        if(!MainActivity.getConnect().isConnected()){
            MainActivity.getConnect().connect();
            return;
        }
        PendingResult<SignInResult> signInResult = HuaweiId.HuaweiIdApi.signIn(MainActivity.getConnect());
        signInResult.setResultCallback(new SignInResultCallback());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.loginHelper != null) {
            this.loginHelper.ActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_SIGN_IN_UNLOGIN) {
            //当返回值是-1的时候表明用户登录成功，需要开发者再次调用signIn
            if(resultCode == Activity.RESULT_OK) {
                Log.i("huawei", "用户登录 成功");
                signInHuawei();
            } else {
                //当resultCode 为0的时候表明用户未登录，则开发者可以处理用户不登录事件
                Log.i("huawei", "用户登录失败或者未登录");
            }
        }
        else if (requestCode == REQUEST_SIGN_IN_AUTH) {
            //当返回值是-1的时候表明用户确认授权，
            if(resultCode == Activity.RESULT_OK) {
                Log.i("huawei", "用户已经授权");
                SignInResult result = HuaweiId.HuaweiIdApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // 授权成功，result.getSignInHuaweiId()获取华为帐号信息
                    SignInHuaweiId account = result.getSignInHuaweiId();
                    huaweiLogin(account.getOpenId(),account.getDisplayName(),account.getPhotoUrl());
                    Log.i("huawei", "用户授权成功，直接返回帐号信息"+account.toString());
                } else {
                    // 授权失败，result.getStatus()获取错误原因
                    Log.i("huawei", "授权失败 失败原因:" + result.getStatus().toString());
                }
            } else {
                //当resultCode 为0的时候表明用户未授权，则开发者可以处理用户未授权事件
                Log.i("huawei", "用户未授权");
            }
        }
    }


    class SignInResultCallback implements ResultCallback<SignInResult> {
        @Override
        public void onResult(SignInResult result) {
            if (result.isSuccess()) {
                //可以获取帐号的 openid，昵称，头像 at信息
                SignInHuaweiId account = result.getSignInHuaweiId();
                huaweiLogin(account.getOpenId(),account.getDisplayName(),account.getPhotoUrl());
                Log.i("huawei", "登录成功" + account.toString());
            } else {
                //当未登录或者未授权，回调的result中包含处理该种异常的intent，开发者只需要通过getData将对应异常的intent获取出来
                //并通过startActivityForResult启动对应的异常处理界面。再相应的页面处理完毕后返回结果后，开发者需要做相应的处理
                if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_UNLOGIN) {
                    Log.i("huawei", "帐号未登录");
                    Intent intent = result.getData();
                    if (intent != null) {
                        startActivityForResult(intent, REQUEST_SIGN_IN_UNLOGIN);
                    } else {
                        //异常场景，未知原因导致的登录失败，开发者可以在这走容错处理
                    }
                } else if (result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_AUTH) {
                    Log.i("huawei", "帐号已登录，需要用户授权");
                    Intent intent = result.getData();
                    if (intent != null) {
                        startActivityForResult(intent, REQUEST_SIGN_IN_AUTH);
                    } else {
                        //异常场景，未知原因导致的登录失败，开发者可以在这走容错处理
                    }
                } else {
                    //其他错误码，开发者可以在这走容错处理
                }
            }
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
