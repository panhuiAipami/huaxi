package net.huaxi.reader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/17.
 */

public class StringUtils {
    public static final String EMPTY = " ";

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return true;
        for (int i = 0; i < strLen; i++)
            if (!Character.isWhitespace(str.charAt(i)))
                return false;

        return true;
    }

    public static boolean isBlankOr(String... strs) {
        for (String str : strs) {
            return isBlank(str);
        }

        return true;
    }

    public static boolean isBlankAnd(String... strs) {
        for (String str : strs) {

            if (!isBlank(str))
                return false;

        }

        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

    public static final String URL_REG_EXPRESSION = "^(https?://)?([a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)+(/*[A-Za-z0-9/\\-_&:?\\+=//.%]*)*";
    public static final String EMAIL_REG_EXPRESSION = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";

    public static boolean isUrl(String s) {
        if (s == null) {
            return false;
        }
        return Pattern.matches(URL_REG_EXPRESSION, s);
    }

    public static boolean isEmail(String s) {
        if (s == null) {
            return true;
        }
        return Pattern.matches(EMAIL_REG_EXPRESSION, s);
    }

    public static String join(String spliter, Object[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        if (spliter == null) {
            spliter = "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                break;
            }
            if (arr[i] == null) {
                continue;
            }
            builder.append(arr[i].toString());
            builder.append(spliter);
        }
        return builder.toString();
    }

    public static String fromFile(File f) throws IOException {
        InputStream is = new FileInputStream(f);
        byte[] bs = new byte[is.available()];
        is.read(bs);
        is.close();
        return new String(bs);
    }

    public static void toFile(File f, String s) throws IOException {
        // 只有手机rom有足够的空间才写入本地缓存
        // if (CommonUtil.enoughSpaceOnPhone(s.getBytes().length)) {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(s.getBytes());
        fos.close();
        // }
    }

    /**
     * yt 分割字符串，原理：检测字符串中的分割字符串，然后取子串
     * <p/>
     * a|b|c 拆分成: {"a","b","c"}
     *
     * @param original 需要分割的字符串
     * @param regex    分割字符串
     * @return 分割后生成的字符串数组
     */
    public static String[] split(String original, String regex) {
        if (original == null || regex == null) {
            return null;
        }
        // 取子串的起始位置
        int startIndex = 0;
        // 将结果数据先放入Vector中
        Vector<String> v = new Vector<String>();
        // 返回的结果字符串数组
        String[] str = null;
        // 存储取子串时起始位置
        int index = 0;

        // 获得匹配子串的位置
        startIndex = original.indexOf(regex);
        // System.out.println("0" + startIndex);

        // 如果没有字符regex，则直接返回整个字符串
        if (startIndex == -1) {
            v.addElement(original);
        } else {
            // 如果起始字符串的位置小于字符串的长度，则证明没有取到字符串末尾。
            // -1代表取到了末尾
            while (startIndex < original.length() && startIndex != -1) {
                String temp = original.substring(index, startIndex);
                // 取子串
                v.addElement(temp);

                // 设置取子串的起始位置
                index = startIndex + regex.length();

                // 获得匹配子串的位置
                startIndex = original.indexOf(regex,
                        startIndex + regex.length());
            }

            // 取结束的子串
            v.addElement(original.substring(index));
        }
        // 将Vector对象转换成数组
        str = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            str[i] = (String) v.elementAt(i);
        }

        // 返回生成的数组
        return str;
    }


    /**
     * 中文数字转换阿拉伯数字
     *
     * @param str
     * @return
     */
    public static long getArabNumberFromChineseNumber(String str) {

        if (!isChineaseNum(str)) {
            return Long.parseLong(str);
        }

        char[] num = str.toCharArray();
        long result = 0;
        long maxUnit = 0;
        int i = num.length - 1;
        if (table.get(num[i]) < 10) {
            result = table.get(num[i--]);
        }

        while (i > 0) {

            if ("零".equals(String.valueOf(num[i]))) {
                i--;
                continue;
            }
            long unit = table.get(num[i--]);
            long value = table.get(num[i--]);

            if (maxUnit > unit) {
                result += value * unit * maxUnit;
            } else {
                result += value * unit;
                maxUnit = unit;
            }
        }
        return result;
    }

    private static Map<Character, Long> table;

    static {
        table = new HashMap<Character, Long>();
        table.put('零', 0l);
        table.put('一', 1l);
        table.put('二', 2l);
        table.put('三', 3l);
        table.put('四', 4l);
        table.put('五', 5l);
        table.put('六', 6l);
        table.put('七', 7l);
        table.put('八', 8l);
        table.put('九', 9l);
        table.put('十', 10l);
        table.put('百', 100l);
        table.put('千', 1000l);
        table.put('万', 10000l);
        table.put('亿', 100000000l);
    }

    private static boolean isChineaseNum(String num) {
        try {
            Long.parseLong(num);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static String getImgUrlExt(String url) {
        String murl = url;
        if (StringUtils.isBlank(murl)) {
            return null;
        }
        int wdot=url.indexOf("?");
        if(wdot>-1&&wdot<url.length()){
            murl = url.substring(0, url.indexOf("?"));
        }

        if ((murl != null) && (murl.length() > 0)) {
            int dot = murl.lastIndexOf('.');
            if ((dot > -1) && (dot < (murl.length() - 1))) {
                return murl.substring(dot+1);
            }
        }
        return "";
    }
}
