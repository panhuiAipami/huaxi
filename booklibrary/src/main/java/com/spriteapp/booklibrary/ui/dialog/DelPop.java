package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

/**
 * Created by Administrator on 2018/1/11.
 */

public class DelPop extends PopupWindow {
    private Activity context;
    private LayoutInflater mInflater;
    private LinearLayout del_layout;
    private View mContentView, line;
    private TextView del_text, list_text;
    private ImageView imageView;
    private int currentItem;

    public DelPop(Activity activity, View v, String mode, int image, int currentItem) {
        this.context = activity;
        this.currentItem = currentItem;
        showpopWindow(v, mode, image);

    }

    public void showpopWindow(View v, String mode, int image) {
        //打气筒
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mContentView = mInflater.inflate(R.layout.bookshelf_del_layout, null);
        //设置View
        setContentView(mContentView);
        //设置宽与高
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.pop_back));
        setBackgroundDrawable(dw);
        showAsDropDown(v, 1, 0, Gravity.LEFT);
        viewClick(mContentView, mode, image);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void viewClick(View item, String mode, int image) {
        del_text = (TextView) item.findViewById(R.id.del_text);
        list_text = (TextView) item.findViewById(R.id.list_text);
        imageView = (ImageView) item.findViewById(R.id.imageView);
        del_layout = (LinearLayout) item.findViewById(R.id.del_layout);
        line = item.findViewById(R.id.line);
        list_text.setText(mode);
        imageView.setImageResource(image);
        if (currentItem == 0) {
            del_layout.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        } else {
            del_layout.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
    }

    public TextView getDel_text() {
        return del_text;
    }

    public TextView getList_text() {
        return list_text;
    }

}
