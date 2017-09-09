package net.huaxi.reader.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.hwid.HuaweiIdStatusCodes;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;

/**
 * Created by panhui on 2017/8/29.
 */

public class SignInResultCallback implements ResultCallback<SignInResult>{
    public static int REQUEST_SIGN_IN_UNLOGIN = 0;//登陆
    public static int REQUEST_SIGN_IN_AUTH = 1;//需要授权

    Activity a;
    public SignInResultCallback(Activity a){
        this.a = a;
    }

    @Override
    public void onResult(SignInResult result) {
        if(result.isSuccess()){
            //可以获取帐号的 openid，昵称，头像 at信息
            SignInHuaweiId account = result.getSignInHuaweiId();
            Log.i("huawei", "登录成功"+account.toString());
        } else {
            //当未登录或者未授权，回调的result中包含处理该种异常的intent，开发者只需要通过getData将对应异常的intent获取出来
            //并通过startActivityForResult启动对应的异常处理界面。再相应的页面处理完毕后返回结果后，开发者需要做相应的处理
            if(result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_UNLOGIN){
                Log.i("huawei", "帐号未登录");
                Intent intent = result.getData();
                if(intent != null) {
                    a.startActivityForResult(intent, REQUEST_SIGN_IN_UNLOGIN);
                } else {
                    //异常场景，未知原因导致的登录失败，开发者可以在这走容错处理
                }
            } else if(result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_AUTH){
                Log.i("huawei", "帐号已登录，需要用户授权");
                Intent intent = result.getData();
                if(intent != null) {
                    a.startActivityForResult(intent, REQUEST_SIGN_IN_AUTH);
                } else {
                    //异常场景，未知原因导致的登录失败，开发者可以在这走容错处理
                }
            } else {
                //其他错误码，开发者可以在这走容错处理
            }
        }
    }
}
