package com.spriteapp.booklibrary.ui.activity;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.callback.ProgressCallback;
import com.spriteapp.booklibrary.callback.ReaderMoreSettingCallback;
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
import com.spriteapp.booklibrary.enumeration.PayResultEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.listener.DialogListener;
import com.spriteapp.booklibrary.listener.ReadDialogListener;
import com.spriteapp.booklibrary.manager.SettingManager;
import com.spriteapp.booklibrary.manager.ThemeManager;
import com.spriteapp.booklibrary.model.AddBookModel;
import com.spriteapp.booklibrary.model.UpdateProgressModel;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.ui.adapter.ChapterAdapter;
import com.spriteapp.booklibrary.ui.dialog.GuessYouLikeDialog;
import com.spriteapp.booklibrary.ui.fragment.PersonCenterFragment;
import com.spriteapp.booklibrary.ui.presenter.SubscriberContentPresenter;
import com.spriteapp.booklibrary.ui.view.SubscriberContentView;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.BookUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.DialogUtil;
import com.spriteapp.booklibrary.util.HistoryTime;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.PreferenceHelper;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;
import com.spriteapp.booklibrary.widget.PtrClassicDefaultHeader;
import com.spriteapp.booklibrary.widget.PtrFrameLayout;
import com.spriteapp.booklibrary.widget.PtrHandler;
import com.spriteapp.booklibrary.widget.ReadBottomLayout;
import com.spriteapp.booklibrary.widget.ReadMoreSettingLayout;
import com.spriteapp.booklibrary.widget.ReadProgressLayout;
import com.spriteapp.booklibrary.widget.ReadRightTitleLayout;
import com.spriteapp.booklibrary.widget.readview.Config;
import com.spriteapp.booklibrary.widget.readview.MyPageWidget;
import com.spriteapp.booklibrary.widget.readview.OnReadStateChangeListener;
import com.spriteapp.booklibrary.widget.readview.PageFactory2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class ReadActivity extends TitleActivity implements SubscriberContentView {

    private static final String TAG = "ReadActivity";
    public static final String BOOK_DETAIL_TAG = "BookDetailTag";
    private static final int ANIMATION_TIME = 300;
    public static final String LAST_CHAPTER = "last_chapter";
    private MyPageWidget bookpage;
    private PtrFrameLayout mPtrFrameLayout;
    private ReadMoreSettingLayout readMoreSettingLayout;
    private ReadBottomLayout mBottomLayout;
    private SubscriberContentPresenter mPresenter;
    private Config config;
    protected PageFactory2 pagefactory = null;
    private List<BookChapterResponse> mChapterList;
    private int mTitleHeight;
    private boolean isDismiss = true;
    private LinearLayout linear_left_view;
    private ListView mChapterListView;
    private RelativeLayout download_btn;
    private LinearLayout mChapterLayout;
    private LinearLayout mProgressLayout;
    private LinearLayout mModeLayout;
    private LinearLayout mTextLayout;
    private DrawerLayout mDrawerLayout;
    private ChapterDb mChapterDb;
    private ChapterAdapter mChapterAdapter;
    //    private TextSizeLayout mTextSizeLayout;
    private TextView book_reader_title_textView;
    private ImageView is_add_shelf;

    /**
     * 是否开始阅读章节
     **/
    private boolean startRead = false;
    private boolean selectChapter = false;
    private static int mBookId;
    private int mCurrentChapter;
    private ReadProgressLayout mReadProgressLayout;
    private boolean isNight;
    private View mShowView;
    private View mDismissView;
    private int mBottomHeight;
    private int mReadProgressHeight;
    private int mTempHeight;
    private boolean hasNotifyAdapter;
    private Context mContext;
    private AlertDialog mPayChapterDialog;
    private int mLoadChapterId;
    private BookDb mBookDb;
    private BookDetailResponse mOldBookDetail;
    private BookDetailResponse mNewBookDetail;
    private ReadRightTitleLayout mRightTitleLayout;
    private ContentDb mContentDb;
    private SubscriberContent mQueryContent;
    private SimpleDateFormat mDateFormat;
    private ReadReceiver mReadReceiver;
    //判断是否是通过点击dialog购买章节,购买成功后进行toast提示
    private boolean isClickPayChapter;
    private RecentBookDb mRecentBookDb;
    private PopupWindow popupWindow;//书籍详情与分享
    private int chapter = 0;
    private boolean IsRegister = true;
    private String titleFile = "";
    private boolean isAddOrClean = false;

    public final int BOOKDETAIL = 1;
    public final int BOOKCHAPTER = 2;
    public final int BOOKCONTENT = 3;


    //上传阅读时长
    public static int READTIME = 120;//测试10秒,正式使用用户信息里的字段，如果没有则默认使用120秒;
    public static int time = 120;
    private static int oldChapter_id;
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (time == 0) {
                        if (mBookId != 0 && oldChapter_id != 0)
                            HistoryTime.getHistory(mBookId + "", oldChapter_id + "");
                    } else if (time > 0) {
                        time--;
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
                case BOOKDETAIL:
                    bookDetailResult();
                    break;
                case BOOKCHAPTER:
                    List<BookChapterResponse> catalogList = (List<BookChapterResponse>) msg.obj;
                    bookChapterDataResult(catalogList);
                    break;
                case BOOKCONTENT:
                    Base<SubscriberContent> result = (Base<SubscriberContent>) msg.obj;
                    bookContentResult(result);
                    break;
            }
        }
    };

    @Override
    public void initData() {
        showReaderDialog("正在打开书本...");
        Config.createConfig(this);
        isNight = SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_NIGHT_MODE);

        if (IsRegister) {
            EventBus.getDefault().register(this);
        }

        if (AppUtil.isLogin() && UserBean.getInstance().getRead_timespan() != 0) {
            READTIME = UserBean.getInstance().getRead_timespan();
            time = READTIME;
        }

        //标题栏
        mRightTitleLayout = new ReadRightTitleLayout(this);
        mRightLayout.addView(mRightTitleLayout);
        mRightTitleLayout.setTitleListener(mTitleListener);
        mContext = this;
        Intent intent = getIntent();
        Log.d("getNotification", "跳转");
        String bookid = intent.getStringExtra("book_id");
        String push_id = intent.getStringExtra("push_id");
        String chapter_id = intent.getStringExtra("chapter_id");
        BookDetailResponse bookDetail = null;


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
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {//从网页跳转过来
                Log.d("readActivity--", "从网页跳转过来");
                Uri uri = intent.getData();
                if (uri != null) {
                    Log.d("readActivity--", "uri不为空");
                    bookDetail = new BookDetailResponse();
                    String book = uri.getQueryParameter("book_id");
                    String chapter = uri.getQueryParameter("chapter_id");
                    if (!TextUtils.isEmpty(book)) {//书籍id不为空
                        Log.d("readActivity--", "书籍id不为空");
                        bookDetail.setBook_id(Integer.parseInt(book));
                        bookDetail.setChapter_id(TextUtils.isEmpty(chapter) ? 0 : Integer.parseInt(chapter));
                    }
                }
            } else {
                bookDetail = (BookDetailResponse) intent.getSerializableExtra(BOOK_DETAIL_TAG);
            }
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
        mPtrFrameLayout.setBookId(mBookId);
        mOldBookDetail = mBookDb.queryBook(mBookId);
        isAddOrClean = BookUtil.isBookAddShelf(mOldBookDetail);
        is_add_shelf.setVisibility(isAddOrClean ? View.VISIBLE : View.GONE);
//        mRightTitleLayout.setAddShelfViewState(BookUtil.isBookAddShelf(mOldBookDetail));
        mChapterList = mChapterDb.queryCatalog(mBookId);
        int lastChapterId = SettingManager.getInstance().getLastChapter(String.valueOf(mBookId), 0);
        if (chapter == 0) {
            mCurrentChapter = bookDetail.getChapter_id();
        } else {
            mCurrentChapter = chapter;//使用推送的章节id
        }

        if (mCurrentChapter == 0) {
            Log.d("mCurrentChapter", "上一次chapter_id" + lastChapterId);
            mCurrentChapter = lastChapterId;
        } else {
            if (mCurrentChapter != lastChapterId) {
                SettingManager.getInstance().saveReadProgress(String.valueOf(mBookId), mCurrentChapter, 0, 0);
            }
        }

        initChapterAdapter();
        openChapter();

        if (mOldBookDetail == null) {
            //请求书的详情
            mPresenter.getBookDetail(mBookId);
        } else {//缓存显示
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


    class ReadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                Log.e(TAG, Intent.ACTION_BATTERY_CHANGED);
                int level = intent.getIntExtra("level", 0);
                if (pagefactory != null)
                    pagefactory.updateBattery(level);
            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                Log.e(TAG, Intent.ACTION_TIME_TICK);
                if (pagefactory != null)
                    pagefactory.updateTime();
            }
        }
    }

    private void judgeChapterNeedLoad() {
        if (CollectionUtil.isEmpty(mChapterList) || mOldBookDetail == null) {
            //请求章节目录
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


    /**
     * 打开内容阅读
     */
    private void openChapter() {
        if (!isCurrentChapterExists(mCurrentChapter) && mCurrentChapter != 0) {
            mPresenter.getContent(mBookId, mCurrentChapter);
            mLoadChapterId = mCurrentChapter;
            return;
        }

        if (!CollectionUtil.isEmpty(mChapterList)) {
            int status;
            int pos[];
            //首次打开阅读
            if (pagefactory == null) {
                pagefactory = new PageFactory2(this, mBookId, mChapterList);
                config = Config.getInstance();
                bookpage.setPageMode(config.getPageMode());
                pagefactory.setPageWidget(bookpage);
                pagefactory.setOnReadStateChangeListener(mReadListener);
                pagefactory.setProgressCallback(mProgressCallback);
            }


            //切换章节阅读
            if (!startRead && selectChapter) {
                startRead = true;
                selectChapter = false;
                pos = new int[]{mCurrentChapter, 0, 0};
            } else {//正常打开
                pos = SettingManager.getInstance().getReadProgress(mBookId + "");
            }
            status = pagefactory.openBook(pos[0], new int[]{pos[1], pos[2]}, true);
            if (status == 0) {
                mReadListener.onLoadChapterFailure(mCurrentChapter);
            } else {
                BookDetailResponse shareDetail = mNewBookDetail != null ?
                        mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
                //更多设置里面内容显示
                if (readMoreSettingLayout != null) {
                    readMoreSettingLayout.initRaderSetting(shareDetail);
                }
                if (bookpage != null) {
                    bookpage.bookDetail(this,shareDetail);
                }
                //延迟关闭，防止黑屏闪过
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissReaderDialog();
                    }
                }, 50);
//                tv_open_hint.setVisibility(View.GONE);
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
        mChapterAdapter = new ChapterAdapter(this, mChapterList, mBookId);
        mChapterListView.setAdapter(mChapterAdapter);
        mChapterListView.setSelectionFromTop(
                BookUtil.getCurrentChapterPosition(mChapterList, mCurrentChapter),
                ScreenUtil.getScreenHeight() / 2);
    }

    @Override
    public void configViews() {
        setMode();
        mTitleLayout.measure(0, 0);
        mTitleHeight = mTitleLayout.getMeasuredHeight();
        mBottomLayout.measure(0, 0);
        mBottomHeight = mBottomLayout.getMeasuredHeight();
        mTempHeight = mBottomHeight;
        mReadProgressLayout.measure(0, 0);
        mReadProgressHeight = mReadProgressLayout.getMeasuredHeight();
        mTitleLayout.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT < Constant.ANDROID_P_API) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {//android p 沉浸式标题栏
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            int statusH = Util.getStatusBarHeight(this);
            is_add_shelf.setPadding(0, statusH + Util.dp2px(this, 10), 0, 0);
            mChapterListView.setPadding(0, statusH, 0, 0);
        }

        bookpage.setTouchListener(new MyPageWidget.TouchListener() {
            @Override
            public Boolean center() {
                toggleReadBar();
                return isDismiss;
            }

            @Override
            public Boolean prePage() {
                if (!isDismiss) {
                    return false;
                }

                if (pagefactory != null)
                    pagefactory.prePage();
                if (pagefactory != null && pagefactory.isfirstPage()) {
                    return false;
                }

                return true;
            }

            @Override
            public Boolean nextPage() {
                if (!isDismiss) {
                    return false;
                }

                if (pagefactory != null)
                    pagefactory.nextPage();

                if (pagefactory != null && pagefactory.islastPage()) {
                    return false;
                }
                return true;
            }

            @Override
            public void cancel() {
                pagefactory.cancelPage();
            }
        });

    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.mPtrFrameLayout);
        bookpage = (MyPageWidget) findViewById(R.id.book_reader_read_container);
        mBottomLayout = (ReadBottomLayout) findViewById(R.id.book_reader_bottom_layout);
        readMoreSettingLayout = (ReadMoreSettingLayout) findViewById(R.id.readMoreSettingLayout);
        readMoreSettingLayout.setActivity(this);
        readMoreSettingLayout.setMoreSettingCallback(moreSettingCallback);
        linear_left_view = (LinearLayout) findViewById(R.id.linear_left_view);
        mChapterListView = (ListView) findViewById(R.id.book_reader_catalog_list_view);
        download_btn = (RelativeLayout) findViewById(R.id.download_btn);
        mChapterLayout = (LinearLayout) findViewById(R.id.book_reader_chapter_layout);
        mProgressLayout = (LinearLayout) findViewById(R.id.book_reader_progress_layout);
        mModeLayout = (LinearLayout) findViewById(R.id.book_reader_mode_layout);
        mTextLayout = (LinearLayout) findViewById(R.id.book_reader_text_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.book_reader_drawer_layout);
        mReadProgressLayout = (ReadProgressLayout) findViewById(R.id.book_reader_read_progress_layout);
//        mTextSizeLayout = (TextSizeLayout) findViewById(R.id.book_reader_text_size_layout);

        book_reader_title_textView = (TextView) findViewById(R.id.book_reader_title_textView);
//        book_reader_title_textView.setMaxEms(6);
        book_reader_title_textView.setVisibility(View.GONE);
//        book_reader_title_textView.setTextColor(ContextCompat.getColor(this, R.color.book_reader_reader_text_color));
        mShowView = mBottomLayout;
        mDismissView = mBottomLayout;
//        mTextSizeLayout.setCallBack(mTextSizeCallback);
        mReadProgressLayout.setCallback(mPositionCallback);
        is_add_shelf = (ImageView) findViewById(R.id.is_add_shelf);
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
        mChapterLayout.setOnClickListener(this);
        mProgressLayout.setOnClickListener(this);
        mModeLayout.setOnClickListener(this);
        mTextLayout.setOnClickListener(this);
        download_btn.setOnClickListener(this);
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
                selectChapter = true;
                openChapter();
                mDrawerLayout.closeDrawer(linear_left_view);
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

        //下拉操作
        final PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(this);
        header.setAddOrClean(!isAddOrClean);
        mPtrFrameLayout.setPtrUIHandler(header);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                /**
                 *检查是否可以刷新，这里使用默认的PtrHandler进行判断
                 */
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                //刷新操作
                if (!isDismiss)
                    dismissView(mTitleLayout, mDismissView);//隐藏顶部和底部栏
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!AppUtil.isLogin(ReadActivity.this)) {
                            mPtrFrameLayout.refreshComplete();
                            return;
                        }
                        if (isAddOrClean) {//书架中已存在,移除
                            addToShelf(true, true);
                            is_add_shelf.setVisibility(View.GONE);
                            isAddOrClean = false;

                            BookDetailResponse bookDetail = mBookDb.queryBook(mBookId);
                            if (bookDetail != null || BookUtil.isBookAddShelf(bookDetail)) {
                                if (AppUtil.isLogin()) {
                                    bookDetail.setBook_add_shelf(0);
                                    mBookDb.update(bookDetail, 0);
                                }
                            }

                        } else {//加书架
                            addToShelf(true, false);
                            is_add_shelf.setVisibility(View.VISIBLE);
                            isAddOrClean = true;
                        }
                        header.setAddOrClean(!isAddOrClean);
                        mPtrFrameLayout.refreshComplete();
                    }
                }, 300);
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
            mDrawerLayout.openDrawer(linear_left_view);
        } else if (v == mProgressLayout) {
            if (pagefactory != null) {
                mReadProgressLayout.setProgress(pagefactory.getCurrentProgress());
            }
            //获取进度
            mReadProgressLayout.setVisibility(View.VISIBLE);
            mShowView = mReadProgressLayout;
            mDismissView = mBottomLayout;
            doBottomAnimation(mShowView, true, true);
            doBottomAnimation(mDismissView, false);
            mDismissView = mReadProgressLayout;
        } else if (v == mModeLayout) {//夜间模式切换
            changeMode();
        } else if (v == mTextLayout) {//更多
            readMoreSettingLayout.setVisibility(View.VISIBLE);
            status_height.setVisibility(View.GONE);
            mShowView = readMoreSettingLayout;
            mDismissView = mBottomLayout;
            doBottomAnimation(mDismissView, false);
            doBottomAnimation(mShowView, true);
            dismissView(mTitleLayout, mDismissView);
            mDismissView = readMoreSettingLayout;
        } else if (v == mLeftLayout) {
            showAddShelfPrompt();
        } else if (v == download_btn) {
            ActivityUtil.toDownloadChapterActivity(this, mBookId);
        }
    }

    /**
     * 加书架弹窗
     */
    boolean isRecommend = false;

    private void showAddShelfPrompt() {
        if (mBookDb == null) {
            finish();
            return;
        }
        final BookDetailResponse bookDetail = mBookDb.queryBook(mBookId);
        if (bookDetail == null || BookUtil.isBookAddShelf(bookDetail)) {
            if (AppUtil.isLogin()) {
                addToShelf(true, false);//书架中已存在但要刷新顺序
            }
            finish();
            return;
        }
        if (isRecommend) {
            finish();
            return;
        }

        DialogUtil.showCommonDialog(this, getResources().getString(R.string.book_reader_sure_to_add_shelf),
                new DialogListener() {
                    @Override
                    public void clickCancel() {
                        isRecommend = true;
                        new GuessYouLikeDialog(ReadActivity.this, bookDetail, 2).show();
                    }

                    @Override
                    public void clickSure() {
                        addToShelf(false, false);
                        finish();
                    }
                });
    }

    private boolean showAddShelfPrompt1() {//是否存入书架头部布尔值
        if (mBookDb == null) {
            return false;
        }
        BookDetailResponse bookDetail = mBookDb.queryBook(mBookId);
        if (bookDetail == null || BookUtil.isBookAddShelf(bookDetail)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (readMoreSettingLayout != null && readMoreSettingLayout.getVisibility() == View.VISIBLE) {
            readMoreSettingLayout.setVisibility(View.GONE);
            return;
        } else if (mOldBookDetail != null || mNewBookDetail != null) {
            showAddShelfPrompt();
            return;
        }
        super.onBackPressed();
    }

    private void changeMode() {
        isNight = !isNight;
        SharedPreferencesUtil.getInstance().putBoolean(Constant.IS_NIGHT_MODE, isNight);
        setMode();
    }

    private void setMode() {
        Log.d("setMode", "isNight===" + isNight);
//        mTextSizeLayout.changeMode(isNight);
        mReadProgressLayout.changeMode(isNight);
        mBottomLayout.changeMode(isNight);
        mRightTitleLayout.changeMode(isNight);
        readMoreSettingLayout.changeMode(isNight);

        is_add_shelf.setImageResource(isNight ? R.mipmap.add_shelf_night_icon : R.mipmap.add_shelf_yellow_icon);

        int bg_color = SharedPreferencesUtil.getInstance().getInt(Constant.READ_PAGE_BG_COLOR, 1);
        if (pagefactory != null) {
            pagefactory.setDayOrNight(isNight);
        }
        status_height.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background :
                R.color.book_reader_white);
        mTitleLayout.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background :
                R.color.book_reader_white);
        mLineView.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background :
                R.color.book_reader_divide_line_color);
        mChapterListView.setBackgroundResource(isNight ? R.color.book_reader_chapter_night_background :
                R.color.book_reader_white);
        linear_left_view.setBackgroundResource(isNight ? R.color.book_reader_chapter_night_background :
                R.color.book_reader_white);
        mChapterListView.setSelector(isNight ? R.drawable.book_reader_common_press_night_selector :
                R.drawable.book_reader_common_press_selector);
        //标题
//        book_reader_title_textView.setTextColor(ContextCompat.getColor(this, isNight ? R.color.night_title : R.color.book_reader_home_bottom_text_choose_color));
        if (mChapterAdapter != null) {
            mChapterAdapter.setNight(isNight);
            mChapterAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 标题栏的点击事件
     */
    private ReadRightTitleLayout.ReadTitleListener mTitleListener =
            new ReadRightTitleLayout.ReadTitleListener() {
                @Override
                public void clickBuy() {
                    toggleReadBar();
                    showPayChapterDialog();
                }

                @Override
                public void clickAddShelf() {//下载
//                    addToShelf(false);
                    ActivityUtil.toDownloadChapterActivity(ReadActivity.this, mBookId);
                }

                @Override
                public void clickComment() {//评论
                    ActivityUtil.toBookCommentActivity(ReadActivity.this,mNewBookDetail==null?mOldBookDetail:mNewBookDetail);
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
                    //弹出pop
                    showpopWindow(mRightTitleLayout);
                }
            };

    private void addToShelf(boolean isShelf, boolean isClean) {
        AddBookModel model = new AddBookModel();
        model.setBookId(mBookId);
        model.setChapterId(mCurrentChapter);
        model.setAddShelf(isShelf);//刷新纪录的标识,是否弹出提示Toast
        model.setClean(isClean);
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
                    HuaXiSDK.getInstance().showShareDialog(mContext, shareDetail, isNight, 1);
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (HomeActivity.ISHAUDU && oldChapter_id != 0 && time != READTIME && time < READTIME && time != 0 && handler != null && AppUtil.isLogin()) {
            Log.d("handleMessage", "onRestart===" + time);
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (HomeActivity.ISHAUDU && handler != null && AppUtil.isLogin()) {
            Log.d("handleMessage", "onStop====" + time);
            handler.removeCallbacksAndMessages(null);
        }
    }

    private OnReadStateChangeListener mReadListener = new OnReadStateChangeListener() {
        @Override
        public void onChapterChanged(int chapter) {

            isClickPayChapter = false;
            updateCurrentProgress(chapter);
            oldChapter_id = mCurrentChapter;
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
//                    if (BaseActivity.deviceWidth >= 1080) {
//                        book_reader_title_textView.setMaxEms(4);
//                    } else if (BaseActivity.deviceWidth < 1080 && BaseActivity.deviceWidth >= 720) {
//                        book_reader_title_textView.setMaxEms(2);
//                    } else if (BaseActivity.deviceWidth < 720) {
//                        book_reader_title_textView.setMaxEms(1);
//                    }
                }
            } else {
                mRightTitleLayout.setBuyImageState(false);
            }

            if (pagefactory != null) {
                mReadProgressLayout.setCount(pagefactory.getChapterTotalPage());
                mReadProgressLayout.setProgress(pagefactory.getCurrentProgress());
            }
            if (CollectionUtil.isEmpty(mChapterList)) {
                return;
            }
            for (int i = 0, size = mChapterList.size(); i < size; i++) {
                BookChapterResponse catalog = mChapterList.get(i);
                int chapter_id = catalog.getChapter_id();
                if (chapter_id == chapter) {
                    mReadProgressLayout.setTitle(catalog.getChapter_title());

                    if (HomeActivity.ISHAUDU && AppUtil.isLogin() && catalog.getChapter_is_vip() == 0 && oldChapter_id == chapter) {//当前章节等于跳转章节并且跳转章节为免费


                        Log.d("onChapterChanged", "选取章节等于" + "当前章节ID===" + oldChapter_id + "跳转的章节ID===" + chapter);
                        time = READTIME;
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(0, 1000);//一秒


                    } else if (HomeActivity.ISHAUDU && AppUtil.isLogin() && catalog.getChapter_is_vip() == 0 && oldChapter_id != chapter) {//当前章节不等于跳转章节并且跳转章节为免费

                        Log.d("onChapterChanged", "选取章节不等于" + "当前章节ID===" + oldChapter_id + "跳转的章节ID===" + chapter);
                        time = READTIME;
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(0, 1000);//一秒
                    }


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
            if (!isDismiss) {
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
        public void onCenterClick() {//中间点击
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
        public void clickCancelPay() {//取消购买
//            toggleReadBar();
        }

        @Override
        public void clickPayChapter() {//购买章节
            isClickPayChapter = true;
            int payType = 0;
            if (mQueryContent != null) {
                payType = mQueryContent.getChapter_pay_type();
            }
            if (payType == ChapterEnum.TO_PAY_PAGE.getCode()) {
//                ActivityUtil.toWebViewActivity(mContext, Constant.H5_PAY_URL, true);
                ActivityUtil.toRechargeActivity(ReadActivity.this);
                return;
            }
            showAutoSubDialog();
            startRead = false;
            mPresenter.getContent(mBookId, mCurrentChapter, AutoSubEnum.AUTO_SUB.getValue());
        }

        @Override
        public void clickSetting() {
            PersonCenterFragment.toSetting = 1;
            ActivityUtil.toSettingActivity(mContext);
        }
    };


    @Override
    public void showNetWorkProgress() {
        if (selectChapter)
            showDialog();
    }

    @Override
    public void disMissProgress() {
        if (selectChapter)
            dismissDialog();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    private synchronized void hideReadBar() {
        dismissView(mTitleLayout, mDismissView);
        status_height.setVisibility(View.GONE);
//        hideStatusBar();
    }

    private synchronized void showReadBar() {
        if (mTitleLayout.getVisibility() == View.GONE) {
            mTitleLayout.setVisibility(View.VISIBLE);
        }
        if (mBottomLayout.getVisibility() == View.GONE) {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
//        showStatusBar();
        showView(mTitleLayout, mBottomLayout);
        if (Build.VERSION.SDK_INT >= Constant.ANDROID_P_API)
            status_height.setVisibility(View.VISIBLE);
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

    //阅读进度显示
    private ProgressCallback mProgressCallback = new ProgressCallback() {
        @Override
        public void sendProgress(float progress) {
            mReadProgressLayout.setPercent(progress);
        }
    };

    //滑动进度，读取内容
    private ReadProgressLayout.PositionCallback mPositionCallback = new ReadProgressLayout.PositionCallback() {
        @Override
        public void sendPosition(int position) {
            pagefactory.changeChapter(position);
        }
    };

    /**
     * 更多设置
     */
    ReaderMoreSettingCallback moreSettingCallback = new ReaderMoreSettingCallback() {
        @Override
        public void sendTextSize(float textSize) {
            if (pagefactory != null) {
                pagefactory.changeFontSize(ScreenUtil.dpToPxInt(textSize));

                mReadProgressLayout.setCount(pagefactory.getChapterTotalPage());
            }
        }

        @Override
        public void sendFontStyle(Typeface typeface) {
            if (pagefactory != null) {
                pagefactory.changeTypeface(typeface);
            }
        }

        @Override
        public void sendFontFormat(int format) {
            if (pagefactory != null) {
                pagefactory.setFontSpace(format);
                mReadProgressLayout.setCount(pagefactory.getChapterTotalPage());
            }
        }

        @Override
        public void sendReaderBgColor(int color) {
            if (isNight)
                changeMode();

            if (pagefactory != null)
                pagefactory.changeBookBg(color);
        }

        @Override
        public void sendPageMode(int pageMode) {
            if (pagefactory != null)
                pagefactory.setPageMode(pageMode);
        }

        @Override
        public void sendProgressFormat(int progressFormat) {
            if (pagefactory != null)
                pagefactory.setPProgressFormat(progressFormat);
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
        mBottomHeight = isShowProgress ? mReadProgressHeight : mTempHeight;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y,
                isShow ? mBottomHeight : 0, isShow ? 0 : mBottomHeight);
        animator.setDuration(ANIMATION_TIME);
        animator.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mBookId != 0 && mCurrentChapter != 0 && mChapterList != null && mChapterList.size() != 0) {
                for (int i = 0; i < mChapterList.size(); i++) {
                    if (mCurrentChapter == mChapterList.get(i).getChapter_id()) {//用于书架展示上次阅读
                        if (!TextUtils.isEmpty(mChapterList.get(i).getChapter_title())) {
                            PreferenceHelper.putString(LAST_CHAPTER + UserBean.getInstance().getUser_id() + mBookId, mChapterList.get(i).getChapter_title());
                            if (showAddShelfPrompt1())//书籍在书架上面才存入true
                                SharedPreferencesUtil.getInstance().putBoolean(LAST_CHAPTER, true);
                        }
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
        pagefactory = null;
        bookpage = null;
    }


    /**
     * 加载阅读内容完成《3》
     *
     * @param result
     */
    @Override
    public void setData(Base<SubscriberContent> result) {
        Message msg = new Message();
        msg.obj = result;
        msg.what = BOOKCONTENT;
        handler.sendMessage(msg);
    }

    public void bookContentResult(Base<SubscriberContent> result) {
        String message = result.getMessage();
        int code = result.getCode();
        final SubscriberContent bookContentData = result.getData();
        if (bookContentData == null) {
//            tv_open_hint.setText("打开书本失败！");
            ToastUtil.showSingleToast(message);
            dismissReaderDialog();
            return;
        }
        String key = bookContentData.getChapter_content_key();
        String content = bookContentData.getChapter_content();
        if (StringUtil.isEmpty(key)) {
            ToastUtil.showSingleToast("key为空" + bookContentData.getChapter_id());
            return;
        }
        if (StringUtil.isEmpty(content)) {
            ToastUtil.showSingleToast("content为空" + bookContentData.getChapter_id());
            return;
        }
        judgeNeedUpdatePayPage(bookContentData);

        if (code == ApiCodeEnum.SUCCESS.getValue()) {
            bookContentData.setChapter_need_buy(ChapterEnum.DO_NOT_NEED_BUY.getCode());
            if (isClickPayChapter) {
                ToastUtil.showSingleToast(R.string.book_reader_buy_successful_text);
                isClickPayChapter = false;
            }
        } else {
            if (code == ChapterEnum.BALANCE_SHORT.getCode()) {
                bookContentData.setChapter_pay_type(ChapterEnum.TO_PAY_PAGE.getCode());
            } else if (code == ChapterEnum.UN_SUBSCRIBER.getCode()) {
                bookContentData.setChapter_pay_type(ChapterEnum.AUTO_BUY.getCode());
            }
            bookContentData.setChapter_need_buy(ChapterEnum.NEED_BUY.getCode());
        }

        //当前章节内容存在更新，否则存入
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isCurrentChapterExists(bookContentData.getChapter_id())) {
                    mContentDb.update(mBookId, bookContentData.getChapter_id(), bookContentData);
                } else {
                    mContentDb.insert(bookContentData);
                }
            }
        }).start();


        //开始阅读内容
        openChapter();

        //更新进度
        if (NetworkUtil.isAvailable(mContext)) {
            updateCurrentProgress(mCurrentChapter);
        }
    }


    /**
     * 加载章节完成《2》
     *
     * @param catalogList
     */
    @Override
    public void setChapter(List<BookChapterResponse> catalogList) {
        Message msg = new Message();
        msg.obj = catalogList;
        msg.what = BOOKCHAPTER;
        handler.sendMessage(msg);

    }

    public void bookChapterDataResult(List<BookChapterResponse> catalogList) {
        if (CollectionUtil.isEmpty(catalogList)) {
            ToastUtil.showSingleToast("章节列表为空" + mBookId);
            return;
        }
        if (mChapterList == null) {
            mChapterList = new ArrayList<>();
        }
        mChapterList.clear();
        mChapterList.addAll(catalogList);
        mChapterAdapter.notifyDataSetChanged();

        //添加标题
//        BookDetailResponse shareDetail = mNewBookDetail != null ?
//                mNewBookDetail : mOldBookDetail != null ? mOldBookDetail : null;
//        if (shareDetail != null && shareDetail.getBook_name() != null && !shareDetail.getBook_name().isEmpty()) {
//            book_reader_title_textView.setText(shareDetail.getBook_name());
//        }

        //保存章节目录
        saveChapterToDb();

        //首次打开本书，读第一章，存阅读记录
        if (mCurrentChapter == 0) {
            mCurrentChapter = mChapterList.get(0).getChapter_id();
            mChapterList.get(0).setChapterReadState(ChapterEnum.HAS_READ.getCode());
            SettingManager.DEFAULT_LAST_CHAPTER = mCurrentChapter;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //更新本地章节最后更新时间
                if (mOldBookDetail != null || mNewBookDetail != null) {
                    mBookDb.updateChapterTime(mBookId);
                }
                //更新阅读章节
                mChapterDb.updateReadState(mBookId, mCurrentChapter);
            }
        }).start();

        //获取阅读内容
        if (pagefactory == null) {
            mPresenter.getContent(mBookId, mCurrentChapter);
        }
    }


    /**
     * 加载书籍详情完成《1》
     *
     * @param data
     */
    @Override
    public void setBookDetail(final BookDetailResponse data) {
        mNewBookDetail = data;
        handler.sendEmptyMessage(BOOKDETAIL);
    }

    public void bookDetailResult() {

        judgeChapterNeedLoad();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mOldBookDetail != null) {
                    mBookDb.update(mNewBookDetail, mOldBookDetail.getBook_add_shelf());
                    return;
                }
                //插入数据库
                mBookDb.insert(mNewBookDetail, BookEnum.NOT_ADD_SHELF);
                mRecentBookDb.insert(mNewBookDetail);
            }
        }).start();
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
        if (pagefactory != null) {
            pagefactory.setCurrentChapter();
        }
        HuaXiSDK.getInstance().toLoginPage(mContext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99) {
            readMoreSettingLayout.refreshFontSelect(true);
        }
    }
}
