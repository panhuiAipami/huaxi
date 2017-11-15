package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.content.Intent;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.PublishCommentActivity;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;
import com.spriteapp.booklibrary.ui.activity.RecentReadActivity;
import com.spriteapp.booklibrary.ui.activity.SettingActivity;
import com.spriteapp.booklibrary.ui.activity.WebViewActivity;

/**
 * Created by kuangxiaoguo on 2017/7/15.
 */

public class ActivityUtil {

    public static void toWebViewActivity(Context context, String url) {
        toWebViewActivity(context, url, false);
    }

    public static void toWebViewActivity(Context context, String url, boolean isH5Pay) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.LOAD_URL_TAG, url);
        intent.putExtra(WebViewActivity.IS_H5_PAY_TAG, isH5Pay);
        context.startActivity(intent);
    }

    public static void toReadActivity(Context context, BookDetailResponse detail) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(ReadActivity.BOOK_DETAIL_TAG, detail);
        context.startActivity(intent);
    }

    public static void toSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void toPublishCommentActivity(Context context, int bookId) {
        if (!AppUtil.isLogin()) {
            HuaXiSDK.getInstance().toLoginPage(context);
            return;
        }
        Intent intent = new Intent(context, PublishCommentActivity.class);
        intent.putExtra(PublishCommentActivity.BOOK_ID_TAG, bookId);
        context.startActivity(intent);
    }

    public static void toCommonActivity(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }
}
