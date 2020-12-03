package com.github.paganini2008.devtools.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RealtimeRefreshingIniConfig
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeRefreshingIniConfig implements IniConfig, Executable, Serializable {

	private static final long serialVersionUID = 3805117699529531174L;
	protected final RefreshingIniConfig delegate;
	private final int interval;
	private Timer timer;

	protected RealtimeRefreshingIniConfig(RefreshingIniConfig delegate, int interval) {
		this.delegate = delegate;
		this.interval = interval;
	}

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

	public Iterator<String> iterator() {
		return delegate.iterator();
	}

	public Map<String, String> set(String section, Map<String, String> kwargs) {
		return delegate.set(section, kwargs);
	}

	public String set(String section, String name, String value) {
		return delegate.set(section, name, value);
	}

	public void store(OutputStream os, String charset) throws IOException {
		delegate.store(os, charset);
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
		return delegate.getString(section, name);
	}

	public String getString(String section, String name, String defaultValue) {
		return delegate.getString(section, name, defaultValue);
	}

	public Byte getByte(String section, String name) {
		return delegate.getByte(section, name);
	}

	public Byte getByte(String section, String name, Byte defaultValue) {
		return delegate.getByte(section, name, defaultValue);
	}

	public Short getShort(String section, String name) {
		return delegate.getShort(section, name);
	}

	public Short getShort(String section, String name, Short defaultValue) {
		return delegate.getShort(section, name, defaultValue);
	}

	public Integer getInteger(String section, String name) {
		return delegate.getInteger(section, name);
	}

	public Integer getInteger(String section, String name, Integer defaultValue) {
		return delegate.getInteger(section, name, defaultValue);
	}

	public Long getLong(String section, String name) {
		return delegate.getLong(section, name);
	}

	public Long getLong(String section, String name, Long defaultValue) {
		return delegate.getLong(section, name, defaultValue);
	}

	public Float getFloat(String section, String name) {
		return delegate.getFloat(section, name);
	}

	public Float getFloat(String section, String name, Float defaultValue) {
		return delegate.getFloat(section, name, defaultValue);
	}

	public Double getDouble(String section, String name) {
		return delegate.getDouble(section, name);
	}

	public Double getDouble(String section, String name, Double defaultValue) {
		return delegate.getDouble(section, name, defaultValue);
	}

	public Boolean getBoolean(String section, String name) {
		return delegate.getBoolean(section, name);
	}

	public Boolean getBoolean(String section, String name, Boolean defaultValue) {
		return delegate.getBoolean(section, name, defaultValue);
	}

	public Map<String, Map<String, String>> toMap() {
		return delegate.toMap();
	}

}
