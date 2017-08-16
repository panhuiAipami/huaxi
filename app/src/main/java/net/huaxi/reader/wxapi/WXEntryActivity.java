package net.huaxi.reader.wxapi;

import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.appinterface.ListenerManager;
import net.huaxi.reader.appinterface.ShareResult;
import net.huaxi.reader.util.LoginHelper;

import static net.huaxi.reader.common.AppContext.getLoginHelper;

/**
 * Created by ZMW on 2015/12/3.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        api = WXAPIFactory.createWXAPI(this, LoginHelper.WX_APP_ID);
        api.registerApp(LoginHelper.WX_APP_ID);
        api.handleIntent(getIntent(), this);
        LogUtils.debug("weixinLogin" + LoginHelper.WX_APP_ID);

    }

    @Override
    public void onReq(BaseReq req) {

    }

    protected void onNewIntent(android.content.Intent intent) {
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    ShareResult result;
    @Override
    public void onResp(BaseResp resp) {
        if (ListenerManager.getInstance().getResult() != null) {
            result = ListenerManager.getInstance().getResult();
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (result != null)
                    result.success();
//                Log.i("onResp", "-----------分享成功-------------->");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                Log.i("onResp", "-----------分享取消-------------->");
                if (result != null)
                    result.cancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                if (result != null)
                    result.faild();
//                Log.i("onResp", "-----------AUTH_DENIED-------------->");
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
//                Log.i("onResp", "-----------不支持-------------->");
                break;
            default:
//                Log.i("onResp", "-----------未知错误-------------->");
                break;
        }

        if (resp instanceof SendAuth.Resp) {
            LogUtils.debug("weixinLogin_errorCode" + resp.errCode + "");
            LogUtils.debug("weixinLogin_Code" + ((SendAuth.Resp) resp).code);
            if (StringUtils.isNotBlank(((SendAuth.Resp) resp).code)) {
                LogUtils.debug("weixinLogin 4");
                getLoginHelper().getWeiXinToken(resp.errCode, ((SendAuth.Resp) resp).code);
            }
        }
        finish();

    }


}
