package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.appinterface.onCatalogLoadFinished;
import net.huaxi.reader.book.BookContentBottomView;
import net.huaxi.reader.book.BookContentModel;
import net.huaxi.reader.book.BookContentView;
import net.huaxi.reader.book.ReadPageFactory;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.book.paging.PagingManager;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.dialog.RechargeDialog;
import net.huaxi.reader.https.BookCatalogThreadLoader;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.ScreenLightUtils;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.util.listener.ScreenUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;

/**
 * function:    书籍阅读
 * author:     ryantao
 * create:     15/11/11
 * modtime:
 */
public class BookContentActivity extends BaseActivity {

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
        //设置无标题∑
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        ScreenUtils.fullScreen(this);
        tintManager.setStatusBarTintEnabled(false);
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
        ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_READING);
        if (ReadPageState.BOOKTYPE_NORMAL != mBookContentModel.getBookState()) {
            if (ReadPageState.BOOKTYPE_RECHARGE == mBookContentModel.getBookState()) {
                LogUtils.debug("开始自动订阅!" + mBookContentModel.getBookState());
                gotoPay();
            } else {
                LogUtils.debug("刷新当前页面[GET]" + mBookContentModel.getBookState());
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
                ReadPageFactory.getSingleton().changeAutoSub();
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
     * 加载内容
     */
    private void loadContent() {
        mBookContentModel.blockBookContentView(false);
        if (DataSourceManager.getSingleton().getChapterCount() > 0) {
            Log.d("loadContent........", "loadContent: "+DataSourceManager.getSingleton().toString());
            ReadPageFactory.getSingleton().refreshPage();


        } else {
            refreshCatalog();
        }
    }

    private void clickButton() {
        int bookState = mBookContentModel.getBookState();
        LogUtils.debug("bookState:"+bookState);
        switch (bookState) {
            case ReadPageState.BOOKTYPE_ORDER_PAY:
                gotoPay();
                break;
            case ReadPageState.BOOKTYPE_RECHARGE:
                gotoRecharge();
                break;
            case ReadPageState.BOOKTYPE_UNLOGIN:
                gotoLogin();
                break;
            default:
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
            ReadPageFactory.getSingleton().pay();
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
        final String action = ReadPageFactory.getSingleton().isAutoSub() ? "b_add" : "b_del";
        Map<String, String> allParams = new HashMap<String, String>();
        allParams.put("u_action", action);
        allParams.put("bk_mid", DataSourceManager.getSingleton().getBookId());
        allParams.putAll(CommonUtils.getPublicPostArgs());
        PostRequest autoSubRequest = new PostRequest(URLConstants.USER_AUTO_SUB_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    LogUtils.debug("post auto sub = " + response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(error);
            }
        }, allParams);
        RequestQueueManager.addRequest(autoSubRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBookContentModel != null) {
            mBookContentModel.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        loadContent();
            refreshCatalog();
            //初始化阅读页面的屏幕亮度
            ScreenLightUtils.initScreenBright(this);


            //
            ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_READING);
            if (ReadPageState.BOOKTYPE_NORMAL != mBookContentModel.getBookState()) {
                if (ReadPageState.BOOKTYPE_RECHARGE == mBookContentModel.getBookState()) {
                    LogUtils.debug("开始自动订阅!" + mBookContentModel.getBookState());
                    gotoPay();
                } else {
                    LogUtils.debug("刷新当前页面[GET]" + mBookContentModel.getBookState());
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

}
