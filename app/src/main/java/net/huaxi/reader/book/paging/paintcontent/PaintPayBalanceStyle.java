package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;

/**
 * Function:    支付页面-余额样式
 * Author:      taoyf
 * Create:      16/9/1
 * Modtime:     16/9/1
 */
public class PaintPayBalanceStyle extends PaintBaseStyle {
    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.PAY_BALANCE;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getPayBalanceColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getPayBalanceTextSize();
    }
}
