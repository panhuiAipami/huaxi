package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.book.paging.BookContentPaint.BOOK_CONTENT_PAIN_TYPE;

/***
 * 绘制按钮上的文字的颜色的文案样式
 *
 * @author Think
 */
public class PaintButtonTextStyle extends PaintBaseStyle {


    @Override
    public BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BOOK_CONTENT_PAIN_TYPE.PAY_BUTTON;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getBuyButtonTextColor();
    }

    @Override
    public int getTextSize() {
        return mBookContentSettings.getButtonTextSize();
    }

}
