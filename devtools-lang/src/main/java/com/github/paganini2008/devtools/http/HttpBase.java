/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
 * 
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
public interface HttpBase<T extends HttpBase<T>> {

	static final String DEFAULT_CHARSET = "UTF-8";

	URL url();

	T url(URL url);

	String method();

	T method(String method);

	T charset(String charset);

	String charset();

	String header(String name);

	T header(String name, String value);

	boolean hasHeader(String name);

	boolean hasHeaderWithValue(String name, String value);

	T removeHeader(String name);

	Map<String, String> headers();

	T headers(Map<String, String> headers);

	String cookie(String name);

	T cookie(String name, String value);

	T cookies(Map<String, String> cookies);

	boolean hasCookie(String name);

	T removeCookie(String name);

	Map<String, String> cookies();

	String cookie();

}
