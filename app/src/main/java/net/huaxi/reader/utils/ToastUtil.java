package net.huaxi.reader.utils;


import android.widget.Toast;

import net.huaxi.reader.MyApplication;

/**
 * Toast统一管理类
 * 
 * @author way
 * 
 */
public class ToastUtil {
	// Toast
	private static Toast toast;

	/**
	 * 短时间显示Toast
	 *
	 * @param message
	 */
	public static void showShort(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(MyApplication.getInstance(), message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 短时间显示Toast
	 *
	 * @param message
	 */
	public static void showShort(int message) {
		try{
			if (null == toast) {
				toast = Toast.makeText(MyApplication.getInstance(), message, Toast.LENGTH_SHORT);
				// toast.setGravity(Gravity.CENTER, 0, 0);
			} else {
				toast.setText(message);
			}
			toast.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param message
	 */
	public static void showLong(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(MyApplication.getInstance(), message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param message
	 */
	public static void showLong(String message) {
		if (null == toast) {
			toast = Toast.makeText(MyApplication.getInstance(), message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 *  @param message
	 * @param duration
	 */
	public static void show(CharSequence message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(MyApplication.getInstance(), message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 *  @param message
	 * @param duration
	 */
	public static void show(int message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(MyApplication.getInstance(), message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/** Hide the toast, if any. */
	public static void hideToast() {
		if (null != toast) {
			toast.cancel();
		}
	}

}
