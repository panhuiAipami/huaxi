package net.huaxi.reader.book.paging.paintcontent;

import net.huaxi.reader.R;
import net.huaxi.reader.book.paging.BookContentPaint;
import net.huaxi.reader.common.AppContext;

/**
 * ryantao
 * 16/4/11.
 */
public class PaintCRAuthor extends PaintBaseStyle {

    @Override
    public BookContentPaint.BOOK_CONTENT_PAIN_TYPE getStyleType() {
        return BookContentPaint.BOOK_CONTENT_PAIN_TYPE.CR_AUTHOR;
    }

    @Override
    public int getTextColor() {
        return mBookContentSettings.getTextColor();
    }

    @Override
    public int getTextSize() {
//        return mBookContentSettings.getDefaultTextSize();
        return AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.copyright_book_author_size);
    }

}
