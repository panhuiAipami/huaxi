package net.huaxi.reader.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.huawei.hms.activity.BridgeActivity;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.MD5Utils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterMainFragmentPager;
import net.huaxi.reader.appinterface.CallBackHuaweiPayInfo;
import net.huaxi.reader.appinterface.GoToShuJia;
import net.huaxi.reader.appinterface.ListenerManager;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.MainTabFragEnum;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.fragment.FmBookShelf;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.thread.AppCheckUpdateTask;
import net.huaxi.reader.thread.HuaWeiPayTask;
import net.huaxi.reader.util.HuaweiPayResult;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.UITabBottom;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static net.huaxi.reader.thread.HuaWeiPayTask.REQUEST_HMS_RESOLVE_ERROR;
import static net.huaxi.reader.thread.HuaWeiPayTask.REQ_CODE_PAY;

public class MainActivity extends BaseActivity implements GoToShuJia, HuaweiApiClient.OnConnectionFailedListener, HuaweiApiClient.ConnectionCallbacks, CallBackHuaweiPayInfo {
    long firstTime = 0;
    private ViewPager vpMain;
    private UITabBottom bottonTools;
    private AdapterMainFragmentPager pagerAdapter;
    private View vToolsMaskBackground;

    //华为移动服务Client
    private static HuaweiApiClient client;

    public ViewPager getVpMain() {
        return vpMain;
    }

    public View getvToolsMaskBackground() {
        return vToolsMaskBackground;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        try {
            ListenerManager.getInstance().setGoToShuJia(this);
            ListenerManager.getInstance().setBackHuaweiPayInfo(this);
            setContentView(R.layout.activity_main);
            initView();
            updateApk();
            getTaskStatus();
            //记录阅读状态，如果未正常阅读，需要重新打开。
            String lastNotFinishedBook = SharePrefHelper.getLastNotExactFinished();
            if (StringUtils.isNotEmpty(lastNotFinishedBook)) {
                EnterBookContent.openBookContent(getActivity(), lastNotFinishedBook);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.SPLASH_TO_READ);
            }
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HuaweiApiClient getConnect() {
        return client;
    }

    /**
     * 用户打开app检测是否有新版本
     * 放在异步请求中
     */
    private void updateApk() {
        try {
            long endTime = System.currentTimeMillis();
            //新版本提示更新时间间隔，如果用户点击(下次提醒我)按钮，则一天后再提示
            //如果用户没有点击，直接退出app，则用户再进入app时再次提醒版本更新
            long startTime = SharePrefHelper.getLastUpdateApkTime();
            long interval = endTime - startTime;
            //时间判断
            if (interval >= Constants.UPDATE_APK_INTERVAL) {
                SharePrefHelper.setLastUpdateApkTime(endTime);
                AppCheckUpdateTask appCheckUpdateTask = new AppCheckUpdateTask(MainActivity.this);
                appCheckUpdateTask.setFlag(false);
                appCheckUpdateTask.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        vToolsMaskBackground = findViewById(R.id.main_tools_mask_backround);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        vpMain = (ViewPager) findViewById(R.id.main_content);
        vpMain.setOffscreenPageLimit(3);
        bottonTools = (UITabBottom) findViewById(R.id.main_tools_bottom);
        pagerAdapter = new AdapterMainFragmentPager(this, getSupportFragmentManager(),
                MainTabFragEnum.values());
        vpMain.setAdapter(pagerAdapter);
        bottonTools.setmViewPager(vpMain);
        vpMain.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageIndex) {
                LogUtils.debug("onPageSelected index==" + pageIndex);
                if (pageIndex != MainTabFragEnum.bookshelf.getIndex()) {
                    tintManager.setStatusBarTintColor(getResources().getColor(R.color
                            .c01_themes_color));
                } else {
                    LogUtils.debug("onPageSelected index==" + pageIndex);
                    if (pagerAdapter.getFragment(pageIndex) != null) {
                        LogUtils.debug("onPageSelected index==" + pageIndex);
                        (((FmBookShelf) pagerAdapter.getFragment(pageIndex))).setStateBar();
                    }
                }
                bottonTools.selectTab(vpMain.getCurrentItem());
                switch (pageIndex) {
                    case 0:
                        UMEventAnalyze.countEvent(MainActivity.this, UMEventAnalyze.MAINACTIVITY_BOOKSHELF);
                        break;
                    case 1:
                        UMEventAnalyze.countEvent(MainActivity.this, UMEventAnalyze.MAINACTIVITY_BOOKSTORE);
                        break;
                    case 2:
                        UMEventAnalyze.countEvent(MainActivity.this, UMEventAnalyze.MAINACTIVITY_CLASSIFY);
                        break;
                    case 3:
                        UMEventAnalyze.countEvent(MainActivity.this, UMEventAnalyze.MAINACTIVITY_USER);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(final int pageIndex, float percent, int offset) {
                int currentIndex = vpMain.getCurrentItem();
                if (Math.abs(currentIndex - pageIndex) <= 1) {
                    bottonTools.scroll(pageIndex, percent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int status) {
                LogUtils.debug("status =" + status);
                System.out.println("onPageScrollStateChanged==" + status);
                if (status == ViewPager.SCROLL_STATE_IDLE) {
                    bottonTools.selectTab(vpMain.getCurrentItem());
                }
            }
        });
    }


    //当有请求来时，初始化tab
    private void initTabs() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int id = getIntent().getIntExtra("id", -1);
        LogUtils.debug(" getIntent() id = " + id);
        if (MainTabFragEnum.bookshelf.getIndex() == id) {
            vpMain.setCurrentItem(MainTabFragEnum.bookshelf.getIndex());
        } else if (MainTabFragEnum.bookcity.getIndex() == id) {
            vpMain.setCurrentItem(MainTabFragEnum.bookcity.getIndex());
        } else if (MainTabFragEnum.paihang.getIndex() == id) {
            vpMain.setCurrentItem(MainTabFragEnum.paihang.getIndex());
        } else if (MainTabFragEnum.person.getIndex() == id) {
            vpMain.setCurrentItem(MainTabFragEnum.person.getIndex());
        }
    }

    public void onKeyBack() {
        Fragment fragment = pagerAdapter.getFragment(vpMain.getCurrentItem());
        if (fragment != null && fragment instanceof FmBookShelf) {
            FmBookShelf fbs = (FmBookShelf) fragment;
            if (fbs.getBookShelfAdapter().getDeleteState()) {
                fbs.doTitleRightClick();
                return;
            }
        }
        if (firstTime + 2000 > System.currentTimeMillis()) {
//            MobclickAgent.onKillProcess(this);
            finish();
//            AppManager.getAppManager().AppExit();
        } else {
            ViewUtils.toastShort("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client != null)
        client.disconnect();
    }

    @Override
    public void finish() {
        EventBus.getDefault().unregister(this);
        super.finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initTabs();
    }

    @Override
    public void go() {
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vpMain.setCurrentItem(0, true);
            bottonTools.selectTab(vpMain.getCurrentItem());
        }
    };

    public static void getTaskStatus() {
        //任务是否完成接口
        String token = MD5Utils.MD5("userID=" + UserHelper.getInstance().getUserId() + "&device=android1W4h7$9+*3@");
        String url = URLConstants.shareWeiXin + "action=checkUserCompleteCurrentDayTask&userID=" + UserHelper.getInstance().getUserId() + "&device=android&_token=" + token;
        GetRequest request1 = new GetRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    if (jsonObject != null) {
                        int totalTask = jsonObject.getInt("totalTask");
                        int completeTask = jsonObject.getInt("completeTask");
                        SharePrefHelper.putBoolean(Constants.SHOW_TASK_STATUS, totalTask > completeTask);
                        if (AppContext.appContext.getHandler2() != null)
                            AppContext.appContext.getHandler2().sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(error);
            }
        });
        RequestQueueManager.addRequest(request1);
    }

    HuaweiIdSignInOptions signInOptions = new
            HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
            .requestOpenId()
            .build();

    public void connect() {
        //创建华为移动服务client实例用以实现支付功能
        //需要指定api为HuaweiPay.PAY_API
        //连接回调以及连接失败监听
        if (Constants.CHANNEL_IS_HUAWEI) {
            client = new HuaweiApiClient.Builder(getActivity())
                    .addApi(HuaweiPay.PAY_API)
                    .addApi(HuaweiId.SIGN_IN_API, signInOptions)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();

            //建议在oncreate的时候连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            client.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(HuaWeiPayTask.TAG, "HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());
        if (HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {
            final int errorCode = connectionResult.getErrorCode();
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用
                    Toast.makeText(AppContext.context(),"请先安装华为移动服务",Toast.LENGTH_LONG).show();
                    HuaweiApiAvailability.getInstance().resolveError(getActivity(), errorCode, REQUEST_HMS_RESOLVE_ERROR);
                }
            });
        } else {
            //其他错误码请参见开发指南或者API文档

        }
    }

    @Override
    public void onConnected() {
        Log.i(HuaWeiPayTask.TAG, "HuaweiApiClient 连接成功");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void info(double productPrice, String orderId, String key) {
        HuaWeiPayTask p = new HuaWeiPayTask(MainActivity.getConnect(), new HuaweiPayResult(this, REQ_CODE_PAY));
        p.pay(key, "花溪小说", "充值", productPrice, orderId);
    }


    /**
     * 当用户未登录或者未授权，调用signin接口拉起对应的页面处理完毕后会将结果返回给当前acity处理
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //连接失败处理
        if (requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra(BridgeActivity.EXTRA_RESULT, 0);

                if (result == ConnectionResult.SUCCESS) {
                    Log.i(HuaWeiPayTask.TAG, "错误成功解决");
                    if (!client.isConnecting() && !client.isConnected()) {
                        client.connect();
                    }
                } else if (result == ConnectionResult.CANCELED) {
                    Log.i(HuaWeiPayTask.TAG, "解决错误过程被用户取消");
                } else if (result == ConnectionResult.INTERNAL_ERROR) {
                    Log.i(HuaWeiPayTask.TAG, "发生内部错误，重试可以解决");
                    //CP可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                } else {
                    Log.i(HuaWeiPayTask.TAG, "未知返回码");
                }
            } else {
                Log.i(HuaWeiPayTask.TAG, "调用解决方案发生错误");
            }
        } else if (requestCode == REQ_CODE_PAY) {//连接成功
            //当返回值是-1的时候表明用户支付调用调用成功
            if (resultCode == Activity.RESULT_OK) {
                //获取支付完成信息
                PayResultInfo payResultInfo = HuaweiPay.HuaweiPayApi.getPayResultInfoFromIntent(data);
                if (payResultInfo != null) {
                    Map<String, Object> paramsa = new HashMap<>();
                    if (PayStatusCodes.PAY_STATE_SUCCESS == payResultInfo.getReturnCode()) {

                        paramsa.put("returnCode", payResultInfo.getReturnCode());
                        paramsa.put("userName", payResultInfo.getUserName());
                        paramsa.put("requestId", payResultInfo.getRequestId());
                        paramsa.put("amount", payResultInfo.getAmount());
                        paramsa.put("time", payResultInfo.getTime());

                        String orderId = payResultInfo.getOrderID();
                        if (!TextUtils.isEmpty(orderId)) {
                            paramsa.put("orderID", orderId);
                        }
                        String withholdID = payResultInfo.getWithholdID();
                        if (!TextUtils.isEmpty(withholdID)) {
                            paramsa.put("withholdID", withholdID);
                        }
                        String errMsg = payResultInfo.getErrMsg();
                        if (!TextUtils.isEmpty(errMsg)) {
                            paramsa.put("errMsg", errMsg);
                        }

                        String noSigna = HuaWeiPayTask.getNoSign(paramsa);
                        boolean success = HuaWeiPayTask.doCheck(noSigna, payResultInfo.getSign());

                        if (success) {
                            Log.i(HuaWeiPayTask.TAG, "支付/订阅成功");
                        } else {
                            //支付成功，但是签名校验失败。CP需要到服务器上查询该次支付的情况，然后再进行处理。
                            Log.i(HuaWeiPayTask.TAG, "支付/订阅成功，但是签名验证失败");
                        }

                        Log.i(HuaWeiPayTask.TAG, "商户名称: " + payResultInfo.getUserName());
                        if (!TextUtils.isEmpty(orderId)) {
                            Log.i(HuaWeiPayTask.TAG, "订单编号: " + orderId);
                        }
                        Log.i(HuaWeiPayTask.TAG, "支付金额: " + payResultInfo.getAmount());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        String time = payResultInfo.getTime();
                        if (time != null) {
                            try {
                                Date curDate = new Date(Long.valueOf(time));
                                String str = formatter.format(curDate);
                                Log.i(HuaWeiPayTask.TAG, "交易时间: " + str);
                            } catch (NumberFormatException e) {
                                Log.i(HuaWeiPayTask.TAG, "交易时间解析出错 time: " + time);
                            }
                        }
                        Log.i(HuaWeiPayTask.TAG, "商户订单号: " + payResultInfo.getRequestId());
                    } else if (PayStatusCodes.PAY_STATE_CANCEL == payResultInfo.getReturnCode()) {
                        //支付失败，原因是用户取消了支付，可能是用户取消登录，或者取消支付
                        Log.i(HuaWeiPayTask.TAG, "0支付失败：用户取消" + payResultInfo.getErrMsg());
                    } else {
                        //支付失败，其他一些原因
                        Log.i(HuaWeiPayTask.TAG, "支付失败：" + payResultInfo.getErrMsg());
                    }
                } else {
                    //支付失败
                }
            } else {
                //当resultCode 为0的时候表明用户未登录，则CP可以处理用户不登录事件
                Log.i(HuaWeiPayTask.TAG, "resultCode为0, 用户未登录 CP可以处理用户不登录事件");
            }
        }
    }
}
