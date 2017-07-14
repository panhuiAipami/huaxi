package net.huaxi.reader.view.fresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by ZMW on 2016/4/11.
 */
public class XSRecentlyPullLayout extends PtrFrameLayout {
    private XSRecentlyPullHeadView headView;

    public XSRecentlyPullLayout(Context context) {
        super(context);
        initView();
    }

    public XSRecentlyPullLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public XSRecentlyPullLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        headView = new XSRecentlyPullHeadView(getContext());
        headView.setup(this);
        headView.setLayoutParams(new LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setHeaderView(headView);
        addPtrUIHandler(headView);
        setLoadingMinTime(1000);
        setDurationToCloseHeader(1000);
    }
}
