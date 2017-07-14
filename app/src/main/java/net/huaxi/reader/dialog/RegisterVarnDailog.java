package net.huaxi.reader.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.ForgotPasswordSendActivity;
import net.huaxi.reader.activity.LoginActivity;


/**
 * @Description: [登录错误错误超过]
 * @Author: [Saud]
 * @CreateDate: [16/8/26 15:21]
 * @UpDate: [16/8/26 15:21]
 * @Version: [v1.0]
 */
public class RegisterVarnDailog extends BaseDialog implements View.OnClickListener {
    private final Activity activity;
    private final View mResetPassword;
    private final View mTryAgain;
    private final String phoneNum;

    public RegisterVarnDailog(Activity activity,String phoneNum) {
        this.activity = activity;
        this.phoneNum = phoneNum;
        initDialog(activity, null, R.layout.dialog_register_varn, TYPE_CENTER, true);
        mDialog.setCancelable(false);
        mResetPassword = mDialog.findViewById(R.id.btn_reset_password);
        mTryAgain = mDialog.findViewById(R.id.btn_try_again);
        initListener();
    }

    private void initListener() {
        mResetPassword.setOnClickListener(this);
        mTryAgain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_reset_password:
                mDialog.dismiss();
                intent = new Intent(activity, ForgotPasswordSendActivity.class);
                activity.startActivity(intent);

                break;
            case R.id.btn_try_again:
                mDialog.dismiss();
                intent = new Intent(activity, LoginActivity.class);
                intent.putExtra("phonenum",phoneNum );
                activity.startActivity(intent);
                break;
            default:
                break;
        }
    }
}
