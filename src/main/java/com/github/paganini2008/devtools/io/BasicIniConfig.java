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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.MultiMappedMap;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * In-memory {@link IniConfig} that loads INI style files (sections, {@code key=value} pairs and {@code #} comments) from
 * the file system or the class path.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class BasicIniConfig implements IniConfig, Serializable {

	private static final long serialVersionUID = 7872169560893030827L;
	private final MultiMappedMap<String, String, String> config;

	public BasicIniConfig() {
		this(new MultiMappedMap<String, String, String>(
				(Supplier<Map<String, String>> & Serializable) () -> new ConcurrentHashMap<String, String>()));
	}

	protected BasicIniConfig(MultiMappedMap<String, String, String> config) {
		this.config = config;
	}

	/**
	 * Loads all class path resources with the name (using the system class loader if {@code loader} is {@code null}); values
	 * from resources earlier on the class path override later ones.
	 */
	public void loadFromClassPath(String name, String charset, ClassLoader loader) throws IOException {
		Enumeration<URL> urls;
		if (loader == null) {
			urls = ClassLoader.getSystemResources(name);
		} else {
			urls = loader.getResources(name);
		}
		List<URL> urlList = ListUtils.reverse(urls);
		for (URL url : urlList) {
			try (BufferedReader br = IOUtils.getBufferedReader(url.openStream(), charset)) {
				parse(br);
			}
		}
	}

	/**
	 * Loads the INI file; existing values with the same keys are overridden.
	 *
	 * @throws java.io.FileNotFoundException if the file does not exist
	 */
	public void load(File file, String charset) throws IOException {
		try (BufferedReader br = FileUtils.getBufferedReader(file, charset)) {
			parse(br);
		}
	}

	private void parse(BufferedReader br) throws IOException {
		String line;
		String section = null;
		int index;
		while (null != (line = br.readLine())) {
			line = line.trim();
			if (StringUtils.isBlank(line)) {
				continue;
			}
			index = line.indexOf("#");
			if (index == 0) {
				continue;
			} else if (index > 0) {
				line = line.substring(0, index).trim();
			}
			if (line.startsWith("[") && line.endsWith("]") && line.length() > 2) {
				section = line.substring(1, line.length() - 1);
			} else if (StringUtils.isNotBlank(section)) {
				index = line.indexOf("=");
				if (index > 0) {
					config.put(section, line.substring(0, index).trim(), line.substring(index + 1).trim());
				}
			}
		}
	}

	public String[] sections() {
		return config.keySet().toArray(new String[0]);
	}

	public Map<String, String> get(String section) {
		Map<String, String> m = section != null ? config.get(section) : null;
		return m != null ? new HashMap<String, String>(m) : new HashMap<String, String>();
	}

	public Map<String, String> get(String section, String substr, MatchMode mode) {
		return MapUtils.toMatchedKeyMap(get(section), substr, mode);
	}

	public String set(String section, String name, String value) {
		return config.put(section, name, value);
	}

	public Map<String, String> set(String section, Map<String, String> kwargs) {
		return config.put(section, kwargs);
	}

	public String getString(String section, String name) {
		return getString(section, name, null);
	}

	public String getString(String section, String name, String defaultValue) {
		return getValue(section, name, String.class, defaultValue);
	}

	public Byte getByte(String section, String name) {
		return getByte(section, name, null);
	}

	public Byte getByte(String section, String name, Byte defaultValue) {
		return getValue(section, name, Byte.class, defaultValue);
	}

	public Short getShort(String section, String name) {
		return getShort(section, name, null);
	}

	public Short getShort(String section, String name, Short defaultValue) {
		return getValue(section, name, Short.class, defaultValue);
	}

	public Integer getInteger(String section, String name) {
		return getInteger(section, name, null);
	}

	public Integer getInteger(String section, String name, Integer defaultValue) {
		return getValue(section, name, Integer.class, defaultValue);
	}

	public Long getLong(String section, String name) {
		return getLong(section, name, null);
	}

	public Long getLong(String section, String name, Long defaultValue) {
		return getValue(section, name, Long.class, defaultValue);
	}

	public Float getFloat(String section, String name) {
		return getFloat(section, name, null);
	}

	public Float getFloat(String section, String name, Float defaultValue) {
		return getValue(section, name, Float.class, defaultValue);
	}

	public Double getDouble(String section, String name) {
		return getDouble(section, name, null);
	}

	public Double getDouble(String section, String name, Double defaultValue) {
		return getValue(section, name, Double.class, defaultValue);
	}

	public Boolean getBoolean(String section, String name) {
		return getBoolean(section, name, null);
	}

	public Boolean getBoolean(String section, String name, Boolean defaultValue) {
		return getValue(section, name, Boolean.class, defaultValue);
	}

	private <T> T getValue(String section, String name, Class<T> requiredType, T defaultValue) {
		Map<String, String> m = section != null ? config.get(section) : null;
		String value = m != null && name != null ? m.get(name) : null;
		if (value == null) {
			return defaultValue;
		}
		if (requiredType.isInstance(value)) {
			return requiredType.cast(value);
		}
		T result = ConvertUtils.convertValue(value, requiredType, defaultValue);
		return result != null ? result : defaultValue;
	}

	public Map<String, Map<String, String>> toMap() {
		return new LinkedHashMap<String, Map<String, String>>(config);
	}

	public Iterator<String> iterator() {
		return config.keySet().iterator();
	}

	public String toString() {
		return config.toString();
	}

	/**
	 * Writes the configuration in INI format to the file.
	 */
	public void store(File file, String charset) throws IOException {
		try (OutputStream output = FileUtils.openOutputStream(file)) {
			store(output, charset);
		}
	}

	public void store(OutputStream os, String charset) throws IOException {
		BufferedWriter bw = IOUtils.getBufferedWriter(os, charset);
		for (String section : sections()) {
			Map<String, String> inners = config.get(section);
			if (inners == null) {
				continue;
			}
			bw.write("[" + section + "]");
			bw.newLine();
			for (Map.Entry<String, String> inner : inners.entrySet()) {
				bw.write(inner.getKey() + "=" + inner.getValue());
				bw.newLine();
			}
		}
		bw.flush();
	}

}
