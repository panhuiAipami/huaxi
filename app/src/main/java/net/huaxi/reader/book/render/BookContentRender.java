package net.huaxi.reader.book.render;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.ImageUtils;
import com.tools.commonlibs.tools.LogUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.book.BookContentLineInfo;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.book.paging.BookContentPaint;
import net.huaxi.reader.book.paging.PageContent;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.statistic.ReportUtils;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * 内容渲染
 * Created by taoyingfeng
 * 2015/12/4.
 */
public class BookContentRender {

    private static final String DIANDIANDIAN = "...";
    private final int IMG_W, IMG_H;
    private Bitmap mBackgroundBitmap;
    private BookContentPaint mPaint;
    private String mChapterTitle;  //章节标题，可以使书名、或者是章节名.
    private int readpage_pay_balanc,Present_price;
    public static int p,b,h;
    public static int sum;

    public BookContentRender() {
        mPaint = new BookContentPaint();
        IMG_W = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_width);
        IMG_H = AppContext.context().getResources().getDimensionPixelSize(R.dimen.page_pay_button_height);
    }

    public void setBackgroundBitmap(Bitmap mBackgroundBitmap) {
        this.mBackgroundBitmap = mBackgroundBitmap;
    }

    public void setChapterName(String mChapterName) {
        this.mChapterTitle = mChapterName;
    }

    /**
     * 绘制正文页面
     *
     * @param canvas
     * @param list
     */
    public void drawStateNormalCanvas(Canvas canvas, List<BookContentLineInfo> list) {
        drawEmptyView(canvas);
        drawNormalText(canvas, list);
    }

    /**
     * 绘制登录界面.
     *
     * @param canvas
     * @param pageContent
     */
    public void drawStateLoginCancas(Canvas canvas, PageContent pageContent) {

        drawEmptyView(canvas);
        //获取按钮背景图片.
        Bitmap bitmap = getButtonBg();

//        //支付按钮
//        int y = drawPayButton(canvas, pageContent.getBookType(), bitmap);
//        //关闭按钮
//        drawCloseButton(canvas, y, bitmap);
//        //计算隐藏自动订阅界面的高度.
//        y = y - CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_auto_subscribe_margin);
//        //余额
//        y = drawPayBalance(canvas, 0, y);
//        //价格
//        String price = String.format(CommonApp.context().getString(R.string.readpage_pay_price), pageContent.getPrice());
//        int disCountToMarginX = (int) (BookContentSettings.getInstance().getScreenWidth() - (int) mPaint.measureText(price)) / 2;
//        y = drawPayDiscount(canvas, price, disCountToMarginX,y);
//
//        drawDescText(canvas, pageContent.getLines(), y);

        //登录按钮(根据类型判断)
        int y = drawPayButton(canvas, pageContent.getBookType(), bitmap);
        //折后价格(现价)
        String price = String.format(CommonApp.context().getString(R.string.readpage_pay_price), pageContent.getPrice());
        int disCountToMarginX = (int) (BookContentSettings.getInstance().getScreenWidth() - (int) mPaint.measureText(price)) / 2;
        y = drawPayDiscount(canvas, price,disCountToMarginX, y);
        if (pageContent.isHasDiscount()) {
            //原价
            String originPrice = String.format(CommonApp.context().getString(R.string.readpage_pay_originprice), pageContent.getOriginPrice());
            y = drawPayInitialPrice(canvas, originPrice,disCountToMarginX, y);
        }
        //绘制分割线
        y = drawPaySplitLine(canvas, y);
        //绘制描述
        drawDescText(canvas, pageContent.getLines(), y);

    }

    /**
     * 绘制购买界面(余额不足)
     *
     * @param canvas      画布
     * @param pageContent 渲染页面
     * @param balance     余额
     */
    public void drawStatePayCancas(Canvas canvas, PageContent pageContent, int balance,int petal, boolean autoSub) {
        
        drawEmptyView(canvas);
        Bitmap bitmap = getButtonBg();

        //支付按钮
        int y = drawPayButton(canvas, pageContent.getBookType(), bitmap);

        //关闭按钮
//        drawCloseButton(canvas, y, bitmap);
        int y2 = y + IMG_H;
        y2 = drawAutoSubscribe(canvas, y2, autoSub);
        //包月作品,绘画开通会员入口
//        if (DataSourceManager.getSingleton().getIsMonthly()) {
//        drawMonthlyTips(canvas, y2);
//        }
        //折后价格(现价)
        String price = String.format(CommonApp.context().getString(R.string.readpage_pay_price), pageContent.getPrice());
        p= Integer.parseInt(pageContent.getPrice());
//        h=Integer.parseInt(pageContent.get)
//        Log.i("TAG", "drawStatePayCancas: 现价="+p);
        LogUtils.debug(" 章节价格"+price);
        int disCountToMarginX = (int) (BookContentSettings.getInstance().getScreenWidth() - (int) mPaint.measureText(price)) / 2;;
        y = drawPayDiscount(canvas, price,disCountToMarginX, y);
        if(pageContent.isHasDiscount()) {
            //原价
            String originPrice = String.format(CommonApp.context().getString(R.string.readpage_pay_originprice), pageContent.getOriginPrice());
            y = drawPayInitialPrice(canvas, originPrice, disCountToMarginX, y);
        }
        //余额
        y = drawPayBalance(canvas, balance,petal,disCountToMarginX, y);
        //绘制分割线
        y = drawPaySplitLine(canvas, y);
        //绘制描述
        drawDescText(canvas, pageContent.getLines(), y);
        releaseBitmap(bitmap);
    }

    /**
     * 绘画加载状态
     *
     * @param canvas
     */
    @DebugLog
    public void drawStateLoading(Canvas canvas) {
        drawEmptyView(canvas);
//        Bitmap bitmap = getLoadingBg();
        //按钮
//        int y = drawPayButton(canvas, ReadPageState.BOOKTYPE_LOADING, bitmap);

//        releaseBitmap(bitmap);

        mPaint.changeStyleToContent();
        String[] buttonTexts = AppContext.context().getResources().getStringArray(R.array.readpage_button_text);
        String buyText = CommonApp.getInstance().getString(R.string.readpage_pay_button);  //充值Text;
        if (buttonTexts != null && buttonTexts.length >= ReadPageState.BOOKTYPE_LOADING) {
            buyText = buttonTexts[ReadPageState.BOOKTYPE_LOADING-1];
        }
        int btnTextX = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(buyText)) / 2;
        int y = (int) (BookContentSettings.getInstance().getScreenHeight() + mPaint.getTextSize()) / 2;
        canvas.drawText(buyText, btnTextX, y, mPaint);

    }

    @DebugLog
    public void drawStateErrorOrLogin(Canvas canvas, int bookType) {
        drawEmptyView(canvas);
        Bitmap bitmap = getButtonBg();
        //支付按钮
        int y = drawPayButton(canvas, bookType, bitmap);
        //取消按钮
        drawCloseButton(canvas, y, bitmap);
        String title = "";
        String[] ars = CommonApp.getInstance().getResources().getStringArray(R.array.readpage_state_tips);
        if (ars != null && ars.length > 0) {
            title = ars[bookType - 1];
        }
        drawErrorTips(canvas, title, y - 2 * CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen
                .page_auto_subscribe_to_button));
        releaseBitmap(bitmap);
    }


    /**
     * 绘画版本首页
     */
    public void drawStateCopyRight(Canvas canvas) {
        //绘画背景，但不画顶部标题
        drawBackground(canvas);
        int titleToBar = AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.copyright_title_to_topbar);
        int authorToBar = AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.copyright_author_to_title);
        int crToBottom = Utility.getScreenHeight() - AppContext.getInstance().getResources().getDimensionPixelSize(R.dimen.copyright_cr_to_bottom);
        String bookName = DataSourceManager.getSingleton().getBookName();
        String author = DataSourceManager.getSingleton().getBookAuthor();

        String copyRight = "本书由花溪小说网电子版制作与发行";
        String copyRight2 = "版权所有·侵权必究";

        mPaint.changeStyleToCRBookName();
        computeAndDrawTitle(canvas, bookName, titleToBar);
        mPaint.changeStyleToCRAuthor();
        computeAndDrawTitle(canvas, author, authorToBar);
        mPaint.changeStyleToCRDesc();
        computeAndDrawTitle(canvas, copyRight2, crToBottom);

        int y = crToBottom - Utility.getFontHeight(mPaint.getTextSize()) - AppContext.getInstance().getResources()
                .getDimensionPixelSize(R.dimen.copyright_cr_desc_margin);

        mPaint.changeStyleToCRAuthor();
        computeAndDrawTitle(canvas, copyRight, y);

    }

    /**
     * 绘制空的VIEW
     *
     * @param canvas 画布
     */
    public void drawEmptyView(Canvas canvas) {
        drawBackground(canvas);
        drawTop(canvas);

    }

    /**
     * 绘画文本
     */
    private void drawNormalText(Canvas canvas, List<BookContentLineInfo> list) {
        mPaint.changeStyleToContent();
        int marginTop = BookContentSettings.getInstance().getMarginTop();
        int top = BookContentSettings.getInstance().getTopToTitle() + marginTop;
        if (canvas != null && list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                BookContentLineInfo lineInfo = list.get(i);
                if (lineInfo != null) {
                    drawContent(canvas, lineInfo.getLineType(), lineInfo.getContent(), lineInfo.getLineXys());
                }
            }
        }
    }

    /**
     * 绘画描述文本
     */
    private void drawDescText(Canvas canvas, List<BookContentLineInfo> list, int maxY) {
        mPaint.changeStyleToDesc();
//        int marginTop = BookContentSettings.getInstance().getMarginTop();
//        int top = BookContentSettings.getInstance().getTopToTitle() + marginTop;
//        final int textSize = BookContentSettings.getInstance().getDefaultTextSize();
//        final int lineSpace = (int) BookContentSettings.getInstance().getLineSpace();   //行间距
        final int margin = CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_auto_subscribe_to_button);

        if (canvas != null && list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                BookContentLineInfo lineInfo = list.get(i);
                if (lineInfo != null) {
                    if (lineInfo.getLineXys().length >= 2) {
                        float y = lineInfo.getLineXys()[1];
                        if (y > maxY - margin) {
                            break;
                        }
                    }
                    drawDesc(canvas, lineInfo.getContent(), lineInfo.getLineXys());
                    LogUtils.debug("描述内容 = " + lineInfo.toString());
                }
            }
        }
    }

    /**
     * 用画笔 画出背景
     */
    public void drawBackground(Canvas canvas) {
        try {
            if (mBackgroundBitmap == null || mBackgroundBitmap.isRecycled()) {
                canvas.drawColor(BookContentSettings.getInstance().getBackgroundColor());
            } else {
                if (!mBackgroundBitmap.isRecycled()) {
                    // 9.3.9将背景设置为图片，将小图片拼接
                    synchronized (mBackgroundBitmap) {
                        int width = mBackgroundBitmap.getWidth();
                        int height = mBackgroundBitmap.getHeight();

                        float scaleWidth = ((float) BookContentSettings.getInstance().getScreenWidth()) / width;
                        float scaleHeight = ((float) BookContentSettings.getInstance().getScreenHeight()) / height;
                        for (int i = 0; i < scaleWidth + 1; i++) {
                            for (int j = 0; j < scaleHeight + 1; j++) {
                                canvas.drawBitmap(mBackgroundBitmap, i * width, j * height, null);
                            }
                        }
                    }
                } else {
                    canvas.drawColor(BookContentSettings.getInstance().getBackgroundColor());
                }
            }
        } catch (Exception e) {
            canvas.drawColor(BookContentSettings.getInstance().getBackgroundColor());
            ReportUtils.reportError(e);
        }
    }


    /**
     * 用画笔 来画顶部的章节信息
     *
     * @param canvas
     */
    private void drawTop(Canvas canvas) {

        mPaint.changeStyleToTop();

        if (mChapterTitle != null && mChapterTitle.length() > 0) {

            int mTitleDianDianDianLength = getStringWidth(mPaint, DIANDIANDIAN);

            int topHeight = BookContentSettings.getInstance().getMarginTop();

            final int screenWidth = BookContentSettings.getInstance().getScreenWidth() - BookContentSettings.getInstance().getMarginLeft
                    () - BookContentSettings.getInstance().getMarginRight() - mTitleDianDianDianLength;

            final String tmpStr = getLineString(mPaint, mChapterTitle, screenWidth);

            canvas.drawText(tmpStr, BookContentSettings.getInstance().getMarginLeft(), topHeight, mPaint);

        }
    }

    private int drawCloseButton(Canvas canvas, int y, Bitmap bitmap) {
        int lelf = (BookContentSettings.getInstance().getScreenWidth() - IMG_W) / 2;
//        int top = (BookContentSettings.getInstance().getScreenHeight() - IMG_H) / 3 * 2;
        int top = y + CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_auto_subscribe_to_button) + IMG_H;
        // Log4an.e("2", "lelf=" + lelf + ",top=" + top);
        Rect rect = new Rect(lelf, top, lelf + IMG_W, top + IMG_H);
//        drawNinepath(canvas, R.drawable.btn_common_rose_d3, rect);
        mPaint.changeStyleToPayButton();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, rect.left, rect.top, null);
        }

        String buyText = CommonApp.getInstance().getString(R.string.close);  //关闭ext;
        int btnTextX = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(buyText)) / 2;
        final int textAdjustOffset = (int) ((mPaint.getFontMetrics().ascent - mPaint.getFontMetrics().top) - (mPaint.getFontMetrics()
                .bottom - mPaint.getFontMetrics().descent));
        canvas.drawText(buyText, btnTextX, top + (IMG_H + mPaint.getTextSize()) / 2 - textAdjustOffset, mPaint);
        return rect.top;
    }

    /***
     * TODO:绘制付费按钮
     *
     * @param canvas
     * @param bookType 阅读状态.
     */
    private int drawPayButton(Canvas canvas, int bookType, Bitmap bitmap) {
        int left = (BookContentSettings.getInstance().getScreenWidth() - IMG_W) / 2;
        int top = (BookContentSettings.getInstance().getScreenHeight() - IMG_H) / 3 * 2;
        Rect rect = new Rect(left, top, left + IMG_W, top + IMG_H);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, left, top, null);
        }
//        drawNinepath(canvas, R.drawable.btn_common_rose_d3, rect);
        mPaint.changeStyleToPayButton();
        String[] buttonTexts = AppContext.context().getResources().getStringArray(R.array.readpage_button_text);
        String buyText = CommonApp.getInstance().getString(R.string.readpage_pay_button);  //充值Text;
        //花贝与花瓣的总和
        sum=BookContentRender.h+BookContentRender.b;
        if(readpage_pay_balanc<Present_price||BookContentRender.p>BookContentRender.b&&BookContentRender.p!=0&&p>h){
            if(UserHelper.getInstance().isLogin()){
                buyText=buttonTexts[4];
            }else {
                buyText=buttonTexts[5];


            }

        }else if(buttonTexts != null && buttonTexts.length >= bookType) {
            buyText = buttonTexts[bookType - 1];
        }
        int btnTextX = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(buyText)) / 2;
        final int textAdjustOffset = (int) ((mPaint.getFontMetrics().ascent - mPaint.getFontMetrics().top) - (mPaint.getFontMetrics()
                .bottom - mPaint.getFontMetrics().descent));
        canvas.drawText(buyText, btnTextX, top + (IMG_H + mPaint.getTextSize()) / 2 - textAdjustOffset, mPaint);
        return rect.top;
    }

    /**
     * 获取按钮的背景图片.
     *
     * @return
     */
    private Bitmap getButtonBg() {
        int theme = BookContentSettings.getInstance().getTheme();
        Drawable d = null;
        if (Constants.THEME_NIGHT == theme) {
            d = AppContext.context().getResources().getDrawable(R.drawable.readpage_pay_night_bg);
        } else {
            d = AppContext.context().getResources().getDrawable(R.drawable.readpage_pay_day_bg);
        }
        Bitmap bitmap = Utility.drawableToBitmap(d);
        return bitmap;
    }

    /**
     * 获取loading按钮的背景
     *
     * @return
     */
    private Bitmap getLoadingBg() {
        int theme = BookContentSettings.getInstance().getTheme();
        Drawable d = null;
        if (Constants.THEME_NIGHT == theme) {
            d = AppContext.context().getResources().getDrawable(R.drawable.readpage_loading_btn_night_bg);
        } else {
            d = AppContext.context().getResources().getDrawable(R.drawable.readpage_loading_btn_day_bg);
        }
        Bitmap bitmap = Utility.drawableToBitmap(d);
        return bitmap;
    }

    private void releaseBitmap(Bitmap bitmap) {
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 画按钮的.9图片
     *
     * @param c
     * @param id
     * @param r1
     */
    private void drawNinepath(Canvas c, int id, Rect r1) {
        Bitmap bmp = BitmapFactory.decodeResource(CommonApp.context().getResources(), id);
        NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
        patch.draw(c, r1);
    }


    /**
     * 绘制支付界面分割线
     * @param canvas
     * @param y
     * @return
     */
    private int drawPaySplitLine(Canvas canvas, int y) {
        mPaint.changeStyleToPaySplitLine();
        int lineY = y - CommonApp.getInstance().getResources().getDimensionPixelSize
                (R.dimen.page_blance_to_line);

        canvas.drawLine(BookContentSettings.getInstance().getMarginLeft(), lineY, BookContentSettings.getInstance().getScreenWidth() -
                BookContentSettings.getInstance().getMarginRight(), lineY, mPaint);

        return lineY - CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_blance_to_line);
    }

    private void drawLineInOriginPrice(Canvas canvas,int startX,int endX,int y) {
        mPaint.changeStyleToPayPriceLine();
        canvas.drawLine(startX, y, endX, y, mPaint);
    }

    /***
     * 绘制余额
     *
     * @param canvas  画布
     * @param balance 余额
     * @param y       纵坐标
     */
    private int drawPayBalance(Canvas canvas, int balance,int petal, int x, int y) {
        mPaint.changeStyleToBalance();
        //将用户的花贝与花瓣余额传递进去
        String payTip = String.format(CommonApp.getInstance().getString(R.string.readpage_pay_balance),new Object[]{balance,petal});
        b=balance;
        h=petal;
        readpage_pay_balanc=balance;

//        final int payTipX = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(payTip)) / 2;

        final int payTipY = y - CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_pay_balance_to_origin);

        canvas.drawText(payTip, x, payTipY, mPaint);

//        canvas.drawLine(0,payTipY, BookContentSettings.getInstance().getScreenWidth(),payTipY,mPaint);

        return payTipY - Utility.getFontHeight(mPaint.getTextSize());
    }

    /**
     * 绘画原始价格
     * @param canvas
     * @param originPrice
     * @param y
     * @return
     */
    private int drawPayInitialPrice(Canvas canvas, String originPrice, int x, int y) {
        mPaint.changeStyleToBalance();
//        final int payTipX = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(originPrice)) / 2;
        final int payTipY = y - CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_pay_balance_to_origin);
        canvas.drawText(originPrice, x, payTipY, mPaint);

        int textSize = Utility.getFontHeight(mPaint.getTextSize());

        //绘制横划线
        int lineY = payTipY - (textSize / 3);
        float startX = x + mPaint.measureText(originPrice, 0, originPrice.indexOf(" ") + 1);
        float end = x + mPaint.measureText(originPrice);

        drawLineInOriginPrice(canvas, (int) startX, (int) end, lineY);

        return payTipY - textSize;
    }

    /***
     * 绘制现价(折后价)
     */
    private int drawPayDiscount(Canvas canvas, String title,int x, int y) {
        mPaint.changeStyleToPayDiscount();
        final int payTipY = y - CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_pay_discount_to_button);
        canvas.drawText(title, x, payTipY, mPaint);
        return payTipY - Utility.getFontHeight(mPaint.getTextSize());
        //        return computeAndDrawTitle(canvas, title, y);

    }


    /**
     * 绘画非会员作品提示。
     *
     * @param canvas 画布
     * @param y      坐标
     * @return
     */
    private int drawMonthlyTips(Canvas canvas, int y) {
        mPaint.changeStyleToAutoSub();
        String monthtips = CommonApp.getInstance().getString(R.string.readpage_monthly);

        final int temp_x = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(monthtips)) / 2;

        final int temp_y = y  + (int) mPaint.getTextSize() + CommonApp.getInstance().getResources()
                .getDimensionPixelSize(R.dimen.page_auto_subscribe_to_button);

        canvas.drawText(monthtips, temp_x, temp_y, mPaint);

        System.err.println("drawMonthlyTips y = " + (temp_y - (int) mPaint.getTextSize()));

        return temp_y + Utility.getFontHeight(mPaint.getTextSize());

    }

    /**
     * 绘制自动订阅
     */
    private int drawAutoSubscribe(Canvas canvas, int y, boolean autoAub) {
        mPaint.changeStyleToAutoSub();

        String autoPay = CommonApp.context().getString(R.string.readpage_pay_auto);

        int textWidth = (int) mPaint.measureText(autoPay);
//        final int payTipX = (int) (BookContentSettings.getInstance().getScreenWidth() - mPaint.measureText(autoPay) - 18) / 2;

        final int payTipY = y + CommonApp.getInstance().getResources().getDimensionPixelSize(R.dimen.page_auto_subscribe_to_button)
                +Utility.getFontHeight(mPaint.getTextSize()) ;

        int checkBoxX = drawAutoSubCheckBox(canvas, autoAub, textWidth, payTipY);

        canvas.drawText(autoPay, checkBoxX, payTipY, mPaint);

        return payTipY;

    }

    private int drawAutoSubCheckBox(Canvas canvas, boolean checked, int textWidth, int y) {
        final int balance = (int) (Constants.MARGIN_AUTO_TO_TEXT * Utility.getDensity());
        final int fontsize = Utility.getFontHeight(mPaint.getTextSize());
        final int boxWidth = fontsize + balance;
        final int bY = y + Utility.getFontDescent(mPaint.getTextSize()) - fontsize / 2 - boxWidth / 2;
        final int payTipX = (int) (BookContentSettings.getInstance().getScreenWidth() - textWidth - boxWidth) / 2;
        //// FIXME: 2015/12/16 自动订阅checkbox需要替换
        Bitmap oldBitmap = null;
        int bitmapRes = 0;
        boolean isNight = BookContentSettings.getInstance().getTheme() == Constants.THEME_NIGHT ? true : false;
//        if (!isNight) {  //白天模式
            if (checked) {
                oldBitmap = BitmapFactory.decodeResource(CommonApp.resources(), R.mipmap.readpage_checkbox_day_selected);
            } else {
                oldBitmap = BitmapFactory.decodeResource(CommonApp.resources(), R.mipmap.readpage_checkbox_day_normal);
            }
//        } else {  //夜晚模式∂
//            if (checked) {
//                oldBitmap = BitmapFactory.decodeResource(CommonApp.resources(), R.mipmap.readpage_checkbox_night_selected);
//            } else {
//                oldBitmap = BitmapFactory.decodeResource(CommonApp.resources(), R.mipmap.readpage_checkbox_night_selected);
//            }
//        }
        Bitmap bitmap = ImageUtils.scaleImage(oldBitmap, boxWidth, boxWidth);
        canvas.drawBitmap(bitmap, payTipX, bY, null);
//        canvas.drawLine(0, bY, 1080, bY, mPaint);
//        canvas.drawLine(0, bY + boxWidth, 1080, bY + boxWidth, mPaint);
        releaseBitmap(oldBitmap);
        releaseBitmap(bitmap);
        return payTipX + boxWidth;
    }

    /**
     * 绘制错误提示.
     */

    private void drawErrorTips(Canvas canvas, String title, int y) {
        mPaint.changeStyleToErrorTips();
        computeAndDrawTitle(canvas, title, y);
    }

    private int computeAndDrawTitle(Canvas canvas, String title, int y) {
        if (!TextUtils.isEmpty(title)) {
            int margin = BookContentSettings.getInstance().getMarginLeft();
            int screenWidth = BookContentSettings.getInstance().getScreenWidth();
            int oneW = (int) mPaint.measureText("国");
            int txtWidth = (int) mPaint.measureText(title);
            int count = 0, viewCount = 0;
            y -= CommonApp.context().getResources().getDimensionPixelSize(R.dimen.page_pay_discount_to_button);
            if (txtWidth > screenWidth) {
                int a = (int) txtWidth / screenWidth;
                int b = (int) txtWidth % screenWidth;
                if (b != 0) {
                    count = a + 1;
                } else {
                    count = a;
                }
                viewCount = screenWidth / oneW;
                int length = title.length();
                int index = 0, endIndex = viewCount;
                String s[] = new String[count];
                int height = Utility.getFontHeight(BookContentSettings.getInstance().getPayDiscountTextSize());
                y -= height * (count - 1);
                for (int i = 0; i < count; i++) {
                    endIndex = index + viewCount;
                    if (endIndex >= length) {
                        endIndex = length;
                    }
                    s[i] = title.substring(index, endIndex);
                    index += viewCount;
                    int titleLineOneX = (int) (screenWidth - mPaint.measureText(s[i])) / 2;
                    canvas.drawText(s[i], titleLineOneX, y, mPaint);
                    y += height;
                }
            } else {
                int titleX = (int) (screenWidth - txtWidth) / 2;
                canvas.drawText(title, titleX, y, mPaint);
            }
            return y - Utility.getFontHeight(BookContentSettings.getInstance().getPayDiscountTextSize());
        }
        return y;
    }


    /**
     * 画正文的文字
     *
     * @param canvas  画布
     * @param content 正文内容
     * @param xys     每个字符的位置坐标
     */
    private void drawContent(Canvas canvas, int lineType, String content, float[] xys) {
//        canvas.drawLine(0, xys[1], 1080, xys[1], mPaint);
        if (BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType() == lineType) {
            mPaint.changeStyleToContentTitle();
        } else {
            mPaint.changeStyleToContent();
        }
        canvas.drawPosText(content, xys, mPaint);
    }

    /**
     * 画描述内容.
     *
     * @param canvas
     * @param content
     * @param xys
     */
    private void drawDesc(Canvas canvas, String content, float[] xys) {
        mPaint.changeStyleToDesc();
        LogUtils.debug("drawDesc TextSize = " + BookContentSettings.getInstance().getDefaultTextSize());
        canvas.drawPosText(content, xys, mPaint);
    }


    /********************************************************************
     * 工具类.
     *********************************************************************/

    /**
     * 获取字符串宽度
     *
     * @param paint 画笔
     * @param str   内容
     * @return
     */
    public int getStringWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 调整字符串长度，保证其不长于屏幕宽度
     *
     * @param paint
     * @param str
     * @param screenWidth
     * @return
     */
    private String getLineString(Paint paint, String str, int screenWidth) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);

                if (iRet >= screenWidth) {
                    return str.substring(0, j) + DIANDIANDIAN;
                }
            }
        }
        return str;
    }

}
