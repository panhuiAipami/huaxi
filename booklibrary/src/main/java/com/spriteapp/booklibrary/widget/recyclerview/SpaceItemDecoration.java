package com.spriteapp.booklibrary.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kuangxiaoguo on 2017/5/11.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "SpaceItemDecoration";
    private int space;
    private int bottomSpace;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    public SpaceItemDecoration(int space, int bottomSpace) {
        this.space = space;
        this.bottomSpace = bottomSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = layoutManager.getSpanCount();
        outRect.left = space;
        int position = parent.getChildAdapterPosition(view);
        //只有第一行需要设置top高度
        if (position < spanCount) {
            outRect.top = bottomSpace;
        } else {
            outRect.top = 0;
        }
        int itemCount = parent.getAdapter().getItemCount();

        if (itemCount > spanCount) {
            int value = itemCount % spanCount;
            switch (value) {
                case 0:
                    if (position >= itemCount - 1) {
                        outRect.bottom = bottomSpace;
                    }
                    break;
                case 1:
                case 2:
                    if (position >= itemCount - value) {
                        outRect.bottom = bottomSpace;
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
