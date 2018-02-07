package com.spriteapp.booklibrary.ui.activity;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.callback.ProgressCallback;
import com.spriteapp.booklibrary.callback.TextSizeCallback;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.ChapterDb;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.database.RecentBookDb;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.enumeration.AutoSubEnum;
import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.enumeration.ChapterEnum;
import com.spriteapp.booklibrary.enumeration.PageStyleEnum;
import com.spriteapp.booklibrary.enumeration.PayResultEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.listener.DialogListener;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.listener.ReadActivityFinish;
import com.spriteapp.booklibrary.listener.ReadDialogListener;
import com.spriteapp.booklibrary.manager.SettingManager;
import com.spriteapp.booklibrary.manager.ThemeManager;
import com.spriteapp.booklibrary.model.AddBookModel;
import com.spriteapp.booklibrary.model.UpdateProgressModel;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.ui.adapter.ChapterAdapter;
import com.spriteapp.booklibrary.ui.presenter.SubscriberContentPresenter;
import com.spriteapp.booklibrary.ui.view.SubscriberContentView;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.BookUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.DialogUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.widget.ReadBottomLayout;
import com.spriteapp.booklibrary.widget.ReadProgressLayout;
import com.spriteapp.booklibrary.widget.ReadRightTitleLayout;
import com.spriteapp.booklibrary.widget.TextSizeLayout;
import com.spriteapp.booklibrary.widget.readview.BaseReadView;
import com.spriteapp.booklibrary.widget.readview.OnReadStateChangeListener;
import com.spriteapp.booklibrary.widget.readview.OverlappedWidget;
import com.spriteapp.booklibrary.widget.readview.PageWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.constant.Constant.BOOK_DETAIL;
import static java.lang.Integer.parseInt;

/**
 * Created by kuangxiaoguo on 2017/7/10.
 */

public class ReadActivity extends TitleActivity implements SubscriberContentView, ReadActivityFinish {

    private static final String TAG = "ReadActivity";
    public static final String BOOK_DETAIL_TAG = "BookDetailTag";
    private static final int ANIMATION_TIME = 300;
    public static final String LAST_CHAPTER = "last_chapter";
    FrameLayout mBookContainer;
    private ReadBottomLayout mBottomLayout;
    private SubscriberContentPresenter mPresenter;
    private BaseReadView mWidget;
    private List<BookChapterResponse> mChapterList;
    private int mTitleHeight;
    private boolean isDismiss = true;
    private ListView mChapterListView;
    private LinearLayout mChapterLayout;
    private LinearLayout mProgressLayout;
    private LinearLayout mModeLayout;
    private LinearLayout mTextLayout;
    private DrawerLayout mDrawerLayout;
    private ChapterDb mChapterDb;
    private ChapterAdapter mChapterAdapter;
    private TextSizeLayout mTextSizeLayout;
    private TextView book_reader_title_textView;

    /**
     * 是否开始阅读章节
     **/
    private boolean startRead = false;
    private int mBookId;
    private int mCurrentChapter;
    private ReadProgressLayout mReadProgressLayout;
    private boolean isNight;
    private View mShowView;
    private View mDismissView;
    private int mBottomHeight;
    private int mReadProgressHeight;
    private int mTempHeight;
    private boolean isChangeTextSize;
    private boolean hasNotifyAdapter;
    private Context mContext;
    private AlertDialog mPayChapterDialog;
    private int mLoadChapterId;
    private BookDb mBookDb;
    private BookDetailResponse mOldBookDetail;
    private BookDetailResponse mNewBookDetail;
    private boolean hasInitWidget;
    private ReadRightTitleLayout mRightTitleLayout;
    private ContentDb mContentDb;
    private SubscriberContent mQueryContent;
    private SimpleDateFormat mDateFormat;
    private ReadReceiver mReadReceiver;
    //判断是否是通过点击dialog购买章节,购买成功后进行toast提示
    private boolean isClickPayChapter;
    private RecentBookDb mRecentBookDb;
    PopupWindow popupWindow;//书籍详情与分享
    private int chapter = 0;
    private boolean IsRegister = true;
    private String titleFile = "";

    @Override
    public void initData() {
        if (IsRegister) {
            EventBus.getDefault().register(this);
//            IsRegister = !IsRegister;
        }
        mRightTitleLayout = new ReadRightTitleLayout(this);
        mRightLayout.addView(mRightTitleLayout);
        mRightTitleLayout.setTitleListener(mTitleListener);
        mContext = this;
        Intent intent = getIntent();
        Log.d("getNotification", "跳转");
        String bookid = intent.getStringExtra("book_id");
        String push_id = intent.getStringExtra("push_id");
        String chapter_id = intent.getStringExtra("chapter_id");
        BookDetailResponse bookDetail;
        if (bookid != null && !bookid.isEmpty()) {//推送
            bookDetail = new BookDetailResponse();
            bookDetail.setBook_id(parseInt(bookid));
            if (push_id != null && !push_id.isEmpty()) {//push_id,回调
                HuaXiSDK.getInstance().toWXPay(null, push_id);
            }
            if (chapter_id != null && !chapter_id.isEmpty()) {//推送有章节id
                chapter = Integer.parseInt(chapter_id);
                mCurrentChapter = chapter;//赋值给当前章节id
            }
        } else {
            bookDetail = (BookDetailResponse) intent.getSerializableExtra(BOOK_DETAIL_TAG);
        }

        DialogUtil.setDialogListener(mDialogListener);
        mPresenter = new SubscriberContentPresenter();
        mPresenter.attachView(this);
        mBookDb = new BookDb(this);
        mChapterDb = new ChapterDb(this);
        mContentDb = new ContentDb(this);
        mRecentBookDb = new RecentBookDb(this);
        if (bookDetail == null) {
            return;
        }
        mBookId = bookDetail.getBook_id();

        mOldBookDetail = mBookDb.queryBook(mBookId);
        mRightTitleLayout.setAddShelfViewState(BookUtil.isBookAddShelf(mOldBookDetail));
        mChapterList = mChapterDb.queryCatalog(mBookId);
        int lastChapterId = SettingManager.getInstance().getLastChapter(String.valueOf(mBookId), 0);
        if (chapter == 0) {
            mCurrentChapter = bookDetail.getChapter_id();
        } else {
            mCurrentChapter = chapter;//使用推送的章节id
        }

        if (mCurrentChapter == 0) {
            Log.d("mCurrentChapter", lastChapterId + "上一次chapter_id");
            mCurrentChapter = lastChapterId;
        } else {
            if (mCurrentChapter != lastChapterId) {
                SettingManager.getInstance().saveReadProgress(String.valueOf(mBookId), mCurrentChapter, 0, 0);
            }
        }
        Log.d("IsHttp", "setMode");
        initChapterAdapter();
        openChapter();
        if (mOldBookDetail == null) {
            Log.d("IsHttp", "http请求");
            mPresenter.getBookDetail(mBookId);
        } else {
            Log.d("IsHttp", "http不请求");
            mRecentBookDb.insert(mOldBookDetail);
            long lastUpdateBookTime = mOldBookDetail.getLast_update_book_datetime();
            int hour = TimeUtil.getTimeInterval(lastUpdateBookTime);
            //书籍详情更新时间超过3小时需再次更新
            if (hour >= Constant.UPDATE_BOOK_DETAIL_INTERVAL) {
                mPresenter.getBookDetail(mBookId, false);
            } else {
                judgeChapterNeedLoad();
                //添加标题
                BookDetailResponse shareDetail = mNewBookDetail != null ?
                        mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
                if (shareDetail != null && shareDetail.getBook_name() != null && !shareDetail.getBook_name().isEmpty()) {
                    book_reader_title_textView.setText(shareDetail.getBook_name());
                }
            }
        }
        int textSizePosition = SharedPreferencesUtil.getInstance().getInt(Constant.READ_TEXT_SIZE_POSITION);
        mTextSizeLayout.setPosition(textSizePosition);

        setBatteryState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("onNewIntent", "执行read_onNewIntent");
        try {
            ThemeManager.isNull();
            EventBus.getDefault().unregister(this);
            try {
                unregisterReceiver(mReadReceiver);
            } catch (Exception e) {
            }
            if (mPresenter != null) {
                mPresenter.detachView();
            }
            mCurrentChapter = 0;
            mBookId = 0;
            mLoadChapterId = 0;
            mOldBookDetail = null;
            mNewBookDetail = null;
            mChapterList = null;
            mWidget = null;
            addContentView();
            findViewId();
            initData();
            configViews();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setBatteryState() {
        try {
            mDateFormat = new SimpleDateFormat("HH:mm");
            mReadReceiver = new ReadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(mReadReceiver, intentFilter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setActivityFinish() {
//        if (!this.isFinishing()) {
//            finish();
//            Log.d("getNotification", "setActivityFinish");
//            Log.d("setActivityFinish", "setActivityFinish1");
//        }
//        Log.d("setActivityFinish", "setActivityFinish");
    }

    class ReadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (mWidget != null) {
                    if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                        int level = intent.getIntExtra("level", 0);
                        mWidget.setBattery(100 - level);
                    } else if (Intent.ACTION_TIME_TICK.equals(action)) {
                        mWidget.setTime(mDateFormat.format(new Date()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void judgeChapterNeedLoad() {
        if (CollectionUtil.isEmpty(mChapterList) || mOldBookDetail == null) {
            Log.d("bookIds", "bookId=====" + mBookId);
            mPresenter.getChapter(mBookId);
        } else {
            //判断是否完结
            int finishTag = mOldBookDetail.getBook_finish_flag();
            if (finishTag == BookEnum.BOOK_FINISH_TAG.getValue()) {
                return;
            }
            // 判断章节时间差是否大于六小时
            long time = mOldBookDetail.getLast_update_chapter_datetime();
            int hour = TimeUtil.getTimeInterval(time);
            if (hour >= Constant.UPDATE_CHAPTER_INTERVAL) {
                mPresenter.getChapter(mBookId, false);
            }
        }
    }

    private void openChapter() {
        if (!isCurrentChapterExists(mCurrentChapter) && mCurrentChapter != 0) {
            mPresenter.getContent(mBookId, mCurrentChapter);
            mLoadChapterId = mCurrentChapter;
            return;
        }
        if (!CollectionUtil.isEmpty(mChapterList)) {
            if (mWidget == null) {
                int pageStyle = SharedPreferencesUtil.getInstance().getInt(Constant.PAGE_CHANGE_STYLE);
                mWidget = pageStyle == PageStyleEnum.DEFAULT_STYLE.getValue() ?
                        new OverlappedWidget(this, String.valueOf(mBookId), mChapterList, mReadListener)
                        : new PageWidget(this, String.valueOf(mBookId), mChapterList, mReadListener);
                mBookContainer.addView(mWidget);
                isNight = SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_NIGHT_MODE);
                if (mWidget.getPageFactory() == null) {
                    return;
                }
                setMode();
                mWidget.setProgressCallback(mProgressCallback);
                mWidget.setTouchPageListener(mTouchPageListener);
            }
            if (!startRead) {
                startRead = true;
                if (!mWidget.isPrepared) {
                    mWidget.init();
                    hasInitWidget = true;
                    dismissDialog();
                } else {
                    mWidget.jumpToChapter(mCurrentChapter);
                }
            }
        }

    }

    private void showPayChapterDialog() {
        if (mPayChapterDialog != null && mPayChapterDialog.isShowing()) {
            return;
        }
        if (mPayChapterDialog != null) {
            mPayChapterDialog.show();
            return;
        }

        int payPrice = mQueryContent.getChapter_price();
        int chapterPayType = mQueryContent.getChapter_pay_type();
        String title;
        String message;
        String positiveText;
        String negativeText;
        if (chapterPayType == ChapterEnum.AUTO_BUY.getCode()) {
            title = getResources().getString(R.string.book_reader_pay_chapter);
            message = getResources().getString(R.string.book_reader_pay_prompt);
            negativeText = getResources().getString(R.string.book_reader_cancel_text);
            positiveText = String.format(getResources().getString(R.string.book_reader_buy_text),
                    String.valueOf(payPrice));
        } else {
            title = getResources().getString(R.string.book_reader_recharge_title);
            message = getResources().getString(R.string.book_reader_recharge_message);
            negativeText = getResources().getString(R.string.book_reader_recharge_negative_text);
            positiveText = getResources().getString(R.string.book_reader_recharge_positive_text);
        }
        mPayChapterDialog = DialogUtil.getPayChapterDialog(mContext, title, message, negativeText, positiveText);
        mPayChapterDialog.show();
    }

    private void initChapterAdapter() {
        if (mChapterList == null) {
            mChapterList = new ArrayList<>();
        }
        mChapterAdapter = new ChapterAdapter(this, mChapterList);
        mChapterListView.setAdapter(mChapterAdapter);
        mChapterListView.setSelectionFromTop(
                BookUtil.getCurrentChapterPosition(mChapterList, mCurrentChapter),
                ScreenUtil.getScreenHeight() / 2);
    }

    @Override
    public void configViews() {
        mTitleLayout.measure(0, 0);
        mTitleHeight = mTitleLayout.getMeasuredHeight();
        mBottomLayout.measure(0, 0);
        mBottomHeight = mBottomLayout.getMeasuredHeight();
        mTempHeight = mBottomHeight;
        mReadProgressLayout.measure(0, 0);
        mReadProgressHeight = mReadProgressLayout.getMeasuredHeight();
        mTitleLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        boolean isNight = SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_NIGHT_MODE);
        mBookContainer.setBackgroundColor(getResources().getColor(isNight ? R.color.book_reader_read_night_background :
                R.color.book_reader_read_day_background));
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        mBookContainer = (FrameLayout) findViewById(R.id.book_reader_read_container);
        mBottomLayout = (ReadBottomLayout) findViewById(R.id.book_reader_bottom_layout);
        mChapterListView = (ListView) findViewById(R.id.book_reader_catalog_list_view);
        mChapterLayout = (LinearLayout) findViewById(R.id.book_reader_chapter_layout);
        mProgressLayout = (LinearLayout) findViewById(R.id.book_reader_progress_layout);
        mModeLayout = (LinearLayout) findViewById(R.id.book_reader_mode_layout);
        mTextLayout = (LinearLayout) findViewById(R.id.book_reader_text_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.book_reader_drawer_layout);
        mReadProgressLayout = (ReadProgressLayout) findViewById(R.id.book_reader_read_progress_layout);
        mTextSizeLayout = (TextSizeLayout) findViewById(R.id.book_reader_text_size_layout);
        book_reader_title_textView = (TextView) findViewById(R.id.book_reader_title_textView);
        book_reader_title_textView.setMaxEms(8);
        book_reader_title_textView.setTextColor(ContextCompat.getColor(this, R.color.book_reader_home_bottom_text_choose_color));
        mShowView = mBottomLayout;
        mDismissView = mBottomLayout;
        mTextSizeLayout.setCallBack(mTextSizeCallback);
        mReadProgressLayout.setCallback(mPositionCallback);
        setListener();
    }

    /**
     * 在加载时调用，当前加载的chapterId是否存在
     */
    private boolean isCurrentChapterExists(int chapterId) {
        SubscriberContent content = mContentDb.queryContent(mBookId, chapterId);
        if (content != null) {
            content.setAuto_sub(SharedPreferencesUtil.getInstance()
                    .getInt(Constant.IS_BOOK_AUTO_SUB));
        }
        return content != null;
    }

    /**
     * 在切换章节时调用，给mQueryContent赋值用来判断购买逻辑
     */
    private boolean isChapterExists(int chapterId) {
        mQueryContent = mContentDb.queryContent(mBookId, chapterId);
        if (mQueryContent != null) {
            mQueryContent.setAuto_sub(SharedPreferencesUtil.getInstance()
                    .getInt(Constant.IS_BOOK_AUTO_SUB));
        }
        return mQueryContent != null;
    }

    private void setListener() {
        ListenerManager.getInstance().setReadActivityFinish(this);
        mChapterLayout.setOnClickListener(this);
        mProgressLayout.setOnClickListener(this);
        mModeLayout.setOnClickListener(this);
        mTextLayout.setOnClickListener(this);
        mChapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookChapterResponse catalog = mChapterList.get(position);
                mCurrentChapter = catalog.getChapter_id();
                mChapterAdapter.setCurrentChapter(mCurrentChapter);
                mChapterDb.updateReadState(mBookId, mCurrentChapter);
                mChapterList.get(position).setChapterReadState(ChapterEnum.HAS_READ.getCode());
                mChapterAdapter.notifyDataSetChanged();
                startRead = false;
                openChapter();
                mDrawerLayout.closeDrawer(mChapterListView);
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (!isDismiss) {
                    hideReadBar();
                }
                if (hasNotifyAdapter) {
                    return;
                }
                mCurrentChapter = SettingManager.getInstance().getLastChapter(String.valueOf(mBookId), mCurrentChapter);
                if (mCurrentChapter != mChapterAdapter.getCurrentChapter()) {
                    mChapterAdapter.setCurrentChapter(mCurrentChapter);
                    mChapterDb.updateReadState(mBookId, mCurrentChapter);
                    updateReadState();
                    mChapterAdapter.notifyDataSetChanged();
                }
                hasNotifyAdapter = true;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                hasNotifyAdapter = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_read, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onClick(View v) {
        if (v == mChapterLayout) {
            mShowView = mBottomLayout;
            mDismissView = mBottomLayout;
            mDrawerLayout.openDrawer(mChapterListView);
        } else if (v == mProgressLayout) {
            if (mWidget != null) {
                mReadProgressLayout.setProgress(mWidget.getCurrentProgress());
            }
            mReadProgressLayout.setVisibility(View.VISIBLE);
            mShowView = mReadProgressLayout;
            mDismissView = mBottomLayout;
            doBottomAnimation(mShowView, true, true);
            doBottomAnimation(mDismissView, false);
            mDismissView = mReadProgressLayout;
        } else if (v == mModeLayout) {
            changeMode();
        } else if (v == mTextLayout) {
            mTextSizeLayout.setVisibility(View.VISIBLE);
            mShowView = mTextSizeLayout;
            mDismissView = mBottomLayout;
            doBottomAnimation(mDismissView, false);
            doBottomAnimation(mShowView, true);
            mDismissView = mTextSizeLayout;
        } else if (v == mLeftLayout) {
            showAddShelfPrompt();
        }
    }

    private void showAddShelfPrompt() {
        if (mBookDb == null) {
            finish();
            return;
        }
        BookDetailResponse bookDetail = mBookDb.queryBook(mBookId);
        if (bookDetail == null || BookUtil.isBookAddShelf(bookDetail)) {
            if (AppUtil.isLogin()) {
                addToShelf(true);//书架中已存在但要刷新顺序
            }
            finish();
            return;
        }
        DialogUtil.showCommonDialog(this, getResources().getString(R.string.book_reader_sure_to_add_shelf),
                new DialogListener() {
                    @Override
                    public void clickCancel() {
                        finish();
                    }

                    @Override
                    public void clickSure() {
                        addToShelf(false);
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mOldBookDetail != null || mNewBookDetail != null) {
            showAddShelfPrompt();
            return;
        }
        super.onBackPressed();
    }

    private void changeMode() {
        if (mWidget == null) {
            return;
        }
        isNight = !isNight;
        SharedPreferencesUtil.getInstance().putBoolean(Constant.IS_NIGHT_MODE, isNight);
        setMode();
    }

    private void setMode() {
        Log.d("setMode", "isNight===" + isNight);
        mTextSizeLayout.changeMode(isNight);
        mReadProgressLayout.changeMode(isNight);
        mBottomLayout.changeMode(isNight);
        mRightTitleLayout.changeMode(isNight);

        mWidget.setTheme(isNight ? ThemeManager.NIGHT : ThemeManager.NORMAL);
        mWidget.setTextColor(getResources().getColor(isNight ? R.color.book_reader_reader_text_night_color
                        : R.color.book_reader_reader_text_color),
                getResources().getColor(isNight ? R.color.book_reader_read_title_night_color
                        : R.color.book_reader_read_title_color));
        mBackImageView.setImageResource(isNight ? R.drawable.book_reader_title_back_night_selector :
                R.drawable.book_reader_title_back_selector);
        mTitleLayout.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background :
                R.color.book_reader_white);
        mLineView.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background :
                R.color.book_reader_divide_line_color);
        mChapterListView.setBackgroundResource(isNight ? R.color.book_reader_chapter_night_background :
                R.color.book_reader_white);
        mChapterListView.setSelector(isNight ? R.drawable.book_reader_common_press_night_selector :
                R.drawable.book_reader_common_press_selector);
        book_reader_title_textView.setTextColor(ContextCompat.getColor(this, isNight ? R.color.night_title : R.color.book_reader_home_bottom_text_choose_color));
        if (mChapterAdapter != null) {
            mChapterAdapter.setNight(isNight);
            mChapterAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setData(Base<SubscriberContent> result) {
        String message = result.getMessage();
        int code = result.getCode();
        SubscriberContent data = result.getData();
        if (data == null) {
            ToastUtil.showSingleToast(message);
            return;
        }
        String key = data.getChapter_content_key();
        String content = data.getChapter_content();
        if (StringUtil.isEmpty(key)) {
            ToastUtil.showSingleToast("key为空" + data.getChapter_id());
            return;
        }
        if (StringUtil.isEmpty(content)) {
            ToastUtil.showSingleToast("content为空" + data.getChapter_id());
            return;
        }
        judgeNeedUpdatePayPage(data);
        if (code == ApiCodeEnum.SUCCESS.getValue()) {
            data.setChapter_need_buy(ChapterEnum.DO_NOT_NEED_BUY.getCode());
            if (isClickPayChapter) {
                ToastUtil.showSingleToast(R.string.book_reader_buy_successful_text);
                isClickPayChapter = false;
            }
        } else {
            if (code == ChapterEnum.BALANCE_SHORT.getCode()) {
                data.setChapter_pay_type(ChapterEnum.TO_PAY_PAGE.getCode());
            } else if (code == ChapterEnum.UN_SUBSCRIBER.getCode()) {
                data.setChapter_pay_type(ChapterEnum.AUTO_BUY.getCode());
            }
            data.setChapter_need_buy(ChapterEnum.NEED_BUY.getCode());
        }
        if (isCurrentChapterExists(data.getChapter_id())) {
            mContentDb.update(mBookId, data.getChapter_id(), data);
        } else {
            mContentDb.insert(data);
        }
        if (NetworkUtil.isAvailable(mContext)) {
            updateCurrentProgress(mCurrentChapter);
        }
        openChapter();
    }

    private void judgeNeedUpdatePayPage(SubscriberContent data) {
        if (data == null) {
            return;
        }
        int realPoint = data.getUsed_real_point();
        int falsePoint = data.getUsed_false_point();
        if (realPoint > 0 || falsePoint > 0) {
            EventBus.getDefault().post(UpdaterPayEnum.UPDATE_PAY_RESULT);
        }
    }

    private ReadRightTitleLayout.ReadTitleListener mTitleListener =
            new ReadRightTitleLayout.ReadTitleListener() {
                @Override
                public void clickBuy() {
                    toggleReadBar();
                    showPayChapterDialog();
                }

                @Override
                public void clickAddShelf() {
                    addToShelf(false);
                }

                @Override
                public void clickHome() {
                    finish();
                }

                @Override
                public void clickReward() {
                }

                @Override
                public void clickMore() {
//                    BookDetailResponse shareDetail = mNewBookDetail != null ?
//                            mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
//                    if (shareDetail != null) {
//                        HuaXiSDK.getInstance().showShareDialog(mContext, shareDetail, isNight);
//                    }
                    //弹出pop
                    showpopWindow(mRightTitleLayout);
                }
            };

    private void addToShelf(boolean isShelf) {
        AddBookModel model = new AddBookModel();
        model.setBookId(mBookId);
        model.setChapterId(mCurrentChapter);
        model.setAddShelf(isShelf);//刷新纪录的标识,是否弹出提示Toast
        EventBus.getDefault().post(model);
    }

    public void showpopWindow(View v) {
        View layout = View.inflate(this, R.layout.share_deatils_layout, null);
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(this, R.color.pop_back));
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(v, 30, 0, Gravity.LEFT);
        viewClick(layout);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void viewClick(View item) {
        ImageView book_details, book_share;
        book_details = (ImageView) item.findViewById(R.id.book_details);
        book_share = (ImageView) item.findViewById(R.id.book_share);
        book_details.setOnClickListener(new View.OnClickListener() {//书籍详情
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                BookDetailResponse shareDetail = mNewBookDetail != null ?
                        mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
                if (shareDetail != null) {
                    ActivityUtil.toWebViewActivity(ReadActivity.this, BOOK_DETAIL + shareDetail.getBook_id(), false, 1);
//                    finish();//销毁ReadActivity
                }


            }
        });
        book_share.setOnClickListener(new View.OnClickListener() {//书籍分享
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                BookDetailResponse shareDetail = mNewBookDetail != null ?
                        mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
                if (shareDetail != null) {
                    HuaXiSDK.getInstance().showShareDialog(mContext, shareDetail, isNight);
                }
            }
        });

    }

    private OnReadStateChangeListener mReadListener = new OnReadStateChangeListener() {
        @Override
        public void onChapterChanged(int chapter) {
            isClickPayChapter = false;
            updateCurrentProgress(chapter);
            mCurrentChapter = chapter;
            mChapterDb.updateReadState(mBookId, mCurrentChapter);
            if (mPayChapterDialog != null && mPayChapterDialog.isShowing()) {
                mPayChapterDialog.dismiss();
            }
            mPayChapterDialog = null;
            if (isChapterExists(chapter)
                    && mQueryContent.getChapter_need_buy() == ChapterEnum.NEED_BUY.getCode()) {
                //更新章节购买类型为
                mContentDb.updatePayType(mBookId, mCurrentChapter, ChapterEnum.AUTO_BUY.getCode());
                //是自动订阅且余额充足可自动扣费
                boolean needAutoLoad = mQueryContent.getAuto_sub() == AutoSubEnum.AUTO_SUB.getValue()
                        && mQueryContent.getChapter_pay_type() == ChapterEnum.AUTO_BUY.getCode();
                if (needAutoLoad) {
                    startRead = false;
                    mPresenter.getContent(mBookId, mQueryContent.getChapter_id());
                } else {
                    showPayChapterDialog();
                }
                mRightTitleLayout.setBuyImageState(!needAutoLoad);
                if (!needAutoLoad) {
                    if (BaseActivity.deviceWidth >= 1080) {
                        book_reader_title_textView.setMaxEms(4);
                    } else if (BaseActivity.deviceWidth < 1080 && BaseActivity.deviceWidth >= 720) {
                        book_reader_title_textView.setMaxEms(2);
                    } else if (BaseActivity.deviceWidth < 720) {
                        book_reader_title_textView.setMaxEms(1);
                    }
                }
            } else {
                mRightTitleLayout.setBuyImageState(false);
            }
            mReadProgressLayout.setTitle(getCurrentChapterTitle(chapter));
            if (mWidget != null) {
                mReadProgressLayout.setCount(mWidget.getChapterTotalPage());
                mReadProgressLayout.setProgress(mWidget.getCurrentProgress());
            }
            if (CollectionUtil.isEmpty(mChapterList)) {
                return;
            }
            for (int i = 0, size = mChapterList.size(); i < size; i++) {
                BookChapterResponse catalog = mChapterList.get(i);
                int chapter_id = catalog.getChapter_id();
                if (chapter_id == chapter) {
                    mChapterList.get(i).setChapterReadState(ChapterEnum.HAS_READ.getCode());
                    int loadPosition = i + 1;
                    if (loadPosition >= mChapterList.size()) {
                        return;
                    }
                    mLoadChapterId = mChapterList.get(loadPosition).getChapter_id();
                    if (!isCurrentChapterExists(mLoadChapterId)) {
                        mPresenter.getContent(mBookId, mLoadChapterId,
                                SharedPreferencesUtil.getInstance().getInt(Constant.IS_BOOK_AUTO_SUB),
                                false);
                    }
                    break;
                }
            }
        }

        @Override
        public void onPageChanged(int chapter, int page) {
            if (!isChangeTextSize && !isDismiss) {
                hideReadBar();
            }
        }

        @Override
        public void onLoadChapterFailure(int chapter) {
            isClickPayChapter = false;
            if (!isDismiss) {
                hideReadBar();
            }
            mCurrentChapter = chapter;
            mChapterDb.updateReadState(mBookId, mCurrentChapter);
            updateReadState();
            mLoadChapterId = mCurrentChapter;
            startRead = false;
            mPresenter.getContent(mBookId, mCurrentChapter);
        }

        @Override
        public void onCenterClick() {
            toggleReadBar();
        }

        @Override
        public void onFlip() {
        }
    };

    private void updateReadState() {
        int position = BookUtil.getCurrentChapterPosition(mChapterList, mCurrentChapter);
        mChapterList.get(position).setChapterReadState(ChapterEnum.HAS_READ.getCode());
    }

    private void updateCurrentProgress(int chapterId) {
        if (CollectionUtil.isEmpty(mChapterList)) {
            return;
        }
        UpdateProgressModel model = new UpdateProgressModel();
        model.setBookId(mBookId);
        model.setChapterId(chapterId);
        model.setChapterIndex(getChapterIndex(chapterId));
        model.setChapterTotal(mChapterList.size());
        EventBus.getDefault().post(model);
    }

    private int getChapterIndex(int chapterId) {
        int index = 0;
        for (int i = 0; i < mChapterList.size(); i++) {
            BookChapterResponse bookChapter = mChapterList.get(i);
            if (bookChapter.getChapter_id() == chapterId) {
                index = i + 1;
                break;
            }
        }
        return index;
    }

    public void onEventMainThread(PayResultEnum resultEnum) {
        if (resultEnum == PayResultEnum.SUCCESS) {
            startRead = false;
            showAutoSubDialog();
            mPresenter.getContent(mBookId, mCurrentChapter, AutoSubEnum.AUTO_SUB.getValue());
        }
    }

    public void showAutoSubDialog() {
        int isAutoSub = SharedPreferencesUtil.getInstance().getInt(Constant.IS_BOOK_AUTO_SUB);
        if (isAutoSub == AutoSubEnum.AUTO_SUB.getValue()) {
            return;
        }
        boolean hasShowDialog = SharedPreferencesUtil.getInstance()
                .getBoolean(Constant.HAS_SHOW_AUTO_SUB_PROMPT_DIALOG);
        if (hasShowDialog) {
            return;
        }
        AlertDialog autoSubDialog = DialogUtil.getAutoSubDialog(this);
        autoSubDialog.show();
        SharedPreferencesUtil.getInstance()
                .putBoolean(Constant.HAS_SHOW_AUTO_SUB_PROMPT_DIALOG, true);
    }

    private ReadDialogListener mDialogListener = new ReadDialogListener() {
        @Override
        public void clickCancelPay() {
            toggleReadBar();
        }

        @Override
        public void clickPayChapter() {
            isClickPayChapter = true;
            int payType = 0;
            if (mQueryContent != null) {
                payType = mQueryContent.getChapter_pay_type();
            }
            if (payType == ChapterEnum.TO_PAY_PAGE.getCode()) {
                ActivityUtil.toWebViewActivity(mContext, Constant.H5_PAY_URL, true);
                return;
            }
            showAutoSubDialog();
            startRead = false;
            mPresenter.getContent(mBookId, mCurrentChapter, AutoSubEnum.AUTO_SUB.getValue());
        }

        @Override
        public void clickSetting() {
            ActivityUtil.toSettingActivity(mContext);
        }
    };

    private String getCurrentChapterTitle(int chapter) {
        String title = "";
        if (CollectionUtil.isEmpty(mChapterList)) {
            return title;
        }
        for (BookChapterResponse response : mChapterList) {
            if (response.getChapter_id() == chapter) {
                title = response.getChapter_title();
                break;
            }
        }
        return title;
    }

    @Override
    public void showNetWorkProgress() {
        showDialog();
    }

    @Override
    public void disMissProgress() {
        if (hasInitWidget) {
            dismissDialog();
        }
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    private synchronized void hideReadBar() {
        dismissView(mTitleLayout, mDismissView);
        hideStatusBar();
    }

    private synchronized void showReadBar() {
        if (mTitleLayout.getVisibility() == View.GONE) {
            mTitleLayout.setVisibility(View.VISIBLE);
        }
        if (mBottomLayout.getVisibility() == View.GONE) {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
        showView(mTitleLayout, mBottomLayout);
    }

    private synchronized void toggleReadBar() {
        if (isDismiss) {
            showReadBar();
        } else {
            hideReadBar();
        }
    }

    private void dismissView(View titleView, View bottomView) {
        isDismiss = true;
        if (titleView != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(titleView, View.TRANSLATION_Y, 0, -mTitleHeight);
            animator.setDuration(ANIMATION_TIME);
            animator.start();
        }

        if (bottomView != null) {
            doBottomAnimation(bottomView, false, bottomView == mReadProgressLayout);
        }
        mShowView = mBottomLayout;
    }

    private ProgressCallback mProgressCallback = new ProgressCallback() {
        @Override
        public void sendProgress(float progress) {
            mReadProgressLayout.setPercent(progress);
        }
    };

    private ReadProgressLayout.PositionCallback mPositionCallback = new ReadProgressLayout.PositionCallback() {
        @Override
        public void sendPosition(int position) {
            if (mWidget != null) {
                mWidget.setSelectPage(position);
            }
        }
    };

    private TextSizeCallback mTextSizeCallback = new TextSizeCallback() {
        @Override
        public void sendTextSize(int size) {
            if (mWidget != null) {
                mWidget.setFontSize(ScreenUtil.dpToPxInt(size));
                mWidget.setChapterTotalPage();
                mReadProgressLayout.setCount(mWidget.getChapterTotalPage());
            }
        }
    };

    private void showView(View titleView, View bottomView) {
        isDismiss = false;
        if (titleView != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(titleView, View.TRANSLATION_Y, -mTitleHeight, 0);
            animator.setDuration(ANIMATION_TIME);
            animator.start();
        }

        if (bottomView != null) {
            doBottomAnimation(bottomView, true);
        }
        mDismissView = mBottomLayout;
    }

    private void doBottomAnimation(View view, boolean isShow) {
        doBottomAnimation(view, isShow, false);
    }

    /**
     * @param isShowProgress 是否是点击调节进度
     */
    private void doBottomAnimation(View view, boolean isShow, boolean isShowProgress) {
        isChangeTextSize = isShow && view == mTextSizeLayout;
        mBottomHeight = isShowProgress ? mReadProgressHeight : mTempHeight;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y,
                isShow ? mBottomHeight : 0, isShow ? 0 : mBottomHeight);
        animator.setDuration(ANIMATION_TIME);
        animator.start();
    }

    private BaseReadView.TouchPageListener mTouchPageListener = new BaseReadView.TouchPageListener() {
        @Override
        public void touchPage() {
            isChangeTextSize = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mBookId != 0 && mCurrentChapter != 0 && mChapterList != null && mChapterList.size() != 0) {
                for (int i = 0; i < mChapterList.size(); i++) {
                    if (mCurrentChapter == mChapterList.get(i).getChapter_id()) {//用于书架展示上次阅读
                        SharedPreferencesUtil.getInstance().putString(LAST_CHAPTER + mBookId, mChapterList.get(i).getChapter_title());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.isNull();
        EventBus.getDefault().unregister(this);
        try {
            unregisterReceiver(mReadReceiver);
        } catch (Exception e) {
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void setChapter(List<BookChapterResponse> catalogList) {
        if (CollectionUtil.isEmpty(catalogList)) {
            ToastUtil.showSingleToast("章节列表为空" + mBookId);
            return;
        }
        if (mChapterList == null) {
            mChapterList = new ArrayList<>();
        }
        mChapterList.clear();
        mChapterList.addAll(catalogList);
        saveChapterToDb();
        mChapterAdapter.notifyDataSetChanged();
        //添加标题
        BookDetailResponse shareDetail = mNewBookDetail != null ?
                mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
        if (shareDetail != null && shareDetail.getBook_name() != null && !shareDetail.getBook_name().isEmpty()) {
            book_reader_title_textView.setText(shareDetail.getBook_name());
        }
        if (mOldBookDetail != null || mNewBookDetail != null) {
            mBookDb.updateChapterTime(mBookId);
        }
        if (mCurrentChapter == 0) {
            mCurrentChapter = mChapterList.get(0).getChapter_id();
            mChapterDb.updateReadState(mBookId, mCurrentChapter);
            mChapterList.get(0).setChapterReadState(ChapterEnum.HAS_READ.getCode());
            SettingManager.DEFAULT_LAST_CHAPTER = mCurrentChapter;
        }
        if (mWidget == null) {
            mPresenter.getContent(mBookId, mCurrentChapter);
        }
    }

    private void saveChapterToDb() {
        Observable.just(1).map(new Function<Integer, Object>() {

            @Override
            public Integer apply(Integer integer) {
                mChapterDb.insert(mChapterList, mBookId);
                return null;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {

                    Disposable disposable;

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                    }

                });
    }

    @Override
    public void toChannelLogin() {
        if (mCurrentChapter != mLoadChapterId) {
            return;
        }
        if (mWidget != null) {
            mWidget.setCurrentChapter();
        }
        HuaXiSDK.getInstance().toLoginPage(mContext);
    }

    @Override
    public void setBookDetail(BookDetailResponse data) {
        mNewBookDetail = data;
        judgeChapterNeedLoad();
        if (mOldBookDetail != null) {
            mBookDb.update(data, mOldBookDetail.getBook_add_shelf());
            return;
        }
//        Log.d("book_details1", data.toString());
        mBookDb.insert(data, BookEnum.NOT_ADD_SHELF);
        mRecentBookDb.insert(data);
    }

    public void IsActive() {
        finish();
    }

}
