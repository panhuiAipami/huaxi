package net.huaxi.reader.model.version;

/**
 * Created by ZMW on 2016/5/26.
 */
public interface AppDownloadListener {
    void start();
    void finsh();
    void downloadProgress(float f);
    void error(String desc);
}
