package com.spriteapp.booklibrary.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.spriteapp.booklibrary.util.ScreenUtil;

/**
 * Created by kuangxiaoguo on 2017/7/29.
 */

public class StoreItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int bottomSpace;

    public StoreItemDecoration(int space) {
        this.space = space;
    }

    public StoreItemDecoration(int space, int bottomSpace) {
        this.space = space;
        this.bottomSpace = bottomSpace;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        if (spanIndex == 0) {
            outRect.left = ScreenUtil.dpToPxInt(15);
        } else {
            outRect.left = space;
        }
        outRect.bottom = bottomSpace;
    }

}
