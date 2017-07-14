package net.huaxi.reader.book.datasource;

import net.huaxi.reader.book.paging.PageContent;

/**
 * 章节内容加载回调接口.
 * Created by taoyingfeng on 2015/12/3.
 */
public interface IChapterContentLoadListener {

    /**
     * 书籍章节内容加载完成
     *
     * @param pageContent 页面内容
     */
    public void onLoadFinished(PageContent pageContent);


    /**
     * 内容加载中.
     */
    public void onLoading();

}
