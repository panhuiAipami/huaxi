package net.huaxi.reader.thread;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;

import com.android.volley.toolbox.RequestFuture;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.https.ResponseHelper;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.huaxi.reader.activity.BookContentActivity;
import net.huaxi.reader.activity.LoginActivity;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.https.PostRequest;

import hugo.weaving.DebugLog;

/**
 * 切换用户书架迁徙任务
 * taoyingfeng
 * 2016/1/19.
 */
public class SwitchUserMigrateTask extends EasyTask<Activity, Void, Void, Void> {

    private Dialog progressDialog;

    public SwitchUserMigrateTask(@NonNull Activity caller) {
        super(caller);
    }

    public SwitchUserMigrateTask(@NonNull Activity caller, Dialog progressDialog) {
        super(caller);
        this.progressDialog = progressDialog;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog =ViewUtils.showProgressDialog(caller);
    }

    @Override
    public void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (caller instanceof BookContentActivity) {
            ((BookContentActivity) caller).clearAndRefresh();
        } else if (caller instanceof LoginActivity) {
            caller.finish();
        } else if (caller instanceof LoginActivity) {
        }
    }

    @Override
    @DebugLog
    public Void doInBackground(Void... params) {

        //首先当前用户不用登录用户.
        if (!Constants.DEFAULT_USERID.equals(UserHelper.getInstance().getUserId())) {
            /************************* 从默认用户迁移 ************************/
            StringBuffer sb = new StringBuffer();
            int index = 0;
            //迁徙书籍信息.
            LogUtils.debug("SwitchUserMigrateTask============开始获取默认用户数据库===========");
            List<BookTable> bookTables = BookDao.getInstance(Constants.DEFAULT_USERID).findAllBook();
            int result = BookDao.getInstance(Constants.DEFAULT_USERID).deleteAllBook();

            List<BookTable> defaultShelfBook= SharePrefHelper.getDefaultShelfBook();
            for(BookTable bookTable:defaultShelfBook){
                BookDao.getInstance(Constants.DEFAULT_USERID).addBook(bookTable);
            }

            LogUtils.debug("SwitchUserMigrateTask============结束获取默认用户数据库===========");
            LogUtils.debug("删除默认用户书籍 " + result + " 本" + "用户ID=" + Constants.DEFAULT_USERID);
            LogUtils.debug("SwitchUserMigrateTask============开始获取当前用户数据库===========");
            if (bookTables != null && !bookTables.isEmpty()) {
                Iterator<BookTable> iterator = bookTables.iterator();
                while (iterator.hasNext()) {
                    BookTable bookTable = iterator.next();
                    if (bookTable != null && StringUtils.isNotEmpty(bookTable.getBookId())) {
                        sb.append(bookTable.getBookId() + ",");
                        BookDao.getInstance().addBook(bookTable);
                    }
                }
                LogUtils.debug("添加用户书籍信息结束");
            }
            LogUtils.debug("SwitchUserMigrateTask============结束获取当前用户数据库===========");
            //迁移用户的目录;-----------------
            if (StringUtils.isNotEmpty(DataSourceManager
                    .getSingleton().getBookId())) {
                List<ChapterTable> chapterTables = ChapterDao.getInstance(Constants.DEFAULT_USERID)
                        .findChapterListByBid(DataSourceManager
                                .getSingleton().getBookId());
                if (chapterTables != null && !chapterTables.isEmpty()) {
                    Iterator<ChapterTable> iterator = chapterTables.iterator();
                    while (iterator.hasNext()) {
                        ChapterTable chapterTable = iterator.next();
                        if (chapterTable != null && StringUtils.isNotEmpty(chapterTable.getBookId()
                        )) {
                            ChapterDao.getInstance().addChapter(chapterTable);
                        }
                    }
                    LogUtils.debug("添加用户目录信息结束");
                }
            }

            if (sb.length() > 0) {
                String mids = sb.substring(0, sb.length() - 1);
                Map<String, String> map = CommonUtils.getPublicPostArgs();
                map.put("u_action", "b_add");
                map.put("bk_mids", mids);
                try {
                    RequestFuture<JSONObject> future = RequestFuture.newFuture();
                    PostRequest postRequest = new PostRequest(URLConstants.BOOKSHELF_MULTI_OPERATE_BOOK_URL, future, future, map);
                    RequestQueueManager.addRequest(postRequest);
                    JSONObject jsonObject = future.get(10, TimeUnit.SECONDS);
                    LogUtils.debug("SwitchUserMigrateTask==response===" + jsonObject.toString());
                    if (jsonObject != null) {
                        if (ResponseHelper.isSuccess(jsonObject)) {
                            LogUtils.debug("同步数据 === " + ResponseHelper.getErrorId(jsonObject));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogUtils.debug("用户数据切换，阅读数据迁移结束!");
        }
        return null;
    }
}
