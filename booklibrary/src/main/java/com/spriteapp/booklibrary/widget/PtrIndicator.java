package com.spriteapp.booklibrary.widget;

import android.graphics.PointF;

/**
 * Created by hzh on 2016/5/3.
 */
public class PtrIndicator
{
    public final static int START_POS = 0;

    private float mResistence = 1.7f;
    private float mRatioOfHeaderHeightToRefresh = 1.2f;

    private int mHeaderHeight = 0;
    private int mOffsetToRefresh;

    private int mOffsetToKeepHeaderWhileLoading = -1;

    private int mCurrentPos = START_POS;
    private int mLastPos = START_POS;
    private int mPressedPos = 0;

    private float mOffsetX = 0;
    private float mOffsetY = 0;

    private PointF mPtLastMove = new PointF();

    private boolean mIsUnderTouch = false;

    public boolean isUnderTouch()
    {
        return mIsUnderTouch;
    }

    public void onRelease()
    {
        mIsUnderTouch = false;
    }

    public void setResistence(float resistence)
    {
        this.mResistence = resistence;
    }

    public float getResistence()
    {
        return mResistence;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio)
    {
        this.mRatioOfHeaderHeightToRefresh = ratio;
    }

    public float getRatioOfHeaderHeightToRefresh()
    {
        return mRatioOfHeaderHeightToRefresh;
    }

    public void setHeaderHeight(int height)
    {
        this.mHeaderHeight = height;
        updateHeight();
    }

    private void updateHeight()
    {
        mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
    }

    public int getHeaderHeight()
    {
        return mHeaderHeight;
    }

    public int getCurrentPosY()
    {
        return mCurrentPos;
    }

    public int getLastPos()
    {
        return mLastPos;
    }

    public void setCurrentPosY(int current)
    {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
    }

    public int getOffsetToRefresh()
    {
        return mOffsetToRefresh;
    }

    public void onPressDown(float x, float y)
    {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPos;
        mPtLastMove.set(x, y);
    }

    public void onMove(float x, float y)
    {
        float offsetX = x - mPtLastMove.x;
        float offsetY = y - mPtLastMove.y;
        setOffset(offsetX, offsetY / mResistence);
        mPtLastMove.set(x, y);
    }

    protected void setOffset(float x, float y)
    {
        mOffsetX = x;
        mOffsetY = y;
    }

    public boolean isInStartPosition()
    {
        return mCurrentPos == START_POS;
    }

    public boolean hasLeftStartPosition()
    {
        return mCurrentPos > START_POS;
    }

    public boolean hasJustLeftStartPosition()
    {
        return mLastPos == START_POS && hasLeftStartPosition();
    }

    public boolean hasJustBackToStartPosition()
    {
        return mLastPos != START_POS && isInStartPosition();
    }

    public float getOffsetX()
    {
        return mOffsetX;
    }

    public float getOffsetY()
    {
        return mOffsetY;
    }

    public boolean isAlreadyHere(int to)
    {
        return mCurrentPos == to;
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset)
    {
        mOffsetToKeepHeaderWhileLoading = offset;
    }

    public int getOffsetToKeepHeaderWhileLoading()
    {
        return mOffsetToKeepHeaderWhileLoading >= 0 ? mOffsetToKeepHeaderWhileLoading : mHeaderHeight;
    }

    public boolean isOverOffsetToKeepHeaderWhileLoading()
    {
        return mCurrentPos > getOffsetToKeepHeaderWhileLoading();
    }

    public boolean isOverOffsetToRefresh()
    {
        return mCurrentPos > mOffsetToRefresh;
    }

    public boolean willOverTop(int to)
    {
        return to < START_POS;
    }

}
