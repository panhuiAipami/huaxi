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

/**
 * Created by Administrator on 2018/1/11.
 */

public class FollowPop extends PopupWindow {
    private Activity context;
    private LayoutInflater mInflater;
    private View mContentView;
    private TextView guanzhu, jubao, pingbi;

    public FollowPop(Activity activity, View v) {
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
        guanzhu = (TextView) item.findViewById(R.id.guanzhu);
        jubao = (TextView) item.findViewById(R.id.jubao);
        pingbi = (TextView) item.findViewById(R.id.pingbi);
    }

    public TextView setFollow() {//返回关注textView
        return guanzhu;
    }

    public TextView setJuBao() {//返回举报textView
        return jubao;
    }
}
