package net.huaxi.reader.book.datasource.model;

/**
 * 小阅文件;
 * Created by taoyingfeng
 * 2015/12/17.
 */
public class XsFile {
    //约定header
    private String header;
    //版本号
    private String version;
    //版权
    private String author;
    //encoding
    private String encoding;
    //crc验证
    private int crc;
    //字节偏移
    private int offset;
    //文件长度
    private int length;
    //最后更新时间
    private int lastupdatetime;
    //预留字段
    private String reverse;
    //数据的真实内容
    private byte[] data;
    //文件解析状态码
    private int state;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getReverse() {
        return reverse;
    }

    public void setReverse(String reverse) {
        this.reverse = reverse;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(int lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    @Override
    public String toString() {
        return " header = " + this.header + "| version = " + this.version + "| author = " + this.author + "| encoding = " + this.encoding +
                "| crc = " + this.crc + "| length = " + this.length + "| lastupdatetime = " + this.lastupdatetime + "| offset = " +
                offset + "| reverse = " + this.reverse;
    }
}
