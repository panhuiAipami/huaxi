package net.huaxi.reader.view.fresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by ZMW on 2016/1/20.
 */
public class XSDevilfishLayout extends PtrFrameLayout {
    private XSRefreshHeadView xsDevilfishHeadView;
    private boolean canrecycle=true;
    public XSDevilfishLayout(Context context) {
        super(context);
        initViews();
    }

    public XSDevilfishLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public XSDevilfishLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        xsDevilfishHeadView = new XSRefreshHeadView(getContext());
        xsDevilfishHeadView.setLayoutParams(new LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        xsDevilfishHeadView.setUp(this);
        setHeaderView(xsDevilfishHeadView);
        addPtrUIHandler(xsDevilfishHeadView);
        setLoadingMinTime(1000);
        setDurationToCloseHeader(1000);
    }

    public XSRefreshHeadView getHeader() {
        return xsDevilfishHeadView;
    }

    public void setCanrecycle(boolean canrecycle){
        this.canrecycle=canrecycle;
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(canrecycle){
            xsDevilfishHeadView.recycle();
        }
    }

}
