package com.spriteapp.booklibrary.widget.readview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.ProgressCallback;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.manager.SettingManager;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.BookUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.EncryptUtils;
import com.spriteapp.booklibrary.util.MapUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;
import com.spriteapp.booklibrary.widget.readview.util.BreakResult;
import com.spriteapp.booklibrary.widget.readview.util.ShowChar;
import com.spriteapp.booklibrary.widget.readview.util.ShowLine;
import com.spriteapp.booklibrary.widget.readview.util.TRPage;
import com.spriteapp.booklibrary.widget.readview.util.TextBreakUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class PageFactory2 {
    private static final int LEFT_PLUS_MARGIN = 5;
    int book_id;
    private Context mContext;
    private Config config;
    //页面宽
    private int mWidth;
    //页面高
    private int mHeight;
    private int statusHeight;
    //文字字体大小
    private float m_fontSize;
    //时间格式
    private SimpleDateFormat sdf;
    //时间
    private String date;
    //进度格式
    //电池边界宽度
    private float mBorderWidth;
    // 上下与边缘的距离
    private float marginHeight;
    // 左右与边缘的距离
    private float marginWidth;
    //状态栏距离底部高度
    private float statusMarginBottom;
    //行间距
    private float lineSpace;
    //文字画笔
    private Paint mPaint, waitPaint;
    //加载画笔
    private Paint mChapterTitlePaint;
    //加载画笔
    private Paint mTitlePaint;
    //文字颜色
    private int m_textColor = Color.rgb(50, 65, 78);
    // 绘制内容的宽
    private float mVisibleHeight;
    // 绘制内容的宽
    private float mVisibleWidth;
    //背景图片
    private Bitmap m_book_bg = null;
    private Intent batteryInfoIntent;
    //电池电量百分比
    private float mBatteryPercentage;
    //电池外边框
    private RectF rect1 = new RectF();
    //电池内边框
    private RectF rect2 = new RectF();
    //当前是否为第一页
    private boolean m_isfirstPage;
    //当前是否为最后一页
    private boolean m_islastPage;
    //书本widget
    private MyPageWidget mBookPageWidget;
    private ProgressCallback mProgressCallback;
    //书本路径
    private String bookPath = "";
    //书本名字
    private String bookName = "";
    //书本章节
    private List<BookChapterResponse> chaptersList;
    //当前电量
    private int level = 0;
    private BookUtil mBookUtil;
    private TRPage currentPage;
    private TRPage prePage;
    private TRPage cancelPage;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private int percentLen = 0;

    private String mCurrentContent;
    private String chapterName;
    private float mPageLineCount, measureMarginWidth;
    private int currentChapter, tempChapter, currentPageNum;
    private OnReadStateChangeListener listener;
    private Map<Integer, Integer> mPageMap;
    private int mTotalPage;
    private int mbBufferLen;
    /**
     * 页首页尾的位置
     */
    private int curEndPos = 0, curBeginPos = 0, tempBeginPos, tempEndPos;

    private static Status mStatus = Status.OPENING;

    public enum Status {
        OPENING,
        FINISH,
        FAIL,
    }

    public PageFactory2(Context context, int book_id, List<BookChapterResponse> chaptersList) {
        this.book_id = book_id;
        this.chaptersList = chaptersList;
        mBookUtil = new BookUtil();
        mContext = context.getApplicationContext();
        config = Config.getInstance();


        //获取屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        mWidth = metric.widthPixels;
        mHeight = metric.heightPixels;

        sdf = new SimpleDateFormat("HH:mm");//HH:mm为24小时制,hh:mm为12小时制
        date = sdf.format(new java.util.Date());

        mBorderWidth = mContext.getResources().getDimension(R.dimen.reading_board_battery_border_width);
        marginWidth = mContext.getResources().getDimension(R.dimen.readingMarginWidth);
        marginHeight = mContext.getResources().getDimension(R.dimen.readingMarginHeight);
        statusMarginBottom = mContext.getResources().getDimension(R.dimen.reading_status_margin_bottom);
        int format = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_FORMAT, 1);
        m_fontSize = config.getFontSize();
        lineSpace = m_fontSize / 17 * (5 * (format + 1));
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;

        if (Build.VERSION.SDK_INT >= Constant.ANDROID_P_API) {
            statusHeight = Util.getStatusBarHeight(mContext);
            mVisibleHeight -= statusHeight;
        }
        waitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        waitPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        waitPaint.setTextSize(mContext.getResources().getDimension(R.dimen.reading_max_text_size));// 字体大小
        waitPaint.setColor(Color.YELLOW);// 字体颜色
        waitPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(Color.BLACK);
        mPaint.setSubpixelText(true);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        TextHeight = Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);

        mChapterTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChapterTitlePaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mChapterTitlePaint.setTextSize(m_fontSize - 2);
        mChapterTitlePaint.setColor(Color.BLACK);
        mChapterTitlePaint.setFakeBoldText(true);
        mChapterTitlePaint.setSubpixelText(true);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mTitlePaint.setTextSize(ScreenUtil.dpToPxInt(11));
        mTitlePaint.setColor(AppUtil.getAppContext().getResources().getColor(R.color.book_reader_read_title_color));
        mTitlePaint.setSubpixelText(true);


        batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//注册广播,随时获取到电池电量信息

        percentLen = (int) mTitlePaint.measureText("00.00%");

        BookDetailResponse bookDetailResponse = new BookDb(mContext).queryBook(book_id);
        bookName = bookDetailResponse.getBook_name();

        initBg(config.getDayOrNight());
        measureMarginWidth();
    }

    //初始化背景
    private void initBg(Boolean isNight) {
        if (isNight) {
            //设置背景
            Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
            setBgBitmap(bitmap);
            //设置字体颜色
            setM_textColor(mContext.getResources().getColor(R.color.book_reader_reader_text_new_night_color));
            setBookPageBg(Color.BLACK);
        } else {
            //设置背景
            setBookBg(config.getBookBgType());
        }
    }


    private void drawStatus(Bitmap bitmap) {
        String status = "";
        switch (mStatus) {
            case OPENING:
                status = "正在打开书本...";
                break;
            case FAIL:
                status = "打开书本失败！";
                break;

        }

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(getBgBitmap(), 0, 0, null);
        waitPaint.setColor(getTextColor());

        Rect targetRect = new Rect(0, 0, mWidth, mHeight);
//        c.drawRect(targetRect, waitPaint);
        Paint.FontMetricsInt fontMetrics = waitPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        waitPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText(status, targetRect.centerX(), baseline, waitPaint);
//        c.drawText("正在打开书本...", mHeight / 2, 0, waitPaint);
        mBookPageWidget.postInvalidate();
    }

    @SuppressLint("WrongConstant")
    public void onDraw(Bitmap bitmap, List<ShowLine> mLines, Boolean updateCharter) {
        if (mLines.size() <= 0) {
            return;
        }

        float y = ScreenUtil.dpToPxInt(35);
        if (Build.VERSION.SDK_INT >= Constant.ANDROID_P_API) {
            y += statusHeight;
        }
        Canvas c = new Canvas(bitmap);
        c.drawBitmap(getBgBitmap(), 0, 0, null);
        mPaint.setTextSize(getFontSize());
        mPaint.setColor(getTextColor());
        mChapterTitlePaint.setColor(getTextColor());

        //画书名
        c.drawText(bookName, marginWidth + ScreenUtil.dpToPxInt(5), y, mTitlePaint);
        y += ScreenUtil.dpToPx(35);

        // 绘制章节名
        if (curBeginPos == 0) {
            //切换章节需要找章节名
            if (TextUtils.isEmpty(chapterName) && !CollectionUtil.isEmpty(chaptersList)) {
                for (BookChapterResponse catalogResponse : chaptersList) {
                    if (catalogResponse.getChapter_id() == currentChapter) {
                        chapterName = catalogResponse.getChapter_title();
                        break;
                    }
                }
            }
            c.drawText(chapterName, measureMarginWidth, y, mChapterTitlePaint);
            y += ScreenUtil.dpToPx(30);
        }


        // 绘制阅读页面文字
        if (mLines.size() > 0) {
            y += ScreenUtil.dpToPx(10);
            for (ShowLine strLine : mLines) {
                y = drawLineText(strLine, c, y);
            }
        }

        // 绘制电池
        level = batteryInfoIntent.getIntExtra("level", 0);
        int scale = batteryInfoIntent.getIntExtra("scale", 100);
        mBatteryPercentage = (float) level / scale;
        float rect1Left = marginWidth + statusMarginBottom;//电池外框left位置
        //画电池外框
        float width = Util.dp2px(mContext, 20) - mBorderWidth;
        float height = Util.dp2px(mContext, 10);
        rect1.set(rect1Left, mHeight - height - statusMarginBottom - ScreenUtil.dpToPxInt(5), rect1Left + width, mHeight - statusMarginBottom - ScreenUtil.dpToPxInt(5));
        rect2.set(rect1Left + mBorderWidth, mHeight - height + mBorderWidth - statusMarginBottom - ScreenUtil.dpToPxInt(5), rect1Left + width - mBorderWidth, mHeight - mBorderWidth - statusMarginBottom - ScreenUtil.dpToPxInt(5));
        c.save(Canvas.CLIP_SAVE_FLAG);
        c.clipRect(rect2, Region.Op.DIFFERENCE);
        c.drawRect(rect1, mTitlePaint);
        c.restore();
        //画电量部分
        rect2.left += mBorderWidth;
        rect2.right -= mBorderWidth;
        rect2.right = rect2.left + rect2.width() * mBatteryPercentage;
        rect2.top += mBorderWidth;
        rect2.bottom -= mBorderWidth;
        c.drawRect(rect2, mTitlePaint);
        //画电池头
        int poleHeight = Util.dp2px(mContext, 10) / 2;
        rect2.left = rect1.right;
        rect2.top = rect2.top + poleHeight / 4;
        rect2.right = rect1.right + mBorderWidth;
        rect2.bottom = rect2.bottom - poleHeight / 4;
        c.drawRect(rect2, mTitlePaint);

        //绘制进度
        float percent = (float) curEndPos * 100 / mbBufferLen;
        if (mProgressCallback != null) {
            mProgressCallback.sendProgress(percent);
        }
        c.drawText(decimalFormat.format(percent) + "%", mWidth - marginWidth - percentLen,
                mHeight - ScreenUtil.dpToPxInt(10), mTitlePaint);

        //绘制时间
        c.drawText(date, marginWidth + ScreenUtil.dpToPxInt(LEFT_PLUS_MARGIN * 2) +
                        width
                , mHeight - ScreenUtil.dpToPxInt(9.5f), mTitlePaint);

        SettingManager.getInstance().saveReadProgress(book_id + "", currentChapter, curBeginPos, curEndPos);
        mBookPageWidget.getListData(mLines);
        mBookPageWidget.postInvalidate();
    }

    private float TextHeight = 0;

    private float drawLineText(ShowLine lines, Canvas cs, float y) {
        boolean isChangeLine = false;
        String text = lines.getLineData();
        if (text.endsWith("\n")) {
            cs.drawText(text, measureMarginWidth, y, mPaint);
            y += lineSpace;
            isChangeLine = true;
        } else {
            isChangeLine = false;
            cs.drawText(text, measureMarginWidth, y, mPaint);
        }

        float leftposition = ScreenUtil.dpToPxInt(20);
        float rightposition = 0;
        float bottomposition = (isChangeLine?y-lineSpace:y) + mPaint.getFontMetrics().descent;

        for (ShowChar c : lines.CharsData) {

            rightposition = leftposition + c.charWidth;
            Point tlp = new Point();
            c.TopLeftPosition = tlp;
            tlp.x = (int) leftposition;
            tlp.y = (int) (bottomposition - TextHeight);

            Point blp = new Point();
            c.BottomLeftPosition = blp;
            blp.x = (int) leftposition;
            blp.y = (int) bottomposition;

            Point trp = new Point();
            c.TopRightPosition = trp;
            trp.x = (int) rightposition;
            trp.y = (int) (bottomposition - TextHeight);

            Point brp = new Point();
            c.BottomRightPosition = brp;
            brp.x = (int) rightposition;
            brp.y = (int) bottomposition;

            leftposition = rightposition;

        }
        y += lineSpace + m_fontSize;
        return y;
    }


    public void setProgressCallback(ProgressCallback mProgressCallback) {
        this.mProgressCallback = mProgressCallback;
    }

    /**
     * 打开书本
     *
     * @throws IOException
     */
    public int openBook(int chapter, int[] position, boolean isfirstPage) {
        chapterName = null;
        this.currentChapter = chapter;
        drawStatus(mBookPageWidget.getCurPage());
        drawStatus(mBookPageWidget.getNextPage());
        SubscriberContent subscriberContent = new ContentDb(mContext).queryContent(book_id, chapter);
        if (subscriberContent == null) {
            return 0;
        }
        String key = subscriberContent.getChapter_content_key();
        String content = subscriberContent.getChapter_content();
        mCurrentContent = EncryptUtils.decrypt(content, key);
        mbBufferLen = mCurrentContent.length();
        curBeginPos = position[0];
        curEndPos = position[1];
        setChapterTotalPage();
        onChapterChanged(chapter);
        if (isfirstPage) {
            currentPage = getPageForBegin(curBeginPos);
            if (mBookPageWidget != null) {
                mStatus = Status.FINISH;
                currentPage(true);
            }
        }

        return 1;
    }


    /**
     * 跳转下一页
     */
    public void nextPage() {
        if (!hasNextPage()) { // 最后一章的结束页
            if (!m_islastPage)
                ToastUtil.showSingleToast("没有下一页啦");
            m_islastPage = true;
            return;
        } else {
            m_islastPage = false;
        }
        tempChapter = currentChapter;
        tempBeginPos = curBeginPos;
        tempEndPos = curEndPos;
        if (curEndPos >= mbBufferLen && !CollectionUtil.isEmpty(chaptersList)) { // 中间章节结束页
//            Log.d("nextPage", curEndPos + "-------章节结束,取下一页-----------" + mbBufferLen);
            for (int i = 0, size = chaptersList.size(); i < size; i++) {
                BookChapterResponse catalog = chaptersList.get(i);
                if (catalog.getChapter_id() == currentChapter) {
                    if (i + 1 < size) {
                        currentChapter = chaptersList.get(i + 1).getChapter_id();
                        chapterName = chaptersList.get(i + 1).getChapter_title();
//                        Log.d("nextPage", currentChapter+"-------下一章节-------" + chaptersList.get(i + 1).getChapter_title());
                        break;
                    }
                }
            }
            int ret = openBook(currentChapter, new int[]{0, 0}, false); // 打开下一章
            if (ret == 0) {
                onLoadChapterFailure(currentChapter);
                if (!NetworkUtil.isAvailable(mContext)) {
                    currentChapter = tempChapter;
                }
                curBeginPos = tempBeginPos;
                curEndPos = tempEndPos;
                return;
            } else {
                currentPageNum = 0;
            }
        } else {
            curBeginPos = curEndPos; // 起始指针移到结束位置
        }
        //-------------------------------
        cancelPage = currentPage;
        onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
        prePage = currentPage;
        currentPage = getNextPage();
        onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
        onPageChanged(currentChapter, ++currentPageNum);
        //-------------------------------
        return;
    }

    /**
     * 加载章节需要登录时
     */
    public void setCurrentChapter() {
        currentChapter = tempChapter;
    }

    /**
     * 跳转上一页
     */
    public void prePage() {
        if (!hasPrePage()) { // 第一章第一页
            if (!m_isfirstPage) {
                ToastUtil.showSingleToast("没有上一页哦");
            }
            m_isfirstPage = true;
            return;
        } else {
            m_isfirstPage = false;
        }
        // 保存当前页的值
        tempChapter = currentChapter;
        tempBeginPos = curBeginPos;
        tempEndPos = curEndPos;
        if (curBeginPos <= 0 && !CollectionUtil.isEmpty(chaptersList)) {
            for (int i = 0, size = chaptersList.size(); i < size; i++) {
                BookChapterResponse catalog = chaptersList.get(i);
                int chapter_id = catalog.getChapter_id();
                //上一章
                if (chapter_id == currentChapter && i != 0) {
                    currentChapter = chaptersList.get(i - 1).getChapter_id();
                    chapterName = chaptersList.get(i - 1).getChapter_title();
                    break;
                }
            }
            int value = openBook(currentChapter, new int[]{0, 0}, false);
            if (value == 0) {
                onLoadChapterFailure(currentChapter);
                if (!NetworkUtil.isAvailable(mContext)) {
                    currentChapter = tempChapter;
                }
                return;
            }
            // 跳转到上一章的最后一页
            if (!MapUtil.isEmpty(mPageMap)) {
                if (mPageMap.containsKey(mTotalPage - 1)) {
                    curEndPos = mPageMap.get(mTotalPage - 1);
                    curBeginPos = curEndPos;
                }
            }
            //------------------------
            cancelPage = currentPage;
            onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
            currentPage = getNextPage();
            onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
            //------------------------
            onPageChanged(currentChapter, currentPageNum);
            return;
        }

        boolean hasPosition = mPageMap.containsValue(curBeginPos);
        if (hasPosition) {
            for (Map.Entry<Integer, Integer> next : mPageMap.entrySet()) {
                int value = next.getValue();
                if (value == curBeginPos) {
                    int key = next.getKey();
                    if (mPageMap.containsKey(key - 1)) {
                        curEndPos = mPageMap.get(key - 1);
                    } else {
                        curEndPos = 0;
                    }
                    curBeginPos = curEndPos;
                    break;
                }
            }
        } else {
            cancelPage = currentPage;
            currentPage = getPrePage();
        }
        //------------------------
        cancelPage = currentPage;
        onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
        currentPage = getNextPage();
        onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
        //------------------------
        onPageChanged(currentChapter, --currentPageNum);
        return;
    }

    public void cancelPage() {
        currentChapter = tempChapter;
        curBeginPos = tempBeginPos;
        curEndPos = curBeginPos;

        int ret = openBook(currentChapter, new int[]{curBeginPos, curEndPos}, true);
        if (ret == 0) {
            onLoadChapterFailure(currentChapter);
            return;
        }
    }


    public boolean hasNextPage() {
        if (CollectionUtil.isEmpty(chaptersList)) {
            return false;
        }
        BookChapterResponse lastCatalog = chaptersList.get(chaptersList.size() - 1);
        return lastCatalog.getChapter_id() != currentChapter || curEndPos < mbBufferLen;
    }

    public boolean hasPrePage() {
        if (CollectionUtil.isEmpty(chaptersList)) {
            return false;
        }
        BookChapterResponse firstCatalog = chaptersList.get(0);
        return firstCatalog.getChapter_id() != currentChapter
                || (firstCatalog.getChapter_id() == currentChapter && curBeginPos > 0);
    }


    public TRPage getNextPage() {
        mBookUtil.setPostition(currentPage.getEnd());

        TRPage trPage = new TRPage();
        trPage.setBegin(currentPage.getEnd());
        trPage.setLines(getNextLines());
//        Log.e("getNextPage", "<--getNextPage--下一页--" + trPage.getLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    public TRPage getPrePage() {
        mBookUtil.setPostition(currentPage.getBegin());

        TRPage trPage = new TRPage();
        trPage.setEnd(mBookUtil.getPosition() - 1);
        trPage.setLines(getPreLines());
//        Log.e("getPrePage", "<--getPrePage-上一页--" + trPage.getLines());
        trPage.setBegin(mBookUtil.getPosition());
        return trPage;
    }

    public TRPage getPageForBegin(int begin) {
        curEndPos = curBeginPos;
        TRPage trPage = new TRPage();
        trPage.setBegin(begin);

        mBookUtil.setPostition(begin - 1);
        trPage.setLines(getNextLines());
//        Log.d("getPageForBegin", begin + "<--getPageForBegin---当前----" + trPage.getLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    /**
     * 根据起始位置指针，读取一页内容
     */
    private Vector<ShowLine> getNextLines() {
        if (currentPage == null)
            currentPage = new TRPage();
        Vector<ShowLine> lines = new Vector<>();
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (m_fontSize + lineSpace);

        while ((lines.size() < mPageLineCount) && (curEndPos < mbBufferLen)) {
            //一个段落内容，根据\n 划分段
            String strParagraph = readNextParagraph(curEndPos);
//            Log.e("getNextLines-->" + curEndPos, curEndPos + strParagraph.length() + "-------下一段落： " + strParagraph);
            currentPage.setEnd(currentPage.getEnd() + strParagraph.length());
            mBookUtil.setPostition(currentPage.getEnd() + strParagraph.length());
            curEndPos += strParagraph.length();

            boolean isParagraphEmpty = StringUtil.isEmpty(strParagraph);
//            String text = strParagraph;
//            if (!TextUtils.isEmpty(text)) {
//                text += "@";
//            }
            while (strParagraph.length() > 0) {
                //截取最大宽度能容纳文本数
                int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);

                BreakResult breakResult = TextBreakUtil.BreakText(strParagraph.substring(0, paintSize), mVisibleWidth, 0, mPaint);
                ShowLine showLine = new ShowLine();
                showLine.CharsData = breakResult.showChars;
                lines.add(showLine);

                //剩下无法容纳的文本
                strParagraph = strParagraph.substring(paintSize);
                if (lines.size() >= mPageLineCount) {
                    break;
                }
            }
            //在段落末尾加上区分符号
            if (!CollectionUtil.isEmpty(lines) && !isParagraphEmpty) {
//                ShowLine sl = lines.get(lines.size() - 1);
//                List<ShowChar> CharsData = sl.CharsData;
//                ShowChar sc = CharsData.get(CharsData.size() - 1);
//                String text = String.valueOf(sc.chardata);
//                text += "@";
//                for (int i = 0, size = text.toCharArray().length; i < size; i++) {
//                    sc.chardata+=text.toCharArray()[i];
//                }
//                Log.e("getNextLiness","--------getNextLines----------"+sc.chardata);
//                CharsData.set(CharsData.size() - 1, sc);
//                sl.CharsData = CharsData;
//                lines.set(lines.size() - 1, sl);
            }
            //减去多余文本
            if (strParagraph.length() != 0) {
                mBookUtil.setPostition(currentPage.getEnd() - strParagraph.length());
                currentPage.setEnd(currentPage.getEnd() - strParagraph.length());
                curEndPos -= (strParagraph).length();
            }
            paraSpace += lineSpace;
            mPageLineCount = (mVisibleHeight - paraSpace) / (m_fontSize + lineSpace);
        }

        int index = 0;
        for (ShowLine l : lines) {
            for (ShowChar c : l.CharsData) {
                c.Index = index++;
            }
        }
        return lines;
    }

    /**
     * 读取下一段落
     *
     * @param curEndPos 当前页结束位置指针
     */
    private String readNextParagraph(int curEndPos) {
        String result = "";
        int i = curEndPos;
        while (i < mbBufferLen) {
            char value = 0;
            value = mCurrentContent.charAt(i);
            result += String.valueOf(value);
            i++;
            if (("\n").equals(String.valueOf(value)) && !StringUtil.isEmpty(result)) {
                break;
            }
        }
        return result;
    }


    /**
     * 指针移到上一页页首
     */
    private Vector<ShowLine> getPreLines() {
        Vector<ShowLine> lines = new Vector<>(); // 页面行
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (m_fontSize + lineSpace);
        while ((lines.size() < mPageLineCount) && (curBeginPos > 0)) {
            Vector<ShowLine> paraLines = new Vector<>(); // 段落行
            String strParagraph = readUpParagraph(curBeginPos);
//            Log.e("getPreLines", "----上一页的行： " + strParagraph);
            mBookUtil.setPostition(currentPage.getBegin() - strParagraph.length());
            currentPage.setBegin(currentPage.getBegin() - strParagraph.length());

            curBeginPos -= strParagraph.length();
            while (strParagraph.length() > 0) { // 3.逐行添加到lines
                int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);

                BreakResult breakResult = TextBreakUtil.BreakText(strParagraph.substring(0, paintSize), mVisibleWidth, 0, mPaint);
                ShowLine showLine = new ShowLine();
                showLine.CharsData = breakResult.showChars;
                paraLines.add(showLine);
                strParagraph = strParagraph.substring(paintSize);
            }

            lines.addAll(0, paraLines);

            while (lines.size() > mPageLineCount) { // 4.如果段落添加完，但是超出一页，则超出部分需删减
                currentPage.setBegin(currentPage.getBegin() + lines.get(0).CharsData.size());
                curBeginPos += lines.get(0).CharsData.size(); // 5.删减行数同时起始位置指针也要跟着偏移
                lines.remove(0);
            }
            currentPage.setBegin(currentPage.getBegin());
            curEndPos = curBeginPos; // 6.最后结束指针指向下一段的开始处
            paraSpace += lineSpace;
            mPageLineCount = (mVisibleHeight - paraSpace) / (m_fontSize + lineSpace); // 添加段落间距，实时更新容纳行数
        }

        int index = 0;
        for (ShowLine l : lines) {
            for (ShowChar c : l.CharsData) {
                c.Index = index++;
            }
        }
        return lines;
    }

    /**
     * 读取上一段落
     */
    private String readUpParagraph(long curBeginPos) {
        String result = "";
        long i = curBeginPos - 1;
        while (i >= 0) {
            char value = mCurrentContent.charAt((int) i);
            String s = String.valueOf(value);
            result += s;
            i--;
            if ("\n".equals(String.valueOf(value)) && i != curBeginPos - 1) {
                break;
            }
        }

        return result;
    }


    //绘制当前页面
    public void currentPage(Boolean updateChapter) {
        onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), updateChapter);
        onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), updateChapter);
    }

    //更新电量
    public void updateBattery(int mLevel) {
        if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
            if (level != mLevel) {
                level = mLevel;
                currentPage(false);
            }
        }
    }

    public void updateTime() {
        if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
            String mDate = sdf.format(new java.util.Date());
            if (date != mDate) {
                date = mDate;
                currentPage(false);
            }
        }
    }


    //改变进度
    public void changeChapter(int position) {
        setSelectPage(position);
        currentPage = getPageForBegin(position);
        currentPage(true);
    }


    //改变字体大小
    public void changeFontSize(int fontSize) {
        this.m_fontSize = fontSize;
        mPaint.setTextSize(m_fontSize);
        mChapterTitlePaint.setTextSize(m_fontSize - 2);
        currentPage = getPageForBegin(currentPage.getBegin());
        measureMarginWidth();
        currentPage(true);
    }

    //改变字体
    public void changeTypeface(Typeface typeface) {
        mPaint.setTypeface(typeface);
        mTitlePaint.setTypeface(typeface);
        mChapterTitlePaint.setTypeface(typeface);
        currentPage = getPageForBegin(currentPage.getBegin());
        measureMarginWidth();
        currentPage(true);
    }

    /**
     * 设置间距
     */
    public void setFontSpace(int space) {
        lineSpace = m_fontSize / 17 * (5 * (space + 1));
        mPageLineCount = mVisibleHeight / (m_fontSize + lineSpace);
        currentPage = getPageForBegin(currentPage.getBegin());
        measureMarginWidth();
        currentPage(true);
    }

    private void measureMarginWidth() {
        float wordWidth = mPaint.measureText("\u3000");
        float width = mVisibleWidth % wordWidth;
        measureMarginWidth = marginWidth + width / 2;
    }

    //设置页面的背景
    public void setBookBg(int type) {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        int color = 0;
        switch (type - 1) {
            case Config.BOOK_BG_DEFAULT:
                canvas.drawColor(mContext.getResources().getColor(R.color.reader_book_bg1));
                color = mContext.getResources().getColor(R.color.black);
                setBookPageBg(mContext.getResources().getColor(R.color.reader_book_bg1));
                break;
            case Config.BOOK_BG_1:
                canvas.drawColor(mContext.getResources().getColor(R.color.reader_book_bg2));
                color = mContext.getResources().getColor(R.color.black);
                setBookPageBg(mContext.getResources().getColor(R.color.reader_book_bg2));
                break;
            case Config.BOOK_BG_2:
                canvas.drawColor(mContext.getResources().getColor(R.color.reader_book_bg3));
                color = mContext.getResources().getColor(R.color.black);
                setBookPageBg(mContext.getResources().getColor(R.color.reader_book_bg3));
                break;
            case Config.BOOK_BG_3:
                canvas.drawColor(mContext.getResources().getColor(R.color.reader_book_bg4));
                color = mContext.getResources().getColor(R.color.black);
                setBookPageBg(mContext.getResources().getColor(R.color.reader_book_bg4));
                break;
            case Config.BOOK_BG_4:
                canvas.drawColor(mContext.getResources().getColor(R.color.reader_book_bg5));
                color = mContext.getResources().getColor(R.color.black);
                setBookPageBg(mContext.getResources().getColor(R.color.reader_book_bg5));
                break;
            case Config.BOOK_BG_5:
                canvas.drawColor(mContext.getResources().getColor(R.color.reader_book_bg6));
                color = mContext.getResources().getColor(R.color.black);
                setBookPageBg(mContext.getResources().getColor(R.color.reader_book_bg6));
                break;
        }

        setBgBitmap(bitmap);
        //设置字体颜色
        setM_textColor(color);
    }

    public void setBookPageBg(int color) {
        if (mBookPageWidget != null) {
            mBookPageWidget.setBgColor(color);
        }
    }

    //改变背景
    public void changeBookBg(int type) {
        setBookBg(type);
        currentPage(false);
    }

    //设置日间或者夜间模式
    public void setDayOrNight(Boolean isNgiht) {
        initBg(isNgiht);
        currentPage(false);
    }

    public void setPageMode(int mode) {
        mBookPageWidget.setPageMode(mode);
    }

    public void setPProgressFormat(int format) {

    }

    public void clear() {
        bookPath = "";
        bookName = "";
        mBookPageWidget = null;
        cancelPage = null;
        prePage = null;
        currentPage = null;
    }

    public static Status getStatus() {
        return mStatus;
    }


    public String getBookPath() {
        return bookPath;
    }

    //是否是第一页
    public boolean isfirstPage() {
        return m_isfirstPage;
    }

    //是否是最后一页
    public boolean islastPage() {
        return m_islastPage;
    }

    //设置页面背景
    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    //设置页面背景
    public Bitmap getBgBitmap() {
        return m_book_bg;
    }

    //设置文字颜色
    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }

    //获取文字颜色
    public int getTextColor() {
        return this.m_textColor;
    }

    //获取文字大小
    public float getFontSize() {
        return this.m_fontSize;
    }

    public void setPageWidget(MyPageWidget mBookPageWidget) {
        this.mBookPageWidget = mBookPageWidget;
    }


    public void setOnReadStateChangeListener(OnReadStateChangeListener listener) {
        this.listener = listener;
    }

    private void onChapterChanged(int chapter) {
        if (listener != null)
            listener.onChapterChanged(chapter);
    }

    private void onPageChanged(int chapter, int page) {
        if (listener != null)
            listener.onPageChanged(chapter, page);
    }

    private void onLoadChapterFailure(int chapter) {
        if (listener != null)
            listener.onLoadChapterFailure(chapter);
    }

    public float getCurrentProgress() {
        return (float) curEndPos * 100 / mbBufferLen;
    }


    public int getChapterTotalPage() {
        return mTotalPage;
    }

    public void setSelectPage(int position) {
        if (MapUtil.isEmpty(mPageMap)) {
            return;
        }
        if (position == mPageMap.size()) {
            position--;
        }
        if (position < 0 || position >= mPageMap.size()) {
            return;
        }
        if (mPageMap.containsKey(position)) {
            curBeginPos = mPageMap.get(position);
        }
    }

    /**
     * 获取当前章的总页数
     */
    public int setChapterTotalPage() {
        if (mPageMap == null) {
            mPageMap = new LinkedHashMap<>();
        }
        mPageMap.clear();
        Vector<String> lines = new Vector<>();
        mTotalPage = 0;
        int currentEndPos = 0;
        while (currentEndPos < mbBufferLen) {
            int paraSpace = 0;
            mPageLineCount = mVisibleHeight / (m_fontSize + lineSpace);
            int curBeginPos = currentEndPos;
            while ((lines.size() < mPageLineCount) && (currentEndPos < mbBufferLen)) {
                String strParagraph = readNextParagraph(currentEndPos);
                currentEndPos += strParagraph.length();
                while (strParagraph.length() > 0) {
                    int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                    lines.add(strParagraph.substring(0, paintSize));
                    strParagraph = strParagraph.substring(paintSize);
                    if (lines.size() >= mPageLineCount) {
                        break;
                    }
                }

                if (strParagraph.length() != 0) {
                    currentEndPos -= strParagraph.length();
                }
                paraSpace += lineSpace;
                mPageLineCount = (mVisibleHeight - paraSpace) / (m_fontSize + lineSpace);
            }
            if (currentEndPos < mbBufferLen) {
                lines.clear();
            }
            mPageMap.put(mTotalPage, curBeginPos);
            mTotalPage++;
        }
        return mTotalPage;
    }

}
