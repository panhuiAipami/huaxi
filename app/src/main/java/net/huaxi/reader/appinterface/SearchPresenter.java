package net.huaxi.reader.appinterface;

import java.util.List;

/**
 * Created by Saud on 16/5/1.
 */
public interface SearchPresenter {

    /**
     * 获取一个list
     *
     * @param key
     */
    void getHistoryList(String key);

    /**
     * 保存一个list
     *
     * @param list
     */
    void saveHistoryList(List<String> list);


    /**
     * 从数据库查找关键字
     * search Data from DataBase
     *
     * @param key
     */
    void searchSimpleKey(String key);


    /**
     * search Data from Server
     *
     * @param key
     */
    void searchFromNet(String key);

    /**
     * 获取热词
     */
    void searchHotKey();


    /**
     * 释放内存
     */
    void destroy();



}
