package com.spriteapp.booklibrary.listener;

import android.content.Context;

import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;

/**
 * Created by kuangxiaoguo on 2017/7/25.
 */

public interface ChannelListener {

    void toLoginPage(Context context);

    void showShareDialog(Context context, BookDetailResponse shareDetail, boolean isNightMode);

    void toWXPay(WeChatBean response, String push_id);

}
