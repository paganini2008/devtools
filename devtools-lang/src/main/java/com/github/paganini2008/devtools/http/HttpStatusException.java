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

import java.io.IOException;

/**
 * HttpStatusException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class HttpStatusException extends IOException {

	private static final long serialVersionUID = 2734229897177246457L;
	private int statusCode;
	private String url;

	public HttpStatusException(String message, int statusCode, String url) {
		super(message);
		this.statusCode = statusCode;
		this.url = url;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getUrl() {
		return url;
	}

	public String toString() {
		return super.toString() + ". Status=" + statusCode + ", URL=" + url;
	}

}
