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

import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.CaseInsensitiveMap;

/**
 * 
 * HttpBaseImpl
 * 
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
@SuppressWarnings("unchecked")
public abstract class HttpBaseImpl<T extends HttpBase<T>> implements HttpBase<T>, Serializable {

	private static final long serialVersionUID = 6403400581804405562L;
	protected URL url;
	protected String method;
	protected String charset = DEFAULT_CHARSET;
	protected Map<String, String> headers;
	protected Map<String, String> cookies;

	protected HttpBaseImpl() {
		headers = new CaseInsensitiveMap<String>();
		cookies = new LinkedHashMap<String, String>();
	}

	public URL url() {
		return url;
	}

	public T url(URL url) {
		Assert.isNull(url, "URL must not be null.");
		this.url = url;
		return (T) this;
	}

	public String method() {
		return method;
	}

	public T method(String method) {
		Assert.hasNoText(method, "Http method name must not be empty.");
		this.method = method;
		return (T) this;
	}

	public T charset(String charset) {
		Assert.hasNoText(charset, "Charset must not be null.");
		if (!Charset.isSupported(charset)) {
			throw new IllegalCharsetNameException(charset);
		}
		this.charset = charset;
		return (T) this;
	}

	public String charset() {
		return charset;
	}

	public String header(String name) {
		Assert.isNull(name, "Header name must not be null.");
		return headers.get(name);
	}

	public T header(String name, String value) {
		Assert.hasNoText(name, "Header name must not be empty.");
		Assert.isNull(value, "Header value must not be null.");
		headers.put(name, value);
		return (T) this;
	}

	public T headers(Map<String, String> headers) {
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				this.headers.put(entry.getKey(), entry.getValue());
			}
		}
		return (T) this;
	}

	public T cookies(Map<String, String> cookies) {
		if (cookies != null) {
			for (Map.Entry<String, String> entry : cookies.entrySet()) {
				this.cookies.put(entry.getKey(), entry.getValue());
			}
		}
		return (T) this;
	}

	public boolean hasHeader(String name) {
		Assert.hasNoText(name, "Header name must not be empty.");
		return headers.containsKey(name);
	}

	public boolean hasHeaderWithValue(String name, String value) {
		return hasHeader(name) && header(name).equalsIgnoreCase(value);
	}

	public T removeHeader(String name) {
		Assert.hasNoText(name, "Header name must not be empty.");
		headers.remove(name);
		return (T) this;
	}

	public Map<String, String> headers() {
		return headers;
	}

	public String cookie(String name) {
		Assert.hasNoText(name, "Cookie name must not be empty.");
		return cookies.get(name);
	}

	public T cookie(String name, String value) {
		Assert.hasNoText(name, "Cookie name must not be empty.");
		Assert.isNull(value, "Cookie value must not be null.");
		cookies.put(name, value);
		return (T) this;
	}

	public boolean hasCookie(String name) {
		Assert.hasNoText(name, "Cookie name must not be empty.");
		return cookies.containsKey(name);
	}

	public T removeCookie(String name) {
		Assert.hasNoText(name, "Cookie name must not be empty.");
		cookies.remove(name);
		return (T) this;
	}

	public Map<String, String> cookies() {
		return cookies;
	}

	public String cookie() {
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> cookie : cookies().entrySet()) {
			if (!first) {
				str.append("; ");
			} else {
				first = false;
			}
			str.append(cookie.getKey()).append('=').append(cookie.getValue());
		}
		return str.toString();
	}
}
