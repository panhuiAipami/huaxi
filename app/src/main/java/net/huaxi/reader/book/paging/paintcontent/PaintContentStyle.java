package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/***
 * 阅读页面正文的画笔样式
 *
 * @author wyu
 */
public class PaintContentStyle extends PaintBaseStyle {


    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.CONTENT;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getTextSize();
    }
}
