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
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class RefreshingProperties extends ReadonlyProperties implements Executable {

	private static final long serialVersionUID = 1L;

	protected Properties delegate;

	protected abstract Properties createObject() throws Exception;

	public synchronized boolean refresh() throws Exception {
		boolean hasChanged = false;
		Properties lastest = createObject();
		Properties current = null;
		if (delegate != null) {
			current = (Properties) delegate.clone();
			hasChanged = !MapUtils.deepEquals(lastest, current);
		}
		this.delegate = lastest;
		if (hasChanged) {
			onChange(lastest, current);
		}
		return hasChanged;
	}

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

	public Collection<Object> values() {
		return delegate.values();
	}

	public Set<Object> keySet() {
		return delegate.keySet();
	}

	public Set<Map.Entry<Object, Object>> entrySet() {
		return delegate.entrySet();
	}

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

	public Map<String, String> toMap() {
		return MapUtils.toMap(delegate);
	}

	public Map<String, String> toMap(String substr, MatchMode mode) {
		Map<String, String> m = toMap();
		return MapUtils.toMatchedKeyMap(m, substr, mode);
	}

	public boolean execute() {
		try {
			refresh();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Timer timer;

	public void setInterval(long interval, TimeUnit timeUnit) throws Exception {
		if (timer == null) {
			refresh();
			timer = ThreadUtils.scheduleAtFixedRate(this, interval, timeUnit);
		}
	}

	public void clearInterval() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
