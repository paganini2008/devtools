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
package com.github.paganini2008.devtools.io;

import java.io.IOException;

/**
 * 
 * ClassPathIniConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ClassPathIniConfig extends RefreshingIniConfig {

	private static final long serialVersionUID = -4691878287460797044L;
	private final String[] names;
	private final ClassLoader loader;
	private String charset = "UTF-8";

	public ClassPathIniConfig(String... names) {
		this(names, ClassPathIniConfig.class.getClassLoader());
	}

	public ClassPathIniConfig(String[] names, ClassLoader loader) {
		this.names = names;
		this.loader = loader;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected IniConfig getConfig() throws IOException {
		BasicIniConfig config = new BasicIniConfig();
		if (names != null && names.length > 0) {
			for (String name : names) {
				config.loadFromClassPath(name, charset, loader);
			}
		}
		return config;
	}

}
