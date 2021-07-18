/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.collection.ReadonlyProperties;
import com.github.paganini2008.devtools.collection.RefreshingProperties;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RealtimeRefreshingProperties
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class RealtimeRefreshingProperties extends ReadonlyProperties implements Executable {

	private static final long serialVersionUID = 4232794532546686597L;

	private final RefreshingProperties delegate;
	private final int interval;

	RealtimeRefreshingProperties(RefreshingProperties delegate, int interval) {
		this.delegate = delegate;
		this.interval = interval;
	}

	private Timer timer;

	public synchronized void start() {
		if (timer == null) {
			timer = ThreadUtils.scheduleAtFixedRate(this, interval, TimeUnit.SECONDS);
		}
	}

	public synchronized void close() {
		if (timer != null) {
			timer.cancel();
		}
	}

	public boolean execute() {
		try {
			delegate.refresh();
			return true;
		} catch (Exception e) {
			return false;
		}
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

}
