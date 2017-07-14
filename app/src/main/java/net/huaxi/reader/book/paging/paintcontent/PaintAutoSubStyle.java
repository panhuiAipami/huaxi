package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/****
 * 阅读页面付费按钮上的提示文案的画笔样式
 *
 * @author wyu
 */
public class PaintAutoSubStyle extends PaintBaseStyle {


    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.AUTO_SUB;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getAutoSubTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getAutoSubTextSize();
    }

}
