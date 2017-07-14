package net.huaxi.reader.presenter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.appinterface.ClassifyPresenter;
import net.huaxi.reader.appinterface.ClassifyViewListener;
import net.huaxi.reader.bean.ClassifyChildBean;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;

import net.huaxi.reader.common.CommonUtils;

/**
 * @Description: [ 子分类页面的业务逻辑 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/7/14 15:03 ]
 * @UpDate: [ 16/7/14 15:03 ]
 * @Version: [ v1.0 ]
 */
public class ClassifyPresenterImpl implements ClassifyPresenter {

    private ClassifyViewListener listener;
    private int limit;
    private int offset;
    private String vip;
    private String orderby;
    private String classityID = "";
    private String classityParentID = "";

    public ClassifyPresenterImpl(ClassifyViewListener listener, String classityParentID) {
        this.classityParentID = classityParentID;
        this.listener = listener;
    }

    @Override
    public void loadingData(int limit, int offset, String vip, String orderby, String classityID) {
        this.limit = limit;
        this.offset = offset;

        if ("".equals(classityID) || classityID == null) {
            this.classityID = "";
        } else {
            this.classityID = classityID;
        }
        if ("".equals(vip) || vip == null) {
            this.vip = "";
        } else {
            this.vip = "&vip=" + vip;
        }
        if ("".equals(orderby) || orderby == null) {
            this.orderby = "";
        } else {
            this.orderby = "&orderby=" + orderby;
        }
        synGetData();
    }


    private void synGetData() {

        String url = String.format(URLConstants.CLASSIFY_BOOK_LIST, classityID, classityParentID, offset, limit) + vip + orderby + CommonUtils.getPublicGetArgs();
        final GetRequest request = new GetRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ClassifyChildBean childBean = null;
                try {
                    boolean success = ResponseHelper.isSuccess(response);
                    Type type = new TypeToken<ClassifyChildBean>() {
                    }.getType();
                    if (success) {
                        childBean = new Gson().fromJson(response.toString(), type);
                    }
                    if (listener != null) {
                        listener.onDataList(childBean);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(new Throwable(error.getMessage()));
                LogUtils.debug("网络错误");
            }
        }

        );
        RequestQueueManager.addRequest(request);
    }
}
