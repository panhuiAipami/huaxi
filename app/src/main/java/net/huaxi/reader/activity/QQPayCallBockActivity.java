package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.IOpenApiListener;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.data.base.BaseResponse;
import com.tencent.mobileqq.openpay.data.pay.PayResponse;
import com.tools.commonlibs.activity.BaseActivity;

import net.huaxi.reader.common.BroadCastConstant;
import net.huaxi.reader.util.LoginHelper;

public class QQPayCallBockActivity extends BaseActivity implements IOpenApiListener {


    private IOpenApi openApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        openApi = OpenApiFactory.getInstance(this, LoginHelper.QQLOGIN_APP_ID);
        openApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openApi.handleIntent(intent, this);
    }

    @Override
    public void onOpenResponse(BaseResponse response) {
        PayResponse payResponse;
        Intent intent = new Intent();
        intent.setAction(BroadCastConstant.QQPAYCALLBACK);
        if (response instanceof PayResponse) {
            payResponse = (PayResponse) response;
            if (payResponse.retCode == 0) {//成功
                intent.putExtra("success", 1);
            } else {
                intent.putExtra("success", 0);
            }
        }
        sendBroadcast(intent);
        finish();
    }

}
