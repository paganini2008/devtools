package com.github.paganini2008.devtools.http;

import java.io.InputStream;
import java.util.Map;

import com.github.paganini2008.devtools.Assert;

/**
 * AbstractRequestBuilder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractRequestBuilder implements RequestBuilder2 {

	protected HttpRequest request = new HttpRequestImpl();

	public RequestBuilder2 userAgent(String userAgent) {
		Assert.isNull(userAgent, "User agent must not be null");
		request.header("User-Agent", userAgent);
		return this;
	}

	public RequestBuilder2 maxRedirects(int redirects) {
		request.maxRedirects(redirects);
		return this;
	}

	public RequestBuilder2 timeout(int millis) {
		request.connectTimeout(millis);
		request.readTimeout(millis);
		return this;
	}

	public RequestBuilder2 connectTimeout(int millis) {
		request.connectTimeout(millis);
		return this;
	}

	public RequestBuilder2 readTimeout(int millis) {
		request.readTimeout(millis);
		return this;
	}

	public RequestBuilder2 maxBodySize(int bytes) {
		request.maxBodySize(bytes);
		return this;
	}

	public RequestBuilder2 referer(String referer) {
		Assert.isNull(referer, "Referer must not be null");
		request.header("Referer", referer);
		return this;
	}

	public RequestBuilder2 followRedirects(boolean followRedirects) {
		request.followRedirects(followRedirects);
		return this;
	}

	public RequestBuilder2 method(HttpMethod method) {
		request.method(method);
		return this;
	}

	public RequestBuilder2 ignoreHttpErrors(boolean ignoreHttpErrors) {
		request.ignoreHttpErrors(ignoreHttpErrors);
		return this;
	}

	public RequestBuilder2 ignoreContentType(boolean ignoreContentType) {
		request.ignoreContentType(ignoreContentType);
		return this;
	}

	public RequestBuilder2 validateTLSCertificates(boolean value) {
		request.validateTLSCertificates(value);
		return this;
	}

	public RequestBuilder2 data(String key, String value) {
		request.data(key, value);
		return this;
	}

	public RequestBuilder2 data(String key, String filename, InputStream inputStream) {
		request.data(key, filename, inputStream);
		return this;
	}

	public RequestBuilder2 data(Map<String, String> data) {
		request.data(data);
		return this;
	}

	public RequestBuilder2 data(String... nameValues) {
		String prev = null;
		for (String arg : nameValues) {
			if (prev == null) {
				prev = arg;
			} else {
				data(prev, arg);
				prev = null;
			}
		}
		if (prev != null) {
			data(prev, null);
		}
		return this;
	}

	public RequestBuilder2 headers(Map<String, String> headers) {
		request.headers(headers);
		return this;
	}

	public RequestBuilder2 header(String name, String value) {
		request.header(name, value);
		return this;
	}

	public RequestBuilder2 cookie(String name, String value) {
		request.cookie(name, value);
		return this;
	}

	public RequestBuilder2 cookies(Map<String, String> cookies) {
		request.cookies(cookies);
		return this;
	}

	public RequestBuilder2 charset(String charset) {
		request.charset(charset);
		return this;
	}
}
