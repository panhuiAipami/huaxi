package com.spriteapp.booklibrary.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by nys on 2017/7/8.
 */

public class EncryptUtils {
    private static byte[] iv1 = {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};

    /**
     * 加密
     *
     * @param plainText
     * @param password
     * @return
     * @throws Exception
     */
    public static byte[] desEncrypt(byte[] plainText, String password) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(iv1);
        DESKeySpec dks = new DESKeySpec(password.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte encryptedData[] = cipher.doFinal(plainText);
        return encryptedData;
    }

    /**
     * 解密
     *
     * @param plainText
     * @param password
     * @return
     * @throws Exception
     */
    public static byte[] desDecrypt(byte[] plainText, String password) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(iv1);
        DESKeySpec dks = new DESKeySpec(password.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte encryptedData[] = cipher.doFinal(plainText);
        return encryptedData;
    }

    public static String encrypt(String input, String password) {
        String result = null;
        try {
            byte[] bytes = desEncrypt(input.getBytes(), password);
            result = base64Encode(bytes);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String decrypt(String content, String key) {
        String result = null;
        try {
            byte[] bytes = base64Decode(content.getBytes());
            result = new String(desDecrypt(bytes, key));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String base64Encode(byte[] s) {
        if (s == null)
            return null;
        return Base64.encodeToString(s, Base64.NO_WRAP);
    }

    public static byte[] base64Decode(byte[] s) {
        if (s == null)
            return null;
        return Base64.decode(s, Base64.DEFAULT);
    }
}
