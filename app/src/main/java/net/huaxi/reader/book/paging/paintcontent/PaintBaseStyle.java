package net.huaxi.reader.book.paging.paintcontent;

import android.graphics.Typeface;

import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.book.paging.BookContentPaint;

/***
 * 客户端中维护的画笔的状态提供的{@link BookContentSettings}为其子类调用实现分开控制
 */
abstract class PaintBaseStyle implements BookContentPaint.IBookContentPaintStyle {

    protected BookContentSettings mBookContentSettings;

    PaintBaseStyle() {
        mBookContentSettings =
                BookContentSettings.getInstance();
    }

    @Override
    public Typeface getTypeface() {
        return Typeface.DEFAULT;
    }

    @Override
    public float getStrokeWidth() {
        return 0;
    }
}
