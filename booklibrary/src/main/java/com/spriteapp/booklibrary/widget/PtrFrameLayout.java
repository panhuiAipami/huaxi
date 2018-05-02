package com.spriteapp.booklibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.widget.readview.MyPageWidget;


/**
 * Created by hzh on 2016/5/3.
 */
public class PtrFrameLayout extends ViewGroup {
    //status enum
    public final static byte PTR_STATUS_INIT = 1;
    public byte mStatus = PTR_STATUS_INIT;
    public final static byte PTR_STATUS_PREPARE = 2;
    public final static byte PTR_STATUS_LOADING = 3;
    public final static byte PTR_STATUS_COMPLETE = 4;

    private PtrIndicator mPtrIndicator;

    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;

    private long mLoadingStartTime = 0;
    private int book_id;
    private View mHeaderView;
    private View mContentView;

    private int mPagingTouchSlop;

    private final boolean DEBUG = true;
    private static int ID = 1;
    protected final String TAG = "ptr-frame-" + ++ID;

    private int mHeaderHeight;

    private boolean mPreventForHorizontal = false;

    private ScrollChecker mScrollChecker;

    private PtrHandler mPtrHandler;
    private PtrUIHandler mPtrUIHandler;

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPtrIndicator = new PtrIndicator();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);

        mDurationToClose = a.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, mDurationToClose);
        mDurationToCloseHeader = a.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header, mDurationToCloseHeader);

        float resistence = a.getFloat(R.styleable.PtrFrameLayout_ptr_resistence, mPtrIndicator.getResistence());
        mPtrIndicator.setResistence(resistence);

        float ratio = a.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, //
                mPtrIndicator.getRatioOfHeaderHeightToRefresh());
        mPtrIndicator.setRatioOfHeaderHeightToRefresh(ratio);

        a.recycle();

        mScrollChecker = new ScrollChecker();

        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mPagingTouchSlop = vc.getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        } else if (childCount == 2) {
            mHeaderView = getChildAt(0);
            mContentView = getChildAt(1);
        } else if (childCount == 1) {
            mContentView = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty!");
            mContentView = errorView;
            addView(mContentView);
        }

        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }

        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG) {
        }

        //测量子view
        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPtrIndicator.setHeaderHeight(mHeaderHeight);
        }
        if (mContentView != null) {
            measureContentView(mContentView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureContentView(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int widthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, //
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        int heightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, //
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);

        child.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offsetY = mPtrIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin - mHeaderHeight + offsetY;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
            if (DEBUG) {
            }
        }

        if (mContentView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContentView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetY;
            final int right = left + mContentView.getMeasuredWidth();
            final int bottom = top + mContentView.getMeasuredHeight();
            mContentView.layout(left, top, right, bottom);
            if (DEBUG) {
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public boolean dispatchTouchEventSuper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    float actiondownX = 0;
    float actiondownY = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mHeaderView == null || mContentView == null) {
            return dispatchTouchEventSuper(e);
        }
        Log.e("dispatchTouchEvent", MyPageWidget.mCurrentMode + "-------------------" + MyPageWidget.Mode.Normal);
        if (MyPageWidget.mCurrentMode != MyPageWidget.Mode.Normal) {
            return dispatchTouchEventSuper(e);
        }

        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyPageWidget.isPullDown = false;
                    }
                }, 1000);

                mPtrIndicator.onRelease();
                if (mPtrIndicator.hasLeftStartPosition()) {
                    onRelease();
                    return true;
                } else {
                    return dispatchTouchEventSuper(e);
                }

            case MotionEvent.ACTION_DOWN:
                actiondownX = e.getX();
                actiondownY = e.getY();
                mPtrIndicator.onPressDown(e.getX(), e.getY());

                //默认情况，PtrFrameLayout处理水平移动
                mPreventForHorizontal = false;

                mScrollChecker.abortIfWorking();

                //ACTION_DOWN event passed to parent
                dispatchTouchEventSuper(e);

                return true;

            case MotionEvent.ACTION_MOVE:
                float x = e.getX();
                float y = e.getY();
                mPtrIndicator.onMove(e.getX(), e.getY());
                float offsetX = mPtrIndicator.getOffsetX();
                float offsetY = mPtrIndicator.getOffsetY();

                if (!mPreventForHorizontal && //
                        Math.abs(offsetX) > mPagingTouchSlop && //
                        Math.abs(offsetX) > Math.abs(offsetY)) {
                    if (mPtrIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }

                if (mPreventForHorizontal) {
                    return dispatchTouchEventSuper(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mPtrIndicator.hasLeftStartPosition();


                if (moveDown && mPtrHandler != null && !mPtrHandler.checkCanDoRefresh(this, mContentView, mHeaderView)) {
                    return dispatchTouchEventSuper(e);
                }
//                Log.i("dispatchTouchEvent" , Math.abs(actiondownY - y) + "-y---------------------------------x-" + Math.abs(actiondownX - x));
                if ((moveUp && canMoveUp) || moveDown) {//下拉收藏
                    MyPageWidget.isPullDown = true;
                    if (Math.abs(actiondownY - y) > 10 && Math.abs(actiondownY - y) > Math.abs(actiondownX - x) && Math.abs(actiondownX - x) < 80) {
                        if (x > 100 && x < BaseActivity.deviceWidth - 100)
                            movePos(offsetY);
                    }
                    return true;
                }
        }   //switch

        return super.dispatchTouchEvent(e);
    }

    private void movePos(float offsetY) {
        if (offsetY < 0 && mPtrIndicator.isInStartPosition()) {
            if (DEBUG) {
                Log.d(TAG, "has reached the top!");
            }
            return;
        }

        int to = mPtrIndicator.getCurrentPosY() + (int) offsetY;

        //check border
        if (mPtrIndicator.willOverTop(to)) {
            if (DEBUG) {
                Log.d(TAG, "over top");
            }
            to = mPtrIndicator.START_POS;
        }
        mPtrIndicator.setCurrentPosY(to);

        int change = to - mPtrIndicator.getLastPos();

        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }

        boolean isUnderTouch = mPtrIndicator.isUnderTouch();

        if (mPtrIndicator.hasJustLeftStartPosition() && mStatus == PTR_STATUS_INIT) {
            mStatus = PTR_STATUS_PREPARE;
            //回调头部UI接口
            if (mPtrUIHandler != null) {
                mPtrUIHandler.onUIRefreshPrepare(this);
            }
            if (DEBUG) {
                Log.e(TAG, "onUIRefreshPrepare");
            }
        }

        if (mPtrIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
        }

        mHeaderView.offsetTopAndBottom(change);
        mContentView.offsetTopAndBottom(change);
        invalidate();

        if (mPtrUIHandler != null) {
            mPtrUIHandler.onUIPositionChange(this, isUnderTouch, mStatus, mPtrIndicator);
        }
    }

    private void onRelease() {
        tryToPeformRefresh();

        if (mStatus == PTR_STATUS_LOADING) {
            //这个判断，考虑到在加载的过程中，用户可以向上滑动来不显示header
            if (mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading()) {
                mScrollChecker.tryToScrollTo(mPtrIndicator.getOffsetToKeepHeaderWhileLoading(), mDurationToClose);
            }
        } else {
            if (mStatus == PTR_STATUS_COMPLETE) {
                notifyUIRefreshComplete();
                tryToNotifyReset();
            } else {
                scrollBackToTop();
            }
        }
    }

    private void tryToPeformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE) {
            return;
        }

        if (mPtrIndicator.isOverOffsetToRefresh()) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPtrUIHandler != null) {
            mPtrUIHandler.onUIRefreshBegin(this);
            if (DEBUG) {
                Log.d(TAG, "onUIRefreshBegin");
            }
        }
        if (mPtrHandler != null) {
            mPtrHandler.onRefreshBegin(this);
        }
    }

    private void tryToNotifyReset() {
        if (mStatus == PTR_STATUS_PREPARE || mStatus == PTR_STATUS_COMPLETE) {
            mPtrUIHandler.onUIReset(this);
            if (DEBUG) {
                Log.d(TAG, "mPtrUIHandler");
            }
            mStatus = PTR_STATUS_INIT;
        }
    }

    private void notifyUIRefreshComplete() {
        if (mPtrUIHandler != null) {
            mPtrUIHandler.onUIRefreshComplete(this);
        }

        scrollBackToTop();
    }

    private void scrollBackToTop() {
        mScrollChecker.tryToScrollTo(mPtrIndicator.START_POS, mDurationToCloseHeader);
    }

    /**
     * call this, when data is loaded
     */
    public void refreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;

        notifyUIRefreshComplete();

        tryToNotifyReset();
    }

    public void setPtrHandler(PtrHandler handler) {
        mPtrHandler = handler;
    }

    public void setPtrUIHandler(PtrUIHandler handler) {
        mPtrUIHandler = handler;
    }

    public void setBookId(int book_id) {
        this.book_id = book_id;
    }

    public void setHeaderView(View header) {
        if (header == null) {
            return;
        }

        if (mHeaderView != null && mHeaderView != header) {
            removeView(mHeaderView);
        }

        LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new MarginLayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }

        mHeaderView = header;
        addView(mHeaderView);
    }

    /**
     * 利用Scroller + post(Runnable)实现平滑移动
     */
    class ScrollChecker implements Runnable {
        private Scroller mScroller;
        private int mStart;
        private int mTo;

        private boolean mIsScrolling = false;

        private int mLastFlingY;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (DEBUG) {
            }

            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            mIsScrolling = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPtrIndicator.isAlreadyHere(to)) {
                return;
            }

            mStart = mPtrIndicator.getCurrentPosY();
            mTo = to;
            int distance = mTo - mStart;
            if (DEBUG) {
            }
            removeCallbacks(this);

            mLastFlingY = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsScrolling = true;
        }

        public void abortIfWorking() {
            if (mIsScrolling) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                finish();
            }
        }
    }
}
