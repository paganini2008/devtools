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

/**
 * 
 * HttpMethod
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public enum HttpMethod {

	GET(false, true), 
	POST(true, false), 
	HEAD(false, true), 
	PUT(true, false), 
	DELETE(false, false), 
	OPTIONS(true, false), 
	TRACE(true, false), 
	PATCH(true, false);

	private final boolean doOutput;
	private final boolean followRedirects;

	private HttpMethod(boolean doOutput, boolean followRedirects) {
		this.doOutput = doOutput;
		this.followRedirects = followRedirects;
	}

	public boolean doOutput() {
		return doOutput;
	}

	public boolean followRedirects() {
		return followRedirects;
	}
}
