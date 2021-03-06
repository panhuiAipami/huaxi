package com.spriteapp.booklibrary.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;


public class PreferenceHelper {
	//分享的标题
	public static final String AES_KEY = "aes_key";
	//引导去市场评论
	public final static String GOTOAPPSTORE = "gotoAppStore";

	public static boolean getBoolean(String key, boolean defValue) {

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext());
		return settings.getBoolean(key, defValue);
	}

	public static void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static int getInt(String key, int defValue) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext());
		return settings.getInt(key, defValue);
	}

	public static void putInt(String key, int value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static long getLong(String key, long defValue) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext());
		return settings.getLong(key, defValue);
	}

	public static void putLong(String key, long value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static String getString(String key, String defValue) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext());
		return settings.getString(key, defValue);
	}

	public static void putString(String key, String value) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void remove(String key) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).edit();
		editor.remove(key);
		editor.commit();
	}

	public static void registerOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
		try {
			PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).registerOnSharedPreferenceChangeListener(listener);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void unregisterOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
		try {
			PreferenceManager.getDefaultSharedPreferences(AppUtil.getAppContext()).unregisterOnSharedPreferenceChangeListener(listener);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
