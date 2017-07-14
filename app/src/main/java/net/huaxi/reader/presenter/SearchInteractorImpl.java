package net.huaxi.reader.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.appinterface.SearchInteractor;
import net.huaxi.reader.bean.HotKeyBean;
import net.huaxi.reader.bean.SearchBean;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.model.AssetsDatabaseManager;
import net.huaxi.reader.statistic.ReportUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.bean.SearchKeyBean;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.util.EncodeUtils;

/**
 * @Description: [一句话描述该类的功能]
 * @Author: [Saud]
 * @CreateDate: [$date$ $time$]
 * @UpDate: [$date$ $time$]
 * @Version: [v1.0]
 */
public class SearchInteractorImpl implements SearchInteractor {

    private static final int AUTHOR_NAME = 1;
    private static final int BOOK_TITLE = 2;
    private Activity activity;
    private Gson gson;
    private OnSearchListener listener;
    private AssetsDatabaseManager mg;
    private SQLiteDatabase db;
    private List<SearchKeyBean> mSearchKeys = new ArrayList<>();
    private List<String> mTempList = new ArrayList<>();


    public SearchInteractorImpl(Activity activity, OnSearchListener listener) {
        this.listener = listener;
        this.activity = activity;
        this.gson = new Gson();
    }


    @Override
    public void savaHistoryList(List<String> key) {

        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(activity);
        String prefStr = gson.toJson(key);
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveString("historyKey", prefStr);

    }

    @Override
    public void getHistoryList(String key) {

        new EasyTask<Activity, String, Void, List<String>>(activity) {
            @Override
            public void onPostExecute(List<String> list) {
                super.onPostExecute(list);
                if (listener != null) {
                    listener.onHistory(list);
                }
            }

            @Override
            public List<String> doInBackground(String... params) {
                SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(activity);
                String str = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getString("historyKey");
                List<String> list = gson.fromJson(str, new TypeToken<List<String>>() {
                }.getType());
                return list;
            }

        }.execute();


    }

    @Override
    public void getHotKey() {


        new EasyTask<Activity, Void, Void, List<String>>(activity) {

            @Override
            public void onPostExecute(List<String> list) {
                super.onPostExecute(list);
                if (listener != null) {
                    listener.onHotKey(list);
                }
                getHotKeyFromSever();
            }

            @Override
            public List<String> doInBackground(Void... params) {
                return getHotKeyFormTemp();
            }
        }.execute();


    }

    @Override
    public void getSearchSimpleKey(String str) {
        List<SearchKeyBean> searchKeyBeens = searchKeyForDB(str);
//        LogUtils.debug("提示词=" + searchKeyBeens.size());
        if (listener != null) {
            listener.onSimpleKey(searchKeyBeens);
        }
    }

    @Override
    public void getSearchData(String key) {
        doSearch(key);
    }

    @Override
    public void destroy() {
        if (mg != null) {
            mg.closeAllDatabase();
        }
        activity = null;
        listener = null;
        gson = null;

    }

    /**
     * 服务器更新的热词
     */
    private void getHotKeyFromSever() {
        //获取服务器数据
        String hotkeyUrl = URLConstants.APP_SEARCH_HOTKEY + CommonUtils.getPublicGetArgs();
        GetRequest request = new GetRequest(hotkeyUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("热词==" + response.toString());
                if (ResponseHelper.isSuccess(response)) {
                    List<String> tempList = getHotKeyFormTemp();
                    if (tempList == null || tempList.size() == 0) {//如果没有缓存数据
                        List<String> list = parseHotKey(response.toString());
                        if (listener != null) {
                            listener.onHotKey(list);
                        }
                    }
                    SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(SearchInteractorImpl.this.activity);
                    SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveString("hotKey", response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(error);
            }
        });
        RequestQueueManager.addRequest(request);
    }

    /**
     * 缓存的热词
     *
     * @return
     */
    private List<String> getHotKeyFormTemp() {
        //       获取缓存数据
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(activity);
        String str = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getString("hotKey");
        List<String> list = null;
        list = parseHotKey(str);
        return list;
    }


    /**
     * 对Json类型的数据热词解析
     *
     * @param response
     */
    private List<String> parseHotKey(String response) {
        Type type = new TypeToken<HotKeyBean>() {
        }.getType();
        HotKeyBean hotbean = new Gson().fromJson(response, type);
        List<String> hotWords = new ArrayList<>();
        if (hotbean != null) {
            List<HotKeyBean.VdataBean.ListBean> list = hotbean.getVdata().getList();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    hotWords.add(list.get(i).getTitle());
                }
            }
        }

        return hotWords;
    }

    /**
     * 从assets中拷贝数据库，并且查询数据库
     * 数据库只有在版本升级后才会自动拷贝，否则只拷贝一次。
     *
     * @param str
     */
    private List<SearchKeyBean> searchKeyForDB(String str) {

        if (mg == null) {
            // 获取管理对象，因为数据库需要通过管理对象才能够获取
            mg = AssetsDatabaseManager.getManager(activity.getApplicationContext());
            // 通过管理对象获取数据库
            db = mg.getDatabase("searchDB.db");
        }
        mSearchKeys.clear();
        mTempList.clear();
        if (StringUtils.isNotBlank(str)) {
            //查询作者名字
            Cursor cursor2 = db.query("books", null, "bp_au_pname like ?", new String[]{"%" + str + "%"}, null, null, null, null);
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    String authorName = cursor2.getString(cursor2.getColumnIndex("bp_au_pname"));
                    SearchKeyBean searchBean = new SearchKeyBean();
                    searchBean.setKey(authorName);
                    searchBean.setTag(AUTHOR_NAME);
                    if (!mTempList.contains(authorName)) {
                        mSearchKeys.add(searchBean);
                        mTempList.add(authorName);
                    }
                }
            }

            //查询书名
            Cursor cursor1 = db.query("books", null, "bk_title like ?", new String[]{"%" + str + "%"}, null, null, null, null);
            if (cursor1 != null) {
                while (cursor1.moveToNext()) {
                    String bookTitle = cursor1.getString(cursor1.getColumnIndex("bk_title"));
                    SearchKeyBean searchBean = new SearchKeyBean();
                    searchBean.setTag(BOOK_TITLE);
                    searchBean.setKey(bookTitle);
                    if (!mTempList.contains(bookTitle)) {
                        mSearchKeys.add(searchBean);
                        mTempList.add(bookTitle);
                    }
                }
            }
        }
        return mSearchKeys;
    }


    /**
     * 查找关键词内容
     *
     * @param searchword
     */
    private void doSearch(String searchword) {
        int offset = 0;
        int limit = 20;
        String url = String.format(URLConstants.SEARCH_BOOK_URL, EncodeUtils.encodeString_UTF8(searchword), offset, limit) + CommonUtils.getPublicGetArgs();

        GetRequest request = new GetRequest(url, new Response.Listener<JSONObject>() {

            private List<SearchBean> mSList = null;

            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne=="+ response.toString());
                if (ResponseHelper.isSuccess(response)) {
                    JSONObject object = ResponseHelper.getVdata(response);
                    if (object != null) {
                        try {
                            JSONArray array = object.getJSONArray(XSKEY.KEY_LIST);
                            if (array != null && array.length() > 0) {
                                Type type = new TypeToken<ArrayList<SearchBean>>() {
                                }.getType();
                                mSList = new Gson().fromJson(array.toString(), type);
                                if (mSList == null || mSList.size() == 0) {
                                    getAboutRecommedData();
                                }
                            }else {
                                getAboutRecommedData();
                            }
                        } catch (Exception e) {
                            getAboutRecommedData();
                            ReportUtils.reportError(e);
                        }
                    }

                } else {
                    getAboutRecommedData();
                }

                if (listener != null) {
                    listener.onSearch(mSList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getAboutRecommedData();
                ReportUtils.reportError(error);
            }
        });
        RequestQueueManager.addRequest(request);
    }

    /**
     * 相关推荐的网络数据
     */
    private void getAboutRecommedData() {

        String url = String.format(URLConstants.SEARCH_ABOUT_RECOMMED_BOOK_URL, "searchrcm") + CommonUtils.getPublicGetArgs();
        GetRequest request = new GetRequest(url, new Response.Listener<JSONObject>() {

            private List<SearchBean> mSList = null;

            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("resposne==" + response.toString());
                if (ResponseHelper.isSuccess(response)) {
                    JSONObject object = ResponseHelper.getVdata(response);
                    if (object != null) {
                        try {
                            JSONArray array = object.getJSONArray(XSKEY.KEY_LIST);
                            if (array != null && array.length() > 0) {
                                Type type = new TypeToken<ArrayList<SearchBean>>() {
                                }.getType();
                                mSList = new Gson().fromJson(array.toString(), type);
                            }
                        } catch (Exception e) {

                        }
                    }
                }
                if (listener != null) {
                    listener.onAboutBook(mSList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onAboutBook(null);
                }
                ReportUtils.reportError(error);
            }
        });
        RequestQueueManager.addRequest(request);
    }


}
