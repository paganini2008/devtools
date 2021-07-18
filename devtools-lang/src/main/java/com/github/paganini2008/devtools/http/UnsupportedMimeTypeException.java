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
 * UnsupportedMimeTypeException
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class UnsupportedMimeTypeException extends IOException {

	private static final long serialVersionUID = 4853305326101523342L;
	private String mimeType;
	private String url;

	public UnsupportedMimeTypeException(String message, String mimeType, String url) {
		super(message);
		this.mimeType = mimeType;
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getUrl() {
		return url;
	}

	public String toString() {
		return super.toString() + ". Mimetype=" + mimeType + ", URL=" + url;
	}

}
