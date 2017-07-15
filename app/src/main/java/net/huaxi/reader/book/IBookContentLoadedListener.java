package net.huaxi.reader.book;

import net.huaxi.reader.book.paging.PageContent;

/**
 * 当前页内容回调接口
 * Created by taoyingfeng
 * 2015/12/11.
 */
public interface IBookContentLoadedListener {

    public void onLoading(float percent);

    public void onLoadContentCompleted(PageContent pageContent);

    public void onLoadContentFiled(int code);

}
