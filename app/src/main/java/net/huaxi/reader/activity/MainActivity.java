package net.huaxi.reader.activity;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.MD5Utils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterMainFragmentPager;
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
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.UITabBottom;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements GoToShuJia {

    long firstTime = 0;
    private ViewPager vpMain;
    private UITabBottom bottonTools;
    private AdapterMainFragmentPager pagerAdapter;
    private View vToolsMaskBackground;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                new AppCheckUpdateTask(MainActivity.this).execute();
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

//    @Subscribe
//    public void onEvent(EventBusUtil.EventBean eventBean) {
//        if (EventBusUtil.EVENTMODEL_MIGRATE == eventBean.getModle() && EventBusUtil.EVENTTYPE_MIGRATE_SHOW_ONE == eventBean.getType()) {
//            if (mUserMigrateHelper != null && isNeedShowAgain) {
//                isNeedShowAgain = false;//标记防止多次接收消息，多次show出来
//                mUserMigrateHelper.startMigrate();
//            }
//        }
//    }

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


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mUserMigrateHelper = null;
//        if (mIMigrateUserService != null) {
//            unbindService(mConnection);
//        }
//    }

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
}
