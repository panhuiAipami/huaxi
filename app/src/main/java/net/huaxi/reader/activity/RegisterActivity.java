package net.huaxi.reader.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.model.RegisterModel;

import net.huaxi.reader.BaseActivity;
import net.huaxi.reader.R;
import net.huaxi.reader.callback.HttpActionHandle;
import net.huaxi.reader.http.OKhttpRequest;
import net.huaxi.reader.http.UrlUtils;
import net.huaxi.reader.utils.ToastUtil;
import net.huaxi.reader.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, HttpActionHandle {
    private TextView title_name, send_btn, complete, agreement;
    private EditText edphone, ednum, edpassword_one, edpassword_two;
    private String phone, password_one, num, password_two_;
    private OKhttpRequest request;
    private Map<String, String> params;
    private int time = 59;

    @Override
    protected void handleMessage(Message msg) throws Exception {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                String text = null;
                if (time == 0) {
                    send_btn.setEnabled(true);
                    text = "重新获取";
                    time = 60;
                } else {
                    text = time-- + "s";
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
                send_btn.setText(text);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findById();
        initData();
        initListen();
    }


    public void findById() {
        title_name = (TextView) findViewById(R.id.text_title);
        title_name.setText("注册");
        send_btn = (TextView) findViewById(R.id.send_btn);
        complete = (TextView) findViewById(R.id.complete);
        agreement = (TextView) findViewById(R.id.agreement);
        edphone = (EditText) findViewById(R.id.edphone);
        ednum = (EditText) findViewById(R.id.ednum);
        edpassword_one = (EditText) findViewById(R.id.edpassword_one);
        edpassword_two = (EditText) findViewById(R.id.password_two);
    }

    public void initListen() {
        findViewById(R.id.image_back).setOnClickListener(this);
        send_btn.setOnClickListener(this);
        complete.setOnClickListener(this);
        agreement.setOnClickListener(this);
    }

    public void initData() {
        request = new OKhttpRequest(this);
        params = new HashMap<>();
    }


    public void sendNum() {//发送验证码
        if (params.size() != 0) params.clear();
        params.put("mobile", phone);
        params.put("u_action", "captcha");
        request.get(UrlUtils.LOGIN_REGISTER, UrlUtils.LOGIN_REGISTER, params);
    }

    public void proveNum() {//验证信息
        showLoadingDialog();
        if (params.size() != 0) params.clear();
        params.put("mobile", phone);
        params.put("captcha", num);
        params.put("password", password_one);
        params.put("u_action", "activate");
        request.get("proveNum", UrlUtils.LOGIN_REGISTER, params);
    }

    @Override
    public void handleActionError(String actionName, Object object) {
        if (actionName.equals(UrlUtils.LOGIN_REGISTER)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                ToastUtil.showShort(jsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (actionName.equals("proveNum")) {
            dismissDialog();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                ToastUtil.showShort(jsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleActionSuccess(String actionName, Object object) {
        if (actionName.equals(UrlUtils.LOGIN_REGISTER)) {//发送验证码
            send_btn.setEnabled(false);
            mHandler.sendEmptyMessage(0);
        } else if (actionName.equals("proveNum")) {
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
                if (ListenerManager.getInstance().getLoginSuccess() != null) {
                    ListenerManager.getInstance().getLoginSuccess().loginState(1);
                }
                setResult(RESULT_OK);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back://返回
                finish();
                break;
            case R.id.send_btn://发送验证码
                phone = edphone.getText().toString().trim();
                if (phone.isEmpty()) {
                    ToastUtil.showShort("请输入手机号");
                    return;
                } else {
                    if (Util.isMobileNO(phone)) {
                        sendNum();//发送验证码
                    } else {
                        ToastUtil.showShort("请输入正确的手机号");
                    }
                }
                break;
            case R.id.complete://注册
                phone = edphone.getText().toString().trim();
                num = ednum.getText().toString().trim();
                password_one = edpassword_one.getText().toString().trim();
                password_two_ = edpassword_two.getText().toString().trim();
                if (phone.isEmpty()) {
                    ToastUtil.showShort("请输入手机号");
                    return;
                } else if (num.isEmpty()) {
                    ToastUtil.showShort("请输入验证码");
                    return;
                } else if (password_one.isEmpty()) {
                    ToastUtil.showShort("请输入密码");
                    return;
                } else if (password_two_.isEmpty()) {
                    ToastUtil.showShort("请确认密码");
                    return;
                } else if (!Util.isMobileNO(phone)) {
                    ToastUtil.showShort("请输入正确的手机号");
                    return;
                } else if (!password_one.equals(password_two_)) {
                    ToastUtil.showShort("两次输入的密码不一致");
                    return;
                } else if (phone.length() > 12 || phone.length() < 6) {
                    ToastUtil.showShort("请输入6-12位字母或数字密码");
                    return;
                } else {//验证注册信息
                    proveNum();
                }
                break;
            case R.id.agreement:
//                if (params.size() != 0) params.clear();
//                params.put("book_id", "732");
//                request.get("name", "book_details", params);
                break;
        }
    }
}
