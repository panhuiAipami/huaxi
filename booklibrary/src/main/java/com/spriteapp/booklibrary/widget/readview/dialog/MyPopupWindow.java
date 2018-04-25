package com.spriteapp.booklibrary.widget.readview.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;


public class MyPopupWindow extends PopupWindow {
    Context mContext;
    private  LayoutInflater mInflater;
    private  View mContentView;
    View.OnClickListener click;

    public MyPopupWindow(Context context, View.OnClickListener click) {
        super(context);
        this.click = click;
        this.mContext=context;
        //打气筒
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mContentView = mInflater.inflate(R.layout.popup_window_layout,null);
        //设置View
        setContentView(mContentView);
//        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
//        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * 设置进出动画
         */
//        setAnimationStyle(R.style.MyPopupWindow);
        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        setBackgroundDrawable(new ColorDrawable());
        /**
         * 设置可以获取集点
         */
        setFocusable(false);
        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);
        /**
         *设置可以触摸
         */
        setTouchable(true);
        /**
         * 设置点击外部可以消失
         */
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                /**
                 * 判断是不是点击了外部
                 */
                if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
                    return true;
                }
                //不是点击外部
                return false;
            }
        });

        /**
         * 初始化View与监听器
         */
        initView();
        initListener();
    }

    TextView textView1,textView2,textView3 ;
    private void initView() {
        textView1 = (TextView) mContentView.findViewById(R.id.textView1);
        textView2 = (TextView) mContentView.findViewById(R.id.textView2);
        textView3 = (TextView) mContentView.findViewById(R.id.textView3);


    }

    private void initListener() {
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                click.onClick(v);
            }
        });
    }
}