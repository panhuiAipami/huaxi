package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint;
import net.huaxi.reader.book.paging.BookContentPaint.BOOK_CONTENT_PAIN_TYPE;


/***
 * 阅读页面 折后价(现价)
 * @author wyu
 */
public class PaintPayDiscountStyle extends PaintBaseStyle {

    @Override
    public BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.PAY_DISCOUNT;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getPayDiscountTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getPayDiscountTextSize();
    }
}
