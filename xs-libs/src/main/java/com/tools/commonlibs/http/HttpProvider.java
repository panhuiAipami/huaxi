/**
 * 
 */
package com.tools.commonlibs.http;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.tools.commonlibs.tools.LogUtils;

/**
 * Http请求
 * 
 * @author li.li
 * 
 */
public class HttpProvider {
	/** 从连接池中取连接的超时时间 */
	private static final int POOL_TIMEOUT = 5 * 1000;
	/** 连接超时 */
	public static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
	/** 请求超时 */
	public static final int DEFAULT_SO_TIMEOUT = 40 * 1000;
	/** 每个路由(route)最大连接数 */
	private final static int DEFAULT_ROUTE_CONNECTIONS = Integer.MAX_VALUE;
	/** 连接池中的最多连接总数 */
	private final static int DEFAULT_MAX_CONNECTIONS = Integer.MAX_VALUE;
	/** Socket 缓存大小 */
	private final static int DEFAULT_SOCKET_BUFFER_SIZE = 1024;

	private static volatile HttpProvider instance;

	private final DefaultHttpClient client;

	private HttpProvider() {
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);// 禁止302重定向

		/** 以先发送部分请求（如：只发送请求头）进行试探，如果服务器愿意接收，则继续发送请求体 */
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		/**
		 * 即在有传输数据需求时，会首先检查连接池中是否有可供重用的连接，如果有，则会重用连接。
		 * 同时，为了确保该“被重用”的连接确实有效，会在重用之前对其进行有效性检查
		 */
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();

		// 支持HTTP请求
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		// 支持HTTPS请求
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schReg.register(new Scheme("https", sf, 443));
		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}

		// 线程安全连接池
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
		client = new DefaultHttpClient(conMgr, httpParams);

		// 设置拦截器
		client.addRequestInterceptor(new BaseHttpRequestInterceptor());
		client.addResponseInterceptor(new BaseHttpResponseInterceptor());

		// 设置请求重试控制器（服务器或网络故障重试）
		client.setHttpRequestRetryHandler((new DefaultHttpRequestRetryHandler(2, false)));

		ConnManagerParams.setTimeout(httpParams, POOL_TIMEOUT);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_ROUTE_CONNECTIONS));
		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CONNECT_TIMEOUT);// 连接超时(指的是连接一个url的连接等待时间)
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SO_TIMEOUT);// 读取数据超时(指的是连接上一个url，获取response的返回等待时间)
		HttpConnectionParams.setTcpNoDelay(httpParams, true);// nagle算法默认是打开的，会引起delay的问题；所以要手工关掉。
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

	}

	public static HttpProvider getInstance() {

		if (instance == null) {
			synchronized (HttpProvider.class) {

				if (instance == null)
					instance = new HttpProvider();

			}
		}

		return instance;
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Throwable
	 */
	public HttpResult get(String url, Map<String, String> headers, Map<String, String> params, String encoding) throws Throwable {

		HttpResult result = HttpUtils.get(url, headers, params, encoding, client);

		return result;
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Throwable
	 */
	public HttpResult post(String url, Map<String, String> headers, Map<String, String> params, String encoding) throws Throwable {

		HttpResult result = HttpUtils.post(url, headers, params, encoding, client);

		return result;
	}

	/**
	 * 上传请求
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Throwable
	 */
	public HttpResult post(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> files, String encoding)
			throws Throwable {

		HttpResult result = HttpUtils.post(url, headers, params, files, encoding, client);

		return result;
	}

	/**
	 * 释放资源
	 * 
	 * @param httpResult
	 */
	public void releaseConnection(HttpResult httpResult) {
		if (httpResult != null)
			httpResult.getRequest().abort();
	}

	// /**
	// * 释放资源
	// */
	// public void shutdown() {
	// if (client != null)
	// client.getConnectionManager().shutdown();
	//
	// }

	private final static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)

		throws NoSuchAlgorithmException, KeyManagementException,

		KeyStoreException, UnrecoverableKeyException {

			super(truststore);

			TrustManager tm = new X509TrustManager() {

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {

					return null;

				}

				@Override
				public void checkClientTrusted(

				java.security.cert.X509Certificate[] chain, String authType)

				throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(

				java.security.cert.X509Certificate[] chain, String authType)

				throws java.security.cert.CertificateException {

				}

			};

			sslContext.init(null, new TrustManager[] { tm }, null);

		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,

		boolean autoClose) throws IOException, UnknownHostException {

			return sslContext.getSocketFactory().createSocket(socket, host, port,

			autoClose);

		}

		@Override
		public Socket createSocket() throws IOException {

			return sslContext.getSocketFactory().createSocket();

		}

	}

}
