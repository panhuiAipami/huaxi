package com.tools.commonlibs.utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.tools.commonlibs.tools.LogUtils;

/**
 * JSON转换工具
 * 
 * @author li.li
 *
 */
public class JsonUtils {

	/**
	 * JSON转对象
	 * 
	 * @param json
	 *            JSON字符串
	 * @param clazz
	 *            对象类型
	 * @return 对象
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			Gson gson = new Gson();
			T obj = gson.fromJson(json, clazz);

			return obj;
		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 * JSON转对象
	 * 
	 * Type type = new TypeToken<List<类>>() {}.getType();
	 * 
	 * @param json
	 *            JSON字符串
	 * @param Type
	 *            对象类型
	 * @return 对象
	 */
	public static <T> List<T> fromJson(String json, Type typeOfT) {
		try {

			Gson gson = new Gson();
			List<T> obj = gson.fromJson(json, typeOfT);

			return obj;

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 对象对JSON
	 * 
	 * @param obj
	 *            对象
	 * @return json字符串
	 */
	public static <T> String toJson(T obj) {

		try {

			Gson gson = new Gson();
			String json = gson.toJson(obj);

			return json;

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}

		return null;
	}

}
