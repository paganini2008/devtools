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
package com.github.paganini2008.devtools.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.cache.UncheckedExpiredCache.DefaultCachedObject;

/**
 * ExpiredCacheTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ExpiredCacheTest {

	/**
	 * Puts an entry which was created the given milliseconds ago, so that no
	 * sleep is needed to test the expiration
	 */
	private static void putAged(Cache delegate, Object key, Object value, int expiredSeconds, long ageMillis) {
		delegate.putObject(key, new DefaultCachedObject(value, System.currentTimeMillis() - ageMillis, expiredSeconds));
	}

	@Test
	public void freshEntry_isNotExpired() {
		UncheckedExpiredCache cache = new UncheckedExpiredCache(new HashCache());
		cache.putObject("a", 1, false, 60);
		cache.putObject("b", 2);
		assertEquals(1, cache.getObject("a"));
		assertEquals(2, cache.getObject("b"));
		assertEquals(2, cache.getSize());
		assertTrue(cache.hasKey("a"));
		assertEquals(new HashSet<Object>(Arrays.asList("a", "b")), cache.keys());
	}

	@Test
	public void agedEntry_isExpiredAndRemovedOnRead() {
		HashCache delegate = new HashCache();
		UncheckedExpiredCache cache = new UncheckedExpiredCache(delegate);
		putAged(delegate, "old", 1, 1, 1500);
		putAged(delegate, "young", 2, 1, 500);
		putAged(delegate, "forever", 3, -1, 100000);
		putAged(delegate, "zero", 4, 0, 100000);
		assertNull(cache.getObject("old"));
		assertFalse(delegate.hasKey("old"));
		assertEquals(2, cache.getObject("young"));
		assertEquals(3, cache.getObject("forever"));
		assertEquals(4, cache.getObject("zero"));
	}

	@Test
	public void isExpired_boundary() {
		UncheckedExpiredCache cache = new UncheckedExpiredCache(new HashCache());
		long now = System.currentTimeMillis();
		assertTrue(cache.isExpired(new DefaultCachedObject("x", now - 1000, 1)));
		assertTrue(cache.isExpired(new DefaultCachedObject("x", now - 5000, 1)));
		assertFalse(cache.isExpired(new DefaultCachedObject("x", now + 1000, 1)));
		assertFalse(cache.isExpired(new DefaultCachedObject("x", now - 5000, 0)));
		assertFalse(cache.isExpired(new DefaultCachedObject("x", now - 5000, -1)));
	}

	@Test
	public void setExpired_changesTtlOfLiveEntryOnly() {
		HashCache delegate = new HashCache();
		UncheckedExpiredCache cache = new UncheckedExpiredCache(delegate);
		putAged(delegate, "a", 1, -1, 5000);
		cache.setExpired("a", 1);
		assertNull(cache.getObject("a"));

		putAged(delegate, "b", 2, 1, 5000);
		// already expired entry is not revived
		cache.setExpired("b", 100);
		assertNull(cache.getObject("b"));

		cache.putObject("c", 3, false, 1);
		cache.setExpired("c", 100);
		DefaultCachedObject object = (DefaultCachedObject) delegate.getObject("c");
		assertEquals(100, object.getExpired());
		object.setExpired(5);
		assertEquals(5, object.getExpired());
		assertEquals(3, object.getValue());
		assertTrue(object.getCreated() > 0);

		// missing key is ignored
		cache.setExpired("missing", 1);
	}

	@Test
	public void putIfAbsent_removeAndClear() {
		UncheckedExpiredCache cache = new UncheckedExpiredCache(new HashCache());
		cache.putObject("a", 1, true);
		cache.putObject("a", 2, true);
		assertEquals(1, cache.getObject("a"));
		assertEquals(1, cache.removeObject("a"));
		assertNull(cache.removeObject("a"));
		cache.putObject("b", null);
		assertTrue(cache.hasKey("b"));
		assertNull(cache.getObject("b"));
		assertTrue(cache.toString().contains("b"));
		cache.clear();
		assertEquals(0, cache.getSize());
	}

	@Test
	public void close_closesDelegate() {
		AtomicBoolean closed = new AtomicBoolean();
		UncheckedExpiredCache cache = new UncheckedExpiredCache(new HashCache() {
			@Override
			public void close() {
				closed.set(true);
			}
		});
		cache.close();
		assertTrue(closed.get());
	}

	@Test
	public void decorateWithAbstractCacheFactories() {
		HashCache hashCache = new HashCache();
		ExpiredCache unchecked = hashCache.expiredCache();
		assertTrue(unchecked instanceof UncheckedExpiredCache);
		assertThrows(UnsupportedOperationException.class, () -> ((AbstractCache) unchecked).expiredCache());

		ExpiredCache checked = ((AbstractCache) unchecked).expiredCache(1, TimeUnit.HOURS);
		try {
			assertTrue(checked instanceof CheckedExpiredCache);
			assertThrows(UnsupportedOperationException.class, () -> ((AbstractCache) checked).expiredCache(1, TimeUnit.SECONDS));
			assertThrows(UnsupportedOperationException.class, () -> ((AbstractCache) checked).expiredCache());
			checked.putObject("a", 1);
			// shares the same underlying storage
			assertTrue(hashCache.hasKey("a"));
		} finally {
			checked.close();
		}
	}

	@Test
	@Timeout(5)
	public void checkedExpiredCache_purgesExpiredEntriesInBackground() throws Exception {
		HashCache delegate = new HashCache();
		CheckedExpiredCache cache = new CheckedExpiredCache(delegate, 20, TimeUnit.MILLISECONDS);
		try {
			putAged(delegate, "old", 1, 1, 5000);
			cache.putObject("fresh", 2, false, 60);
			while (delegate.hasKey("old")) {
				Thread.sleep(10);
			}
			assertEquals(1, cache.getSize());
			assertEquals(2, cache.getObject("fresh"));
		} finally {
			cache.close();
		}
	}

	@Test
	public void checkedExpiredCache_operations() {
		CheckedExpiredCache cache = new CheckedExpiredCache(1, TimeUnit.HOURS);
		try {
			cache.putObject("a", 1);
			cache.putObject("b", 2, true);
			cache.putObject("b", 3, true);
			cache.putObject("c", 3, false, 60);
			assertEquals(1, cache.getObject("a"));
			assertEquals(2, cache.getObject("b"));
			assertTrue(cache.hasKey("c"));
			assertEquals(3, cache.getSize());
			assertEquals(new HashSet<Object>(Arrays.asList("a", "b", "c")), cache.keys());
			// removeObject really removes the entry
			assertEquals(1, cache.removeObject("a"));
			assertFalse(cache.hasKey("a"));
			assertNull(cache.getObject("a"));
			cache.setExpired("b", 1);
			assertTrue(cache.toString().contains("b"));
			cache.refresh();
			assertEquals(2, cache.getSize());
			cache.clear();
			assertEquals(0, cache.getSize());
		} finally {
			cache.close();
		}
	}

	@Test
	public void checkedExpiredCache_reusesUncheckedDelegate() {
		HashCache hashCache = new HashCache();
		UncheckedExpiredCache unchecked = new UncheckedExpiredCache(hashCache);
		CheckedExpiredCache cache = new CheckedExpiredCache(unchecked, 1, TimeUnit.HOURS);
		try {
			cache.putObject("a", 1);
			assertSame(DefaultCachedObject.class, hashCache.getObject("a").getClass());
			assertEquals(1, unchecked.getObject("a"));
		} finally {
			cache.close();
		}
	}

	@Test
	public void refresh_removesAllExpiredEntries() {
		HashCache delegate = new HashCache(new java.util.HashMap<Object, Object>());
		CheckedExpiredCache cache = new CheckedExpiredCache(delegate, 1, TimeUnit.HOURS);
		try {
			for (int i = 0; i < 100; i++) {
				putAged(delegate, i, i, 1, i % 2 == 0 ? 5000 : 0);
			}
			// must not throw ConcurrentModificationException on a plain HashMap
			cache.refresh();
			assertEquals(50, cache.getSize());
		} finally {
			cache.close();
		}
	}

}
