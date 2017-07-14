package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/***
 * 阅读页面的顶部的文案信息 图书名称的画笔样式
 *
 * @author wyu
 */
public class PaintTopStyle extends PaintBaseStyle {


    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.TOP;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getTopTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getTopTextSize();
    }
}
