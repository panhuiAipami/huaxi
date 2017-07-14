package net.huaxi.reader.https;

import android.app.Activity;

import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.util.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.huaxi.reader.appinterface.onCatalogLoadFinished;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.EncodeUtils;

import hugo.weaving.DebugLog;

/**
 * @Description: [目录获取数据的帮助类]
 * @Author: [Saud]
 * @CreateDate: [16/1/7 10:52]
 * @UpDate: [16/1/7 10:52]
 * @Version: [v1.0]
 */
public class BookCatalogThreadLoader extends EasyTask<Activity, Void, Void, Void> {

    private String bookId;
    private onCatalogLoadFinished catalogueDataListener;
    private List<ChapterTable> chapterTables;
    private long netTimeStamp=0;
    private long timeStamp = 0;


    public BookCatalogThreadLoader(Activity activity, String bookid, onCatalogLoadFinished catalogueDataListener) {
        super(activity);
        this.bookId = bookid;
        this.catalogueDataListener = catalogueDataListener;
        chapterTables = new ArrayList<ChapterTable>();
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public void onPostExecute(Void aVoid) {
    }

    @Override
    public Void doInBackground(Void... params) {
        getData();
        return null;
    }

    /**
     * 通过查询返回异步返回数据
     */
    private void getData() {
        timeStamp = getBookTimeStamp(bookId);
        if (chapterTables != null && !chapterTables.isEmpty()) {
            result(XSErrorEnum.TEMP_SUCCESS.getCode());
        }
            syncNetData(bookId);//网络数据
    }

    /**
     * 去数据库查询目录
     *
     * @param bookid
     * @return
     */
    private List<ChapterTable> getCatalogFromDB(String bookid) {
        List<ChapterTable> chapters = ChapterDao.getInstance().findChapterListByBid(bookid);
        return chapters;
    }


    /**
     * 获取数据库时间戳
     *
     * @param bookid
     * @return
     */
    private long getBookTimeStamp(String bookid) {
        long timeStemp = 0;
        BookTable bookTable = BookDao.getInstance().findBook(bookid);
        if (bookTable != null) {
            timeStemp = bookTable.getCatalogUpdateTime();
        }
        chapterTables = getCatalogFromDB(bookId);
        if (chapterTables == null || chapterTables.size() == 0) {
            timeStemp = 0;
        }
        return timeStemp;
    }


    /**
     * 网络获取数据
     */
    @DebugLog
    private void syncNetData(final String bookid) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            result(XSErrorEnum.NETWORK_UNAVAILABLE.getCode());
            return;
        }
        try {
            String url;
            if (UserHelper.getInstance().isLogin()) {
                url = URLConstants.GET_BOOKCONTENT_DOWNLOAD;
            } else {
                url = URLConstants.GET_BOOKCONTENT_CATALOGUE;
            }
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            GetRequest request = new GetRequest(String.format(url, EncodeUtils.encodeString_UTF8(bookid), timeStamp) + CommonUtils.getPublicGetArgs()
            , future, future);
            RequestQueueManager.addRequest(request);
            JSONObject response = future.get(30, TimeUnit.SECONDS);
//            LogUtils.info("网络读取目录耗时 " + (System.currentTimeMillis() - starttime) + " ms");
            int errorId = ResponseHelper.getErrorId(response);
            if (errorId == XSErrorEnum.SUCCESS.getCode()) {
                List chapterTables = null;
                try {
                    Type type = new TypeToken<ArrayList<ChapterTable>>() {
                    }.getType();
                    JSONObject jsonObject = ResponseHelper.getVdata(response);
                    chapterTables = new Gson().fromJson(jsonObject.getJSONArray(XSKEY.BOOK_CATALOG.CONTENTS).toString(), type);
                    try {

                    BookCatalogThreadLoader.this.netTimeStamp = jsonObject.getLong(XSKEY.BOOK_CATALOG.LAST_UPDATE_TIME);
                    }catch (Exception e){
                        System.out.println(e.toString());
                    }
                    if (chapterTables != null) {
                        BookCatalogThreadLoader.this.chapterTables = chapterTables;
                    }
                } catch (JSONException e) {
                    ReportUtils.reportError(new Throwable(e.toString()));
                    e.printStackTrace();
                }

            } else {
//                ReportUtils.reportError(new Throwable(ResponseHelper.getErrorDesc(response)));
                LogUtils.debug(response.toString());
            }
            result(errorId);
        } catch (Exception e) {
            ReportUtils.reportError(e);
            result(XSErrorEnum.CHAPTER_DOWNLOAD_FAILED.getCode());
        }
    }

    /**
     * 更新数据库
     */
    @DebugLog
    private void updateDB() {
        //更新目录
        ChapterDao.getInstance().addChapterList(chapterTables, bookId);
        //更新book的时间戳
        BookTable book = BookDao.getInstance().findBook(bookId);
        if (book != null && netTimeStamp > 0) {
            book.setCatalogUpdateTime(netTimeStamp);
            book.setHasNewChapter(0);
            BookDao.getInstance().updateBook(book);
            caller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_BOOKSHELF,EventBusUtil.EVENTTYPE_UPDATE_SHELF));
                }
            });
        }
    }

    /**
     * 网络返回结果
     */
    private void result(int resultCode) {

        if (resultCode == XSErrorEnum.SUCCESS.getCode()) {
            updateDB();//返回了数据，更新数据库
        }
        if (catalogueDataListener != null) {
            catalogueDataListener.onFinished(chapterTables, resultCode);
        }
    }
}
