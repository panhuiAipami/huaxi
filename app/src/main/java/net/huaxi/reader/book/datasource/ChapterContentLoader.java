package net.huaxi.reader.book.datasource;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.FileUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.book.datasource.model.ChapterPage;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.download.Task;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.util.HashMap;
import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.book.BookChapterInfo;
import net.huaxi.reader.book.ReadPageFactory;
import net.huaxi.reader.book.datasource.model.XsFile;
import net.huaxi.reader.book.paging.PageContent;
import net.huaxi.reader.book.paging.PagingManager;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.https.download.TaskManagerDelegate;
import net.huaxi.reader.https.download.TaskStateEnum;
import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;
import net.huaxi.reader.util.EncodeUtils;

/**
 * 章节内容加载
 * <p/>
 * Created by taoyingfeng on 2015/12/3.
 */
public class ChapterContentLoader extends Thread {

    private String mBookId;
    private String mChapterId;
    private String mChapterName;
    private int mStartIndex;
    private String mSuffix;
    private String mEncoding;
    private boolean locateLastChapter;
    private IChapterContentLoadListener mChapterContentLoadListener;
    private volatile boolean mIsPost = false;  //请求方式.
    private long startTime = 0;


    /**
     * 异步加载章节内容
     *
     * @param bookId            书籍ID
     * @param chapterId         章节ID
     * @param chapterName       章节名称
     * @param startIndex        开始索引
     * @param suffix            文件后缀名
     * @param encoding          编码格式
     * @param locateLastChapter true：定位到上一章最后一页. false:根据startIndex确定Location坐标.
     * @param loadListener
     */
    public ChapterContentLoader(String bookId, String chapterId, String chapterName, int startIndex, String suffix, String encoding,
                                boolean locateLastChapter, IChapterContentLoadListener loadListener) {
        this.mBookId = bookId;
        this.mChapterId = chapterId;
        this.mChapterName = chapterName;
        this.mStartIndex = startIndex;
        this.mSuffix = suffix;
        this.mEncoding = encoding;
        this.locateLastChapter = locateLastChapter;
        this.mChapterContentLoadListener = loadListener;
        setPriority(MAX_PRIORITY);
        ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_READING);
    }

    /**
     * 生产章节内容;
     *
     * @return 未排版内容
     */
    private static BookChapterInfo produceChapterContentFormNet(String bookId, String chapterId, String chapterName, int startIndex,
                                                                String encoding, String suffix, int netCode) {
        BookChapterInfo chapterInfo = null;
        if (netCode == XSErrorEnum.SUCCESS.getCode()) {
            chapterInfo = produceChapterContent(bookId, chapterId, chapterName, startIndex, encoding, suffix);
        } else {
            chapterInfo = new BookChapterInfo();
            chapterInfo.setBookId(bookId);
            chapterInfo.setChapterId(chapterId);
            chapterInfo.setChapterName(chapterName);
            chapterInfo.setStartIndex(startIndex);
            chapterInfo.setEncoding(encoding);
            chapterInfo.setErrorCode(netCode);
        }
        return chapterInfo;
    }

    /**
     * 生产章节对象
     */
    private static BookChapterInfo produceChapterContent(String bookId, String chapterId, String chapterName, int startIndex, String
            encoding, String suffix) {
        BookChapterInfo chapterInfo = new BookChapterInfo();
        chapterInfo.setBookId(bookId);
        chapterInfo.setChapterId(chapterId);
        chapterInfo.setChapterName(chapterName);
        chapterInfo.setStartIndex(startIndex);
        chapterInfo.setEncoding(encoding);
        XsFile xsfile = readContentInfo(bookId, chapterId, suffix);
        if (xsfile != null && xsfile.getState() == XSErrorEnum.SUCCESS.getCode() && xsfile.getData() != null && xsfile.getData().length >
                0) {
            byte[] bytes = getByteFormat(xsfile.getData(), chapterName);
            chapterInfo.setContentBytes(bytes);
            chapterInfo.setErrorCode(XSErrorEnum.SUCCESS.getCode());
        } else {
            if (xsfile != null) {
                chapterInfo.setErrorCode(xsfile.getState());
            } else {
                chapterInfo.setErrorCode(XSErrorEnum.CHAPTER_FILE_PARSE_FILED.getCode());
            }
        }
        xsfile = null;
        return chapterInfo;
    }

    /**
     * 读取章节内容
     */
    private static XsFile readContentInfo(String bookId, String chapterId, String fileSuffix) {
        FileReaderUtil mReaderUtil = new FileReaderUtil();
        String filepath = Utility.getChapterFilePath(bookId, chapterId, fileSuffix);
        XsFile xsFile = new XsFile();
        int mFileLength;  //文件长度
        try {
            mReaderUtil.open(filepath);
            mFileLength = (int) mReaderUtil.getTextLength();
            MappedByteBuffer mByteBuffer = mReaderUtil.getMappedByteBuffer(0, mFileLength);
            byte[] readByteArray = new byte[mFileLength];
            mByteBuffer.get(readByteArray, 0, readByteArray.length);
            xsFile = XsFileParse.parseXsFile(readByteArray);
            readByteArray = null;
        } catch (FileNotFoundException e) {
            LogUtils.error(e.getMessage(), e);
            if (xsFile != null) {
                xsFile.setState(XSErrorEnum.CHAPTER_FILE_NOT_EXIST.getCode());
            }
        } catch (IOException e) {
            LogUtils.error(e.getMessage(), e);
            if (xsFile != null) {
                xsFile.setState(XSErrorEnum.CHAPTER_FILE_PARSE_FILED.getCode());
            }
        } finally {
            try {
                mReaderUtil.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO: 2016/1/21 删除无效的章节文件.
            if (xsFile.getState() != XSErrorEnum.SUCCESS.getCode()) {
                FileUtils.deleteFile(new File(filepath));
            }
        }
        return xsFile;
    }

    /**
     * 过滤数组中的特殊字符.(给章节内容添加标题)
     *
     * @param data
     * @return
     */
    private static byte[] getByteFormat(byte[] data, String chapterName) {
        if (data != null && data.length > 0) {
            try {
                //// TODO: 2016/1/26 替换文件流中的特殊字符<br>,</p><p>,<p>,</p>
                String temp = new String(data, "UTF-8");
                Log.d("temp_format", "getByteFormat: "+temp);
                String format = temp.replaceAll("\\{br\\}", "").replaceAll("\\{\\/p\\}\\{p\\}", "\r\n").replaceAll("\\{\\/p\\}", "")
                        .replaceAll("\\{p\\}", "");
                Log.d("temp_format", "getByteFormat: "+format);
                boolean hasChapterName = false;
                if (!TextUtils.isEmpty(format)) {
                    int maxlength = Math.min(format.length(), 18);
                    String tempStr = format.substring(0, maxlength);
                    if (Utility.isTitle(tempStr)) {  //如果章节内容有标题.
                        hasChapterName = true;
                    }
                    if (!TextUtils.isEmpty(chapterName) && !hasChapterName) {
                        format = chapterName + "\r\n" + format;
                    }
                    //// FIXME: 16/4/11 添加章节尾部评论
//                    format += "\n" + AppContext.getInstance().getString(R.string.readpage_comment_mark);
                }
                return format.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            ReportUtils.reportError(new Throwable("格式化章页面内容的特殊字符，发现章节内容为空!"));
        }
        return null;
    }

    /**
     * 设置Post请求方式.
     *
     * @param isPost
     */
    public void setIsPost(boolean isPost) {
        this.mIsPost = isPost;
    }

    @Override
    public void run() {
        /******内容加载和解析是最耗时的操作，采用线程执行*******/
        loadContent(mBookId, mChapterId, mChapterName, mStartIndex, mSuffix, mEncoding, locateLastChapter, mChapterContentLoadListener);
    }

    /**
     * 获取章节内容
     *
     * @param bookId       书籍ID
     * @param chapterId    章节ID
     * @param chapterName  章节名称
     * @param startIndex   阅读开始位置
     * @param suffix       文件后缀名(xsc)
     * @param encoding     编码格式
     * @param loadListener 回调监听
     */
    private void loadContent(final String bookId, final String chapterId, final String chapterName, final int startIndex, final String
            suffix, final String encoding, final boolean locateLastChapter, final IChapterContentLoadListener loadListener) {
        LogUtils.debug("正在加载 bookId =" + bookId + " chapterId =" + chapterId + " chapterName =" + chapterName);
        String filePath = Utility.getChapterFilePath(bookId, chapterId, suffix);
        LogUtils.debug("filePath:"+filePath);
        long startTime = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(filePath)) {
            //本地读取
            File file = new File(filePath);
            if (file.exists() && file.length() > XsFileParse.HEADER_LENGTH) {
                //读取文件内容，并进行返回;
                BookChapterInfo chapterInfo = produceChapterContent(bookId, chapterId, chapterName, startIndex, encoding, suffix);
                if (chapterInfo != null) {
                    PagingManager.getSingleton().Paging(chapterId, chapterName, chapterInfo.getContentBytes(), encoding, ReadPageState
                            .BOOKTYPE_NORMAL);
                    int chapterNo = DataSourceManager.getSingleton().getChapterNoById(chapterId);
                    PageContent pageContent = PagingManager.getSingleton().getPageContent(chapterId, chapterNo, startIndex,
                            locateLastChapter);
//                    LogUtils.debug(" 内容加载和排版耗时 = " + (System.currentTimeMillis() - startTime) + " 毫秒");
                    if (loadListener != null) {
                        actionCount(bookId, chapterId);
                        loadListener.onLoadFinished(pageContent);
                    }
                    chapterInfo = null;
                }

            } else {
                if (NetUtils.checkNet() == NetType.TYPE_NONE) {
                    if (loadListener != null) {
                        //当前网络不可用
                        loadListener.onLoadFinished(PagingManager.producePageContentByErrorCode(chapterId, chapterName, XSErrorEnum
                                .NETWORK_UNAVAILABLE.getCode(), ReadPageState.BOOKTYPE_NONE_NETWORK));
                    }
                    return;
                }
                if (loadListener != null) {
                    loadListener.onLoading();
                }
                Request<JSONObject> request = null;
                if (mIsPost) {
                    Map<String, String> pMap = new HashMap<String, String>();
                    pMap.put(XSKEY.READER_CHAPTER.BOOKID, EncodeUtils.encodeString_UTF8(bookId));
                    startTime = System.currentTimeMillis();
                    pMap.put(XSKEY.READER_CHAPTER.CHAPTERID, EncodeUtils.encodeString_UTF8(chapterId));
                    pMap.putAll(CommonUtils.getPublicPostArgs());
                    LogUtils.debug("++++++++++++++ do pay interface +++++++++++++++");
                    request = new PostRequest(URLConstants.READPAGE_READ_CHAPTER_POST, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            onMyResponse(response, bookId, chapterId, chapterName, startIndex, suffix, encoding, locateLastChapter,
                                    loadListener);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            onMyErrorResponse(error, chapterId, chapterName, loadListener);
                        }
                    }, pMap, "1.2");
                } else {
                    final String url = String.format(URLConstants.READPAGE_READ_CHAPTER,EncodeUtils.encodeString_UTF8(bookId), EncodeUtils.encodeString_UTF8(chapterId));
                    request = new GetRequest(url + CommonUtils.getPublicGetArgs(),
                            new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            onMyResponse(response, bookId, chapterId, chapterName, startIndex, suffix, encoding, locateLastChapter,
                                    loadListener);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            onMyErrorResponse(error, chapterId, chapterName, loadListener);
                        }
                    }, "1.2");
                }
                RequestQueueManager.addRequest(request);
            }
        }
    }

    private void onMyResponse(JSONObject response, String bookId, String chapterId, String chapterName, int startIndex, String suffix,
                              String encoding, boolean locationLastChapter, IChapterContentLoadListener loadListener) {
        UMEventAnalyze.intervalEvent(AppContext.context(), UMEventAnalyze.ONLINE_READING_COST, System.currentTimeMillis() - startTime);
        int errorId = ResponseHelper.getErrorId(response);
        JSONObject vData = ResponseHelper.getVdata(response);
        JSONObject data = null;
        boolean hasSubed = false;
        boolean hasJsonData = false;
         if (ResponseHelper.hasSubed(response)) {
            hasSubed = true;
        }
        try {
            if (vData != null) {
                JSONArray array = vData.getJSONArray(XSKEY.READER_CHAPTER.KEY_LIST);
                if (array != null && array.length() > 0) {
                    data = array.getJSONObject(0);
                }
            }
        } catch (JSONException e) {
            ReportUtils.reportError(e);
        }
        if (data != null) {
            hasJsonData = true;
        }
        if (hasSubed && hasJsonData) {
            handleDownloadTask(data, bookId, chapterId, chapterName, startIndex, suffix, encoding, locationLastChapter, loadListener);
            LogUtils.debug("There....");
        } else {

            handleException(errorId, data, chapterId, chapterName, encoding, loadListener);
        }
    }

    private void onMyErrorResponse(VolleyError error, String chapterId, String chapterName, IChapterContentLoadListener loadListener) {
        UMEventAnalyze.intervalEvent(AppContext.context(), UMEventAnalyze.ONLINE_READING_COST, System.currentTimeMillis() - startTime);
        ReportUtils.reportError(error);
        if (loadListener != null) {
            //当前网络发生异常!
            loadListener.onLoadFinished(PagingManager.producePageContentByErrorCode(chapterId, chapterName, XSErrorEnum.NETWORK_NUKONWN
                    .getCode(), ReadPageState.BOOKTYPE_ERROR));
        }
    }

    /**
     * 解析URL,启动下载任务
     */
    private void handleDownloadTask(JSONObject data, final String bookId, final String chapterId, final String chapterName, final int
            startIndex, final String suffix, final String encoding, final boolean locateLastChapter, final IChapterContentLoadListener
                                            loadListener) {
        //网络读取
        String path = Utility.getCacheRootPath() + File.separator + mBookId + File.separator;
        String downUrl = "";
        String bId = "";
        String cId = "";
        String cptTextId = "";
        if (data != null) {
            downUrl = data.optString(XSKEY.READER_CHAPTER.REQ_URL, "");
            bId = data.optString(XSKEY.READER_CHAPTER.BOOKID, "");
            cId = data.optString(XSKEY.READER_CHAPTER.CHAPTERID, "");
            cptTextId = data.optString(XSKEY.READER_CHAPTER.CPTTEXTID, "");
        }
        if (StringUtils.isEmpty(downUrl)) {
            String errorMsg = String.format(AppContext.context().getString(R.string.readpage_content_download_url_empty), bookId,
                    chapterId);
            ReportUtils.reportError(new Throwable(errorMsg));
        }
        //// TODO: 2016/3/5 此处章节下载不需要添加公共参数。
        Task task = new Task(chapterId, chapterId, downUrl, suffix, path, new ITaskStateChangeListener() {
            @Override
            public void onStateChanged(Task task) {
                int errorCode = task.getErrorCode();
                if (errorCode == XSErrorEnum.SUCCESS.getCode() && task.getState() == TaskStateEnum.FINISHED && task.getProgress() == 100) {
                    //下载完成
                    long startTime = System.currentTimeMillis();
                    BookChapterInfo chapterInfo = produceChapterContentFormNet(bookId, chapterId, chapterName, startIndex, encoding,
                            suffix, errorCode);
                    if (chapterInfo != null) {
                        PagingManager.getSingleton().Paging(chapterId, chapterName, chapterInfo.getContentBytes(), encoding,
                                ReadPageState.BOOKTYPE_NORMAL);
                        int chapterNo = DataSourceManager.getSingleton().getChapterNoById(chapterId);
                        PageContent pageContent = PagingManager.getSingleton().getPageContent(chapterId, chapterNo, startIndex,
                                locateLastChapter);
//                        LogUtils.debug(" 内容加载和排版耗时 = " + (System.currentTimeMillis() - startTime) + " 毫秒");
                        if (loadListener != null) {
                            actionCount(bookId, chapterId);
                            loadListener.onLoadFinished(pageContent);
                        }
                    }
                } else {
                    if (errorCode != XSErrorEnum.SUCCESS.getCode() && task.getProgress() < 100 && (task.getState() == TaskStateEnum
                            .FAILED || task.getState() == TaskStateEnum.FINISHED)) {
                        //下载失败，并结束下载任务.
                        if (loadListener != null) {
                            loadListener.onLoadFinished(PagingManager.producePageContentByErrorCode(chapterId, chapterName, errorCode,
                                    ReadPageState.BOOKTYPE_ERROR));
                        }
                    }
                }
            }
        });
        TaskManagerDelegate.startTask(task);
    }

    /**
     * 章节阅读异常处理(未订阅、余额不足)
     */
    private void handleException(int errorId, JSONObject data, final String chapterId, final String chapterName, final String encoding,
                                 final IChapterContentLoadListener loadListener) {
        int bookType = ReadPageState.BOOKTYPE_ERROR;
        PageContent pageContent = null;
        try {
            String intro = null, price = null,originPrice = null;
            boolean isAutoSub = true;
            boolean hasDiscount = false;
            if (XSErrorEnum.CHAPTER_NOT_SUBSCRIBE.getCode() == errorId) {//章节未订阅.
                bookType = ReadPageState.BOOKTYPE_ORDER_PAY;
            } else if (XSErrorEnum.CHAPTER_SHORT_BALANCE.getCode() == errorId) { //余额不足
                bookType = ReadPageState.BOOKTYPE_RECHARGE;
            } else if (XSErrorEnum.CHAPTER_NEED_LOGIN.getCode() == errorId) {  //需要登录
                bookType = ReadPageState.BOOKTYPE_UNLOGIN;
            } else {
                bookType = ReadPageState.BOOKTYPE_ERROR;
            }
            if (data != null) {
                //针对预览信息排版
                intro = data.optString(XSKEY.READER_CHAPTER.INTRO, "");
                price = data.optString(XSKEY.READER_CHAPTER.PRICE, "0");
                isAutoSub = data.optInt(XSKEY.READER_CHAPTER.AUTO_SUB, 1) == 1 ? true : false;
                originPrice = data.optString(XSKEY.READER_CHAPTER.ORIGIN_PRICE,"0");
                hasDiscount = data.optInt(XSKEY.READER_CHAPTER.HAS_DISCOUNT, 0) == 0 ? false : true;
            }
            if (loadListener == null) {
                //预加载不处理异常信息.
                return;
            }
            if (StringUtils.isNotEmpty(intro) && StringUtils.isNotEmpty(price)) {
                byte[] temp = intro.getBytes("UTF-8");
                byte[] bytes = getByteFormat(temp, mChapterName);
                ChapterPage chapterPage = PagingManager.getSingleton().Paging(chapterId, chapterName, bytes, encoding, bookType);
                if (chapterPage != null) {
                    pageContent = chapterPage.getPageMap().get(1);
                }
            }
            if (pageContent != null) {
                pageContent.setErrorCode(errorId);
                pageContent.setPrice(price);
                pageContent.setOriginPrice(originPrice);
                pageContent.setHasDiscount(hasDiscount);
                pageContent.setBookType(bookType);
                pageContent.setAutoSub(isAutoSub);
                pageContent.setPercent(DataSourceManager.getSingleton().getPercentByChapterId(chapterId));
                //重置缓存中的autosub标识.
                ReadPageFactory.getSingleton().setLastAutoSub(isAutoSub);
                if (isAutoSub) {
                    ReadPageFactory.getSingleton().setAutoSub(isAutoSub);
                } else {
                    //如果用户默认没有选中自动订阅，我们需要人为的给他设置这个状态。
                    ReadPageFactory.getSingleton().setAutoSub(!isAutoSub);
                }
            }
        } catch (UnsupportedEncodingException e) {
            ReportUtils.reportError(e);
        } catch (Exception e) {
            ReportUtils.reportError(e);
        } finally {
            if (pageContent == null) {
                pageContent = PagingManager.producePageContentByErrorCode(chapterId, chapterName, errorId, bookType);
            }
            if (loadListener != null) {
                loadListener.onLoadFinished(pageContent);
            }
        }
    }

    /**
     * 章节翻阅统计
     *
     * @param bookId
     * @param chapterId
     */
    public void actionCount(final String bookId, final String chapterId) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            return;
        }
        String url = String.format(URLConstants.READPAGE_ANALYSIS_COUNT_URL, EncodeUtils.encodeString_UTF8(bookId), EncodeUtils
                .encodeString_UTF8(chapterId)) + CommonUtils.getPublicGetArgs();
        try {
            GetRequest request = new GetRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int errorId = ResponseHelper.getErrorId(response);
                    LogUtils.info("翻阅章节统计 bookId =" + bookId + " chapterId =" + chapterId + errorId);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            RequestQueueManager.addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
