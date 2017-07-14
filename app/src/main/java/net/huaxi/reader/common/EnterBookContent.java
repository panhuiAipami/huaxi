package net.huaxi.reader.common;

import android.app.Activity;
import android.content.Intent;

import net.huaxi.reader.activity.BookContentActivity;

/**
 * Created by taoyingfeng
 * 2015/12/16.
 */
public class EnterBookContent {
    public static final String BOOK_ID = "book_id";
    public static final String CHAPTER_ID = "chapter_id";

    /**
     * 打开阅读界面
     *
     * @param activity
     */
    public static void openBookContent(Activity activity, String bookId) {
        Intent it = new Intent(activity, BookContentActivity.class);
        it.putExtra(BOOK_ID, bookId);
        activity.startActivity(it);
    }

    /**
     * 打开阅读界面，跳到指定章节.
     *
     * @param activity
     * @param bookId
     * @param chapterId
     */
    public static void openBookContent(Activity activity, String bookId, String chapterId) {
        Intent it = new Intent(activity, BookContentActivity.class);
        it.putExtra(BOOK_ID, bookId);
        it.putExtra(CHAPTER_ID, chapterId);
        activity.startActivity(it);
    }

}
