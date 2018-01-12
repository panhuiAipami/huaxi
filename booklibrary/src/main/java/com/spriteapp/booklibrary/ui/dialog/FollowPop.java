package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.ToastUtil;

/**
 * Created by Administrator on 2018/1/11.
 */

public class FollowPop extends PopupWindow {
    private Activity context;
    private LayoutInflater mInflater;
    private View mContentView;

    public FollowPop(Activity activity,View v) {
        this.context = activity;
        showpopWindow(v);

    }

    public void showpopWindow(View v) {
        //打气筒
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mContentView = mInflater.inflate(R.layout.square_pop_layout, null);
        //设置View
        setContentView(mContentView);
        //设置宽与高
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.pop_back));
        setBackgroundDrawable(dw);
        showAsDropDown(v, 30, 0, Gravity.LEFT);
        viewClick(mContentView);
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }
    public void viewClick(View item) {
        TextView guanzhu, jubao, pingbi;
        guanzhu = (TextView) item.findViewById(R.id.guanzhu);
        jubao = (TextView) item.findViewById(R.id.jubao);
        pingbi = (TextView) item.findViewById(R.id.pingbi);

        guanzhu.setOnClickListener(new View.OnClickListener() {//关注
            @Override
            public void onClick(View v) {
                dismiss();
                ToastUtil.showToast("关注");


            }
        });
        pingbi.setOnClickListener(new View.OnClickListener() {//屏蔽
            @Override
            public void onClick(View v) {
                dismiss();
                ToastUtil.showToast("屏蔽");
            }
        });
        jubao.setOnClickListener(new View.OnClickListener() {//举报
            @Override
            public void onClick(View v) {
                dismiss();
                ToastUtil.showToast("举报");
            }
        });

    }
}
