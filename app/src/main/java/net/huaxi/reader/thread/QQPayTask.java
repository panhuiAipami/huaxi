package net.huaxi.reader.thread;

import android.app.Activity;

import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.data.pay.PayApi;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.bean.QQPayBean;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.util.LoginHelper;

/**
 * @Description: [QQ支付的功能实现类]
 * @Author: [Saud]
 * @CreateDate: [16/8/23 15:40]
 * @UpDate: [16/8/23 15:40]
 * @Version: [v1.0]
 */
public class QQPayTask extends EasyTask<Activity, Void, Void, PayApi> {


    public static volatile boolean isRunning = false;
    private IOpenApi openApi;
    private QQPayBean qqPayBean;
    final String BARGAINOR_ID = "1205937701";//商户号
    private int paySerial = 1;

    public QQPayTask(Activity caller, QQPayBean qqPayBean, String pid) {
        super(caller);
        this.qqPayBean = qqPayBean;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(PayApi api) {
        isRunning = false;
        if (api.checkParams()) {
            openApi.execApi(api);
        }
    }


    @Override
    public PayApi doInBackground(Void... params) {
        // 返回
        PayApi api = null;
        if (isRunning) {
            return null;
        }
        isRunning = true;
        User user = UserHelper.getInstance().getUser();
        openApi = OpenApiFactory.getInstance(caller, LoginHelper.QQLOGIN_APP_ID);
        if (user != null) {// 支付宝合作渠道没有充值金额限制
            api = new PayApi();
            api.appId = LoginHelper.QQLOGIN_APP_ID;
            api.callbackScheme = "qwallet" + LoginHelper.QQLOGIN_APP_ID;
            api.bargainorId = BARGAINOR_ID;
            api.tokenId = qqPayBean.getVdata().getToken_id();
            api.nonce = qqPayBean.getVdata().getNonce();
            api.timeStamp = qqPayBean.getVdata().getTimestamp();
            api.sig = qqPayBean.getVdata().getSign();
            api.sigType = qqPayBean.getVdata().getSig_type();
            api.serialNumber = "" + paySerial++;
            api.pubAcc = "";
            api.pubAccHint = "";
        } else {
            ViewUtils.toastShort("请先登录");
        }
        return api;
    }

}
