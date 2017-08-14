package net.huaxi.reader.book.paging;

import com.tools.commonlibs.tools.LogUtils;

import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.book.datasource.model.ChapterPage;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.statistic.ReportUtils;

import java.util.HashMap;

/**
 * 分页内容管理中心
 * Created by taoyingfeng
 * 2015/12/25.
 */
public class PagingManager {

    private static PagingManager singleton = null;

    Paging bookContentComposing;

    public static final int LOAD_NEXT_CHAPTER = -101;  //必须负值
    public static final int LOAD_PRE_CHAPTER = -102;  //必须负值


    public static PagingManager getSingleton() {
//        if (singleton == null) {
//            synchronized (PagingManager.class) {
//                singleton = new PagingManager();
//            }
//        }
//        return singleton;
        return SingletonHolder.SINGLETON;
    }

    //静态内部类，解决了我们只有在调用getInstance方法后，才去创建这个单例实例的需求。不会因为加载这个类就创建实例，解决第一种方法的缺点。
    private static class SingletonHolder {
        private static PagingManager SINGLETON = new PagingManager();
    }

    private PagingManager() {
        bookContentComposing = new Paging();
    }

    /*缓存分页后的章节内容   key = chapterId, value = 排版后的章节内容 */
    private volatile HashMap<String, ChapterPage> mPageMap = new HashMap<String, ChapterPage>();

    public boolean hasChapterPage(String chapterId) {
        if (mPageMap != null && mPageMap.containsKey(chapterId)) {
            return true;
        }
        return false;
    }

//    /**
//     * 添加章节分页记录
//     * @param chapterId
//     * @param chapterPage
//     */
//    public void addPageMap(String chapterId, ChapterPage chapterPage) {
//        if (mPageMap == null) {
//            mPageMap = new HashMap<String, ChapterPage>();
//        }
//        mPageMap.put(chapterId, chapterPage);
//    }

    public ChapterPage getChaperPage(String chapterId) {
        if (hasChapterPage(chapterId)) {
            return mPageMap.get(chapterId);
        }
        return null;
    }

    /**
     * 清空章节分页缓存.(阅读设置变化时，调用这个方法)
     */
    public void resetPageMap() {
        if (mPageMap != null) {
            mPageMap.clear();
        }
    }

    /**
     * 清空页面缓存(主要用于用户订阅，充值)
     *
     * @param chapterId (章节ID)
     */
    public void resetPageCache(String chapterId) {
        synchronized (mPageMap) {
            if (mPageMap != null && mPageMap.containsKey(chapterId)) {
                mPageMap.remove(chapterId);
            }
        }
    }

    /**
     * 定位新的操作位置.
     *
     * @param page
     * @param startIndex 当前阅读位置
     * @param isNext     true:下一页,false:上一页.
     * @return
     */
    public int findNewStartIndex(ChapterPage page, int startIndex, Boolean isNext) {
        if (page != null) {
            int curPageNum = 0;
            HashMap<Integer, PageContent> indexHashMap = page.getPageMap();
            if (indexHashMap != null && indexHashMap.size() > 0) {
                for (int i = 1; i <= indexHashMap.size(); i++) {
                    PageContent index = indexHashMap.get(i);
                    if (startIndex >= index.getStartIndex() && startIndex <= index.getEndIndex()) {
                        curPageNum = i;
                        break;
                    }
                }
                if (startIndex == 0) {
                    curPageNum = 1;
                }
            }
            if (isNext) {
                curPageNum++;
            } else {
                curPageNum--;
            }
            if (curPageNum > indexHashMap.size()) {
                //需要寻找下一章
                return LOAD_NEXT_CHAPTER;
            }
            if (curPageNum < 1) {
                //需要寻找上一章
                return LOAD_PRE_CHAPTER;
            }
            LogUtils.info("&&&& 当前页共" + indexHashMap.size() + " 页------看到第" + curPageNum + "页");
            PageContent index = indexHashMap.get(curPageNum);
            return index.getStartIndex();
        }
        return LOAD_NEXT_CHAPTER;
    }

    /**
     * 查找页面的内容
     *
     * @param chapterId      章节ID.
     * @param chapterNo      章节编号
     * @param startIndex     页面起始索引.
     * @param locateLastPage 定位本章最后一页.
     * @return
     */
    public PageContent getPageContent(String chapterId, int chapterNo, int startIndex, boolean locateLastPage) {
        //// FIXME: 2016/1/4 添加参数来定位章节的最后一页，主要是目录中的总数不是字节的总数.
        ChapterPage chapterPage = mPageMap.get(chapterId);
        PageContent pageContent = null;
        int index = -1;
        if (chapterPage != null) {
            HashMap<Integer, PageContent> indexHashMap = chapterPage.getPageMap();
            if (indexHashMap != null && indexHashMap.size() > 0) {
                if (locateLastPage) {
                    pageContent = indexHashMap.get(index = indexHashMap.size());
                } else {
                    for (index = 1; index <= indexHashMap.size(); index++) {
                        pageContent = indexHashMap.get(index);
                        if (startIndex >= pageContent.getStartIndex() && startIndex <= pageContent.getEndIndex()) {
                            break;
                        }
                    }
                }
                LogUtils.info("------getPageContent----->获取PageContent对象 第" + chapterNo + "章 startIndex =" + startIndex + " [locateLastPage =" + locateLastPage
                        + "] index =" +
                        index + "页");
                int count = DataSourceManager.getSingleton().getChapterCount();
                float percent = 0;
                if (chapterNo > 0 && count > 0) {
                    float son = ((chapterNo - 1) * indexHashMap.size() + index);
                    percent = son / (count * indexHashMap.size());
                }
                pageContent.setPercent(percent);
            }
        }
        return pageContent;
    }

    /**
     * 对当前章节分页并混存.
     */
    public ChapterPage Paging(String chapterId, String chapterName, byte[] data, String encoding, int bookType) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            ChapterPage chapterPage = bookContentComposing.getChapterPageInfos(chapterId, chapterName, data, encoding, bookType);
            if (chapterPage != null && (ReadPageState.BOOKTYPE_NORMAL == bookType || ReadPageState.BOOKTYPE_ORDER_PAY == bookType ||
                    ReadPageState.BOOKTYPE_RECHARGE == bookType)) {
                synchronized (mPageMap) {
                    mPageMap.put(chapterId, chapterPage);
                    LogUtils.info("@@@@chapterName = " + chapterName + " 分页后 size =  " + chapterPage.getPageMap().size());
                }
            }
            return chapterPage;
        } catch (Exception e) {
            ReportUtils.reportError(e);
        }
        return null;
    }

    /**
     * 对当前章节分页但不缓存.
     */
    public ChapterPage PagingWithoutCache(String chapterId, String chapterName, byte[] data, String encoding, int bookType) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            ChapterPage chapterPage = bookContentComposing.getChapterPageInfos(chapterId, chapterName, data, encoding, bookType);
            return chapterPage;
        } catch (Exception e) {
            ReportUtils.reportError(e);
        }
        return null;
    }


    /**
     * 根据错误码生成页面渲染类
     */
    public static PageContent producePageContentByErrorCode(String chapterId, String chapterName, int errorId, int bookType) {
        PageContent pageContent = new PageContent();
        pageContent.setChapterId(chapterId);
        pageContent.setChapterName(chapterName);
        pageContent.setErrorCode(errorId);
        pageContent.setBookType(bookType);
        pageContent.setPercent(DataSourceManager.getSingleton().getPercentByChapterId(chapterId));  //设置模糊的阅读百分比.
        return pageContent;
    }


}
