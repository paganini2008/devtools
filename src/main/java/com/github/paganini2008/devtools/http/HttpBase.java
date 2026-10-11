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

import java.net.URL;
import java.util.Map;

/**
 * 
 * HttpBase
 * <p>
 * Common part of http requests and responses: url, method, charset, headers
 * and cookies.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * 
 * @since 2.0.1
 */
public interface HttpBase<T extends HttpBase<T>> {

	static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * Returns the url.
	 */
	URL url();

	T url(URL url);

	String method();

	T method(String method);

	/**
	 * Sets the charset.
	 * @throws java.nio.charset.IllegalCharsetNameException if the charset is not supported
	 */
	T charset(String charset);

	/**
	 * Returns the charset, of the form data for a request or of the body for a response.
	 */
	String charset();

	/**
	 * Returns the header value (case insensitive name), or null.
	 */
	String header(String name);

	/**
	 * Sets a header, replacing the existing one.
	 */
	T header(String name, String value);

	boolean hasHeader(String name);

	/**
	 * Returns true if the header exists with the given value (case insensitive).
	 */
	boolean hasHeaderWithValue(String name, String value);

	T removeHeader(String name);

	Map<String, String> headers();

	/**
	 * Adds all the headers. Null is ignored.
	 */
	T headers(Map<String, String> headers);

	String cookie(String name);

	T cookie(String name, String value);

	/**
	 * Adds all the cookies. Null is ignored.
	 */
	T cookies(Map<String, String> cookies);

	boolean hasCookie(String name);

	T removeCookie(String name);

	Map<String, String> cookies();

	/**
	 * Returns the cookies formatted as the value of the 'Cookie' header.
	 */
	String cookie();

}
