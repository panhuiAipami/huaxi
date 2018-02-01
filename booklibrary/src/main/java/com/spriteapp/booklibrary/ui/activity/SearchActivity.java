package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.listener.ChannelListener;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;

/**
 * 搜索
 */
public class SearchActivity extends BaseActivity {


    @Override
    public int getLayoutResId() throws Exception {
        return R.layout.activity_search;
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void configViews() throws Exception {
        ChannelListener c = new ChannelListener() {
            @Override
            public void toLoginPage(Context context) {

            }

            @Override
            public void showShareDialog(Context context, BookDetailResponse shareDetail, boolean isNightMode) {

            }

            @Override
            public void toWXPay(WeChatBean response, String push_id) {

            }
        };


    }

    @Override
    public void findViewId() throws Exception {

    }
}
