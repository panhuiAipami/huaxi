package net.huaxi.reader.util;

import com.tools.commonlibs.tools.LogUtils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * 编码工具
 * Created by taoyingfeng
 * 2016/1/6.
 */
public class EncodeUtils {

    public static final int XOR_KEY = 0xFF;//xor加密key

    /**
     * 解密
     *
     * @param data
     * @return
     */
    public static void decodeXor(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        for (int i = 0; i < data.length; i++) {// 遍历字符数组
            data[i] = xor(data[i]);// 对每个数组元素进行异或运算
        }
    }

    /**
     * 异或运算
     *
     * @return
     */
    private static byte xor(byte b) {

        return (byte) (b ^ XOR_KEY);// 对每个数组元素进行异或运算
    }

    /***********************************/
    /**
     * 异或运算
     *
     * @param str 异或前的字符串
     * @return 异或后的字符串
     */
    private String XOR(String str) {
        String result = "";
        char[] chars = str.toCharArray();

        for (char c : chars) {
            char cc = (char) (c ^ 0xFFFF);
            result += cc;
        }
        return result;
    }


    /**
     * 获取CRC值.
     *
     * @param data
     * @return
     */
    public static int getCrc(byte[] data) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        return (int) checksum.getValue();
    }

    /**
     * md5加密
     *
     * @return
     */
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            LogUtils.error(e.getMessage(), e);
        }

        return "";
    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 对字符串进行UTF-8编码.
     *
     * @param value
     * @return
     */
    public static String encodeString_UTF8(String value) {
        String defaultString = "";
        try {
            defaultString = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
        }
        return defaultString;
    }

}
