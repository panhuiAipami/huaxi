package com.spriteapp.booklibrary.util;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class ByteToHexUtil {

    public static String fromByteToHex(byte[] resultBytes) {
        StringBuilder builder = new StringBuilder();
        for (byte resultByte : resultBytes) {
            if (Integer.toHexString(0xFF & resultByte).length() == 1) {
                builder.append(0).append(Integer.toHexString(0xFF & resultByte));
            } else {
                builder.append(Integer.toHexString(0xFF & resultByte));
            }
        }
        return builder.toString();
    }
}
