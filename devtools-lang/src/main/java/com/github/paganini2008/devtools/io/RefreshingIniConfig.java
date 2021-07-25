/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
 * RefreshingIniConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class RefreshingIniConfig implements IniConfig, Serializable {

	private static final long serialVersionUID = -5059524272830465517L;
	private IniConfig delegate;

	protected abstract IniConfig getConfig() throws Exception;

	public Map<String, String> set(String section, Map<String, String> kwargs) {
		return delegate.set(section, kwargs);
	}

	public String set(String section, String name, String value) {
		return delegate.set(section, name, value);
	}

	public String[] sections() {
		return delegate.sections();
	}

	public Map<String, String> get(String section) {
		return delegate.get(section);
	}

	public Map<String, String> get(String section, String substr, MatchMode mode) {
		return delegate.get(section, substr, mode);
	}

	public String getString(String section, String name) {
		return getString(section, name, null);
	}

	public String getString(String section, String name, String defaultValue) {
		return delegate.getString(section, name, defaultValue);
	}

	public Byte getByte(String section, String name) {
		return getByte(section, name, null);
	}

	public Byte getByte(String section, String name, Byte defaultValue) {
		return delegate.getByte(section, name, defaultValue);
	}

	public Short getShort(String section, String name) {
		return getShort(section, name, null);
	}

	public Short getShort(String section, String name, Short defaultValue) {
		return delegate.getShort(section, name, defaultValue);
	}

	public Integer getInteger(String section, String name) {
		return getInteger(section, name, null);
	}

	public Integer getInteger(String section, String name, Integer defaultValue) {
		return delegate.getInteger(section, name, defaultValue);
	}

	public Long getLong(String section, String name) {
		return getLong(section, name, null);
	}

	public Long getLong(String section, String name, Long defaultValue) {
		return delegate.getLong(section, name, defaultValue);
	}

	public Float getFloat(String section, String name) {
		return getFloat(section, name, null);
	}

	public Float getFloat(String section, String name, Float defaultValue) {
		return delegate.getFloat(section, name, defaultValue);
	}

	public Double getDouble(String section, String name) {
		return getDouble(section, name, null);
	}

	public Double getDouble(String section, String name, Double defaultValue) {
		return delegate.getDouble(section, name, defaultValue);
	}

	public Boolean getBoolean(String section, String name) {
		return getBoolean(section, name, null);
	}

	public Boolean getBoolean(String section, String name, Boolean defaultValue) {
		return delegate.getBoolean(section, name, defaultValue);
	}

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

	protected void onChange(String section, Map<String, String> latest, Map<String, String> current) {
	}

	public Iterator<String> iterator() {
		return delegate.iterator();
	}

	public Map<String, Map<String, String>> toMap() {
		return delegate.toMap();
	}

	public String toString() {
		return delegate.toString();
	}

	public void store(OutputStream os, String charset) throws IOException {
		delegate.store(os, charset);
	}

	public RealtimeRefreshingIniConfig refresh(int interval) {
		return new RealtimeRefreshingIniConfig(this, interval);
	}

}
