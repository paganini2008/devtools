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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * {@link Resource} whose content is (re)loaded with {@link #refresh()}. Before the first refresh every getter returns its
 * default value.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class RefreshingResource implements Resource, Serializable {

	private static final long serialVersionUID = 6728764457749444189L;
	private volatile Map<String, String> config;

	/**
	 * Loads the current version of the entries.
	 */
	protected abstract Map<String, String> getConfig() throws IOException;

	/**
	 * Called by {@link #refresh()} when the entries have changed.
	 */
	protected void onChange(Map<String, String> latest, Map<String, String> current) {
	}

	/**
	 * Reloads the entries.
	 *
	 * @return {@code true} if the entries have changed since the last refresh
	 */
	public synchronized boolean refresh() throws Exception {
		boolean hasChanged = false;
		Map<String, String> latest = getConfig();
		if (config != null) {
			final Map<String, String> current = new HashMap<String, String>(config);
			hasChanged = !MapUtils.deepEquals(latest, current);
			if (hasChanged) {
				onChange(latest, current);
			}
		}
		this.config = latest;
		return hasChanged;
	}

	/**
	 * Writes freshly loaded entries in properties format to the file.
	 */
	public void store(File file, String charset) throws IOException {
		try (OutputStream output = FileUtils.openOutputStream(file)) {
			store(output, charset);
		}
	}

	/**
	 * Writes freshly loaded entries in properties format (ISO-8859-1 with unicode escapes) to the stream.
	 */
	public void store(OutputStream os, String charset) throws IOException {
		Properties p = MapUtils.toProperties(getConfig());
		p.store(os, "Created on " + DateUtils.format(System.currentTimeMillis()));
	}

	public String getString(String name) {
		return getString(name, null);
	}

	public String getString(String name, String defaultValue) {
		return getValue(name, String.class, defaultValue);
	}

	public Byte getByte(String name) {
		return getByte(name, null);
	}

	public Byte getByte(String name, Byte defaultValue) {
		return getValue(name, Byte.class, defaultValue);
	}

	public Short getShort(String name) {
		return getShort(name, null);
	}

	public Short getShort(String name, Short defaultValue) {
		return getValue(name, Short.class, defaultValue);
	}

	public Integer getInteger(String name) {
		return getInteger(name, null);
	}

	public Integer getInteger(String name, Integer defaultValue) {
		return getValue(name, Integer.class, defaultValue);
	}

	public Long getLong(String name) {
		return getLong(name, null);
	}

	public Long getLong(String name, Long defaultValue) {
		return getValue(name, Long.class, defaultValue);
	}

	public Float getFloat(String name) {
		return getFloat(name, null);
	}

	public Float getFloat(String name, Float defaultValue) {
		return getValue(name, Float.class, defaultValue);
	}

	public Double getDouble(String name) {
		return getDouble(name, null);
	}

	public Double getDouble(String name, Double defaultValue) {
		return getValue(name, Double.class, defaultValue);
	}

	public Boolean getBoolean(String name) {
		return getBoolean(name, null);
	}

	public Boolean getBoolean(String name, Boolean defaultValue) {
		return getValue(name, Boolean.class, defaultValue);
	}

	private <T> T getValue(String name, Class<T> requiredType, T defaultValue) {
		Map<String, String> m = config;
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

	public Map<String, String> toMap() {
		return config != null ? new HashMap<String, String>(config) : null;
	}

	public Map<String, String> toMap(String substr, MatchMode mode) {
		Map<String, String> m = toMap();
		return m != null ? MapUtils.toMatchedKeyMap(m, substr, mode) : null;
	}

	/**
	 * Creates a view refreshing this resource every {@code interval} seconds once started.
	 */
	public RealtimeRefreshingResource refresh(int interval) {
		return new RealtimeRefreshingResource(this, interval);
	}

	public String toString() {
		return config != null ? config.toString() : "";
	}

}
