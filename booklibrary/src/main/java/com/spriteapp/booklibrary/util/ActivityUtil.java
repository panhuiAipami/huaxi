package com.spriteapp.booklibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.CommentReplyActivity;
import com.spriteapp.booklibrary.ui.activity.CreateDynamicActivity;
import com.spriteapp.booklibrary.ui.activity.PublishCommentActivity;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;
import com.spriteapp.booklibrary.ui.activity.SettingActivity;
import com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity;
import com.spriteapp.booklibrary.ui.activity.WebViewActivity;

/**
 * Created by kuangxiaoguo on 2017/7/15.
 */

public class ActivityUtil {
    public static final int LOGIN_BACK = 1;
    public static final int TOCREATEDYNAMICACTIVITY = 0;//跳转到发广播activity的result
    public static final String SQUAREID = "squareid";//跳转到帖子详情activity的id
    public static final String ISLOOKCOMMENT = "is_look_comment";//跳转到帖子详情activity的id
    public static final String REPLYTITLE = "comment_reply_title";
    public static final String COMMENT_ID = "comment_id";

    public static void toWebViewActivity(Context context, String url) {
        toWebViewActivity(context, url, false);
    }

    public static void toWebViewActivity(Context context, String url, boolean isH5Pay) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.LOAD_URL_TAG, url);
        intent.putExtra(WebViewActivity.IS_H5_PAY_TAG, isH5Pay);
        context.startActivity(intent);
    }

    public static void toWebViewActivity(Context context, String url, boolean isH5Pay, int IsRead) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.LOAD_URL_TAG, url);
        intent.putExtra(WebViewActivity.IS_H5_PAY_TAG, isH5Pay);
        intent.putExtra(WebViewActivity.IS_READ, IsRead);
        context.startActivity(intent);
    }

    public static void toReadActivity(Context context, BookDetailResponse detail) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(ReadActivity.BOOK_DETAIL_TAG, detail);
        context.startActivity(intent);
    }

    public static void toReadActivity(Activity context, BookDetailResponse detail, boolean hua) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(ReadActivity.BOOK_DETAIL_TAG, detail);
        context.startActivity(intent);
//        context.overridePendingTransition(R.anim.activity_out, R.anim.activity_in);

    }

    public static void toCreateDynamicActivity(Activity context) {
        Intent intent = new Intent(context, CreateDynamicActivity.class);
        context.startActivityForResult(intent, TOCREATEDYNAMICACTIVITY);
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

    public static void toSquareDetailsActivity(Context context, int squareid, int IsLookComment) {
        Intent intent = new Intent(context, SquareDetailsActivity.class);
        intent.putExtra(SQUAREID, squareid);
        intent.putExtra(ISLOOKCOMMENT, IsLookComment);
        context.startActivity(intent);

    }

    public static void toCommentReplyActivity(Context context, int total, int comment_id, int squareid) {//总条数
        Intent intent = new Intent(context, CommentReplyActivity.class);
        intent.putExtra(REPLYTITLE, total);
        intent.putExtra(COMMENT_ID, comment_id);
        intent.putExtra(SQUAREID, squareid);
        context.startActivity(intent);

    }
}
