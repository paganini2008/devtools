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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.io.IOUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * HttpClientTest, runs against a local {@link HttpServer}
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class HttpClientTest {

	private static HttpServer server;
	private static String baseUrl;
	private static final AtomicInteger errorCounter = new AtomicInteger();

	private final HttpClient client = new HttpClient();

	private static void respond(HttpExchange exchange, int status, String contentType, byte[] body) throws IOException {
		if (contentType != null) {
			exchange.getResponseHeaders().set("Content-Type", contentType);
		}
		exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
		if (body.length > 0) {
			OutputStream os = exchange.getResponseBody();
			os.write(body);
		}
		exchange.close();
	}

	private static void respond(HttpExchange exchange, int status, String body) throws IOException {
		respond(exchange, status, "text/plain; charset=UTF-8", body.getBytes(StandardCharsets.UTF_8));
	}

	@BeforeAll
	public static void startServer() throws IOException {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/text", exchange -> {
			exchange.getResponseHeaders().add("Set-Cookie", "sid=abc123; Path=/");
			exchange.getResponseHeaders().add("Set-Cookie", "flag");
			exchange.getResponseHeaders().add("X-Test", "yes");
			respond(exchange, 200, "hello 你好");
		});
		server.createContext("/echo", exchange -> {
			StringBuilder str = new StringBuilder();
			str.append("method=").append(exchange.getRequestMethod()).append('\n');
			str.append("query=").append(exchange.getRequestURI().getRawQuery()).append('\n');
			for (String name : Arrays.asList("Cookie", "Content-Type", "X-HTTP-Method-Override", "User-Agent", "Referer", "X-Custom")) {
				str.append(name).append('=').append(exchange.getRequestHeaders().getFirst(name)).append('\n');
			}
			str.append("body=").append(new String(IOUtils.toByteArray(exchange.getRequestBody()), StandardCharsets.UTF_8));
			respond(exchange, 200, str.toString());
		});
		server.createContext("/gzip", exchange -> {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
				gzip.write("compressed content".getBytes(StandardCharsets.UTF_8));
			}
			exchange.getResponseHeaders().set("Content-Encoding", "gzip");
			respond(exchange, 200, "text/plain", bos.toByteArray());
		});
		server.createContext("/json", exchange -> respond(exchange, 200, "application/json", "{\"a\":1}".getBytes(StandardCharsets.UTF_8)));
		server.createContext("/xml", exchange -> respond(exchange, 200, "application/xml", "<a>1</a>".getBytes(StandardCharsets.UTF_8)));
		server.createContext("/big", exchange -> {
			byte[] data = new byte[10000];
			Arrays.fill(data, (byte) 'x');
			respond(exchange, 200, "text/plain", data);
		});
		server.createContext("/empty", exchange -> respond(exchange, 204, null, new byte[0]));
		server.createContext("/error", exchange -> {
			errorCounter.incrementAndGet();
			respond(exchange, 500, "boom");
		});
		server.createContext("/redirect", exchange -> {
			exchange.getResponseHeaders().add("Set-Cookie", "redirected=1");
			exchange.getResponseHeaders().set("Location", "/echo?from=redirect");
			respond(exchange, 302, "text/plain", new byte[0]);
		});
		server.createContext("/loop", exchange -> {
			exchange.getResponseHeaders().set("Location", "/loop");
			respond(exchange, 302, "text/plain", new byte[0]);
		});
		server.createContext("/away", exchange -> {
			exchange.getResponseHeaders().set("Location", "http://other.invalid/x");
			respond(exchange, 302, "text/plain", new byte[0]);
		});
		server.createContext("/created", exchange -> {
			exchange.getResponseHeaders().set("Location", "/text");
			respond(exchange, 201, "created");
		});
		server.start();
		baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
	}

	@AfterAll
	public static void stopServer() {
		server.stop(0);
	}

	@BeforeEach
	public void reset() {
		errorCounter.set(0);
	}

	private static Map<String, String> parse(String body) {
		Map<String, String> map = new java.util.HashMap<String, String>();
		for (String line : body.split("\n")) {
			int i = line.indexOf('=');
			if (i > 0 && !map.containsKey(line.substring(0, i))) {
				map.put(line.substring(0, i), line.substring(i + 1));
			}
		}
		return map;
	}

	@Test
	public void get_readsBodyHeadersAndCookies() throws IOException {
		HttpRequest request = new HttpRequests.GetBuilder(baseUrl + "/text").build();
		HttpResponse response = client.execute(request);
		assertEquals(200, response.statusCode());
		assertEquals("OK", response.statusMessage());
		assertEquals("hello 你好", response.body());
		// body can be read repeatedly
		assertEquals("hello 你好", response.body());
		assertEquals("UTF-8", response.charset());
		assertEquals("text/plain; charset=UTF-8", response.contentType());
		assertEquals("yes", response.header("x-test"));
		assertEquals("abc123", response.cookie("sid"));
		assertEquals("", response.cookie("flag"));
		assertEquals("GET", response.method());
		assertEquals(baseUrl + "/text", response.url().toString());
		assertEquals("hello 你好".getBytes(StandardCharsets.UTF_8).length, response.length());
		assertArrayEquals("hello 你好".getBytes(StandardCharsets.UTF_8), response.bytes());
		assertNull(response.previous());
		assertEquals(0, response.numRedirects());
		assertTrue(response.elapsedTime() >= 0);
		assertTrue(response.toString().contains("StatusCode: 200"));
	}

	@Test
	public void get_serialisesDataIntoQueryString() throws IOException {
		HttpRequest request = new HttpRequests.GetBuilder(baseUrl + "/echo?x=1").data("name", "a b&c").data("empty", (String) null)
				.data("k", "中").userAgent("ua").referer("ref").header("X-Custom", "c").cookie("c1", "v1").cookie("c2", "v2").build();
		Map<String, String> echo = parse(client.execute(request).body());
		assertEquals("GET", echo.get("method"));
		assertEquals("x=1&name=a+b%26c&empty=&k=%E4%B8%AD", echo.get("query"));
		assertEquals("ua", echo.get("User-Agent"));
		assertEquals("ref", echo.get("Referer"));
		assertEquals("c", echo.get("X-Custom"));
		assertEquals("c1=v1; c2=v2", echo.get("Cookie"));
		assertTrue(request.data().isEmpty());
	}

	@Test
	public void post_sendsUrlEncodedForm() throws IOException {
		HttpRequest request = new HttpRequests.PostBuilder(baseUrl + "/echo").data("a", "1", "b", "x y", "c").charset("UTF-8").build();
		Map<String, String> echo = parse(client.execute(request).body());
		assertEquals("POST", echo.get("method"));
		assertEquals("application/x-www-form-urlencoded; charset=UTF-8", echo.get("Content-Type"));
		assertEquals("a=1&b=x+y&c=", echo.get("body"));
	}

	@Test
	public void post_sendsMultipartAndClosesStream() throws IOException {
		AtomicBoolean closed = new AtomicBoolean();
		InputStream in = new ByteArrayInputStream("file-content".getBytes(StandardCharsets.UTF_8)) {
			@Override
			public void close() throws IOException {
				closed.set(true);
				super.close();
			}
		};
		HttpRequest request = new HttpRequests.PostBuilder(baseUrl + "/echo").data("field", "value").data("upload", "a\"b.txt", in)
				.build();
		String body = client.execute(request).body();
		Map<String, String> echo = parse(body);
		assertTrue(echo.get("Content-Type").startsWith("multipart/form-data; boundary="));
		assertTrue(body.contains("Content-Disposition: form-data; name=\"field\"\r\n\r\nvalue\r\n"));
		assertTrue(body.contains("name=\"upload\"; filename=\"a%22b.txt\"\r\nContent-Type: application/octet-stream\r\n\r\nfile-content\r\n"));
		assertTrue(closed.get());
	}

	@Test
	public void otherMethods() throws IOException {
		assertEquals("PUT", parse(client.execute(new HttpRequests.PutBuilder(baseUrl + "/echo").data("a", "1").build()).body()).get("method"));
		assertEquals("DELETE", parse(client.execute(new HttpRequests.DeleteBuilder(baseUrl + "/echo").build()).body()).get("method"));
		assertEquals("OPTIONS", parse(client.execute(new HttpRequests.OptionsBuilder(baseUrl + "/echo").build()).body()).get("method"));
		assertEquals("TRACE", parse(client.execute(new HttpRequests.TraceBuilder(baseUrl + "/echo").build()).body()).get("method"));
		// PATCH is tunneled through POST
		Map<String, String> echo = parse(client.execute(new HttpRequests.PatchBuilder(baseUrl + "/echo").data("a", "1").build()).body());
		assertEquals("POST", echo.get("method"));
		assertEquals("PATCH", echo.get("X-HTTP-Method-Override"));
		assertEquals("a=1", echo.get("body"));
		HttpResponse head = client.execute(new HttpRequests.HeadBuilder(baseUrl + "/text").build());
		assertEquals(200, head.statusCode());
		assertEquals("", head.body());
	}

	@Test
	public void gzipContent_isDecompressed() throws IOException {
		HttpResponse response = client.execute(new HttpRequests.GetBuilder(baseUrl + "/gzip").build());
		assertEquals("compressed content", response.body());
		assertTrue(response.hasHeaderWithValue("Content-Encoding", "GZIP"));
	}

	@Test
	public void contentType_isChecked() throws IOException {
		UnsupportedMimeTypeException e = assertThrows(UnsupportedMimeTypeException.class,
				() -> client.execute(new HttpRequests.GetBuilder(baseUrl + "/json").build()));
		assertEquals("application/json", e.getMimeType());
		assertEquals(baseUrl + "/json", e.getUrl());
		assertTrue(e.toString().contains("Mimetype=application/json"));
		assertEquals("{\"a\":1}", client.execute(new HttpRequests.GetBuilder(baseUrl + "/json").ignoreContentType(true).build()).body());
		assertEquals("<a>1</a>", client.execute(new HttpRequests.GetBuilder(baseUrl + "/xml").build()).body());
	}

	@Test
	public void maxBodySize_truncatesBody() throws IOException {
		HttpResponse response = client.execute(new HttpRequests.GetBuilder(baseUrl + "/big").maxBodySize(100).build());
		assertEquals(100, response.length());
		assertEquals(10000, client.execute(new HttpRequests.GetBuilder(baseUrl + "/big").build()).bytes().length);
	}

	@Test
	public void noContent() throws IOException {
		HttpResponse response = client.execute(new HttpRequests.GetBuilder(baseUrl + "/empty").build());
		assertEquals(204, response.statusCode());
		assertEquals(0, response.length());
		assertEquals("", response.body());
	}

	@Test
	public void httpError_throwsOrIsIgnored() throws IOException {
		HttpStatusException e = assertThrows(HttpStatusException.class,
				() -> client.execute(new HttpRequests.GetBuilder(baseUrl + "/error").build()));
		assertEquals(500, e.getStatusCode());
		assertEquals(baseUrl + "/error", e.getUrl());
		assertTrue(e.toString().contains("Status=500"));
		assertEquals(1, errorCounter.get());

		HttpResponse response = client.execute(new HttpRequests.GetBuilder(baseUrl + "/error").ignoreHttpErrors(true).build());
		assertEquals(500, response.statusCode());
		assertEquals("boom", response.body());
	}

	@Test
	public void retryRequestsIfError_sendsAtMostMaxRequests() throws IOException {
		HttpRequest request = new HttpRequests.GetBuilder(baseUrl + "/error").retryRequestsIfError(true).build();
		request.maxRequests(3);
		assertEquals(3, request.maxRequests());
		assertThrows(HttpStatusException.class, () -> client.execute(request));
		assertEquals(3, errorCounter.get());
	}

	@Test
	public void manualRedirect_followsLocationWithGet() throws IOException {
		HttpRequest request = new HttpRequests.PostBuilder(baseUrl + "/redirect").data("a", "1").build();
		assertFalse(request.followRedirects());
		HttpResponse response = client.execute(request);
		Map<String, String> echo = parse(response.body());
		// the redirection is done with GET and without the form data
		assertEquals("GET", echo.get("method"));
		assertEquals("from=redirect", echo.get("query"));
		assertEquals("redirected=1", echo.get("Cookie"));
		assertEquals(1, response.numRedirects());
		assertEquals(302, response.previous().statusCode());
		assertEquals("1", response.cookie("redirected"));
	}

	@Test
	public void manualRedirect_tooManyRedirects() {
		HttpRequest request = new HttpRequests.PostBuilder(baseUrl + "/loop").maxRedirects(2).build();
		assertEquals(2, request.maxRedirects());
		TooManyRedirectsException e = assertThrows(TooManyRedirectsException.class, () -> client.execute(request));
		List<String> urls = e.getRedirectUrls();
		assertEquals(3, urls.size());
		assertTrue(urls.get(0).endsWith("/loop"));
		assertTrue(e.toString().contains(" ==> "));
	}

	@Test
	public void manualRedirect_outsideBaseUrlIsRejected() {
		HttpRequest request = new HttpRequests.PostBuilder(baseUrl + "/away").build();
		request.baseUrl(baseUrl);
		assertEquals(baseUrl, request.baseUrl());
		assertThrows(IllegalStateException.class, () -> client.execute(request));
	}

	@Test
	public void locationHeaderOfNonRedirectStatus_isNotFollowed() throws IOException {
		HttpResponse response = client.execute(new HttpRequests.PostBuilder(baseUrl + "/created").build());
		assertEquals(201, response.statusCode());
		assertEquals("created", response.body());
		assertEquals(0, response.numRedirects());
	}

	@Test
	public void unsupportedProtocol() {
		assertThrows(MalformedURLException.class, () -> client.execute(new HttpRequests.GetBuilder("ftp://127.0.0.1/a").build()));
	}

	@Test
	public void saveAs(@TempDir File dir) throws IOException {
		HttpResponse response = client.execute(new HttpRequests.GetBuilder(baseUrl + "/text").build());
		File file = new File(dir, "a.txt");
		response.saveAs(file);
		assertEquals("hello 你好", new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
		StringWriter writer = new StringWriter();
		response.saveAs(writer, "UTF-8");
		assertEquals("hello 你好", writer.toString());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.saveAs(bos);
		assertEquals("hello 你好", new String(bos.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	public void customConnectionFactory() throws IOException {
		AtomicInteger opened = new AtomicInteger();
		DefaultHttpConnectionFactory delegate = new DefaultHttpConnectionFactory();
		HttpClient custom = new HttpClient(request -> {
			opened.incrementAndGet();
			return delegate.openConnection(request);
		});
		assertEquals("hello 你好", custom.execute(new HttpRequests.GetBuilder(baseUrl + "/text").build()).body());
		assertEquals(1, opened.get());
	}

}
