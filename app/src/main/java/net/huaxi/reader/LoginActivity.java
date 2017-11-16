package net.huaxi.reader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.RegisterModel;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewId();
        setListener();
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

    }

    @Override
    public void handleActionSuccess(String actionName, Object object) {
        if (actionName.equals("login_wechat") || actionName.equals(UrlUtils.LOGIN_ACCOUNT)) {
            dismissDialog();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                JSONObject user = jsonObject.getJSONObject("data");
                RegisterModel model = new RegisterModel();
                model.setUserName(user.getString("user_nickname"));
                model.setUserId(user.getString("user_id"));
                model.setToken(jsonObject.getString("token"));
//                    UserInfo.getInstance().setUser_nickname(user.getString("user_nickname"));
//                    UserInfo.getInstance().setUser_id(user.getString("user_id"));
//                    UserInfo.getInstance().setToken(jsonObject.getString("token"));
//                    UserInfo.getInstance().commit();
                HuaXiSDK.getInstance().syncLoginStatus(model);
                setResult(RESULT_OK);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case JUMPPOS:
                    finish();
                    break;
            }
        }
    }
}
