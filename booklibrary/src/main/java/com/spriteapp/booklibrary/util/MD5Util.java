package com.spriteapp.booklibrary.util;

import android.util.Log;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class MD5Util {

    private static final String TAG = "TAG";

    public static String encryptMD5(String data) {
        return encryptMD5(data.getBytes());
    }

    /**
     * 计算字符串的MD5值
     *
     * @param data 要计算的字符串对应的字节数组
     * @return 字符串的MD5值
     */
    public static String encryptMD5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            byte[] resultBytes = md5.digest();
            /*
            将字节数组转化为16进制
             */
            return ByteToHexUtil.fromByteToHex(resultBytes);
        } catch (Exception e) {
            Log.d(TAG, "encryptMD5: " + e.getMessage());
        }
        return null;
    }

    /**
     * 计算文件的MD5值
     *
     * @param path 文件路径
     * @return 文件MD5值
     */
    public static String getFileMD5(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            DigestInputStream dis = new DigestInputStream(fileInputStream, MessageDigest.getInstance("MD5"));
            byte[] buffer = new byte[1024];
            int read = dis.read(buffer, 0, 1024);
            while (read != -1) {
                read = dis.read(buffer, 0, 1024);
            }
            dis.close();
            fileInputStream.close();
            MessageDigest md5 = dis.getMessageDigest();
            return ByteToHexUtil.fromByteToHex(md5.digest());
        } catch (Exception e) {
            Log.d(TAG, "getFileMD5: " + e.getMessage());
        }
        return null;
    }
}
