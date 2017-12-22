package com.spriteapp.booklibrary.listener;

import android.content.Context;

import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.PayResponse;

/**
 * Created by kuangxiaoguo on 2017/7/25.
 */

public interface ChannelListener {

    void toLoginPage(Context context);

    void showShareDialog(Context context, BookDetailResponse shareDetail, boolean isNightMode);
    void toWXPay(PayResponse response);

}
