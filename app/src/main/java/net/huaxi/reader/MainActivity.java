package net.huaxi.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.listener.ChannelListener;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.huaxi.reader.activity.ShareActivity;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.dialog.ShareDialog;
import net.huaxi.reader.utils.LoginHelper;
import net.huaxi.reader.utils.PreferenceHelper;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;
import static com.spriteapp.booklibrary.ui.activity.HomeActivity.libActivity;
import static com.spriteapp.booklibrary.util.ToastUtil.showToast;

public class MainActivity extends AppCompatActivity {
    public static final String SEXTIME = "sextime";
    private static final String TAG = "MainActivity";
    public static final String SIGN_SECRET = "fygopf7cixub8cpkh1oruik2byt2ykvkh81sy6";
    public static final int CLIENT_ID = 40;
    private static final int CHANNEL_ID = 2;
    public static final String SHAREDATA = "share_data";
    private ShareDialog shareDialog;

    private ShareBean shareBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceHelper.putBoolean(SEXTIME, false);//存入本地性别选择
        HuaXiConfig config = new HuaXiConfig.Builder().setContext(this)
                .setChannelListener(new ChannelListener() {
                    @Override
                    public void toLoginPage(Context context) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LoginHelper.login_back);
                    }

                    @Override
                    public void showShareDialog(Context context, BookDetailResponse shareDetail, boolean isNightMode) {
//                        Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
                        if (shareDetail != null) {
                            shareBean = new ShareBean();
                            shareBean.setTitle(shareDetail.getBook_name().isEmpty() ? "" : shareDetail.getBook_name());
                            shareBean.setDesc(shareDetail.getBook_intro().isEmpty() ? "" : shareDetail.getBook_intro());
                            shareBean.setImgUrl(shareDetail.getBook_image().isEmpty() ? "" : shareDetail.getBook_image());
                            shareBean.setShareUrl(shareDetail.getBook_share_url().isEmpty() ? "" : shareDetail.getBook_share_url());
                            shareBean.setNid(shareDetail.getBook_id());

//                        ListenerManager.getInstance().getShareBeanCallBack().getShareBean(shareBean);
                            Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(SHAREDATA, shareBean);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void toWXPay(WeChatBean response) {
                        if (response == null) {
                            return;
                        }
                        wxPay(response);
                    }
                })
                .setChannelId(CHANNEL_ID)
                .setClientId(CLIENT_ID)
                .setSex(PreferenceHelper.getInt(SEX, 0))//性别
                .setSignSecret(SIGN_SECRET).build();
        HuaXiSDK.getInstance().init(config);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void initShareDialog() {
        if (shareBean.getNid() == 0 && shareBean == null)
            return;
        if (shareDialog != null) {
            shareDialog.dismiss();
            shareDialog = null;
        }
        shareDialog = new ShareDialog(this, shareBean);
        shareDialog.show();
    }

    public void wxPay(WeChatBean response) {//微信支付
        IWXAPI WXApi = WXAPIFactory.createWXAPI(this, LoginHelper.WX_APP_ID, true);
//        WXApi.registerApp(LoginHelper.WX_APP_ID);
//        PayReq req = new PayReq();
//        req.appId = LoginHelper.WX_APP_ID;
//        req.partnerId = response.getParams().getApp_id();
//        req.extData = response.getAmount() + "";
//        req.signType = response.getParams().getSign_type();
//        req.sign = response.getParams().getSign();
//        req.timeStamp = response.getParams().getTimestamp();
//        req.nonceStr = response.getParams().getCharset();
        if (WXApi.isWXAppInstalled()) {
            RequestMsg msg = new RequestMsg();
            msg.setTokenId(response.getToken_id());
            msg.setTradeType(MainApplication.WX_APP_TYPE);
            msg.setAppId(LoginHelper.WX_APP_ID);
            PayPlugin.unifiedAppPay(libActivity, msg);
        } else {
            showToast("请先安装微信");
        }

    }
}
