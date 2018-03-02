package com.spriteapp.booklibrary.widget;

/**
 * Created by hzh on 2016/5/4.
 */
public interface PtrUIHandler
{
    public void onUIReset(PtrFrameLayout frame);

    public void onUIRefreshPrepare(PtrFrameLayout frame);

    public void onUIRefreshBegin(PtrFrameLayout frame);

    public void onUIRefreshComplete(PtrFrameLayout frame);

    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
}
