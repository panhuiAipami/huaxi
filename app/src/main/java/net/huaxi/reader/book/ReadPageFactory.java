package net.huaxi.reader.book;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.book.datasource.IChapterContentLoadListener;
import net.huaxi.reader.book.paging.PageContent;
import net.huaxi.reader.book.render.BookContentRender;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterContentDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONException;
import org.json.JSONObject;

import hugo.weaving.DebugLog;

/**
 * 阅读页生产类
 * Created by dongyongjie
 * 2015/12/5.
 */
public class ReadPageFactory {

    private static ReadPageFactory singleton;
    public volatile boolean isRunning = false;
    BookContentRender mBookContentRender;
    private IBookContentLoadedListener mBookContentLoadedListener;
    private Canvas mOldCanvas;
    private Canvas mNewCanvas;
    //记录服务端返回的订阅状态
    private volatile boolean lastAutoSub = true;
    //记录客户端的订阅状态(红色checkbox的状态)
    private volatile boolean autoSub = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBooleanIsDingyue();

    private ReadPageFactory() {
        mBookContentRender = new BookContentRender();
    }

    public static synchronized ReadPageFactory getSingleton() {
        if (singleton == null) {
            singleton = new ReadPageFactory();
        }
        return singleton;
    }

    public Canvas getOldCanvas() {
        return mOldCanvas;
    }

    public void setOldCanvas(Canvas mOldCanvas) {
        this.mOldCanvas = mOldCanvas;
    }

    public Canvas getNewCanvas() {
        return mNewCanvas;
    }

    public void setNewCanvas(Canvas mNewCanvas) {
        this.mNewCanvas = mNewCanvas;
    }

    public void setBackgoundBitmap(Bitmap bitmap) {
        if (mBookContentRender != null) {
            mBookContentRender.setBackgroundBitmap(bitmap);
        }
    }

    /**
     * 改变客户端的订阅状态.(checkbox的)
     */
    public void changeAutoSub() {

        LogUtils.debug("here_1");
        if (mBookContentRender != null) {
            LogUtils.debug("here_2");
            autoSub = !autoSub;
            LogUtils.debug("autsub_here"+autoSub);
            DataSourceManager.getSingleton().resetPageContentCache(autoSub);
            LogUtils.debug("____55555");
            refreshPage();
            SharedPreferenceUtil.getInstanceSharedPreferenceUtil().setBooleanIsDingyue(autoSub);

            if (autoSub){
                LogUtils.debug("here_3");
                UMEventAnalyze.countEvent(AppContext.context(),UMEventAnalyze.READPAGE_AUTOORDER_SELECT);
            }
        }
    }

    /**
     * 判断自动订阅状态是否
     *
     * @return boolean
     */
    public boolean hasChangeAutoSub() {
        return lastAutoSub != autoSub;
    }

    /**
     * 是否自动订阅.
     *
     * @return boolean
     */
    public boolean isAutoSub() {
        return SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBooleanIsDingyue();
    }

    public void setAutoSub(boolean autoSub) {
        this.autoSub = autoSub;
    }

    public void setLastAutoSub(boolean lastAutoSub) {
        this.lastAutoSub = lastAutoSub;
    }

    /**
     * 设置内容加载回调接口.
     *
     * @param listener 回调接口
     */
    public void setBookContentLoadedListener(IBookContentLoadedListener listener) {
        this.mBookContentLoadedListener = listener;
    }

    /**
     * 是否为有效行为
     *
     * @return boolean
     */
    public boolean isValidAction() {
        return mBookContentLoadedListener != null;
    }

    public synchronized void drawStateLoading(Canvas canvas) {
        if (isValidAction()) {
            BookTable bookTable = DataSourceManager.getSingleton().getLastReadRecord();
            float percent = 0;
            String name = "";
            if (bookTable != null) {
                ChapterTable chapterTable = ChapterDao.getInstance().findChapterByChapterId(bookTable.getLastReadChapter(), bookTable
                        .getBookId());
                if (chapterTable != null) {
                    name = chapterTable.getName();
                    int count = DataSourceManager.getSingleton().getChapterCount();
                    int chapterNo = DataSourceManager.getSingleton().getChapterNoById(chapterTable.getChapterId());
                    if (count > 0 && chapterNo > 0) {
                        percent = (chapterNo - 1) / (float) count;
                    }
                }
            }
            if (mBookContentRender != null) {
                mBookContentRender.setChapterName(name);
                mBookContentRender.drawStateLoading(canvas);
            }
            if (mBookContentLoadedListener != null) {
                mBookContentLoadedListener.onLoading(percent);
            }
        }
    }

    public synchronized void loadFailed(int errorCode) {
        if (mBookContentLoadedListener != null) {
            mBookContentRender.drawStateErrorOrLogin(getNewCanvas(), errorCode);
            mBookContentLoadedListener.onLoadContentFiled(errorCode);
        }
    }

    /**
     * 排版和绘画
     *
     * @param pageContent page content
     * @param canvas      画布
     * @param isFinished  内容加载是否结束.
     * @return boolean
     */
    @DebugLog
    private synchronized boolean composingAndRender(final PageContent pageContent, final Canvas canvas, final boolean isFinished) {
        //// FIXME: 2015/12/15 错误操作
        if (pageContent == null) {
            return false;
        }

        LogUtils.info(" code " + pageContent.getErrorCode() + ", chaptername = " + pageContent.getChapterName());
        if (pageContent.getPageNo() == 1) {     //章节的第一页标题显示书名.
            mBookContentRender.setChapterName(DataSourceManager.getSingleton().getBookName());
        } else {
            mBookContentRender.setChapterName(pageContent.getChapterName());
        }
//        pageContent.setErrorCode(XSErrorEnum.CHAPTER_NOT_SUBSCRIBE.getCode());
        try {
            if (XSErrorEnum.SUCCESS.getCode() == pageContent.getErrorCode()) {
                mBookContentRender.drawStateNormalCanvas(canvas, pageContent.getLines());
                if (mBookContentLoadedListener != null && isFinished) {
                    mBookContentLoadedListener.onLoadContentCompleted(pageContent);
                }
                return true;
            } else if (XSErrorEnum.CHAPTER_COPYRIGHT.getCode() == pageContent.getErrorCode()) {     //版权页面
                mBookContentRender.drawStateCopyRight(canvas);
                if (mBookContentLoadedListener != null && isFinished) {
                    mBookContentLoadedListener.onLoadContentCompleted(pageContent);
                }
                return true;
            } else if (XSErrorEnum.CHAPTER_NOT_SUBSCRIBE.getCode() == pageContent.getErrorCode()) { //未订阅
                //现价大于等于余额的情况下
                if(BookContentRender.p>BookContentRender.b&&BookContentRender.p>BookContentRender.sum){
                    XSErrorEnum.CHAPTER_SHORT_BALANCE.setCode(10263);
                    pageContent.setErrorCode(10263);
                    if(XSErrorEnum.CHAPTER_SHORT_BALANCE.getCode() == pageContent.getErrorCode()){
                        return false;

                    }
                }else {
                    //现价小于余额的情况下
                    getBalanceAndDraw(canvas, pageContent, isFinished);
                }

                return false;
            } else if (XSErrorEnum.CHAPTER_SHORT_BALANCE.getCode() == pageContent.getErrorCode()) { //余额不足
                getBalanceAndDraw(canvas, pageContent, isFinished);
                LogUtils.debug("loca 2");
                return false;
            } else if (XSErrorEnum.CHAPTER_DOWNLOAD_FAILED.getCode() == pageContent.getErrorCode()) {  //下载失败
                mBookContentRender.drawStateErrorOrLogin(canvas, ReadPageState.BOOKTYPE_ERROR);
            } else if (XSErrorEnum.CHAPTER_AUTO_SUBSCRIBE_FAILED.getCode() == pageContent.getErrorCode()) { //自动订阅失败
                mBookContentRender.drawStateErrorOrLogin(canvas, ReadPageState.BOOKTYPE_ERROR);
            } else if (XSErrorEnum.NETWORK_UNAVAILABLE.getCode() == pageContent.getErrorCode()) {  //网络错误.
                mBookContentRender.drawStateErrorOrLogin(canvas, ReadPageState.BOOKTYPE_NONE_NETWORK);
            } else if (XSErrorEnum.CHAPTER_NEED_LOGIN.getCode() == pageContent.getErrorCode()) {
                mBookContentRender.drawStateLoginCancas(canvas, pageContent);
            } else if (XSErrorEnum.CHAPTER_FIRST.getCode() == pageContent.getErrorCode() || XSErrorEnum.CHAPTER_LAST.getCode() ==
                    pageContent.getErrorCode()) {
                LogUtils.debug("第一页和最后一页，不渲染新界面");
            } else {
                mBookContentRender.drawStateErrorOrLogin(canvas, ReadPageState.BOOKTYPE_ERROR);
            }
            if (mBookContentLoadedListener != null && isFinished) {
                mBookContentLoadedListener.onLoadContentFiled(pageContent.getErrorCode());
            }
        } catch (Exception e) {
            ReportUtils.reportError(e);
        }
        return false;
    }

    /**
     * 获取余额，然后渲染结果。
     *
     * @param canvas canvas
     * @param pageContent page content
     * @param isFinished  内容加载是否结束.
     */
    private synchronized void getBalanceAndDraw(final Canvas canvas, final PageContent pageContent, final boolean isFinished) {
        if (NetUtils.checkNet() == NetType.TYPE_NONE) {
            mBookContentRender.drawStatePayCancas(canvas, pageContent, 0,0, isAutoSub());
            LogUtils.debug("pageContent....1"+pageContent.getPrice());
        } else {
            String url = URLConstants.PAY_INFO + "?1=1" + CommonUtils.getPublicGetArgs();
//            Log.i("dongyongjie", "getBalanceAndDraw: 请求路径==="+url);
            GetRequest payInfoRequest = new GetRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int coins = 0;
                    int petals = 0;
                    if (ResponseHelper.isSuccess(response)) {
                        JSONObject jsonObject = ResponseHelper.getVdata(response);
                        try {
                            JSONObject data = jsonObject.getJSONObject(XSKEY.USER_INFO.KEY_INFO);
//                            Log.i("qqq", "onResponse: 用户信息==="+data.toString());
                            if (data != null) {
                                coins = data.optInt(XSKEY.USER_INFO.COIN, 0);
                                petals = data.optInt(XSKEY.USER_INFO.PETALS,0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mBookContentRender.drawStatePayCancas(canvas, pageContent, coins,petals, isAutoSub());
                        LogUtils.debug("pageContent....2"+pageContent.getPrice());
                        UMEventAnalyze.countEvent(AppContext.context(),UMEventAnalyze.READPAGE_ORDER);
                        if (mBookContentLoadedListener != null && isFinished) {
                            mBookContentLoadedListener.onLoadContentFiled(pageContent.getErrorCode());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ReportUtils.reportError(error);
                    LogUtils.debug("pageContent....3"+pageContent.getPrice());
                    mBookContentRender.drawStatePayCancas(canvas, pageContent, 0,0, isAutoSub());
                    if (mBookContentLoadedListener != null && isFinished) {
                        mBookContentLoadedListener.onLoadContentFiled(pageContent.getErrorCode());
                    }
                }
            },"1.1");
            RequestQueueManager.addRequest(payInfoRequest);
        }
    }

    /**
     * 订阅当前章节.
     */
    public synchronized void pay() {
        if (isValidAction()) {
            DataSourceManager.getSingleton().setChapterContentLoadListener(new IChapterContentLoadListener() {
                @Override
                public void onLoadFinished(PageContent pageContent) {
                    composingAndRender(pageContent, getNewCanvas(), true);

                }

                @Override
                public void onLoading() {
                    drawStateLoading(getNewCanvas());
                }
            });
            DataSourceManager.getSingleton().loadCurrentPage(true);
            return;
        }
        LogUtils.error("没有发现回调接口，视为无效加载行为");
    }

    /**
     * 加载内容完成
     * 刷新当前页
     */
    public synchronized void refreshPage() {
        if (isValidAction()) {
            DataSourceManager.getSingleton().setChapterContentLoadListener(new IChapterContentLoadListener() {
                @Override
                public void onLoadFinished(PageContent pageContent) {
                    // 解决由于目录更新，导致的数据库阅读记录未及时更新问题。
                    updateBookReadLocation(pageContent);
                    composingAndRender(pageContent, getNewCanvas(), true);
                    LogUtils.debug("loca_pageContent_freshpage:"+pageContent.toString());
                }

                @Override
                public void onLoading() {
                    drawStateLoading(getNewCanvas());
                }
            });
            DataSourceManager.getSingleton().loadCurrentPage(false);
            return;
        }
        LogUtils.error("没有发现回调接口，视为无效加载行为");
    }

    /**
     * 当前页(禁用该方法).
     */
    public synchronized void loadCurrentPage() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        if (isValidAction()) {
            DataSourceManager.getSingleton().setChapterContentLoadListener(new IChapterContentLoadListener() {
                @Override
                public void onLoadFinished(PageContent pageContent) {
                    composingAndRender(pageContent, getOldCanvas(), false);
                    LogUtils.debug("loca_pageContent_CurrentPage:"+pageContent.toString());
                    isRunning = false;
                }

                @Override
                public void onLoading() {
//                    drawStateLoading(getNewCanvas());
                }

            });
            DataSourceManager.getSingleton().loadCurrentPage(false);
            return;
        }
        LogUtils.error("没有发现回调接口，视为无效加载行为");
    }

    /**
     * 下一页
     */
    public synchronized boolean loadNextPage() {
        if (isValidAction()) {
            DataSourceManager.getSingleton().setChapterContentLoadListener(new IChapterContentLoadListener() {
                @Override
                public void onLoadFinished(PageContent pageContent) {
                    updateBookReadLocation(pageContent);
                    composingAndRender(pageContent, getNewCanvas(), true);
                    LogUtils.debug("loca_pageContent_NextPage:"+pageContent.toString());
                }

                @Override
                public void onLoading() {
                    drawStateLoading(getNewCanvas());
                }
            });
            return DataSourceManager.getSingleton().loadNextPage();
        }
        return false;
    }


    /**
     * 上一页
     *
     * @return true:内容存在，false:内容不存在.
     */
    public synchronized boolean loadPrePage() {
        if (isValidAction()) {
            DataSourceManager.getSingleton().setChapterContentLoadListener(new IChapterContentLoadListener() {
                @Override
                public void onLoadFinished(PageContent pageContent) {
                    updateBookReadLocation(pageContent);
                    composingAndRender(pageContent, getNewCanvas(), true);
                    LogUtils.debug("loca_pageContent_PrePage:"+pageContent.toString());
                }

                @Override
                public void onLoading() {
                    drawStateLoading(getNewCanvas());
                }
            });
            return DataSourceManager.getSingleton().loadPrePage();
        }
        return false;
    }

    /**
     * 下一章（第一页）
     */
    public synchronized void loadNextChapter() {
        //重置阅读记录
        DataSourceManager.getSingleton().resetLastReadLocation(true);
        LogUtils.debug("____11111");
        refreshPage();
    }

    /**
     * 上一章(最后一页)
     */
    public synchronized void loadPreChapter() {
        //重置阅读记录
        DataSourceManager.getSingleton().resetLastReadLocation(false);
        LogUtils.debug("____22222");
        refreshPage();
    }

    /**
     * 跳转到某一章
     *
     * @param chapterId 章节ID
     */
    public synchronized void jumpToChapter(String chapterId) {
        //重置阅读记录
        DataSourceManager.getSingleton().resetLastReadLocation(chapterId);
        LogUtils.debug("____33333");
        refreshPage();
    }

    /**
     * 跳转到某一章
     *
     * @param percent 百分比
     */
    public synchronized void jumpToChapterByPercent(int percent) {
        String chapterId = DataSourceManager.getSingleton().getChapterByNo(percent);
        if (StringUtils.isNotEmpty(chapterId)) {
            DataSourceManager.getSingleton().resetLastReadLocation(chapterId);
            LogUtils.debug("____44444");
            refreshPage();
        }
    }


    /**
     * 更新章节阅读记录.
     *
     * @param pageContent page content
     */
    private void updateBookReadLocation(PageContent pageContent) {
        //如果翻到最后一页，禁止置空阅读记录。
        if (pageContent != null && XSErrorEnum.CHAPTER_LAST.getCode() != pageContent.getErrorCode()) {
            int result = BookDao.getInstance().updateLastReadLocation(DataSourceManager.getSingleton().getBookId(), pageContent
                    .getChapterId(), pageContent.getStartIndex(), pageContent.getPercent());
            //更新章节阅读状态
            ChapterContentDao.getInstance().updateReadState(DataSourceManager.getSingleton().getBookId(), pageContent.getChapterId(), 1);
            LogUtils.info("写入阅读记录 result =" + result + " chapterId =" + pageContent.getChapterId() + " Name =" + pageContent
                    .getChapterName() + " index =" + pageContent.getStartIndex() + " percent =" + pageContent.getPercent());
        } else {
            LogUtils.error("写入阅读记录异常,");
        }
    }

    /**
     * 清空Canvas缓存
     *
     * @param canvas
     */
    public void clearCanvasCache(Canvas canvas) {
        if (mBookContentRender != null) {
            mBookContentRender.drawEmptyView(canvas);
        }
    }


}
