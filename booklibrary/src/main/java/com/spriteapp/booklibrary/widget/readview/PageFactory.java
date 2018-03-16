/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spriteapp.booklibrary.widget.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.ProgressCallback;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.manager.SettingManager;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.EncryptUtils;
import com.spriteapp.booklibrary.util.MapUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.StringUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PageFactory {
    private static final String TAG = "PageFactory";
    private static final int LEFT_PLUS_MARGIN = 5;
    private Context mContext;
    /**
     * 屏幕宽高
     */
    private int mHeight, mWidth;
    /**
     * 文字区域宽高
     */
    private int mVisibleHeight, mVisibleWidth;
    /**
     * 间距
     */
    private int marginHeight, marginWidth;
    /**
     * 字体大小
     */
    private int mFontSize;
    /**
     * 每页行数
     */
    private int mPageLineCount;
    /**
     * 行间距
     **/
    private int mLineSpace;
    /**
     * 字节长度
     */
    private int mbBufferLen;
    /**
     * 页首页尾的位置
     */
    private int curEndPos = 0, curBeginPos = 0, tempBeginPos, tempEndPos;
    private int currentChapter, tempChapter;
    private Vector<String> mLines = new Vector<>();

    private Paint mPaint;
    private Paint mTitlePaint;
    private Bitmap mBookPageBg;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private int percentLen = 0;
    private String time;
    private int battery = 40;
    private Rect rectF;
    private Bitmap batteryBitmap;

    private String bookId;
    private List<BookChapterResponse> chaptersList;
    private int currentPage = 1;

    private OnReadStateChangeListener listener;
    private String mCurrentContent;
    private ProgressCallback mProgressCallback;
    private ContentDb mContentDb;
    private int mBatteryWidth;

    public PageFactory(Context context, String bookId, List<BookChapterResponse> chaptersList) {
        this(context, ScreenUtil.getScreenWidth(), ScreenUtil.getScreenHeight(),
                SettingManager.getInstance().getReadFontSize(),
                bookId, chaptersList);
    }

    public PageFactory(Context context, int width, int height, int fontSize, String bookId,
                       List<BookChapterResponse> chaptersList) {
        mContext = context;
        mWidth = width;
        mHeight = height;
        mFontSize = fontSize;
        mLineSpace = mFontSize / 17 * 12;
        marginWidth = ScreenUtil.dpToPxInt(14);
        marginHeight = ScreenUtil.dpToPxInt(66);
        mVisibleHeight = mHeight - marginHeight * 2 + ScreenUtil.dpToPxInt(25);
        mVisibleWidth = mWidth - marginWidth * 2;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        rectF = new Rect(0, 0, mWidth, mHeight);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(Color.BLACK);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextSize(ScreenUtil.dpToPxInt(11));
        mTitlePaint.setColor(AppUtil.getAppContext().getResources().getColor(R.color.book_reader_read_title_color));

        percentLen = (int) mTitlePaint.measureText("00.00%");

        this.bookId = bookId;
        this.chaptersList = chaptersList;

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        time = dateFormat.format(new Date());
        mContentDb = new ContentDb(mContext);
        mBatteryWidth = ScreenUtil.dpToPxInt(20);
    }

    /**
     * 打开书籍文件
     *
     * @param chapter  阅读章节
     * @param position 阅读位置
     * @return 0：文件不存在或打开失败  1：打开成功
     */
    public int openBook(int chapter, int[] position) {
        this.currentChapter = chapter;
        SubscriberContent subscriberContent = mContentDb.queryContent(Integer.valueOf(bookId), chapter);
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
        mLines.clear();
        return 1;
    }

    /**
     * 绘制阅读页面
     */
    public synchronized void onDraw(Canvas canvas) {
        if (mLines.size() == 0) {
            curEndPos = curBeginPos;
            mLines = pageDown();
        }
        if (mLines.size() <= 0) {
            return;
        }
        int y = marginHeight;
        // 绘制背景
        if (mBookPageBg != null && !mBookPageBg.isRecycled() && rectF != null) {
            canvas.drawBitmap(mBookPageBg, null, rectF, null);
            Log.d("mBookPageBg", "mBookPageBg不为空");
        } else {
            Log.d("mBookPageBg", "mBookPageBg为空");
            canvas.drawColor(AppUtil.getAppContext().getResources().
                    getColor(R.color.book_reader_read_page_default_background));
        }
        // 绘制标题
        if (!CollectionUtil.isEmpty(chaptersList)) {
            for (BookChapterResponse catalogResponse : chaptersList) {
                int chapter_id = catalogResponse.getChapter_id();
                if (chapter_id == currentChapter) {
                    String title = catalogResponse.getChapter_title();
                    canvas.drawText(title, marginWidth + ScreenUtil.dpToPxInt(5), ScreenUtil.dpToPx(40), mTitlePaint);
                    break;
                }
            }
        }

        y += ScreenUtil.dpToPx(3);
        // 绘制阅读页面文字
        for (String line : mLines) {
            y += mLineSpace;
            if (line.endsWith("@")) {
                canvas.drawText(line.substring(0, line.length() - 1), marginWidth + ScreenUtil.dpToPxInt(LEFT_PLUS_MARGIN), y, mPaint);
                y += mLineSpace;
            } else {
                canvas.drawText(line, marginWidth + ScreenUtil.dpToPxInt(LEFT_PLUS_MARGIN), y, mPaint);
            }
            y += mFontSize;
        }
        // 绘制电池
        if (batteryBitmap != null) {
            canvas.drawBitmap(batteryBitmap, marginWidth + ScreenUtil.dpToPxInt(LEFT_PLUS_MARGIN),
                    mHeight - ScreenUtil.dpToPxInt(20), mTitlePaint);
        }

        //绘制进度
        float percent = (float) curEndPos * 100 / mbBufferLen;
        if (mProgressCallback != null) {
            mProgressCallback.sendProgress(percent);
        }
        canvas.drawText(decimalFormat.format(percent) + "%", mWidth - marginWidth - percentLen,
                mHeight - ScreenUtil.dpToPxInt(12), mTitlePaint);

        //绘制时间
        canvas.drawText(time, marginWidth + ScreenUtil.dpToPxInt(LEFT_PLUS_MARGIN * 2) +
                        mBatteryWidth
                , mHeight - ScreenUtil.dpToPxInt(12), mTitlePaint);
        // 保存阅读进度
        SettingManager.getInstance().saveReadProgress(bookId, currentChapter, curBeginPos, curEndPos);
    }

    public void setProgressCallback(ProgressCallback mProgressCallback) {
        this.mProgressCallback = mProgressCallback;
    }

    /**
     * 指针移到上一页页首
     */
    private void pageUp() {
        Vector<String> lines = new Vector<>(); // 页面行
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        while ((lines.size() < mPageLineCount) && (curBeginPos > 0)) {
            Vector<String> paraLines = new Vector<>(); // 段落行
            String strParagraph = readUpParagraph(curBeginPos);
            curBeginPos -= strParagraph.length();
            while (strParagraph.length() > 0) { // 3.逐行添加到lines
                int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                paraLines.add(strParagraph.substring(0, paintSize));
                strParagraph = strParagraph.substring(paintSize);
            }
            lines.addAll(0, paraLines);

            while (lines.size() > mPageLineCount) { // 4.如果段落添加完，但是超出一页，则超出部分需删减
                curBeginPos += lines.get(0).length(); // 5.删减行数同时起始位置指针也要跟着偏移
                lines.remove(0);
            }
            curEndPos = curBeginPos; // 6.最后结束指针指向下一段的开始处
            paraSpace += mLineSpace;
            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace); // 添加段落间距，实时更新容纳行数
        }
    }

    /**
     * 根据起始位置指针，读取一页内容
     */
    private Vector<String> pageDown() {
        Vector<String> lines = new Vector<>();
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        while ((lines.size() < mPageLineCount) && (curEndPos < mbBufferLen)) {
            String strParagraph = readNextParagraph(curEndPos);

            curEndPos += strParagraph.length();
            boolean isParagraphEmpty = StringUtil.isEmpty(strParagraph);
            while (strParagraph.length() > 0) {
                int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                lines.add(strParagraph.substring(0, paintSize));
                strParagraph = strParagraph.substring(paintSize);
                if (lines.size() >= mPageLineCount) {
                    break;
                }
            }
            if (!CollectionUtil.isEmpty(lines) && !isParagraphEmpty) {
                lines.set(lines.size() - 1, lines.get(lines.size() - 1) + "@");
            }
            if (strParagraph.length() != 0) {
                curEndPos -= (strParagraph).length();
            }
            paraSpace += mLineSpace;
            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
        }
        return lines;
    }

    private int mTotalPage;

    public int getChapterTotalPage() {
        return mTotalPage;
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
            mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
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
                paraSpace += mLineSpace;
                mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
            }
            if (currentEndPos < mbBufferLen) {
                lines.clear();
            }
            mPageMap.put(mTotalPage, curBeginPos);
            mTotalPage++;
        }
        return mTotalPage;
    }


    private Map<Integer, Integer> mPageMap;

    public float getCurrentProgress() {
        return (float) curEndPos * 100 / mbBufferLen;
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
            mLines.clear();
        }
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
     * 读取上一段落
     */
    private String readUpParagraph(int curBeginPos) {
        String result = "";
        int i = curBeginPos - 1;
        while (i >= 0) {
            char value = mCurrentContent.charAt(i);
            String s = String.valueOf(value);
            result += s;
            i--;
            if ("\n".equals(String.valueOf(value)) && i != curBeginPos - 1) {
                break;
            }
        }
        return result;
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

    /**
     * 跳转下一页
     */
    public BookStatus nextPage() {
        if (!hasNextPage()) { // 最后一章的结束页
            return BookStatus.NO_NEXT_PAGE;
        }
        tempChapter = currentChapter;
        tempBeginPos = curBeginPos;
        tempEndPos = curEndPos;
        if (curEndPos >= mbBufferLen && !CollectionUtil.isEmpty(chaptersList)) { // 中间章节结束页
            for (int i = 0, size = chaptersList.size(); i < size; i++) {
                BookChapterResponse catalog = chaptersList.get(i);
                if (catalog.getChapter_id() == currentChapter) {
                    if (i + 1 < size) {
                        currentChapter = chaptersList.get(i + 1).getChapter_id();
                        break;
                    }
                }
            }
            int ret = openBook(currentChapter, new int[]{0, 0}); // 打开下一章
            if (ret == 0) {
                onLoadChapterFailure(currentChapter);
                if (!NetworkUtil.isAvailable(mContext)) {
                    currentChapter = tempChapter;
                }
                curBeginPos = tempBeginPos;
                curEndPos = tempEndPos;
                return BookStatus.NEXT_CHAPTER_LOAD_FAILURE;
            } else {
                currentPage = 0;
            }
        } else {
            curBeginPos = curEndPos; // 起始指针移到结束位置
        }
        mLines.clear();
        mLines = pageDown(); // 读取一页内容
        onPageChanged(currentChapter, ++currentPage);
        return BookStatus.LOAD_SUCCESS;
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
    public BookStatus prePage() {
        if (!hasPrePage()) { // 第一章第一页
            return BookStatus.NO_PRE_PAGE;
        }
        // 保存当前页的值
        tempChapter = currentChapter;
        tempBeginPos = curBeginPos;
        tempEndPos = curEndPos;
        if (curBeginPos <= 0 && !CollectionUtil.isEmpty(chaptersList)) {
            for (int i = 0, size = chaptersList.size(); i < size; i++) {
                BookChapterResponse catalog = chaptersList.get(i);
                int chapter_id = catalog.getChapter_id();
                if (chapter_id == currentChapter && i != 0) {
                    currentChapter = chaptersList.get(i - 1).getChapter_id();
                    break;
                }
            }
            int value = openBook(currentChapter, new int[]{0, 0});
            if (value == 0) {
                onLoadChapterFailure(currentChapter);
                if (!NetworkUtil.isAvailable(mContext)) {
                    currentChapter = tempChapter;
                }
                return BookStatus.PRE_CHAPTER_LOAD_FAILURE;
            }
            // 跳转到上一章的最后一页
            mLines.clear();
            if (!MapUtil.isEmpty(mPageMap)) {
                if (mPageMap.containsKey(mTotalPage - 1)) {
                    curEndPos = mPageMap.get(mTotalPage - 1);
                    curBeginPos = curEndPos;
                }
            }
            mLines = pageDown();
            onPageChanged(currentChapter, currentPage);
            return BookStatus.LOAD_SUCCESS;
        }
        mLines.clear();
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
            pageUp(); // 起始指针移到上一页开始处
        }
        mLines = pageDown(); // 读取一页内容
        onPageChanged(currentChapter, --currentPage);
        return BookStatus.LOAD_SUCCESS;
    }

    public void cancelPage() {
        currentChapter = tempChapter;
        curBeginPos = tempBeginPos;
        curEndPos = curBeginPos;

        int ret = openBook(currentChapter, new int[]{curBeginPos, curEndPos});
        if (ret == 0) {
            onLoadChapterFailure(currentChapter);
            return;
        }
        mLines.clear();
        mLines = pageDown();
    }

    /**
     * 设置字体大小
     *
     * @param fontSize 单位：px
     */
    public void setTextFont(int fontSize) {
        mFontSize = fontSize;
        mLineSpace = mFontSize / 17 * 12;
        mPaint.setTextSize(mFontSize);
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        setChapterTotalPage();
        setCurrentPageBeginPos();
        nextPage();
    }

    /**
     * 调整文字大小时重新获取绘制位置
     */
    private void setCurrentPageBeginPos() {
        if (MapUtil.isEmpty(mPageMap)) {
            return;
        }
        for (Map.Entry<Integer, Integer> next : mPageMap.entrySet()) {
            if (next.getValue() > curBeginPos) {
                int key = next.getKey();
                if (mPageMap.containsKey(key - 1)) {
                    curEndPos = mPageMap.get(key - 1);
                } else {
                    curEndPos = 0;
                }
                break;
            } else if (next.getValue() == curBeginPos) {
                curEndPos = next.getValue();
                break;
            }
        }
    }

    /**
     * 设置字体颜色
     */
    public void setTextColor(int textColor, int titleColor) {
        mPaint.setColor(textColor);
        mTitlePaint.setColor(titleColor);
    }

    /**
     * 设置字体样式
     */
    public void setTexTypeFace(Typeface tc) {
        mPaint.setTypeface(tc);
        mTitlePaint.setTypeface(tc);
    }

    public void setBgBitmap(Bitmap BG) {
        Log.d("setBgBitmap", "BG===" + BG);
        mBookPageBg = BG;
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

    public void convertBatteryBitmap() {
        ProgressBar batteryView = (ProgressBar) LayoutInflater.from(mContext).inflate(R.layout.layout_battery_progress, null);
        batteryView.setProgressDrawable(ContextCompat.getDrawable(mContext,
                SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_NIGHT_MODE) ?
                        R.drawable.seekbar_battery_night_bg : R.drawable.seekbar_battery_bg));
        batteryView.setProgress(battery);
        batteryView.setDrawingCacheEnabled(true);
        batteryView.measure(View.MeasureSpec.makeMeasureSpec(mBatteryWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(ScreenUtil.dpToPxInt(10), View.MeasureSpec.EXACTLY));
        batteryView.layout(0, 0, batteryView.getMeasuredWidth(), batteryView.getMeasuredHeight());
        batteryView.buildDrawingCache();
        //batteryBitmap = batteryView.getDrawingCache();
        // tips: @link{https://github.com/JustWayward/BookReader/issues/109}
        batteryBitmap = Bitmap.createBitmap(batteryView.getDrawingCache());
        batteryView.setDrawingCacheEnabled(false);
        batteryView.destroyDrawingCache();
    }

    public void setBattery(int battery) {
        this.battery = battery;
        convertBatteryBitmap();
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void recycle() {
        if (mBookPageBg != null && !mBookPageBg.isRecycled()) {
            mBookPageBg.recycle();
            mBookPageBg = null;
        }

        if (batteryBitmap != null && !batteryBitmap.isRecycled()) {
            batteryBitmap.recycle();
            batteryBitmap = null;
        }
    }
}
