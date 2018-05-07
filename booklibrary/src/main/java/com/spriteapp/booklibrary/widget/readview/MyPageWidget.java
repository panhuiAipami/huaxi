package com.spriteapp.booklibrary.widget.readview;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.dialog.BookCommentDialog;
import com.spriteapp.booklibrary.ui.dialog.MyPopupWindow;
import com.spriteapp.booklibrary.ui.dialog.ShareSelectTextDialog;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;
import com.spriteapp.booklibrary.widget.readview.animation.AnimationProvider;
import com.spriteapp.booklibrary.widget.readview.animation.CoverAnimation;
import com.spriteapp.booklibrary.widget.readview.animation.NoneAnimation;
import com.spriteapp.booklibrary.widget.readview.animation.SimulationAnimation;
import com.spriteapp.booklibrary.widget.readview.animation.SlideAnimation;
import com.spriteapp.booklibrary.widget.readview.util.ShowChar;
import com.spriteapp.booklibrary.widget.readview.util.ShowLine;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class MyPageWidget extends View implements MyPopupWindow.OnButtonClick {
    private int mScreenWidth = 0; // 屏幕宽
    private int mScreenHeight = 0; // 屏幕高
    private Context mContext;
    private Activity activity;

    //是否移动了
    private Boolean isMove = false;
    //是否翻到下一页
    private Boolean isNext = false;
    //是否取消翻页
    private Boolean cancelPage = false;
    //是否没下一页或者上一页
    private Boolean noNext = false;
    private int downX = 0;
    private int downY = 0;

    private int moveX = 0;
    private int moveY = 0;
    //翻页动画是否在执行
    private Boolean isRuning = false;

    Bitmap mCurPageBitmap = null; // 当前页
    Bitmap mNextPageBitmap = null;
    private AnimationProvider mAnimationProvider;

    private Scroller mScroller;
    private int mBgColor = 0xFAF5ED;
    private TouchListener mTouchListener;
    private Paint mBorderPointPaint, mTextSelectPaint = null;
    private List<ShowLine> mLinseData = null;
    private List<ShowLine> mSelectLines = new ArrayList<>();
    private List<ShowChar> sectionEnd = new ArrayList<>();
    public static boolean isPullDown = false;
    private float TextHeight;
    private String selectText = "";
    private int selectTextSection = 0;
    private MyPopupWindow popupWindow;
    private BookDetailResponse bookDetailResponse;
    private String mCurrentContent;

    public MyPageWidget(Context context) {
        this(context, null);
    }

    public MyPageWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ListenerManager.getInstance().setOnButtonClick(this);
        popupWindow = new MyPopupWindow(context);
        mBorderPointPaint = new Paint();
        mBorderPointPaint.setColor(ContextCompat.getColor(context, R.color.square_comment_selector));
        mBorderPointPaint.setStrokeWidth(5);//宽度

        mTextSelectPaint = new Paint();
        mTextSelectPaint.setColor(ContextCompat.getColor(context, R.color.select_font_bg));

        mContext = context;
        initPage();
        mScroller = new Scroller(getContext(), new LinearInterpolator());
        mAnimationProvider = new CoverAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
        Paint.FontMetrics fontMetrics = mBorderPointPaint.getFontMetrics();
        TextHeight = Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);

        setOnLongClickListener(mLongClickListener);
    }


    private void initPage() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);      //android:LargeHeap=true  use in  manifest application
        mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
    }

    public void setPageMode(int pageMode) {
        switch (pageMode) {
            case Config.PAGE_MODE_SIMULATION://覆盖
                mAnimationProvider = new CoverAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            case Config.PAGE_MODE_COVER://仿真
                mAnimationProvider = new SimulationAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            case Config.PAGE_MODE_SLIDE://平移滑动
                mAnimationProvider = new SlideAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            case Config.PAGE_MODE_NONE://无效果(暂时没用到)
                mAnimationProvider = new NoneAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            default:
                mAnimationProvider = new CoverAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
        }
    }

    public Bitmap getCurPage() {
        return mCurPageBitmap;
    }

    public Bitmap getNextPage() {
        return mNextPageBitmap;
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    public void getListData(List<ShowLine> list) {
        mLinseData = list;
    }

    public void getSectionEnd(List<ShowChar> sectionEnd) {
        this.sectionEnd = sectionEnd;
    }

    public void getChapterContent(String content) {
        this.mCurrentContent = content;
    }

    public void bookDetail(Activity activity,BookDetailResponse shareDetail) {
        this.activity = activity;
        bookDetailResponse = shareDetail;
    }

    private OnLongClickListener mLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {

            if (mCurrentMode == Mode.Normal && !isMove && !isPullDown) {
                if (Down_X > 0 && Down_Y > 0) {// 说明还没释放，是长按事件
                    mCurrentMode = Mode.PressSelectText;
                    postInvalidate();
                }
            }
            return false;
        }
    };

    public void showPopWindow() {
        popupWindow.showAtLocation(this, Gravity.TOP | Gravity.LEFT, (int) Down_X, (int) Down_Y);
    }

    public void dismissPopWindow() {
        if (popupWindow != null)
            popupWindow.dismiss();
    }

    private float Tounch_X = 0, Tounch_Y = 0;
    private float Down_X = -1, Down_Y = -1;
    public static Mode mCurrentMode = Mode.Normal;
    Boolean isTrySelectMove = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
//        Log.e("onTouchEvent", "------------onTouchEvent--------" + PageFactory2.getStatus());
        if (PageFactory2.getStatus() == PageFactory2.Status.OPENING) {
            return true;
        }


        int x = (int) event.getX();
        int y = (int) event.getY();
        Tounch_X = x;
        Tounch_Y = y;

        mAnimationProvider.setTouchPoint(x, y);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
            Down_X = Tounch_X;
            Down_Y = Tounch_Y;
            dismissPopWindow();
            if (mCurrentMode != Mode.Normal) {
                isTrySelectMove = CheckIfTrySelectMove(Down_X, Down_Y);
//                if (!isTrySelectMove) {// 如果不是准备滑动选择文字，转变为正常模式，隐藏选择框
//                    mCurrentMode = Mode.Normal;
//                    invalidate();
//                }
            } else {
                isTrySelectMove = true;
                downX = (int) event.getX();
                downY = (int) event.getY();
                moveX = 0;
                moveY = 0;
                isMove = false;
//            cancelPage = false;
                noNext = false;
                isNext = false;
                isRuning = false;
                mAnimationProvider.setStartPoint(downX, downY);
                abortAnimation();
                Log.e("ACTION_MOVE", "ACTION_DOWN");
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (mCurrentMode == Mode.SelectMoveForward) {

                if (CanMoveForward(event.getX(), event.getY())) {// 判断是否是向上移动

                    Log.e("is CanMoveForward", "-----ACTION_MOVE--------CanMoveForward");

                    ShowChar firstselectchar = DetectPressShowChar(event.getX(), event.getY());
                    if (firstselectchar != null) {
                        FirstSelectShowChar = firstselectchar;
                        invalidate();
                    } else {
                        Log.e("firstselectchar", "------ACTION_MOVE------firstselectchar is null");
                    }

                } else {
                    Log.e("is CanMoveForward", "CanMoveForward");
                }

            } else if (mCurrentMode == Mode.SelectMoveBack) {

                if (CanMoveBack(event.getX(), event.getY())) {// 判断是否可以向下移动
                    Log.e("CanMoveBack", "not CanMoveBack");

                    ShowChar lastselectchar = DetectPressShowChar(event.getX(), event.getY());

                    if (lastselectchar != null) {
                        LastSelectShowChar = lastselectchar;
                        invalidate();
                    } else {
                        Log.e("is lastselectchar", "lastselectchar is null");
                    }

                } else {
                    Log.e("is CanMoveBack", "not CanMoveBack");
                }
            } else if (mCurrentMode == Mode.PressSelectText) {
                return false;
            } else {


                final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                //判断是否移动了
                if (!isMove) {
                    isMove = Math.abs(downX - x) > slop || Math.abs(downY - y) > slop;
                }

                if (isMove) {
                    isMove = true;
                    if (moveX == 0 && moveY == 0) {
                        Log.e("ACTION_MOVE", y - downY + "<---------isMove--X=>" + (x - downX));
                        //判断翻得是上一页还是下一页
                        if (x - downX > 0 && Math.abs(y - downY) < Math.abs(x - downX)) {
                            isNext = false;
                        } else if (x - downX < 0 && Math.abs(y - downY) < Math.abs(x - downX)) {
                            isNext = true;
                        }
                        cancelPage = false;
                        if (isNext) {
                            Boolean isNext = mTouchListener.nextPage();
//                        calcCornerXY(downX,mScreenHeight);
                            mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                            Log.e("ACTION_MOVE", "-----nextPage---->" + isNext);
                            if (!isNext) {
                                noNext = true;
                                return true;
                            }
                        } else {
                            Boolean isPre = mTouchListener.prePage();
                            mAnimationProvider.setDirection(AnimationProvider.Direction.pre);
                            Log.e("ACTION_MOVE", "-----prePage---->" + isPre);

                            if (!isPre) {
                                noNext = true;
                                return true;
                            }
                        }
                        Log.e("ACTION_MOVE", "isNext:" + isNext);
                    } else {
                        //判断是否取消翻页
                        if (isNext) {
                            if (x - moveX > 0) {
                                cancelPage = true;
                                mAnimationProvider.setCancel(true);
                            } else {
                                cancelPage = false;
                                mAnimationProvider.setCancel(false);
                            }
                        } else {
                            if (x - moveX < 0) {
                                mAnimationProvider.setCancel(true);
                                cancelPage = true;
                            } else {
                                mAnimationProvider.setCancel(false);
                                cancelPage = false;
                            }
                        }
                        Log.e("ACTION_MOVE", "cancelPage:" + cancelPage);
                    }

                    moveX = x;
                    moveY = y;
                    isRuning = true;
                    this.postInvalidate();
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(true);
            //确定popWindow的位置
            if (mCurrentMode != Mode.Normal && FirstSelectShowChar != null) {
                Down_X = BaseActivity.deviceWidth / 2 - Util.dp2px(mContext, 90);
                Down_Y = FirstSelectShowChar.TopLeftPosition.y - Util.dp2px(mContext, 70);
                if (!isTrySelectMove) {// 如果不是准备滑动选择文字，转变为正常模式，隐藏选择框
                    dismissPopWindow();
                    mCurrentMode = Mode.Normal;
                    invalidate();
                } else {
                    showPopWindow();
                }
                Release();
            } else {
                Log.e("ACTION_UP", "-------->ACTION_UP");
                if (!isMove) {
                    if (onSectionClick()) {
                        return true;
                    }

                    cancelPage = false;
                    //是否点击了中间
                    if (downX > mScreenWidth / 5 && downX < mScreenWidth * 4 / 5 && downY > mScreenHeight / 3 && downY < mScreenHeight * 2 / 3) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        Log.e("ACTION_UP", "-------->center");
//                    mCornerX = 1; // 拖拽点对应的页脚
//                    mCornerY = 1;
//                    mTouch.x = 0.1f;
//                    mTouch.y = 0.1f;
                        return true;
                    } else if (x < mScreenWidth / 2) {
                        isNext = false;
                    } else {
                        isNext = true;
                    }

                    if (isNext) {
                        Boolean isNext = mTouchListener.nextPage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                        if (!isNext) {
                            return true;
                        }
                    } else {
                        Boolean isPre = mTouchListener.prePage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.pre);
                        if (!isPre) {
                            return true;
                        }
                    }
                }

                if (cancelPage && mTouchListener != null) {
                    mTouchListener.cancel();
                }

                Log.e("ACTION_UP", "-------->isNext:" + isNext);

                if (!noNext) {
                    isRuning = true;
                    mAnimationProvider.startAnimation(mScroller);
                    this.postInvalidate();
                }
            }
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
//        Log.e("onDraw","isNext:" + isNext + "          isRuning:" + isRuning);
        if (isRuning) {
            mAnimationProvider.drawMove(canvas);
        } else {
            mAnimationProvider.drawStatic(canvas);
        }

        if (mCurrentMode != Mode.Normal) {
            Log.e("onDraw", "---x-------onDraw-----------------y--" + mCurrentMode);
            DrawSelectText(canvas);
        }
    }

    private float BorderPointradius = 20;
    private Path mSelectTextPath = new Path();
    private ShowChar FirstSelectShowChar = null;
    private ShowChar LastSelectShowChar = null;

    private void DrawSelectText(Canvas canvas) {
        if (mCurrentMode == Mode.PressSelectText) {
            DrawPressSelectText(canvas, true);
            DrawMoveSelectText(canvas);
        } else if (mCurrentMode == Mode.SelectMoveForward) {
            DrawMoveSelectText(canvas);
        } else if (mCurrentMode == Mode.SelectMoveBack) {
            DrawMoveSelectText(canvas);
        }
    }

    private void DrawMoveSelectText(Canvas canvas) {
        if (FirstSelectShowChar == null || LastSelectShowChar == null)
            return;
        GetSelectData();
        DrawSeletLines(canvas);
        DrawBorderPoint(canvas);
    }

    //文字两边的光标
    private void DrawBorderPoint(Canvas canvas) {
        float Padding = 0;

        canvas.drawCircle(FirstSelectShowChar.TopLeftPosition.x - Padding,
                FirstSelectShowChar.TopLeftPosition.y - Padding, BorderPointradius, mBorderPointPaint);

        canvas.drawCircle(LastSelectShowChar.BottomRightPosition.x + Padding,
                LastSelectShowChar.BottomRightPosition.y + Padding, BorderPointradius, mBorderPointPaint);

        canvas.drawLine(FirstSelectShowChar.TopLeftPosition.x - Padding,
                FirstSelectShowChar.TopLeftPosition.y - Padding, FirstSelectShowChar.BottomLeftPosition.x - Padding,
                FirstSelectShowChar.BottomLeftPosition.y, mBorderPointPaint);

        canvas.drawLine(LastSelectShowChar.BottomRightPosition.x + Padding,
                LastSelectShowChar.BottomRightPosition.y + Padding, LastSelectShowChar.TopRightPosition.x + Padding,
                LastSelectShowChar.TopRightPosition.y, mBorderPointPaint);
    }

    // 绘制椭圆型的选中背景
    private void DrawSeletLines(Canvas canvas) {
        selectText = "";
        for (ShowLine l : mSelectLines) {
            selectText += l.getLineData();
            selectTextSection = l.sectionIndex;
            if (l.CharsData != null && l.CharsData.size() > 0) {


                ShowChar fistchar = l.CharsData.get(0);
                ShowChar lastchar = l.CharsData.get(l.CharsData.size() - 1);

                float fw = fistchar.charWidth;
                float lw = lastchar.charWidth;

                RectF rect = new RectF(fistchar.TopLeftPosition.x, fistchar.TopLeftPosition.y,
                        lastchar.TopRightPosition.x, lastchar.BottomRightPosition.y);

                canvas.drawRoundRect(rect, fw / 2,
                        TextHeight / 2, mTextSelectPaint);

            }
        }
        Log.e("selectline---------->", mSelectLines.get(0).sectionIndex + "------选取内容------>" + selectText);
    }

    private void GetSelectData() {

        Boolean Started = false;
        Boolean Ended = false;

        mSelectLines.clear();

        // 找到选择的字符数据，转化为选择的行，然后将行选择背景画出来
        for (ShowLine l : mLinseData) {

            ShowLine selectline = new ShowLine();
            selectline.CharsData = new ArrayList<ShowChar>();
            selectline.sectionIndex = l.sectionIndex;
            for (ShowChar c : l.CharsData) {

                if (!Started) {
                    if (c.Index == FirstSelectShowChar.Index) {
                        Started = true;
                        selectline.CharsData.add(c);
                        if (c.Index == LastSelectShowChar.Index) {
                            Ended = true;
                            break;
                        }
                    }
                } else {

                    if (c.Index == LastSelectShowChar.Index) {
                        Ended = true;
                        if (!selectline.CharsData.contains(c)) {
                            selectline.CharsData.add(c);
                        }
                        break;
                    } else {
                        selectline.CharsData.add(c);
                    }
                }
            }

            mSelectLines.add(selectline);

            if (Started && Ended) {
                break;
            }
        }
    }


    //按下时查找字符串所在位置，画上下左右
    private void DrawPressSelectText(Canvas canvas, boolean isFirst) {
        ShowChar p = DetectPressShowChar(Down_X, Down_Y, isFirst);
        // 找到了选择的字符，首次按下选一行，不走这
        if (p != null && !isFirst) {
//            FirstSelectShowChar = LastSelectShowChar = p;
            mSelectTextPath.reset();
            mSelectTextPath.moveTo(p.TopLeftPosition.x, p.TopLeftPosition.y);
            mSelectTextPath.lineTo(p.TopRightPosition.x, p.TopRightPosition.y);
            mSelectTextPath.lineTo(p.BottomRightPosition.x, p.BottomRightPosition.y);
            mSelectTextPath.lineTo(p.BottomLeftPosition.x, p.BottomLeftPosition.y);
            canvas.drawPath(mSelectTextPath, mTextSelectPaint);

            DrawBorderPoint(canvas);
        }
    }

    /**
     * 查找字符串在哪一个的哪个字
     *
     * @param down_X2
     * @param down_Y2
     * @return
     */
    private ShowChar DetectPressShowChar(float down_X2, float down_Y2) {
        return DetectPressShowChar(down_X2, down_Y2, false);
    }

    private ShowChar DetectPressShowChar(float down_X2, float down_Y2, boolean isFirst) {
        for (ShowLine l : mLinseData) {
            for (ShowChar c : l.CharsData) {
                if (down_Y2 > c.BottomLeftPosition.y) {
                    break;// 说明是在下一行
                }
                if (down_X2 >= c.BottomLeftPosition.x && down_X2 <= c.BottomRightPosition.x) {
                    //首次按下把这一行都选中
                    if (isFirst) {
                        FirstSelectShowChar = l.getCharsData().get(0);
                        LastSelectShowChar = l.getCharsData().get(l.getCharsData().size() - 1);
                    }
                    return c;
                }
            }
        }
        return null;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mAnimationProvider.setTouchPoint(x, y);
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRuning = false;
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mAnimationProvider.setTouchPoint(mScroller.getFinalX(), mScroller.getFinalY());
            postInvalidate();
        }
    }

    public boolean isRunning() {
        return isRuning;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }


    public interface TouchListener {

        Boolean center();

        Boolean prePage();

        Boolean nextPage();

        void cancel();
    }

    public enum Mode {
        Normal, PullDown, PressSelectText, SelectMoveForward, SelectMoveBack
    }

    private boolean CanMoveBack(float Tounchx, float Tounchy) {

        Path p = new Path();
        p.moveTo(FirstSelectShowChar.TopLeftPosition.x, FirstSelectShowChar.TopLeftPosition.y);
        p.lineTo(getWidth(), FirstSelectShowChar.TopLeftPosition.y);
        p.lineTo(getWidth(), getHeight());
        p.lineTo(0, getHeight());
        p.lineTo(0, FirstSelectShowChar.BottomLeftPosition.y);
        p.lineTo(FirstSelectShowChar.BottomLeftPosition.x, FirstSelectShowChar.BottomLeftPosition.y);
        p.lineTo(FirstSelectShowChar.TopLeftPosition.x, FirstSelectShowChar.TopLeftPosition.y);

        return computeRegion(p).contains((int) Tounchx, (int) Tounchy);
    }

    private boolean CanMoveForward(float Tounchx, float Tounchy) {

        Path p = new Path();
        p.moveTo(LastSelectShowChar.TopRightPosition.x, LastSelectShowChar.TopRightPosition.y);
        p.lineTo(getWidth(), LastSelectShowChar.TopRightPosition.y);
        p.lineTo(getWidth(), 0);
        p.lineTo(0, 0);
        p.lineTo(0, LastSelectShowChar.BottomRightPosition.y);
        p.lineTo(LastSelectShowChar.BottomRightPosition.x, LastSelectShowChar.BottomRightPosition.y);
        p.lineTo(LastSelectShowChar.TopRightPosition.x, LastSelectShowChar.TopRightPosition.y);

        return computeRegion(p).contains((int) Tounchx, (int) Tounchy);
    }

    private void Release() {
        Down_X = -1;// 释放
        Down_Y = -1;
    }

    // 检测是否准备滑动选择文字
    private Boolean CheckIfTrySelectMove(float xposition, float yposition) {
        if (FirstSelectShowChar == null || LastSelectShowChar == null) {
            return false;
        }

        float flx, fty, frx, fby;//第一个字左边 X，右边 X，顶部Y，底部Y

        float hPadding = FirstSelectShowChar.charWidth;
        Log.e("CheckIfTrySelectMove", "------------hPadding---------" + hPadding);
        hPadding = hPadding < 10 ? 10 : hPadding;

        flx = FirstSelectShowChar.TopLeftPosition.x - hPadding;
        frx = FirstSelectShowChar.TopLeftPosition.x;

        fty = FirstSelectShowChar.TopLeftPosition.y;
        fby = FirstSelectShowChar.BottomLeftPosition.y;

        float llx, lty, lrx, lby;//最后一个字右边 X，右边加上字的宽度 X，顶部Y，底部Y

        llx = LastSelectShowChar.BottomRightPosition.x;
        lrx = LastSelectShowChar.BottomRightPosition.x + hPadding;

        lty = LastSelectShowChar.TopRightPosition.y;
        lby = LastSelectShowChar.BottomRightPosition.y;

        Log.i("up1x", xposition + "-----1----->=" + flx + "--------------<=" + frx);
        Log.i("up2y", yposition + "-----2----->=" + fty + "--------------<=" + fby);
        if ((xposition + 20 >= flx && xposition - 20 <= frx) && (yposition >= fty && yposition <= fby)) {
            mCurrentMode = Mode.SelectMoveForward;
            return true;
        }
        Log.w("down1x", xposition + "---------1------->=" + llx + "--------------<=" + lrx);
        Log.w("down2y", yposition + "---------2------->=" + lty + "--------------<=" + lby);
        if ((xposition + 20 >= llx && xposition <= lrx) && (yposition >= lty)) {
            mCurrentMode = Mode.SelectMoveBack;
            return true;
        }

        //在选择范围外30dp取消选择
        if (fty - yposition > Util.dp2px(mContext, 30) || yposition - lby > Util.dp2px(mContext, 30)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 通过路径计算区域
     *
     * @param path 路径对象
     * @return 路径的Region
     */
    private Region computeRegion(Path path) {
        Region region = new Region();
        RectF f = new RectF();
        path.computeBounds(f, true);
        region.setPath(path, new Region((int) f.left, (int) f.top, (int) f.right, (int) f.bottom));
        return region;
    }


    /**
     * 点击段尾评论按钮
     */
    public boolean onSectionClick() {
        int space = ScreenUtil.dpToPxInt(25);
        for (int i = 0; i < sectionEnd.size(); i++) {
            ShowChar c = sectionEnd.get(i);
            Log.e(sectionEnd.size() + "onSectionClick--" + downY + "---<=" + c.BottomLeftPosition.y, downX + "---------->=" + c.BottomLeftPosition.x + "--------<=" + c.BottomRightPosition.x);
            if (Math.abs(downX - c.BottomRightPosition.x) < space && Math.abs(downY - c.BottomRightPosition.y) < space) {//&& downY + 35 <= c.BottomLeftPosition.y
                ToastUtil.showSingleToast(i + "点击了段落id=" + c.sectionIndex);
                if(ListenerManager.getInstance().getSendBookComment() !=null){
                    ListenerManager.getInstance().getSendBookComment().show(this,c.sectionIndex,c.BottomRightPosition.x,c.BottomRightPosition.y);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void comment() {
        initSelectBg();
        new BookCommentDialog(activity,selectText,selectTextSection).show();
    }

    @Override
    public void copy() {
        initSelectBg();
        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("copy_text", selectText);
        clipboardManager.setPrimaryClip(mClipData);
        ToastUtil.showToast("已复制到剪贴板");
    }

    @Override
    public void share() {
        initSelectBg();
        ShareSelectTextDialog selectTextDialog = new ShareSelectTextDialog(mContext);
        selectTextDialog.setData(bookDetailResponse, selectText);
        selectTextDialog.show();
    }

    public void initSelectBg() {
        mCurrentMode = Mode.Normal;
        invalidate();
    }

    /**
     * 获取段落位置
     *
     * @param selectText
     * @return
     */
    public int getSectionIndex(String selectText) {
        if (!TextUtils.isEmpty(mCurrentContent)) {
            String section[] = mCurrentContent.split("\n");
            if (selectText.contains("\n")) {
                String selectArr[] = selectText.split("\n");
                selectText = selectArr[selectArr.length - 1];
            }
            for (int i = 0; i < section.length; i++) {
                String text = section[i];
                if (text.contains(selectText)) {
                    return (i + 1);
                }
            }
        }
        return 0;
    }
}
