package net.huaxi.reader.thread;

import android.app.Activity;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.bean.WXPayOrder;

import net.huaxi.reader.util.LoginHelper;

/**
 * Created by ZMW on 2015/12/21.
 */
public class WXPayTask extends EasyTask<Activity, Void, Void, PayReq> {


    // 保证同一时间运行一个微信支付
    public static volatile boolean isRunning;
    IWXAPI mWeixinAPI = null;
    WXPayOrder order;
    String pid;
    public WXPayTask(Activity caller,WXPayOrder order,String pid) {
        super(caller);
        this.order=order;
        this.pid=pid;
    }

    @Override
    public PayReq doInBackground(Void... params) {
        if (isRunning) {
            return null;
        }
        isRunning = true;
        mWeixinAPI = WXAPIFactory.createWXAPI(caller, LoginHelper.WX_APP_ID, false);
        mWeixinAPI.registerApp(LoginHelper.WX_APP_ID);

        // 1.生成订单信息。
        if (order == null) {
            return null;
        }
        //.调起本地支付
        PayReq req = payOrder(order);

        return req;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void onPostExecute(PayReq result) {
        super.onPostExecute(result);
        isRunning = false;
        if (result != null) {
            LogUtils.debug("订单生成成功");
            mWeixinAPI.sendReq(result);
        } else {
            LogUtils.debug("订单生成失败");
        }
    }


    /**
     * 调起本地微信支付，支付订单.
     *
     * @author taoyf
     * @time 2015年3月31日
     */
    public PayReq payOrder(WXPayOrder payorder) {
        PayReq request = new PayReq();
        request.appId = LoginHelper.WX_APP_ID;
        request.partnerId = LoginHelper.WX_APP_PARTNERID;
        request.prepayId = payorder.getPrepayId();
        request.packageValue = "Sign=WXPay";
        request.nonceStr = payorder.getNonce();
        request.timeStamp = payorder.getTimeStamp();
        request.sign = payorder.getSign();
        return request;

    }



}
