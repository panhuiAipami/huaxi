package net.huaxi.reader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.RegisterModel;
import com.spriteapp.huaxireader.R;

/**
 * Created by kuangxiaoguo on 2017/7/26.
 */

public class LoginActivity extends Activity {

    EditText mUserNameEditText;
    EditText mUserIdEditText;
    Button mLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewId();
        setListener();
    }

    private void setListener() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mUserNameEditText.getText().toString();
                String userId = mUserIdEditText.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userName.trim())) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userId.trim())) {
                    Toast.makeText(LoginActivity.this, "请输入用户id", Toast.LENGTH_SHORT).show();
                    return;
                }
                RegisterModel model = new RegisterModel();
                model.setUserName(userName);
                model.setUserId(userId);
                HuaXiSDK.getInstance().syncLoginStatus(model);
                finish();
            }
        });
    }

    private void findViewId() {
        mUserNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        mUserIdEditText = (EditText) findViewById(R.id.user_id_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
    }
}
