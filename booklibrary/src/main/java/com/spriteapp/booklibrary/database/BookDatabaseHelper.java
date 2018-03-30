package com.spriteapp.booklibrary.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.spriteapp.booklibrary.constant.DbConstants;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class BookDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DB_NAME = "HuaXiDb";
    private static BookDatabaseHelper mHelper;
    public static final Object dbLock = new Object();

    public static BookDatabaseHelper getInstance(Context context) {
        if (mHelper == null) {
            synchronized (BookDatabaseHelper.class) {
                if (mHelper == null) {
                    mHelper = new BookDatabaseHelper(context, DB_NAME, null, DATABASE_VERSION);
                }
            }
        }
        return mHelper;
    }

    private BookDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        synchronized (dbLock) {
            createDB(db);
        }
    }

    private void createDB(SQLiteDatabase db) {
        db.execSQL(getShelfSQL(DbConstants.BOOK_TABLE_NAME));
        db.execSQL(getContentSQL());
        db.execSQL(getCatalogSQL());
        db.execSQL(getShelfSQL(DbConstants.RECENT_BOOK_TABLE_NAME));
    }

    private String getShelfSQL(String tableName) {
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists " + tableName + "(");
        builder.append("book_id integer primary key not null, ");
        builder.append(DbConstants.BOOK_NAME).append(" text,");
        builder.append(DbConstants.LAST_UPDATE_CHAPTER_TITLE).append(" text,");
        builder.append(DbConstants.BOOK_IMAGE).append(" text,");
        builder.append(DbConstants.AUTHOR_AVATAR).append(" text,");
        builder.append(DbConstants.AUTHOR_NAME).append(" text,");
        builder.append(DbConstants.LAST_CHAPTER_ID).append(" integer,");
        builder.append(DbConstants.LAST_CHAPTER_INDEX).append(" integer,");
        builder.append(DbConstants.TOTAL_CHAPTER).append(" integer,");
        builder.append(DbConstants.LAST_READ_TIME).append(" integer,");
        builder.append(DbConstants.LAST_UPDATE_BOOK_DATETIME).append(" integer,");
        builder.append(DbConstants.LAST_UPDATE_CHAPTER_DATETIME).append(" integer,");
        builder.append(DbConstants.BOOK_FINISH_FLAG).append(" integer,");
        builder.append(DbConstants.BOOK_IS_VIP).append(" integer,");
        builder.append(DbConstants.BOOK_ADD_SHELF).append(" integer,");
        builder.append(DbConstants.BOOK_IS_RECOMMEND_DATA).append(" integer,");
        builder.append(DbConstants.BOOK_INTRODUCTION).append(" text,");
        builder.append(DbConstants.BOOK_SHARE_URL).append(" text");
        builder.append(")");
        return builder.toString();
    }

    private String getContentSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists " + DbConstants.CONTENT_TABLE_NAME + "(");
        builder.append("id integer primary key not null, ");
        builder.append(DbConstants.BOOK_ID).append(" integer,");
        builder.append(DbConstants.CHAPTER_ID).append(" integer,");
        builder.append(DbConstants.CHAPTER_TITLE).append(" text,");
        builder.append(DbConstants.CHAPTER_PRICE).append(" integer,");
        builder.append(DbConstants.CHAPTER_IS_VIP).append(" integer,");
        builder.append(DbConstants.CHAPTER_CONTENT_BYTE).append(" integer,");
        builder.append(DbConstants.AUTO_SUB).append(" integer,");
        builder.append(DbConstants.CHAPTER_INTRO).append(" text,");
        builder.append(DbConstants.CHAPTER_CONTENT_KEY).append(" text,");
        builder.append(DbConstants.CHAPTER_CONTENT).append(" text,");
        builder.append(DbConstants.CHAPTER_NEED_BUY).append(" integer,");
        builder.append(DbConstants.CHAPTER_PAY_TYPE).append(" integer,");
        builder.append(DbConstants.CHAPTER_ISAES).append(" integer");
        builder.append(")");
        return builder.toString();
    }

    private String getCatalogSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists " + DbConstants.CHAPTER_TABLE_NAME + "(");
        builder.append("id integer primary key not null, ");
        builder.append(DbConstants.BOOK_ID).append(" integer,");
        builder.append(DbConstants.CHAPTER_ID).append(" integer,");
        builder.append(DbConstants.CHAPTER_TITLE).append(" text,");
        builder.append(DbConstants.CHAPTER_CONTENT_BYTE).append(" integer,");
        builder.append(DbConstants.CHAPTER_IS_SUB).append(" integer,");
        builder.append(DbConstants.CHAPTER_PRICE).append(" integer,");
        builder.append(DbConstants.CHAPTER_IS_VIP).append(" integer,");
        builder.append(DbConstants.CHAPTER_ORDER).append(" integer,");
        builder.append(DbConstants.CHAPTER_IS_DOWN_LOAD).append(" integer,");
        builder.append(DbConstants.CHAPTER_READ_STATE).append(" integer");
//        builder.append(DbConstants.BOOK_ADD_SHELF).append(" integer");
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            String sql = "alter table [" + DbConstants.BOOK_TABLE_NAME + "] add [" + DbConstants.LAST_UPDATE_CHAPTER_TITLE + "] nvarchar(300)";
            db.execSQL(sql);
        } else if (oldVersion == 2 && newVersion == 3) {//测试未发布
            addColumns(db);
        } else if ((oldVersion == 2 || oldVersion == 3) && newVersion == 4) {//其它
            addColumns(db);
        } else if (newVersion == 5) {//修复版
            if (oldVersion == 2) {
                addColumns(db);
            } else if (oldVersion == 3) {
                addColumns(db);
            } else if (oldVersion == 4) {
                addColumns(db);
            }
        }
    }

    public void addColumns(SQLiteDatabase db) {
        //章节加 是否下载
        if (!checkColumnExist1(db, DbConstants.CHAPTER_TABLE_NAME, DbConstants.CHAPTER_IS_DOWN_LOAD)) {
            String sql = "alter table [" + DbConstants.CHAPTER_TABLE_NAME + "] add [" + DbConstants.CHAPTER_IS_DOWN_LOAD + "] nvarchar(300)";
            db.execSQL(sql);
        }

        //内容加 是否加密
        if (!checkColumnExist1(db, DbConstants.CONTENT_TABLE_NAME, DbConstants.CHAPTER_ISAES)) {
            String sql2 = "alter table [" + DbConstants.CONTENT_TABLE_NAME + "] add [" + DbConstants.CHAPTER_ISAES + "] nvarchar(300)";
            db.execSQL(sql2);
        }

        //书架加 作者头像
        if (!checkColumnExist1(db, DbConstants.BOOK_TABLE_NAME, DbConstants.AUTHOR_AVATAR)) {
            String sql3 = "alter table [" + DbConstants.BOOK_TABLE_NAME + "] add [" + DbConstants.AUTHOR_AVATAR + "] nvarchar(300)";
            db.execSQL(sql3);
        }
        //书架加作者名字
        if (!checkColumnExist1(db, DbConstants.BOOK_TABLE_NAME, DbConstants.AUTHOR_NAME)) {
            String sql4 = "alter table [" + DbConstants.BOOK_TABLE_NAME + "] add [" + DbConstants.AUTHOR_NAME + "] nvarchar(300)";
            db.execSQL(sql4);
        }
    }

    /**
     * 方法1：检查某表列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExist1(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                    , null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }
}
