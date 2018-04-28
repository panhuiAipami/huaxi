package com.spriteapp.booklibrary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.activity.BridgeActivity;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.listener.DelBookShelf;
import com.spriteapp.booklibrary.listener.GotoHomePage;
import com.spriteapp.booklibrary.listener.HuaWeiPayCallBack;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.model.CateBean;
import com.spriteapp.booklibrary.model.StoreBean;
import com.spriteapp.booklibrary.model.TabBar;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.store.AppUpDateModel;
import com.spriteapp.booklibrary.thread.HuaWeiPayTask;
import com.spriteapp.booklibrary.ui.dialog.GoToAppStoreDialog;
import com.spriteapp.booklibrary.ui.dialog.MessageRemindDialog;
import com.spriteapp.booklibrary.ui.dialog.SortPop;
import com.spriteapp.booklibrary.ui.fragment.BookshelfFragment;
import com.spriteapp.booklibrary.ui.fragment.CommunityFragment;
import com.spriteapp.booklibrary.ui.fragment.HomeFragment;
import com.spriteapp.booklibrary.ui.fragment.HomePageFragment;
import com.spriteapp.booklibrary.ui.fragment.OutsideBookShelfFragment;
import com.spriteapp.booklibrary.ui.fragment.PersonCenterFragment;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.HuaweiPayResult;
import com.spriteapp.booklibrary.util.PreferenceHelper;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.thread.HuaWeiPayTask.REQUEST_HMS_RESOLVE_ERROR;
import static com.spriteapp.booklibrary.thread.HuaWeiPayTask.REQ_CODE_PAY;
import static com.spriteapp.booklibrary.ui.fragment.HomePageFragment.FRAGMENTTYPE;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class HomeActivity extends TitleActivity implements View.OnClickListener, DelBookShelf, HuaweiApiClient.OnConnectionFailedListener, HuaweiApiClient.ConnectionCallbacks, HuaWeiPayCallBack, GotoHomePage {

    //从网页进入app
    public static final String BOOK_ID = "book_id";
    public static final String CHAPTER_ID = "chapter_id";
    private String book_id = null;
    private String chapter_id = null;


    private static final String TAG = "HomeActivity";
    public static final String SEX = "sex";
    public static final String ADVERTISEMENT = "advertisement";
    private static final int BOOKSHELF_POSITION = 0;
    private static final int DISCOVER_POSITION = 1;
    private static final int COMMUNITY_POSITION = 3;
    private static final int BOOKSTORE_POSITION = 2;
    private static final int ME_POSITION = 4;
    public static final int PERSON_TO_BOOKSHELF = 10;
    public static final int BOOKSHELF_TO_BOOKSTORE = 20;
    private static final int TOP_BAR_HEIGHT = 47;
    ViewPager mHomeViewPager;
    LinearLayout mBookshelfLayout;
    LinearLayout mDiscoverLayout;
    LinearLayout mBookstoreLayout;
    LinearLayout mMeLayout;
    LinearLayout mCommunityLayout;
    private List<Fragment> mFragmentList;
    private ViewPagerAdapter mAdapter;
    private Context mContext;
    public static Context libContent;
    public static Activity libActivity;
    private List<StoreBean> sy = new ArrayList<>();
    private List<StoreBean> shu = new ArrayList<>();
    private HomePageFragment homePageFragment1;
    private HomePageFragment homePageFragment2;
    private ImageView image_one, image_two, image_three, image_four, image_five;
    private TextView text_one, text_two, text_three, text_four, text_five;
    private LinearLayout icon_layout;
    private View icon_line;

    //打包记得改成:huawei,测试用:alibaba,如果要打2.5.0之前的包(不支持华为登录与支付)用:aaaaa


//    public static final String HUAWEI = "alibaba";


    /**
     * 花都小说打包需要改变的常量规则
     * 花都CLIENT_ID=72，SIGN_SECRET=xn7667qjhq8ew2vy1mfz5h5c63ijdjh97px9ri;
     * 华为渠道CLIENT_ID=73，SIGN_SECRET=4zf8xzwv6c3ldcb8f2486ydji5z7u5ml5ktzxc，CHANNEL_IS_HUAWEI=true;
     * 花溪CLIENT_ID=40，SIGN_SECRET=fygopf7cixub8cpkh1oruik2byt2ykvkh81sy6，CHANNEL_IS_HUAWEI=false;
     */

    //华为打包为true，否则为false（华为渠道需要用华为支付）
    public static boolean CHANNEL_IS_HUAWEI = false;
    //花都打包为:true,否则为false (添加拉新功能)
    public static final boolean ISHAUDU = false;
    public static final String SIGN_SECRET = CHANNEL_IS_HUAWEI ? "4zf8xzwv6c3ldcb8f2486ydji5z7u5ml5ktzxc" : ISHAUDU ? "xn7667qjhq8ew2vy1mfz5h5c63ijdjh97px9ri" : "fygopf7cixub8cpkh1oruik2byt2ykvkh81sy6";
    public static final int CLIENT_ID = CHANNEL_IS_HUAWEI ? 73 : ISHAUDU ? 72 : 40;//华为渠道必须修改


    //华为移动服务Client
    private static HuaweiApiClient client;
    MessageRemindDialog dialog;
    OutsideBookShelfFragment bookshelfFragment;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Drawable drawable = (Drawable) msg.obj;
            switch (msg.what) {
                case 0:
                    image_one.setImageDrawable(drawable);
                    break;
                case 1:
                    image_two.setImageDrawable(drawable);
                    break;
                case 2:
                    image_three.setImageDrawable(drawable);
                    break;
                case 3://社区
                    image_five.setImageDrawable(drawable);
                    break;
                case 4://我的
                    image_four.setImageDrawable(drawable);
                    break;
            }
        }
    };
    private ImageView paixu;
    private ImageView qiandao;
    private TextView selector_or_close;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("HomeActivity", "执行onNewIntent方法");
    }

    @Override
    public void initData() {
        long a = PreferenceHelper.getLong(PreferenceHelper.AES_KEY, 0l);
        if (a == 0) {
            PreferenceHelper.putLong(PreferenceHelper.AES_KEY, System.currentTimeMillis());
        }

        mContext = this;
        int fisrtSex = SharedPreferencesUtil.getInstance().getInt(SEX, 0);
        if (fisrtSex == 0) {
            SharedPreferencesUtil.getInstance().putInt(SEX, HuaXiSDK.getInstance().getSex());
        }
        try {
            Intent intent = getIntent();
            int type = intent.getIntExtra(ADVERTISEMENT, 0);
            if (type == 1) {
                Log.d("toJump", "跳转广告页");
                String link = FileHelper.readObjectFromJsonFile(this, Constant.START_PAGE_URL, String.class);
                if (link != null && !link.isEmpty()) {
                    Log.d("toJump", link);
                    if (link.contains("book_details")) {
                        Uri uri = Uri.parse(link);
                        String book_id = uri.getQueryParameter(WebConstant.BOOK_ID_QUERY);
                        String chapter_id = uri.getQueryParameter(WebConstant.CHAPTER_ID_QUERY);
                        if (book_id != null && !book_id.isEmpty()) {
                            BookDetailResponse response = new BookDetailResponse();
                            response.setBook_id(Integer.parseInt(book_id));
                            if (chapter_id != null && !chapter_id.isEmpty()) {
                                response.setChapter_id(Integer.parseInt(chapter_id));
                            } else {
                                response.setChapter_id(0);
                            }
                            ActivityUtil.toReadActivity(this, response);
                        }
                    } else {
                        ActivityUtil.toWebViewActivity(this, link);
                    }

                }
            } else {
                Log.d("toJump", "不跳转广告页");
                book_id = intent.getStringExtra(BOOK_ID);
                chapter_id = intent.getStringExtra(CHAPTER_ID);
                if (!TextUtils.isEmpty(book_id)) {//直接跳转到阅读页并销毁
                    Log.d("toJump", "直接跳转到阅读页并销毁");
                    ActivityUtil.toReadActivity(this, Integer.parseInt(book_id), TextUtils.isEmpty(chapter_id) ? 0 : Integer.parseInt(chapter_id));
                    finish();
                }
            }

            setTitle("精选");
            UserBean.getInstance().restData();
            addSearchView();
            initFragment();
            setAdapter();
            setListener();
            appUpdate();
            addFlag();

            libContent = getApplicationContext();//获取lib上下文
            libActivity = this;
//        requestPermissions();
            boolean open = NotificationManagerCompat.from(this).areNotificationsEnabled();
            if (!open) {
                messagePermisssion();
            }
            gotoAppStoreComment();
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void messagePermisssion() {
        //前三天每天提醒一次，三天后不提示
        if (PreferenceHelper.getInt("messageNumber", 0) < 4 && System.currentTimeMillis() - PreferenceHelper.getLong("messageTime", 0) >= 1000 * 60 * 60 * 12) {
            PreferenceHelper.putLong("messageTime", System.currentTimeMillis());
            int number = PreferenceHelper.getInt("messageNumber", 0);
            PreferenceHelper.putInt("messageNumber", number + 1);

            dialog = new MessageRemindDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    toSetting();
                }
            });
        }
    }

    private void toSetting() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    /**
     * 五天后提示去市场评分
     */
    public void gotoAppStoreComment() {
        long time = PreferenceHelper.getLong(PreferenceHelper.GOTOAPPSTORE, 0);
        if (time > 0 && System.currentTimeMillis() - time >= 1000 * 60 * 60 * 24 * 5) {
            PreferenceHelper.putLong(PreferenceHelper.GOTOAPPSTORE, -1);
            new GoToAppStoreDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.showLongToast("您的手机没有安装Android应用市场");
                        e.printStackTrace();
                    }
                }
            });
        } else if (time == 0) {
            PreferenceHelper.putLong(PreferenceHelper.GOTOAPPSTORE, System.currentTimeMillis());
        }
    }

    public void addFlag() {
        BookApi.getInstance()
                .service
                .app_cate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<CateBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<CateBean> appUpDateModelBase) {
                        Log.d("cate11", appUpDateModelBase.toString() + "yyy");
                        if (appUpDateModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
//                            String name = appUpDateModelBase.getData().toString();
//                            Log.d("cate11", name + "哈g哈");
//                            Gson gson = new Gson();
//                            String s = gson.toJson(appUpDateModelBase.getData());
//                            Log.d("cate1111", s + "哈g哈");
                            if (appUpDateModelBase.getData() != null) {
                                if (appUpDateModelBase.getData().getHello_messages() != null && !appUpDateModelBase.getData().getHello_messages().isEmpty()) {
                                    ToastUtil.showLongToast(appUpDateModelBase.getData().getHello_messages());
                                }
                                /**
                                 * 顶部导航，废弃
                                 */
//                                if (appUpDateModelBase.getData().getTop_menu() != null && appUpDateModelBase.getData().getTop_menu().getStore() != null && appUpDateModelBase.getData().getTop_menu().getStore().size() != 0) {
//                                    sy.addAll(appUpDateModelBase.getData().getTop_menu().getStore());
//                                    if (homePageFragment1 != null) {
//                                        homePageFragment1.getHttpListData(appUpDateModelBase.getData().getTop_menu());
//                                    }
//                                    if (homePageFragment2 != null) {
//                                        homePageFragment2.getHttpListData(appUpDateModelBase.getData().getTop_menu());
//                                    }
//                                }
//                                if (appUpDateModelBase.getData().getTop_menu() != null && appUpDateModelBase.getData().getTop_menu().getChosen() != null && appUpDateModelBase.getData().getTop_menu().getChosen().size() != 0) {
//                                    shu.addAll(appUpDateModelBase.getData().getTop_menu().getChosen());
//                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.PathTitle, appUpDateModelBase.getData().getTop_menu());
//                                }
                                if (appUpDateModelBase.getData().getTabbar() != null) {//底部导航栏图标
                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.NAVIGATION, appUpDateModelBase.getData().getTabbar());
                                }
                                if (appUpDateModelBase.getData().getSplashscreen() != null) {//广告页
                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.START_PAGE, appUpDateModelBase.getData().getSplashscreen());
                                    Log.d("addFileImage", "url===" + appUpDateModelBase.getData().getSplashscreen());
                                }
                                if (appUpDateModelBase.getData().getSplashscreen_url() != null) {//广告页链接
                                    FileHelper.writeObjectToJsonFile(AppUtil.getAppContext(), Constant.START_PAGE_URL, appUpDateModelBase.getData().getSplashscreen_url());
                                }
                            }

                        }


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("cate11", "失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void appUpdate() {
        BookApi.getInstance()
                .service
                .app_Upate(com.spriteapp.booklibrary.util.Util.getAppMetaData(HomeActivity.this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<AppUpDateModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<AppUpDateModel> appUpDateModelBase) {
                        Log.d("update11", appUpDateModelBase.toString() + "yyy");
                        if (appUpDateModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            String name = appUpDateModelBase.getData().toString();
//                            Log.d("update11", name + "哈g哈");
                            Util.chechForUpdata("checkForUpdates", appUpDateModelBase.getData(), HomeActivity.this, false);
                        }


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("update11", "失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setListener() {
        mHomeViewPager.addOnPageChangeListener(new ViewPagerListener());
        mBookshelfLayout.setOnClickListener(this);
        mDiscoverLayout.setOnClickListener(this);
        mBookstoreLayout.setOnClickListener(this);
        mMeLayout.setOnClickListener(this);
        mCommunityLayout.setOnClickListener(this);
        ListenerManager.getInstance().setDelBookShelf(this);
        ListenerManager.getInstance().setHuaWeiPayCallBack(this);
        ListenerManager.getInstance().setGotoHomePage(this);
    }

    private void setAdapter() {
        mHomeViewPager.setOffscreenPageLimit(5);//viewpager缓存
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mHomeViewPager.setAdapter(mAdapter);
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
//        mFragmentList.add(new BookshelfFragment());
//        mFragmentList.add(new NativeBookStoreFragment());
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        homePageFragment1 = new HomePageFragment();
        homePageFragment2 = new HomePageFragment();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putInt(FRAGMENTTYPE, 1);
        bundle2.putInt(FRAGMENTTYPE, 2);
        homePageFragment1.setArguments(bundle1);
        homePageFragment2.setArguments(bundle2);
        mFragmentList.add(HomeFragment.newInstance(0));
        mFragmentList.add(HomeFragment.newInstance(1));
//        mFragmentList.add(homePageFragment2);
//        mFragmentList.add(new DiscoverFragment());
        bookshelfFragment = new OutsideBookShelfFragment();
        mFragmentList.add(bookshelfFragment);
        mFragmentList.add(new CommunityFragment());//社区分类
        mFragmentList.add(new PersonCenterFragment());
    }

    @Override
    public void configViews() throws Exception {
        super.configViews();
        gone(mBackImageView);
        gone(mTitleLayout);
        setViewpagerTopMargin(0);
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_book, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        mHomeViewPager = (ViewPager) findViewById(R.id.book_reader_home_view_pager);
        mBookshelfLayout = (LinearLayout) findViewById(R.id.book_reader_bookshelf_layout);
        mDiscoverLayout = (LinearLayout) findViewById(R.id.book_reader_discover_layout);
        mBookstoreLayout = (LinearLayout) findViewById(R.id.book_reader_bookstore_layout);
        icon_layout = (LinearLayout) findViewById(R.id.icon_layout);
        icon_line = findViewById(R.id.icon_line);
        mMeLayout = (LinearLayout) findViewById(R.id.book_reader_me_layout);
        mCommunityLayout = (LinearLayout) findViewById(R.id.book_reader_community_layout);
        image_one = (ImageView) findViewById(R.id.image_one);
        image_two = (ImageView) findViewById(R.id.image_two);
        image_three = (ImageView) findViewById(R.id.image_three);
        image_four = (ImageView) findViewById(R.id.image_four);
        image_five = (ImageView) findViewById(R.id.image_five);
        text_one = (TextView) findViewById(R.id.text_one);
        text_two = (TextView) findViewById(R.id.text_two);
        text_three = (TextView) findViewById(R.id.text_three);
        text_four = (TextView) findViewById(R.id.text_four);
        text_five = (TextView) findViewById(R.id.text_five);
        setIcon();
        mBookshelfLayout.setSelected(true);
    }

    public void setIcon() {
        List<TextView> textViewList = new ArrayList<>();
        List<ImageView> imageViewList = new ArrayList<>();
        textViewList.add(text_one);
        textViewList.add(text_two);
        textViewList.add(text_three);
        textViewList.add(text_five);
        textViewList.add(text_four);

        imageViewList.add(image_one);
        imageViewList.add(image_two);
        imageViewList.add(image_three);
        imageViewList.add(image_five);
        imageViewList.add(image_four);
        try {
            TabBar tabBar = FileHelper.readObjectFromJsonFile(this, Constant.NAVIGATION, TabBar.class);
            if (AppUtil.isNetAvailable(this)) {
                if (tabBar != null && tabBar.getColor() != null && !tabBar.getColor().isEmpty() && tabBar.getColor_on() != null && tabBar.getBackground_color() != null && !tabBar.getBackground_color().isEmpty() && tabBar.getBorder_style() != null && !tabBar.getBorder_style().isEmpty() && !tabBar.getColor_on().isEmpty() && tabBar.getLists() != null && tabBar.getLists().size() > 4) {
                    Log.d("textViewColor", "进入修改颜色的方法");
                    icon_layout.setBackgroundColor(Color.parseColor("#" + tabBar.getBackground_color()));
                    icon_line.setBackgroundColor(Color.parseColor("#" + tabBar.getBorder_style()));
                    setTextColor(tabBar, tabBar.getColor(), tabBar.getColor_on(), textViewList);
                }
                if (tabBar != null && tabBar.getLists() != null && tabBar.getLists().size() > 4) {
                    Log.d("textViewColor", "进入修改icon的方法");
                    setImageIcon(imageViewList, tabBar);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextColor(TabBar tab, String normalColor, String seletorColor, List<TextView> text) {
        for (int i = 0; i < text.size(); i++) {
            Log.d("textViewColor", tab.getLists().get(i).getText());
            int[] colors = new int[]{Color.parseColor("#" + normalColor), Color.parseColor("#" + seletorColor), Color.parseColor("#" + normalColor)};
            int[][] states = new int[3][];
            states[0] = new int[]{-android.R.attr.state_selected};
            states[1] = new int[]{android.R.attr.state_selected};
            states[2] = new int[]{};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            text.get(i).setTextColor(colorStateList);//修改文本颜色
            text.get(i).setText(tab.getLists().get(i).getText());//修改文本
            Log.d("textViewColor", tab.getLists().get(i).getText());
        }
    }

    public void setImageIcon(final List<ImageView> image, final TabBar tab) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int dpi = dm.densityDpi;
                int icon_type = dpi > 320 ? 3 : 2;
                for (int i = 0; i < image.size(); i++) {
                    //修改底部图标
                    StateListDrawable drawable = new StateListDrawable();
                    drawable.addState(new int[]{android.R.attr.state_selected}, GlideUtils.loadImageFromNetwork(icon_type == 2 ? tab.getLists().get(i).getIcon_on().get_$2x() : tab.getLists().get(i).getIcon_on().get_$3x()));
                    drawable.addState(new int[]{-android.R.attr.state_pressed}, GlideUtils.loadImageFromNetwork(icon_type == 2 ? tab.getLists().get(i).getIcon().get_$2x() : tab.getLists().get(i).getIcon().get_$3x()));
                    Message message = new Message();
                    message.obj = drawable;
                    message.what = i;
                    handler.sendMessage(message);
                    Log.d("Thread", tab.getLists().get(i).getIcon_on().get_$2x());
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        if (v == mBookshelfLayout) {
            mHomeViewPager.setCurrentItem(BOOKSHELF_POSITION);
            setSelectView(BOOKSHELF_POSITION);
        } else if (v == mDiscoverLayout) {
            mHomeViewPager.setCurrentItem(DISCOVER_POSITION);
            setSelectView(DISCOVER_POSITION);
        } else if (v == mCommunityLayout) {//社区
            mHomeViewPager.setCurrentItem(COMMUNITY_POSITION);
            setSelectView(COMMUNITY_POSITION);
        } else if (v == mBookstoreLayout) {
            mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
            setSelectView(BOOKSTORE_POSITION);
        } else if (v == mMeLayout) {
            mHomeViewPager.setCurrentItem(ME_POSITION);
            setSelectView(ME_POSITION);
            if (!AppUtil.isLogin(this)) {

            }
        }
    }

    public void setSelectView(int position) {//view的选中状态
        switch (position) {
            case BOOKSHELF_POSITION:
                mBookshelfLayout.setSelected(true);
                setSelectFalse(mDiscoverLayout, mBookstoreLayout, mMeLayout, mCommunityLayout);
                break;
            case DISCOVER_POSITION:
                mDiscoverLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mBookstoreLayout, mMeLayout, mCommunityLayout);
                break;
            case COMMUNITY_POSITION:
                mCommunityLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mBookstoreLayout, mMeLayout, mDiscoverLayout);
                break;
            case BOOKSTORE_POSITION:
                mBookstoreLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mMeLayout, mCommunityLayout);
                break;
            case ME_POSITION:
                mMeLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mBookstoreLayout, mCommunityLayout);
                break;
            case PERSON_TO_BOOKSHELF:
                mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
                mBookstoreLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mDiscoverLayout, mMeLayout, mCommunityLayout);
                break;
            case BOOKSHELF_TO_BOOKSTORE:
                mHomeViewPager.setCurrentItem(DISCOVER_POSITION);
                mDiscoverLayout.setSelected(true);
                setSelectFalse(mBookshelfLayout, mBookstoreLayout, mMeLayout, mCommunityLayout);
                break;

        }
    }

    @Override
    public void del_book(int book_id, int pos, int num, int act) {
        if (book_id == 0) {
            mLeftLayout.setVisibility(View.GONE);
        } else if (book_id != 0) {
            if (mLeftLayout.getVisibility() == View.GONE)
                mLeftLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void info(double productPrice, String orderId, String key) {
        HuaWeiPayTask p = new HuaWeiPayTask(HomeActivity.getConnect(), new HuaweiPayResult(this, REQ_CODE_PAY));
        p.pay(key, "花溪小说", "充值", productPrice, orderId);
    }

    @Override
    public void gotoPage(int type) {//任务回调
        if (mHomeViewPager != null&&type==1) {
            mHomeViewPager.setCurrentItem(BOOKSHELF_POSITION);
            setSelectView(BOOKSHELF_POSITION);
        }else if(mHomeViewPager != null&&type==2){
            mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
            setSelectView(BOOKSTORE_POSITION);
        }

    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {


        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    private class ViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case BOOKSHELF_POSITION:
//                    addFreeTextView();
                    addSearchView();
                    setTitle(R.string.book_reader_book_selector);
                    setSelectView(BOOKSHELF_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
                case DISCOVER_POSITION:
                    mRightLayout.removeAllViews();
                    mLeftLayout.removeAllViews();
                    setTitle(R.string.book_reader_bookstore);
                    addSearchView();
                    setSelectView(DISCOVER_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
                case COMMUNITY_POSITION:
                    mRightLayout.removeAllViews();
                    mLeftLayout.removeAllViews();
                    setTitle(R.string.book_reader_community);
                    addSearchView();
                    setSelectView(COMMUNITY_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
                case BOOKSTORE_POSITION:
//                    setTitle(R.string.book_reader_bookshelf);
                    setTitle("");
//                    addSearchView();
                    addFreeTextView();
                    setSelectView(BOOKSTORE_POSITION);
                    gone(mTitleLayout);
                    setViewpagerTopMargin(0);
                    break;
                case ME_POSITION:
                    setTitle(R.string.book_reader_me);
                    mRightLayout.removeAllViews();
                    mLeftLayout.removeAllViews();
                    setSelectView(ME_POSITION);
                    visible(mTitleLayout);
                    setViewpagerTopMargin(TOP_BAR_HEIGHT);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void setViewpagerTopMargin(int topMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHomeViewPager.getLayoutParams();
        params.topMargin = ScreenUtil.dpToPxInt(topMargin);
        mHomeViewPager.setLayoutParams(params);
    }

    private void addSearchView() {
        mRightLayout.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.book_reader_book_store_search_layout, null);
        mRightLayout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityUtil.toWebViewActivity(mContext, Constant.BOOK_SEARCH_URL);
                ActivityUtil.toSearchActivity(mContext);
            }
        });
    }

    private void addFreeTextView() {
        mRightLayout.removeAllViews();
        mLeftLayout.removeAllViews();
        final LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.book_reader_free_text_layout, null);
        qiandao = (ImageView) view.findViewById(R.id.qiandao);
        qiandao.setOnClickListener(new View.OnClickListener() {//签到
            @Override
            public void onClick(View v) {
                if (!AppUtil.isLogin()) {
                    HuaXiSDK.getInstance().toLoginPage(mContext);
                    return;
                }
                ActivityUtil.toWebViewActivity(mContext, Constant.CHECK_IN_URL);
            }
        });
        paixu = (ImageView) view.findViewById(R.id.paixu);
        paixu.setOnClickListener(new View.OnClickListener() {//排序
            @Override
            public void onClick(View v) {
                SortPop pop = new SortPop(HomeActivity.this, v);
                pop.setSortOnItemClickListener(new SortPop.OnItemClickListener() {
                    @Override
                    public void refresh() {
                        if (bookshelfFragment != null)
                            bookshelfFragment.refreshSort();
                    }
                });
            }
        });
        selector_or_close = (TextView) view.findViewById(R.id.selector_or_close);
        selector_or_close.setOnClickListener(new View.OnClickListener() {//全选或者取消
            @Override
            public void onClick(View v) {
                if ("全选".equals(selector_or_close.getText().toString())) {
                    selector_or_close.setText("取消");
                    if (bookshelfFragment != null)
                        bookshelfFragment.setFinish(2);
                } else if ("取消".equals(selector_or_close.getText().toString())) {
                    selector_or_close.setText("全选");
                    if (bookshelfFragment != null)
                        bookshelfFragment.setFinish(3);
                }
            }
        });
        TextView leftView = (TextView) LayoutInflater.from(this).inflate(R.layout.finish_layout, null);
        int rightTitleColor = HuaXiSDK.getInstance().getConfig().getRightTitleColor();
//        if (rightTitleColor != 0) {
//            view.setTextColor(rightTitleColor);
//            leftView.setTextColor(rightTitleColor);
//        }
        mRightLayout.addView(view);
        mLeftLayout.addView(leftView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!AppUtil.isLogin()) {
//                    HuaXiSDK.getInstance().toLoginPage(mContext);
//                    return;
//                }
//                ActivityUtil.toWebViewActivity(mContext, Constant.CHECK_IN_URL);
            }
        });
        mLeftLayout.setVisibility(View.GONE);
        mLeftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookshelfFragment != null) {
                    bookshelfFragment.setFinish(1);
                }

            }
        });
        if (bookshelfFragment != null) {
            if (bookshelfFragment.getIs_del1() == 0) {

            } else if (bookshelfFragment.getIs_del1() == 1) {
                if (mTitleLayout.getVisibility() == View.GONE)
                    visible(mTitleLayout);
                gone(paixu, qiandao);
                visible(selector_or_close, mLeftLayout);
                selector_or_close.setText("取消");
            } else if (bookshelfFragment.getIs_del1() == 2) {
                if (mTitleLayout.getVisibility() == View.GONE)
                    visible(mTitleLayout);
                gone(paixu, qiandao);
                visible(selector_or_close, mLeftLayout);
                selector_or_close.setText("全选");
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        try {
//            BookshelfFragment shelfFragment = getShelfFragment();
//            if (shelfFragment != null && shelfFragment.isDeleteBook()) {
//                shelfFragment.setDeleteBook();
//                return;
//            }
//            Fragment currentFragment = getCurrentFragment();
//            if (currentFragment != null && !(currentFragment instanceof BookshelfFragment)) {
//                mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
//                setSelectView(BOOKSTORE_POSITION);
//                return;
//            }
//            super.onBackPressed();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            BookshelfFragment shelfFragment = getShelfFragment();
            if (shelfFragment != null && shelfFragment.isDeleteBook()) {
                shelfFragment.setDeleteBook();
                return true;
            }
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null && !(currentFragment instanceof BookshelfFragment)) {
                mHomeViewPager.setCurrentItem(BOOKSTORE_POSITION);
                setSelectView(BOOKSTORE_POSITION);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(true);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onKeyDown(keyCode, event);
    }

    public Fragment getCurrentFragment() {
        if (CollectionUtil.isEmpty(mFragmentList)) {
            return null;
        }
        int currentItem = mHomeViewPager.getCurrentItem();
        if (currentItem < 0 || currentItem >= mFragmentList.size()) {
            return null;
        }
        return mFragmentList.get(currentItem);
    }

    public BookshelfFragment getShelfFragment() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof BookshelfFragment) {
            return (BookshelfFragment) currentFragment;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragmentList.get(COMMUNITY_POSITION).onActivityResult(requestCode, resultCode, data);//将返回传给社区Fragment
        mFragmentList.get(ME_POSITION).onActivityResult(requestCode, resultCode, data);//将返回传给社区Fragment
        if (resultCode == RESULT_OK) {
            //连接失败处理
            if (requestCode == REQUEST_HMS_RESOLVE_ERROR) {
                if (resultCode == Activity.RESULT_OK) {

                    int result = data.getIntExtra(BridgeActivity.EXTRA_RESULT, 0);

                    if (result == ConnectionResult.SUCCESS) {
                        Log.i(HuaWeiPayTask.TAG, "错误成功解决");
                        if (!client.isConnecting() && !client.isConnected()) {
                            client.connect(this);
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

    public void requestPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);// 读sd卡
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);// 写sd卡
        permissions.add(Manifest.permission.READ_PHONE_STATE);// 允许程序访问电话状态
        doRequestPermissions(new PermissionRequestObj(permissions) {

            public void callback(boolean allGranted, List<String> list, TitleActivity.PermissionRequestObj pro) {
                if (allGranted) {

                }
            }
        });
    }

    public void showLeftView(int type) {//是否改变状态
        if (mLeftLayout.getVisibility() == View.GONE)
            mLeftLayout.setVisibility(View.VISIBLE);
        Log.d("showLeftView", "条件不成立");
        if (selector_or_close.getVisibility() == View.GONE) {
            if (mTitleLayout.getVisibility() == View.GONE)
                visible(mTitleLayout);
            gone(paixu, qiandao);
            visible(selector_or_close);
            Log.d("showLeftView", "fragment可见时");
        }
        if (type == 3) {
            selector_or_close.setText("取消");
        } else if (type == 4) {
            selector_or_close.setText("全选");
        }

    }

    public void goneLeftView() {
        if (mLeftLayout.getVisibility() == View.VISIBLE)
            mLeftLayout.setVisibility(View.GONE);
        if (selector_or_close.getVisibility() == View.VISIBLE) {
            gone(selector_or_close);
//            visible(paixu, qiandao);
            if (mTitleLayout.getVisibility() == View.VISIBLE)
                gone(mTitleLayout);
            visible(paixu);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null)
            client.disconnect();
    }

    public static HuaweiApiClient getConnect() {
        return client;
    }

    HuaweiIdSignInOptions signInOptions = new
            HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
            .requestOpenId()
            .build();

    public void connect() {
        //创建华为移动服务client实例用以实现支付功能
        //需要指定api为HuaweiPay.PAY_API
        //连接回调以及连接失败监听
        if (CHANNEL_IS_HUAWEI) {
            client = new HuaweiApiClient.Builder(this)
                    .addApi(HuaweiPay.PAY_API)
                    .addApi(HuaweiId.SIGN_IN_API, signInOptions)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();

            //建议在oncreate的时候连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            client.connect(this);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(HuaWeiPayTask.TAG, "HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());
        if (HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {
            final int errorCode = connectionResult.getErrorCode();
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用
//                    Toast.makeText(AppContext.context(),"请先安装华为移动服务",Toast.LENGTH_LONG).show();
                    HuaweiApiAvailability.getInstance().resolveError(HomeActivity.this, errorCode, REQUEST_HMS_RESOLVE_ERROR);
                }
            });
        } else {
            //其他错误码请参见开发指南或者API文档

        }
    }
}
