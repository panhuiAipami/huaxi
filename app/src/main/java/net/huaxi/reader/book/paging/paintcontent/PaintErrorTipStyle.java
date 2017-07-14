package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/**
 * taoyingfeng
 * 2016/1/12.
 */
public class PaintErrorTipStyle extends PaintBaseStyle {
    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.ERROR_TIPS;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getAutoSubTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getTextColor();
    }
}
