package net.huaxi.reader.book.paging;

import android.graphics.Paint;
import android.graphics.Typeface;

import net.huaxi.reader.book.paging.paintcontent.PaintAutoSubStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintButtonTextStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintCRAuthor;
import net.huaxi.reader.book.paging.paintcontent.PaintCRDesc;
import net.huaxi.reader.book.paging.paintcontent.PaintContentTitleStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintErrorTipStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintPayBalanceStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintPayDiscountStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintPayPriceLineStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintPaySplitLineStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintSubDescStyle;

import java.util.HashMap;

import net.huaxi.reader.book.paging.paintcontent.PaintBottomStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintCRBookName;
import net.huaxi.reader.book.paging.paintcontent.PaintContentStyle;
import net.huaxi.reader.book.paging.paintcontent.PaintTopStyle;

/***
 * 维护绘制过程中的画笔的状态.
 */
public class BookContentPaint extends Paint {


    private HashMap<String, IBookContentPaintStyle> mStyleCache = new HashMap<String, IBookContentPaintStyle>();
    private BOOK_CONTENT_PAIN_TYPE mPaintType;

    public BookContentPaint() {
        super(Paint.ANTI_ALIAS_FLAG);
        setTextAlign(Align.LEFT);
    }

    private void resetPaint(BOOK_CONTENT_PAIN_TYPE type) {

        if (mPaintType == type) return;

        mPaintType = type;

        IBookContentPaintStyle sStyle = mStyleCache.get(type.toString());
        if (sStyle == null) {
            sStyle = generStyle(type);
            mStyleCache.put(type.toString(), sStyle);
        }

        setColor(sStyle.getTextColor());
        setTextSize(sStyle.getTextSize());
        setTypeface(sStyle.getTypeface());
        setStrokeWidth(sStyle.getStrokeWidth());
//        LogUtils.debug(" strokeWidth = "+getStrokeWidth());
//        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    private IBookContentPaintStyle generStyle(BOOK_CONTENT_PAIN_TYPE type) {
        IBookContentPaintStyle sStyle = null;
        switch (type) {
            case CONTENT:
                sStyle = new PaintContentStyle();
                break;
            case CONTENT_DESC:
                sStyle = new PaintSubDescStyle();
                break;
            case BOTTOM:
                sStyle = new PaintBottomStyle();
                break;
            case TOP:
                sStyle = new PaintTopStyle();
                break;
            case PAY_DISCOUNT:
                sStyle = new PaintPayDiscountStyle();
                break;
            case PAY_BALANCE:
                sStyle = new PaintPayBalanceStyle();
                break;
            case PAY_PRICE_LINE:
                sStyle = new PaintPayPriceLineStyle();
                break;
            case PAY_SPLIT_LINE:
                sStyle = new PaintPaySplitLineStyle();
                break;
            case AUTO_SUB:
                sStyle = new PaintAutoSubStyle();
                break;
            case PAY_BUTTON:
                sStyle = new PaintButtonTextStyle();
                break;
            case CONTENT_TITLE:
                sStyle = new PaintContentTitleStyle();
                break;
            case ERROR_TIPS:
                sStyle = new PaintErrorTipStyle();
                break;
            case CR_BOOK_NAME:
                sStyle = new PaintCRBookName();
                break;
            case CR_AUTHOR:
                sStyle = new PaintCRAuthor();
                break;
            case CR_DESC:
                sStyle = new PaintCRDesc();
                break;
            default:
                break;
        }

        return sStyle;
    }

    public void changeStyleToContent() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.CONTENT);
    }

    public void changeStyleToDesc() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.CONTENT_DESC);
    }

    public void changeStyleToContentTitle() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.CONTENT_TITLE);
    }

    public void changeStyleToPayDiscount() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.PAY_DISCOUNT);
    }

    public void changeStyleToTop() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.TOP);
    }

    public void changeStyleToBottom() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.BOTTOM);
    }

    public void changeStyleToPayButton() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.PAY_BUTTON);
    }

    public void changeStyleToAutoSub() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.AUTO_SUB);
    }

    public void changeStyleToBalance(){
        resetPaint(BOOK_CONTENT_PAIN_TYPE.PAY_BALANCE);
    }

    public void changeStyleToPaySplitLine() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.PAY_SPLIT_LINE);
    }

    public void changeStyleToPayPriceLine(){
        resetPaint(BOOK_CONTENT_PAIN_TYPE.PAY_PRICE_LINE);
    }


    public void changeStyleToErrorTips() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.ERROR_TIPS);
    }

    public void changeStyleToCRBookName() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.CR_BOOK_NAME);
    }

    public void changeStyleToCRAuthor() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.CR_AUTHOR);
    }

    public void changeStyleToCRDesc() {
        resetPaint(BOOK_CONTENT_PAIN_TYPE.CR_DESC);
    }

    public void release() {
        if (mStyleCache != null) {
            mStyleCache.clear();
        }
    }

    public static enum BOOK_CONTENT_PAIN_TYPE {
        PAY_BALANCE, PAY_DISCOUNT, PAY_SPLIT_LINE,PAY_PRICE_LINE, CONTENT, CONTENT_DESC, CONTENT_TITLE, TOP, BOTTOM, PAY_BUTTON, AUTO_SUB, ERROR_TIPS,
        CR_BOOK_NAME, CR_AUTHOR, CR_DESC
    }

    public interface IBookContentPaintStyle {

        BOOK_CONTENT_PAIN_TYPE getStyleType();

        int getTextColor();

        int getTextSize();

        Typeface getTypeface();

        float getStrokeWidth();
    }
}
