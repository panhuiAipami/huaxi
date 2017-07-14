package net.huaxi.reader.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.ForgotPasswordSendActivity;


/**   
 * @Description:  [登录错误错误超过]
 * @Author:       [Saud]   
 * @CreateDate:   [16/8/26 15:21]
 * @UpDate:       [16/8/26 15:21]   
 * @Version:      [v1.0] 
 * 
 */
public class LoginVarnDailog extends BaseDialog implements View.OnClickListener {
    private final Activity activity;
    private final View mResetPassword;
    private final View mTryAgain;

    public LoginVarnDailog(Activity activity) {
        this.activity = activity;
        initDialog(activity, null, R.layout.dialog_login_varn, TYPE_CENTER, true);
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
        switch (v.getId()) {
            case R.id.btn_reset_password:
                Intent intent=new Intent(activity, ForgotPasswordSendActivity.class);
                activity.startActivity(intent);
                mDialog.dismiss();
                break;
            case R.id.btn_try_again:
                mDialog.dismiss();
                break;
            default:
                break;
        }
    }
}
