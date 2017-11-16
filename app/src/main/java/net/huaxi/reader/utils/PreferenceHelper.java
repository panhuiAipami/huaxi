package net.huaxi.reader.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import net.huaxi.reader.MyApplication;


public class PreferenceHelper {
	//分享的标题
	public final static String shareTitle = "shareTitle";
	//分享的格式
	public final static String shareformat = "shareformat";
	//分享的格式字数
	public final static String shareformat_length = "shareformat_length";
	//显示排行的小圆点
	public final static String show_ranking_spot = "show_ranking_spot";


	public static boolean getBoolean(String key, boolean defValue) {

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
		return settings.getBoolean(key, defValue);
	}

	public static void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static int getInt(String key, int defValue) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
		return settings.getInt(key, defValue);
	}

	public static void putInt(String key, int value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static long getLong(String key, long defValue) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
		return settings.getLong(key, defValue);
	}

	public static void putLong(String key, long value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static String getString(String key, String defValue) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
		return settings.getString(key, defValue);
	}

	public static void putString(String key, String value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void remove(String key) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).edit();
		editor.remove(key);
		editor.commit();
	}

	public static void registerOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
		try {
			PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).registerOnSharedPreferenceChangeListener(listener);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void unregisterOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
		try {
			PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).unregisterOnSharedPreferenceChangeListener(listener);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
