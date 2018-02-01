package com.spriteapp.booklibrary.ui.activity;

import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;

public class ExpandMoreActivity extends TitleActivity {


    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_expand_more, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
    }
}
