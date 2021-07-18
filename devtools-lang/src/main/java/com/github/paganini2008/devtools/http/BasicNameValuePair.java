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

import java.io.InputStream;

/**
 * BasicNameValuePair
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BasicNameValuePair implements NameValuePair {

	private String name;
	private String value;
	private InputStream stream;
	
	public BasicNameValuePair() {
	}

	public BasicNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public BasicNameValuePair(String name, String value, InputStream stream) {
		this.name = name;
		this.value = value;
		this.stream = stream;
	}

	public NameValuePair name(String name) {
		this.name = name;
		return this;
	}

	public String name() {
		return name;
	}

	public NameValuePair value(String value) {
		this.value = value;
		return this;
	}

	public String value() {
		return value;
	}

	public NameValuePair inputStream(InputStream inputStream) {
		this.stream = inputStream;
		return this;
	}

	public InputStream inputStream() {
		return stream;
	}

	public boolean hasInputStream() {
		return stream != null;
	}

}
