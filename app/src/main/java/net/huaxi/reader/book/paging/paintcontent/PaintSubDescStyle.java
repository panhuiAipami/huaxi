package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/**
 * taoyingfeng
 * 2016/1/26.
 */
public class PaintSubDescStyle extends PaintBaseStyle {

    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.AUTO_SUB;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getDefaultTextSize();
    }

}
