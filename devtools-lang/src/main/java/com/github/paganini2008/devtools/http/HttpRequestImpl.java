/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.io.FileUtils;

/**
 * 
 * HttpRequestImpl
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class HttpRequestImpl extends HttpBaseImpl<HttpRequest> implements HttpRequest {

	private static final long serialVersionUID = -1294872498012012776L;
	private int connectTimeoutMillis;
	private int readTimeoutMillis;
	private boolean followRedirects;
	private boolean doOutput;
	private List<NameValuePair> data;
	private boolean ignoreHttpErrors = false;
	private boolean ignoreContentType = false;
	private boolean validateTSLCertificates = false;
	private boolean retryRequestsIfError = false;
	private String baseUrl;
	private int maxBodySizeBytes;
	private SocketAddress proxy;
	private int maxRequests;
	private int maxRedirects;

	HttpRequestImpl() {
		connectTimeoutMillis = 60 * 1000;
		readTimeoutMillis = 60 * 1000;
		maxBodySizeBytes = 1 * 1024 * 1024;
		followRedirects = false;
		data = new ArrayList<NameValuePair>();
		headers.put("Accept-Encoding", "gzip");
		maxRequests = 3;
		maxRedirects = 10;
	}

	public int maxRedirects() {
		return maxRedirects;
	}

	public HttpRequest maxRedirects(int redirects) {
		this.maxRedirects = redirects;
		return this;
	}

	public int maxRequests() {
		return maxRequests;
	}

	public HttpRequest maxRequests(int requests) {
		this.maxRequests = requests;
		return this;
	}

	public boolean validateTLSCertificates() {
		return validateTSLCertificates;
	}

	public HttpRequest validateTLSCertificates(boolean value) {
		validateTSLCertificates = value;
		return this;
	}

	public boolean followRedirects() {
		return followRedirects;
	}

	public HttpRequest followRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public HttpRequest doOutput(boolean value) {
		this.doOutput = value;
		return this;
	}

	public boolean doOutput() {
		return doOutput;
	}

	public int readTimeout() {
		return readTimeoutMillis;
	}

	public HttpRequest readTimeout(int millis) {
		Assert.isFalse(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
		this.readTimeoutMillis = millis;
		return this;
	}

	public int connectTimeout() {
		return connectTimeoutMillis;
	}

	public HttpRequest connectTimeout(int millis) {
		Assert.isFalse(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
		this.connectTimeoutMillis = millis;
		return this;
	}

	public boolean ignoreHttpErrors() {
		return ignoreHttpErrors;
	}

	public HttpRequest ignoreHttpErrors(boolean ignoreHttpErrors) {
		this.ignoreHttpErrors = ignoreHttpErrors;
		return this;
	}

	public boolean ignoreContentType() {
		return ignoreContentType;
	}

	public HttpRequest ignoreContentType(boolean ignoreContentType) {
		this.ignoreContentType = ignoreContentType;
		return this;
	}

	public HttpRequest data(Map<String, String> data) {
		if (data != null) {
			for (Map.Entry<String, String> entry : data.entrySet()) {
				data(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public HttpRequest data(String name, String value) {
		return data(new BasicNameValuePair().name(name).value(value));
	}

	public HttpRequest data(String name, String value, File file) {
		try {
			return data(name, value, FileUtils.openInputStream(file));
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public HttpRequest data(String name, String value, InputStream inputStream) {
		return data(new BasicNameValuePair().name(name).value(value).inputStream(inputStream));
	}

	public HttpRequest data(Collection<NameValuePair> data) {
		if (data != null) {
			for (NameValuePair entry : data) {
				data(entry);
			}
		}
		return this;
	}

	public HttpRequest data(NameValuePair nameValue) {
		Assert.isNull(nameValue, "NameValue must not be null.");
		data.add(nameValue);
		return this;
	}

	public List<NameValuePair> data() {
		return data;
	}

	public int maxBodySize() {
		return maxBodySizeBytes;
	}

	public HttpRequest maxBodySize(int bytes) {
		this.maxBodySizeBytes = bytes;
		return this;
	}

	public String baseUrl() {
		return baseUrl;
	}

	public HttpRequest baseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public SocketAddress proxy() {
		return proxy;
	}

	public HttpRequest proxy(String hostname, int port) {
		this.proxy = new InetSocketAddress(hostname, port);
		return this;
	}

	public boolean retryRequestsIfError() {
		return retryRequestsIfError;
	}

	public HttpRequest retryRequestsIfError(boolean retryRequests) {
		this.retryRequestsIfError = retryRequests;
		return this;
	}

}
