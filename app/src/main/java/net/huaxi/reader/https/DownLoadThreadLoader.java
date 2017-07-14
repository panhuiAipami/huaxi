package net.huaxi.reader.https;

import android.app.Activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.EncodeUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.appinterface.onCatalogLoadFinished;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.XSErrorEnum;

/**
 * @Description: [目录获取数据的帮助类]
 * @Author: [Saud]
 * @CreateDate: [16/1/7 10:52]
 * @UpDate: [16/1/7 10:52]
 * @Version: [v1.0]
 */
public class DownLoadThreadLoader extends Thread {

    private Activity activity;
    private String bookId;
    private onCatalogLoadFinished catalogueDataListener;
    private List<ChapterTable> chapterTables;
    private long netTimeStamp;


    public DownLoadThreadLoader(Activity activity, String bookid, onCatalogLoadFinished catalogueDataListener) {
        this.activity = activity;
        this.bookId = bookid;
        this.catalogueDataListener = catalogueDataListener;
        chapterTables = new ArrayList<ChapterTable>();
    }

    @Override
    public void run() {
        long timeStamp = 0;
        chapterTables = ChapterDao.getInstance().findChapterListByBid(bookId);
        if (chapterTables != null && chapterTables.size() > 0) {
            result(XSErrorEnum.SUCCESS.getCode());
            // FIXME: 2016/3/31 获取章节最后更新时间没有用到
//            timeStamp = getBookTimeStamp();
        }
        if (NetUtils.checkNet() == NetType.TYPE_NONE) {
            result(XSErrorEnum.NETWORK_UNAVAILABLE.getCode());
        } else {
            syncNetData(timeStamp);
        }
    }


    /**
     * 获取数据库时间戳
     *
     * @return
     */
    private long getBookTimeStamp() {
        long timeStemp = 0;
        BookTable bookTable = BookDao.getInstance().findBook(bookId);
        if (bookTable != null) {
            timeStemp = bookTable.getCatalogUpdateTime();
        }
        return timeStemp;
    }


    /**
     * 网络获取数据
     */
    private void syncNetData(long timeStamp) {
        String url;

        if (StringUtils.isBlank(SharePrefHelper.getCookie()) || "-1".equals(SharePrefHelper.getCookie())) {
            url = URLConstants.GET_BOOKCONTENT_CATALOGUE;
        } else {
            url = URLConstants.GET_BOOKCONTENT_DOWNLOAD;
        }

        GetRequest request = new GetRequest(String.format(url, EncodeUtils.encodeString_UTF8(bookId), timeStamp) + CommonUtils.getPublicGetArgs()
                , new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSErrorEnum.SUCCESS.getCode()) {

                    Type type = new TypeToken<ArrayList<ChapterTable>>() {
                    }.getType();
                    JSONObject jsonObject = ResponseHelper.getVdata(response);
                    List chapterTables = new Gson().fromJson(jsonObject.optJSONArray(XSKEY.BOOK_CATALOG.CONTENTS).toString(), type);
                    netTimeStamp = jsonObject.optLong(XSKEY.BOOK_CATALOG.LAST_UPDATE_TIME);
                    if (chapterTables != null) {
                        DownLoadThreadLoader.this.chapterTables = chapterTables;
                        updateDB();//返回了数据，更新数据库
                    }
                } else {
                    //ReportUtils.reportError(new Throwable(ResponseHelper.getErrorDesc(response)));
                    LogUtils.debug(ResponseHelper.getErrorDesc(response));
                }
                result(errorid);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                result(XSErrorEnum.NETWORK_UNAVAILABLE.getCode());
                ReportUtils.reportError(new Throwable(error.toString()));
            }
        });
        RequestQueueManager.addRequest(request);
//        RequestFuture<JSONObject> future = RequestFuture.newFuture();
//        GetRequest request = new GetRequest(String.format(url, EncodeUtils.encodeString_UTF8(bookId), timeStamp) + CommonUtils
//                .getPublicGetArgs(), future, null);
//        RequestQueueManager.addRequest(request);
//        try {
//            JSONObject response = future.get(5, TimeUnit.SECONDS);
//
//            int errorid = ResponseHelper.getErrorId(response);
//            if (errorid == XSErrorEnum.SUCCESS.getCode()) {
//                List chapterTables = null;
//                try {
//                    Type type = new TypeToken<ArrayList<ChapterTable>>() {
//                    }.getType();
//                    JSONObject jsonObject = ResponseHelper.getVdata(response);
//                    chapterTables = new Gson().fromJson(jsonObject.getJSONArray(XSKEY.BOOK_CATALOG.CONTENTS).toString(), type);
//                    netTimeStamp = jsonObject.optLong(XSKEY.BOOK_CATALOG.LAST_UPDATE_TIME);
//                    if (chapterTables != null) {
//                        DownLoadThreadLoader.this.chapterTables = chapterTables;
//                        updateDB();//返回了数据，更新数据库
//                    }
//
//                } catch (JSONException e) {
//                    ReportUtils.reportError(new Throwable(e.toString()));
//                    e.printStackTrace();
//                }
//
//            } else {
//                ReportUtils.reportError(new Throwable(ResponseHelper.getErrorDesc(response)));
////                LogUtils.debug(ResponseHelper.getErrorDesc(response));
//            }
//            result(errorid);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }

    }


    /**
     * 网络返回结果
     */
    private void result(final int resultCode) {
        if (catalogueDataListener != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    catalogueDataListener.onFinished(chapterTables, resultCode);
                }
            });
        }
    }

    /**
     * 更新数据库
     */
    private void updateDB() {
        //更新目录
        ChapterDao.getInstance().addChapterList(chapterTables, bookId);
        //更新book的时间戳
        BookTable book = BookDao.getInstance().findBook(bookId);
        if (book != null) {
            book.setCatalogUpdateTime(netTimeStamp);
            book.setHasNewChapter(0);
            BookDao.getInstance().updateBook(book);
        }
    }
}
