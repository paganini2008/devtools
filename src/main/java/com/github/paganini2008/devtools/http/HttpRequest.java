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

import java.io.InputStream;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * HttpRequest
 * <p>
 * An http request with its connection settings and form data.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * 
 * @since 2.0.1
 */
public interface HttpRequest extends HttpBase<HttpRequest> {

	/**
	 * Returns the maximum number of bytes of the response body to read (1M by default), -1 means no
	 * limit.
	 */
	int maxBodySize();

	HttpRequest maxBodySize(int bytes);

	/**
	 * Returns whether the TLS certificates and host names are validated (true by default). If
	 * false, any certificate is trusted.
	 */
	boolean validateTLSCertificates();

	HttpRequest validateTLSCertificates(boolean value);
	
	/**
	 * Sets whether the data is sent in the request body (true) or the url query (false).
	 */
	HttpRequest doOutput(boolean value);
	
	boolean doOutput();

	/**
	 * Returns whether the redirections are followed by the connection itself. If false, the client
	 * follows them with GET.
	 */
	boolean followRedirects();

	HttpRequest followRedirects(boolean followRedirects);

	/**
	 * Returns whether the request is resent on an error status, at most {@link #maxRequests()}
	 * times in total.
	 */
	boolean retryRequestsIfError();

	HttpRequest retryRequestsIfError(boolean retryRequests);

	/**
	 * Returns the read timeout in milliseconds, 0 means infinite.
	 */
	int readTimeout();

	/**
	 * Sets the read timeout in milliseconds.
	 * @throws IllegalArgumentException if negative
	 */
	HttpRequest readTimeout(int millis);

	int connectTimeout();

	/**
	 * Sets the connect timeout in milliseconds.
	 * @throws IllegalArgumentException if negative
	 */
	HttpRequest connectTimeout(int millis);

	/**
	 * Returns whether an error status is returned as a response instead of throwing {@link
	 * HttpStatusException}.
	 */
	boolean ignoreHttpErrors();

	HttpRequest ignoreHttpErrors(boolean ignoreHttpErrors);

	/**
	 * Returns whether a non-text content type is accepted instead of throwing {@link
	 * UnsupportedMimeTypeException}.
	 */
	boolean ignoreContentType();

	/**
	 * Returns the maximum number of redirections followed by the client (10 by default).
	 */
	int maxRedirects();

	/**
	 * Returns the url prefix redirections must start with, null means any.
	 */
	String baseUrl();

	HttpRequest baseUrl(String baseUrl);

	HttpRequest maxRedirects(int redirects);

	HttpRequest ignoreContentType(boolean ignoreContentType);

	HttpRequest data(Map<String, String> data);

	HttpRequest data(String name, String value);

	/**
	 * Adds a file to upload (multipart), the value is the file name. The stream is closed after
	 * sending.
	 */
	HttpRequest data(String name, String value, InputStream inputStream);

	HttpRequest data(Collection<NameValuePair> data);

	HttpRequest data(NameValuePair nameValue);

	/**
	 * Returns the form data.
	 */
	List<NameValuePair> data();

	/**
	 * Returns the http proxy, or null.
	 */
	SocketAddress proxy();

	HttpRequest proxy(String hostname, int port);

	/**
	 * Returns the maximum number of requests sent when retrying on errors (3 by default).
	 */
	int maxRequests();

	HttpRequest maxRequests(int requests);

	interface Builder {

		Builder userAgent(String userAgent);

		Builder timeout(int millis);

		Builder connectTimeout(int millis);

		Builder readTimeout(int millis);

		Builder maxBodySize(int bytes);

		Builder referer(String referrer);

		Builder maxRedirects(int redirects);
		
		Builder retryRequestsIfError(boolean retryRequests);

		Builder ignoreHttpErrors(boolean ignoreHttpErrors);

		Builder ignoreContentType(boolean ignoreContentType);

		Builder validateTLSCertificates(boolean value);

		Builder data(String key, String value);

		Builder data(String key, String filename, InputStream inputStream);

		Builder data(Map<String, String> data);

		Builder data(String... nameValues);

		Builder header(String name, String value);

		Builder headers(Map<String, String> headers);

		Builder cookie(String name, String value);

		Builder cookies(Map<String, String> cookies);

		Builder charset(String charset);

		HttpRequest build();
	}

}
