package net.huaxi.reader.book;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.Utils;
import net.huaxi.reader.common.Utility;

/**
 * 阅读自定义界面
 * Created by taoyingfeng
 * 2015/12/10.
 */
public class BookContentView extends View {

    /* 无滑动方向 */
    public static final int DIRECTION_VOID = 0;
    /**
     * 向左滑动(翻下一页)
     */
    public static final int DIRECTION_LEFT = 3;
    /**
     * 向右滑动 (翻上一页)
     */
    public static final int DIRECTION_RIGHT = 4;
    /**
     * 其他
     */
    public static final int DIRECTION_OTHER = 5;
    private static final int TOUCH_AREA_X_COUNT = 3;
    private static final int TOUCH_AREA_Y_COUNT = 3;
    /**
     * 为防止Y轴坐标>=View高度，而让其等于View的高度-dI
     */
    final private float dI = 0.01f;
    /**
     * 显示区域宽度
     */
    public int mWidth;
    /**
     * 显示区域高度
     */
    public int mHeight;
    // 屏幕宽
    public int mScreenWidth;
    // 屏幕高
    public int mScreenHeight;
    /**
     * Distance in pixels a touch can wander before we think the user is scrolling
     */
    int mTouchSlop = 10;
//    /**
//     * 画笔
//     **/
//    Paint paint;
    /**
     * 屏幕密度
     **/
    float density;
    /**
     * 屏幕对角宽度
     */
    float mMaxLength;
    /**
     * 当前页
     */
    private Bitmap mOldContentPageBitmap = null;
    /**
     * 下一页
     */
    private Bitmap mNewContentPageBitmap = null;
    /**
     * 是否向右滑动 0空闲状态；1向上拖动；2向下拖动；3向左滑动；4向右滑动；5菜单；6长按事件
     */
    private volatile int mScrollDirection = DIRECTION_VOID;
    private float mLastMoveX = 0;
    /**
     * touch事件down时的x轴坐标
     */
    private float mTouchDownX;
    /**
     * touch事件down时的y轴坐标
     */
    private float mTouchDownY;
    /**
     * 一次完整touch事件中上一次的x轴坐标
     */
    private float mLastTouchX;
    /**
     * 一次完整touch事件中上一次的y轴坐标
     */
    private float mLastTouchY;
    /**
     * 设备通知栏高度
     */
    private int mNotifactionBarHeight;
    /**
     * 边宽度(单位dip)
     */
    private int mSideWidth = 0;

    /**
     * 边界阴影的宽度
     */
    private int shadowWidthMin = 7;
    private int shadowWidthMax = 15;
    /**
     * Activity对象
     **/
    private Activity mActivity;
    /**
     * 小说页背面颜色值
     */
    private int mReadBackBgColor;
    /*view是否可用: true可用，false不可用 弹出菜单时候和联网读取下一章时不可用其余可用 */
    private volatile boolean mViewEnabled;
    //是否真正的移动;
    private volatile boolean mIsRealMove = false;
    /*目录加载是否完成*/
    private boolean isInitCatalog = false;
    /*是否是触摸状态*/
    private volatile boolean mIsTouched = false;
    /*菜单是否显示*/
    private volatile boolean isShowMenu = false;
    private FlingRunnable mFlingRunnable = new FlingRunnable();
    private OnViewEventListener onViewEventListener;
    private GradientDrawable mFrontShadowDrawableLR;
    private GradientDrawable mFolderShadowDrawableRL;
    private boolean isNeedScroll = false;  //阴影效果


    public BookContentView(Context context) {
        this(context, null, 0);
        initData(context);
    }

    public BookContentView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initData(context);
    }

    public BookContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    /**
     * 设置当前页与下一页的显示Bitmap
     *
     * @param mCurPageBitmap  当前页面内容的Bitmap;
     * @param mNextPageBitmap 下一个页面内容的Bitmap;
     */
    public void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap) {
        this.mOldContentPageBitmap = mCurPageBitmap;
        this.mNewContentPageBitmap = mNextPageBitmap;
    }

    public void setOnViewEventListener(OnViewEventListener onViewEventListener) {
        this.onViewEventListener = onViewEventListener;
    }

    public void setIsInitCatalog(boolean isinit) {
        this.isInitCatalog = isinit;
    }

    public void setIsShowMenu(boolean isShowMenu) {
        this.isShowMenu = isShowMenu;
    }

    /**
     * 初始化数据
     */
    private void initData(Context context) {
        mActivity = (Activity) context;
        setDrawingCacheEnabled(true);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (mHeight == 0) {
                    initDelayParams();
                }
                return true;
            }
        });
//        mTouchSlop = (int) (mTouchSlop * Utility.getDensity());
        mTouchSlop = ViewConfiguration.get(mActivity).getScaledTouchSlop();//一个距离，判断是否要滑动
        LogUtils.info(" mTouchSlop=" + mTouchSlop);
        mSideWidth = (int) (Utility.getDensity() * mSideWidth);
        LogUtils.info(" mSideWidth=" + mSideWidth);
        // 获取手机的宽度和高度
        mScreenWidth = Utility.getScreenWidth();
        mScreenHeight = Utility.getScreenHeight();
        // 获取显示的宽度和高度
        mWidth = mScreenWidth - mSideWidth;
        // TODO 任何地方改动后会改变自定义控件高度时 都要重新给mHeight赋值
        mHeight = mScreenHeight;
        // mHeight = getHeight();
        density = Utility.getDensity();

        mMaxLength = (float) Math.hypot(mWidth, mHeight);//直角三角形斜边的长度

        /** 设置View不使用硬件加速（目前App只支持4.1版本以上，不需要使用反射关闭） */
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }

        shadowWidthMax = Utils.dip2px(context, 6);
        shadowWidthMin = Utils.dip2px(context, 5);
        int[] mFrontShadowColors = new int[]{0x70333333, 0x111111};
        mFrontShadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
        mFrontShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFolderShadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
        mFolderShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        //// FIXME: 2015/12/11 默认背景图片 后期要做优化
        mReadBackBgColor = BookContentSettings.getInstance().getBackgroundColor();
    }

    /**
     * 延迟初始化的数据
     */
    public void initDelayParams() {
        //获取状态栏的高度
        initDisolayActionBar();

        initWH();
    }

    private void initWH() {
        mScreenWidth = BookContentSettings.getInstance().getScreenWidth();
        mScreenHeight = BookContentSettings.getInstance().getScreenHeight();
        // 获取显示的宽度和高度
        mWidth = mScreenWidth - mSideWidth;
        // TODO 任何地方改动后会改变自定义控件高度时 都要重新给mHeight赋值
        mHeight = mScreenHeight;
    }

    private void initDisolayActionBar() {
        if (mNotifactionBarHeight == 0 && mActivity != null) {
            Rect frame = new Rect();
            mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            mNotifactionBarHeight = frame.top;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果没有设置回调监听则return true
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.info("@@@@@@@@@@@@@@@@   捕捉到用户手势     @@@@@@@@@@@@@@@@@@@@@");
                if (!mFlingRunnable.isFinished()) {
                    return false;
                }
                if (!isInitCatalog) {
                    return true;
                }
                if (isShowMenu) {
                    return true;
                }
                if (!mViewEnabled) {
                    return true;
                }
                mScrollDirection = DIRECTION_VOID;  //重置手势方向
                /* 记录触摸起点坐标 */
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                /**/
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();

                LogUtils.info("Action_Down  mLastTouchX = " + mLastTouchX + "   mTouchDownX = " + mTouchDownX);
                mIsTouched = true;
                isNeedScroll = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isInitCatalog) {
                    return true;
                }
                if (!mViewEnabled) {
                    return true;
                }
                if (isShowMenu) {
                    return true;
                }
                if (!mIsTouched) {
                    LogUtils.debug("######  ACTION_MOVE wont do something ######");
                    return true;
                }
                float tempX = event.getX();
                float tempY = event.getY();

                if (mLastTouchX != 0 && mLastTouchY != 0 && (mLastTouchX != tempX || mLastTouchY != tempY)) {
                    //设置方向，此处代码只执行一次
                    if (mScrollDirection == DIRECTION_VOID && ((Math.abs(tempX - mTouchDownX)) >= mTouchSlop || Math.abs(event.getY() -
                            mTouchDownY) > mTouchSlop)) {
                        mScrollDirection = mLastTouchX < tempX ? DIRECTION_RIGHT : DIRECTION_LEFT;
                        LogUtils.debug(" Action_Move  Direction =" + (mScrollDirection == DIRECTION_RIGHT ? "看上一页" : "看下一页") + " " +
                                "LastTouchX =" + mLastTouchX + " X =" + tempX);
                        // FIXME: 2015/12/22 根据方向，加载下一页的内容
                        if (onViewEventListener != null) {
                            if (onViewEventListener.onLoadNextPage(mScrollDirection)) {
                                LogUtils.debug("滑动事件阻断，禁止滑动!");
                                mIsTouched = false;
                            } else {
                                isNeedScroll = true;
                            }
                            break;
                        }
                    }
                }
                mLastTouchX = tempX;
                mLastTouchY = tempY;

                if ((mScrollDirection == DIRECTION_LEFT && mLastTouchX > mTouchDownX) || !isNeedScroll) {
                    mLastTouchX = mTouchDownX;//可以否防止画出向右画出边距
                }
                if (mIsTouched && mScrollDirection != DIRECTION_VOID && mViewEnabled) {
                    postInvalidate();
                }
                LogUtils.info("Action_Move LastTouchX =" + mLastTouchX + " mTouchDownX =" +
                        mTouchDownX);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //菜单打开，则返回
                if (!isInitCatalog) {
                    return true;
                }
                //View不可点击时，允许点击菜单;同时为了防止用户上一页动画并停止.
                if (!mViewEnabled) {
                    LogUtils.debug("######  ACTION_UP mViewEnabled can't enabled ######");
                    if (mScrollDirection != DIRECTION_VOID) {
                        startScrollAnimation(false);
                    } else {
                        final OnViewEventListener.ClickAction clickAction = calcTouchArea((int) mTouchDownX, (int) mTouchDownY);
                        if (clickAction == OnViewEventListener.ClickAction.MENU) {
                            onViewEventListener.onViewClick(clickAction);
                        }
                    }
                    return true;
                }
                //是否是完整的touch事件
                if (!mIsTouched) {
                    return true;
                }
                LogUtils.info("********************  End Touch Action  **************************");
                boolean isTouchAction = mScrollDirection == DIRECTION_VOID;
                //点击事件、没有Move行为
                if (isTouchAction) {
                    OnViewEventListener.ClickAction clickAction = calcTouchArea((int) mTouchDownX, (int) mTouchDownY);
                    if (isShowMenu) {  //菜单显示时，不记录手势，只隐藏菜单。
                        clickAction = OnViewEventListener.ClickAction.MENU;
                    }
                    mScrollDirection = onViewEventListener.onViewClick(clickAction);
                }

                if (mScrollDirection == DIRECTION_LEFT || mScrollDirection == DIRECTION_RIGHT) {
                    if (onViewEventListener != null) {
                        onViewEventListener.onViewTouchEnd(mScrollDirection, false);
                    }
                    startScrollAnimation(isTouchAction);
                }
                break;
        }

        return true;
    }

    /**
     * 平滑动画
     *
     * @param isTouchAction
     */

    private void startScrollAnimation(boolean isTouchAction) {
        int distance = 0;
        if (mScrollDirection == DIRECTION_LEFT) {
            distance = (int) (mTouchDownX - mLastTouchX) - mWidth;
        } else {
            distance = (int) (mWidth - mLastTouchX + mTouchDownX);
        }
        if (isTouchAction) {
            mFlingRunnable.startByTouch(distance);
        } else {
            mFlingRunnable.startByScroll(distance);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘画自定义内容
        canvas.drawColor(mReadBackBgColor);
        switch (mScrollDirection) {
            case DIRECTION_LEFT:// 翻下一章
                drawNewContentCanvas(canvas);
                drawOldContentCanvas(canvas);
                break;
            case DIRECTION_RIGHT:// 翻上一章
                drawOldContentCanvas(canvas);
                drawNewContentCanvas(canvas);
                break;
            default:
                drawOldContentCanvas(canvas);
                drawNewContentCanvas(canvas);
                break;
        }
        if (isNeedScroll) {
            drawContentShadow(canvas);
        }
    }

    /**
     * 画上一页正文页翻动时剩下的部分
     *
     * @param canvas
     */
    private void drawOldContentCanvas(Canvas canvas) {
        if (mOldContentPageBitmap == null) {
            return;
        }
        if (mScrollDirection == DIRECTION_VOID) {
            return;
        }
        float moveX = mLastMoveX;
        if (mScrollDirection == DIRECTION_LEFT) {// 向左移动多少就应该绘制偏移多少
            moveX = mLastTouchX - mTouchDownX;
        } else {
            moveX = 0;
        }
        canvas.drawBitmap(mOldContentPageBitmap, moveX, 0, null);
        if (mOldContentPageBitmap != null) {
//            LogUtils.debug(" old bitmap size = " + mOldContentPageBitmap.getByteCount() + "B");
        }
        mLastMoveX = moveX;
    }

    public void drawNewContentCanvas(Canvas canvas) {
        if (mNewContentPageBitmap == null) {
            return;
        }
        float moveX = mLastMoveX;
        // 当前手指触摸的位置的X轴坐标就是要偏移的距离
        if (mScrollDirection == DIRECTION_RIGHT) {
            moveX = -mWidth + mLastTouchX - mTouchDownX;
        } else {
            moveX = 0;
        }
        canvas.drawBitmap(mNewContentPageBitmap, moveX, 0, null);
        if (mNewContentPageBitmap != null) {
//            LogUtils.debug(" new bitmap size = " + mNewContentPageBitmap.getByteCount() + "B");
        }
    }

    /**
     * 画阅读页面边上的阴影
     */
    public void drawContentShadow(Canvas canvas) {
        GradientDrawable shadowDrawable = mFrontShadowDrawableLR;
        if (mScrollDirection == DIRECTION_RIGHT) {
            shadowDrawable = mFrontShadowDrawableLR;
            shadowDrawable.setBounds((int) (mLastTouchX - mTouchDownX), 0, (int) (mLastTouchX - mTouchDownX) + shadowWidthMax, mOldContentPageBitmap.getHeight());
        } else if (mScrollDirection == DIRECTION_LEFT) {// 向左移动多少就应该绘制偏移多少
            int shadowX = (mWidth + (int) mLastMoveX);
            if (shadowX < 2) {
                shadowX = -shadowWidthMax;
            }
            shadowDrawable = mFrontShadowDrawableLR;
            shadowDrawable.setBounds(shadowX, 0, shadowX + shadowWidthMax, mOldContentPageBitmap.getHeight());
        }
        shadowDrawable.draw(canvas);
    }


    /**
     * View大小改变时重新初始化相关数据
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initDelayParams();
        if (onViewEventListener != null) {
            onViewEventListener.onViewSizeChanged();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 刷新
     */
    public void postInvalidateRefresh() {

        initWH();

        postInvalidate();
    }

    /**
     * view是否可用
     */
    public void setViewEnabled(boolean enabled) {
        mViewEnabled = enabled;
    }

    /**
     * 计算
     */
    private OnViewEventListener.ClickAction calcTouchArea(int x, int y) {
        final int x_area_width = mWidth / TOUCH_AREA_X_COUNT;
        final int y_area_height = mHeight / TOUCH_AREA_Y_COUNT;
        switch ((int) (Math.min((x / x_area_width), TOUCH_AREA_X_COUNT - 1) + Math.min((y / y_area_height), TOUCH_AREA_Y_COUNT - 1) *
                TOUCH_AREA_X_COUNT)) {
            case OnViewEventListener.AREA_TOP_LEFT:
            case OnViewEventListener.AREA_MIDDLE_LEFT:
            case OnViewEventListener.AREA_BOTTOM_LEFT:
//            case OnViewEventListener.AREA_TOP_MIDDLE:
                return OnViewEventListener.ClickAction.PREV_PAGE;
            case OnViewEventListener.AREA_TOP_RIGHT:
            case OnViewEventListener.AREA_MIDDLE_RIGHT:
            case OnViewEventListener.AREA_BOTTOM_RIGHT:
//            case OnViewEventListener.AREA_BOTTOM_MIDDLE:
                return OnViewEventListener.ClickAction.NEXT_PAGE;

            default:
                return OnViewEventListener.ClickAction.MENU;
        }
    }

    /**
     * 事件监听
     */
    public interface OnViewEventListener {
        /**
         * 上左区域
         */
        public static final int AREA_TOP_LEFT = 0;
        /**
         * 上中区域
         */
        public static final int AREA_TOP_MIDDLE = 1;
        /**
         * 上右区域
         */
        public static final int AREA_TOP_RIGHT = 2;
        /**
         * 中左区域
         */
        public static final int AREA_MIDDLE_LEFT = 3;
        /**
         * 中中区域
         */
        public static final int AREA_MIDDLE_MIDDLE = 4;
        /**
         * 中右区域
         */
        public static final int AREA_MIDDLE_RIGHT = 5;
        /**
         * 下左区域
         */
        public static final int AREA_BOTTOM_LEFT = 6;
        /**
         * 下中区域
         */
        public static final int AREA_BOTTOM_MIDDLE = 7;
        /**
         * 下右区域
         */
        public static final int AREA_BOTTOM_RIGHT = 8;

        /**
         * 请按OnClickListener类的静态属性判断
         */
        public int onViewClick(ClickAction action);

        /**
         * touch事件开始 true表示阻断事件
         *
         * @param scrollDirection 滑动方向
         */
        public boolean onLoadNextPage(int scrollDirection);

        /**
         * touch事件结束
         *
         * @param scrollDirection 滑动方向
         * @param isBack          是否往回翻
         */
        public void onViewTouchEnd(int scrollDirection, boolean isBack);

        /**
         * 当view大小改变时
         */
        public void onViewSizeChanged();

        /**
         * 滑动翻页动画结束
         */
        public void onAnimationFinished();

        public enum ClickAction {
            MENU, PREV_PAGE, NEXT_PAGE
        }

    }

    private class FlingRunnable implements Runnable {

        private static final int SCROLL_ANIMATION_DURATION = 200;

        private static final int TOUCH_ANIMATION_DURATION = 200;

        private static final int MIN_ANIMATION_DURATION = 200;

        private Scroller mScroller;

        private int mLastFlingX;

        public FlingRunnable() {
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        }

        /**
         * 移除消息队列里的上个动作
         */
        private void startCommon() {
            removeCallbacks(this);
        }

        /**
         * 另外开启一个线程来横向滑动书页
         *
         * @param distance 滑动的距离
         */
        public void startByScroll(int distance) {
            startUsingDistance(distance, SCROLL_ANIMATION_DURATION);
        }

        public void startByTouch(int distance) {
            startUsingDistance(distance, TOUCH_ANIMATION_DURATION);
        }

        /**
         * 异步书页偏移的 实际执行方法
         *
         * @param distance 偏移距离(负值向左，正值向右)
         * @param during   持续时间
         */
        private void startUsingDistance(int distance, int during) {
            if (distance == 0) return;
            startCommon();
            mLastFlingX = 0;
            // 起始点为（0，0），x偏移量为 -distance ，y的偏移量为 0，持续时间
            LogUtils.info("偏移距离 distance= " + distance);
            //Horizontal distance to travel. Positive numbers will scroll the content to the left.
            mScroller.startScroll(0, 0, -distance, 0, Math.max(MIN_ANIMATION_DURATION, Math.abs(distance) * during / mWidth));
            post(this);
        }

        /**
         * 停止滑动
         */
        private void endFling(boolean scrollIntoSlots) {
            mScroller.forceFinished(true);
            if (onViewEventListener != null) {
                onViewEventListener.onAnimationFinished();
            }
        }

        @Override
        public void run() {
            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();// 返回true的话 则动画还没有结束
            final int x = scroller.getCurrX();// 返回滚动时 当前的x坐标
            int delta = mLastFlingX - x;
            if (delta != 0) {
                mLastTouchX += delta;
//                LogUtils.info(" -------postInvalidate---( x = " + x + " | delta = "+ delta+" |
// mLastTouchX = " + mLastTouchX + ")----- ");
                postInvalidate();
            }

            if (more) {
                mLastFlingX = x;
                post(this);
            } else {
                endFling(true);
                LogUtils.info("=====    翻页结束   =====");
            }
        }

        public boolean isFinished() {
            return mScroller.isFinished();
        }
    }

}
