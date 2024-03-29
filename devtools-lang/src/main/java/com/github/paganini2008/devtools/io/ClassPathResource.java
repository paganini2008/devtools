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

import java.io.IOException;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ClassPathResource
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class ClassPathResource extends RefreshingResource {

	private static final long serialVersionUID = -4336490159044412458L;
	private final String[] names;
	private final ClassLoader loader;
	private String charset = "UTF-8";

	public ClassPathResource(String... names) {
		this(names, ClassPathResource.class.getClassLoader());
	}

	public ClassPathResource(String[] names, ClassLoader loader) {
		this.names = names;
		this.loader = loader;
	}

	protected Map<String, String> getConfig() throws IOException {
		BasicProperties p = new BasicProperties();
		if (names != null && names.length > 0) {
			for (String name : names) {
				p.load(name, charset, loader);
			}
		}
		return MapUtils.toMap(p);
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
