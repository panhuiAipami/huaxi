package com.spriteapp.booklibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spriteapp.booklibrary.constant.DbConstants;
import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架db
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class RecentBookDb {

    private static final String TAG = "RecentBookDb";
    private static final int MAX_RECENT_BOOK_COUNT = 20;
    private BookDatabaseHelper mHelper;
    private SQLiteDatabase db;

    public RecentBookDb(Context context) {
        mHelper = BookDatabaseHelper.getInstance(context);
    }

    public void openDB() {
        if (mHelper == null) {
            return;
        }
        db = mHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (db == null) {
            return;
        }
        db.close();
    }

    public void insert(BookDetailResponse model) {
        if (model == null) {
            return;
        }
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            if (isBookExists(model.getBook_id())) {
                closeDB();
                return;
            }
            db.beginTransaction();
            deleteLastBook();
            ContentValues values = new ContentValues();
            values.put(DbConstants.BOOK_ID, model.getBook_id());
            values.put(DbConstants.BOOK_NAME, model.getBook_name());
            values.put(DbConstants.BOOK_IMAGE, model.getBook_image());
            values.put(DbConstants.BOOK_INTRODUCTION, model.getBook_intro());
            values.put(DbConstants.BOOK_SHARE_URL, model.getBook_share_url());
            values.put(DbConstants.LAST_CHAPTER_ID, model.getChapter_id());
            values.put(DbConstants.LAST_CHAPTER_INDEX, model.getLast_chapter_index());
            values.put(DbConstants.TOTAL_CHAPTER, model.getBook_chapter_total());
            values.put(DbConstants.LAST_READ_TIME, System.currentTimeMillis() / 1000);
            values.put(DbConstants.LAST_UPDATE_BOOK_DATETIME, System.currentTimeMillis() / 1000);
            values.put(DbConstants.LAST_UPDATE_CHAPTER_DATETIME, System.currentTimeMillis() / 1000);
            values.put(DbConstants.BOOK_FINISH_FLAG, model.getBook_finish_flag());
            values.put(DbConstants.BOOK_IS_VIP, model.getBook_is_vip());
            values.put(DbConstants.BOOK_IS_RECOMMEND_DATA, BookEnum.MY_BOOK.getValue());
            db.insert(DbConstants.RECENT_BOOK_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            closeDB();
        }
    }

    public void updateReadTime(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.LAST_READ_TIME, System.currentTimeMillis() / 1000);
            db.update(DbConstants.RECENT_BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public List<BookDetailResponse> queryBookData() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            List<BookDetailResponse> modelList = new ArrayList<>();
            Cursor cursor = db.query(DbConstants.RECENT_BOOK_TABLE_NAME, null, null, null,
                    null, null, "last_read_time desc");
            if (cursor.getColumnCount() != 0) {
                while (cursor.moveToNext()) {
                    BookDetailResponse model = new BookDetailResponse();
                    model.setBook_id(cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_ID)));
                    model.setBook_name(cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_NAME)));
                    model.setBook_image(cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_IMAGE)));
                    model.setBook_intro(cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_INTRODUCTION)));
                    model.setBook_share_url(cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_SHARE_URL)));
                    model.setChapter_id(cursor.getInt(cursor.getColumnIndex(DbConstants.LAST_CHAPTER_ID)));
                    model.setBook_chapter_total(cursor.getInt(cursor.getColumnIndex(DbConstants.TOTAL_CHAPTER)));
                    model.setLastReadTime(cursor.getInt(cursor.getColumnIndex(DbConstants.LAST_READ_TIME)));
                    model.setLast_chapter_index(cursor.getInt(cursor.getColumnIndex(DbConstants.LAST_CHAPTER_INDEX)));
                    model.setLast_update_book_datetime(cursor.getInt(cursor.getColumnIndex(DbConstants.LAST_UPDATE_BOOK_DATETIME)));
                    model.setLast_update_chapter_datetime(cursor.getInt(cursor.getColumnIndex(DbConstants.LAST_UPDATE_CHAPTER_DATETIME)));
                    model.setBook_finish_flag(cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_FINISH_FLAG)));
                    model.setBook_add_shelf(cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_ADD_SHELF)));
                    model.setIs_recommend_book(cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_IS_RECOMMEND_DATA)));
                    modelList.add(model);
                }
            }
            cursor.close();
            closeDB();
            return modelList;
        }
    }

    private void deleteLastBook() {
        Cursor cursor = db.query(DbConstants.RECENT_BOOK_TABLE_NAME, null, null, null,
                null, null, "last_read_time desc");
        if (cursor == null) {
            return;
        }
        int count = cursor.getCount();
        if (count < MAX_RECENT_BOOK_COUNT) {
            return;
        }
        if (!cursor.moveToLast()) {
            return;
        }
        int deleteBookId = cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_ID));
        db.delete(DbConstants.RECENT_BOOK_TABLE_NAME, "book_id = ?", new String[]{String.valueOf(deleteBookId)});
        cursor.close();
    }

    private boolean isBookExists(int bookId) {
        boolean isExists = false;
        Cursor cursor = db.query(DbConstants.RECENT_BOOK_TABLE_NAME,
                new String[]{"book_id"}, "book_id = ?", new String[]{String.valueOf(bookId)}, null, null, null);
        if (cursor.getColumnCount() != 0) {
            while (cursor.moveToNext()) {
                isExists = true;
            }
        }
        cursor.close();
        return isExists;
    }

}
