package net.huaxi.reader.book.paging.encode;

public abstract class IEncoding {
    public final static String ENCODING_GBK = "GBK";
    public final static String ENCODING_GB2312 = "GB2312";
    public final static String ENCODING_ASCII = "ASCII";
    public final static String ENCODING_UTF8 = "UTF-8";

    /**
     * 转换后的值
     */
    public char[] mValues = null;

    public abstract String getEncodingName();

    /**
     * 一个字符占用的最大byte数
     *
     * @return
     */
    public abstract int maxBytesCount();

    /**
     * 转成chars,用于前进读取
     *
     * @param data
     * @param offset
     * @param end
     * @return 使用了多少byte
     */
    public abstract int toChars(byte[] data, int offset, int end);

    /**
     * 反向转成chars,用于后退读取
     *
     * @param data
     * @param offset
     * @param end
     * @return 使用了多少byte
     */
    public abstract int toCharsReverse(byte[] data, int offset, int end);

    /**
     * 翻转
     *
     * @param s
     * @param start
     * @param end
     * @return
     */
    protected char[] reverse(char[] s, int start, int end) {
        if (s == null || s.length == 0) {
            return s;
        }
        int n = s.length - 1;
        int halfLength = (end - start) / 2;
        for (int i = start; i <= halfLength; i++) {
            char c = s[i];
            s[i] = s[n - i];
            s[n - i] = c;
        }
        return s;
    }
}
