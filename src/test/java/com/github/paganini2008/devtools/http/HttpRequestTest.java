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
package com.github.paganini2008.devtools.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * HttpRequestTest, tests the request settings and connection creation without
 * sending anything
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class HttpRequestTest {

	@Test
	public void defaults() {
		HttpRequest request = new HttpRequests.GetBuilder("http://127.0.0.1/a").build();
		assertEquals("GET", request.method());
		assertEquals("http://127.0.0.1/a", request.url().toString());
		assertEquals("UTF-8", request.charset());
		assertEquals(60000, request.connectTimeout());
		assertEquals(60000, request.readTimeout());
		assertEquals(1024 * 1024, request.maxBodySize());
		assertEquals(3, request.maxRequests());
		assertEquals(10, request.maxRedirects());
		assertTrue(request.validateTLSCertificates());
		assertTrue(request.followRedirects());
		assertFalse(request.doOutput());
		assertFalse(request.ignoreHttpErrors());
		assertFalse(request.ignoreContentType());
		assertFalse(request.retryRequestsIfError());
		assertNull(request.proxy());
		assertNull(request.baseUrl());
		assertEquals("gzip", request.header("Accept-Encoding"));
	}

	@Test
	public void builders_setMethodAndOutput() {
		assertEquals("HEAD", new HttpRequests.HeadBuilder("http://h/").build().method());
		HttpRequest post = new HttpRequests.PostBuilder("http://h/").build();
		assertTrue(post.doOutput());
		assertFalse(post.followRedirects());
		assertTrue(new HttpRequests.PutBuilder("http://h/").build().doOutput());
		assertTrue(new HttpRequests.PatchBuilder("http://h/").build().doOutput());
		assertFalse(new HttpRequests.DeleteBuilder("http://h/").build().doOutput());
		assertFalse(new HttpRequests.TraceBuilder("http://h/").build().doOutput());
		assertTrue(new HttpRequests.OptionsBuilder("http://h/").build().doOutput());
		assertThrows(IllegalArgumentException.class, () -> new HttpRequests.GetBuilder("not a url"));
	}

	@Test
	public void builder_setsAllOptions() {
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("X-A", "1");
		Map<String, String> cookies = Collections.singletonMap("c", "v");
		Map<String, String> data = Collections.singletonMap("d", "1");
		InputStream in = new ByteArrayInputStream(new byte[0]);
		HttpRequest request = new HttpRequestBuilder("POST", "http://127.0.0.1/").timeout(1000).connectTimeout(2000).readTimeout(3000)
				.maxBodySize(10).maxRedirects(1).ignoreHttpErrors(true).ignoreContentType(true).validateTLSCertificates(false)
				.retryRequestsIfError(true).headers(headers).headers(null).cookies(cookies).cookies(null).data(data).data("f", "a.txt", in)
				.data((Map<String, String>) null).charset("GBK").build();
		assertEquals(2000, request.connectTimeout());
		assertEquals(3000, request.readTimeout());
		assertEquals(10, request.maxBodySize());
		assertEquals(1, request.maxRedirects());
		assertTrue(request.ignoreHttpErrors());
		assertTrue(request.ignoreContentType());
		assertFalse(request.validateTLSCertificates());
		assertTrue(request.retryRequestsIfError());
		assertEquals("1", request.header("x-a"));
		assertEquals("v", request.cookie("c"));
		assertEquals("GBK", request.charset());
		assertEquals(2, request.data().size());
		assertEquals("d", request.data().get(0).name());
		assertSame(in, request.data().get(1).inputStream());
		assertTrue(request.data().get(1).hasInputStream());

		assertThrows(IllegalArgumentException.class, () -> request.readTimeout(-1));
		assertThrows(IllegalArgumentException.class, () -> request.connectTimeout(-1));
		assertThrows(IllegalArgumentException.class, () -> new HttpRequestBuilder("GET", "http://h/").userAgent(null));
		assertThrows(IllegalArgumentException.class, () -> new HttpRequestBuilder("GET", "http://h/").referer(null));
	}

	@Test
	public void dataCollectionsAndFiles(@TempDir File dir) throws IOException {
		HttpRequestImpl request = (HttpRequestImpl) new HttpRequests.PostBuilder("http://127.0.0.1/").build();
		request.data(Arrays.<NameValuePair>asList(new BasicNameValuePair("a", "1"), new BasicNameValuePair("b", "2", null)));
		request.data((java.util.Collection<NameValuePair>) null);
		File file = new File(dir, "f.txt");
		Files.write(file.toPath(), "x".getBytes());
		request.data("file", "f.txt", file);
		assertEquals(3, request.data().size());
		assertTrue(request.data().get(2).hasInputStream());
		request.data().get(2).inputStream().close();
		assertThrows(IllegalStateException.class, () -> request.data("file", "g.txt", new File(dir, "missing.txt")));
		assertThrows(IllegalArgumentException.class, () -> request.data((NameValuePair) null));
	}

	@Test
	public void headersAndCookies() {
		HttpRequest request = new HttpRequests.GetBuilder("http://127.0.0.1/").build();
		request.header("Content-Type", "text/plain");
		assertTrue(request.hasHeader("content-type"));
		assertTrue(request.hasHeaderWithValue("CONTENT-TYPE", "TEXT/PLAIN"));
		assertFalse(request.hasHeaderWithValue("X-None", "a"));
		request.removeHeader("content-type");
		assertFalse(request.hasHeader("Content-Type"));
		assertNull(request.header("Content-Type"));
		assertThrows(IllegalArgumentException.class, () -> request.header("", "a"));
		assertThrows(IllegalArgumentException.class, () -> request.header("a", null));
		assertThrows(IllegalArgumentException.class, () -> request.header(null));

		request.cookie("a", "1").cookie("b", "2");
		assertTrue(request.hasCookie("a"));
		assertEquals("a=1; b=2", request.cookie());
		request.removeCookie("a");
		assertFalse(request.hasCookie("a"));
		assertEquals("b=2", request.cookie());
		assertEquals(1, request.cookies().size());
		assertThrows(IllegalArgumentException.class, () -> request.cookie("a", null));
		assertThrows(IllegalArgumentException.class, () -> request.cookie(""));

		assertThrows(IllegalCharsetNameException.class, () -> request.charset("no-such-charset"));
		assertThrows(IllegalArgumentException.class, () -> request.method(""));
		assertThrows(IllegalArgumentException.class, () -> request.url(null));
		request.proxy("127.0.0.1", 8888);
		assertEquals(new InetSocketAddress("127.0.0.1", 8888), request.proxy());
		request.followRedirects(false).doOutput(true);
		assertFalse(request.followRedirects());
		assertTrue(request.doOutput());
	}

	@Test
	public void nameValuePair() {
		BasicNameValuePair pair = new BasicNameValuePair();
		pair.name("n").value("v");
		assertEquals("n", pair.name());
		assertEquals("v", pair.value());
		assertFalse(pair.hasInputStream());
		InputStream in = new ByteArrayInputStream(new byte[0]);
		pair.inputStream(in);
		assertSame(in, pair.inputStream());
		assertTrue(new BasicNameValuePair("a", "b", in).hasInputStream());
	}

	@Test
	public void connectionFactory_configuresConnection() throws IOException {
		HttpRequest request = new HttpRequests.GetBuilder("http://127.0.0.1:1/a").cookie("c", "v").header("X-A", "1").timeout(1234).build();
		HttpURLConnection connection = new DefaultHttpConnectionFactory().openConnection(request);
		assertEquals("GET", connection.getRequestMethod());
		assertEquals(1234, connection.getConnectTimeout());
		assertEquals(1234, connection.getReadTimeout());
		assertEquals("c=v", connection.getRequestProperty("Cookie"));
		assertEquals("1", connection.getRequestProperty("X-A"));
		assertTrue(connection.getInstanceFollowRedirects());

		request.proxy("127.0.0.1", 1);
		assertNotNull(new DefaultHttpConnectionFactory().openConnection(request));

		HttpRequest patch = new HttpRequests.PatchBuilder("http://127.0.0.1:1/a").build();
		connection = new DefaultHttpConnectionFactory().openConnection(patch);
		assertEquals("POST", connection.getRequestMethod());
		assertEquals("PATCH", connection.getRequestProperty("X-HTTP-Method-Override"));
	}

	@Test
	public void connectionFactory_tlsValidation() throws IOException {
		DefaultHttpConnectionFactory factory = new DefaultHttpConnectionFactory();
		HttpRequest secure = new HttpRequests.GetBuilder("https://127.0.0.1:1/a").build();
		HttpsURLConnection connection = (HttpsURLConnection) factory.openConnection(secure);
		assertSame(HttpsURLConnection.getDefaultSSLSocketFactory(), connection.getSSLSocketFactory());
		assertSame(HttpsURLConnection.getDefaultHostnameVerifier(), connection.getHostnameVerifier());

		HttpRequest insecure = new HttpRequests.GetBuilder("https://127.0.0.1:1/a").validateTLSCertificates(false).build();
		connection = (HttpsURLConnection) factory.openConnection(insecure);
		assertFalse(HttpsURLConnection.getDefaultSSLSocketFactory() == connection.getSSLSocketFactory());
		assertTrue(connection.getHostnameVerifier().verify("any.host", null));
		// the trust-all factory is created once
		assertSame(connection.getSSLSocketFactory(), ((HttpsURLConnection) factory.openConnection(insecure)).getSSLSocketFactory());
	}

	@Test
	public void httpMethods() {
		assertTrue(HttpMethod.POST.doOutput());
		assertFalse(HttpMethod.GET.doOutput());
		assertTrue(HttpMethod.GET.followRedirects());
		assertFalse(HttpMethod.TRACE.doOutput());
		assertEquals(8, HttpMethod.values().length);
	}

	@Test
	public void responseWithoutData() {
		HttpResponseImpl response = new HttpResponseImpl();
		assertNull(response.body());
		assertEquals(0, response.bytes().length);
		assertEquals(0, response.length());
	}

}
