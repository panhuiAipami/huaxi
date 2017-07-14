package com.tools.commonlibs.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tools.commonlibs.R;


/**
 * Created by ZMW on 2016/1/19.
 */
public class LoadingDialog extends Dialog {
    private String text;
    private boolean isDim = false;
    private boolean canCanceled = true;

    public LoadingDialog(Context context, String text) {
        super(context, R.style.popBottomDialog);
        this.text = text;
    }

    /**
     * 构造函数
     *
     * @param context
     * @param text
     * @param isDim   背景是否暗淡.
     */
    public LoadingDialog(Context context, String text, boolean isDim, boolean canCanceled) {
        super(context, R.style.popBottomDialog);
        this.text = text;
        this.isDim = isDim;
        this.canCanceled = canCanceled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_layout);
        setCanceledOnTouchOutside(canCanceled);
        setCancelable(canCanceled);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (isDim) {    // 背景暗淡
            lp.flags = (lp.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {// 背景透明
            lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        getWindow().setAttributes(lp);
        if (text != null) {
            TextView textView = (TextView) findViewById(R.id.loading_dialog_textview);
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }
}
