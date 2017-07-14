package net.huaxi.reader.view.fresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.tools.commonlibs.tools.Utils;

import net.huaxi.reader.R;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by ZMW on 2016/1/29.
 */
public class XSRefreshDrawable extends Drawable implements Animatable {

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

    //logo
    private Bitmap mLogo;
    private int mLogoHeight;
    private int mLogoWidth;
    private final static float LOGO_WIDTH_RATIO = 0.075F;


    public XSRefreshDrawable(Context context, XSRefreshHeadView headView) {
        mContext=context;
        mParent = headView;
        mMatrix = new Matrix();
        initiateDimens();
        createBitmaps();
        setupAnimations();
    }

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
        mAnimation.setDuration(600);
    }

    private void createBitmaps() {
        mLogo= BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .refresh_logo_xs);
        mLogo=Bitmap.createScaledBitmap(mLogo,mLogoWidth,mLogoHeight,true);
    }

    private void initiateDimens() {
        PtrLocalDisplay.init(mContext);
        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mTotalDragDistance = Utils.dip2px(mContext,80);
        mLogoWidth= (int) (LOGO_WIDTH_RATIO*mScreenWidth);
        mLogoHeight=mLogoWidth;
    }

    boolean isRefreshing;//动画是否在运行

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, mTotalDragDistance + top);
    }

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

    @Override
    public void draw(Canvas canvas) {
        final int saveCount = canvas.save();
        canvas.translate(0, mTotalDragDistance - mTop);//canvas跟着下拉去走
        drawLogo(canvas);
        canvas.restoreToCount(saveCount);
    }
    private float LOGO_BEGIN_SCALE=0.2f;
    private void drawLogo(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#f9f9f9"));
        Matrix matrix = mMatrix;
        matrix.reset();
        float scale = 1.0f;
        float offsetX = 0f;
        float offsetY = 0f;

        if(animationOffset<1 && !isRefreshing){
            scale=LOGO_BEGIN_SCALE+(1-LOGO_BEGIN_SCALE)*animationOffset;
        }
        float scaleOffset=scale*mLogoWidth / 2;
        offsetX = (mScreenWidth/2) - (scaleOffset);
        offsetY= (float) (0.5*mTop-scaleOffset);
        matrix.postScale(scale, scale,mLogoWidth*scale/2,mLogoWidth*scale/2);
        matrix.postTranslate(offsetX, offsetY);
        float r = (isRefreshing ? 360 : -360) * animationOffset * (isRefreshing ? 1f : 0.6f);
        matrix.postRotate(r, offsetX+scaleOffset, offsetY+scaleOffset);
//        if (DEBUG) {
//            LogUtils.debug("Logo==WaveBScale==" + scale);
//            LogUtils.debug("Logo==offsetX==" + offsetX);
//            LogUtils.debug("Logo==offsetY==" + offsetY);
//            LogUtils.debug("Logo==animationOffset==" + animationOffset);
//        }
        if(DEBUG){
            LogUtils.debug("mLogo.isRecycled()=="+mLogo.isRecycled());
        }
        if(!mLogo.isRecycled()){
            canvas.drawBitmap(mLogo, matrix, null);
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

    private Context getContext() {
        return mContext;
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    public void resetOriginals() {
        setmPercent(0);
        invalidateSelf();
    }

    public void offsetTopAndBottom(int currentPosY) {
        mTop = currentPosY;
        invalidateSelf();
    }

    public void setmPercent(float mPercent) {
        this.mPercent = mPercent;
        setAnimationOffset(mPercent);
    }

    private float animationOffset = 0.0f;//子动画进度

    private void setAnimationOffset(float offset) {
        animationOffset = offset;
        mParent.invalidate();
        invalidateSelf();
    }

    public void recycle() {
        if(mLogo!=null && !mLogo.isRecycled())
        mLogo.recycle();
    }
}
