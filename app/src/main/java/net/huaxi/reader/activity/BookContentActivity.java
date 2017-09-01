package net.huaxi.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.appinterface.CallBackHuaweiPayInfo;
import net.huaxi.reader.appinterface.ListenerManager;
import net.huaxi.reader.appinterface.onCatalogLoadFinished;
import net.huaxi.reader.book.BookContentBottomView;
import net.huaxi.reader.book.BookContentModel;
import net.huaxi.reader.book.BookContentView;
import net.huaxi.reader.book.ReadPageFactory;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.book.paging.PagingManager;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.dialog.RechargeDialog;
import net.huaxi.reader.https.BookCatalogThreadLoader;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.thread.HuaWeiPayTask;
import net.huaxi.reader.util.HuaweiPayResult;
import net.huaxi.reader.util.ScreenLightUtils;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.util.listener.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;

import static net.huaxi.reader.thread.HuaWeiPayTask.REQ_CODE_PAY;


/**
 * function:    书籍阅读
 * author:     ryantao
 * create:     15/11/11
 * modtime:
 */
public class BookContentActivity extends BaseActivity implements CallBackHuaweiPayInfo {
    /**
     * 是否自动订阅下一章
     */
    public static  String IS_DINGYUE = null;
    public static final int FROM_RECHARGE = 1021;
    public static final int FROM_LOGIN = 1022;
    /*  Bitmap对象 */
    BookContentView mBookContentView;
    BookContentModel mBookContentModel;
    BookContentBottomView mBookBottomView;
    Button buyBtn;  //购买按钮;
    Button autoSubBtn;  //自动订阅
    Button openVipBtn;  //开通会员
    Button closeBtn; //关闭按钮
    private JavaScript javaScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListenerManager.getInstance().setBackHuaweiPayInfo(this);
        //设置全屏
        ScreenUtils.fullScreen(this);
        setContentView(R.layout.activity_bookcontent);
        initViews();
        parseIntent(getIntent());
//        loadContent();
        refreshCatalog();
        //初始化阅读页面的屏幕亮度
        ScreenLightUtils.initScreenBright(this);
    }

    private void parseIntent(Intent intent) {
        //解析书籍ID，并传递给DS；
        String bookId = intent.getStringExtra(EnterBookContent.BOOK_ID);
        IS_DINGYUE = bookId;
        BookTable bookTable = BookDao.getInstance().findBook(bookId);
        if (bookTable == null) {
//            toast(getString(R.string.readpage_not_find_book));
            finish();
            return;
        }
        DataSourceManager.getSingleton().setBook(bookId, bookTable.getName(), bookTable.getAuthorName(), bookTable.getIsMonthly() == 1);
        //记录当前阅读状态，结束阅读需要清除。
        SharePrefHelper.setLastNotExactFinished(bookId);
        String chapterId = getIntent().getStringExtra(EnterBookContent.CHAPTER_ID);
        if (StringUtils.isNotEmpty(chapterId)) {
            DataSourceManager.getSingleton().resetLastReadLocation(chapterId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReadPageFactory.getSingleton().changeAutoSub(false);
        //----------------------------------------------------------
        ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_READING);
        //不是阅读状态
        if (ReadPageState.BOOKTYPE_NORMAL != mBookContentModel.getBookState()) {
            //是充值状态
            if (ReadPageState.BOOKTYPE_RECHARGE == mBookContentModel.getBookState()) {
                LogUtils.debug("开始自动订阅!" + mBookContentModel.getBookState());
                gotoPay();
            } else {
                LogUtils.debug("刷新当前页面[GET]" + mBookContentModel.getBookState());
                //登录阅读
                if (ReadPageState.BOOKTYPE_UNLOGIN == mBookContentModel.getBookState()) {
//                    if (!Constants.DEFAULT_USERID.equals(UserHelper.getInstance().getUserId())) {
//                        SwitchUserMigrateTask task = new SwitchUserMigrateTask(BookContentActivity.this);
//                        task.execute();
//                        return;
//                    }
                }
                clearAndRefresh();
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mBookContentView = (BookContentView) findViewById(R.id.bookcontent);
        mBookBottomView = (BookContentBottomView) findViewById(R.id.bookbottom);
        mBookContentModel = new BookContentModel(getActivity(), mBookContentView);
        mBookContentModel.setBookContentBottom(mBookBottomView);
        buyBtn = (Button) findViewById(R.id.readpage_pay_button);
        buyBtn.setBackgroundResource(android.R.color.transparent);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton();
                UMEventAnalyze.countEvent(AppContext.context(),UMEventAnalyze.READPAGE_ORDER_OK);
            }
        });
        autoSubBtn = (Button) findViewById(R.id.readpage_pay_auto_sub);
        autoSubBtn.setBackgroundResource(android.R.color.transparent);
        autoSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPageFactory.getSingleton().changeAutoSub(true);
            }
        });
        openVipBtn = (Button) findViewById(R.id.readpage_openvips);
        openVipBtn.setBackgroundResource(android.R.color.transparent);
        openVipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserHelper.getInstance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intent = new Intent(getActivity(), SimpleWebViewActivity.class);
                intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_VIP);
                startActivity(intent);
            }
        });
        closeBtn = (Button) findViewById(R.id.readpage_close);
        closeBtn.setBackgroundResource(android.R.color.transparent);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookContentModel.onBackPressed();
            }
        });
        mBookContentModel.setDrawBtn(buyBtn);
        mBookContentModel.setAutoSubBtn(autoSubBtn);
        mBookContentModel.setOpenVipBtn(openVipBtn);
        mBookContentModel.setCloseBtn(closeBtn);
        //设置阅读页加载回调监听.
        ReadPageFactory.getSingleton().setBookContentLoadedListener(mBookContentModel);

        // FIXME: 16/3/21 这里是不是可以设置成对高版本机型设置硬件加速，提高动画流畅度，提高阅读体验。
        /** 设置View不使用硬件加速 */
//        try {
//            Class<View> c = View.class;
//            Method setLayerTypeMethod = c.getDeclaredMethod("setLayerType", int.class, Paint.class);
//            if (setLayerTypeMethod != null) {
//                int layerType = 1; // View.LAYER_TYPE_SOFTWARE
//                setLayerTypeMethod.invoke(this, layerType, null);
//            }
//        } catch (Exception ignored) {
//        }
    }


    /***
     * 获取目录返回结果
     * 阅读详情也有这结果回掉
     */
    private void refreshCatalog() {
        mBookContentModel.blockBookContentView(false);
        ReadPageFactory.getSingleton().drawStateLoading(ReadPageFactory.getSingleton().getNewCanvas());
        BookCatalogThreadLoader help = new BookCatalogThreadLoader(this, DataSourceManager.getSingleton().getBookId(), new
                onCatalogLoadFinished() {
                    @Override
                    public void onFinished(List<ChapterTable> chapterTables, int rus) {
                        boolean hasExistChapter = false;
                        if (chapterTables != null && chapterTables.size() > 0) {
                            hasExistChapter = true;
                            for (int i = 0; i < chapterTables.size(); i++) {
                                ChapterTable chapterTable = chapterTables.get(i);
                                DataSourceManager.getSingleton().addCatalog(i + 1, chapterTable);
                            }
                        }
                        if (hasExistChapter) {
                            mBookContentModel.setIsInitCatalog(true);
                            loadContent();
                        } else {
                            int errorCode = ReadPageState.BOOKTYPE_ERROR;
                            if (XSErrorEnum.NETWORK_UNAVAILABLE.getCode() == rus) {
                                errorCode = ReadPageState.BOOKTYPE_NONE_NETWORK;
                            }
                            ReadPageFactory.getSingleton().loadFailed(errorCode);
                            String errorMsg = String.format(getString(R.string.readpage_catalog_download_error), rus) + "(" + errorCode + ")";
                            toast(errorMsg);
                            ReportUtils.reportError(new Throwable(errorMsg));
                        }
                    }
                });
        help.execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBookContentModel.startListener();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookContentModel.stopListener();
        mBookContentModel.onRelease();
        mBookContentModel = null;
        //置空书籍ID
        DataSourceManager.getSingleton().setBook("", "", "", false);
        //释放内容.
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            mBookContentModel.onBackPressed();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            mBookContentModel.showMenu();
        }
        return true;
    }

    /**
     * 加载阅读内容
     */
    private void loadContent() {
        mBookContentModel.blockBookContentView(false);
        //章节大于0去加载内容
        if (DataSourceManager.getSingleton().getChapterCount() > 0) {
            Log.d("loadContent........", "loadContent: "+DataSourceManager.getSingleton().toString());
            ReadPageFactory.getSingleton().refreshPage();
        } else {
            refreshCatalog();
        }
    }

    /**
     * 订阅按钮状态及点击事件
     */
    private void clickButton() {
        int bookState = mBookContentModel.getBookState();
        LogUtils.debug("bookState:"+bookState);
        switch (bookState) {
            case ReadPageState.BOOKTYPE_ORDER_PAY:
                gotoPay();//去订阅
                break;
            case ReadPageState.BOOKTYPE_RECHARGE://充值
                gotoRecharge();
                break;
            case ReadPageState.BOOKTYPE_UNLOGIN://登录阅读
                gotoLogin();
                break;
            default://清空当前页，然后刷新.
                clearAndRefresh();
                break;
        }
    }

    /**
     * 进入充值界面.
     */
    private void gotoRecharge() {
        javaScript = new JavaScript(BookContentActivity.this,null);
        //跳转到充值界面Activity
//        Intent intent = new Intent(getActivity(), SimpleWebViewActivity.class);
//        intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_RECHARGE);
//        startActivityForResult(intent, FROM_RECHARGE);
        //弹出充值Dialog
        final RechargeDialog rechargeDialog=new RechargeDialog(this);
        rechargeDialog.show();
        rechargeDialog.getDialog().findViewById(R.id.dialgo_Recharge1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                javaScript.pay("1","2","","3000","充值","1111");
                        rechargeDialog.cancel();
            }
        });
        rechargeDialog.getDialog().findViewById(R.id.dialgo_Recharge2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                javaScript.pay("1","2","","5000","充值","1111");
                rechargeDialog.cancel();
            }
        });
        rechargeDialog.getDialog().findViewById(R.id.dialgo_Recharge3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                javaScript.pay("1","2","","9800","充值","1111");
                rechargeDialog.cancel();
            }
        });
    }

    private void gotoLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, FROM_LOGIN);
        UMEventAnalyze.countEvent(BookContentActivity.this, UMEventAnalyze.LOGIN_READPAGE_TO_LOGIN);
    }

    /**
     * 清空当前页，然后刷新.
     */
    @DebugLog
    public void clearAndRefresh() {
        BookTable bookTable = DataSourceManager.getSingleton().getLastReadRecord();
        if (bookTable != null && StringUtils.isNotEmpty(bookTable.getLastReadChapter())) {
            PagingManager.getSingleton().resetPageCache(bookTable.getLastReadChapter());
        }
        loadContent();
    }

    /**
     * 订阅当前界面
     */
    private void gotoPay() {
        BookTable bookTable = DataSourceManager.getSingleton().getLastReadRecord();
        if (bookTable != null && StringUtils.isNotEmpty(bookTable.getLastReadChapter())) {
            postAutoSub();

            PagingManager.getSingleton().resetPageCache(bookTable.getLastReadChapter());
            ReadPageFactory.getSingleton().pay();//订阅
        }
    }

    /**
     * 异步设置自动订阅功能.
     */
    private void postAutoSub() {
        LogUtils.debug("Here AutoSub");
        if (Constants.DEFAULT_USERID.equals(UserHelper.getInstance().getUserId())) {
            LogUtils.info("用户未登录，不提交订阅功能");
            return;
        }
        if (!ReadPageFactory.getSingleton().hasChangeAutoSub()) {
            LogUtils.debug("为改变autosub的状态，");
            return;
        }
//        final String action = ReadPageFactory.getSingleton().isAutoSub() ? "b_add" : "b_del";
//        Map<String, String> allParams = new HashMap<String, String>();
//        allParams.put("u_action", action);
//        allParams.put("bk_mid", DataSourceManager.getSingleton().getBookId());
//        allParams.putAll(CommonUtils.getPublicPostArgs());
//        PostRequest autoSubRequest = new PostRequest(URLConstants.USER_AUTO_SUB_URL, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                if (response != null) {
//                    LogUtils.debug("post auto sub = " + response.toString());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                ReportUtils.reportError(error);
//            }
//        }, allParams);
//        RequestQueueManager.addRequest(autoSubRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBookContentModel != null) {
            mBookContentModel.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PAY) {//连接成功
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
                                //华为充值成功后，是充值状态，刷新
                                if (ReadPageState.BOOKTYPE_RECHARGE == mBookContentModel.getBookState()) {
                                    LogUtils.debug("开始自动订阅!" + mBookContentModel.getBookState());
                                    gotoPay();
                                } else {
                                    LogUtils.debug("刷新当前页面[GET]" + mBookContentModel.getBookState());
                                    //登录阅读
                                    if (ReadPageState.BOOKTYPE_UNLOGIN == mBookContentModel.getBookState()) {
                                    }
                                    clearAndRefresh();
                                }
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
                        Log.i(HuaWeiPayTask.TAG, "1支付失败：用户取消" + payResultInfo.getErrMsg());
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



        if(UserHelper.getInstance().isLogin()){
//            ReadPageFactory singleton = ReadPageFactory.getSingleton();
//            singleton.getBalanceAndDraw(new Canvas(),new PageContent(),true);
//            onCreate(null);
            //刷新
//            mBookBottomView.postInvalidate();
//            mBookContentView.postInvalidate();
            //重新加载布局文件
            setContentView(R.layout.activity_bookcontent);
            //重新加载控件
            initViews();
            parseIntent(getIntent());

            refreshCatalog();
            //初始化阅读页面的屏幕亮度
            ScreenLightUtils.initScreenBright(this);


            //----------------------------------------------------------
            ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_READING);
            //不是阅读状态
            if (ReadPageState.BOOKTYPE_NORMAL != mBookContentModel.getBookState()) {
                //是充值状态
                if (ReadPageState.BOOKTYPE_RECHARGE == mBookContentModel.getBookState()) {
                    LogUtils.debug("开始自动订阅!" + mBookContentModel.getBookState());
                    gotoPay();
                } else {
                    LogUtils.debug("刷新当前页面[GET]" + mBookContentModel.getBookState());
                    //登录阅读
                    if (ReadPageState.BOOKTYPE_UNLOGIN == mBookContentModel.getBookState()) {
//                    if (!Constants.DEFAULT_USERID.equals(UserHelper.getInstance().getUserId())) {
//                        SwitchUserMigrateTask task = new SwitchUserMigrateTask(BookContentActivity.this);
//                        task.execute();
//                        return;
//                    }
                    }
                    clearAndRefresh();
                }
            }
        }
    }

    @Override
    public void info(double productPrice, String orderId, String key) {
        HuaWeiPayTask p = new HuaWeiPayTask(MainActivity.getConnect(),new HuaweiPayResult(getActivity(),REQ_CODE_PAY));
        p.pay(key,"花溪小说","充值",productPrice,orderId);
    }
}
