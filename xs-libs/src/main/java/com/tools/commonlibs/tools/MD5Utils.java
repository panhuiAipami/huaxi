package com.tools.commonlibs.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5编码工具.
 * @author taoyf
 * @time  2015年1月8日
 */
public class MD5Utils {

	public static void main(String[] args){
		String s=MD5("836819141237"+"2014-12-05 15:30:23"+"f305524991549d99768f09e345a871bf");
		System.out.println(s);
	}

	/**
	 * md5加密
	 * @param s
	 * @return
	 */
	public static String MD5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		}

		return "";
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);//0xf0  11110000
			sb.append(HEX_DIGITS[b[i] & 0x0f]);//0x0f 00001111
		}
		return sb.toString();
	}


}
