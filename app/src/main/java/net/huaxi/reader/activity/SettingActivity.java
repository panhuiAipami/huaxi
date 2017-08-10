package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.PhoneUtils;
import com.tools.commonlibs.tools.Utils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.AppVersion;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.dialog.CommonDialogFoot;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.model.version.AppVersionHelper;
import net.huaxi.reader.model.version.AppVersionService;
import net.huaxi.reader.thread.AppCheckUpdateTask;
import net.huaxi.reader.thread.ClearCacheTask;
import net.huaxi.reader.thread.GetCacheTask;
import net.huaxi.reader.util.EventBusUtil;
import net.huaxi.reader.util.UMEventAnalyze;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;


public class SettingActivity extends BaseActivity implements View.OnClickListener {
    public TextView tvClearCache;
    private ImageView ivBack;
    private RelativeLayout rlAboutWe, rlPaise, rlClearCache, rlCheckVersion, rlContentPreference;
    private TextView tvLogout;
    private ToggleButton tbAutoSubscription, tbWarnIsopen, tbNeedRecentlyRecord;
    private MaterialDialog progressDeterminateDialog;
    private AppVersion appVersion;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到view视图窗口
        Window window = getActivity().getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.c01_themes_color));
        setContentView(R.layout.activity_setting);
//        StatusBarCompat.setStatusBarColor(this, R.color.c01_themes_color, false);
//        EventBus.getDefault().register(this);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        tbWarnIsopen = (ToggleButton) findViewById(R.id.setting_warn_isopen_togglebutton);
        tbAutoSubscription = (ToggleButton) findViewById(R.id.setting_auto_subscription_togglebutton);
        tvLogout = (TextView) findViewById(R.id.setting_logout_textview);
        ivBack = (ImageView) findViewById(R.id.setting_back_imageview);
        rlAboutWe = (RelativeLayout) findViewById(R.id.setting_aboutwe_layout);
        rlPaise = (RelativeLayout) findViewById(R.id.setting_paise_layout);
//        rlReport = (RelativeLayout) findViewById(R.id.setting_report_layout);
        rlClearCache = (RelativeLayout) findViewById(R.id.setting_clear_cache_layout);
        tvClearCache = (TextView) findViewById(R.id.setting_clear_cache_textview);
//        rlHelpCenter = (RelativeLayout) findViewById(R.id.setting_help_center_layout);
        rlCheckVersion = (RelativeLayout) findViewById(R.id.setting_check_appversion_layout);
        rlContentPreference = (RelativeLayout) findViewById(R.id.setting_content_preference_layout);
        if (UserHelper.getInstance().isLogin()) {
            tvLogout.setVisibility(View.VISIBLE);
        } else {
            tvLogout.setVisibility(View.GONE);
        }
    }


    private void initEvent() {
        tvLogout.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        rlPaise.setOnClickListener(this);
        rlAboutWe.setOnClickListener(this);
        rlClearCache.setOnClickListener(this);
        rlCheckVersion.setOnClickListener(this);
        rlContentPreference.setOnClickListener(this);
        tbAutoSubscription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogUtils.debug("自动订阅开。。。。。。");
                    SharePrefHelper.setAutoSubscription(true);
                } else {
                    LogUtils.debug("自动订阅关。。。。。。");
                    SharePrefHelper.setAutoSubscription(false);
                }
            }
        });
        tbWarnIsopen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogUtils.debug("自动通知开。。。。。。");
                    SharePrefHelper.setReceiveInfomation(true);
                    UMEventAnalyze.countEvent(SettingActivity.this, UMEventAnalyze.SETTING_MSG_OPEN);
                } else {
                    LogUtils.debug("自动通知关。。。。。。");
                    SharePrefHelper.setReceiveInfomation(false);
                }
            }
        });
    }

    public void initData() {
        //1初始化自动订阅设置
        tbAutoSubscription.setChecked(SharePrefHelper.getAutoSubscription());
        //2初始化消息提醒设置
        tbWarnIsopen.setChecked(SharePrefHelper.getReceiveInfomation());
        new GetCacheTask(SettingActivity.this, tvClearCache).execute();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == tvLogout.getId()) {
            final CommonDialogFoot dialog = new CommonDialogFoot(this,getString(R.string.setting_logout_tips),getString(R.string.ok),getString(R.string.cancel));
            dialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    UMEventAnalyze.countEvent(SettingActivity.this, UMEventAnalyze.SETTING_LOGOUT);
                    logout();
                }
            });
            dialog.show();
        } else if (ivBack.getId() == v.getId()) {
            finish();
        } else if (rlPaise.getId() == v.getId()) {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.SETTING_PASIE);
            try {
                Utils.gotoMarket(getActivity(), getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (rlAboutWe.getId() == v.getId()) {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.SETTING_ABOUT);
            Intent intent = new Intent(SettingActivity.this, SettingAboutWeActivity.class);
            startActivity(intent);
        } else if (rlClearCache.getId() == v.getId()) {
            //1清理数据库缓存，非当前用户数据库，当前用户的数据库章节表信息
            //2清理图片缓存
            clearCache();
            UMEventAnalyze.countEvent(this, UMEventAnalyze.SETTING_CACHE);
        } else if (rlCheckVersion.getId() == v.getId()) {
            AppCheckUpdateTask appCheckUpdateTask = new AppCheckUpdateTask(SettingActivity.this);
            appCheckUpdateTask.setFlag(true);
            appCheckUpdateTask.execute();
            EnterBookContent.openBookDetail(SettingActivity.this,"14");

            //检测新版本，调用方法
//            startCheck();
//            new AppUpdateTask(SettingActivity.this).execute();
//            new AppCheckUpdateTask(SettingActivity.this).execute();
        } else if (rlContentPreference.getId() == v.getId()) {
            Intent it = new Intent(SettingActivity.this, ContentPerferenceActivity.class);
            startActivity(it);
        }
    }

    /**
     * 版本检测安装
     */
    public void startCheck() {
//        String path="http://api.hxdrive.net/api/checkver";
//        GetRequest request = new GetRequest("http://172.16.0.192/h1/2", new Response.Listener<JSONObject>()
          GetRequest request = new GetRequest(URLConstants.APP_CHECK_UPDATE_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug(response.toString());
                if (ResponseHelper.isSuccess(response)) {
                    appVersion = AppVersionHelper.parseToAppVersion(response);
                    int build=Integer.parseInt(appVersion.getBuild());
                    if (new File(appVersion.getFilePath()).exists()) {
                        if (build != PhoneUtils.getVersionCode()) {
                            initDownloadCompleteDialog();
                        } else {
                            ViewUtils.toastShort("已为最新");
                        }
                    } else {
                        final Intent intent = new Intent(SettingActivity.this, AppVersionService.class);
                        intent.putExtra("appversion", appVersion);
                        startService(intent);
                        initDownloadDialog(intent);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    LogUtils.debug(error.networkResponse.statusCode + "");
                }
            }
        });
        RequestQueueManager.addRequest(request);


    }


    private void clearCache() {
        new ClearCacheTask(SettingActivity.this).execute();
    }

    private void logout() {

        if (!NetUtils.checkNetworkUnobstructed()) {
            finish();
            return;
        }

        Map<String, String> map = CommonUtils.getPublicPostArgs();
        PostRequest request = new PostRequest(URLConstants.PHONE_LOGOUT, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, map);

        RequestQueueManager.addRequest(request);
        UserHelper.getInstance().logout();
//        EventBus.getDefault().post(new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_LOGOUT, EventBusUtil.EVENTTYPE_LOGOUT));
        finish();
    }

    @Subscribe
    public void onEvent(EventBusUtil.EventBean bean) {
        if (bean != null && bean.getModle() == EventBusUtil.EVENTMODEL_APPVERSIONSERVICE) {
            switch (bean.getType()) {
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_COMPLETE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initDownloadCompleteDialog();
                        }
                    });
                    break;
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_FILE:
                    // 下载失败
                    try {
                        if (progressDeterminateDialog != null)
                            progressDeterminateDialog.cancel();
                        ViewUtils.toastShort("下载失败");
                    } catch (Exception ex) {
                        LogUtils.error(ex.getMessage(), ex);
                    }

                    break;
                case EventBusUtil.EVENTTYPE_VERSIONSERVICE_DOWNLOAD_PROGRESS:
                    float progress = Float.parseFloat(bean.getDesc());
                    if (progressDeterminateDialog != null) {
                        progressDeterminateDialog.setProgress((int) (progress));
                    }
                    break;
            }
        }
    }

    private void initDownloadDialog(final Intent intent) {
        if (progressDeterminateDialog != null && progressDeterminateDialog.isShowing()) {
            progressDeterminateDialog.cancel();
        }
        progressDeterminateDialog = new MaterialDialog.Builder(this)
                .content(R.string.download_please_waiting)
                .progress(false, 100, false)
                .cancelable(false)
                .positiveText(R.string.download_in_background)
                .negativeText(R.string.download_cancel)
                .show();

        progressDeterminateDialog.setProgress(0);
        progressDeterminateDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                progressDeterminateDialog.cancel();
            }
        });
        progressDeterminateDialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                stopService(intent);
            }
        });
    }

    private void initDownloadCompleteDialog() {
        if (progressDeterminateDialog != null && progressDeterminateDialog.isShowing()) {
            progressDeterminateDialog.dismiss();
        }

        progressDeterminateDialog = new MaterialDialog.Builder(this)
                .content(R.string.download_app_commlete)
                .progress(false, 100, false)
                .cancelable(false)
                .positiveText(R.string.download_app_install_apk)
                .negativeText(R.string.download_app_install_later)
                .build();

        progressDeterminateDialog.show();
        progressDeterminateDialog.setProgress(100);
        progressDeterminateDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                File file = new File(appVersion.getFilePath());
                Utils.installAPK(getApplicationContext(), file);
            }
        });
        progressDeterminateDialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                progressDeterminateDialog.cancel();
            }
        });
    }

}
