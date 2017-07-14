package net.huaxi.reader.thread;

import android.app.Activity;

import com.android.volley.toolbox.RequestFuture;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.task.EasyTask;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.util.EncodeUtils;

import org.json.JSONObject;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.https.PostRequest;

/**
 * Function:    异步执行同步删除本地为成功删除的书籍。
 * Author:      taoyf
 * Create:      16/8/31
 * Modtime:     16/8/31
 */
public class SycnDeleteFailedBookTask extends EasyTask<Activity,String,Void,Void> {

    private long starttime = 0;

    public SycnDeleteFailedBookTask(Activity activity) {
        super(activity);
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        LogUtils.debug("SycnDeleteFailedBookTask Start");
        starttime = System.currentTimeMillis();
    }

    @Override
    public void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        LogUtils.debug(" SycnDeleteFailedBookTask  耗时: "+ (System.currentTimeMillis() - starttime) +" ms" );
    }

    @Override
    public Void doInBackground(String... params) {
        //如果用户没有登录,退出。
        if (!UserHelper.getInstance().isLogin()) {
            LogUtils.debug("用户未登录!");
            return null;
        }
        //同步删除当前用户删除的书籍
        if (params != null && params.length > 0) {
            for (final String bid : params) {
                asynDeleteBook(bid);
            }
        }
        //同步删除没有删除成功的书籍。
        String[] ids = SharePrefHelper.getBookshelfDelFailBidArray();
        SharePrefHelper.clearBookshelfDelFailBids();
        for (final String bid : ids) {
            asynDeleteBook(bid);
        }
        return null;
    }

    private void asynDeleteBook(String bid) {
        if (StringUtils.isBlank(bid)) {
            return;
        }
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_action", EncodeUtils.encodeString_UTF8("b_del"));
        map.put("bk_mid", EncodeUtils.encodeString_UTF8(bid));
        PostRequest postRequest = new PostRequest(URLConstants.BOOKSHELF_DEL_ADD_BOOK, future, future,map);
        RequestQueueManager.addRequest(postRequest);
        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            LogUtils.debug("resposne==" + response.toString());
            if (10014 == ResponseHelper.getErrorId(response)) {
                LogUtils.debug("没有书");
                return;
            }
            if (!ResponseHelper.isSuccess(response)) {
                SharePrefHelper.setBookshelfDelFailBids(bid);
                return;
            }
            LogUtils.debug("删除成功 ==bid==" + bid);
        } catch (Exception e) {
            e.printStackTrace();
            SharePrefHelper.setBookshelfDelFailBids(bid);
        }
    }
}
