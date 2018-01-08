package com.spriteapp.booklibrary.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by panhui on 2017/8/2.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        outRect.left = space;
//        outRect.bottom = space;
        if (parent.getChildLayoutPosition(view) % 3 == 0) {
//            outRect.left = 0;
        }
    }


}
