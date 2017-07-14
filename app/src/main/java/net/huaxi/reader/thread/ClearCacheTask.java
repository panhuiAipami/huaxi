package net.huaxi.reader.thread;

import android.app.Activity;
import android.app.Dialog;

import com.bumptech.glide.Glide;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.BitmapWriteTool;
import com.tools.commonlibs.tools.FileUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.activity.SettingActivity;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.util.CacheFilesUtils;

import java.io.File;
import java.util.List;

import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;

/**
 * Created by ZMW on 2016/1/20.
 */
public class ClearCacheTask extends EasyTask<Activity, Void, Void, Integer> {

    private Dialog pregressDialog;

    public ClearCacheTask(Activity activity) {
        super(activity);
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        pregressDialog = ViewUtils.showProgressDialog(caller);
    }

    @Override
    public Integer doInBackground(Void... params) {
        long start = System.currentTimeMillis();
        //1,删除其他用户的库
        String[] databaseList = caller.databaseList();
        String userId = UserHelper.getInstance().getUserId();
        String currentdatabase = "readnovel" + userId;
        for (String databaseName : databaseList) {
            if (databaseName.startsWith("readnovel") && !databaseName.equals(currentdatabase) && !databaseName.equals("readnovel-1")) {
                caller.deleteDatabase(databaseName);
            }
        }
        long t1 = System.currentTimeMillis();
        LogUtils.debug("删除其他用户的库==" + (t1 - start));
        //2，清楚当前用户的章节数据
        ChapterDao.getInstance().clearCache();
        long t2 = System.currentTimeMillis();
        LogUtils.debug("清楚当前用户的章节数据==" + (t2 - t1));
        List<BookTable> bookTableList = BookDao.getInstance().findAllBook();
        for (int i = 0; i < bookTableList.size(); i++) {
            BookTable bt = bookTableList.get(i);
            bt.setCatalogUpdateTime(0);
            BookDao.getInstance().updateBook(bt);
        }
        //3,清楚书籍内容文件
        FileUtils.deleteDir(new File(Constants.XSREADER_BOOK));
        FileUtils.deleteDir(new File(Constants.XSREADER_TEMP));
        long t3 = System.currentTimeMillis();
        LogUtils.debug("清楚书籍内容文件==" + (t3 - t2));

        //4,清除图片缓存
        CacheFilesUtils.cleanImageCache();
        Glide.get(caller).clearDiskCache();
        CacheFilesUtils.deleteFilesByDirectory(new File(BitmapWriteTool.ROOTPATH));
        //(清除用户头像缓存)
        long t4 = System.currentTimeMillis();
        LogUtils.debug("清除图片缓存==" + (t4 - t3));

        //5,清除volley缓存
        CacheFilesUtils.cleanVolleyCache(caller);
        long t5 = System.currentTimeMillis();
        LogUtils.debug("清除volley缓存==" + (t5 - t4));
        LogUtils.debug("删除缓存用时==" + (System.currentTimeMillis() - start));
        //6,清除阅读记录
        BookDao.getInstance().clearLastReadDate();

        return null;
    }

    @Override
    public void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        pregressDialog.cancel();
        ((SettingActivity) caller).initData();
    }
}
