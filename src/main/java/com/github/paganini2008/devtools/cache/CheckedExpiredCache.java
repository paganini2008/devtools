package com.github.paganini2008.devtools.cache;

import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * CheckedExpiredCache
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CheckedExpiredCache extends AbstractCache implements ExpiredCache {

	private final ExpiredCache delegate;
	private Timer timer;

	public CheckedExpiredCache(Cache delegate, int interval) {
		this.delegate = delegate instanceof ExpiredCache ? (ExpiredCache) delegate
				: new UncheckedExpiredCache(delegate);
		timer = ThreadUtils.scheduleAtFixedRate(() -> {
			refresh();
			return true;
		}, 1, TimeUnit.SECONDS);
	}

	public void refresh() {
		Set<Object> keys = delegate.keys();
		for (Object key : keys) {
			delegate.getObject(key);
		}
	}

	public void putObject(Object key, Object value) {
		delegate.putObject(key, value);
	}

	public Object getObject(Object key) {
		return delegate.getObject(key);
	}

	public Object removeObject(Object key) {
		return delegate.getObject(key);
	}

	public void clear() {
		delegate.clear();
	}

	public int getSize() {
		return delegate.getSize();
	}

	public Set<Object> keys() {
		return delegate.keys();
	}

	public void putObject(Object key, Object value, int expired) {
		delegate.putObject(key, value, expired);
	}

	public void setExpired(Object key, int expired) {
		delegate.setExpired(key, expired);
	}

	public boolean hasKey(Object key) {
		return delegate.hasKey(key);
	}

	public String toString() {
		return delegate.toString();
	}

	public void close() {
		delegate.close();
		timer.cancel();
	}

}
