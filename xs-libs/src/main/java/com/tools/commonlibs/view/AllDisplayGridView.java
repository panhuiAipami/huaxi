package com.tools.commonlibs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义GridView，在内容变化的时候，不显示滚动条，高度自适应.
 * 
 * @author taoyf
 * @time 2015年6月5日
 */
public class AllDisplayGridView extends GridView {

    public AllDisplayGridView(Context context) {
		super(context);
	}

	public AllDisplayGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AllDisplayGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
