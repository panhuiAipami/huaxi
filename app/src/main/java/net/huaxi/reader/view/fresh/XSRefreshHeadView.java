package net.huaxi.reader.view.fresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.tools.commonlibs.tools.LogUtils;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import in.srain.cube.views.ptr.indicator.PtrTensionIndicator;

/**
 * Created by ZMW on 2016/1/29.
 */
public class XSRefreshHeadView extends View implements PtrUIHandler {

    public static final boolean debug=false;
    private XSRefreshDrawable mDrawable;
    private PtrFrameLayout mPtrFrameLayout;
    private PtrTensionIndicator mPtrTensionIndicator;

    public XSRefreshHeadView(Context context) {
        super(context);
        init();
    }

    public XSRefreshHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XSRefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void setUp(PtrFrameLayout ptrFrameLayout) {
        mPtrFrameLayout = ptrFrameLayout;
        mPtrTensionIndicator = new PtrTensionIndicator();
        mPtrFrameLayout.setPtrIndicator(mPtrTensionIndicator);
    }
    private void init() {
        mDrawable = new XSRefreshDrawable(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height=mDrawable.getTotalDragDistance();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingTop() +
                getPaddingBottom(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        mDrawable.setBounds(pl, pt, pl + right - left, pt + bottom - top);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mDrawable.draw(canvas);
        float percent = mPtrTensionIndicator.getOverDragPercent();
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mDrawable.resetOriginals();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mDrawable.resetOriginals();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mDrawable.start();
        float percent = mPtrTensionIndicator.getOverDragPercent();
        mDrawable.offsetTopAndBottom(mPtrTensionIndicator.getCurrentPosY());
        mDrawable.setmPercent(percent);
        invalidate();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        if(debug){
            LogUtils.debug("head==onUIRefreshComplete");
        }
        float percent = mPtrTensionIndicator.getOverDragPercent();
        mDrawable.stop();
        mDrawable.offsetTopAndBottom(mPtrTensionIndicator.getCurrentPosY());
        mDrawable.setmPercent(percent);
        invalidate();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        if(debug){
            LogUtils.debug("head==onUIPositionChange"+mPtrTensionIndicator.getOverDragPercent());
        }
        float percent = mPtrTensionIndicator.getOverDragPercent();
        mDrawable.offsetTopAndBottom(mPtrTensionIndicator.getCurrentPosY());
        mDrawable.setmPercent(percent);
        invalidate();
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr == mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }

    public void recycle(){
        mDrawable.recycle();
    }
}
