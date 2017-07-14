package net.huaxi.reader.view.fresh;

import android.view.View;
import android.widget.AbsListView;

import net.huaxi.reader.R;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by ZMW on 2016/4/14.
 */
public abstract class ShelfPtrHandler implements PtrHandler {
    public boolean canChildScrollUp(View view) {
        View nobook=view.findViewById(R.id.shelf_nobook_layout);
        if(nobook.getVisibility()==View.VISIBLE){
            return false;
        }
        View view1=view.findViewById(R.id.shelf_book_recyclerview);
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view1 instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view1;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view1.getScrollY() > 0;
            }
        } else {
            return view1.canScrollVertically(-1);
        }
    }

    /**
     * Default implement for check can perform pull to refresh
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }
}
