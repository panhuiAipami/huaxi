package com.spriteapp.booklibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.ApprenticeListActivity;
import com.spriteapp.booklibrary.ui.activity.BagActivity;
import com.spriteapp.booklibrary.ui.activity.BindPhoneActivity;
import com.spriteapp.booklibrary.ui.activity.CommentReplyActivity;
import com.spriteapp.booklibrary.ui.activity.CreateDynamicActivity;
import com.spriteapp.booklibrary.ui.activity.DownloadChapterActivity;
import com.spriteapp.booklibrary.ui.activity.MangerAlipayActivity;
import com.spriteapp.booklibrary.ui.activity.ProfitDetailsActivity;
import com.spriteapp.booklibrary.ui.activity.PublishCommentActivity;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;
import com.spriteapp.booklibrary.ui.activity.RechargeActivity;
import com.spriteapp.booklibrary.ui.activity.SearchActivity;
import com.spriteapp.booklibrary.ui.activity.SettingActivity;
import com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity;
import com.spriteapp.booklibrary.ui.activity.TaskActivity;
import com.spriteapp.booklibrary.ui.activity.WebViewActivity;
import com.spriteapp.booklibrary.ui.activity.WithdrawalsActivity;

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
    public static final String USER_ID = "user_id";
    public static final String REWARDTYPE = "reward_type";
    public static final int BACKREFRESH = 9;

    public static final String BOOK_ID = "book_id";


    public static void toWebViewActivity(Context context, String url) {
//        Log.d("toWebViewActivity", "url==="+url);
        toWebViewActivity(context, url, false);
    }


    public static void toWebViewActivity(Context context, String url, boolean isH5Pay) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.LOAD_URL_TAG, url);
        intent.putExtra(WebViewActivity.IS_H5_PAY_TAG, isH5Pay);
        context.startActivity(intent);
    }

    public static void toWebViewActivityBack(Activity context, String url, boolean isH5Pay) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.LOAD_URL_TAG, url);
        intent.putExtra(WebViewActivity.IS_H5_PAY_TAG, isH5Pay);
        context.startActivityForResult(intent, 9);
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

    public static void toReadActivity(Activity context, int book_id, int chapter_id) {
        BookDetailResponse detail = new BookDetailResponse();
        detail.setBook_id(book_id);
        detail.setChapter_id(chapter_id);

        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(ReadActivity.BOOK_DETAIL_TAG, detail);
        context.startActivity(intent);
    }

    public static void toReadActivityPassword(Activity context, int book_id, int chapter_id) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("book_id", book_id + "");
        intent.putExtra("chapter_id", chapter_id + "");
        context.startActivity(intent);
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

    public static void toDownloadChapterActivity(Activity context, int book_id) {
        Intent intent = new Intent(context, DownloadChapterActivity.class);
        intent.putExtra(BOOK_ID, book_id);
        context.startActivity(intent);

    }

    public static void toSquareDetailsActivity(Activity context, int squareid, int IsLookComment) {
        Intent intent = new Intent(context, SquareDetailsActivity.class);
        intent.putExtra(SQUAREID, squareid);
        intent.putExtra(ISLOOKCOMMENT, IsLookComment);
        context.startActivityForResult(intent, TOCREATEDYNAMICACTIVITY);

    }

    public static void toCommentReplyActivity(Context context, int total, int comment_id, int squareid, int user_id) {//总条数
        Intent intent = new Intent(context, CommentReplyActivity.class);
        intent.putExtra(REPLYTITLE, total);
        intent.putExtra(COMMENT_ID, comment_id);
        intent.putExtra(SQUAREID, squareid);
        intent.putExtra(USER_ID, user_id);
        context.startActivity(intent);
    }

    public static void toSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void toRechargeActivity(Activity context) {
        Intent intent = new Intent(context, RechargeActivity.class);
        context.startActivityForResult(intent, BACKREFRESH);
    }

    public static void toTaskActivity(Activity context) {
        Intent intent = new Intent(context, TaskActivity.class);
        context.startActivityForResult(intent, BACKREFRESH);
    }

    public static void toBindPhoneActivity(Context context) {
        Intent intent = new Intent(context, BindPhoneActivity.class);
        context.startActivity(intent);
    }

    public static void toWithdrawalsActivity(Activity context) {
        Intent intent = new Intent(context, WithdrawalsActivity.class);
        context.startActivityForResult(intent, BACKREFRESH);
    }

    public static void toMangerAlipayActivity(Activity context) {
        Intent intent = new Intent(context, MangerAlipayActivity.class);
        context.startActivityForResult(intent, 0);
    }

    public static void toApprenticeListActivity(Activity context) {
        Intent intent = new Intent(context, ApprenticeListActivity.class);
        context.startActivityForResult(intent, 0);
    }

    public static void toProfitDetailsActivity(Activity context, int type) {
        Intent intent = new Intent(context, ProfitDetailsActivity.class);
        intent.putExtra(REWARDTYPE, type);
        context.startActivityForResult(intent, 0);
    }
    public static void toBagActivity(Context context) {
        Intent intent = new Intent(context, BagActivity.class);
        context.startActivity(intent);
    }
}
