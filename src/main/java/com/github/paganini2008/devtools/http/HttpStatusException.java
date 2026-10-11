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

import java.io.IOException;

/**
 * HttpStatusException
 * <p>
 * Thrown when the response status is an error (not in [200, 400)) and the
 * errors are not ignored.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
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

	/**
	 * Returns the status code of the response.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Returns the requested url.
	 */
	public String getUrl() {
		return url;
	}

	public String toString() {
		return super.toString() + ". Status=" + statusCode + ", URL=" + url;
	}

}
