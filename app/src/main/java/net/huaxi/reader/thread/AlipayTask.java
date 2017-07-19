package net.huaxi.reader.thread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.activity.SimpleWebViewActivity;
import net.huaxi.reader.bean.AliPayBean;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by eric on 2017/5/17.
 */

public class AlipayTask {

    private AliPayBean aliPayBean;
    private Activity activity;
    private String pid;

    private static final int SDK_PAY_FLAG = 1;

    public AlipayTask(Activity activity,AliPayBean aliPayBean,String pid) throws UnsupportedEncodingException {

        this.activity = activity;
        this.aliPayBean = aliPayBean;
        this.pid = pid;
        payV2();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ViewUtils.toastShort("支付成功");

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ViewUtils.toastShort("支付失败");
                    }
                    if( activity instanceof SimpleWebViewActivity){
                        SimpleWebViewActivity activitys = (SimpleWebViewActivity) activity;
                        if (TextUtils.equals(resultStatus, "9000")) {
                            activitys.setWebView(SimpleWebViewActivity.Pay_coins_successfully);
                        }else{
                            activitys.setWebView(SimpleWebViewActivity.Pay_coins_failed);
                        }

                    }
                    break;
                }

                default:
                    break;
            }
        };
    };

    public void payV2() throws UnsupportedEncodingException {


       final String orderInfo = aliPayBean.getContent();

        LogUtils.debug("orderInfo:"+orderInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


}
