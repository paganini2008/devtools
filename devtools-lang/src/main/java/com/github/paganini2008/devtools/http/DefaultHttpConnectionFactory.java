package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import com.github.paganini2008.devtools.net.UrlUtils;

/**
 * 
 * DefaultHttpConnectionFactory
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {

	private SSLSocketFactory sslSocketFactory;

	public HttpURLConnection openConnection(HttpRequest httpRequest) throws IOException {
		HttpURLConnection connection;
		if (httpRequest.proxy() != null) {
			connection = (HttpURLConnection) httpRequest.url().openConnection(new Proxy(Proxy.Type.HTTP, httpRequest.proxy()));
		} else {
			connection = (HttpURLConnection) httpRequest.url().openConnection();
		}
		connection.setRequestMethod(httpRequest.method());
		connection.setConnectTimeout(httpRequest.connectTimeout());
		connection.setReadTimeout(httpRequest.readTimeout());
		connection.setDoOutput(httpRequest.doOutput());
		connection.setInstanceFollowRedirects(httpRequest.followRedirects());

		if (httpRequest.cookies().size() > 0) {
			connection.addRequestProperty("Cookie", httpRequest.cookie());
		}
		for (Map.Entry<String, String> header : httpRequest.headers().entrySet()) {
			connection.addRequestProperty(header.getKey(), header.getValue());
		}
		if (connection instanceof HttpsURLConnection) {
			if (httpRequest.validateTLSCertificates()) {
				initUnsecuredTSL();
				((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
				((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
			}
		}
		return connection;
	}

	private synchronized void initUnsecuredTSL() throws IOException {
		if (sslSocketFactory == null) {
			sslSocketFactory = UrlUtils.defaultSSLSocketFactory();
		}
	}

	private static HostnameVerifier getInsecureVerifier() {
		return new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
	}

}
