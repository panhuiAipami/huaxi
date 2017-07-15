package net.huaxi.reader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterContentTable;
import net.huaxi.reader.db.model.UserTable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.db.model.ChapterTable;

public class OrmDataBaseHelper extends OrmLiteSqliteOpenHelper {
//	private static final String DATABASE_NAME="readnovel.db";

    private static final int DATABASE_VERSION = 2;
    public static final String TAG = "DatabaseHelper";

    //	public OrmDataBaseHelper(Context context) {
    //		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    //	}
    public OrmDataBaseHelper(Context context, String dataBaseName) {
        super(context, dataBaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            //创建所需要的表
            Log.i(TAG, "oncreate...............");
            TableUtils.createTable(connectionSource, UserTable.class);
            TableUtils.createTable(connectionSource, BookTable.class);
            TableUtils.createTable(connectionSource, ChapterTable.class);
            TableUtils.createTable(connectionSource, ChapterContentTable.class);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //更新表
        Log.i(TAG, "onUpload..............." + oldVersion);
        if (oldVersion < 2) {
            DatabaseUtil.upgradeTable(sqliteDatabase, connectionSource, BookTable.class, DatabaseUtil.OPERATION_TYPE.ADD);
        }
//        if (oldVersion < 3) {
//            //处理2到3的逻辑
//        }
    }

    private static OrmDataBaseHelper instance;

    public static synchronized OrmDataBaseHelper getDataBaseHelper(Context context, String userId) {
        if (userId == null || context == null) {
            return null;
        }
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (OrmDataBaseHelper.class) {
                if (instance == null)
                    instance = new OrmDataBaseHelper(context, "readnovel" + userId);
            }
        } else {
            synchronized (OrmDataBaseHelper.class) {

                if (!instance.getDatabaseName().equals("readnovel" + userId)) {
                    instance = new OrmDataBaseHelper(context, "readnovel" + userId);
                }
            }
        }
        return instance;
    }

    public static synchronized OrmDataBaseHelper getDataBaseHelper(Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        String userId = UserHelper.getInstance().getUserId();
//		LogUtils.debug("要获取的数据库userid==="+userId);
        if (instance == null) {
            synchronized (OrmDataBaseHelper.class) {
                if (instance == null)
                    instance = new OrmDataBaseHelper(context, "readnovel" + userId);
            }
        } else {
            synchronized (OrmDataBaseHelper.class) {

                if (!instance.getDatabaseName().equals("readnovel" + userId)) {
                    instance = new OrmDataBaseHelper(context, "readnovel" + userId);
                }
            }
        }
        return instance;
    }

    private Map<String, Dao> daos = new HashMap<String, Dao>();

    public synchronized Dao getDao(Class clazz) {
        Dao dao = null;
        String className = clazz.getSimpleName();
        String key = className + instance.getDatabaseName();

        if (daos.containsKey(key)) {
            dao = daos.get(key);
        }
        if (dao == null) {
            try {
                dao = super.getDao(clazz);
            } catch (SQLException e) {
                Log.e(TAG, "获取" + className + "时异常，异常信息：" + e.getMessage());
            }
            daos.put(key, dao);
        }
//		LogUtils.debug("SwitchUserMigrateTask==获取dao的key=="+key);
        return dao;
    }
}
