package com.tools.commonlibs.http;

import java.io.Closeable;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

/**
 * 网络工具
 * 
 * @author taoyf
 * @time 2015年1月8日
 */
public class HttpUtils {

	public static final String GZIP = "gzip";
	public static final String ENCODING = "utf-8";

	public static HttpResult post(String url, Map<String, String> headers, Map<String, String> params, String encoding, DefaultHttpClient client)
			throws Throwable {

		HttpPost post = new HttpPost(url);

		if (params != null && !params.isEmpty()) {

			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String temp : params.keySet()) {
				list.add(new BasicNameValuePair(temp, params.get(temp)));
			}

			post.setEntity(new UrlEncodedFormEntity(list, encoding));
		}

		if (headers != null && !headers.isEmpty())
			post.setHeaders(assemblyHeader(headers));

		return execute(client, post);
	}

	public static HttpResult post(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> files, String encoding,
			DefaultHttpClient client) throws Throwable {

		HttpPost post = new HttpPost(url);
		// MultipartEntity entitys = new MultipartEntity();
		//
		// if (params != null && !params.isEmpty()) {
		//
		// // 设置传递String参数
		// if (params != null && !params.isEmpty())
		// for (String temp : params.keySet()) {
		// entitys.addPart(temp, new StringBody(params.get(temp),
		// Charset.forName(HTTP.UTF_8)));
		// }
		//
		// }
		//
		// // 设置传递File参数
		// if (files != null && !files.isEmpty())
		// for (String temp : files.keySet()) {
		// entitys.addPart(temp, new FileBody(files.get(temp)));
		// }
		//
		// post.setEntity(entitys);

		if (headers != null && !headers.isEmpty())
			post.setHeaders(assemblyHeader(headers));

		return execute(client, post);
	}

	public static HttpResult get(String url, Map<String, String> headers, Map<String, String> params, String encoding, DefaultHttpClient client)
			throws Throwable {

		if (params != null && !params.isEmpty())
			url += assemblyParameter(params);

		HttpGet get = new HttpGet(url);

		if (headers != null && !headers.isEmpty())
			get.setHeaders(assemblyHeader(headers));

		return execute(client, get);
	}

	public static HttpResult execute(AbstractHttpClient client, HttpUriRequest request) throws Throwable {
		HttpResult result = null;// 返回结果

		// 新建监控接口对象
		URI uri = request.getURI();

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(uri.getScheme());
		urlBuilder.append("://");
		urlBuilder.append(uri.getAuthority());
		urlBuilder.append(uri.getPath());
        if (!StringUtils.isBlank(uri.getRawQuery())) {
            urlBuilder.append("?");
            urlBuilder.append(uri.getRawQuery());
        }

		String url = urlBuilder.toString();
		long startTime = System.currentTimeMillis();
		try {

			HttpResponse response = client.execute(request);

			int code = response.getStatusLine().getStatusCode();

			if (code < 400) {// 400以下认为是正常请求

				result = new HttpResult();
				result.setStatusCode(code);
				result.setHttpEntity(response.getEntity());
				result.setRequest(request);

//				MyStatAppMonitor.newInstance(url)//
//						.setMillisecondsConsume(System.currentTimeMillis() - startTime)// 设置接口耗时
//						.setRespSize(response.getEntity().getContentLength())// 设置响应包大小
//						.setResultType(StatAppMonitor.SUCCESS_RESULT_TYPE)// 标记为成功
//						.reportAppMonitorStat();// 上报接口监控

			} else {

//				MyStatAppMonitor.newInstance(url)//
//						.setMillisecondsConsume(System.currentTimeMillis() - startTime)// 设置接口耗时
//						.setRespSize(response.getEntity().getContentLength())// 设置响应包大小
//						.setResultType(StatAppMonitor.LOGIC_FAILURE_RESULT_TYPE)// 标记为逻辑失败
//						.reportAppMonitorStat();// 上报接口监控

			}

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
//			MyStatAppMonitor.newInstance(url)//
//					.setMillisecondsConsume(System.currentTimeMillis() - startTime)// 设置接口耗时
//					.setResultType(StatAppMonitor.FAILURE_RESULT_TYPE)// 标识为失败
//					.reportAppMonitorStat();// 上报接口监控

		}

		return result;

	}

	public static Header[] assemblyHeader(Map<String, String> headers) {
		final Header[] allHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet()) {
			allHeader[i] = new BasicHeader(str, headers.get(str));
			i++;
		}

		return allHeader;
	}

	public static String assemblyCookie(List<Cookie> cookies) {
		final StringBuffer sbu = new StringBuffer();

		for (Cookie cookie : cookies) {
			sbu.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
		}

		if (sbu.length() > 0)
			sbu.deleteCharAt(sbu.length() - 1);

		return sbu.toString();
	}

	public static Map<String, String> assemblyCookieMap(List<Cookie> cookies) {
		final Map<String, String> cookieMap = new HashMap<String, String>(cookies.size());

		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}

		return cookieMap;
	}

	public static String assemblyParameter(Map<String, String> parameters) {
		String para = "?";
		for (String str : parameters.keySet()) {
			para += str + "=" + parameters.get(str) + "&";
		}
		return para.substring(0, para.length() - 1);
	}

	@SuppressWarnings("unused")
	private static void closeStream(Closeable is) {
		if (is == null)
			return;
		try {
			is.close();
		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {

	}

}
