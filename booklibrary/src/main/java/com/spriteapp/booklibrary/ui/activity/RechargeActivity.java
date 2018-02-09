package com.spriteapp.booklibrary.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;


/**
 * 充值
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener {
    TextView hua_ban, hua_bei, user_name;
    ImageView user_head;

    @Override
    public int getLayoutResId() throws Exception {
        return R.layout.activity_recharge;
    }

    @Override
    public void initData() throws Exception {
        setUserData();
    }

    @Override
    public void configViews() throws Exception {
    }

    public void findViewId() throws Exception {
        findViewById(R.id.image_back).setOnClickListener(this);
        user_head = (ImageView) findViewById(R.id.user_head);
        user_name = (TextView) findViewById(R.id.user_name);
        hua_ban = (TextView) findViewById(R.id.hua_ban);
        hua_bei = (TextView) findViewById(R.id.hua_bei);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.image_back) {
            finish();
        }
    }

    public void setUserData() {
        if (AppUtil.isLogin(this)) {
            GlideUtils.loadImage(user_head, UserBean.getInstance().getUser_avatar(), this);
            hua_ban.setText(Util.getString(UserBean.getInstance().getUser_false_point() + ""));
            hua_bei.setText(Util.getString(UserBean.getInstance().getUser_real_point() + ""));
            user_name.setText(UserBean.getInstance().getUser_nickname());
        }
    }
}
