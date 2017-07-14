package net.huaxi.reader.util;

import android.content.Context;
import android.os.Environment;

import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;

import java.io.File;

import net.huaxi.reader.common.Constants;

/**
 * Created by ma on 2016/1/19.
 */
public class CacheFilesUtils {
    /*
     *  * 文 件 名: DataCleanManager.java * 描 述:
	 * 主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录
	 */

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {

    }

    public static void cleanImageCache() {
        ImageCacheUtils.getmImageLoderCache().evictAll();
        File imagecache = new File(Constants.XSREADER_IMGCACHE);
        LogUtils.debug("imagecache==" + imagecache.getAbsolutePath());
        boolean b = deleteFilesByDirectory(imagecache);
        if (b) {
            LogUtils.debug("删除成功");
        } else {
            LogUtils.debug("删除失败");
        }
        File imagecache2 = new File(Constants.XSREADER_SPLASH_IMGCACHE);
        LogUtils.debug("imagecache==" + imagecache2.getAbsolutePath());

        b = deleteFilesByDirectory(imagecache2);
        if (b) {
            LogUtils.debug("删除成功");
        } else {
            LogUtils.debug("删除失败");
        }
    }

    public static void cleanVolleyCache(Context context) {
        File cacheFile = new File(context.getCacheDir(), "volley");
        ClearCacheRequest clearCacheRequest = new ClearCacheRequest(new DiskBasedCache(cacheFile)
                , new Runnable() {
            @Override
            public void run() {

            }
        });
        RequestQueueManager.getInstance().add(clearCacheRequest);
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    //    删除方法只删除目录下的所有文件
    public static boolean deleteFilesByDirectory(File file) {

        if (!file.exists())
            return false;

        if (file.isDirectory()) {
            String[] children = file.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                File file1=new File(file, children[i]);
                boolean success = deleteFilesByDirectory(file1);
                if (!success)
                    return false;
            }
        }
        // 目录此时为空，可以删除
        file.delete();
        return true;

    }
}
