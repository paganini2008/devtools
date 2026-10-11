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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;

/**
 * HttpResponse
 * <p>
 * The response of an http request.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface HttpResponse extends HttpBase<HttpResponse> {

	/**
	 * Returns the status code.
	 */
	int statusCode();

	String statusMessage();

	String contentType();

	/**
	 * Returns the body decoded by the charset of the content type (UTF-8 by default), or null if
	 * not filled.
	 */
	String body();

	/**
	 * Returns the raw body.
	 */
	byte[] bytes();

	/**
	 * Returns the number of bytes of the body.
	 */
	long length();

	/**
	 * Returns the response before the redirection, or null.
	 */
	HttpResponse previous();

	/**
	 * Returns the number of redirections followed by the client.
	 */
	int numRedirects();
	
	/**
	 * Returns the time used in milliseconds.
	 */
	long elapsedTime();
	
	void elapsedTime(long time);

	/**
	 * Writes the body decoded by the given charset.
	 */
	void saveAs(Writer writer, String charset) throws IOException;

	/**
	 * Writes the raw body.
	 */
	void saveAs(OutputStream os) throws IOException;

	/**
	 * Writes the raw body into the file.
	 */
	default void saveAs(File f) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = FileUtils.openOutputStream(f);
			saveAs(fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}
}
