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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;

/**
 * HttpResponse
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface HttpResponse extends HttpBase<HttpResponse> {

	int statusCode();

	String statusMessage();

	String contentType();

	String body();

	byte[] bytes();

	long length();

	HttpResponse previous();

	int numRedirects();
	
	long elapsedTime();
	
	void elapsedTime(long time);

	void saveAs(Writer writer, String charset) throws IOException;

	void saveAs(OutputStream os) throws IOException;

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
