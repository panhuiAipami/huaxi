package net.huaxi.reader.book.paging;

import net.huaxi.reader.book.BookContentLineInfo;
import net.huaxi.reader.book.render.ReadPageState;

import java.util.List;

/**
 * 书籍渲染页面内容.
 * Created by taoyingfeng on 2015/12/3.
 */
public class PageContent {
    //章节ID
    private String chapterId;
    //章节名
    private String chapterName;
    //当前章节页码
    private int pageNo;
    //当前章节开始字节
    private int startIndex = -1;
    //当前章节最后字节
    private int endIndex = -1;
    //阅读百分比
    private float percent = 0;
    //每行的内容.
    private List<BookContentLineInfo> lines;
    //错误码.
    private int errorCode;
    //章节阅读状态.
    private int bookType = ReadPageState.BOOKTYPE_NORMAL;
    //价格(未订阅使用)
    private String price;
    //是否自动订阅checKBox
    private boolean autoSub = true;
    //是否有折扣
    private boolean hasDiscount;
    //原始价格
    private String originPrice;


    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<BookContentLineInfo> getLines() {
        return lines;
    }

    public void setLines(List<BookContentLineInfo> lines) {
        this.lines = lines;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAutoSub() {
        return autoSub;
    }

    public void setAutoSub(boolean autoSub) {
        this.autoSub = autoSub;
    }

    public boolean isHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public String getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(String originPrice) {
        this.originPrice = originPrice;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("chapterId = " + chapterId);
        sb.append(", chapterName = " + chapterName);
        sb.append("，pageNo = " + pageNo);
        sb.append(", startIndex = " + startIndex);
        sb.append(", endIndex = " + endIndex);
        sb.append(", autosub = " + autoSub);
        sb.append(", hasdiscount = " + hasDiscount);
        sb.append(", originprice = " + originPrice);
        if (lines != null && lines.size() > 0) {
            for (BookContentLineInfo lineInfo : lines) {
                if (lineInfo != null) {
                    sb.append("\r\n, lineinfo = " + lineInfo.toString());
                }
            }
        }
        return sb.toString();
    }
}
