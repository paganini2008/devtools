package com.github.paganini2008.devtools.http;

import static com.github.paganini2008.devtools.io.IOUtils.NEWLINE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.io.IOUtils;

/**
 * HttpResponseImpl
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class HttpResponseImpl extends HttpBaseImpl<HttpResponse> implements HttpResponse, HttpReponseBody {

	private static final long serialVersionUID = 2657269569071379931L;
	private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
	private HttpResponse previous;
	private int statusCode;
	private String statusMessage;
	private String contentType;
	private long length;
	private ByteBuffer byteData;
	private int numRedirects = 0;

	public HttpResponseImpl() {
	}

	protected HttpResponseImpl(HttpResponse response) {
		this.previous = response;
	}

	public int statusCode() {
		return statusCode;
	}

	public String statusMessage() {
		return statusMessage;
	}

	public String contentType() {
		return contentType;
	}

	public long length() {
		return length;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("URL: ").append(url()).append(NEWLINE);
		str.append("StatusCode: ").append(statusCode()).append(NEWLINE);
		str.append("StatusMessage: ").append(statusMessage()).append(NEWLINE);
		str.append("ContentType: ").append(contentType()).append(NEWLINE);
		str.append("Charset: ").append(charset()).append(NEWLINE);
		str.append("Length: ").append(length()).append(NEWLINE);
		str.append("Headers: ").append(headers()).append(NEWLINE);
		str.append("Cookies: ").append(cookies()).append(NEWLINE);
		return str.toString();
	}

	public String body() {
		if(charset == null){
			charset = "UTF-8";
		}
		String body = Charset.forName(charset).decode(byteData).toString();
		byteData.rewind();
		return body;
	}

	public byte[] bytes() {
		return byteData.array();
	}

	public HttpResponse previous() {
		return previous;
	}

	public int numRedirects() {
		return numRedirects;
	}

	public void refresh(HttpURLConnection connection) throws IOException {
		method = HttpMethod.valueOf(connection.getRequestMethod());
		url = connection.getURL();
		statusCode = connection.getResponseCode();
		statusMessage = connection.getResponseMessage();
		contentType = connection.getContentType();
		charset = getCharsetFromContentType(contentType);
		Map<String, List<String>> headers = connection.getHeaderFields();
		if (headers != null) {
			processResponseHeaders(headers);
		}
		if (previous != null) {
			for (Map.Entry<String, String> prevCookie : previous.cookies().entrySet()) {
				if (!hasCookie(prevCookie.getKey())) {
					cookie(prevCookie.getKey(), prevCookie.getValue());
				}
			}
			numRedirects = previous.numRedirects() + 1;
		}
	}

	public void fillData(ByteBuffer byteData) {
		this.byteData = byteData;
		this.length = byteData.capacity();
	}

	private static String getCharsetFromContentType(String contentType) {
		if (StringUtils.isBlank(contentType)) {
			return null;
		}
		Matcher m = charsetPattern.matcher(contentType);
		if (m.find()) {
			String charset = m.group(1).trim();
			charset = charset.replace("charset=", "");
			if (charset.length() == 0)
				return null;
			try {
				if (Charset.isSupported(charset)) {
					return charset;
				}
				charset = charset.toUpperCase(Locale.ENGLISH);
				if (Charset.isSupported(charset)) {
					return charset;
				}
			} catch (IllegalCharsetNameException e) {
				return null;
			}
		}
		return null;
	}

	private void processResponseHeaders(Map<String, List<String>> headers) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String name = entry.getKey();
			if (StringUtils.isBlank(name)) {
				continue;
			}
			List<String> values = entry.getValue();
			if (name.equalsIgnoreCase("Set-Cookie")) {
				Map<String, String> cookieMap;
				for (String value : values) {
					if (value == null) {
						continue;
					}
					cookieMap = StringUtils.splitAsMap(value, ";", "=");
					Map.Entry<String, String> first = MapUtils.getFirstEntry(cookieMap);
					String cookieName = first.getKey();
					String cookieVal = first.getValue();
					if (cookieName.length() > 0) {
						cookie(cookieName, cookieVal);
					}
				}
			} else {
				if (CollectionUtils.isNotEmpty(values)) {
					header(name, values.get(0));
				}
			}
		}
	}

	public void output(OutputStream os, int maxSize) throws IOException {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(bytes());
			IOUtils.copy(in, os, maxSize);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
