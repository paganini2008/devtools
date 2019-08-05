package com.github.paganini2008.devtools.http;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.net.UrlUtils;

/**
 * Connection
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Connection extends AbstractRequestBuilder {

	private static final String CONTENT_ENCODING = "Content-Encoding";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	private static final char[] mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();
	private static final int boundaryLength = 32;
	private static final String LOCATION = "Location";
	private static final Pattern xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");
	private static final HttpConnectionFactory defaultConnectionFactory = new DefaultHttpConnectionFactory();

	private final HttpConnectionFactory connectionFactory;

	public Connection(String url) {
		this(url, defaultConnectionFactory);
	}

	public Connection(String url, HttpConnectionFactory connectionFactory) {
		this(UrlUtils.toURL(url), connectionFactory);
	}

	public Connection(URL url) {
		this(url, defaultConnectionFactory);
	}

	public Connection(URL url, HttpConnectionFactory connectionFactory) {
		request.url(url);
		this.connectionFactory = connectionFactory;
	}

	public static Connection get(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.GET);
		return c;
	}

	public static Connection get(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = get(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	public static Connection post(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.POST);
		return c;
	}

	public static Connection post(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = post(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	public static Connection put(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.PUT);
		return c;
	}

	public static Connection put(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = put(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	public static Connection head(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.HEAD);
		return c;
	}

	public static Connection head(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = head(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	public static Connection delete(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.DELETE);
		return c;
	}

	public static Connection delete(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = delete(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	public static Connection options(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.OPTIONS);
		return c;
	}

	public static Connection options(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = options(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	public static Connection patch(String url) {
		Connection c = new Connection(url);
		c.method(HttpMethod.PATCH);
		return c;
	}

	public static Connection patch(String url, Map<String, String> headers, Map<String, String> data) {
		Connection c = patch(url);
		c.headers(headers);
		c.data(data);
		return c;
	}

	protected HttpResponse response = new HttpResponseImpl();
	private int numRequests;
	private long elapsedTime;
	private long startTime = -1;

	public long elapsedTime() {
		return elapsedTime;
	}

	public int numRequests() {
		return numRequests;
	}

	private static String mimeBoundary() {
		final StringBuilder mime = new StringBuilder(boundaryLength);
		final Random rand = new Random();
		for (int i = 0; i < boundaryLength; i++) {
			mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
		}
		return mime.toString();
	}

	private String setOutputContentType() {
		boolean needsMulti = false;
		for (NameValuePair nameValue : request.data()) {
			if (nameValue.hasInputStream()) {
				needsMulti = true;
				break;
			}
		}
		String bound = null;
		if (needsMulti) {
			bound = mimeBoundary();
			request.header(CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + bound);
		} else {
			request.header(CONTENT_TYPE, FORM_URL_ENCODED + "; charset=" + request.charset());
		}
		return bound;
	}

	private void serialise() throws IOException {
		URL in = request.url();
		StringBuilder url = new StringBuilder();
		boolean first = true;
		url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
		if (in.getQuery() != null) {
			url.append(in.getQuery());
			first = false;
		}
		for (NameValuePair nameValue : request.data()) {
			if (!first) {
				url.append('&');
			} else {
				first = false;
			}
			url.append(URLEncoder.encode(nameValue.name(), request.charset())).append('=')
					.append(URLEncoder.encode(nameValue.value(), request.charset()));
		}
		request.url(new URL(url.toString()));
		request.data().clear();
	}

	private void writePost(OutputStream outputStream, String bound) throws IOException {
		final List<NameValuePair> data = request.data();
		final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, request.charset()));
		if (bound != null) {
			for (NameValuePair nameValue : data) {
				w.write("--");
				w.write(bound);
				w.write("\r\n");
				w.write("Content-Disposition: form-data; name=\"");
				w.write(encodeMimeName(nameValue.name()));
				w.write("\"");
				if (nameValue.hasInputStream()) {
					w.write("; filename=\"");
					w.write(encodeMimeName(nameValue.value()));
					w.write("\"\r\nContent-Type: application/octet-stream\r\n\r\n");
					w.flush();
					IOUtils.copy(nameValue.inputStream(), outputStream);
					outputStream.flush();
				} else {
					w.write("\r\n\r\n");
					w.write(nameValue.value());
				}
				w.write("\r\n");
			}
			w.write("--");
			w.write(bound);
			w.write("--");
		} else {
			boolean first = true;
			for (NameValuePair nameValue : data) {
				if (!first) {
					w.append('&');
				} else {
					first = false;
				}
				w.write(URLEncoder.encode(nameValue.name(), request.charset()));
				w.write('=');
				w.write(URLEncoder.encode(nameValue.value(), request.charset()));
			}
		}
		w.close();
	}

	private static String encodeMimeName(String val) {
		if (val == null) {
			return null;
		}
		return val.replaceAll("\"", "%22");
	}

	public HttpResponse execute() throws IOException {
		if (startTime < 0) {
			startTime = System.currentTimeMillis();
		}
		String protocol = request.url().getProtocol();
		if (!protocol.equals("http") && !protocol.equals("https")) {
			throw new MalformedURLException("Only http & https protocols supported.");
		}
		String mimeBoundary = null;
		if (!request.method().hasBody() && request.data().size() > 0) {
			serialise();
		} else if (request.method().hasBody()) {
			mimeBoundary = setOutputContentType();
		}
		HttpURLConnection connection = connectionFactory.createHttpConnection(request);
		try {
			connection.connect();
			if (connection.getDoOutput()) {
				writePost(connection.getOutputStream(), mimeBoundary);
			}
			((HttpResponseImpl) response).refresh(connection);
			if (response.hasHeader(LOCATION) && request.followRedirects()) {
				if (response.numRedirects() > request.maxRedirects()) {
					final List<String> redirectUrls = new ArrayList<String>();
					HttpResponse current = response, last = null;
					while (null != (last = current.previous())) {
						redirectUrls.add(last.url().toString());
						current = last;
					}
					Collections.reverse(redirectUrls);
					throw new TooManyRedirectsException(
							String.format("Too many redirects occurred trying to load URL %s", response.url()), redirectUrls);
				}

				String location = response.header(LOCATION);
				URL target = UrlUtils.toURL(request.url(), location);
				if (StringUtils.isNotBlank(request.baseUrl()) && !target.toString().startsWith(request.baseUrl())) {
					throw new IllegalStateException(
							"Unexpected redirection. RequestUrl: " + target + ", baseUrl:" + request.baseUrl());
				}
				request.url(target);
				request.method(HttpMethod.GET);
				request.data().clear();
				for (Map.Entry<String, String> cookie : response.cookies().entrySet()) {
					request.cookie(cookie.getKey(), cookie.getValue());
				}
				response = new HttpResponseImpl(response);
				return execute();
			}
			int status = connection.getResponseCode();
			if (status < 200 || status >= 400) {
				if (request.retryRequests() && numRequests < request.maxRequests()) {
					numRequests++;
					return execute();
				} else if (!request.ignoreHttpErrors()) {
					throw new HttpStatusException("HTTP error fetching URL", status, request.url().toString());
				}
			}
			String contentType = response.contentType();
			if (contentType != null && !request.ignoreContentType() && !contentType.startsWith("text/")
					&& !xmlContentTypeRxp.matcher(contentType).matches()) {
				throw new UnsupportedMimeTypeException(
						"Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml", contentType,
						request.url().toString());
			}
			ByteBuffer byteData;
			if (connection.getContentLength() != 0) {
				InputStream bodyStream = null;
				InputStream dataStream = null;
				try {
					dataStream = connection.getErrorStream() != null ? connection.getErrorStream() : connection.getInputStream();
					bodyStream = response.hasHeaderWithValue(CONTENT_ENCODING, "gzip")
							? new BufferedInputStream(new GZIPInputStream(dataStream)) : new BufferedInputStream(dataStream);
					byteData = IOUtils.toByteBuffer(bodyStream, request.maxBodySize());
				} finally {
					if (bodyStream != null) {
						bodyStream.close();
					}
					if (dataStream != null) {
						dataStream.close();
					}
				}
			} else {
				byteData = IOUtils.emptyByteBuffer();
			}
			((HttpResponseImpl) response).fillData(byteData);
		} finally {
			connection.disconnect();
			elapsedTime = System.currentTimeMillis() - startTime;

			startTime = -1;
			numRequests = 0;
		}
		return response;
	}

}
