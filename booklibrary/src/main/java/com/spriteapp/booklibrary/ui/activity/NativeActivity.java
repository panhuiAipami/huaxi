package com.spriteapp.booklibrary.ui.activity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.util.ScreenShot;
import com.spriteapp.booklibrary.util.ScreenUtil;

/**
 * Created by Administrator on 2018/1/4.
 */

public class NativeActivity extends TitleActivity {
    private ScrollView scrollView;
    private TextView textView;


    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.native_layout, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textView = (TextView) findViewById(R.id.textView);
        textView.setMaxLines(3);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//执行截屏
                scrollView.setVisibility(View.INVISIBLE);
                textView.setMaxLines(100);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("textView_click", "执行截屏");
                        ScreenShot.shoot(scrollView);
                    }
                });

            }
        });
    }
}
