package com.spriteapp.booklibrary.ui.activity;

import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public abstract class TitleActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TitleActivity";

    ImageView mBackImageView;
    TextView mTitleTextView;
    LinearLayout mLeftLayout;
    LinearLayout mRightLayout;
    LinearLayout mCenterLayout;
    FrameLayout mContainerLayout;
    RelativeLayout mTitleLayout;
    View mLineView;

    public static boolean isActive; //全局变量

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_activity_title;
    }

    @Override
    public void findViewId() throws Exception {
        mBackImageView = (ImageView) findViewById(R.id.book_reader_back_imageView);
        mTitleTextView = (TextView) findViewById(R.id.book_reader_title_textView);
        mLeftLayout = (LinearLayout) findViewById(R.id.book_reader_left_layout);
        mRightLayout = (LinearLayout) findViewById(R.id.book_reader_right_layout);
        mContainerLayout = (FrameLayout) findViewById(R.id.book_reader_container_layout);
        mCenterLayout = (LinearLayout) findViewById(R.id.book_reader_center_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.book_reader_title_layout);
        mLineView = findViewById(R.id.book_reader_title_line_view);
        addContentView();
        mLeftLayout.setOnClickListener(this);
    }

    @Override
    public void configViews() throws Exception {
        HuaXiConfig config = HuaXiSDK.getInstance().getConfig();
        int titleBackground = config.getTitleBackground();
        if (titleBackground != 0) {
            mTitleLayout.setBackgroundColor(titleBackground);
        }
        int titleColor = config.getTitleColor();
        if (titleColor != 0) {
            mTitleTextView.setTextColor(titleColor);
        }
        int backImageResource = config.getBackImageResource();
        if (backImageResource != 0) {
            mBackImageView.setImageResource(backImageResource);
        }
    }

    public void setTitle(int titleId) {
        mTitleTextView.setText(getResources().getString(titleId));
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public abstract void addContentView();

    @Override
    public void onClick(View v) {
        if (v == mLeftLayout) {
            finish();
        }
    }
    // ANDROID 6.0（SDK23）开始手动申请权限----------------------------------------------------------------------Start

    /**
     * 权限正在申请中
     */
    public static boolean permissionRequesting = false;

    final static int PERMISSIONS_REQUEST_CODE_ACTIVITY = 0x01;

    static PermissionRequestObj currentPermissionRequest;

    /**
     * 权限申请
     */
    public void doRequestPermissions(PermissionRequestObj pro) {
        if (Build.VERSION.SDK_INT < 23) {
            if (pro != null) {
                pro.callback(true, null, pro);
            }
            return;
        }

        // 需要请求的权限列表
        final List<String> permissions_NeedRequest = new ArrayList<String>();
        // 用户设置不在提醒的权限列表 需要给出提示
        final List<String> permissions_NeedTip = new ArrayList<String>();

        for (int i = 0; i < pro.size(); i++) {
            String permission = pro.get(i);
            if (!filterPermission(permissions_NeedRequest, permission, pro)) {
                // 添加到需要提示内容中
                permissions_NeedTip.add(permission);
            }
        }

        if (permissions_NeedRequest.size() > 0) {
            permissionRequesting = true;
            currentPermissionRequest = pro;
            ActivityCompat.requestPermissions(TitleActivity.this,
                    permissions_NeedRequest.toArray(new String[permissions_NeedRequest.size()]),
                    PERMISSIONS_REQUEST_CODE_ACTIVITY);
            return;
        }

        // do something
        currentPermissionRequest = null;
        permissionRequesting = false;
        try {
            pro.callback(true, null, pro);
        } catch (Exception e) {
            Log.e("PermissionRequest", "Callback Exception" + e);
        }
    }


    /**
     * 过滤权限 是否已经授权，没有授权的添加到请求列表中
     *
     * @param permissions_NeedRequest 需要请求的权限列表
     * @param permission              权限
     * @param pro
     * @return true 请求权限就好了；false 用户设置了不在提醒 需要给出提示
     */
    private boolean filterPermission(List<String> permissions_NeedRequest, String permission, PermissionRequestObj pro) {
        if (ContextCompat.checkSelfPermission(TitleActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissions_NeedRequest.add(permission);// 没有权限的添加到需要申请的列表中
            if (!ActivityCompat.shouldShowRequestPermissionRationale(TitleActivity.this, permission))
                return false;
        } else {
            pro.authItem(permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 权限请求的返回结果
        permissionRequesting = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACTIVITY: {
                try {
                    if (currentPermissionRequest != null) {
                        for (int i = 0; i < permissions.length; i++) {
                            int r = grantResults[i];
                            if (r == PackageManager.PERMISSION_GRANTED) {
                                String p = permissions[i];
                                currentPermissionRequest.authItem(p);
                            }
                        }
                        boolean AllGranted = currentPermissionRequest.isAllGranted();
                        currentPermissionRequest.isAsyn = true;
                        currentPermissionRequest.callback(AllGranted, currentPermissionRequest.permissionsList_denied,
                                currentPermissionRequest);
                    }
                } catch (Exception e) {
                    Log.e("PermissionRequest", "Callback Exception onRequestPermissionsResult" + e);
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public abstract class PermissionRequestObj {
        // 是否是异步执行
        public boolean isAsyn = false;

        List<String> permissionsList;
        Map<String, Integer> perms = new HashMap<String, Integer>();

        // 请求结果 没有通过的权限列表
        List<String> permissionsList_denied;

        public PermissionRequestObj(List<String> permissionsList) {
            this.permissionsList = permissionsList;
            for (String p : permissionsList) {
                perms.put(p, PackageManager.PERMISSION_DENIED);
            }
        }

        public int size() {
            return permissionsList.size();
        }

        public String get(int index) {
            return permissionsList.get(index);
        }

        private void authItem(String permission) {
            perms.put(permission, PackageManager.PERMISSION_GRANTED);
        }

        /**
         * 判断是否所有权限申请都通过了
         *
         * @return
         */
        private boolean isAllGranted() {
            boolean ret = true;
            for (String p : permissionsList) {
                int auth = perms.get(p);
                if (auth != PackageManager.PERMISSION_GRANTED) {

                    ret = false;

                    if (permissionsList_denied == null)
                        permissionsList_denied = new ArrayList<String>();
                    permissionsList_denied.add(p);
                }
            }
            return ret;
        }

        /**
         * 权限申请的结果回调
         * <p/>
         * 是否所有请求的权限都获取到了
         *
         * @param permissionsList_denied 没有被授权的权限列表
         */
        public abstract void callback(boolean allGranted, List<String> permissionsList_denied, PermissionRequestObj pro);

        public void onSettingCannel() {
        }

        public void onSettingGoto() {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        try {
            if (!isActive) {
                //app 从后台唤醒，进入前台
                isActive = true;
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (!TextUtils.isEmpty(cbm.getText())) {//剪切板有数据
                    // + "剪切板信息" + cbm.getText()
                    //如果剪切板信息是识别码,跳转到阅读页
                    getBook(cbm);

                }

                Log.i("isActive", "程序从后台唤醒");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        try {
            if (!isAppOnForeground()) {
                //app 进入后台
                isActive = false;//记录当前已经进入后台
                Log.i("isActive", "程序进入后台");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() throws Exception {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public void getBook(final ClipboardManager cbm) throws Exception{
        if (!AppUtil.isNetAvailable(this))
            return;
        BookApi.getInstance()
                .service
                .book_command(cbm.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> bookDetailResponse) {
                        if (bookDetailResponse != null) {
                            int resultCode = bookDetailResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookDetailResponse.getData() != null && bookDetailResponse.getData().size() != 0) {//关键字
                                    //识别码直接跳转到阅读界面
                                    ActivityUtil.toReadActivity(TitleActivity.this, bookDetailResponse.getData().get(0).getBook_id(), 0);
                                    cbm.setText("");//清空剪切板
                                }

                            }

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
