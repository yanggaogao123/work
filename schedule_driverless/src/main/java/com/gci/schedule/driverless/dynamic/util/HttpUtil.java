package com.gci.schedule.driverless.dynamic.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);


	/*
	 * 只创建一个连接池 
	 * private static final PoolingHttpClientConnectionManager PHCCM =
	 * new PoolingHttpClientConnectionManager( 4000, TimeUnit.MILLISECONDS);
	 * //响应时间5S 
	 * private static CloseableHttpClient httpClient = null; static { 
	 * //这个值会影响每次请求的时间、cpu使用情况。越小：负载请求数越少，cpu占用率越少，越大:负载请求数越大、cpu占用绿越高
	 * //经过测试4000可满足200的并发量，200的并发量是响应时间在3-3.5s PHCCM.setMaxTotal(4000);
	 * httpClient = HttpClients.custom().setConnectionManager(PHCCM).build(); }
	 */

	public static String queryString(final HttpRequestBase requestBase) {
		String str = null;
		CloseableHttpResponse resq = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			RequestConfig requestConfig = RequestConfig.custom()
			        .setConnectTimeout(10000).setConnectionRequestTimeout(10000)
			        .setSocketTimeout(60000).build();
			requestBase.setConfig(requestConfig);
			resq = httpClient.execute(requestBase);
			HttpEntity entity = resq.getEntity();
			int status = resq.getStatusLine().getStatusCode();
			if (status == 200) {
				str = EntityUtils.toString(entity, "UTF-8");
			} else {
				LOGGER.error(new StringBuffer("链接出错，url：")
						.append(requestBase.getURI()).append(",错误代码")
						.append(status).toString());
			}
		} catch (ClientProtocolException e1) {
			LOGGER.error(new StringBuffer("ClientProtocolException,url：")
					.append(",错误代码").toString(), e1);
		} catch (IOException e1) {
			LOGGER.error(new StringBuffer("IOException,url：")
					.append(requestBase.getURI()).toString(), e1);
		} finally {
			try {
				if (resq != null) {
					resq.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static String getString(URI uri) {
		HttpGet get = new HttpGet();
		get.setURI(uri);
		String str = queryString(get);
		return str;
	}

	public static String postString(URI uri) {
		HttpPost post = new HttpPost();
		post.setURI(uri);
		String str = queryString(post);
		return str;
	}
	
	public static String postString(URI uri, String json) throws UnsupportedEncodingException {
		HttpPost post = new HttpPost();
		post.setURI(uri);
		post.addHeader(HTTP.CONTENT_TYPE, "application/json");
		StringEntity entity= new StringEntity(json,"UTF-8");
		entity.setContentType("text/json");
		post.setEntity(entity);
		String str = queryString(post);
		return str;
	}

	public static String getString(String uri) {
		String str = null;
		try {
			str = getString(new URI(uri));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String postString(String uri) {
		String str = null;
		try {
			str = postString(new URI(uri));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static String submit(String url, String json) {
		String str = null;
		try {
			URI uri = new URI(url);
			str = HttpUtil.postString(uri,json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
}
