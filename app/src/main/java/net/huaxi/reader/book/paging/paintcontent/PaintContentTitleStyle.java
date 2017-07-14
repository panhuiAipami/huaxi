package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint.BOOK_CONTENT_PAIN_TYPE;

/**
 * 阅读页面正文标题的画笔样式
 */
public class PaintContentTitleStyle extends PaintBaseStyle {


    @Override
    public BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BOOK_CONTENT_PAIN_TYPE.CONTENT_TITLE;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getTitleTextSize();
    }
}
