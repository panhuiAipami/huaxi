package com.spriteapp.booklibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spriteapp.booklibrary.constant.DbConstants;
import com.spriteapp.booklibrary.model.response.SubscriberContent;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class ContentDb {

    private BookDatabaseHelper mHelper;
    private SQLiteDatabase db;

    public ContentDb(Context context) {
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

    public void insert(SubscriberContent model) {
        if (model == null) {
            return;
        }
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DbConstants.BOOK_ID, model.getBook_id());
            values.put(DbConstants.CHAPTER_ID, model.getChapter_id());
            values.put(DbConstants.CHAPTER_TITLE, model.getChapter_title());
            values.put(DbConstants.CHAPTER_PRICE, model.getChapter_price());
            values.put(DbConstants.CHAPTER_IS_VIP, model.getChapter_is_vip());
            values.put(DbConstants.CHAPTER_CONTENT_BYTE, model.getChapter_content_byte());
            values.put(DbConstants.AUTO_SUB, model.getAuto_sub());
            values.put(DbConstants.CHAPTER_INTRO, model.getChapter_intro());
            values.put(DbConstants.CHAPTER_CONTENT_KEY, model.getChapter_content_key());
            values.put(DbConstants.CHAPTER_CONTENT, model.chapter_content);
            values.put(DbConstants.CHAPTER_NEED_BUY, model.getChapter_need_buy());
            values.put(DbConstants.CHAPTER_PAY_TYPE, model.getChapter_pay_type());
            values.put(DbConstants.CHAPTER_ISAES, model.getIsAES());
            db.insert(DbConstants.CONTENT_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            closeDB();
        }

    }

    public void deleteContent(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            if (db == null) {
                return;
            }
            db.delete(DbConstants.CONTENT_TABLE_NAME, "book_id = ?", new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public void update(int bookId, int chapterId, SubscriberContent model) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            if (db == null) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAPTER_CONTENT_KEY, model.getChapter_content_key());
            values.put(DbConstants.CHAPTER_CONTENT, model.chapter_content);
            values.put(DbConstants.CHAPTER_NEED_BUY, model.getChapter_need_buy());
            values.put(DbConstants.CHAPTER_PAY_TYPE, model.getChapter_pay_type());
            values.put(DbConstants.CHAPTER_ISAES, model.getIsAES());
            db.update(DbConstants.CONTENT_TABLE_NAME, values, "book_id = ? and chapter_id = ?",
                    new String[]{String.valueOf(bookId), String.valueOf(chapterId)});
            closeDB();
        }
    }

    /**
     * 更新章节为提示用户购买
     * 避免问题：在余额充足的情况下如果没有改变该code类型，
     * 则依然是弹出余额不足dialog，而不是章节购买dialog
     */
    public void updatePayType(int bookId, int chapterId, int payType) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            if (db == null) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAPTER_PAY_TYPE, payType);
            db.update(DbConstants.CONTENT_TABLE_NAME, values, "book_id = ? and chapter_id = ?",
                    new String[]{String.valueOf(bookId), String.valueOf(chapterId)});
            closeDB();
        }
    }

    public SubscriberContent queryContent(int bookId, int chapterId) {
        synchronized (BookDatabaseHelper.dbLock) {
            SubscriberContent model = null;
            openDB();
            String selection = "book_id = ? and chapter_id = ?";
            String[] selectionArgs = {String.valueOf(bookId), String.valueOf(chapterId)};
            Cursor cursor = db.query(DbConstants.CONTENT_TABLE_NAME, null, selection, selectionArgs, null, null, null);
            if (cursor.getColumnCount() != 0) {
                while (cursor.moveToNext()) {
                    if (model == null) {
                        model = new SubscriberContent();
                    }
                    model.setBook_id(cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_ID)));
                    model.setChapter_id(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_ID)));
                    model.setChapter_title(cursor.getString(cursor.getColumnIndex(DbConstants.CHAPTER_TITLE)));
                    model.setChapter_price(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_PRICE)));
                    model.setChapter_is_vip(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_IS_VIP)));
                    model.setChapter_content_byte(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_CONTENT_BYTE)));
                    model.setAuto_sub(cursor.getInt(cursor.getColumnIndex(DbConstants.AUTO_SUB)));
                    model.setChapter_intro(cursor.getString(cursor.getColumnIndex(DbConstants.CHAPTER_INTRO)));
                    model.setChapter_content_key(cursor.getString(cursor.getColumnIndex(DbConstants.CHAPTER_CONTENT_KEY)));
                    model.setChapter_content(cursor.getString(cursor.getColumnIndex(DbConstants.CHAPTER_CONTENT)));
                    model.setChapter_need_buy(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_NEED_BUY)));
                    model.setChapter_pay_type(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_PAY_TYPE)));
                    model.setIsAES(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_ISAES)));
                    model.SQLiteId = cursor.getInt(cursor.getColumnIndex("id"));
                }
            }
            cursor.close();
            closeDB();
            return model;
        }
    }

    public void deleteDb() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.delete(DbConstants.CONTENT_TABLE_NAME, null, null);
            closeDB();
        }
    }

}
