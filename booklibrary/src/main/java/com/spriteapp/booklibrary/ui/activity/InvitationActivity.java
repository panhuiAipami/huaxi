package com.spriteapp.booklibrary.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;

/**
 * 邀请活激活徒弟activity
 */
public class InvitationActivity extends TitleActivity {


    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_invitation, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
