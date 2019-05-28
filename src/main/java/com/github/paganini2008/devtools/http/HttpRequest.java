package com.github.paganini2008.devtools.http;

import java.io.InputStream;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * HttpRequest
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface HttpRequest extends HttpBase<HttpRequest> {

	int maxBodySize();

	HttpRequest maxBodySize(int bytes);

	boolean validateTLSCertificates();

	HttpRequest validateTLSCertificates(boolean value);

	boolean followRedirects();

	HttpRequest followRedirects(boolean followRedirects);

	boolean retryRequests();

	HttpRequest retryRequests(boolean retryRequests);

	int readTimeout();

	HttpRequest readTimeout(int millis);

	int connectTimeout();

	HttpRequest connectTimeout(int millis);

	boolean ignoreHttpErrors();

	HttpRequest ignoreHttpErrors(boolean ignoreHttpErrors);

	boolean ignoreContentType();

	int maxRedirects();

	String baseUrl();

	HttpRequest baseUrl(String baseUrl);

	HttpRequest maxRedirects(int redirects);

	HttpRequest ignoreContentType(boolean ignoreContentType);

	HttpRequest data(Map<String, String> data);

	HttpRequest data(String name, String value);

	HttpRequest data(String name, String value, InputStream inputStream);

	HttpRequest data(Collection<NameValuePair> data);

	HttpRequest data(NameValuePair nameValue);

	List<NameValuePair> data();

	SocketAddress proxy();

	HttpRequest proxy(String hostname, int port);

	int maxRequests();

	HttpRequest maxRequests(int requests);

}
