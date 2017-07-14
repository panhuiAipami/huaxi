package net.huaxi.reader.book;

/**
 * 章节内容
 * Created by taoyingfeng on 2015/12/3.
 */
public class BookChapterInfo {

    private String bookId;
    private String chapterId;
    private String chapterName;
    private int startIndex = 0;
    private byte[] contentBytes;
    private String encoding = FileConstant.ENCODING_UTF_8;
    private int errorCode;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public byte[] getContentBytes() {
        return contentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return " bookId = " + bookId + ", chapterId = " + chapterId + ", chapterName = " + chapterName + ", startIndex = " + startIndex +
                ", encoding = " + encoding + ", errorCode" + errorCode + ", content =" + new String(contentBytes);
    }
}
