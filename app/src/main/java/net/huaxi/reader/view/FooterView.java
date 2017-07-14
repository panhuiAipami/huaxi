package net.huaxi.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import net.huaxi.reader.R;

/**
 * Created by ZMW on 2015/12/26.
 */
public class FooterView extends FrameLayout {
    public static final int FOOTVIEW_STATE_LOADDING=1;
    public static final int FOOTVIEW_STATE_PRELOADDING=2;public static final int FOOTVIEW_STATE_NOMORE=3;
    public static final int FOOTVIEW_STATE_NONE=0;
    private Context context;
    public FooterView(Context context) {
        super(context);
        this.context=context;
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param i
     * 加载中
     * 上拉加载
     * 没有更多
     */
    public void setViewState(int i){
        this.removeAllViews();
        if(i==FOOTVIEW_STATE_LOADDING){
            LayoutInflater.from(context).inflate(R.layout.item_footer_loadding, this);
        }else if(i==FOOTVIEW_STATE_PRELOADDING){
            LayoutInflater.from(context).inflate(R.layout.item_footer_preloadding, this);
        }else if(i==FOOTVIEW_STATE_NOMORE){
            LayoutInflater.from(context).inflate(R.layout.item_footer_nomore, this);
        }else if(i==FOOTVIEW_STATE_NONE){
        }
    }


}
