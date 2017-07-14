package com.tools.commonlibs.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;


/**
 * Http util class
 * 
 * @date 2010-4-27
 */
@SuppressLint("DefaultLocale")
public class Http {
    public static final String POST = "POST";

	public static final String GET = "GET";

	public static final int CON_TIME_OUT = 25 * 1000;

	public static final int READ_TIME_OUT = 20 * 1000;

	public static final int FILE_BUFFER_SIZE = 5 * 1024;

	public static final int NEED_RE_CONNECT = 100001;

	public static final String TYPE_WML = "text/vnd.wap.wml";
	public static final String TYPE_WMLC = "application/vnd.wap.wmlc";

	/**
	 * @param destUrl
	 *            can't be null, others can
	 * @param requestContent
	 * @param params
	 * @return
	 * @throws HttpResponseException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream sendRequest(String destUrl,
			byte[] requestContent, String mothed, BasicHeader[] headers,
			String contentType, Context context) throws HttpResponseException,
			IOException, Exception {

		HttpEntity responseContent = doRequest(destUrl, requestContent, mothed,
				headers, contentType, context);

		return responseContent.getContent();

	}

	public static HttpEntity doRequest(String destUrl, byte[] requestContent,
			String amothed, BasicHeader[] headers, String contentType,
			Context context) throws HttpResponseException, IOException,
			Exception {
		// Log.i("network", " destUrl : " + destUrl);
		HttpResponse httpResponse = doOnlineRequest(destUrl, requestContent,
				amothed, headers, contentType, context);
		return httpResponse.getEntity();
	}

	public static HttpResponse doOnlineRequest(String destUrl,
			byte[] requestContent, String amothed, BasicHeader[] headers,
			String contentType, Context context) throws HttpResponseException,
			IOException, Exception {
		// Log.i("network", " destUrl : " + destUrl);
		HttpResponse httpResponse = null;
		int code = NEED_RE_CONNECT;
		int reset = 0;
		while (code == NEED_RE_CONNECT) {
			httpResponse = getResponse(destUrl, requestContent, amothed,
					headers, contentType, context);
			int rescode = httpResponse.getStatusLine().getStatusCode();
			if (rescode == HttpURLConnection.HTTP_MOVED_TEMP
					|| rescode == HttpURLConnection.HTTP_MOVED_PERM) {
				reset++;
				if (reset < 3) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					code = NEED_RE_CONNECT;
					continue;
				} else {
					throw new HttpResponseException(rescode);
				}
			}
			if (rescode != HttpStatus.SC_OK) {
				Log.i("Http", "doOnlineRequest url : " + destUrl
						+ " || errcode: " + rescode);
				throw new HttpResponseException(rescode);
			}
			Header contentTypeHeader = httpResponse.getEntity()
					.getContentType();
			if (contentTypeHeader != null) {
				String wapCotentType = contentTypeHeader.getValue();
				if (wapCotentType != null
						&& ((wapCotentType.indexOf(TYPE_WML) != -1 || wapCotentType
								.indexOf(TYPE_WMLC) != -1))) {
					reset++;
					if (reset < 3) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
						code = NEED_RE_CONNECT;
						continue;
					} else {
						throw new HttpResponseException(rescode);
					}

				}
			}
			break;
		}
		return httpResponse;
	}

	private static HttpResponse getResponse(String destUrl,
			byte[] requestContent, String amothed, BasicHeader[] headers,
			String contentType, Context context) throws IOException,
			ClientProtocolException {
		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CON_TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, READ_TIME_OUT);

		HttpRequestBase httpPost = null;

		if (amothed.equals(POST)) {
			httpPost = new HttpPost(destUrl);

			if (requestContent != null) {
				ByteArrayEntity requestEntity = new ByteArrayEntity(
						requestContent);
				if (contentType != null) {
					requestEntity.setContentType(contentType);
				}
				((HttpPost) httpPost).setEntity(requestEntity);

			}

		} else {
			String getUrl = destUrl;
			if (requestContent != null) {
				getUrl = destUrl + "?" + new String(requestContent);
			}
			httpPost = new HttpGet(getUrl);
		}

		if (headers != null) {
			for (BasicHeader ah : headers) {
				httpPost.addHeader(ah);
			}
		}

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		//gzip,deflate,sdch(api2.3以后，默认都是gzip压缩.)
		//TODO:如果内容被压缩，会出现getContentLength()=-1.如何处理呢。
//		httpClient.getParams().setParameter("Accept-Encoding", "identity");  //不压缩.
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,
				false);

		InetSocketAddress pxy = getProxy(context);

		if (pxy != null) {
			HttpHost proxy = new HttpHost(pxy.getHostName(), pxy.getPort());
			httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
		}

		HttpResponse httpResponse = httpClient.execute(httpPost);
		return httpResponse;
	}

	public static boolean isWifi(Context context) {
		boolean isWifi = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null
				&& activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			isWifi = true;
		}
		return isWifi;
	}

	@SuppressLint("DefaultLocale")
	@SuppressWarnings("deprecation")
	public static InetSocketAddress getProxy(Context context) {
		String strProxyHost = android.net.Proxy.getDefaultHost();
		int proxyport = android.net.Proxy.getDefaultPort();

		InetSocketAddress pxy = null;

		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetworkInfo != null) {
				int type = activeNetworkInfo.getType();

				// TODO 防止wifi下读取移动的代理
				if (type == ConnectivityManager.TYPE_MOBILE) {
					// 暂时先不加
					if (activeNetworkInfo.getExtraInfo() != null) {
						String apnName = activeNetworkInfo.getExtraInfo()
								.toLowerCase();
						if (apnName.startsWith(APN_TYPE_CMNET_ALIAS)
								|| apnName.startsWith(APN_TYPE_CTNET)
								|| apnName.startsWith(APN_TYPE_CMNET)
								|| apnName.startsWith(APN_TYPE_UNINET)
								|| apnName.startsWith(APN_TYPE_3G_NET)) {
							return null;
						}
					}

					strProxyHost = android.net.Proxy.getHost(context);
					proxyport = android.net.Proxy.getPort(context);

					if (strProxyHost == null
							|| strProxyHost.trim().length() == 0
							|| proxyport <= 0) {
						strProxyHost = android.net.Proxy.getDefaultHost();
						proxyport = android.net.Proxy.getDefaultPort();

						if (strProxyHost == null
								|| strProxyHost.trim().length() == 0
								|| proxyport <= 0) {

							return getProxyByApn(context);
						}
					}
				} else {
					return null;
				}
			}
		}

		if (null != strProxyHost && strProxyHost.trim().length() > 0) {
			// pxy = new InetSocketAddress(strProxyHost, proxyport);
			// by ivanyang 不使用InetAddress避免host那么被赋空
			pxy = InetSocketAddress.createUnresolved(strProxyHost, proxyport);
		}

		return pxy;
	}

	public static HttpURLConnection prepareHttpConnection(String url,
			BasicHeader[] headers, Context context) throws IOException {
		URL arul = new URL(url);
		HttpURLConnection conn = openConnection(arul, context);
		conn.setConnectTimeout(CON_TIME_OUT);
		conn.setReadTimeout(READ_TIME_OUT);
		// 在4.0.4和2.1部分机器上都发现保持keep-alive字段会导致网络不畅有时候
		// if(Constant.ANDROID_ICE_CREAM_SANDWICH) {
		conn.setRequestProperty("Connection", "Close");
		// }
		if (headers != null) {
			for (BasicHeader bh : headers) {
				conn.setRequestProperty(bh.getName(), bh.getValue());
			}
		}

		int resCode = conn.getResponseCode();
		switch (resCode) {
		case HttpURLConnection.HTTP_OK:
		case HttpURLConnection.HTTP_PARTIAL:
			String wapCotentType = conn.getContentType();
			if (wapCotentType != null
					&& ((wapCotentType.indexOf(Http.TYPE_WML) != -1 || wapCotentType
							.indexOf(Http.TYPE_WMLC) != -1))) {
				if (null != conn) {
					conn.disconnect();
				}
				conn = prepareHttpConnection(url, headers, context);
			}
			break;

		case HttpURLConnection.HTTP_MOVED_PERM:
		case HttpURLConnection.HTTP_MOVED_TEMP:
			if (null != conn) {
				conn.disconnect();
			}
			conn = prepareHttpConnection(url, headers, context);
			break;
		case 416:
			throw new RangeException();
		default:
			throw new IOException("HTTP Response Code: " + resCode);
		}
		Log.v("Http", "prepareConnection.Finish to prepare connection");
		return conn;
	};

	public static HttpURLConnection openConnection(URL arul, Context context)
			throws IOException {
		InetSocketAddress pxy = Http.getProxy(context);
		HttpURLConnection urlConnection = null;
		if (pxy == null) {
			urlConnection = (HttpURLConnection) arul.openConnection();
		} else {
			Proxy p = new Proxy(Proxy.Type.HTTP, pxy);
			urlConnection = (HttpURLConnection) arul.openConnection(p);
		}

		return urlConnection;
	}

	public static HttpURLConnection openFileHttpInput(String aurl,
			Context context) throws IOException {
		URL url = new URL(aurl);
		HttpURLConnection conn = openConnection(url, context);
		conn.setConnectTimeout(CON_TIME_OUT);
		conn.setReadTimeout(READ_TIME_OUT);
		// if(Constant.ANDROID_ICE_CREAM_SANDWICH) {
		conn.setRequestProperty("Connection", "Close");
		// }
		return conn;
	}

	public static byte[] openFileHttpByte(String aurl, Context context)
			throws IOException {
		URL url = new URL(aurl);
		HttpURLConnection conn = openConnection(url, context);
		conn.setConnectTimeout(CON_TIME_OUT);
		conn.setReadTimeout(READ_TIME_OUT);

		InputStream is = null;
		byte[] refile;
		try {
			is = conn.getInputStream();
			refile = getByte(conn.getContentLength(), is);
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return refile;
	}

	/**
	 * @param conn
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] getByte(int length, InputStream is) throws IOException {
		int fileSize = length;
		byte[] buffer = new byte[fileSize];
		byte[] tempBuffer = new byte[FILE_BUFFER_SIZE];
		int pos = 0;
		int readLength = 0;
		while ((readLength = is.read(tempBuffer)) != -1) {
			System.arraycopy(tempBuffer, 0, buffer, pos, readLength);
			pos += readLength;
		}
		return buffer;
	}

	public static String getString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw e;
		}

		return sb.toString();
	}

	private static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	public static String APN_TYPE_CTNET = "ctnet";

	public static String APN_TYPE_CTWAP = "ctwap";

	public static String APN_TYPE_CMNET_ALIAS = "epc.tmobile.com";

	public static String APN_TYPE_CMNET = "cmnet";

	public static String APN_TYPE_CMWAP = "cmwap";

	public static String APN_TYPE_UNINET = "uninet";

	public static String APN_TYPE_UNIWAP = "uniwap";

	public static String APN_TYPE_3G_NET = "3gnet";

	public static String APN_TYPE_3G_WAP = "3gwap";

	private static InetSocketAddress getProxyByApn(Context context) {
		String address = null;
		int port = 0;
		String apn = null;

		try {
			Cursor c = context.getContentResolver().query(PREFERRED_APN_URI,
					null, null, null, null);
			c.moveToFirst();

			address = c.getString(c.getColumnIndex("proxy"));
			if (address != null) {
				address.toLowerCase();
			}
			String portStr = c.getString(c.getColumnIndex("port"));
			if (portStr == null) {
				return null;
			}
			try {
				port = Integer.parseInt(portStr);
			} catch (Exception e) {
				port = -1;
			}

			apn = c.getString(c.getColumnIndex("apn"));
			if (apn != null) {
				apn.toLowerCase();
			}
			c.close();

			if (apn != null && apn.startsWith(APN_TYPE_CTWAP)) {
				if (address == null || address.length() == 0
						|| Integer.valueOf(port) <= 0) {
					address = "10.0.0.200";
					port = 80;
				}
			} else if (apn != null && apn.startsWith(APN_TYPE_CMWAP)) {
				if (address == null || address.length() == 0
						|| Integer.valueOf(port) <= 0) {
					address = "10.0.0.172";
					port = 80;
				}
			} else if (apn != null && apn.startsWith(APN_TYPE_UNIWAP)) {
				if (address == null || address.length() == 0
						|| Integer.valueOf(port) <= 0) {
					address = "10.0.0.172";
					port = 80;
				}
			} else if (apn != null && apn.startsWith(APN_TYPE_3G_WAP)) {
				if (address == null || address.length() == 0
						|| Integer.valueOf(port) <= 0) {
					address = "10.0.0.172";
					port = 80;
				}
			} else if (address == null || address.trim().length() <= 0
					|| port <= 0) {
				// 某些手机，3gnet下获取的address = " " port = 80
				return null;
			}

			// return new InetSocketAddress(address, port);
			// by ivanyang 不使用InetAddress避免host那么被赋空
			return InetSocketAddress.createUnresolved(address, port);

		} catch (Exception e) {
			Log.e("getProxyByApn", e.toString());
		}
		return null;
	}
}
