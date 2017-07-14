package net.huaxi.reader.book.paging.paintcontent;

import android.graphics.Typeface;

import net.huaxi.reader.book.paging.BookContentPaint;

/***
 * 绘制阅读页地步的文案的画笔状态
 *
 * @author Think
 */
public class PaintBottomStyle extends PaintBaseStyle {

    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.BOTTOM;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getBottomTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getBottomTextSize();
    }

    @Override
    public Typeface getTypeface() {
        return mBookContentSettings.getTypeface();
    }

}
