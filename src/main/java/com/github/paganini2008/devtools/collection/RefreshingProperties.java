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
package com.github.paganini2008.devtools.collection;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.io.IOUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RefreshingProperties
 * <p>
 * Read-only properties which are reloaded from {@link #createObject()} on
 * {@link #refresh()}, either manually or periodically via
 * {@link #setInterval(long, TimeUnit)}. Before the first refresh it behaves as
 * empty properties.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class RefreshingProperties extends ReadonlyProperties implements Executable {

	private static final long serialVersionUID = 1L;

	protected volatile Properties delegate = new Properties();

	private boolean loaded;

	/**
	 * Loads the latest version of the properties.
	 *
	 * @return the latest properties
	 * @throws Exception if loading fails
	 */
	protected abstract Properties createObject() throws Exception;

	/**
	 * Reloads the properties.
	 *
	 * @return true if the content has changed since the previous refresh (always
	 *         false for the first one)
	 * @throws Exception if loading fails
	 */
	public synchronized boolean refresh() throws Exception {
		boolean hasChanged = false;
		Properties lastest = createObject();
		Properties current = null;
		if (lastest == null) {
			lastest = new Properties();
		}
		if (loaded && delegate != null) {
			current = (Properties) delegate.clone();
			hasChanged = !MapUtils.deepEquals(lastest, current);
		}
		this.delegate = lastest;
		this.loaded = true;
		if (hasChanged) {
			onChange(lastest, current);
		}
		return hasChanged;
	}

	/**
	 * Invoked when a refresh detects a change. Does nothing by default.
	 *
	 * @param lastest
	 *            the newly loaded properties
	 * @param current
	 *            the properties before the refresh
	 */
	protected void onChange(Properties lastest, Properties current) {
	}

	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public Object get(Object key) {
		return delegate.get(key);
	}

	public Object getOrDefault(Object key, Object defaultValue) {
		return delegate.getOrDefault(key, defaultValue);
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public boolean contains(Object value) {
		return delegate.contains(value);
	}

	public Enumeration<Object> keys() {
		return delegate.keys();
	}

	public Enumeration<Object> elements() {
		return delegate.elements();
	}

	public Collection<Object> values() {
		return delegate.values();
	}

	public Set<Object> keySet() {
		return delegate.keySet();
	}

	public Set<Map.Entry<Object, Object>> entrySet() {
		return delegate.entrySet();
	}

	/**
	 * Writes the current properties into the file, overwriting it.
	 */
	public void store(File outputFile, String comments) throws IOException {
		OutputStream out = null;
		try {
			out = FileUtils.openOutputStream(outputFile, false);
			delegate.store(out, comments);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public void store(Writer writer, String comments) throws IOException {
		delegate.store(writer, comments);
	}

	public void store(OutputStream out, String comments) throws IOException {
		delegate.store(out, comments);
	}

	public void storeToXML(OutputStream os, String comment) throws IOException {
		delegate.storeToXML(os, comment);
	}

	public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
		delegate.storeToXML(os, comment, encoding);
	}

	public String getProperty(String key) {
		return delegate.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return delegate.getProperty(key, defaultValue);
	}

	public Enumeration<?> propertyNames() {
		return delegate.propertyNames();
	}

	public Set<String> stringPropertyNames() {
		return delegate.stringPropertyNames();
	}

	public void list(PrintStream out) {
		delegate.list(out);
	}

	public void list(PrintWriter out) {
		delegate.list(out);
	}

	public int size() {
		return delegate.size();
	}

	public String toString() {
		return delegate.toString();
	}

	/**
	 * Returns a snapshot of the current properties as a map.
	 */
	public Map<String, String> toMap() {
		return MapUtils.toMap(delegate);
	}

	/**
	 * Returns a snapshot of the current properties whose names match the substring.
	 */
	public Map<String, String> toMap(String substr, MatchMode mode) {
		Map<String, String> m = toMap();
		return MapUtils.toMatchedKeyMap(m, substr, mode);
	}

	/**
	 * Refreshes the properties, used by the periodic timer.
	 *
	 * @return true if the refresh succeeded
	 */
	public boolean execute() {
		try {
			refresh();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Timer timer;

	/**
	 * Refreshes now and then periodically. Does nothing if a timer is already running.
	 *
	 * @throws Exception if the first refresh fails
	 */
	public synchronized void setInterval(long interval, TimeUnit timeUnit) throws Exception {
		if (timer == null) {
			refresh();
			timer = ThreadUtils.scheduleAtFixedRate(this, interval, timeUnit);
		}
	}

	/**
	 * Stops the periodic refresh.
	 */
	public synchronized void clearInterval() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
