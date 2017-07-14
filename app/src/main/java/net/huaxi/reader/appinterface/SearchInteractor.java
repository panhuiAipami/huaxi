package net.huaxi.reader.appinterface;

import java.util.List;

import net.huaxi.reader.bean.SearchBean;
import net.huaxi.reader.bean.SearchKeyBean;

/**
 * @Description: [搜下的显示模型借口，在这个借口SearchInteractorImpl会实现该借口]
 * @Author: [Saud]
 * @CreateDate: [16/5/3 16:47]
 * @UpDate: [16/5/3 16:47]
 * @Version: [v1.0]
 */
public interface SearchInteractor {


    /**
     * 保持搜索一个list
     */
    void savaHistoryList(List<String> key);

    /**
     * 获取历史的list
     */

    void getHistoryList(String key);


    /**
     * 获取热词
     */
    void getHotKey();


    /**
     * 获取途径词语
     *
     * @param str
     */
    void getSearchSimpleKey(String str);

    /**
     * 服务器数据
     */
    void getSearchData(String str);


    void destroy();


    interface OnSearchListener {
        /**
         * 历史记录数据的回调
         *
         * @param historys
         */
        void onHistory(List<String> historys);

        /**
         * 提示关键字的类是回家
         *
         * @param keys
         */
        void onSimpleKey(List<SearchKeyBean> keys);

        /**
         * 搜索结果的回到
         *
         * @param datas
         */
        void onSearch(List<SearchBean> datas);

        /**
         * 热词的回调
         *
         * @param hotkeys
         */
        void onHotKey(List<String> hotkeys);

        /**
         * 相关书籍
         *
         * @param sList
         */
        void onAboutBook(List<SearchBean> sList);
    }


}
