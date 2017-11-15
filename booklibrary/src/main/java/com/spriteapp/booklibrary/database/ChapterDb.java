package com.spriteapp.booklibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spriteapp.booklibrary.constant.DbConstants;
import com.spriteapp.booklibrary.enumeration.ChapterEnum;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 章节db
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class ChapterDb {

    private BookDatabaseHelper mHelper;
    private SQLiteDatabase mDb;

    public ChapterDb(Context context) {
        mHelper = BookDatabaseHelper.getInstance(context);
    }

    public void openDB() {
        if (mHelper == null) {
            return;
        }
        mDb = mHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (mDb == null) {
            return;
        }
        mDb.close();
    }

    public void insert(List<BookChapterResponse> modelList, int bookId) {
        if (CollectionUtil.isEmpty(modelList)) {
            return;
        }
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            mDb.beginTransaction();
            ContentValues values = new ContentValues();
            for (BookChapterResponse model : modelList) {
                int chapter_id = model.getChapter_id();
                if (isChapterExists(bookId, chapter_id)) {
                    continue;
                }
                values.clear();
                values.put(DbConstants.BOOK_ID, bookId);
                values.put(DbConstants.CHAPTER_ID, chapter_id);
                values.put(DbConstants.CHAPTER_TITLE, model.getChapter_title());
                values.put(DbConstants.CHAPTER_ORDER, model.getChapter_order());
                values.put(DbConstants.CHAPTER_CONTENT_BYTE, model.getChapter_content_byte());
                values.put(DbConstants.CHAPTER_IS_SUB, model.getChapter_is_sub());
                values.put(DbConstants.CHAPTER_PRICE, model.getChapter_price());
                values.put(DbConstants.CHAPTER_IS_VIP, model.getChapter_is_vip());
                values.put(DbConstants.CHAPTER_READ_STATE, model.getChapterReadState());
                mDb.insert(DbConstants.CHAPTER_TABLE_NAME, null, values);
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            closeDB();
        }
    }

    private boolean isChapterExists(int bookId, int chapterId) {
        boolean isExists = false;
        Cursor cursor = mDb.query(DbConstants.CHAPTER_TABLE_NAME,
                new String[]{"book_id", "chapter_id"}, "book_id = ? and chapter_id = ?",
                new String[]{String.valueOf(bookId), String.valueOf(chapterId)}, null, null, null);
        if (cursor.getColumnCount() != 0) {
            while (cursor.moveToNext()) {
                isExists = true;
            }
        }
        cursor.close();
        return isExists;
    }

    public List<BookChapterResponse> queryCatalog(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            List<BookChapterResponse> modelList = new ArrayList<>();
            openDB();
            String selection = "book_id = ?";
            String[] selectionArgs = {String.valueOf(bookId)};
            Cursor cursor = mDb.query(DbConstants.CHAPTER_TABLE_NAME, null, selection, selectionArgs, null, null, null);
            if (cursor.getColumnCount() != 0) {
                while (cursor.moveToNext()) {
                    BookChapterResponse model = new BookChapterResponse();
                    model.setBookId(cursor.getInt(cursor.getColumnIndex(DbConstants.BOOK_ID)));
                    model.setChapter_id(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_ID)));
                    model.setChapter_title(cursor.getString(cursor.getColumnIndex(DbConstants.CHAPTER_TITLE)));
//                    model.setCacheTime(cursor.getInt(cursor.getColumnIndex(DbConstant.CACHE_TIME)));
                    model.setChapter_is_vip(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_IS_VIP)));
                    model.setChapterReadState(cursor.getInt(cursor.getColumnIndex(DbConstants.CHAPTER_READ_STATE)));
                    modelList.add(model);
                }
            }
            cursor.close();
            closeDB();
            return modelList;
        }
    }

    public void updateReadState(int bookId, int chapterId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAPTER_READ_STATE, ChapterEnum.HAS_READ.getCode());
            mDb.update(DbConstants.CHAPTER_TABLE_NAME, values, "book_id = ? and chapter_id = ?",
                    new String[]{String.valueOf(bookId), String.valueOf(chapterId)});
            closeDB();
        }
    }

    public void deleteChapter(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            mDb.delete(DbConstants.CHAPTER_TABLE_NAME, "book_id = ?", new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public void deleteDb() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            mDb.delete(DbConstants.CHAPTER_TABLE_NAME, null, null);
            closeDB();
        }
    }

}
