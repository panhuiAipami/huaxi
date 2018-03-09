package com.spriteapp.booklibrary.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;

public class TaskActivity extends TitleActivity {


    @Override
    public void initData() throws Exception {
        setTitle("搜索");
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_task, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
