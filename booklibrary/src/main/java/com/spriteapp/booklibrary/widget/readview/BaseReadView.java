/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spriteapp.booklibrary.widget.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.ProgressCallback;
import com.spriteapp.booklibrary.manager.SettingManager;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import java.util.List;

/**
 * @author yuyh.
 * @date 2016/10/18.
 */
public abstract class BaseReadView extends View {

    protected int mScreenWidth;
    protected int mScreenHeight;

    protected PointF mTouch = new PointF();
    protected float actiondownX, actiondownY;
    protected float touch_down = 0; // 当前触摸点与按下时的点的差值

    protected Bitmap mCurPageBitmap, mNextPageBitmap;
    protected Canvas mCurrentPageCanvas, mNextPageCanvas;
    protected PageFactory pagefactory = null;

    protected OnReadStateChangeListener listener;
    protected String bookId;
    public boolean isPrepared = false;
    private TouchPageListener mTouchPageListener;

    Scroller mScroller;

    public BaseReadView(Context context, String bookId, List<BookChapterResponse> chaptersList,
                        OnReadStateChangeListener listener) {
        super(context);
        this.listener = listener;
        this.bookId = bookId;

        mScreenWidth = ScreenUtil.getScreenWidth();
        mScreenHeight = ScreenUtil.getScreenHeight();
        mScroller = new Scroller(getContext());
        try {
            mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
            mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError e) {
            return;
        }
        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        pagefactory = new PageFactory(getContext(), bookId, chaptersList);
        pagefactory.setOnReadStateChangeListener(listener);
    }

    public PageFactory getPageFactory() {
        return pagefactory;
    }

    public synchronized void init() {
        if (!isPrepared) {
            try {
                // 自动跳转到上次阅读位置
                int pos[] = SettingManager.getInstance().getReadProgress(bookId);
                int ret = pagefactory.openBook(pos[0], new int[]{pos[1], pos[2]});
                if (ret == 0) {
                    listener.onLoadChapterFailure(pos[0]);
                    return;
                }
                pagefactory.onDraw(mCurrentPageCanvas);
                postInvalidate();
            } catch (Exception e) {
            }
            isPrepared = true;
        }
    }

    private int dx, dy;
    private long et = 0;
    private boolean cancel = false;
    private boolean center = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (pagefactory == null) {
            return true;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                et = System.currentTimeMillis();
                dx = (int) e.getX();
                dy = (int) e.getY();
                mTouch.x = dx;
                mTouch.y = dy;
                actiondownX = dx;
                actiondownY = dy;
                touch_down = 0;
                pagefactory.onDraw(mCurrentPageCanvas);
                boolean isCenter = actiondownX >= mScreenWidth / 3 && actiondownX <= mScreenWidth * 2 / 3
                        && actiondownY >= mScreenHeight / 3 && actiondownY <= mScreenHeight * 2 / 3;
                boolean isBottomOrTop = actiondownY
                        <= getResources().getDimension(R.dimen.book_reader_top_bar_height)
                        || actiondownY >= mScreenHeight
                        - getResources().getDimension(R.dimen.book_reader_bottom_layout_height);
                if (isCenter || isBottomOrTop) {
                    center = true;
                } else if (actiondownX <= ScreenUtil.dpToPx(20)) {
                    return false;
                } else {
                    center = false;
                    calcCornerXY(actiondownX, actiondownY);
                    if (actiondownX < mScreenWidth / 2) {// 从左翻
                        if (mTouchPageListener != null) {
                            mTouchPageListener.touchPage();
                        }
                        BookStatus status = pagefactory.prePage();
                        if (status == BookStatus.NO_PRE_PAGE) {
                            ToastUtil.showSingleToast("没有上一页啦");
                            return false;
                        } else if (status == BookStatus.LOAD_SUCCESS) {
                            abortAnimation();
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            return false;
                        }
                    } else if (actiondownX >= mScreenWidth / 2) {// 从右翻
                        if (mTouchPageListener != null) {
                            mTouchPageListener.touchPage();
                        }
                        BookStatus status = pagefactory.nextPage();
                        if (status == BookStatus.NO_NEXT_PAGE) {
                            ToastUtil.showSingleToast("没有下一页啦");
                            return false;
                        } else if (status == BookStatus.LOAD_SUCCESS) {
                            abortAnimation();
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            return false;
                        }
                    }
                    listener.onFlip();
                    //判断是否为空
                    try {
                        if (mCurPageBitmap == null)
                            mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
                        if (mNextPageBitmap == null)
                            mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
                    } catch (OutOfMemoryError o) {
                        o.printStackTrace();
                    }

                    setBitmaps(mCurPageBitmap, mNextPageBitmap);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (center)
                    break;
                int mx = (int) e.getX();
                int my = (int) e.getY();
                cancel = (actiondownX < mScreenWidth / 2 && mx < mTouch.x) || (actiondownX > mScreenWidth / 2 && mx > mTouch.x);
                mTouch.x = mx;
                mTouch.y = my;
                touch_down = mTouch.x - actiondownX;
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                long t = System.currentTimeMillis();
                int ux = (int) e.getX();
                int uy = (int) e.getY();

                if (center) { // ACTION_DOWN的位置在中间，则不响应滑动事件
                    if (this instanceof PageWidget) {
                        mTouch.x = 0.1f;
                        mTouch.y = 0.1f;
                    }
                    if (Math.abs(ux - actiondownX) < 5 && Math.abs(uy - actiondownY) < 5) {
                        listener.onCenterClick();
                        return false;
                    }
                    break;
                }
                Log.i("onTouchEvent", Math.abs(ux - dx) + "----x------------------------y-->" + Math.abs(uy - dy));
                if (Math.abs(uy - dy) < Math.abs(ux - dx) || ((Math.abs(ux - dx) < 10) && (Math.abs(uy - dy) < 10))) {
                    if (t - et < 1000) { // 单击时间小于一秒为有效
                        startAnimation();//开始翻页
                    } else { // 长按
                        pagefactory.cancelPage();
                        restoreAnimation();
                    }
                    postInvalidate();
                    return true;
                }else{//当为上下划取消翻页
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pagefactory.cancelPage();
                            restoreAnimation();
                        }
                    },180);
                }

                if (cancel) {
                    pagefactory.cancelPage();
                    restoreAnimation();
                    postInvalidate();
                } else {
//                    startAnimation();
//                    postInvalidate();
                }
                cancel = false;
                center = false;
                break;
            default:
                break;
        }
        return true;
    }

    public void setTouchPageListener(TouchPageListener mTouchPageListener) {
        this.mTouchPageListener = mTouchPageListener;
    }

    public interface TouchPageListener {
        void touchPage();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pagefactory == null) {
            return;
        }
        calcPoints();
        drawCurrentPageArea(canvas);
        drawNextPageAreaAndShadow(canvas);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas);
    }

    protected abstract void drawNextPageAreaAndShadow(Canvas canvas);

    protected abstract void drawCurrentPageShadow(Canvas canvas);

    protected abstract void drawCurrentBackArea(Canvas canvas);

    protected abstract void drawCurrentPageArea(Canvas canvas);

    protected abstract void calcPoints();

    protected abstract void calcCornerXY(float x, float y);

    /**
     * 开启翻页
     */
    protected abstract void startAnimation();

    /**
     * 停止翻页动画（滑到一半调用停止的话  翻页效果会卡住 可调用#{restoreAnimation} 还原效果）
     */
    protected abstract void abortAnimation();

    /**
     * 还原翻页
     */
    protected abstract void restoreAnimation();

    protected abstract void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap);

    public abstract void setTheme(int theme);

    /**
     * 复位触摸点位
     */
    protected void resetTouchPoint() {
        mTouch.x = 0.1f;
        mTouch.y = 0.1f;
        touch_down = 0;
        calcCornerXY(mTouch.x, mTouch.y);
    }

    public void jumpToChapter(int chapter) {
        resetTouchPoint();
        pagefactory.openBook(chapter, new int[]{0, 0});
        pagefactory.onDraw(mCurrentPageCanvas);
        pagefactory.onDraw(mNextPageCanvas);
        postInvalidate();
    }

    public void setSelectPage(int position) {
        resetTouchPoint();
        if (isPrepared && pagefactory != null) {
            pagefactory.setSelectPage(position);
            pagefactory.onDraw(mCurrentPageCanvas);
            postInvalidate();
        }
    }

    /**
     * 获取每一章的页数
     */
    public int getChapterTotalPage() {
        if (pagefactory == null) {
            return 0;
        }
        return pagefactory.getChapterTotalPage();
    }

    public void setProgressCallback(ProgressCallback callback) {
        if (pagefactory == null) {
            return;
        }
        pagefactory.setProgressCallback(callback);
    }

    public void setCurrentChapter() {
        if (pagefactory == null) {
            return;
        }
        pagefactory.setCurrentChapter();
    }

    public int setChapterTotalPage() {
        if (pagefactory == null) {
            return 0;
        }
        return pagefactory.setChapterTotalPage();
    }

    public float getCurrentProgress() {
        if (pagefactory == null) {
            return 0;
        }
        return pagefactory.getCurrentProgress();
    }

    public synchronized void setFontSize(final int fontSizePx) {
        resetTouchPoint();
        pagefactory.setTextFont(fontSizePx);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            pagefactory.onDraw(mNextPageCanvas);
            SettingManager.getInstance().saveFontSize(fontSizePx);
            postInvalidate();
        }
    }

    public synchronized void setTextColor(int textColor, int titleColor) {
        resetTouchPoint();
        pagefactory.setTextColor(textColor, titleColor);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            pagefactory.onDraw(mNextPageCanvas);
            postInvalidate();
        }
    }

    public void setBattery(int battery) {
        if (pagefactory == null) {
            return;
        }
        pagefactory.setBattery(battery);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            postInvalidate();
        }
    }

    public void setTime(String time) {
        if (pagefactory == null) {
            return;
        }
        pagefactory.setTime(time);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!(this instanceof PageWidget)) {
            return;
        }
        if (mTouch == null) {
            return;
        }
        mTouch.x = 0.1f;
        mTouch.y = 0.1f;
        if (hasWindowFocus) {
            restoreAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (pagefactory != null) {
            pagefactory.recycle();
        }

        if (mCurPageBitmap != null && !mCurPageBitmap.isRecycled()) {
            mCurPageBitmap.recycle();
            mCurPageBitmap = null;
        }

        if (mNextPageBitmap != null && !mNextPageBitmap.isRecycled()) {
            mNextPageBitmap.recycle();
            mNextPageBitmap = null;
        }
    }

}
