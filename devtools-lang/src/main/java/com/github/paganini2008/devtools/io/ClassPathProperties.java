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

import java.util.Properties;

import com.github.paganini2008.devtools.collection.RefreshingProperties;

/**
 * 
 * ClassPathProperties
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class ClassPathProperties extends RefreshingProperties {

	private static final long serialVersionUID = 1038119041409409706L;
	
	private final String[] names;
	private final ClassLoader loader;

	public ClassPathProperties(String... names) {
		this(names, ClassPathProperties.class.getClassLoader());
	}

	public ClassPathProperties(String[] names, ClassLoader loader) {
		this.names = names;
		this.loader = loader;
	}

	private String charset = "UTF-8";

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected Properties createObject() throws Exception {
		BasicProperties p = new BasicProperties();
		if (names != null && names.length > 0) {
			for (String name : names) {
				p.load(name, charset, loader);
			}
		}
		return p;
	}

}
