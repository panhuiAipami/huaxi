package com.tools.commonlibs.cache;

import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;


/**
 * Created by saymagic on 15/1/27.
 */
public class RequestQueueManager {
    public static RequestQueue mRequestQueue = null;

    public static RequestQueue getInstance() {
        if (null == mRequestQueue) {
//            mRequestQueue = Volley.newRequestQueue(CommonApp.getInstance());//普通的http请求
            mRequestQueue = Volley.newRequestQueue(CommonApp.getInstance(), new SSLHurlstack());
            //支持https
        }
        return mRequestQueue;
    }

    /**
     * 添加Request∂
     *
     * @param request
     * @param object  tag标签，可用URL来标识
     */
    public static void addRequest(Request<?> request, Object object) {
        if (object != null) {
            request.setTag(object);
        }
        getInstance().add(request);
    }

    public static void addRequest(Request<?> request) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
                ViewUtils.toastShort("网络信号不良，请检查您的网络");
            }
            return;
        }
        if (request != null) {
            addRequest(request, null);
        }
    }


    /**
     * 根据tag取消请求队列
     *
     * @param tag
     */
    public static void cancelAll(Object tag) {
        try {
            getInstance().cancelAll(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
