package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * RequestBuilder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface RequestBuilder2 {

	RequestBuilder2 userAgent(String userAgent);

	RequestBuilder2 timeout(int millis);

	RequestBuilder2 connectTimeout(int millis);

	RequestBuilder2 readTimeout(int millis);

	RequestBuilder2 maxBodySize(int bytes);

	RequestBuilder2 referer(String referrer);

	RequestBuilder2 followRedirects(boolean followRedirects);

	RequestBuilder2 method(HttpMethod method);

	RequestBuilder2 maxRedirects(int redirects);

	RequestBuilder2 ignoreHttpErrors(boolean ignoreHttpErrors);

	RequestBuilder2 ignoreContentType(boolean ignoreContentType);

	RequestBuilder2 validateTLSCertificates(boolean value);

	RequestBuilder2 data(String key, String value);

	RequestBuilder2 data(String key, String filename, InputStream inputStream);

	RequestBuilder2 data(Map<String, String> data);

	RequestBuilder2 data(String... nameValues);

	RequestBuilder2 header(String name, String value);

	RequestBuilder2 headers(Map<String, String> headers);

	RequestBuilder2 cookie(String name, String value);

	RequestBuilder2 cookies(Map<String, String> cookies);

	RequestBuilder2 charset(String charset);
	
	int numRequests();
	
	long elapsedTime();

	HttpResponse execute() throws IOException;

}
