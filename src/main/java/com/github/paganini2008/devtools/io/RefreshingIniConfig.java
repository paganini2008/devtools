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
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * {@link IniConfig} whose content can be reloaded with {@link #refresh()}; the content is loaded lazily on first access.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class RefreshingIniConfig implements IniConfig, Serializable {

	private static final long serialVersionUID = -5059524272830465517L;
	private volatile IniConfig delegate;

	/**
	 * Loads the current version of the configuration.
	 */
	protected abstract IniConfig getConfig() throws Exception;

	public Map<String, String> set(String section, Map<String, String> kwargs) {
		return getDelegate().set(section, kwargs);
	}

	public String set(String section, String name, String value) {
		return getDelegate().set(section, name, value);
	}

	public String[] sections() {
		return getDelegate().sections();
	}

	public Map<String, String> get(String section) {
		return getDelegate().get(section);
	}

	public Map<String, String> get(String section, String substr, MatchMode mode) {
		return getDelegate().get(section, substr, mode);
	}

	public String getString(String section, String name) {
		return getString(section, name, null);
	}

	public String getString(String section, String name, String defaultValue) {
		return getDelegate().getString(section, name, defaultValue);
	}

	public Byte getByte(String section, String name) {
		return getByte(section, name, null);
	}

	public Byte getByte(String section, String name, Byte defaultValue) {
		return getDelegate().getByte(section, name, defaultValue);
	}

	public Short getShort(String section, String name) {
		return getShort(section, name, null);
	}

	public Short getShort(String section, String name, Short defaultValue) {
		return getDelegate().getShort(section, name, defaultValue);
	}

	public Integer getInteger(String section, String name) {
		return getInteger(section, name, null);
	}

	public Integer getInteger(String section, String name, Integer defaultValue) {
		return getDelegate().getInteger(section, name, defaultValue);
	}

	public Long getLong(String section, String name) {
		return getLong(section, name, null);
	}

	public Long getLong(String section, String name, Long defaultValue) {
		return getDelegate().getLong(section, name, defaultValue);
	}

	public Float getFloat(String section, String name) {
		return getFloat(section, name, null);
	}

	public Float getFloat(String section, String name, Float defaultValue) {
		return getDelegate().getFloat(section, name, defaultValue);
	}

	public Double getDouble(String section, String name) {
		return getDouble(section, name, null);
	}

	public Double getDouble(String section, String name, Double defaultValue) {
		return getDelegate().getDouble(section, name, defaultValue);
	}

	public Boolean getBoolean(String section, String name) {
		return getBoolean(section, name, null);
	}

	public Boolean getBoolean(String section, String name, Boolean defaultValue) {
		return getDelegate().getBoolean(section, name, defaultValue);
	}

	private IniConfig getDelegate() {
		IniConfig current = delegate;
		if (current == null) {
			synchronized (this) {
				if (delegate == null) {
					try {
						refresh();
					} catch (Exception e) {
						throw new ConfigError("Failed to load config: " + e.getMessage(), e);
					}
				}
				current = delegate;
			}
		}
		return current;
	}

	/**
	 * Reloads the configuration and calls {@link #onChange(String, Map, Map)} for every new or changed section.
	 *
	 * @return {@code true} if a section was added or changed
	 */
	public synchronized boolean refresh() throws Exception {
		boolean hasChanged = false;
		IniConfig latestVersion = getConfig();
		if (delegate != null) {
			final Map<String, Map<String, String>> currentVersion = delegate.toMap();
			Map<String, String> latest, current;
			for (String section : latestVersion) {
				latest = latestVersion.get(section);
				if (currentVersion.containsKey(section)) {
					current = currentVersion.get(section);
					if (!MapUtils.deepEquals(latest, current)) {
						onChange(section, latest, current);
						hasChanged = true;
					}
				} else {
					onChange(section, latest, null);
					hasChanged = true;
				}
			}

		}
		this.delegate = latestVersion;
		return hasChanged;
	}

	/**
	 * Called by {@link #refresh()} for a changed section; {@code current} is {@code null} for a new section.
	 */
	protected void onChange(String section, Map<String, String> latest, Map<String, String> current) {
	}

	public Iterator<String> iterator() {
		return getDelegate().iterator();
	}

	public Map<String, Map<String, String>> toMap() {
		return getDelegate().toMap();
	}

	public String toString() {
		return getDelegate().toString();
	}

	public void store(OutputStream os, String charset) throws IOException {
		getDelegate().store(os, charset);
	}

	/**
	 * Creates a view refreshing this configuration every {@code interval} seconds once started.
	 */
	public RealtimeRefreshingIniConfig refresh(int interval) {
		return new RealtimeRefreshingIniConfig(this, interval);
	}

}
