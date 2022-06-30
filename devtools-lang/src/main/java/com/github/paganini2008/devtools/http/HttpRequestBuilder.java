/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
import java.net.URL;
import java.util.Map;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.http.HttpRequest.Builder;
import com.github.paganini2008.devtools.net.Urls;

/**
 * 
 * HttpRequestBuilder
 * 
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
public class HttpRequestBuilder implements Builder {

	protected final HttpRequest request;

	public HttpRequestBuilder(String method, String url) {
		this(method, Urls.toURL(url));
	}

	public HttpRequestBuilder(String method, URL url) {
		request = new HttpRequestImpl();
		request.method(method);
		request.url(url);
	}

	public Builder userAgent(String userAgent) {
		Assert.isNull(userAgent, "User agent must not be null");
		request.header("User-Agent", userAgent);
		return this;
	}

	public Builder maxRedirects(int redirects) {
		request.maxRedirects(redirects);
		return this;
	}

	public Builder timeout(int millis) {
		request.connectTimeout(millis);
		request.readTimeout(millis);
		return this;
	}

	public Builder connectTimeout(int millis) {
		request.connectTimeout(millis);
		return this;
	}

	public Builder readTimeout(int millis) {
		request.readTimeout(millis);
		return this;
	}

	public Builder maxBodySize(int bytes) {
		request.maxBodySize(bytes);
		return this;
	}

	public Builder referer(String referer) {
		Assert.isNull(referer, "Referer must not be null");
		request.header("Referer", referer);
		return this;
	}

	public Builder ignoreHttpErrors(boolean ignoreHttpErrors) {
		request.ignoreHttpErrors(ignoreHttpErrors);
		return this;
	}

	public Builder ignoreContentType(boolean ignoreContentType) {
		request.ignoreContentType(ignoreContentType);
		return this;
	}

	public Builder validateTLSCertificates(boolean value) {
		request.validateTLSCertificates(value);
		return this;
	}

	public Builder retryRequestsIfError(boolean retryRequests) {
		request.retryRequestsIfError(retryRequests);
		return this;
	}

	public Builder data(String key, String value) {
		request.data(key, value);
		return this;
	}

	public Builder data(String key, String filename, InputStream inputStream) {
		request.data(key, filename, inputStream);
		return this;
	}

	public Builder data(Map<String, String> data) {
		request.data(data);
		return this;
	}

	public Builder data(String... nameValues) {
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

	public Builder headers(Map<String, String> headers) {
		request.headers(headers);
		return this;
	}

	public Builder header(String name, String value) {
		request.header(name, value);
		return this;
	}

	public Builder cookie(String name, String value) {
		request.cookie(name, value);
		return this;
	}

	public Builder cookies(Map<String, String> cookies) {
		request.cookies(cookies);
		return this;
	}

	public Builder charset(String charset) {
		request.charset(charset);
		return this;
	}

	public HttpRequest build() {
		return request;
	}

}
