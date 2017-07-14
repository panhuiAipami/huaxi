package com.tools.commonlibs.http;

import java.io.File;
import java.util.Map;

import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.utils.JsonUtils;

/**
 * http请求帮助类
 * 
 * 提供将一个请求的结果返回一个对象 如果返回的json数据是空的则返回空对象
 * 
 * @author li.li
 *
 *         Dec 27, 2012
 */
public class HttpHelper {

	/**
	 * post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param clazz
	 *            返回的对象实例类型
	 * @return
	 */
	public static <T> T post(String url, Map<String, String> params, Class<T> clazz) {

		String result = post(url, params);

		if (StringUtils.isBlank(result))
			return null;

		T obj = JsonUtils.fromJson(result, clazz);

		return obj;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param clazz
	 *            返回的对象实例类型
	 * @return
	 */
	public static <T> T post(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> files, Class<T> clazz) {

		String result = post(url, headers, params, files);

		if (StringUtils.isBlank(result))
			return null;

		T obj = JsonUtils.fromJson(result, clazz);

		return obj;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param clazz
	 *            返回的对象实例类型
	 * @return
	 */
	public static <T> T get(String url, Map<String, String> params, Class<T> clazz) {

		String result = get(url, params);

		if (StringUtils.isBlank(result))
			return null;

		T obj = JsonUtils.fromJson(result, clazz);

		return obj;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @return
	 */
	public static String post(String url, Map<String, String> params) {
		NetType netType = NetUtils.checkNet();

		if (NetType.TYPE_NONE.equals(netType)) {
			return StringUtils.EMPTY;
		}

		HttpProvider httpProvider = null;
		String result = StringUtils.EMPTY;

		HttpResult httpResult = null;
		try {
			httpProvider = HttpProvider.getInstance();
			httpResult = httpProvider.post(url, null, params, HttpUtils.ENCODING);
			result = httpResult.httpEntityContent();

			return result;

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		} finally {
			if (httpProvider != null)
				httpProvider.releaseConnection(httpResult);
		}

		return null;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @return
	 */
	public static String post(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> files) {
		NetType netType = NetUtils.checkNet();

		if (NetType.TYPE_NONE.equals(netType)) {
			return StringUtils.EMPTY;
		}

		HttpProvider httpProvider = null;
		String result = StringUtils.EMPTY;

		HttpResult httpResult = null;
		try {
			httpProvider = HttpProvider.getInstance();
			httpResult = httpProvider.post(url, headers, params, files, HttpUtils.ENCODING);
			result = httpResult.httpEntityContent();

			return result;

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		} finally {
			if (httpProvider != null)
				httpProvider.releaseConnection(httpResult);
		}

		return null;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @return
	 */
	public static String get(String url, Map<String, String> params) {
		NetType netType = NetUtils.checkNet();

		if (NetType.TYPE_NONE.equals(netType)) {
			return StringUtils.EMPTY;
		}

		HttpProvider httpProvider = null;
		String result = StringUtils.EMPTY;
		HttpResult httpResult = null;
		try {
			httpProvider = HttpProvider.getInstance();
			httpResult = httpProvider.get(url, null, params, HttpUtils.ENCODING);
			result = httpResult.httpEntityContent();

			return result;

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		} finally {

			if (httpProvider != null)
				httpProvider.releaseConnection(httpResult);
		}

		return null;
	}
}
