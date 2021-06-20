/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface HttpRequest extends HttpBase<HttpRequest> {

	int maxBodySize();

	HttpRequest maxBodySize(int bytes);

	boolean validateTLSCertificates();

	HttpRequest validateTLSCertificates(boolean value);
	
	HttpRequest doOutput(boolean value);
	
	boolean doOutput();

	boolean followRedirects();

	HttpRequest followRedirects(boolean followRedirects);

	boolean retryRequestsIfError();

	HttpRequest retryRequestsIfError(boolean retryRequests);

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
