package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/**
 * Function:    原价中的横线样式
 * Author:      taoyf
 * Create:      16/9/6
 * Modtime:     16/9/6
 */
public class PaintPayPriceLineStyle extends PaintBaseStyle {

    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.PAY_PRICE_LINE;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getPayBalanceColor();
    }

    @Override
    public int getTextSize() {
        return 0;
    }

    @Override
    public float getStrokeWidth() {
        return mBookContentSettings.getPayPriceLineWidthSize();
    }
}
