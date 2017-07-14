package net.huaxi.reader.thread;

import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.fragment.FmBookShelf;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.UserHelper;

/**
 * 同步服务器书籍列表
 */
public class BookShelfSyncInitDataTask extends AsyncTask<Void, Void, Void> {
    private static final boolean DEBUG = false;
    public boolean canshow = true;
    private static volatile boolean isRunning = false; //标识任务是否正在进行
    long start;
    private FmBookShelf bookShelf;

    public BookShelfSyncInitDataTask(FmBookShelf bookShelf) {
        this.bookShelf = bookShelf;
    }

    public boolean isCanshow() {
        return canshow;
    }

    public void setCanshow(boolean canshow) {
        if (isRunning && canshow) {
            return;
        }
        this.canshow = canshow;
    }

    @Override
    public Void doInBackground(Void... params) {
        if (isRunning) {
            LogUtils.debug("BookShelfSyncInitDataTask is Running!");
            return null;
        }
        isRunning = true;
        if (!NetUtils.checkNetworkUnobstructed()) {
            bookShelf.shelfBookTables.clear();
            bookShelf.shelfBookTables.addAll(BookDao.getInstance().findShelfBooks());
            isRunning = false;
            return null;
        }

        if (!UserHelper.getInstance().isLogin()) {
            isRunning = false;
            return null;
        }

        start = System.currentTimeMillis();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        RequestFuture<VolleyError> futureError = RequestFuture.newFuture();
        GetRequest request = new GetRequest(String.format(URLConstants.BOOKSHELF_BOOKS,
               SharePrefHelper.getCurLoginUserBookshelfUpdateTime()) + CommonUtils.getPublicGetArgs(),
                future, future
               ,"1.2"
        );
        RequestQueueManager.addRequest(request);

        try {
            long s = System.currentTimeMillis();
            JSONObject response = future.get(10, TimeUnit.SECONDS);

            LogUtils.debug("网络加载用时==" + (System.currentTimeMillis() - s));
            LogUtils.debug("shelf==response===" + response.toString());
            int errorid = ResponseHelper.getErrorId(response);
            if (XSNetEnum._VDATAERRORCODE_ERROR_NOT_LOGIN.getCode() == errorid) {
                LogUtils.debug("登录账号后才能同步网络书架收藏");

            }
            if (XSNetEnum._VDATAERRORCODE_ERROR_BOOKSHELF_NOBOOK.getCode() == errorid ||
                    XSNetEnum._VDATAERRORCODE_ERROR_BOOKSHELF_FIRST_NOBOOK.getCode() == errorid) {
                LogUtils.debug(errorid + "。。。。" + XSNetEnum
                        ._VDATAERRORCODE_ERROR_BOOKSHELF_NOBOOK.getMsg());
                BookDao.getInstance().deleteBook(new ArrayList<String>());

            }
            if (XSNetEnum._VDATAERRORCODE_ERROR_BOOKRACK_NO_UPDATE.getCode() == errorid || 10272
                    == errorid) {
                LogUtils.debug("10029....书架已经是最新的");
            }
            if (!ResponseHelper.isSuccess(response)) {
                LogUtils.debug("shelf==同步书籍失败");
                bookShelf.shelfBookTables.clear();
                bookShelf.shelfBookTables.addAll(BookDao.getInstance().findShelfBooks());
                return null;
            }
            LogUtils.debug("书籍数据===" + ResponseHelper.getVdata(response).toString());

            start = System.currentTimeMillis();
            if (response == null) {
                bookShelf.shelfBookTables.clear();
                bookShelf.shelfBookTables.addAll(BookDao.getInstance().findShelfBooks());
                return null;
            }
            final JSONObject vdata = ResponseHelper.getVdata(response);
            long lut = vdata.optLong("u_lut");

            if (lut != 0) {
                SharePrefHelper.setCurLoginUserBookshelfUpdateTime(lut);
            }
            //1 显示书架
            JSONArray jsonArray = vdata.optJSONArray("u_booklist");
            LogUtils.debug("Shelf......."+jsonArray.toString());
            if (jsonArray == null) {
                bookShelf.shelfBookTables.clear();
                bookShelf.shelfBookTables.addAll(BookDao.getInstance().findShelfBooks());
                return null;
            }
            Type type = new TypeToken<ArrayList<BookTable>>() {
            }.getType();
            List<BookTable> bookTables = new Gson().fromJson(jsonArray.toString(),
                    type);
            //2 格式化数据 同步本地数据库
            List<String> ids = new ArrayList<String>();
            if (bookTables == null) {
                bookShelf.shelfBookTables.clear();
                bookShelf.shelfBookTables.addAll(BookDao.getInstance().findShelfBooks());
                List<BookTable> list = BookDao.getInstance().findAllBook();

                return null;
            }
            for (BookTable bookTable : bookTables) {
                bookTable.setIsOnShelf(1);//都在书架
                if (1 == BookDao.getInstance().hasKey(bookTable.getBookId())) {
                    BookTable bt1 = BookDao.getInstance().findBook(bookTable
                            .getBookId());
                    if (bt1 == null) {
                        continue;
                    }
                    //判断书籍有更新
                    if (DEBUG) {
                        LogUtils.debug("shelf同步==网络中的变化之前bt1==" + bt1.getBookId() + "===" +
                                bt1);
                    }
                    if (bt1.getCatalogUpdateTime() < bookTable.getCatalogUpdateTime() && bt1.getCatalogUpdateTime() != 0) {
                        bt1.setHasNewChapter(1);
                    } else {
                        bt1.setHasNewChapter(0);
                    }
                    bt1.setCoverImageId(bookTable.getCoverImageId());
                    bt1.setName(bookTable.getName());
                    bt1.setAuthorName(bookTable.getAuthorName());
                    bt1.setIsMonthly(bookTable.getIsMonthly());
                    bt1.setAddToShelfTime(bookTable.getAddToShelfTime());
                    if (DEBUG) {
                        LogUtils.debug("shelf同步修改==网络中的bt1==" + bt1.getBookId() + "===" +
                                bt1);
                    }
                    bt1.setIsOnShelf(1);
                    BookDao.getInstance().updateBook(bt1);
                } else {
                    //添加
                    if (DEBUG) {
                        LogUtils.debug("shelf同步添加==" + bookTable.getBookId() + "===" +
                                bookTable);
                    }
                    bookTable.setHasNewChapter(1);
                    bookTable.setCatalogUpdateTime(0);
                    BookDao.getInstance().addBook(bookTable);
                }
                ids.add(bookTable.getBookId());
            }
            BookDao.getInstance().deleteBook(ids);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        LogUtils.debug("总用时==" + (System.currentTimeMillis() - start));
        isRunning = false;
        return null;
    }


    @Override
    protected void onPostExecute(Void i) {
        super.onPostExecute(i);
        if (bookShelf != null) {
            bookShelf.hideRefreshState();
            if (isCanshow()) {
                bookShelf.setHeadBackground(true);
                bookShelf.refreshLocalData();
                LogUtils.debug("BookShelfSyncInitDataTask...time==" + (System.currentTimeMillis() - start));
            }
        }
    }
}
