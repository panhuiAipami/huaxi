package net.huaxi.reader.appinterface;

/**
 * Created by Saud on 16/1/19.
 */
public interface ChapterDownloadListener {
    /**
     * 下载成功的回调
     */
    void downloadAllFinish(int errorid);

    /**
     * 需要登录
     */
    void needLogin();

    /**
     * 金币不足
     */
    void needCoin(int errorid);

    /**
     * 现在错误
     */
    void error();

    /**
     * 服务器返回成功
     */
    void success();

    /**
     * 下载一个成功
     */
    void downloadOneFinish(String chepterId);

    /**
     * 一个下载错误
     */
    void downloadOneError(String chepterId);

    /**
     * 请求的数据错误，或者返回的数据错误
     */
    void dataError();
}
