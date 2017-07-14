package net.huaxi.reader.thread;

import android.app.Activity;

import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.alipay.sdk.app.PayTask;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.activity.SimpleWebViewActivity;
import net.huaxi.reader.bean.AliPayBean;
import net.huaxi.reader.bean.AlipayResult;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.PayUtils;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.statistic.ReportUtils;

/**
 * Created by ZMW on 2015/12/21.
 */
public class AlipayWalletPayTask extends EasyTask<Activity, Void, Void, String> {

    private static final String ALIPAY_PAY_SUCCESS = "9000";

    // 保证同一时间运行一个支付宝支付
    public static volatile boolean isRunning;

//    private ProgressDialog pd;
    private AliPayBean aliPayBean;
    private String pid;

    /**
     * 支付宝 - 支付方法
     *
     */
    public enum AliPayMethod {
        normal(""), // 标准支付
        express("expressGateway"), // 银行卡

        ;

        private String tag;

        private AliPayMethod(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    public AlipayWalletPayTask(Activity caller,AliPayBean aliPayBean,String pid) {
        super(caller);
        this.aliPayBean=aliPayBean;
        this.pid=pid;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(String resultString) {
        isRunning = false;
        final AlipayResult result = new AlipayResult(resultString);
        LogUtils.debug("result==" + result.getResultStatus());
        if (ALIPAY_PAY_SUCCESS.equals(result.getResultStatus())){
            LogUtils.debug("支付成功");
            ViewUtils.toastShort("支付成功");
            UMEventAnalyze.resultEvent(AppContext.context(),UMEventAnalyze.RECHARGE_RESULT,0);
        }else if("6001".equals(result.getResultStatus())){
            LogUtils.debug("取消支付");
            ViewUtils.toastShort("取消支付");
            UMEventAnalyze.resultEvent(AppContext.context(),UMEventAnalyze.RECHARGE_RESULT,1);
        }else{
            LogUtils.debug("支付失败");
            ViewUtils.toastShort("支付失败");
            UMEventAnalyze.resultEvent(AppContext.context(),UMEventAnalyze.RECHARGE_RESULT,2);
        }
        if(caller instanceof SimpleWebViewActivity){
            SimpleWebViewActivity activity= (SimpleWebViewActivity) caller;
            if (ALIPAY_PAY_SUCCESS.equals(result.getResultStatus())){
                if(PayUtils.PRODUCT_TYPE_COIN==Integer.parseInt(pid)){

                    activity.setWebView(SimpleWebViewActivity.Pay_coins_successfully);

                }else{
                    activity.setWebView(SimpleWebViewActivity.Pay_hire_successfully);
                }
            }else{
                if(PayUtils.PRODUCT_TYPE_COIN==Integer.parseInt(pid)) {
                    activity.setWebView(SimpleWebViewActivity.Pay_coins_failed);
                }else{
                    activity.setWebView(SimpleWebViewActivity.Pay_hire_failed);
                }
            }
        }


    }

    @Override
    public String doInBackground(Void... params) {
        // 返回
        if (isRunning) {
            return null;
        }
        String result=null;
        isRunning=true;
        User user = UserHelper.getInstance().getUser();

        if (user != null) {// 支付宝合作渠道没有充值金额限制
            result= pay();
        } else {
            ViewUtils.toastShort("请先登录");
        }

        return result;
    }

    private String pay() {
        String result=null;
        try {
            if (aliPayBean == null) {
                return null;
            }

            String orderInfoPrefix = URLDecoder.decode(aliPayBean.getContent(), "UTF-8");
            LogUtils.debug("orderInfoPrefix:"+orderInfoPrefix);
            String encodeStrSign = URLDecoder.decode(aliPayBean.getSign(), "UTF-8");
            LogUtils.debug("encodeStrSign:"+encodeStrSign);
            try {
                encodeStrSign = URLEncoder.encode(encodeStrSign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                ReportUtils.reportError(e);
            }

            String orderInfo = orderInfoPrefix + "&sign=" + "\"" + encodeStrSign + "\"&sign_type=\"RSA\"";

            LogUtils.debug("orderinfo:"+orderInfo);
            PayTask alipay = new PayTask(caller);

            result = alipay.pay(orderInfo, true);

        } catch (Throwable e) {
            ReportUtils.reportError(e);
        }
        return result;
    }

}
