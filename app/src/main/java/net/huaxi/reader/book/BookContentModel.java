package net.huaxi.reader.book;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.BookCommentActivity;
import net.huaxi.reader.activity.BookDetailActivity;
import net.huaxi.reader.activity.DownLoadActivity;
import net.huaxi.reader.activity.LastPageActivity;
import net.huaxi.reader.activity.MoreSettingActivity;
import net.huaxi.reader.appinterface.MenuChangeListener;
import net.huaxi.reader.book.BookContentView.OnViewEventListener;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.book.paging.BookContentPaint;
import net.huaxi.reader.book.paging.PageContent;
import net.huaxi.reader.book.paging.PagingManager;
import net.huaxi.reader.book.render.BookContentSettingListener;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.dialog.CommonDailog;
import net.huaxi.reader.model.OpenBookSettingViewHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.BookShelfDataUtil;
import net.huaxi.reader.util.UMEventAnalyze;

import java.io.InputStream;
import java.lang.ref.SoftReference;

import static net.huaxi.reader.book.BookContentView.DIRECTION_LEFT;
import static net.huaxi.reader.book.BookContentView.DIRECTION_RIGHT;

/**
 * 阅读数据管理类
 * Created by taoyingfeng
 * 2015/12/11.
 */
public class BookContentModel implements IBookContentLoadedListener, OnViewEventListener, BookContentSettingListener,
        ChapterClickListener, MenuChangeListener {

    public static final String PARENT_ACTIVITY = "bookContentActivity";
    public static final int REQUEST_CODE_MORE_SETTING = 3010;
    BookContentView mBookContentView;
    BookContentBottomView mBookContentBottom;
    BookContentSettings mSettings;
    Activity activity;
    Button drawBtn;
    Button autoSubBtn;
    Button openVipBtn;
    Button closeBtn;
    int time = 0;
    Paint cleanPaint;
    /*  Bitmap对象 */
    private SoftReference<Bitmap> mBackgroundBitmap;
    private SoftReference<Bitmap> mOldContentPageBitmap;
    private SoftReference<Bitmap> mNewContentPageBitmap;
    /*  画布  */
    private Canvas mOldContentPageCanvas;
    private Canvas mNewContentPageCanvas;
    private Bitmap tempNewContentPageBitmap = null;
    private int bookState = ReadPageState.BOOKTYPE_NORMAL;
    private OpenBookSettingViewHelper openPopupHelper;
    private Bitmap tempOldContentPageBitmap = null;

    /**
     * 构造方法
     */
    public BookContentModel(Activity activity, BookContentView contentView) {
        this.activity = activity;
        this.mBookContentView = contentView;
        mSettings = BookContentSettings.getInstance();
        initCanvas();
    }

    public BookContentModel() {
        super();
    }

    public int getBookState() {
        return bookState;
    }

    public void setDrawBtn(Button drawBtn) {
        this.drawBtn = drawBtn;
    }

    public void setCloseBtn(Button closeBtn) {
        this.closeBtn = closeBtn;
    }

    public void setAutoSubBtn(Button autoSubBtn) {
        this.autoSubBtn = autoSubBtn;
    }

    public void setOpenVipBtn(Button openVipBtn) {
        this.openVipBtn = openVipBtn;
    }

    public void setBookContentBottom(BookContentBottomView mBookContentBottom) {
        this.mBookContentBottom = mBookContentBottom;
    }

    /**
     * 控制阅读底部状态栏
     */
    public void setBottomState(boolean visiable) {
        if (visiable) {
            mBookContentBottom.setVisibility(View.VISIBLE);
        } else {
            mBookContentBottom.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 初始化画布
     */
    private void initCanvas() {
        // 创建当前页以及下一页Bitmap
        int width = BookContentSettings.getInstance().getScreenWidth();
        int height = BookContentSettings.getInstance().getScreenHeight();


        if (mOldContentPageBitmap == null || (tempOldContentPageBitmap = mOldContentPageBitmap.get()) == null || tempOldContentPageBitmap
                .isRecycled()) {
            tempOldContentPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            mOldContentPageBitmap = new SoftReference<Bitmap>(tempOldContentPageBitmap);
        }


        if (mNewContentPageBitmap == null || (tempNewContentPageBitmap = mNewContentPageBitmap.get()) == null || tempNewContentPageBitmap
                .isRecycled()) {
            tempNewContentPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mNewContentPageBitmap = new SoftReference<Bitmap>(tempNewContentPageBitmap);
        }

        // 创建绘制当前页以及下一页的画布canvas
        if (tempOldContentPageBitmap == null) {
            mOldContentPageCanvas = new Canvas();
        } else {
            mOldContentPageCanvas = new Canvas(tempOldContentPageBitmap);
        }
        if (tempNewContentPageBitmap == null) {
            mNewContentPageCanvas = new Canvas();
        } else {
            mNewContentPageCanvas = new Canvas(tempNewContentPageBitmap);
        }
        //把Bitmap传递给自定义View,用来渲染内容;
        mBookContentView.setBitmaps(tempOldContentPageBitmap, tempNewContentPageBitmap);
        mBookContentView.setOnViewEventListener(this);
        ReadPageFactory.getSingleton().setOldCanvas(mOldContentPageCanvas);
        ReadPageFactory.getSingleton().setNewCanvas(mNewContentPageCanvas);
        ReadPageFactory.getSingleton().setBackgoundBitmap(createBackground(BookContentSettings.getInstance().getTheme()));
        setIsInitCatalog(false);

    }

    public void postInvalidate() {
        mBookContentView.postInvalidateRefresh();
    }

    public void setIsInitCatalog(boolean isInit) {
        mBookContentView.setIsInitCatalog(isInit);
    }

    /**
     * @param block
     */
    public synchronized void blockBookContentView(final boolean block) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBookContentView.setViewEnabled(block);
                mBookContentView.setEnabled(block);
            }
        });
    }

    public void startListener() {
        if (mBookContentBottom != null) {
            mBookContentBottom.startUpdateTime();
            mBookContentBottom.startUpdateBattery();
        }
    }

    public void stopListener() {
        if (mBookContentBottom != null) {
            mBookContentBottom.stop();
        }
    }

    /**
     * 设置阅读百分比
     */
    public void setReadPercent(float percent) {
        if (mBookContentBottom != null) {
            mBookContentBottom.setReadPercent(percent);
        }
    }

    private boolean loadNextPage() {
//        ReadPageFactory.getSingleton().loadCurrentPage();
        boolean result = false;
        //加载新内容
        try {
            mOldContentPageCanvas.drawBitmap(mNewContentPageBitmap.get(), 0, 0, null);
            ReadPageFactory.getSingleton().clearCanvasCache(mNewContentPageCanvas);
            result = ReadPageFactory.getSingleton().loadNextPage();
        } catch (Exception e) {
            ReportUtils.reportError(e);
        }
        return result;
    }

    private boolean loadPrePage() {
//        ReadPageFactory.getSingleton().loadCurrentPage();
        boolean result = false;

        try {
            mOldContentPageCanvas.drawBitmap(mNewContentPageBitmap.get(), 0, 0, null);
            ReadPageFactory.getSingleton().clearCanvasCache(mNewContentPageCanvas);
            result = ReadPageFactory.getSingleton().loadPrePage();
        } catch (Exception e) {
            ReportUtils.reportError(e);
        }

        return result;
    }

    /**
     * 设置按钮位置
     */
    private void setBtnLocation() {
        int top = (BookContentSettings.getInstance().getScreenHeight() - AppContext.context().getResources().getDimensionPixelSize(R
                .dimen.page_pay_button_height)) / 3 * 2;
        int w = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_width);
        int h = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_height);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(w, h);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.setMargins(0, top, 0, 0);
        drawBtn.setLayoutParams(param);
    }

    /**
     * 关闭按钮定位
     */
    private void setCloseLocation() {
        int w = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_width);
        int h = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_height);
        int top = (BookContentSettings.getInstance().getScreenHeight() - AppContext.context().getResources().getDimensionPixelSize(R
                .dimen.page_pay_button_height)) / 3 * 2 + h + +CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen
                .page_auto_subscribe_to_button);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(w, h);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.setMargins(0, top, 0, 0);
        closeBtn.setLayoutParams(param);
    }

    /**
     * 设置开通VIP按钮位置
     */
    private void setBtnOpenVipsLocation() {
        int w = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_width);
        int h = BookContentSettings.getInstance().getAutoSubTextSize();
        int top = (BookContentSettings.getInstance().getScreenHeight() - AppContext.context().getResources().getDimensionPixelSize(R
                .dimen.page_pay_button_height)) / 3 * 2 + 2 * (h + +CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen
                .page_auto_subscribe_to_button));
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(w, (int) (h+10* BookContentSettings.getInstance().getDensity()));
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.setMargins(0, top, 0, 0);
        System.err.println("drawMonthlyTips top = " + top);
        openVipBtn.setLayoutParams(param);

    }

    /**
     * 设置自定订阅CheckBox位置.
     */
    private void setBtnAutoSubLocation() {
        final int balance = (int) (Constants.MARGIN_AUTO_TO_TEXT * Utility.getDensity());
        final int fontsize = Utility.getFontHeight(BookContentSettings.getInstance().getAutoSubTextSize());
        BookContentPaint mPaint = new BookContentPaint();
        mPaint.changeStyleToAutoSub();
        String autoPay = CommonApp.context().getString(R.string.readpage_pay_auto) + time;
        int textWidth = (int) mPaint.measureText(autoPay);
        int w = fontsize + balance + textWidth;
        int h = fontsize + balance + 20;
        int top = (BookContentSettings.getInstance().getScreenHeight() - AppContext.context().getResources().getDimensionPixelSize(R
                .dimen.page_pay_button_height)) / 3 * 2 + CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen
                .page_auto_subscribe_to_button) + AppContext.context().getResources().getDimensionPixelSize(R
                .dimen.page_pay_button_height);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(w, h);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.setMargins(0, top, 0, 0);
        autoSubBtn.setLayoutParams(param);
    }

    /**
     * 添加支付监听(添加透明布局，监听点击事件)
     */
    private void setPayButtonLocation(final boolean visibile) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibile) {
                    setBtnLocation();
                    drawBtn.setVisibility(View.VISIBLE);
                } else {
                    drawBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setAutoSubButtonLocation(final boolean visibile) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibile) {
                    setBtnAutoSubLocation();
                    autoSubBtn.setVisibility(View.VISIBLE);
                } else {
                    autoSubBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setOpenVipButtonLocation(final boolean visibile){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibile) {
                    setBtnOpenVipsLocation();
                    openVipBtn.setVisibility(View.VISIBLE);
                }else{
                    openVipBtn.setVisibility(View.GONE);
                }
            }
        });
    }


    private void setCloseButtonLocation(final boolean visiable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visiable) {
                    setCloseLocation();
                    closeBtn.setVisibility(View.VISIBLE);
                } else {
                    closeBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 根据主题创建指定的Bitmap，允许为空.
     *
     * @param theme
     * @return
     */
    private Bitmap createBackground(int theme) {
        if (theme == Constants.THEME_YELLOW) {
            return createBigBackGroundBitmap(R.mipmap.background_yellow);
        } else if (theme == Constants.THEME_PINK) {
            return createBigBackGroundBitmap(R.mipmap.background_pink);
        }
        return null;
    }

    private Bitmap createBigBackGroundBitmap(int id) {
        Bitmap tempBackGroundBitmap = null;
        if (mBackgroundBitmap != null && (tempBackGroundBitmap = mBackgroundBitmap.get()) != null && !tempBackGroundBitmap.isRecycled()) {
//            tempBackGroundBitmap.recycle();
            tempBackGroundBitmap = null;
            mBackgroundBitmap = null;
        }

        InputStream is = AppContext.resources().openRawResource(id);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap tempBitmap = BitmapFactory.decodeStream(is, null, options);

        // 之前设置背景为一张图，需要将图片等比拉伸
        int width = tempBitmap.getWidth();
        int height = tempBitmap.getHeight();
        float scaleWidth = ((float) BookContentSettings.getInstance().getScreenWidth()) / width;
        float scaleHeight = ((float) BookContentSettings.getInstance().getScreenHeight()) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        tempBackGroundBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, width, height, matrix, true); // 进行图片等比缩放
        mBackgroundBitmap = new SoftReference<Bitmap>(tempBackGroundBitmap);

        if (tempBitmap != null && tempBitmap != tempBackGroundBitmap) {
            tempBitmap.recycle();
            System.gc();
            tempBitmap = null;
        }
        return tempBackGroundBitmap;
    }

    private void clearPercent() {
        //需要充值阅读百分比.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBookContentBottom != null) {
                    mBookContentBottom.setReadPercent(0);
                }
            }
        });
    }

    @Override
    public void onLoading(float percent) {
        if (mBookContentView != null) {
            final float p = percent;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    postInvalidate();
                    blockBookContentView(false);
                    if (mBookContentBottom != null) {
                        mBookContentBottom.resetReadPercent(p);
                    }
                }
            });
        }
    }

    @Override
    public void onLoadContentCompleted(PageContent pageContent) {
        //// TODO: 2015/12/11 内容加载完成回调
        if (mBookContentView != null && pageContent != null) {
            final float p = pageContent.getPercent();
            bookState = pageContent.getBookType();
            setPayButtonLocation(false);
            setAutoSubButtonLocation(false);
            setOpenVipButtonLocation(false);
            setCloseButtonLocation(false);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    postInvalidate();
                    blockBookContentView(true);
                    if (bookState == ReadPageState.BOOKTYPE_COPYRIGHT) {
                        setBottomState(false);      //隐藏底部状态栏
                    } else {
                        setBottomState(true);       //显示底部状态栏
                        if (mBookContentBottom != null) {
                            mBookContentBottom.resetReadPercent(p);
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onLoadContentFiled(int code) {
        //// TODO: 2015/12/11 内容加载失败回调
        boolean showBtn = false, showSub = false, showOpenVip = false, showClose = false;
        if (XSErrorEnum.CHAPTER_NOT_SUBSCRIBE.getCode() == code) {
            bookState = ReadPageState.BOOKTYPE_ORDER_PAY;
            showBtn = true;
            showSub = true;
            showOpenVip = true && DataSourceManager.getSingleton().getIsMonthly();
            showClose = false;
        } else if (XSErrorEnum.CHAPTER_SHORT_BALANCE.getCode() == code) {
            bookState = ReadPageState.BOOKTYPE_RECHARGE;
            showBtn = true;
            showSub = true;
            showOpenVip = true && DataSourceManager.getSingleton().getIsMonthly();
            showClose = false;
        } else if (XSErrorEnum.CHAPTER_LAST.getCode() == code) {
//            toast(XSErrorEnum.CHAPTER_LAST.getMsg());
            blockBookContentView(true);
            Intent it = new Intent(activity, LastPageActivity.class);
//            activity.startActivity(it);
            //阅读页-最后一页。
            UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_LAST_PAGE);
            return;
        } else if (XSErrorEnum.CHAPTER_FIRST.getCode() == code) {
            toast(XSErrorEnum.CHAPTER_FIRST.getMsg());
            blockBookContentView(true);
            return;
        } else if (XSErrorEnum.NETWORK_UNAVAILABLE.getCode() == code) {
            bookState = ReadPageState.BOOKTYPE_NONE_NETWORK;
            showBtn = true;
            showClose = true;
            toast(XSErrorEnum.NETWORK_UNAVAILABLE.getMsg());
        } else if (XSErrorEnum.CHAPTER_NEED_LOGIN.getCode() == code) {
            showBtn = true;
            showClose = true;
            bookState = ReadPageState.BOOKTYPE_UNLOGIN;
        } else {
            showBtn = true;
            showClose = true;
            bookState = ReadPageState.BOOKTYPE_ERROR;
            toast(AppContext.context().getString(R.string.network_server_error));
        }
        setPayButtonLocation(showBtn);
        setAutoSubButtonLocation(showSub);
        //// TODO: 16/8/31 此版本关闭VIP开通入口
//        setOpenVipButtonLocation(showOpenVip);
        setCloseButtonLocation(showClose);
        blockBookContentView(true);
        postInvalidate();
    }

    @Override
    public int onViewClick(ClickAction action) {    //点击翻页
        switch (action) {
            case MENU:
                showMenu();
                return BookContentView.DIRECTION_VOID;
            case PREV_PAGE:
                if (!SharedPreferenceUtil.getInstanceSharedPreferenceUtil().isLeftClicked()) {
                    return loadPrePage() ? BookContentView.DIRECTION_RIGHT : BookContentView.DIRECTION_VOID;
                } else {
                    return loadNextPage() ? BookContentView.DIRECTION_LEFT : BookContentView.DIRECTION_VOID;
                }
            case NEXT_PAGE:
                return loadNextPage() ? BookContentView.DIRECTION_LEFT : BookContentView.DIRECTION_VOID;
        }
        return BookContentView.DIRECTION_VOID;
    }

    @Override
    public boolean onLoadNextPage(int scrollDirection) {   //手势翻页
        switch (scrollDirection) {
            case DIRECTION_LEFT:// 翻下一页时
                return !loadNextPage();
            case DIRECTION_RIGHT:// 翻上一页
                return !loadPrePage();
        }
        return false;
    }

    @Override
    public void onViewTouchEnd(int scrollDirection, boolean isBack) {

    }

    @Override
    public void onViewSizeChanged() {

    }

    @Override
    public void onAnimationFinished() {
//        //清屏
//        if (cleanPaint == null) {
//            cleanPaint = new Paint();
//        }
//        cleanPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        mOldContentPageCanvas.drawPaint(cleanPaint);
//        ViewUtils.toastShort("动画结束");

    }

    /**
     * 释放内容.
     */
    public void onRelease() {
        try {
            PagingManager.getSingleton().resetPageMap();
            if (mBackgroundBitmap != null && mBackgroundBitmap.get() != null && !mBackgroundBitmap.get().isRecycled()) {
                mBackgroundBitmap.get().recycle();
                mBackgroundBitmap = null;
            }
            if (mOldContentPageBitmap != null && mOldContentPageBitmap.get() != null && !mOldContentPageBitmap.get().isRecycled()) {
                mOldContentPageBitmap.get().recycle();
                mOldContentPageBitmap = null;
            }
            if (mNewContentPageBitmap != null && mNewContentPageBitmap.get() != null && !mNewContentPageBitmap.get().isRecycled()) {
                mNewContentPageBitmap.get().recycle();
                mNewContentPageBitmap = null;
            }
            System.gc();
        } finally {

        }
    }

    /**
     * 弹出阅读菜单
     */
    public void showMenu() {
        if (openPopupHelper == null) {
            openPopupHelper = new OpenBookSettingViewHelper(activity, this);
            openPopupHelper.setChapterChengeListener(this);
            openPopupHelper.setChapterClickListener(this);
            openPopupHelper.setMenuChangeListener(this);
        }
        if (openPopupHelper.isAnyonWindowShowing()) {
            openPopupHelper.closeSettingWindow();
        } else {
            //阅读页-工具栏呼出
            UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_SHOW_TOOLBAR);
            openPopupHelper.showSettingWindow();
        }
    }


    private void toast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewUtils.toastShort(msg);
            }
        });
    }


    @Override
    public void onPreChapter() {
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_PRE_PAGE);
        if (DataSourceManager.getSingleton().getChapterCount() <= 0) {
            toast(AppContext.context().getString(R.string.catalog_loading));
            return;
        }
        ReadPageFactory.getSingleton().loadPreChapter();
    }

    @Override
    public void onNextChapter() {
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_NEXT_PAGE);
        if (DataSourceManager.getSingleton().getChapterCount() <= 0) {
            toast(AppContext.context().getString(R.string.catalog_loading));
            return;
        }
        ReadPageFactory.getSingleton().loadNextChapter();
    }

    @Override
    public void onChapterChanged(int progress) {
        //章节变化百分比
        ReadPageFactory.getSingleton().jumpToChapterByPercent(progress);
    }

    @Override
    public void onComment() {
        //阅读页-评论
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_COMMENT);
        Intent intent = new Intent(activity, BookCommentActivity.class);
        intent.putExtra("bookid", DataSourceManager.getSingleton().getBookId());
        activity.startActivity(intent);
    }

    @Override
    public void onMoreSetting() {
        //阅读页-更多
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_MORE);
        Intent intent = new Intent(activity, MoreSettingActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE_MORE_SETTING);
        openPopupHelper.closeSettingWindow();
    }


    @Override
    public void onBack() {
        goBack();
    }


    @Override
    public void onDownload() {
        //阅读页-工具栏呼出
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_SHOW_TOOLBAR);
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_DOWNLOAD);
        String bookid = DataSourceManager.getSingleton().getBookId();
        Intent intent = new Intent(activity, DownLoadActivity.class);
        intent.putExtra("bookid", bookid);
        activity.startActivity(intent);
    }

    @Override
    public void onListenBook() {
        //听书
        ViewUtils.toastShort("听书");
    }

    @Override
    public void onOpenDetail() {//详情
        String bookid = DataSourceManager.getSingleton().getBookId();
//        Intent intent = new Intent(activity, BookDetailActivity.class);
        Intent intent = new Intent(activity, BookDetailActivity.class);
        intent.putExtra("bookid", bookid);
        intent.putExtra("tag", PARENT_ACTIVITY);
        activity.startActivity(intent);
    }


    @Override
    public void onTextSizeBig() {
        // 是否可点
        if (mSettings.getTextSize() > mSettings.BOOKCONTENT_TEXT_SIZE_MAX - mSettings.BOOKCONTENT_TEXT_SIZE_CHANGE) {
            ViewUtils.toastShort(activity.getString(R.string.readpage_max_textsize));
            return;
        }
        mSettings.setTextSize(mSettings.getTextSize() + mSettings.BOOKCONTENT_TEXT_SIZE_CHANGE, 1);
        PagingManager.getSingleton().resetPageMap();
        ReadPageFactory.getSingleton().refreshPage();

    }

    @Override
    public void onTextSizeSmall() {
        if (mSettings.getTextSize() < BookContentSettings.BOOKCONTENT_TEXT_SIZE_MIN + mSettings.BOOKCONTENT_TEXT_SIZE_CHANGE) {
            ViewUtils.toastShort(activity.getString(R.string.readpage_min_textsize));
            return;
        }
        mSettings.setTextSize(mSettings.getTextSize() - mSettings.BOOKCONTENT_TEXT_SIZE_CHANGE, -1);
        PagingManager.getSingleton().resetPageMap();
        ReadPageFactory.getSingleton().refreshPage();
    }


    @Override
    public void onThemeChanged(int type) {
        mSettings.setTheme(type);
        ReadPageFactory.getSingleton().setBackgoundBitmap(createBackground(type));
        ReadPageFactory.getSingleton().refreshPage();
    }

    @Override
    public void onCatalogItemClick(String chapterId) {
        ReadPageFactory.getSingleton().jumpToChapter(chapterId);
    }

    //    返回键的处理逻辑
    public void onBackPressed() {
        if (openPopupHelper != null && openPopupHelper.isAnyonWindowShowing()) {
            openPopupHelper.closeSettingWindow();
        } else {
            goBack();
        }
    }


    public void goBack() {
        SharePrefHelper.setLastNotExactFinished("");
        if (isExistInBookShelf()) {
            activity.finish();
            return;
        } else {
            CommonDailog _dailog = new CommonDailog(activity, AppContext.getInstance().getString(R.string.readpage_warm_tips),
                    AppContext.getInstance().getString(R.string.readpage_liketoaddshelf),
                    AppContext.getInstance().getString(R.string.readpage_addtoshelf),
                    AppContext.getInstance().getString(R.string.readpage_addnexttime));
            _dailog.setCommonDialogListener(new CommonDailog.CommonDialogListener() {
                @Override
                public void ok(Dialog dialog) {
                    addBookToShelf();
                    dialog.dismiss();
                    activity.finish();
                }

                @Override
                public void cancel(Dialog dialog) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
            _dailog.show();
        }
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_BACK);
    }


    /**
     * 是否在书架存在.
     *
     * @return
     */
    private boolean isExistInBookShelf() {
        boolean isExistInBookShelf = false;
        String bookid = DataSourceManager.getSingleton().getBookId();
        BookTable bookTable = BookDao.getInstance().findBook(bookid);
        if (bookTable != null && bookTable.getIsOnShelf() == 1) {
            isExistInBookShelf = true;
        }
        return isExistInBookShelf;
    }

    /**
     * 添加试读书籍到书架.
     */
    private void addBookToShelf() {
        String bookId = DataSourceManager.getSingleton().getBookId();
        BookTable bookTable = BookDao.getInstance().findBook(bookId);
        if (bookTable != null) {
            bookTable.setIsOnShelf(1);
            BookDao.getInstance().updateBook(bookTable);
        }

        BookShelfDataUtil.addBook(bookId, null);
    }

    @Override
    public void changeCallBack(boolean isShowing) {
        if (isShowing) {
            mBookContentView.setIsShowMenu(true);
        } else {
            mBookContentView.setIsShowMenu(false);
        }
    }


    /**
     * activity的回调
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (openPopupHelper != null) {
            openPopupHelper.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_CODE_MORE_SETTING && resultCode == Activity.RESULT_OK) {
            PagingManager.getSingleton().resetPageMap();
            ReadPageFactory.getSingleton().refreshPage();
        }

    }

}
