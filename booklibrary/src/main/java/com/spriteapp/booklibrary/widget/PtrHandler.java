package com.spriteapp.booklibrary.widget;

import android.view.View;

/**
 * Created by hzh on 2016/5/4.
 */
public interface PtrHandler
{
    /**
     * check can do refresh or not
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * when refresh begin
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);
}
