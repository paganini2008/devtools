/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;

/**
 * Urls
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Urls {

	private static SSLSocketFactory sslSocketFactory;

	public static URI toURI(String url) {
		try {
			return toURL(url).toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URL: " + url, e);
		}
	}

	public static URL toURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + url, e);
		}
	}

	public static URI toURI(URL baseUrl, String url) {
		try {
			return toURL(baseUrl, url).toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
		}
	}

	public static URL toURL(URL baseUrl, String url) {
		Assert.isNull(baseUrl, "Base url must not be null.");
		try {
			return new URL(baseUrl, url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
		}
	}

	public static URI toURI(URL baseUrl, String[] names) {
		try {
			return toURL(baseUrl, names).toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
		}
	}

	public static URL toURL(URL baseUrl, String[] names) {
		String path = toPath(names);
		return toURL(baseUrl, path);
	}

	public static URL toHostUrl(String url) {
		return toHostUrl(toURL(url));
	}

	public static URL toHostUrl(URL url) {
		Assert.isNull(url, "Base url must not be null.");
		try {
			return new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + url.toString(), e);
		}
	}

	private static String toPath(String[] names) {
		Assert.isNull(names, "Names must not be null.");
		StringBuilder path = new StringBuilder();
		for (int i = 0, l = names.length; i < l; i++) {
			path.append(names[i]);
			if (i != l - 1) {
				path.append("/");
			}
		}
		return path.toString();
	}

	public static Map<String, String> getQueryMap(URL url) {
		Assert.isNull(url, "Base url must not be null.");
		String str = url.getQuery();
		return StringUtils.isNotBlank(str) ? StringUtils.splitAsMap(str, "&", "=") : null;
	}

	public static String serialize(Map<String, ?> kwargs) {
		return MapUtils.join(kwargs, "=", "&");
	}

	public static String toString(String url, String charset) throws IOException {
		return toString(toURL(url), charset);
	}

	public static String toString(URL url, String charset) throws IOException {
		return toString(url, CharsetUtils.toCharset(charset));
	}

	public static String toString(URL url, Charset charset) throws IOException {
		return IOUtils.toString(openStream(url), charset);
	}

	public static String toString(URLConnection url, Charset charset) throws IOException {
		return IOUtils.toString(openStream(url), charset);
	}

	public static String toString(URLConnection url, String charset) throws IOException {
		return toString(url, CharsetUtils.toCharset(charset));
	}

	public static char[] toCharArray(String url, String charset) throws IOException {
		return toCharArray(toURL(url), charset);
	}

	public static char[] toCharArray(URL url, String charset) throws IOException {
		return toCharArray(url, CharsetUtils.toCharset(charset));
	}

	public static char[] toCharArray(String url, Charset charset) throws IOException {
		return toCharArray(toURL(url), charset);
	}

	public static char[] toCharArray(URL url, Charset charset) throws IOException {
		return IOUtils.toCharArray(openStream(url), charset);
	}

	public static char[] toCharArray(URLConnection url, String charset) throws IOException {
		return toCharArray(url, CharsetUtils.toCharset(charset));
	}

	public static char[] toCharArray(URLConnection url, Charset charset) throws IOException {
		return IOUtils.toCharArray(openStream(url), charset);
	}

	public static byte[] toByteArray(String url) throws IOException {
		return toByteArray(toURL(url));
	}

	public static byte[] toByteArray(URL url) throws IOException {
		return IOUtils.toByteArray(openStream(url));
	}

	public static byte[] toByteArray(URLConnection conn) throws IOException {
		return IOUtils.toByteArray(openStream(conn));
	}

	public static InputStream openStream(String url) throws IOException {
		Assert.hasNoText(url);
		return openStream(toURL(url));
	}

	public static InputStream openStream(URL url) throws IOException {
		Assert.isNull(url);
		return openStream(url.openConnection());
	}

	public static InputStream openStream(URLConnection connection) throws IOException {
		Assert.isNull(connection);
		if (connection instanceof HttpsURLConnection) {
			initUnsecuredTSL();
			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
		}
		return connection.getInputStream();
	}

	public static int testConnection(String url) throws IOException {
		return testConnection(new URL(url));
	}

	public static int testConnection(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (connection instanceof HttpsURLConnection) {
			initUnsecuredTSL();
			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
		}
		connection.connect();
		return connection.getResponseCode();
	}

	private static HostnameVerifier getInsecureVerifier() {
		return new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
	}

	private static synchronized void initUnsecuredTSL() throws IOException {
		if (sslSocketFactory == null) {
			sslSocketFactory = defaultSSLSocketFactory();
		}
	}

	public static SSLSocketFactory defaultSSLSocketFactory() throws IOException {
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
			}

			public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };
		final SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Can't create unsecure trust manager.", e);
		} catch (KeyManagementException e) {
			throw new IOException("Can't create unsecure trust manager.", e);
		}
	}

	public static void write(String url, OutputStream os) throws IOException {
		write(toURL(url), os);
	}

	public static void write(URL url, OutputStream os) throws IOException {
		InputStream in = null;
		try {
			in = openStream(url);
			IOUtils.copy(in, os);
		} finally {
			IOUtils.close(in);
		}
	}

	public static void write(URLConnection url, OutputStream os) throws IOException {
		InputStream in = null;
		try {
			in = openStream(url);
			IOUtils.copy(in, os);
		} finally {
			IOUtils.close(in);
		}
	}

	public static void write(String url, String file) throws IOException {
		write(url, new File(file));
	}

	public static void write(String url, File file) throws IOException {
		write(toURL(url), file);
	}

	public static void write(URL url, File file) throws IOException {
		OutputStream out = null;
		try {
			out = FileUtils.openOutputStream(file);
			write(url, out);
		} finally {
			IOUtils.close(out);
		}
	}

	public static void write(URLConnection url, File file) throws IOException {
		OutputStream out = null;
		try {
			out = FileUtils.openOutputStream(file);
			write(url, out);
		} finally {
			IOUtils.close(out);
		}
	}

	public static String getCookieString(HttpURLConnection connection) {
		Assert.isNull(connection);
		String name, value, cookie = "";
		for (int i = 1; (name = connection.getHeaderFieldKey(i)) != null; i++) {
			if (name.equalsIgnoreCase("Set-Cookie")) {
				value = connection.getHeaderField(i);
				cookie += value + ";";
			}
		}
		return cookie;
	}

}
