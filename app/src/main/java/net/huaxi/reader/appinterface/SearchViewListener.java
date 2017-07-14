package net.huaxi.reader.appinterface;

import net.huaxi.reader.bean.SearchBean;

import java.util.List;

import net.huaxi.reader.bean.SearchKeyBean;

/**
 * @Description: [一句话描述该类的功能]
 * @Author: [Saud]
 * @CreateDate: [$date$ $time$]
 * @UpDate: [$date$ $time$]
 * @Version: [v1.0]
 */
public interface SearchViewListener {

    /**
     * 搜索list的
     */
    void onHistory(List<String> data);

    /**
     * 相关书籍
     */
    void onAboutBook(List<SearchBean> sList);

    /**
     * 热词
     */
    void onHotKey(List<String> hotKeys);

    /**
     * 搜索结果
     */
    void onSearchData(List<SearchBean> datas);

    /**
     * 提示的关键词
     */
    void onSearchDB(List<SearchKeyBean> datas);



}
