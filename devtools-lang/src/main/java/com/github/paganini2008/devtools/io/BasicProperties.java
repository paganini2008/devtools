/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * BasicProperties
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class BasicProperties extends Properties {

	private static final long serialVersionUID = 4564916474223591678L;

	public void load(File file, String charset) throws IOException {
		Reader reader = null;
		try {
			reader = FileUtils.getBufferedReader(file, charset);
			load(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public void load(String name, String charset) throws IOException {
		load(name, charset, null);
	}

	public void load(String name, String charset, ClassLoader loader) throws IOException {
		Enumeration<URL> urls;
		if (loader == null) {
			urls = ClassLoader.getSystemResources(name);
		} else {
			urls = loader.getResources(name);
		}
		List<URL> urlList = ListUtils.reverse(urls);
		Reader reader;
		for (URL url : urlList) {
			reader = IOUtils.getBufferedReader(url.openStream(), charset);
			try {
				load(reader);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	public BasicProperties filter(String prefix) {
		BasicProperties dest = new BasicProperties();
		MapUtils.copyProperties(this, prefix, dest);
		return dest;
	}

	public BasicProperties filter(String substr, MatchMode mode) {
		BasicProperties dest = new BasicProperties();
		MapUtils.copyProperties(this, substr, mode, dest);
		return dest;
	}

	public Map<String, String> toMap() {
		return MapUtils.toMap(this);
	}

	public Map<String, String> toMap(String prefix) {
		return MapUtils.toMap(this, prefix);
	}

	public Map<String, String> toMap(String substr, MatchMode mode) {
		return MapUtils.toMap(this, substr, mode);
	}

}
