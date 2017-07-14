package net.huaxi.reader.util;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.ResponseHelper;

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.https.PostRequest;

/**
 * Created by ZMW on 2016/1/14.
 */
public class BookShelfDataUtil {

    public interface BookShelfDataListener{
        void back(int response);
    }

    public static void addBook(final String bid,final BookShelfDataListener listener){
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("u_action", "b_add");
        map.put("bk_mid", bid);
        PostRequest request = new PostRequest(URLConstants.BOOKSHELF_DEL_ADD_BOOK, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtils.debug("resposne==" + response.toString());
                        if(listener!=null){
                            listener.back(ResponseHelper.getErrorId(response));
                        }
//                        if (10014 == ResponseHelper.getErrorId(response)) {
//                            LogUtils.debug("没有书");
//                        }
//                        if (10020 == ResponseHelper.getErrorId(response)) {
//                            LogUtils.debug("书籍已经添加==bid==" + bid);
//                        }
//                        if (!ResponseHelper.isSuccess(response)) {
//                            LogUtils.debug("添加失败 ==bid==" + bid );
//                            return;
//                        }
//                        LogUtils.debug("添加成功 ==bid==" + bid);
                    }
                },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug("网络错误");
            }
        }, map);
        RequestQueueManager.addRequest(request);
    }





}
