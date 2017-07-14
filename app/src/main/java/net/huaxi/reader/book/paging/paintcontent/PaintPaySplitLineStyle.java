package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/**
 * Function:    支付页面-分割线样式
 * Author:      taoyf
 * Create:      16/9/1
 * Modtime:     16/9/1
 */
public class PaintPaySplitLineStyle extends PaintBaseStyle {

    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.PAY_SPLIT_LINE;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getPaySplitLineColor();
    }

    @Override
    public int getTextSize() {
        return (int)mBookContentSettings.getPaySplitLineWidthSize();
    }

    @Override
    public float getStrokeWidth() {
        return mBookContentSettings.getPaySplitLineWidthSize();
    }
}
