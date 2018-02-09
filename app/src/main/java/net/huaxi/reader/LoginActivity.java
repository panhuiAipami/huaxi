package net.huaxi.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdStatusCodes;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.enumeration.LoginStateEnum;
import com.spriteapp.booklibrary.enumeration.UpdateTextStateEnum;
import com.spriteapp.booklibrary.model.RegisterModel;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.huaxi.reader.activity.RegisterActivity;
import net.huaxi.reader.callback.HttpActionHandle;
import net.huaxi.reader.http.OKhttpRequest;
import net.huaxi.reader.http.UrlUtils;
import net.huaxi.reader.utils.LoginHelper;
import net.huaxi.reader.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.spriteapp.booklibrary.util.ToastUtil.showToast;

/**
 * Created by kuangxiaoguo on 2017/7/26.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpActionHandle {
    private final int JUMPPOS = 0;
    public MyHandler handler;
    private EditText login_username_edittext, login_password_edittext;
    private TextView login_dialog_weixin_img, login_dialog_huawei_img;
    private Button login_login_button;
    private ImageView login_back_imageview;
    private TextView zhuce;
    private String phone, password;
    private OKhttpRequest request;
    private Map<String, String> params;

    private final int REQUEST_SIGN_IN_UNLOGIN = 1;//登陆
    private final int REQUEST_SIGN_IN_AUTH = 2;//需要授权


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewId();
        setListener();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//设置返回给我们最新的intent
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String code = (String) msg.obj;
            if (code != null)
                login(code);
        }
    }

    private void findViewId() {
        request = new OKhttpRequest(this);
        params = new HashMap<>();
        login_username_edittext = (EditText) findViewById(R.id.login_username_edittext);
        login_password_edittext = (EditText) findViewById(R.id.login_password_edittext);
        login_dialog_weixin_img = (TextView) findViewById(R.id.login_dialog_weixin_img);
        login_dialog_huawei_img = (TextView) findViewById(R.id.login_dialog_huawei_img);
        if (HomeActivity.CHANNEL_IS_HUAWEI) login_dialog_huawei_img.setVisibility(View.VISIBLE);
        zhuce = (TextView) findViewById(R.id.zhuce);
        login_login_button = (Button) findViewById(R.id.login_login_button);
        login_back_imageview = (ImageView) findViewById(R.id.login_back_imageview);

    }

    private void setListener() {
        handler = new MyHandler();
        MyApplication.getInstance().setHandler(handler);
        login_dialog_weixin_img.setOnClickListener(this);
        login_dialog_huawei_img.setOnClickListener(this);
        login_login_button.setOnClickListener(this);
        login_back_imageview.setOnClickListener(this);
        zhuce.setOnClickListener(this);

        login_username_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                login_username_edittext.setTextColor(getResources().getColor(R.color.c05_themes_color));
                login_password_edittext.setTextColor(getResources().getColor(R.color.c05_themes_color));
            }
        });

        login_username_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
            }
        });
        login_password_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                login_username_edittext.setTextColor(getResources().getColor(R.color.c05_themes_color));
                login_password_edittext.setTextColor(getResources().getColor(R.color.c05_themes_color));
            }
        });
        login_password_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                int length = editText.getText().toString().length();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_dialog_weixin_img:
                showLoadingDialog();
                getWxCode();
                break;
            case R.id.login_dialog_huawei_img:
                signInHuawei();
                break;
            case R.id.login_login_button:
                phone = login_username_edittext.getText().toString().trim();
                password = login_password_edittext.getText().toString().trim();
                if (phone.isEmpty()) {
                    ToastUtil.showShort("请输入手机号");
                    return;
                } else if (password.isEmpty()) {
                    ToastUtil.showShort("请输入密码");
                    return;
                } else {
                    loginPhone();//手机号登录
                }
                break;
            case R.id.login_back_imageview:
                finish();
                break;
            case R.id.zhuce:
                startActivityForResult(new Intent(this, RegisterActivity.class), JUMPPOS);
                break;
        }
    }

    /**
     * 微信开放平台获取code方法
     */
    public void getWxCode() {
        IWXAPI WXApi = WXAPIFactory.createWXAPI(this, LoginHelper.WX_APP_ID, true);
        WXApi.registerApp(LoginHelper.WX_APP_ID);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_deme_test";
        if (WXApi.isWXAppInstalled()) {
            WXApi.sendReq(req);
        } else {
            dismissDialog();
            showToast("请先安装微信");
        }
    }

    public void login(String code) {
        if (params.size() != 0) params.clear();
        params.put("code", code);
        request.get("login_wechat", UrlUtils.LOGIN_WECHAT, params);
    }
//    public void login1(String code){
//        BookApi.getInstance()
//                .service
//                .getLogin_Wx(code)
//    }

    public void loginPhone() {
        if (params.size() != 0) params.clear();
        params.put("account", phone);
        params.put("password", password);
        showLoadingDialog();
        request.get(UrlUtils.LOGIN_ACCOUNT, UrlUtils.LOGIN_ACCOUNT, params);
    }

    @Override
    public void handleActionError(String actionName, Object object) {
        dismissDialog();
        HuaXiSDK.mLoginState = LoginStateEnum.FAILED;
        EventBus.getDefault().post(UpdateTextStateEnum.UPDATE_TEXT_STATE);

    }

    @Override
    public void handleActionSuccess(String actionName, Object object) {
        if (actionName.equals("login_wechat") || actionName.equals(UrlUtils.LOGIN_ACCOUNT) || actionName.equals(UrlUtils.HWLOGIN)) {
            dismissDialog();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                JSONObject user = jsonObject.getJSONObject("data");
                RegisterModel model = new RegisterModel();
                model.setUserName(user.getString("user_nickname"));
                model.setUserId(user.getString("user_id"));
                model.setToken(jsonObject.getString("token"));
                model.setUser_gender(user.getInt("user_gender"));
                model.setUser_avatar(user.getString("user_avatar"));
                model.setUser_false_point(user.getInt("user_false_point"));
                model.setUser_real_point(user.getInt("user_real_point"));
                model.setUser_vip_class(user.getInt("user_vip_class"));
                HuaXiSDK.getInstance().syncLoginStatus(model);
                setResult(RESULT_OK);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 登录帐号，开发者可以直接参照该方法写法
     */
    private void signInHuawei() {
        if (!HomeActivity.getConnect().isConnected()) {
            HomeActivity.getConnect().connect();
            return;
        }
        PendingResult<SignInResult> signInResult = HuaweiId.HuaweiIdApi.signIn(HomeActivity.getConnect());
        signInResult.setResultCallback(new SignInResultCallback());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case JUMPPOS:
                    finish();
                    break;
                case REQUEST_SIGN_IN_UNLOGIN:
                    //当返回值是-1的时候表明用户登录成功，需要开发者再次调用signIn
                    if (resultCode == Activity.RESULT_OK) {
                        Log.i("huawei", "用户登录 成功");
                        signInHuawei();
                    } else {
                        //当resultCode 为0的时候表明用户未登录，则开发者可以处理用户不登录事件
                        Log.i("huawei", "用户登录失败或者未登录");
                    }
                    break;
                case REQUEST_SIGN_IN_AUTH:
                    //当返回值是-1的时候表明用户确认授权
                    if (resultCode == Activity.RESULT_OK) {
                        Log.i("huawei", "用户已经授权");
                        SignInResult result = HuaweiId.HuaweiIdApi.getSignInResultFromIntent(data);
                        if (result.isSuccess()) {
                            // 授权成功，result.getSignInHuaweiId()获取华为帐号信息
                            SignInHuaweiId account = result.getSignInHuaweiId();
                            huaweiLogin(account.getOpenId(), account.getDisplayName(), account.getPhotoUrl());
                            Log.i("huawei", "用户授权成功，直接返回帐号信息" + account.toString());
                        } else {
                            // 授权失败，result.getStatus()获取错误原因
                            Log.i("huawei", "授权失败 失败原因:" + result.getStatus().toString());
                        }
                    } else {
                        //当resultCode 为0的时候表明用户未授权，则开发者可以处理用户未授权事件
                        Log.i("huawei", "用户未授权");
                    }
                    break;
            }
        }
    }

    class SignInResultCallback implements ResultCallback<SignInResult> {
        @Override
        public void onResult(SignInResult result) {
            if (result.isSuccess()) {
                //可以获取帐号的 openid，昵称，头像 at信息
                SignInHuaweiId account = result.getSignInHuaweiId();
                huaweiLogin(account.getOpenId(), account.getDisplayName(), account.getPhotoUrl());
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

    /***
     * 华为登录
     * @param openid
     * @param nickname
     * @param avatar
     */
    private void huaweiLogin(String openid, String nickname, String avatar) {
        if (!NetworkUtil.isAvailable(this)) {
            return;
        }
        if (params.size() != 0) params.clear();
        params.put("openid", openid);
        params.put("nickname", nickname);
        params.put("avatar", avatar);
        showLoadingDialog();
        request.post(UrlUtils.HWLOGIN, UrlUtils.HWLOGIN, params);


    }
}
