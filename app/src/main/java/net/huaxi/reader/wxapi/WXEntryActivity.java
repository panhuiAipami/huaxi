package net.huaxi.reader.wxapi;

import android.os.Message;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import net.huaxi.reader.MyApplication;
import net.huaxi.reader.callback.FinishActivity;
import net.huaxi.reader.callback.ListenerManager;
import net.huaxi.reader.callback.ShareResult;
import net.huaxi.reader.utils.MLog;

/**
 * Created by Administrator on 2017/11/15.
 */

public class WXEntryActivity extends WXCallbackActivity {
    ShareResult result;
    FinishActivity finishActivity;

    @Override
    public void onReq(BaseReq req) {

    }

    protected void onNewIntent(android.content.Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            MLog.i("onResp111", "----errorCode = " + resp.errCode + "--------Code=" + ((SendAuth.Resp) resp).code);
            Message ms = new Message();
            ms.obj = ((SendAuth.Resp) resp).code;
            ms.what = 0;
            MyApplication.getInstance().getHandler().sendMessage(ms);
        } else {
            if (ListenerManager.getInstance().getResult() != null) {
                result = ListenerManager.getInstance().getResult();
            }
            if (ListenerManager.getInstance().getFinishActivity() != null) {
                finishActivity = ListenerManager.getInstance().getFinishActivity();
            }
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if (result != null)
                        result.success();
                    Log.i("onResp111", "-----------分享成功-------------->");
                    Log.d("ShareActivity--", "onResp");
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
            if (finishActivity != null)
                finishActivity.finishActivity();
        }
        finish();
    }
}
