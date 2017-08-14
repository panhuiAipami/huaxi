package net.huaxi.reader.book.datasource;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.book.FileConstant;
import net.huaxi.reader.book.datasource.model.ChapterPage;
import net.huaxi.reader.book.paging.PageContent;
import net.huaxi.reader.book.paging.PagingManager;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import hugo.weaving.DebugLog;

/**
 * 数据源管理中心
 * Created by taoyingfeng on 2015/12/3.
 */
public class DataSourceManager {

    private static DataSourceManager singleton = null;
    IChapterContentLoadListener iChapterContentLoadListener;
    private ChapterContentLoader mChapterContentLoaderThread;
    //混存章节序号对应的章节信息。
    private volatile HashMap<Integer, ChapterTable> catalogMap = new LinkedHashMap<Integer, ChapterTable>();
    //混存章节ID对应的章节序列号.
    private volatile HashMap<String, Integer> chaperIdToChapterNo = new HashMap<String, Integer>();
    private String bookId;
    private String bookName;
    private String bookAuthor;
    private boolean isMonthly; //包月

    public static DataSourceManager getSingleton() {
        if (singleton == null) {
            synchronized (DataSourceManager.class) {
                if (singleton == null) {
                    singleton = new DataSourceManager();
                }
            }
        }
        return singleton;
    }

    public HashMap<Integer, ChapterTable> getCatalogMap() {
        return catalogMap;
    }

    public synchronized void addCatalog(Integer chapterNo, ChapterTable chapterTable) {
        catalogMap.put(chapterNo, chapterTable);
        chaperIdToChapterNo.put(chapterTable.getChapterId(), chapterNo);
    }

    private synchronized void clearDataSourceCache() {
        if (catalogMap != null) {
            catalogMap.clear();
        }
        if (chaperIdToChapterNo != null) {
            chaperIdToChapterNo.clear();
        }
    }

    /**
     * 获取当前书籍章节总数.
     *
     * @return
     */
    public int getChapterCount() {
        if (catalogMap != null) {
            return catalogMap.size();
        }
        return 0;
    }

    /**
     * 获取当前阅读章节No
     *
     * @return no
     */
    public int getCurrentChapterNo() {
        int no = 0;
        BookTable bookTable = getLastReadRecord();
        if (bookTable != null) {
            String lastReadChapterId = bookTable.getLastReadChapter();
            no = getChapterNoById(lastReadChapterId);
        }

        return no;
    }

    /**
     * 根据百分比获取章节ID.
     *
     * @param percent 百分比
     * @return 章节ID.
     */
    public String getChapterByNo(int percent) {
        ChapterTable chapterTable = null;
        if (catalogMap != null) {
            chapterTable = catalogMap.get(percent);
        }
        if (chapterTable != null) {
            return chapterTable.getChapterId();
        }
        return null;
    }

    /**
     * 设置书籍信息
     *
     * @param bookId   书籍ID
     * @param bookName 书籍名.
     * @param bookAuthor 作者名
     * @param isMonthly 包月
     */
    public void setBook(String bookId, String bookName, String bookAuthor, boolean isMonthly) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.isMonthly = isMonthly;
        clearDataSourceCache();
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public boolean getIsMonthly() {
        return isMonthly;
    }

    /**
     * 根据章节ID获取章节序列号
     *
     * @param chapterId
     * @return
     */
    public int getChapterNoById(String chapterId) {
        int no = 0;
        if (chaperIdToChapterNo != null && StringUtils.isNotEmpty(chapterId) && chaperIdToChapterNo.get(chapterId) != null) {
            no = chaperIdToChapterNo.get(chapterId);
        }
        return no;
    }

    /**
     * 获取最后阅读记录
     */
    public synchronized BookTable getLastReadRecord() {
        String lastReadChapterId = "";
        BookTable table = BookDao.getInstance().findBook(bookId);
        try {
            if (table != null) {
                lastReadChapterId = table.getLastReadChapter();
                LogUtils.debug("LastReadChapter"+lastReadChapterId);
            }
            //// FIXME: 16/4/11 默认没有阅读记录，显示版权首页
//            if (StringUtils.isEmpty(lastReadChapterId)) {
//                if (catalogMap != null && catalogMap.size() > 0) {
//                    ChapterTable chapterTable = catalogMap.get(1);
//                    if (chapterTable != null) {
//                        lastReadChapterId = chapterTable.getChapterId();
//                        if (StringUtils.isNotEmpty(lastReadChapterId)) {
//                            table.setLastReadChapter(lastReadChapterId);
//                            BookDao.getInstance().updateBook(table);
//                        }
//                    }
//                }
//            }
        } finally {
            return table;
        }
    }

    /**
     * 重置最后阅读章节记录
     *
     * @param isNext true:下一章;false:上一章.
     */
    public void resetLastReadLocation(boolean isNext) {
        BookTable bookTable = getLastReadRecord();
        String nextChapterId = "";
        if (bookTable != null) {
            String lastReadChapterId = bookTable.getLastReadChapter();
            if (StringUtils.isEmpty(lastReadChapterId)) {
                if (isNext) {
                    //针对版本首页翻向下一页的时候，阅读记录修改策略。
                    ChapterTable chapterTable = catalogMap.get(1);
                    if (chapterTable != null) {
                        nextChapterId = chapterTable.getChapterId();
                    }
                    if (StringUtils.isNotEmpty(nextChapterId)) {
                        resetLastReadLocation(nextChapterId);
                    }
                    return;
                }
            }
            if (chaperIdToChapterNo.get(lastReadChapterId) != null) {
                int chapterNo = chaperIdToChapterNo.get(lastReadChapterId);
                int nextChapterNo = isNext ? chapterNo + 1 : chapterNo - 1;
                ChapterTable chapterTable = catalogMap.get(nextChapterNo);
                if (chapterTable != null) {
                    nextChapterId = chapterTable.getChapterId();
                }
                if (StringUtils.isNotEmpty(nextChapterId)) {
                    resetLastReadLocation(nextChapterId);
                }
            }
        }
    }

    /**
     * 修改缓存中PageContent的autoSub状态.
     *
     * @param autoSub 是否自动订阅.
     */
    @DebugLog
    public void resetPageContentCache(boolean autoSub) {
        BookTable bookTable = DataSourceManager.getSingleton().getLastReadRecord();
        if (bookTable != null) {
            String lastReadChapterId = bookTable.getLastReadChapter();
            int lastReadLocation = bookTable.getLastReadLocation();
            int chapterNo = DataSourceManager.getSingleton().getChapterNoById(lastReadChapterId);
            if (PagingManager.getSingleton().hasChapterPage(lastReadChapterId)) {
                //缓存中已经有排版分页内容.
                PageContent pageContent = PagingManager.getSingleton().getPageContent(lastReadChapterId, chapterNo, lastReadLocation,
                        false);
                pageContent.setAutoSub(autoSub);
            }
        }
    }

    /**
     * 重置最后阅读章节记录
     */
    public void resetLastReadLocation(String chapterId) {
        if (StringUtils.isNotEmpty(this.bookId)) {
            //粗略重新计算百分比.
            float percent = getPercentByChapterId(chapterId);
            int result = BookDao.getInstance().updateLastReadLocation(bookId, chapterId, 0, percent);
            LogUtils.info("写入阅读记录 result =" + result + " chapterId =" + chapterId + " percent =" + percent);
        }
    }

    /**
     * 根据章节ID粗略计算百分比
     *
     * @param chapterId
     * @return
     */
    public float getPercentByChapterId(String chapterId) {
        //粗略重新计算百分比.
        int count = getChapterCount();
        int chapterNo = getChapterNoById(chapterId);
        float percent = 0;
        if (chapterNo > 0 && count > 0) {
            percent = (chapterNo - 1) / (float) count;
        }
        return percent;
    }

    public void setChapterContentLoadListener(IChapterContentLoadListener iChapterContentLoadListener) {
        this.iChapterContentLoadListener = iChapterContentLoadListener;
    }


    /**
     * 刷新当前页内容.
     *
     * @param isPost post请求:订阅接口使用.
     */
    public synchronized void loadCurrentPage(final boolean isPost) {
        synchronized (singleton) {
            BookTable bookTable = getLastReadRecord();
            if (bookTable != null) {
                String lastReadChapterId = bookTable.getLastReadChapter();
                int lastReadLocation = bookTable.getLastReadLocation();
                if (StringUtils.isNotEmpty(bookId)) {
                    long startTime = System.currentTimeMillis();
                    if (StringUtils.isNotEmpty(lastReadChapterId)) {    //正常阅读。
                        ChapterTable curChapterTable = ChapterDao.getInstance().findChapterByChapterId(lastReadChapterId, bookId);

                        String chapterName = "";
                        if (curChapterTable != null) {
                            chapterName = curChapterTable.getName();
                        }
                        int chapterNo = DataSourceManager.getSingleton().getChapterNoById(lastReadChapterId);
                        LogUtils.debug("加载当前页，读取目录耗时 == " + (System.currentTimeMillis() - startTime) + " 毫秒");
                        if (PagingManager.getSingleton().hasChapterPage(lastReadChapterId)) {
                            //缓存中已经有排版分页内容.
                            PageContent pageContent = PagingManager.getSingleton().getPageContent(lastReadChapterId, chapterNo,
                                    lastReadLocation, false);

                            if (iChapterContentLoadListener != null) {
                                iChapterContentLoadListener.onLoadFinished(pageContent);
                                return;
                            }
                        }
                        mChapterContentLoaderThread = new ChapterContentLoader(bookId, lastReadChapterId, chapterName, lastReadLocation,
                                FileConstant.XSREADER_FILE_SUFFIX, FileConstant.ENCODING_UTF_8, false, iChapterContentLoadListener);
                        mChapterContentLoaderThread.setIsPost(isPost);
                        mChapterContentLoaderThread.start();
                    } else {
                        // TODO: 16/4/11 没有阅读历史记录，打开首页版权页面
                        PageContent pageContent = new PageContent();
                        pageContent.setBookType(ReadPageState.BOOKTYPE_COPYRIGHT);
                        pageContent.setChapterId("");
                        pageContent.setStartIndex(0);
                        pageContent.setPercent(0);
                        pageContent.setErrorCode(XSErrorEnum.CHAPTER_COPYRIGHT.getCode());

                        if (iChapterContentLoadListener != null) {
                            iChapterContentLoadListener.onLoadFinished(pageContent);
                            //打开首页版权后，缓存第一章节内容。
                            ChapterTable _chapterTable = DataSourceManager.getSingleton().getCatalogMap().get(1);
                            if (_chapterTable != null) {
                                mChapterContentLoaderThread = new ChapterContentLoader(bookId, _chapterTable.getChapterId(),
                                        _chapterTable.getName(), 0,
                                        FileConstant.XSREADER_FILE_SUFFIX, FileConstant.ENCODING_UTF_8, false, null);
                                mChapterContentLoaderThread.setIsPost(false);
                                mChapterContentLoaderThread.start();
                            }
                            return;
                        }
                    }

                }
            } else {
                LogUtils.error("书籍表格中没有响应的数据!");
            }
        }
    }

    /**
     * 下一页内容
     *
     * @return
     */
    public synchronized boolean loadNextPage() {
        //如果当前章节是正确的阅读状态，（非正常状态:未订阅，未登录，余额不足）
        if (isCurChapterHasLoaded()) {
            return loadPage(true);
        } else {
            return loadChapter(true);
        }

    }

    /**
     * 上一页内容
     */
    public synchronized boolean loadPrePage() {
        //如果当前章节是正确的阅读状态，（非正常状态:未订阅，未登录，余额不足）
        if (isCurChapterHasLoaded()) {
            return loadPage(false);
        } else {
            return loadChapter(false);
        }
    }

    /**
     * 判断当前章节是否缓存或者下载.
     *
     * @return
     */
    private boolean isCurChapterHasLoaded() {
        boolean hasCache = false;
        boolean hasFile = false;
        BookTable bookTable = getLastReadRecord();
        if (bookTable != null) {
            String chapterId = bookTable.getLastReadChapter();
            if (PagingManager.getSingleton().hasChapterPage(chapterId)) {
                hasCache = true;
            }
            String filePath = Utility.getChapterFilePath(bookId, chapterId, FileConstant.XSREADER_FILE_SUFFIX);
            if (new File(filePath).exists()) {
                hasFile = true;
            }
        }
        return hasFile && hasCache;
    }


    private synchronized boolean loadPage(boolean isNext) {
        //找到书籍的阅读记录.
        BookTable bookTable = getLastReadRecord();
        if (bookTable != null) {
            String lastReadChapterId = bookTable.getLastReadChapter();
            int lastReadLocation = bookTable.getLastReadLocation();
            LogUtils.debug("阅读记录： chapterId =" + lastReadChapterId + " lastReadLocation =" + lastReadLocation);
            if (StringUtils.isNotEmpty(lastReadChapterId)) {
                ChapterPage chapterPage = PagingManager.getSingleton().getChaperPage(lastReadChapterId);
                if (chapterPage != null) {
                    int location = PagingManager.getSingleton().findNewStartIndex(chapterPage, lastReadLocation, isNext);
                    if (PagingManager.LOAD_PRE_CHAPTER == location) {
                        //显示上一章最后一页
                        return loadChapter(false);
                    } else if (PagingManager.LOAD_NEXT_CHAPTER == location) {
                        //显示下一章第一页
                        return loadChapter(true);
                    } else {
                        bookTable.setLastReadLocation(location);
                        BookDao.getInstance().updateBook(bookTable);
                        ChapterTable curChapterTable = ChapterDao.getInstance().findChapterByChapterId(lastReadChapterId, bookId);
                        String chapterName = "";
                        int chapterNo = DataSourceManager.getSingleton().getChapterNoById(lastReadChapterId);
                        if (curChapterTable != null) {
                            chapterName = curChapterTable.getName();
                        }
                        if (PagingManager.getSingleton().hasChapterPage(lastReadChapterId)) {
                            PageContent pageContent = PagingManager.getSingleton().getPageContent(lastReadChapterId, chapterNo, location,
                                    false);
                            if (iChapterContentLoadListener != null) {
                                iChapterContentLoadListener.onLoadFinished(pageContent);
                                return true;
                            }
                        }
                        //如果加载下一页缓存不存在,重新加载.
                        mChapterContentLoaderThread = new ChapterContentLoader(bookId, lastReadChapterId, chapterName, location,
                                FileConstant.XSREADER_FILE_SUFFIX, FileConstant.ENCODING_UTF_8, !isNext, iChapterContentLoadListener);
                        mChapterContentLoaderThread.start();
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 下一章(不用于按钮跳转。)
     *
     * @param isNext
     * @return true:内容加载结束；false：内容加载未结束.
     */
    private synchronized boolean loadChapter(boolean isNext) {
        synchronized (singleton) {
            String curChapterId = "";
            BookTable bookTable = getLastReadRecord();
            ChapterTable chapterTable = null;
            if (bookTable != null) {
                curChapterId = bookTable.getLastReadChapter();
            }
            boolean hasFindNextChapter = false;
            if (StringUtils.isEmpty(curChapterId)) {
                LogUtils.debug("找不到最后的阅读章节");
                if (isNext && StringUtils.isEmpty(curChapterId) && catalogMap != null && catalogMap.size() > 0) {
                    chapterTable = catalogMap.get(1);
                    if (chapterTable != null) {
                        hasFindNextChapter = true;
                    }
                } else if (!isNext && StringUtils.isEmpty(curChapterId)) {
                    PageContent pageContent = new PageContent();
                    pageContent.setErrorCode(isNext ? XSErrorEnum.CHAPTER_LAST.getCode() : XSErrorEnum.CHAPTER_FIRST.getCode());
                    iChapterContentLoadListener.onLoadFinished(pageContent);
                    return false;
                } else {
                    return false;
                }
            }
            if (!hasFindNextChapter && catalogMap != null && catalogMap.size() > 0 && chaperIdToChapterNo != null && chaperIdToChapterNo
                    .size() > 0 &&
                    chaperIdToChapterNo.get(curChapterId) != null) {
                int curChapterNo = chaperIdToChapterNo.get(curChapterId);
                int nextChapterNo = isNext ? curChapterNo + 1 : curChapterNo - 1;
                chapterTable = catalogMap.get(nextChapterNo);
                if (nextChapterNo < 1 && chapterTable == null) {
                    // TODO: 16/4/11 没有阅读历史记录，打开首页版权页面
                    PageContent pageContent = new PageContent();
                    pageContent.setBookType(ReadPageState.BOOKTYPE_COPYRIGHT);
                    pageContent.setChapterId("");
                    pageContent.setStartIndex(0);
                    pageContent.setPercent(0);
                    pageContent.setErrorCode(XSErrorEnum.CHAPTER_COPYRIGHT.getCode());
                    if (iChapterContentLoadListener != null) {
                        iChapterContentLoadListener.onLoadFinished(pageContent);
                        return true;
                    }
                }
            }
            if (chapterTable != null) {
                //添加这句话，修改在章节跳转的时候，loading状态的章节名称未进行修改.
                DataSourceManager.getSingleton().resetLastReadLocation(isNext);
                mChapterContentLoaderThread = new ChapterContentLoader(chapterTable.getBookId(), chapterTable.getChapterId(),
                        chapterTable.getName(), 0, FileConstant.XSREADER_FILE_SUFFIX, FileConstant.ENCODING_UTF_8, !isNext,
                        iChapterContentLoadListener);
                mChapterContentLoaderThread.start();
            } else {
                if (iChapterContentLoadListener != null) {
                    PageContent pageContent = new PageContent();
                    pageContent.setErrorCode(isNext ? XSErrorEnum.CHAPTER_LAST.getCode() : XSErrorEnum.CHAPTER_FIRST.getCode());
                    iChapterContentLoadListener.onLoadFinished(pageContent);
                    return false;
                }
            }
        }
        //// TODO: 2016/1/9 预加载下一个章节.
        preLoadChapter(isNext);
        return true;
    }

    /**
     * 预加载下一章
     *
     * @param isNext
     * @return
     */
    private synchronized void preLoadChapter(boolean isNext) {
        ChapterTable chapterTable = null;
        String curChapterId = "";
        BookTable bookTable = getLastReadRecord();
        if (bookTable != null) {
            curChapterId = bookTable.getLastReadChapter();
        }
        boolean hasFindNextChapter = false;
        if (StringUtils.isEmpty(curChapterId)) {
            LogUtils.debug("找不到最后的阅读章节");
            if (isNext && StringUtils.isEmpty(curChapterId) && catalogMap != null && catalogMap.size() > 0) {
                chapterTable = catalogMap.get(1);
                if (chapterTable != null) {
                    hasFindNextChapter = true;
                }
            } else {
                return;
            }
        }
        if (!hasFindNextChapter && catalogMap != null && catalogMap.size() > 0 && chaperIdToChapterNo != null && chaperIdToChapterNo.get
                (curChapterId) != null) {
            int curChapterNo = chaperIdToChapterNo.get(curChapterId);
            int nextChapterNo = isNext ? curChapterNo + 1 : curChapterNo - 1;
            if (nextChapterNo > 0) {
                chapterTable = catalogMap.get(nextChapterNo);
            }
        }
        if (chapterTable != null) {
            LogUtils.info("预加载章节" + chapterTable.getName() + "| id =" + chapterTable.getChapterId() + "| words =" + chapterTable
                    .getTotalWords());
            mChapterContentLoaderThread = new ChapterContentLoader(chapterTable.getBookId(), chapterTable.getChapterId(), chapterTable
                    .getName(), 0, FileConstant.XSREADER_FILE_SUFFIX, FileConstant.ENCODING_UTF_8, false, null);
            mChapterContentLoaderThread.start();
        }
    }

}
