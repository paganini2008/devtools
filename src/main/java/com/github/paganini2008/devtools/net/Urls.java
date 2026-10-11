/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
 * URL utilities: conversion, reading content from a URL and writing it to streams or files.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Urls {

	private static SSLSocketFactory sslSocketFactory;

	/**
	 * Converts a string to a URI.
	 *
	 * @throws IllegalArgumentException if the string is not a valid URL or URI
	 */
	public static URI toURI(String url) {
		try {
			return toURL(url).toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URL: " + url, e);
		}
	}

	/**
	 * Converts a string to a URL.
	 *
	 * @throws IllegalArgumentException if the string is not a valid URL
	 */
	public static URL toURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + url, e);
		}
	}

	/**
	 * Resolves a (relative) URL against a base URL and converts it to a URI.
	 *
	 * @throws IllegalArgumentException if the base URL is <code>null</code> or the result is invalid
	 */
	public static URI toURI(URL baseUrl, String url) {
		try {
			return toURL(baseUrl, url).toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
		}
	}

	/**
	 * Resolves a (relative) URL against a base URL.
	 *
	 * @throws IllegalArgumentException if the base URL is <code>null</code> or the result is invalid
	 */
	public static URL toURL(URL baseUrl, String url) {
		Assert.isNull(baseUrl, "Base url must not be null.");
		try {
			return new URL(baseUrl, url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
		}
	}

	/**
	 * Resolves the path segments joined by "/" against a base URL and converts it to a URI.
	 *
	 * @throws IllegalArgumentException if an argument is <code>null</code> or the result is invalid
	 */
	public static URI toURI(URL baseUrl, String[] names) {
		try {
			return toURL(baseUrl, names).toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URL: " + baseUrl, e);
		}
	}

	/**
	 * Resolves the path segments joined by "/" against a base URL.
	 *
	 * @throws IllegalArgumentException if an argument is <code>null</code> or the result is invalid
	 */
	public static URL toURL(URL baseUrl, String[] names) {
		String path = toPath(names);
		return toURL(baseUrl, path);
	}

	/**
	 * Returns the protocol, host and port part of a URL, e.g. "http://example.com:8080".
	 */
	public static URL toHostUrl(String url) {
		return toHostUrl(toURL(url));
	}

	/**
	 * Returns the protocol, host and port part of a URL, e.g. "http://example.com:8080".
	 *
	 * @throws IllegalArgumentException if the URL is <code>null</code>
	 */
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

	/**
	 * Splits the query string of a URL into a map. Values are not decoded.
	 *
	 * @return the parameters, or <code>null</code> if the URL has no query string
	 * @throws IllegalArgumentException if the URL is <code>null</code>
	 */
	public static Map<String, String> getQueryMap(URL url) {
		Assert.isNull(url, "Base url must not be null.");
		String str = url.getQuery();
		return StringUtils.isNotBlank(str) ? StringUtils.splitAsMap(str, "&", "=") : null;
	}

	/**
	 * Joins the entries as "k1=v1&amp;k2=v2". Values are not encoded; <code>null</code> returns an empty string.
	 */
	public static String serialize(Map<String, ?> kwargs) {
		return MapUtils.join(kwargs, "=", "&");
	}

	/**
	 * Reads the content of a URL as a string. The connection stream is always closed.
	 */
	public static String toString(String url, String charset) throws IOException {
		return toString(toURL(url), charset);
	}

	public static String toString(URL url, String charset) throws IOException {
		return toString(url, CharsetUtils.toCharset(charset));
	}

	public static String toString(URL url, Charset charset) throws IOException {
		InputStream in = openStream(url);
		try {
			return IOUtils.toString(in, charset);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String toString(URLConnection url, Charset charset) throws IOException {
		InputStream in = openStream(url);
		try {
			return IOUtils.toString(in, charset);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String toString(URLConnection url, String charset) throws IOException {
		return toString(url, CharsetUtils.toCharset(charset));
	}

	/**
	 * Reads the content of a URL as characters. The connection stream is always closed.
	 */
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
		InputStream in = openStream(url);
		try {
			return IOUtils.toCharArray(in, charset);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static char[] toCharArray(URLConnection url, String charset) throws IOException {
		return toCharArray(url, CharsetUtils.toCharset(charset));
	}

	public static char[] toCharArray(URLConnection url, Charset charset) throws IOException {
		InputStream in = openStream(url);
		try {
			return IOUtils.toCharArray(in, charset);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Reads the content of a URL as bytes. The connection stream is always closed.
	 */
	public static byte[] toByteArray(String url) throws IOException {
		return toByteArray(toURL(url));
	}

	public static byte[] toByteArray(URL url) throws IOException {
		InputStream in = openStream(url);
		try {
			return IOUtils.toByteArray(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static byte[] toByteArray(URLConnection conn) throws IOException {
		InputStream in = openStream(conn);
		try {
			return IOUtils.toByteArray(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Opens a stream to a URL. The caller must close it. For HTTPS, certificates and host names are <b>not verified</b>;
	 * use {@link #openStream(String, boolean)} with {@code true} to verify them.
	 *
	 * @throws IllegalArgumentException if the URL is blank or invalid
	 */
	public static InputStream openStream(String url) throws IOException {
		return openStream(url, false);
	}

	/**
	 * Opens a stream to a URL. The caller must close it. For HTTPS, certificates and host names are verified by the
	 * JDK when {@code validateTLSCertificates} is true, and not verified at all when it is false.
	 *
	 * @throws IllegalArgumentException if the URL is blank or invalid
	 * @since 3.0.0
	 */
	public static InputStream openStream(String url, boolean validateTLSCertificates) throws IOException {
		Assert.hasNoText(url);
		return openStream(toURL(url), validateTLSCertificates);
	}

	/**
	 * Opens a stream to a URL. The caller must close it. For HTTPS, certificates and host names are <b>not verified</b>;
	 * use {@link #openStream(URL, boolean)} with {@code true} to verify them.
	 */
	public static InputStream openStream(URL url) throws IOException {
		return openStream(url, false);
	}

	/**
	 * Opens a stream to a URL. The caller must close it. For HTTPS, certificates and host names are verified by the
	 * JDK when {@code validateTLSCertificates} is true, and not verified at all when it is false.
	 *
	 * @since 3.0.0
	 */
	public static InputStream openStream(URL url, boolean validateTLSCertificates) throws IOException {
		Assert.isNull(url);
		return openStream(url.openConnection(), validateTLSCertificates);
	}

	/**
	 * Opens the input stream of a connection. The caller must close it. For HTTPS, certificates and host names are
	 * <b>not verified</b>; use {@link #openStream(URLConnection, boolean)} with {@code true} to verify them.
	 */
	public static InputStream openStream(URLConnection connection) throws IOException {
		return openStream(connection, false);
	}

	/**
	 * Opens the input stream of a connection. The caller must close it. For HTTPS, certificates and host names are
	 * verified by the JDK when {@code validateTLSCertificates} is true, and not verified at all when it is false.
	 *
	 * @since 3.0.0
	 */
	public static InputStream openStream(URLConnection connection, boolean validateTLSCertificates) throws IOException {
		Assert.isNull(connection);
		if (!validateTLSCertificates) {
			trustAll(connection);
		}
		return connection.getInputStream();
	}

	/**
	 * Connects to a HTTP(S) URL and returns the response code. For HTTPS, certificates and host names are <b>not
	 * verified</b>; use {@link #testConnection(URL, boolean)} with {@code true} to verify them.
	 */
	public static int testConnection(String url) throws IOException {
		return testConnection(new URL(url));
	}

	/**
	 * Connects to a HTTP(S) URL and returns the response code. The connection is disconnected afterwards. For HTTPS,
	 * certificates and host names are <b>not verified</b>; use {@link #testConnection(URL, boolean)} with {@code true} to
	 * verify them.
	 *
	 * @throws IllegalArgumentException if the URL is not a HTTP(S) URL
	 */
	public static int testConnection(URL url) throws IOException {
		return testConnection(url, false);
	}

	/**
	 * Connects to a HTTP(S) URL and returns the response code. The connection is disconnected afterwards. For HTTPS,
	 * certificates and host names are verified by the JDK when {@code validateTLSCertificates} is true, and not
	 * verified at all when it is false.
	 *
	 * @throws IllegalArgumentException if the URL is not a HTTP(S) URL
	 * @since 3.0.0
	 */
	public static int testConnection(URL url, boolean validateTLSCertificates) throws IOException {
		Assert.isNull(url, "Url must not be null.");
		URLConnection urlConnection = url.openConnection();
		if (!(urlConnection instanceof HttpURLConnection)) {
			throw new IllegalArgumentException("Not a http(s) url: " + url);
		}
		HttpURLConnection connection = (HttpURLConnection) urlConnection;
		if (!validateTLSCertificates) {
			trustAll(connection);
		}
		try {
			connection.connect();
			return connection.getResponseCode();
		} finally {
			connection.disconnect();
		}
	}

	private static void trustAll(URLConnection connection) throws IOException {
		if (connection instanceof HttpsURLConnection) {
			initUnsecuredTSL();
			((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) connection).setHostnameVerifier(getInsecureVerifier());
		}
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

	/**
	 * Creates a SSL socket factory which trusts all certificates. Use it for testing or trusted networks only.
	 */
	public static SSLSocketFactory defaultSSLSocketFactory() throws IOException {
		final TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllManager() };
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

	/**
	 * A trust manager which accepts every certificate.
	 */
	static class TrustAllManager implements X509TrustManager {

		public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
		}

		public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

	/**
	 * Copies the content of a URL to the output stream, which is left open.
	 */
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

	/**
	 * Downloads the content of a URL to a file, creating the parent directories if needed.
	 */
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

	/**
	 * Concatenates the values of all Set-Cookie response headers, each followed by ";".
	 */
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
