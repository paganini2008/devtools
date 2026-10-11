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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

/**
 * UrlsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class UrlsTest {

	private static final String CONTENT = "Hello, 世界";

	private static HttpServer server;
	private static String baseUrl;

	@TempDir
	File tempDir;

	@BeforeAll
	public static void startServer() throws IOException {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/hello", exchange -> {
			byte[] data = CONTENT.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Set-Cookie", "a=1");
			exchange.getResponseHeaders().add("Set-Cookie", "b=2");
			exchange.sendResponseHeaders(200, data.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(data);
			}
		});
		server.createContext("/missing", exchange -> {
			exchange.sendResponseHeaders(404, -1);
			exchange.close();
		});
		server.start();
		baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
	}

	@AfterAll
	public static void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	public void testToUrl() throws Exception {
		URL url = Urls.toURL("http://example.com/a/b?x=1");
		assertEquals("example.com", url.getHost());
		URI uri = Urls.toURI("http://example.com/a/b");
		assertEquals("/a/b", uri.getPath());
		assertThrows(IllegalArgumentException.class, () -> Urls.toURL("not a url"));
		assertThrows(IllegalArgumentException.class, () -> Urls.toURI("http://example.com/a b"));

		URL base = new URL("http://example.com/a/b");
		assertEquals("http://example.com/a/c", Urls.toURL(base, "c").toString());
		assertEquals("http://example.com/a/c", Urls.toURI(base, "c").toString());
		assertEquals("http://example.com/a/x/y/z", Urls.toURL(base, new String[] { "x", "y", "z" }).toString());
		assertEquals("http://example.com/a/x/y", Urls.toURI(base, new String[] { "x", "y" }).toString());
		assertThrows(IllegalArgumentException.class, () -> Urls.toURL((URL) null, "c"));
		assertThrows(IllegalArgumentException.class, () -> Urls.toURL(base, (String[]) null));
		assertThrows(IllegalArgumentException.class, () -> Urls.toURL(base, "foo://bar"));
		assertThrows(IllegalArgumentException.class, () -> Urls.toURI(base, "c d"));
		assertThrows(IllegalArgumentException.class, () -> Urls.toURI(base, new String[] { "c d" }));

		assertEquals("http://example.com:8080", Urls.toHostUrl("http://example.com:8080/a/b?x=1").toString());
		assertEquals("https://example.com", Urls.toHostUrl(new URL("https://example.com/a")).toString());
		assertThrows(IllegalArgumentException.class, () -> Urls.toHostUrl((URL) null));
	}

	@Test
	public void testQueryMap() throws Exception {
		Map<String, String> map = Urls.getQueryMap(new URL("http://example.com/?a=1&b=2"));
		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));
		assertNull(Urls.getQueryMap(new URL("http://example.com/")));
		assertThrows(IllegalArgumentException.class, () -> Urls.getQueryMap(null));

		Map<String, Object> kwargs = new LinkedHashMap<String, Object>();
		kwargs.put("a", 1);
		kwargs.put("b", "x");
		assertEquals("a=1&b=x", Urls.serialize(kwargs));
	}

	@Test
	public void testRead() throws Exception {
		String url = baseUrl + "/hello";
		assertEquals(CONTENT, Urls.toString(url, "UTF-8"));
		assertEquals(CONTENT, Urls.toString(new URL(url), StandardCharsets.UTF_8));
		assertEquals(CONTENT, Urls.toString(new URL(url).openConnection(), "UTF-8"));
		assertArrayEquals(CONTENT.toCharArray(), Urls.toCharArray(url, "UTF-8"));
		assertArrayEquals(CONTENT.toCharArray(), Urls.toCharArray(url, StandardCharsets.UTF_8));
		assertArrayEquals(CONTENT.toCharArray(), Urls.toCharArray(new URL(url).openConnection(), "UTF-8"));
		byte[] expected = CONTENT.getBytes(StandardCharsets.UTF_8);
		assertArrayEquals(expected, Urls.toByteArray(url));
		assertArrayEquals(expected, Urls.toByteArray(new URL(url).openConnection()));
		assertNotNull(Urls.openStream(url));
		Urls.openStream(url).close();
		assertThrows(IllegalArgumentException.class, () -> Urls.openStream(""));
		assertThrows(IOException.class, () -> Urls.toString(baseUrl + "/missing", "UTF-8"));
	}

	@Test
	public void testWrite() throws Exception {
		String url = baseUrl + "/hello";
		byte[] expected = CONTENT.getBytes(StandardCharsets.UTF_8);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Urls.write(url, out);
		assertArrayEquals(expected, out.toByteArray());
		out.reset();
		Urls.write(new URL(url).openConnection(), out);
		assertArrayEquals(expected, out.toByteArray());

		File f1 = new File(tempDir, "f1.txt");
		Urls.write(url, f1.getAbsolutePath());
		assertArrayEquals(expected, Files.readAllBytes(f1.toPath()));
		File f2 = new File(tempDir, "sub/f2.txt");
		Urls.write(new URL(url).openConnection(), f2);
		assertArrayEquals(expected, Files.readAllBytes(f2.toPath()));
	}

	@Test
	public void testConnection() throws Exception {
		assertEquals(200, Urls.testConnection(baseUrl + "/hello"));
		assertEquals(404, Urls.testConnection(new URL(baseUrl + "/missing")));
		assertThrows(IllegalArgumentException.class, () -> Urls.testConnection(new File(tempDir, "x").toURI().toURL()));
	}

	@Test
	public void testCookieString() throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/hello").openConnection();
		try {
			assertEquals(200, connection.getResponseCode());
			String cookie = Urls.getCookieString(connection);
			assertTrue(cookie.contains("a=1;"), cookie);
			assertTrue(cookie.contains("b=2;"), cookie);
		} finally {
			connection.disconnect();
		}
	}

	@Test
	public void testHttps() throws Exception {
		Urls.TrustAllManager trustManager = new Urls.TrustAllManager();
		trustManager.checkClientTrusted(new java.security.cert.X509Certificate[0], "RSA");
		trustManager.checkServerTrusted(new java.security.cert.X509Certificate[0], "RSA");
		assertEquals(0, trustManager.getAcceptedIssuers().length);
		SSLSocketFactory factory = Urls.defaultSSLSocketFactory();
		assertNotNull(factory);
		// no network access: the connection is configured but never opened
		HttpsURLConnection connection = (HttpsURLConnection) new URL("https://127.0.0.1:1/").openConnection();
		assertThrows(IOException.class, () -> Urls.openStream(connection));
		assertNotNull(connection.getSSLSocketFactory());
		assertTrue(connection.getHostnameVerifier().verify("any.host", null));
		assertThrows(IOException.class, () -> Urls.testConnection("https://127.0.0.1:1/"));
	}

}
