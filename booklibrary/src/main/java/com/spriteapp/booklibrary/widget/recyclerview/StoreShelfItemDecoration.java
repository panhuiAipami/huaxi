package com.spriteapp.booklibrary.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kuangxiaoguo on 2017/7/29.
 */

public class StoreShelfItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "StoreShelfItemDecoratio";
    private int space;
    private int bottomSpace;

    public StoreShelfItemDecoration(int space) {
        this.space = space;
    }

    public StoreShelfItemDecoration(int space, int bottomSpace) {
        this.space = space;
        this.bottomSpace = bottomSpace;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        if (spanIndex == 1) {
            outRect.left = (int) (space * 0.45f);
        } else if (spanIndex == 2) {
            outRect.left = space / 2;
        }
        outRect.bottom = bottomSpace;
    }

}
