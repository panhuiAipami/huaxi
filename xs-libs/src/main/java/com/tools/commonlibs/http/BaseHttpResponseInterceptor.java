package com.tools.commonlibs.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * 书奇HTTP请求结果拦截器
 * 
 * @author li.li
 * @date 2014-06-24
 * 
 */
public class BaseHttpResponseInterceptor implements HttpResponseInterceptor {

	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {

		HttpEntity entity = response.getEntity();
		Header ceheader = entity.getContentEncoding();

		// 启用GZIP压缩模块
		if (ceheader != null) {
			for (HeaderElement element : ceheader.getElements()) {
				if (element.getName().equalsIgnoreCase(HttpUtils.GZIP)) {// 对GZIP处理
					response.setEntity(new GzipDecompressingEntity(response.getEntity()));
					return;
				}
			}
		}

	}

}
