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
package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.util.Properties;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Loads and caches properties files from the class path.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class PropertiesUtils {

	private final static LruMap<String, Properties> cache = new LruMap<String, Properties>(128);
	private static String charset = "UTF-8";

	/**
	 * Sets the charset used to read properties files (UTF-8 by default).
	 */
	public static void setCharset(String charset) {
		PropertiesUtils.charset = charset;
	}

	public static Properties loadProperties(String name) throws IOException {
		return loadProperties(name, PropertiesUtils.class.getClassLoader());
	}

	/**
	 * Loads (without caching) all class path resources with the name, earlier class path entries overriding later ones.
	 */
	public static Properties loadProperties(String name, ClassLoader loader) throws IOException {
		BasicProperties p = new BasicProperties();
		p.load(name, charset, loader);
		return p;
	}

	public static Properties getProperties(String name, String substr, MatchMode matchMode) throws IOException {
		return getProperties(name, PropertiesUtils.class.getClassLoader(), substr, matchMode);
	}

	/**
	 * Returns the cached properties whose keys match {@code substr} with the match mode.
	 */
	public static Properties getProperties(String name, ClassLoader loader, String substr, MatchMode matchMode) throws IOException {
		Properties p = getProperties(name, loader);
		return ((BasicProperties) p).filter(substr, matchMode);
	}

	public static Properties getProperties(String name, String prefix) throws IOException {
		return getProperties(name, PropertiesUtils.class.getClassLoader(), prefix);
	}

	/**
	 * Returns the cached properties whose keys start with {@code prefix + "."}, with that prefix removed from the keys.
	 */
	public static Properties getProperties(String name, ClassLoader loader, String prefix) throws IOException {
		Properties p = getProperties(name, loader);
		return ((BasicProperties) p).filter(prefix);
	}

	public static Properties getProperties(String name) throws IOException {
		return getProperties(name, PropertiesUtils.class.getClassLoader());
	}

	/**
	 * Returns the properties loaded from the class path resources with the name, cached per name and class loader. The
	 * returned instance is shared and must not be modified.
	 */
	public static Properties getProperties(String name, ClassLoader loader) throws IOException {
		final String key = loader != null ? name + "@" + System.identityHashCode(loader) : name;
		Properties config = cache.get(key);
		if (config == null) {
			config = loadProperties(name, loader);
			cache.put(key, config);
		}
		return config;
	}

}
