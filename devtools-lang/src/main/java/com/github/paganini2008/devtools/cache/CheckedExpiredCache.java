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
package com.github.paganini2008.devtools.cache;

import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * CheckedExpiredCache
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class CheckedExpiredCache extends AbstractCache implements ExpiredCache {

	private final ExpiredCache delegate;
	private Timer timer;

	public CheckedExpiredCache(int checkInterval, TimeUnit timeUnit) {
		this(new HashCache(), checkInterval, timeUnit);
	}

	public CheckedExpiredCache(Cache delegate, int checkInterval, TimeUnit timeUnit) {
		this.delegate = delegate instanceof UncheckedExpiredCache ? (UncheckedExpiredCache) delegate : new UncheckedExpiredCache(delegate);
		timer = ThreadUtils.scheduleAtFixedRate(() -> {
			refresh();
			return true;
		}, checkInterval, timeUnit);
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

	public void putObject(Object key, Object value, boolean ifAbsent) {
		putObject(key, value, ifAbsent, -1);
	}

	public void putObject(Object key, Object value, boolean ifAbsent, int expired) {
		delegate.putObject(key, value, ifAbsent, expired);
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
