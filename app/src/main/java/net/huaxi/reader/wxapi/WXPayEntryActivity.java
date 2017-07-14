package net.huaxi.reader.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.BroadCastConstant;
import net.huaxi.reader.util.UMEventAnalyze;

import net.huaxi.reader.R;

import net.huaxi.reader.util.LoginHelper;

/**
 * Created by ZMW on 2015/12/26.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    private TextView result;

    private Button payresult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_result);
        result = (TextView) findViewById(R.id.result);
        payresult = (Button) findViewById(R.id.btn_payresult);
        api = WXAPIFactory.createWXAPI(this, LoginHelper.WX_APP_ID);
        api.handleIntent(getIntent(), this);

        payresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("log", "onPayFinish, errCode = " + resp.errCode + " msg= " + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int code = resp.errCode;
//            if (code == 0) {
//                Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                result.setText("支付成功!");
//            } else if (code == -2) {
//                Toast.makeText(WXPayEntryActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
//            } else {
//                result.setText("支付失败!");
//            }
//            LogUtils.debug("resp.checkArgs()==" + resp.checkArgs());
//            LogUtils.debug("resp=====" + resp.errStr);
//            LogUtils.debug("resp.openid" + resp.openId);
            Intent intent = new Intent();
            intent.setAction(BroadCastConstant.WXPAYCALLBACK);
            if (code == 0) {
                intent.putExtra("success", 1);
                UMEventAnalyze.resultEvent(AppContext.context(),UMEventAnalyze.RECHARGE_RESULT,0);
            } else {
                intent.putExtra("success", 0);
                if(code == -1){
                    UMEventAnalyze.resultEvent(AppContext.context(),UMEventAnalyze.RECHARGE_RESULT,2);
                } else if (code == -2) {//用户取消
                    UMEventAnalyze.resultEvent(AppContext.context(),UMEventAnalyze.RECHARGE_RESULT,1);
                }

            }
            sendBroadcast(intent);
        }else{
            Intent intent = new Intent();
            intent.setAction(BroadCastConstant.WXPAYCALLBACK);
                intent.putExtra("success", 0);
            sendBroadcast(intent);
        }
        finish();
    }
}
