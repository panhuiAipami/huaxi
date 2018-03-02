package com.spriteapp.booklibrary.util;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;

import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.PayResult;

/**
 * Created by panhui on 2017/8/29.
 */

public class HuaweiPayResult implements ResultCallback<PayResult> {
    private int requestCode;
    private Activity a;
    public HuaweiPayResult(Activity a, int requestCode) {
        this.requestCode = requestCode;
        this.a = a;
    }
    @Override
    public void onResult(PayResult result) {
        //支付鉴权结果，处理result.getStatus()
        Status status = null ;
        status =  result.getStatus();
        if (PayStatusCodes.PAY_STATE_SUCCESS == status.getStatusCode()) {
            //当支付回调 返回码为0的时候，表明支付流程正确，CP需要调用startResolutionForResult接口来进来后续处理
            //支付会先判断华为帐号是否登录，如果未登录，会先提示用户登录帐号。之后才会进行支付流程
            try {
                status.startResolutionForResult(a, requestCode);
            } catch (IntentSender.SendIntentException e) {
                Log.e("huawei", "启动支付失败"+e.getMessage());
            }
        } else {
            Log.i("huawei", "支付失败，原因 :" + status.getStatusCode());
        }
    }
}
