/**
 *
 */
package net.huaxi.reader.common;

import android.app.Activity;
import android.app.Dialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.constants.OpenConstants;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;
import com.tools.commonlibs.utils.JsonUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.AliPayBean;
import net.huaxi.reader.bean.QQPayBean;
import net.huaxi.reader.bean.WXPayOrder;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.thread.AlipayTask;
import net.huaxi.reader.thread.QQPayTask;
import net.huaxi.reader.thread.WXPayTask;
import net.huaxi.reader.util.EncodeUtils;
import net.huaxi.reader.util.LoginHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * 支付工具
 */
public class PayUtils {

    public static final int PRODUCT_TYPE_COIN = 1;
    private static IOpenApi openApi;

    public static void AliPay(final Activity activity, final String money, String body, final String pid,
                              String checksum, final Dialog dialog) {
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("pr_id", EncodeUtils.encodeString_UTF8(pid));
        map.put("quantity", "1");
        map.put("total_fee", EncodeUtils.encodeString_UTF8(money));
        map.put("body", EncodeUtils.encodeString_UTF8(body));
        map.put("checksum", EncodeUtils.encodeString_UTF8(checksum));
        PostRequest request = new PostRequest(URLConstants.PAY_ALIPAY, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)  {
                if (!ResponseHelper.isSuccess(response)) {
                    return;
                }
                if (dialog != null)
                    dialog.cancel();
                LogUtils.debug("response==" + response.toString());
                AliPayBean aliPayBean = JsonUtils.fromJson(ResponseHelper.getVdata(response)
                        .toString(), AliPayBean.class);
                LogUtils.debug("alipayString...."+ResponseHelper.getVdata(response).toString());
                try {
                    AlipayTask task = new AlipayTask(activity,aliPayBean,pid);
                } catch (UnsupportedEncodingException e) {


                }

//                AlipayWalletPayTask alipayTask = new AlipayWalletPayTask(activity,
//                        aliPayBean, pid);
//                alipayTask.execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort("生成支付宝订单失败");
                if (dialog != null)
                    dialog.cancel();
            }
        }, map);
        RequestQueueManager.addRequest(request.setShouldCache(false));
    }

    /**
     * 微信支付
     *
     * @param activity
     * @param money
     * @param body
     * @param pid
     * @param checksum
     * @param dialog
     */
    public static void WxPay(final Activity activity, final String money, String body, final String pid,
                             String checksum, final Dialog dialog) {
        if ("".equals(money)) {
            return;
        }
        LoginHelper helper = new LoginHelper(activity);
        if (!helper.isInstallWXClient()) {
            ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_install_wx_client));
            return;
        }
        //修改微信登录传递的参数
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("pr_id", EncodeUtils.encodeString_UTF8(pid));
        map.put("quantity", "1");
        map.put("total_fee", EncodeUtils.encodeString_UTF8(money));
        map.put("body", EncodeUtils.encodeString_UTF8(body));
        map.put("checksum", EncodeUtils.encodeString_UTF8(checksum));
        PostRequest requeset = new PostRequest(URLConstants.PAY_WXPAY, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("pay resposne=" + response.toString());
                if (dialog != null)
                    dialog.cancel();
                if (!ResponseHelper.isSuccess(response)) {
                    return;
                }
                WXPayOrder order = JsonUtils.fromJson(ResponseHelper.getVdata(response).toString
                        (), WXPayOrder.class);
                int errorid = ResponseHelper.getErrorId(response);
                if (10100 == errorid) {
                    ViewUtils.toastShort("创建订单失败");
                    return;
                }
                if (order == null) {
                    ViewUtils.toastShort("创建订单失败");
                    return;
                }

                WXPayTask task = new WXPayTask(activity, order, pid);
                task.execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null)
                    dialog.cancel();
                ViewUtils.toastShort("创建订单失败");
            }
        }, map);
        RequestQueueManager.addRequest(requeset);

        return;
    }


    /**
     * QQ支付
     *
     * @param activity
     * @param money
     * @param body
     * @param pid
     * @param checksum
     * @param dialog
     */
    public static void QQPay(final Activity activity, final String money, String body, final String pid,
                             String checksum, final Dialog dialog) {
        if ("".equals(money)) {
            if (dialog != null) {
                dialog.dismiss();
            }
            return;
        }

        if (openApi == null) {
            openApi = OpenApiFactory.getInstance(activity, LoginHelper.QQLOGIN_APP_ID);
        }

        if (!openApi.isMobileQQInstalled()) {
            ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_install_qq_client));
            return;
        }

        if (!openApi.isMobileQQSupportApi(OpenConstants.API_NAME_PAY)) {
            ViewUtils.toastShort(AppContext.getInstance().getString(R.string.not_support_qq_client));
            return;
        }

        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("pr_id", pid);
        map.put("quantity", "1");
        map.put("total_fee", money);
        map.put("body", body);
        map.put("checksum", checksum);
        final PostRequest requeset = new PostRequest(URLConstants.PAY_QQ, new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("pay resposne=" + response.toString());
                if (dialog != null) {
                    dialog.cancel();
                }
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == 0) {
                    Type type = new TypeToken<QQPayBean>() {
                    }.getType();
                    QQPayBean order = new Gson().fromJson(response.toString(), type);
                    new QQPayTask(activity, order, pid).execute();
                } else {
                    ViewUtils.toastShort("创建订单失败(" + errorid + ")");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null)
                    dialog.cancel();
                ViewUtils.toastShort("创建订单失败");
            }
        }, map, "1.2");
        RequestQueueManager.addRequest(requeset);

        return;
    }


    /**
     * 支付宝客户端支付成功，微信支付成功，短信支付成功后，调用此代码，调转到相应章节购买！
     */
    public static void successPayCallerBack(Activity caller) {
        caller.finish();
//			Intent intent = new Intent(caller, MainActivity.class);
//			intent.putExtra("id", MainTabFragEnum.person.getIndex());
//			caller.startActivity(intent);
    }

}
