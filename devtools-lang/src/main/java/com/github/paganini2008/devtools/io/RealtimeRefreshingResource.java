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

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.ShutdownHook;
import com.github.paganini2008.devtools.ShutdownHooks;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RealtimeRefreshingResource
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class RealtimeRefreshingResource implements Resource, Executable {

	private final RefreshingResource delegate;
	private final int interval;
	private Timer timer;

	RealtimeRefreshingResource(RefreshingResource delegate, int interval) {
		this.delegate = delegate;
		this.interval = interval;
		ShutdownHooks.addHook(new ShutdownHook() {
			protected void process() {
				close();
			}
		});
	}

	public boolean execute() {
		try {
			delegate.refresh();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void start() {
		if (timer == null) {
			timer = ThreadUtils.scheduleAtFixedRate(this, interval, TimeUnit.SECONDS);
		}
	}

	public String getString(String name) {
		return delegate.getString(name);
	}

	public String getString(String name, String defaultValue) {
		return delegate.getString(name, defaultValue);
	}

	public Byte getByte(String name) {
		return delegate.getByte(name);
	}

	public Byte getByte(String name, Byte defaultValue) {
		return delegate.getByte(name, defaultValue);
	}

	public Short getShort(String name) {
		return delegate.getShort(name);
	}

	public Short getShort(String name, Short defaultValue) {
		return delegate.getShort(name, defaultValue);
	}

	public Integer getInteger(String name) {
		return delegate.getInteger(name);
	}

	public Integer getInteger(String name, Integer defaultValue) {
		return delegate.getInteger(name, defaultValue);
	}

	public Long getLong(String name) {
		return delegate.getLong(name);
	}

	public Long getLong(String name, Long defaultValue) {
		return delegate.getLong(name, defaultValue);
	}

	public Float getFloat(String name) {
		return delegate.getFloat(name);
	}

	public Float getFloat(String name, Float defaultValue) {
		return delegate.getFloat(name, defaultValue);
	}

	public Double getDouble(String name) {
		return delegate.getDouble(name);
	}

	public Double getDouble(String name, Double defaultValue) {
		return delegate.getDouble(name, defaultValue);
	}

	public Boolean getBoolean(String name) {
		return delegate.getBoolean(name);
	}

	public Boolean getBoolean(String name, Boolean defaultValue) {
		return delegate.getBoolean(name, defaultValue);
	}

	public Map<String, String> toMap() {
		return delegate.toMap();
	}

	public Map<String, String> toMap(String substr, MatchMode mode) {
		return delegate.toMap(substr, mode);
	}

	public String toString() {
		return delegate.toString();
	}

	public void close() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
