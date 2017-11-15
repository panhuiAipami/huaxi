package com.spriteapp.booklibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spriteapp.booklibrary.constant.DbConstants;
import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架db
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class BookDb {

    private static final String TAG = "ShelfDB";
    private BookDatabaseHelper mHelper;
    private SQLiteDatabase db;

    public BookDb(Context context) {
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

    /**
     * 存储书架内容
     *
     * @param modelList 书列表
     * @param bookEnum  是否是加入书架
     * @param tyEnum    是否是推荐数据
     */
    public void insert(List<BookDetailResponse> modelList, BookEnum bookEnum, BookEnum tyEnum) {
        if (CollectionUtil.isEmpty(modelList)) {
            return;
        }
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            for (BookDetailResponse model : modelList) {
                if (isBookExists(model.getBook_id())) {
                    continue;
                }
                values.clear();
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
                if (tyEnum != null) {
                    values.put(DbConstants.BOOK_IS_RECOMMEND_DATA, tyEnum.getValue());
                }
                if (bookEnum != null) {
                    values.put(DbConstants.BOOK_ADD_SHELF, bookEnum.getValue());
                }
                db.insert(DbConstants.BOOK_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            closeDB();
        }
    }

    public void insert(BookDetailResponse model, BookEnum bookEnum) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.beginTransaction();
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
            if (bookEnum != null) {
                values.put(DbConstants.BOOK_ADD_SHELF, bookEnum.getValue());
            }
            db.insert(DbConstants.BOOK_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            closeDB();
        }
    }

    public void updateAddShelfTag(int bookId, BookEnum bookEnum) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.LAST_READ_TIME, System.currentTimeMillis() / 1000);
            values.put(DbConstants.BOOK_ADD_SHELF, bookEnum.getValue());
            db.update(DbConstants.BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public void update(BookDetailResponse model, int isShelfExist) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
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
            values.put(DbConstants.BOOK_FINISH_FLAG, model.getBook_finish_flag());
            values.put(DbConstants.BOOK_IS_VIP, model.getBook_is_vip());
            values.put(DbConstants.BOOK_ADD_SHELF, isShelfExist);
            db.update(DbConstants.BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(model.getBook_id())});
            closeDB();
        }
    }

    public void updateReadTime(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.LAST_READ_TIME, System.currentTimeMillis() / 1000);
            db.update(DbConstants.BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public void updateRecommendTag(int bookId, int tag) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.BOOK_IS_RECOMMEND_DATA, tag);
            db.update(DbConstants.BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public void updateReadProgress(BookDetailResponse model) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.LAST_CHAPTER_ID, model.getChapter_id());
            values.put(DbConstants.LAST_CHAPTER_INDEX, model.getLast_chapter_index());
            values.put(DbConstants.TOTAL_CHAPTER, model.getBook_chapter_total());
            db.update(DbConstants.BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(model.getBook_id())});
            closeDB();
        }
    }

    /**
     * 更新本地章节最后更新时间
     */
    public void updateChapterTime(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            ContentValues values = new ContentValues();
            values.put(DbConstants.LAST_UPDATE_CHAPTER_DATETIME, System.currentTimeMillis() / 1000);
            db.update(DbConstants.BOOK_TABLE_NAME, values, "book_id = ?",
                    new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public List<BookDetailResponse> queryBookData() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            List<BookDetailResponse> modelList = new ArrayList<>();
            Cursor cursor = db.query(DbConstants.BOOK_TABLE_NAME, null, "book_add_shelf = ?",
                    new String[]{String.valueOf(BookEnum.ADD_SHELF.getValue())}, null, null, "last_read_time desc");
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

    private boolean isBookExists(int bookId) {
        boolean isExists = false;
        Cursor cursor = db.query(DbConstants.BOOK_TABLE_NAME,
                new String[]{"book_id"}, "book_id = ?", new String[]{String.valueOf(bookId)}, null, null, null);
        if (cursor.getColumnCount() != 0) {
            while (cursor.moveToNext()) {
                isExists = true;
            }
        }
        cursor.close();
        return isExists;
    }

    public BookDetailResponse queryBook(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            BookDetailResponse model = null;
            Cursor cursor = db.query(DbConstants.BOOK_TABLE_NAME,
                    null, "book_id = ?", new String[]{String.valueOf(bookId)}, null, null, null);
            if (cursor.getColumnCount() != 0) {
                while (cursor.moveToNext()) {
                    model = new BookDetailResponse();
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
                }
            }
            cursor.close();
            closeDB();
            return model;
        }
    }

    public void deleteBook(int bookId) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.delete(DbConstants.BOOK_TABLE_NAME, "book_id = ?", new String[]{String.valueOf(bookId)});
            closeDB();
        }
    }

    public void deleteBook(List<BookDetailResponse> dataList) {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            for (int i = 0; i < dataList.size(); i++) {
                BookDetailResponse bookDetail = dataList.get(i);
                db.delete(DbConstants.BOOK_TABLE_NAME, "book_id = ?",
                        new String[]{String.valueOf(bookDetail.getBook_id())});
            }
            closeDB();
        }
    }

    public void deleteRecommendBook() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.delete(DbConstants.BOOK_TABLE_NAME, "book_is_recommend_data = ?",
                    new String[]{String.valueOf(BookEnum.RECOMMEND_BOOK.getValue())});
            closeDB();
        }
    }

    /**
     * 删除之前从未读过的推荐书籍
     * 为了实现未登录前读过书的同步
     */
    public void deleteRecommendNotReadBook() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.delete(DbConstants.BOOK_TABLE_NAME, "last_chapter_index = ?",
                    new String[]{"0"});
            closeDB();
        }
    }

    public void deleteDb() {
        synchronized (BookDatabaseHelper.dbLock) {
            openDB();
            db.delete(DbConstants.BOOK_TABLE_NAME, null, null);
            closeDB();
        }
    }
}
