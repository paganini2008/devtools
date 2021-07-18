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
package com.github.paganini2008.devtools.io;

import java.io.File;
import java.util.Properties;

import com.github.paganini2008.devtools.collection.RefreshingProperties;

/**
 * 
 * FileSystemProperties
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FileSystemProperties extends RefreshingProperties {

	private static final long serialVersionUID = -3729821807160075318L;

	public FileSystemProperties(File... files) {
		this.files = files;
	}

	private final File[] files;
	private String charset = "UTF-8";

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected Properties createObject() throws Exception {
		BasicProperties p = new BasicProperties();
		if (files != null && files.length > 0) {
			for (File file : files) {
				p.load(file, charset);
			}
		}
		return p;
	}

}
