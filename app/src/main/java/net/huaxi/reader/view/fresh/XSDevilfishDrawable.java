package net.huaxi.reader.view.fresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.tools.commonlibs.tools.LogUtils;

import net.huaxi.reader.R;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by ZMW on 2016/1/20.
 */
public class XSDevilfishDrawable extends Drawable implements Animatable {
    private final boolean DEBUG = false;
    private float mPercent;
    Context mContext;
    private Matrix mMatrix;//矩阵工具类
    private Animation mAnimation;//动画

    //父控件
    View mParent;
    private int mTotalDragDistance;//下拉最大距离
    private int mScreenWidth;//屏幕宽度
    private int mTop;//距离上边高度

    private Bitmap mBackground;//背景
    private final static float SKY_RATIO = 0.43f;
    private int mBackgroundHeight;

    private Bitmap mWaveF;//前面的水波纹
    private Bitmap mWaveB;//后面的水波纹
    private static final float WAVE_RATIO = 0.15F;
    private final static float WAVE_WIDTH_RATIO = 1.6F;
    private int mWaveHeight;

    private Bitmap mBubble;
    private int mBubbleWidth;
    private int mBubbleHeight;
    private final float BUBBLE_WIDTH_RATIO = 0.025F;
    private final float BUBBLESCALE = 1.2f;

    public XSDevilfishDrawable(Context context, XSDevilfishHeadView headView) {
        mContext = context;
        mParent = headView;
        mMatrix = new Matrix();
        initiateDimens();
        createBitmaps();
        setupAnimations();
    }

    //初始化距离
    private void initiateDimens() {
        PtrLocalDisplay.init(mContext);
        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mTotalDragDistance = (int) (0.45 * mScreenWidth);
        mBackgroundHeight = (int) (SKY_RATIO * mScreenWidth);
        mWaveHeight = (int) (WAVE_RATIO * mScreenWidth);
        mBubbleWidth = (int) (BUBBLE_WIDTH_RATIO * mScreenWidth);
        mBubbleHeight = (int) (BUBBLE_WIDTH_RATIO * mScreenWidth);

    }

    // 创建图片
    private void createBitmaps() {
        mBackground = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .xs_loadding_background);
        mBackground = Bitmap.createScaledBitmap(mBackground, mScreenWidth, mBackgroundHeight, true);
        mWaveF = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .xs_loadding_wavef);
        mWaveF = Bitmap.createScaledBitmap(mWaveF, (int) (mScreenWidth * WAVE_WIDTH_RATIO),
                mWaveHeight, true);
        mWaveB = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .xs_loadding_waveb);
        mWaveB = Bitmap.createScaledBitmap(mWaveB, (int) (mScreenWidth * WAVE_WIDTH_RATIO),
                mWaveHeight, true);
        mBubble = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .xs_loadding_bubble);
        mBubble = Bitmap.createScaledBitmap(mBubble, mBubbleWidth, mBubbleHeight, true);
    }

    //设置动画
    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationOffset(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(1000);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, mBackgroundHeight + top);
    }

    boolean isRefreshing;//动画是否在运行

    @Override
    public void start() {
        mAnimation.reset();
        isRefreshing = true;
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    //主要绘画方法
    @Override
    public void draw(Canvas canvas) {
        final int saveCount = canvas.save();
        canvas.translate(0, mTotalDragDistance - mTop);//canvas跟着下拉去走
        if (DEBUG) {
            LogUtils.debug("draw");
        }
        drawSky(canvas);
        drawWaveF(canvas);
        drawBubble(canvas);
        drawWaveB(canvas);
        canvas.restoreToCount(saveCount);
    }

    //默认情况是在顶部不动，offsetY 向下偏移量 ，offsetX向右偏移量
    private void drawSky(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();
        int y = Math.max(0, mTop - mTotalDragDistance);//开始是0，慢慢的大于0
        float dragPercent = Math.min(1f, Math.abs(mPercent));
        float skyScale = 1.05f;
        float offsetX = (float) (-mScreenWidth * 0.025);//水平居中
        float offsetY = 0;
        offsetY = (float) (mTop - mTotalDragDistance + mScreenWidth * SKY_RATIO * 0.05 / 2);
        if (DEBUG) {
            LogUtils.debug("sky==skyScale==" + skyScale);
            LogUtils.debug("sky==offsetX==" + offsetX);
            LogUtils.debug("sky==offsetY==" + offsetY);
        }
        matrix.postScale(skyScale, skyScale);
        matrix.postTranslate(offsetX, offsetY);
        canvas.drawBitmap(mBackground, matrix, null);
    }

    //后面的水波纹,向左最大移动 mScreenWidth*((WAVE_WIDTH_RATIO-1)/4)
    private void drawWaveB(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();
        int y = Math.max(0, mTop - mTotalDragDistance);//开始是0，慢慢的大于0
        float manimationOffset = Math.min(1f, Math.abs(animationOffset));
        if (manimationOffset < 0.5) {
            manimationOffset = manimationOffset * 2;
        } else {
            manimationOffset = -2 * manimationOffset + 2;
        }
        float skyScale = 1.0f;
        float offsetX = (float) -(mScreenWidth * ((WAVE_WIDTH_RATIO - 1) / 2)) -
                (manimationOffset > 1 ? 1 : manimationOffset) * mScreenWidth * ((WAVE_WIDTH_RATIO
                        - 1) / 3);//设置水波纹居中
        float offsetY = 0;
//        offsetY = (float) (mTop - mTotalDragDistance + 0.28 * mScreenWidth + mScreenWidth *
//                SKY_RATIO * 0.1);
        offsetY = mTop - mWaveHeight;
        if (DEBUG) {
            LogUtils.debug("mWaveB==WaveBScale==" + skyScale);
            LogUtils.debug("mWaveB==offsetX==" + offsetX);
            LogUtils.debug("mWaveB==offsetY==" + offsetY);
            LogUtils.debug("mWaveB==animationOffset==" + animationOffset);
        }
        matrix.postScale(skyScale, skyScale);
        matrix.postTranslate(offsetX, offsetY);
        canvas.drawBitmap(mWaveB, matrix, null);
    }

    //前面的水波纹
    private void drawWaveF(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();
        int y = Math.max(0, mTop - mTotalDragDistance);//开始是0，慢慢的大于0
        float manimationOffset = Math.min(1f, Math.abs(animationOffset));
        if (manimationOffset < 0.5) {
            manimationOffset = manimationOffset * 2;
        } else {
            manimationOffset = -2 * manimationOffset + 2;
        }
        float skyScale = 1.0f;
        float offsetX = (float) -(mScreenWidth * ((WAVE_WIDTH_RATIO - 1) / 2)) +
                (manimationOffset > 1 ? 1 : manimationOffset) * mScreenWidth * ((WAVE_WIDTH_RATIO
                        - 1) / 3);//设置水波纹居中
        float offsetY = 0;
//        offsetY = (float) (mTop - mTotalDragDistance + 0.28 * mScreenWidth + mScreenWidth *
//                SKY_RATIO * 0.1);
        offsetY = mTop - mWaveHeight;
        if (DEBUG) {
            LogUtils.debug("mWaveB==WaveBScale==" + skyScale);
            LogUtils.debug("mWaveB==offsetX==" + offsetX);
            LogUtils.debug("mWaveB==offsetY==" + offsetY);
        }
        matrix.postScale(skyScale, skyScale);
        matrix.postTranslate(offsetX, offsetY);
        canvas.drawBitmap(mWaveF, matrix, null);
    }


    private void drawBubble(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        //第一个泡泡
        if (animationOffset > 0.2) {
            float fishScale1 = 1.0f;
            float offsetX1 = 0f;
            float offsetY1 = 0f;
//            offsetX1 = (1 - BUBBLE_WIDTH_RATIO) / 2 * mScreenWidth - mBubbleWidth / 2 - 10;
//            offsetY1 = (float) (mTop - mTotalDragDistance + 0.37 * mScreenWidth);
            offsetX1 = (1 - BUBBLE_WIDTH_RATIO) / 2 * mScreenWidth - mBubbleWidth / 2;
            offsetY1 = (float) (mTop - 0.5 * mBackgroundHeight);
            matrix.postScale(fishScale1, fishScale1);
            matrix.postTranslate(offsetX1, offsetY1);
            canvas.drawBitmap(mBubble, matrix, null);
        }
        //第二个泡泡
        if (animationOffset > 0.4) {
            matrix.reset();
            float fishScale2 = 1.2f;
            float offsetX2 = 0f;
            float offsetY2 = 0f;
            offsetX2 = (float) ((1 - BUBBLE_WIDTH_RATIO) / 2 * mScreenWidth - mBubbleWidth / 2 +
                    mScreenWidth * 0.04);
//            offsetY2 = (float) (mTop - mTotalDragDistance + 0.35 * mScreenWidth);
            offsetY2 = (float) (mTop - 0.53 * mBackgroundHeight);
            matrix.postScale(fishScale2, fishScale2);
            matrix.postTranslate(offsetX2, offsetY2);
            canvas.drawBitmap(mBubble, matrix, null);
        }
        //第三个泡泡
        if (animationOffset > 0.6) {
            matrix.reset();
            float fishScale3 = 1.2f * 1.2f;
            float offsetX3 = 0f;
            float offsetY3 = 0f;
            offsetX3 = (float) ((1 - BUBBLE_WIDTH_RATIO) / 2 * mScreenWidth - mBubbleWidth / 2 +
                    mScreenWidth * 0.09);
            offsetY3 = (float) (mTop - 0.62 * mBackgroundHeight);
            matrix.postScale(fishScale3, fishScale3);
            matrix.postTranslate(offsetX3, offsetY3);
            canvas.drawBitmap(mBubble, matrix, null);
        }
        //第四个泡泡
        if (animationOffset > 0.8) {
            matrix.reset();
            float fishScale4 = 1.2f * 1.2f;
            float offsetX4 = 0f;
            float offsetY4 = 0f;
            offsetX4 = (float) ((1 - BUBBLE_WIDTH_RATIO) / 2 * mScreenWidth - mBubbleWidth / 2 +
                    mScreenWidth * 0.16);
//            offsetY4 = (float) (mTop - mTotalDragDistance + 0.25 * mScreenWidth);
            offsetY4 = (float) (mTop - 0.77 * mBackgroundHeight);
            matrix.postScale(fishScale4, fishScale4);
            matrix.postTranslate(offsetX4, offsetY4);
            canvas.drawBitmap(mBubble, matrix, null);
        }
    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    //设置初始状态
    public void resetOriginals() {
        setmPercent(0);
        invalidateSelf();
    }

    //设置滑动百分比
    public void setmPercent(float mPercent) {
        this.mPercent = mPercent;
        setAnimationOffset(mPercent);
    }

    //开始刷新的时候y的偏移量
    public void offsetTopAndBottom(int offset) {
        mTop = offset;
        if (DEBUG) {
            LogUtils.debug("mtop==" + mTop);
        }
        invalidateSelf();
    }

    private Context getContext() {
        return mContext;
    }

    private float animationOffset = 0.0f;//子动画进度

    private void setAnimationOffset(float offset) {
        if (DEBUG) {
            LogUtils.debug("animationoffset==" + offset);
        }
        animationOffset = offset;
        mParent.invalidate();
        invalidateSelf();
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    public void recycle() {
        if (mBackground != null ) {
            mBackground.recycle();
        }
        if (mBubble != null ) {
            mBubble.recycle();
        }
        if (mWaveB != null) {
            mWaveB.recycle();
        }
        if (mWaveF != null ) {
            mWaveF.recycle();
        }
    }
}
