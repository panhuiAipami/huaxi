package net.huaxi.reader.book.paging.paintcontent;

import android.graphics.Typeface;

import net.huaxi.reader.book.paging.BookContentPaint;
import net.huaxi.reader.common.AppContext;

import net.huaxi.reader.R;

/**
 * 首页版权-书名样式
 * ryantao
 * 16/4/11.
 */
public class PaintCRBookName extends PaintBaseStyle {

    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.CR_BOOK_NAME;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getTextColor();
    }

    @Override
    public int getTextSize() {
//        return mBookContentSettings.getDefaultTextSize();
        return AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.copyright_book_name_size);
    }

    @Override
    public Typeface getTypeface() {
        return Typeface.DEFAULT_BOLD;
    }
}
