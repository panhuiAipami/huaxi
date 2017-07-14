package com.tools.commonlibs.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.tools.commonlibs.tools.LogUtils;

/**
 * Http请求结果
 * 
 * @author li.li
 * 
 */

public class HttpResult {

	private int statusCode;
	private HashMap<String, Header> headerAll;
	private HttpEntity httpEntity;
	private String cookie;
	private Map<String, String> cookieMap;
	private HttpUriRequest request;

	public HttpUriRequest getRequest() {
		return request;
	}

	public void setRequest(HttpUriRequest request) {
		this.request = request;
	}

	public Map<String, String> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HashMap<String, Header> getHeaders() {
		return headerAll;
	}

	public void setHeaders(Header[] headers) {
		headerAll = new HashMap<String, Header>();
		for (Header header : headers) {
			headerAll.put(header.getName(), header);
		}
	}

	public HttpEntity getHttpEntity() {
		return httpEntity;
	}

	public void setHttpEntity(HttpEntity httpEntity) {
		this.httpEntity = httpEntity;
	}

	public String httpEntityContent() {

		return httpEntityContent(HTTP.UTF_8);
	}

	public String httpEntityContent(String encode) {

		try {
			return EntityUtils.toString(httpEntity, encode);
		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}

		return null;
	}
}
