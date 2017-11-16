package net.huaxi.reader.callback;

/**
 * Created by Administrator on 2017/8/4.
 */

public interface CallBack {
    //请求动作错误调用
    public void fail(String actionName, Object object);
    //请求动作成功时调用
    public void success(String actionName, Object object);
}
