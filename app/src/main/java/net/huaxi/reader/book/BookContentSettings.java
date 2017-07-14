package net.huaxi.reader.book;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.WindowManager;

import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.Utils;
import net.huaxi.reader.common.Utility;

import net.huaxi.reader.R;
import net.huaxi.reader.book.paging.paintcontent.IBookContentRenderSetting;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;

/**
 * 阅读页设置
 * Created by taoyingfeng
 * 2015/12/4.
 * 包含了所有关于阅读页设置的 方法和属性，可以看做是一个 设置的工具类。 提供单例的mSettings对象以供外部调用
 */
public class BookContentSettings implements IBookContentRenderSetting {

    public static final int DEFAULT_SIZE_POSITION = 3;
    /**
     * 最大行间距
     */
    public static final int MAX_LINESPACE = 30;// px
    /**
     * 最小行间距
     */
    public static final int MIN_LINESPACE = 10;// px
    /**
     * 默认底部时间，百分比文字大小
     */
    private static final int DEFAULT_BOTTOM_TEXTSIZE = 13;
    public static int BOOKCONTENT_DEFAULT_TEXT_SIZE;// 默认正文字号大小
    public static int BOOKCONTENT_TEXT_SIZE_CHANGE;// 字号改变梯度
    public static int BOOKCONTENT_TEXT_SIZE_MAX;// 字体最大
    public static int BOOKCONTENT_TEXT_SIZE_MIN;// 字体最小
    public static int TITLE_DEFAULT_TEXT_SIZE;// 默认标题大小
    public static int TITLE_TEXT_SIZE_CHANGE;// 标题字号改变梯度
    public static int TITLE_TEXT_SIZE_MIN;// 标题字号最小
    public static int TITLE_DEFAULT_SPACE;  // 标题与正文默认间距
    public static int TITLE_SPACE_CHANGE;   // 标题和正文间距改变梯度
    public static int[] TITLE_SPACE_MIN = new int[3];  //标题和正文间距最小
    public static int LINE_DEFAULT_SPACE;// 默认行间距
    public static int LINE_SPACE_CHANGE; //行间距改变梯度
    public static int[] LINE_SPACES_MIN = new int[3];  //行间距最小.
    public static int PARAGRAPH_DEFAULT_SPACE;// 默认段间距
    public static int PARAGRAPH_SPACE_CHANGE;  //段间距改变梯度.
    public static int[] PARAGRAPH_SPACE_MIN = new int[3];  //段间距最小.
    private static BookContentSettings mSettings;
    /**
     * 边宽度(单位dip)
     */
    public int mSideWidth = 0;
    /**
     * 字档 0-9
     */
    private int mSizePosition;

    // ================
    /**
     * 正文字号
     */
    private int mTextSize;
    /**
     * 内容标题字号
     */
    private int mTitleTextSize;
    /**
     * 标题与正文间距
     */
    private int mTitleSpace;// 标题与正文间距
    /**
     * 行间距
     */
    private int mLineSpace;
    /**
     * 段间距
     */
    private int mParagraphSpace;
    /**
     * 是否是全屏模式
     */
    private boolean mIsFullScreen;
    /**
     * 主题
     */
    private int mTheme;
    /**
     * 行间距样式[0-2]
     */
    private int mLineStyle = 1;
    /**
     * 屏幕亮度
     */
    private int mLight;
    private Context mContext;
    /**
     * 左边缘的距离
     */
    private int mMarginLeft;
    /**
     * 右边缘的距离
     */
    private int mMarginRight;
    /**
     * 上边缘的距离
     */
    private int mMarginTop = 22;
    /**
     * 正文首行距顶部标题距离
     */
    private int mMarginTopToTitle = 24;
    /**
     * 下边缘的距离
     */
    private int mMarginBottom = 22;
    /**
     * 设备density
     */
    private float mDensity;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * 顶部文字大小
     */
    private int mTopTextSize = 30;
    /**
     * 小说页背面颜色值索引
     */
    //private int mReadBackBg = 0xFFd3c3a9;
    private int mReadBackBg = 0xFF212a3c;
    /**
     * 默认字体颜色
     */
    private int mTextColor = 0xff3D3D3D;
    /**
     * 自动订阅颜色
     */
    // private int mAutoSubColor = 0xFFA7A7A7;
    private int mAutoSubColor = 0xFF1F2839;
    /**
     * 阅读按钮颜色
     */
    private int mPayButtonColor = 0xFFFFFFFF;
    /**
     * 阅读币颜色
     */
    //private int mPayDiscountColor = 0xFFF9493D;
    private int mPayDiscountColor = 0xFF212a3c;
    /**
     * 余额颜色
     */
    private int mPayBalanceColor = 0xFFFFFF;
    /**
     * 阅读页-分割线颜色
     */
    private int mPaySplitLineColor = 0x33000000;
    /**
     * 背景颜色
     */
    private int mBackgroundColor = 0XFFDDDDDD;
    /**
     * 通知栏高度
     */
    private int mStatusBarHeight;
    /**
     * 阅读页翻页模式
     */
    private int mPageTurnMode = 0;
    /**
     * 电池高度
     */
    private int mBatteryHeight;
    private int resId;// 订单页购买按钮ID

    private BookContentSettings(Context context) {
        mContext = context;
    }

    public static BookContentSettings getInstance() {

        if (mSettings == null) {
            mSettings = new BookContentSettings(CommonApp.getInstance());
            mSettings.init(Utility.getDensity());
        }
        return mSettings;
    }

    public static void onDestroyInstance() {
        if (mSettings != null) {
            mSettings = null;
        }
    }

    /**
     * 修改混村中的行间距类型
     *
     * @param mLineStyle
     */
    public void setLineStyle(int mLineStyle) {
        this.mLineStyle = mLineStyle;
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveLineSpaceStyle(mLineStyle);
    }

    /**
     * 初始化 上下左右边缘的距离， 屏幕的高度 | 宽度 | 行间距 | 通知栏的高度 | 顶部文字大小 | 字体大小 | 屏幕亮度 | 显示主题 | 根据像素密度求出书边的宽度 signed
     * by youyang
     *
     * @param density 像素密度
     */
    public void init(float density) {
        mDensity = density;
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(mContext);
        Resources res = mContext.getResources();
        mBatteryHeight = res.getDimensionPixelSize(R.dimen.page_battery_border_height);
        // 以下字体，风格...===========by lyz
        // 字档
        mSizePosition = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getSizePosition();

        mLineStyle = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getLineSpaceStyle();

        // 正文字号
        BOOKCONTENT_DEFAULT_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.bookcontent_default_text_size);
        BOOKCONTENT_TEXT_SIZE_CHANGE = res.getDimensionPixelSize(R.dimen.bookcontent_text_size_change);
        BOOKCONTENT_TEXT_SIZE_MAX = res.getDimensionPixelSize(R.dimen.bookcontent_text_size_max);
        BOOKCONTENT_TEXT_SIZE_MIN = res.getDimensionPixelSize(R.dimen.bookcontent_text_size_min);
        mTextSize = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentFontSize();

        // 标题字号
        TITLE_DEFAULT_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.title_default_text_size);
        TITLE_TEXT_SIZE_CHANGE = res.getDimensionPixelSize(R.dimen.title_text_size_change);
        TITLE_TEXT_SIZE_MIN = res.getDimensionPixelSize(R.dimen.title_text_size_min);
        mTitleTextSize = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTitleFontSize();

        // 标题与正文间距
        TITLE_DEFAULT_SPACE = res.getDimensionPixelSize(R.dimen.title_default_space);
        TITLE_SPACE_CHANGE = res.getDimensionPixelSize(R.dimen.title_space_change);
        TITLE_SPACE_MIN[0] = res.getDimensionPixelSize(R.dimen.title_space_min_small);
        TITLE_SPACE_MIN[1] = res.getDimensionPixelSize(R.dimen.title_space_min_default);
        TITLE_SPACE_MIN[2] = res.getDimensionPixelSize(R.dimen.title_space_min_big);
        mTitleSpace = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getTitleSpace();

        // 行间距
        LINE_DEFAULT_SPACE = res.getDimensionPixelSize(R.dimen.line_default_space);
        LINE_SPACE_CHANGE = res.getDimensionPixelSize(R.dimen.line_space_change);
        LINE_SPACES_MIN[0] = res.getDimensionPixelSize(R.dimen.line_space_min_small);
        LINE_SPACES_MIN[1] = res.getDimensionPixelSize(R.dimen.line_space_min_default);
        LINE_SPACES_MIN[2] = res.getDimensionPixelSize(R.dimen.line_space_min_big);
        mLineSpace = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentLineSpace();

        // 段间距
        PARAGRAPH_DEFAULT_SPACE = res.getDimensionPixelSize(R.dimen.paragraph_default_space);
        PARAGRAPH_SPACE_CHANGE = res.getDimensionPixelSize(R.dimen.paragraph_space_change);
        PARAGRAPH_SPACE_MIN[0] = res.getDimensionPixelSize(R.dimen.paragraph_space_min_small);
        PARAGRAPH_SPACE_MIN[1] = res.getDimensionPixelSize(R.dimen.paragraph_space_min_default);
        PARAGRAPH_SPACE_MIN[2] = res.getDimensionPixelSize(R.dimen.paragraph_space_min_big);
        mParagraphSpace = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentParagraphSpace();
        LogUtils.debug("first   字档=" + mSizePosition + ",字号=" + mTextSize + ",标题字号=" + mTitleTextSize + ",标题距离=" +
                mTitleSpace + ",行间距=" + mLineSpace + ",段间距=" + mParagraphSpace);


        mScreenWidth = Utility.getScreenWidth();
        mScreenHeight = Utility.getScreenHeight();

        mStatusBarHeight = Utils.getStatusBarHeight();

        // 根据像素密度求出书边的宽度
        mSideWidth = (int) (mDensity * mSideWidth);


        mMarginLeft = res.getDimensionPixelSize(R.dimen.page_padding_left);
        mMarginRight = res.getDimensionPixelSize(R.dimen.page_padding_right);
        mTopTextSize = res.getDimensionPixelSize(R.dimen.page_text_size);
        mMarginTop = Utility.getFontHeight(mTopTextSize) + res.getDimensionPixelSize(R.dimen.page_text_margin_top);
        mMarginBottom = res.getDimensionPixelSize(R.dimen.page_padding_bottom) + Utility.getFontHeight(res.getDimensionPixelSize(R.dimen
                .page_text_size));
        mMarginTopToTitle = res.getDimensionPixelSize(R.dimen.page_text_margin_top_1);
        mLight = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentLight();
        mIsFullScreen = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentFullScreen();
        // setFullScreen();
        mTheme = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();
        mPageTurnMode = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentPageTurnMode();
        setTheme(mTheme);
    }

    // ============== 以下风格字档对应逻辑 ===================

    public void changeScreenWidthHeight() {
        int tempSize = 0;
        tempSize = mScreenHeight;
        mScreenHeight = mScreenWidth;
        mScreenWidth = tempSize;
    }

    /**
     * （变字档，风格不变） 改变字体大小：改变字档，字体大小，标题字号，标题距手段距离，行间距，段间距 须知道字档，（风格），获取对应行列式
     *
     * @param textSize:要变成的字体大小
     * @param sizePositionChange：字档的改变值（1，-1）
     */
    public void setTextSize(int textSize, int sizePositionChange) {
        //针对正常阅读模式，才能进行风格修改。
        if (this.mTextSize != textSize && textSize >= BookContentSettings.BOOKCONTENT_TEXT_SIZE_MIN && textSize <= BookContentSettings
                .BOOKCONTENT_TEXT_SIZE_MAX) {
            // 改变字档
            this.mSizePosition = mSizePosition + sizePositionChange;
            SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveSizePosition(mSizePosition);

            // 改变字体
            this.mTextSize = textSize;
            SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentFontSize(textSize);

            // 改变标题字体,改变前设置并保存好字档
            changeTitleTextSize();

            changeTitleSpace();

            changeLineSpace();

            changeParagraphSpace();

            LogUtils.debug("size 字档=" + mSizePosition + ",字号=" + mTextSize + ",标题字号=" + mTitleTextSize + ",标题距离=" +
                    mTitleSpace + ",行间距=" + mLineSpace + ",段间距=" + mParagraphSpace);
        }
    }

    /**
     * change标题字体 ：根据字档单一决定
     */
    private void changeTitleTextSize() {
        this.mTitleTextSize = TITLE_TEXT_SIZE_MIN + mSizePosition * TITLE_TEXT_SIZE_CHANGE;
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentTitleFontSize(mTitleTextSize);
    }

    /**
     * change标题和正文的间距：根据字档
     */
    public void changeTitleSpace() {
        this.mTitleSpace = TITLE_SPACE_MIN[mLineStyle] + mSizePosition * TITLE_SPACE_CHANGE;
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveTitleSpace(mTitleSpace);
    }

    /**
     * change行间距：根据字档
     */
    public void changeLineSpace() {
        // 取出对应风格的最小 行间距
        int lineSpaceMin = LINE_SPACES_MIN[mLineStyle];
        // 改变后的行间距为 最小的行间距加上梯度乘以字档
        this.mLineSpace = lineSpaceMin + mSizePosition * LINE_SPACE_CHANGE;
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentLineSpace(mLineSpace);
    }

    // ==================以上风格字档对应逻辑=========================

    /**
     * change段间距：根据字档决定.
     */
    public void changeParagraphSpace() {
        this.mParagraphSpace = PARAGRAPH_SPACE_MIN[mLineStyle] + mSizePosition * PARAGRAPH_SPACE_CHANGE;
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentParagraphSpace(mParagraphSpace);
    }

    public int getmLight() {
        return mLight;
    }

    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    public void setFullScreen(boolean isFullScreen) {
        this.mIsFullScreen = isFullScreen;
        if (isFullScreen) {
            mStatusBarHeight = 0;
        } else {
            mStatusBarHeight = Utils.getStatusBarHeight();
        }
    }

    /**
     * 改变全屏模式
     */
    @Deprecated
    protected void changeFullScreen() {
        mIsFullScreen = !mIsFullScreen;
//        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentFullScreen(
//                mIsFullScreen);
        if (mIsFullScreen) {
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // setFullScreen();
        }
    }

    /**
     * @return the sideWidth
     */
    public int getSideWidth() {
        return mSideWidth;
    }

    /**
     * @param sideWidth the sideWidth to set
     */
    public void setSideWidth(int sideWidth) {
        this.mSideWidth = sideWidth;
    }

    /**
     * @return the density
     */
    public float getDensity() {
        return mDensity;
    }

    /**
     * @param density the density to set
     */
    public void setDensity(float density) {
        this.mDensity = density;
    }

    /**
     * @return the marginLeft
     */
    public int getMarginLeft() {
        return mMarginLeft;
    }

    /**
     * @param marginLeft the marginLeft to set
     */
    public void setMarginLeft(int marginLeft) {
        this.mMarginLeft = marginLeft;
    }

    /**
     * @return the marginRight
     */
    public int getMarginRight() {
        return mMarginRight;
    }

    /**
     * @param marginRight the marginRight to set
     */
    public void setMarginRight(int marginRight) {
        this.mMarginRight = marginRight;
    }

    /**
     * @return the screenWidth
     */
    public int getScreenWidth() {
        return mScreenWidth;
    }

    /**
     * @return the screenHeight
     */
    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 行间距
     *
     * @return the lineSpace
     */
    public float getLineSpace() {
        return mLineSpace;
    }

    /**
     * @param lineSpace the lineSpace to set
     */
    public void setLineSpace(int lineSpace) {
        if (this.mLineSpace != lineSpace && lineSpace >= MIN_LINESPACE && lineSpace <= MAX_LINESPACE) {
            this.mLineSpace = lineSpace;
            SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentLineSpace(lineSpace);
        }
    }

    /**
     * 上边缘的距离
     *
     * @return the marginTop
     */
    public int getMarginTop() {
        return mMarginTop;
    }

    /**
     * 正文首行距顶部标题距离
     *
     * @return the marginBottom
     */
    public int getMarginBottom() {
        return mMarginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.mMarginBottom = marginBottom;
    }

    /**
     * @return the readBackBgIndex
     */
    public int getReadBackBgIndex() {
        return mReadBackBg;
    }

    /**
     * @param readBackBgIndex the readBackBgIndex to set
     */
    public void setReadBackBgIndex(int readBackBgIndex) {
        this.mReadBackBg = readBackBgIndex;
    }

    /**
     * @return the textColor
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * @param textColor the textColor to set
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    /**
     * @return the defaultBackgroundColor
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * @param mStatusBarHeight the mStatusBarHeight to set
     */
    public void setmStatusBarHeight(int mStatusBarHeight) {
        this.mStatusBarHeight = mStatusBarHeight;
    }

    /**
     * @return the statusBarHeight
     */
    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    /**
     * @return the topTextSize
     */
    public int getTopTextSize() {
        return mTopTextSize;
    }

    public int getDefaultTextSize() {
        return BOOKCONTENT_DEFAULT_TEXT_SIZE;
    }

    public int getTheme() {
        return mTheme;
    }

    public void setTheme(int theme) {
        if (theme < 0 || theme > 4) {
            theme = Constants.THEME_YELLOW;
        }
        this.mTheme = theme;
//        setBackgroundTheme(theme);
        mTextColor = Constants.READ_CONTENT_COLORS[theme];
        mPayButtonColor = Constants.READ_PAY_BUTTON_COLORS[theme];
        mAutoSubColor = AppContext.context().getResources().getColor(R.color.c05_themes_color);
        mPayDiscountColor = Constants.READ_ORDER_PRICE_COLORS[theme];
        mBackgroundColor = Constants.READ_BACKGROUND_COLORS[theme];
        mPayBalanceColor = AppContext.context().getResources().getColor(R.color.c05_themes_color);
        mPaySplitLineColor = Constants.READ_SPLIT_LINE_COLORS[theme];
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentTheme(theme);
    }

    /**
     * 正文首行距顶部标题距离
     *
     * @return mMarginTopToTitle
     */
    public int getTopToTitle() {
        return mMarginTopToTitle;
    }

    public void setMarginTopToTitle(int marginTopToTitle) {
        this.mMarginTopToTitle = marginTopToTitle;
    }

    public int getmPageTurnMode() {
        return mPageTurnMode;
    }

    public void setmPageTurnMode(int mPageTurnMode) {
        this.mPageTurnMode = mPageTurnMode;
    }

    /**
     * @return the mBatteryHeight
     */
    public int getmBatteryHeight() {
        return mBatteryHeight;
    }

    /**
     * @param mBatteryHeight the mBatteryHeight to set
     */
    public void setmBatteryHeight(int mBatteryHeight) {
        this.mBatteryHeight = mBatteryHeight;
    }

    // ===================================
    public int getmSizePosition() {
        return mSizePosition;
    }

    public void setmSizePosition(int position) {
        this.mSizePosition = position;
    }

    /**
     * @return the textSize
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * 内容标题字号
     *
     * @return mTitleTextSize
     */
    public int getTitleTextSize() {
        return mTitleTextSize;
    }

    public void setmTitleTextSize(int mTitleTextSize) {
        this.mTitleTextSize = mTitleTextSize;
    }

    /**
     * 标题与正文间距
     *
     * @return mTitleSpace
     */
    public int getTitleSpace() {
        return mTitleSpace;
    }

    public void setTitleSpace(int mTitleSpace) {
        this.mTitleSpace = mTitleSpace;
    }

    /**
     * 段间距
     *
     * @return mParagraphSpace
     */
    public int getParagraphSpace() {
        return mParagraphSpace;
    }

    // ====================================

    public void setParagraphSpace(int mParagraphSpace) {
        this.mParagraphSpace = mParagraphSpace;
    }

    /**
     * 设置背景主题
     *
     * @param index
     */
    private void setBackgroundTheme(int index) {

    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 获取字体样式
     *
     * @return
     */
    public Typeface getTypeface() {
        return Typeface.DEFAULT;
    }

    /**
     * 获取顶部字体颜色
     *
     * @return
     * @remark 电池颜色，时间，百分比，顶部标题颜色
     */
    public int getTopTextColor() {
        return Constants.READ_OTHER_COLORS[this.mTheme];
    }

    /**
     * 获取底部字体颜色
     *
     * @return
     * @remark 电池颜色，时间，百分比，顶部标题颜色
     */
    public int getBottomTextColor() {
        return Constants.READ_OTHER_COLORS[this.mTheme];
    }

    /**
     * 获取底部文字大小
     *
     * @return
     */
    public int getBottomTextSize() {
        return DEFAULT_BOTTOM_TEXTSIZE * (int) mDensity;
    }

    /**
     * 获取订单页按钮文字大小
     *
     * @return
     */
    public int getButtonTextSize() {
        return (int) (18 * mDensity);
    }

    /**
     * 获取订单页提示语文字颜色
     *
     * @return
     */
    public int getTipTextColor() {
        return Constants.READ_ORDER_PRICE_COLORS[this.mTheme];
    }

    /**
     * 获取折后价(现价)字体颜色
     *
     * @return
     */
    public int getPayDiscountTextColor() {
        return mPayDiscountColor;
    }

    /**
     * 获取折后价(现价)文字大小
     *
     * @return
     */
    public int getPayDiscountTextSize() {
        return (int) (16 * mDensity);
    }

    /**
     * 获取余额字体颜色
     * @return
     */
    public int getPayBalanceColor() {
        return mPayBalanceColor;
    }

    /**
     * 获取余额字体大小
     * @return
     */
    public int getPayBalanceTextSize(){
        return (int) (12 * mDensity);
    }

    /**
     * 获取分割线字体颜色
     * @return
     */
    public int getPaySplitLineColor() {
        return mPaySplitLineColor;
    }

    /**
     * 获取线大小
     * @return
     */
    public float getPaySplitLineWidthSize(){
        return (float) (1.5 * mDensity);
    }

    /**
     * 获取原价横线线大小
     * @return
     */
    public float getPayPriceLineWidthSize(){
        return (float) (1.0 * mDensity);
    }


    /**
     * 获取自动订阅文字大小
     *
     * @return
     */
    public int getAutoSubTextColor() {
        return mAutoSubColor;
    }

    /**
     * 获取自动订阅文字大小
     *
     * @return
     */
    public int getAutoSubTextSize() {
        return (int) (14 * mDensity);
    }

    public void resetBuyButtonResId(int resId) {
        this.resId = resId;
    }

    @Override
    public int resetBuyButtonResId() {
        return resId;
    }

    @Override
    public int getBuyButtonTextColor() {
        return mPayButtonColor;
    }


}
