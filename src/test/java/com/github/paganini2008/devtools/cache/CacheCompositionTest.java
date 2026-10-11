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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.cache.UncheckedExpiredCache.DefaultCachedObject;

/**
 * CacheCompositionTest, covers the decorated combinations (expiration, master
 * standby, cache store) and the null handling of the caches.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CacheCompositionTest {

	@TempDir
	File dir;

	private static DefaultCachedObject aged(Object value, int expiredSeconds, long ageMillis) {
		return new DefaultCachedObject(value, System.currentTimeMillis() - ageMillis, expiredSeconds);
	}

	// ------------------------------------------------- expiration + bounded caches

	@Test
	public void checkedRefresh_keepsTheLruOrder() {
		LruCache lru = new LruCache(3);
		CheckedExpiredCache cache = new CheckedExpiredCache(lru, 1, TimeUnit.HOURS);
		try {
			cache.putObject("a", 1);
			cache.putObject("b", 2);
			cache.putObject("c", 3);
			// 'a' becomes the most recently used, 'b' the eldest
			assertEquals(1, cache.getObject("a"));
			cache.refresh();
			cache.putObject("d", 4);
			assertEquals(new HashSet<Object>(Arrays.asList("a", "c", "d")), lru.keys());
		} finally {
			cache.close();
		}
	}

	@Test
	public void checkedRefresh_doesNotMoveEntriesBackFromTheStore_butPurgesExpiredOnes() {
		MemoryCacheStore store = new MemoryCacheStore();
		LruCache lru = new LruCache(1);
		lru.setCacheStore(store);
		CheckedExpiredCache cache = new CheckedExpiredCache(lru, 1, TimeUnit.HOURS);
		try {
			lru.putObject("old", aged(0, 1, 5000));
			cache.putObject("a", 1);
			cache.putObject("b", 2);
			// 'old' and 'a' were evicted into the store
			assertEquals(new HashSet<Object>(Arrays.asList("old", "a")), store.keys());
			cache.refresh();
			assertEquals(new HashSet<Object>(Arrays.asList("a")), store.keys());
			// 'b' is the only entry in memory, 'a' was not moved back
			assertEquals(2, cache.getSize());
			assertEquals(new HashSet<Object>(Arrays.asList("a", "b")), lru.keys());
			assertTrue(lru.toString().startsWith("{b="));
		} finally {
			cache.close();
		}
	}

	@Test
	public void checkedRefresh_keepsFifoAndSortedOrder() {
		MemoryCacheStore fifoStore = new MemoryCacheStore();
		FifoCache fifo = new FifoCache(2);
		fifo.setCacheStore(fifoStore);
		CheckedExpiredCache cache = new CheckedExpiredCache(fifo, 1, TimeUnit.HOURS);
		try {
			cache.putObject("a", 1);
			cache.putObject("b", 2);
			cache.putObject("c", 3);
			cache.refresh();
			// 'a' stays in the store, refresh must not reinsert it as the newest entry
			assertEquals(Arrays.asList("a"), new ArrayList<Object>(fifoStore.keys()));
			cache.putObject("d", 4);
			assertEquals(Arrays.asList("a", "b"), new ArrayList<Object>(fifoStore.keys()));
		} finally {
			cache.close();
		}

		SortedCache sorted = new SortedCache(2);
		MemoryCacheStore sortedStore = new MemoryCacheStore();
		sorted.setCacheStore(sortedStore);
		sorted.putObject("old", aged(0, 1, 5000));
		CheckedExpiredCache sortedExpiring = new CheckedExpiredCache(sorted, 1, TimeUnit.HOURS);
		try {
			sortedExpiring.putObject("x", 1);
			sortedExpiring.putObject("y", 2);
			// "old" (the smallest key) was evicted into the store
			assertTrue(sortedStore.hasKey("old"));
			sortedExpiring.refresh();
			assertFalse(sortedStore.hasKey("old"));
			assertEquals(new HashSet<Object>(Arrays.asList("x", "y")), sortedExpiring.keys());
		} finally {
			sortedExpiring.close();
		}
	}

	@Test
	public void peekObject_ignoresNullKeyAndReadsTheStore() {
		for (BoundedCache cache : new BoundedCache[] { new LruCache(1), new FifoCache(1), new SortedCache(1) }) {
			assertNull(cache.peekObject(null));
			assertNull(cache.peekObject("missing"));
			cache.setCacheStore(new MemoryCacheStore());
			cache.putObject("a", 1);
			cache.putObject("b", 2);
			assertEquals(1, cache.peekObject("a"));
			assertEquals(2, cache.peekObject("b"));
			assertNull(cache.peekObject("missing"));
		}
		// a custom bounded cache falls back to getObject
		BoundedCache custom = new BoundedCache() {
			final HashCache delegate = new HashCache();

			public void setCacheStore(CacheStore store) {
			}

			public void putObject(Object key, Object value, boolean ifAbsent) {
				delegate.putObject(key, value, ifAbsent);
			}

			public boolean hasKey(Object key) {
				return delegate.hasKey(key);
			}

			public Object getObject(Object key) {
				return delegate.getObject(key);
			}

			public Object removeObject(Object key) {
				return delegate.removeObject(key);
			}

			public java.util.Set<Object> keys() {
				return delegate.keys();
			}

			public void clear() {
				delegate.clear();
			}

			public int getSize() {
				return delegate.getSize();
			}
		};
		custom.putObject("k", "v");
		assertEquals("v", custom.peekObject("k"));
	}

	@Test
	public void expiredCache_overSerializableCacheStore() {
		SerializableCacheStore store = new SerializableCacheStore(10);
		store.setStoreDir(dir.getAbsolutePath());
		LruCache lru = new LruCache(1);
		lru.setCacheStore(store);
		ExpiredCache cache = lru.expiredCache();
		cache.putObject("a", "A", false, 60);
		// evicts the wrapped entry of 'a' to disk, which requires it to be serializable
		cache.putObject("b", "B", false, 60);
		assertEquals(1, store.getSize());
		assertEquals("A", cache.getObject("a"));

		// the creation time survives serialization, so expiration still works
		lru.putObject("old", aged("O", 1, 5000));
		cache.putObject("c", "C");
		assertTrue(store.hasKey("old"));
		assertNull(cache.getObject("old"));
		assertFalse(lru.hasKey("old"));
	}

	// ----------------------------------------------------------- expiration logic

	@Test
	public void putIfAbsent_replacesAnExpiredEntry() {
		HashCache delegate = new HashCache();
		UncheckedExpiredCache cache = new UncheckedExpiredCache(delegate);
		delegate.putObject("a", aged("old", 1, 5000));
		cache.putObject("a", "new", true);
		assertEquals("new", cache.getObject("a"));
		// a live entry is still kept
		cache.putObject("a", "newer", true, 60);
		assertEquals("new", cache.getObject("a"));
	}

	@Test
	public void hasKey_isFalseForAnExpiredEntry() {
		HashCache delegate = new HashCache();
		UncheckedExpiredCache cache = new UncheckedExpiredCache(delegate);
		delegate.putObject("a", aged(1, 1, 5000));
		delegate.putObject("b", aged(2, 60, 5000));
		assertFalse(cache.hasKey("a"));
		assertFalse(delegate.hasKey("a"));
		assertTrue(cache.hasKey("b"));
		assertFalse(cache.hasKey("missing"));
		assertFalse(cache.hasKey(null));
		assertFalse(cache.removeIfExpired(null));
	}

	@Test
	public void expiredCache_overLruCacheHasKeyBehavesLikeLruHasKey() {
		LruCache lru = new LruCache(2);
		ExpiredCache cache = lru.expiredCache();
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		// hasKey('a') through the expired cache checks the expiration with a peek
		assertTrue(cache.hasKey("a"));
		cache.putObject("c", 3);
		// LruCache.hasKey itself marks 'a' as used, so 'b' is the eldest
		assertEquals(new HashSet<Object>(Arrays.asList("a", "c")), lru.keys());
	}

	// ---------------------------------------------------------------- null handling

	@Test
	public void hashCache_ignoresNullKeysAndValuesOfConcurrentMap() {
		HashCache cache = new HashCache();
		cache.putObject(null, 1);
		cache.putObject("a", null);
		cache.putObject(null, 1, true);
		assertEquals(0, cache.getSize());
		assertNull(cache.getObject(null));
		assertFalse(cache.hasKey(null));
		assertNull(cache.removeObject(null));
		assertNull(cache.getObject(null, "default"));
		// decorated
		ExpiredCache expiring = new HashCache().expiredCache();
		expiring.putObject(null, 1);
		assertNull(expiring.getObject(null));
		assertFalse(expiring.hasKey(null));
		assertNull(expiring.removeObject(null));
		Cache standby = new HashCache().masterStandbyCache(new HashCache());
		standby.putObject(null, 1);
		standby.putObject(null, 1, true);
		assertNull(standby.getObject(null));
		assertFalse(standby.hasKey(null));
		assertNull(standby.removeObject(null));
	}

	@Test
	public void boundedCaches_ignoreNullKeysAndValues() {
		for (BoundedCache cache : new BoundedCache[] { new LruCache(2), new FifoCache(2), new SortedCache(2) }) {
			cache.putObject(null, 1);
			cache.putObject("a", null);
			assertEquals(0, cache.getSize());
			assertNull(cache.getObject(null));
			assertFalse(cache.hasKey(null));
			assertNull(cache.removeObject(null));
		}
	}

	// ---------------------------------------------------------------- master standby

	@Test
	public void masterStandby_putIfAbsentKeepsBothLevelsConsistent() {
		LruCache master = new LruCache(1);
		HashCache backup = new HashCache();
		Cache cache = new MasterStandbyCache(master, backup);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		assertFalse(master.hasKey("a"));
		// 'a' still exists in the backup, so nothing must change
		cache.putObject("a", 99, true);
		assertEquals(1, backup.getObject("a"));
		assertEquals(1, cache.getObject("a"));
		assertEquals(1, master.getObject("a"));
		// absent everywhere: put into both
		cache.putObject("c", 3, true);
		assertEquals(3, master.getObject("c"));
		assertEquals(3, backup.getObject("c"));
	}

	// ----------------------------------------------------------- serializable store

	@Test
	public void serializableStore_keysWithTheSameStringFormDoNotShareAFile() {
		SerializableCacheStore store = new SerializableCacheStore(10);
		store.setStoreDir(dir.getAbsolutePath());
		store.writeObject(1, "int");
		store.writeObject("1", "string");
		assertEquals("int", store.readObject(1));
		assertEquals("string", store.readObject("1"));
		assertEquals(2, dir.listFiles().length);
		// rewriting reuses the file of the key
		store.writeObject("1", "string2");
		assertEquals(2, dir.listFiles().length);
		assertEquals("string2", store.readObject("1"));
		// removing one key keeps the other
		assertEquals("int", store.removeObject(1));
		assertEquals("string2", store.readObject("1"));
		assertEquals(1, dir.listFiles().length);
		// a third key with the same string form
		store.writeObject(1L, "long");
		store.writeObject(1, "int again");
		assertEquals("long", store.readObject(1L));
		assertEquals("int again", store.readObject(1));
		assertEquals("string2", store.readObject("1"));
	}

	// ---------------------------------------- mutation gaps of the bounded caches

	@Test
	public void boundedCaches_putReplacesTheStaleCopyInTheStore_putIfAbsentKeepsIt() {
		for (BoundedCache cache : new BoundedCache[] { new LruCache(2), new FifoCache(2), new SortedCache(2) }) {
			String name = cache.getClass().getSimpleName();
			MemoryCacheStore store = new MemoryCacheStore();
			cache.setCacheStore(store);
			cache.putObject("a", 1);
			cache.putObject("b", 2);
			cache.putObject("c", 3);
			assertTrue(store.hasKey("a"), name);
			// putIfAbsent: 'a' exists (in the store), nothing changes
			cache.putObject("a", 99, true);
			assertTrue(store.hasKey("a"), name);
			assertEquals(3, cache.getSize(), name);
			// put: the stale copy in the store is dropped, not counted twice
			cache.putObject("a", 10);
			// (a sorted cache may evict the new 'a' right away, but never keeps the old value)
			assertTrue(!store.hasKey("a") || store.readObject("a").equals(10), name);
			assertEquals(3, cache.getSize(), name);
			assertEquals(10, cache.getObject("a"), name);
			// and not revived after removal
			assertEquals(10, cache.removeObject("a"), name);
			assertNull(cache.getObject("a"), name);
			assertFalse(cache.hasKey("a"), name);
		}
	}

	@Test
	@Timeout(5)
	public void checkedExpiredCache_closeStopsTheTimer() throws Exception {
		final java.util.concurrent.atomic.AtomicInteger refreshes = new java.util.concurrent.atomic.AtomicInteger();
		HashCache delegate = new HashCache() {
			@Override
			public java.util.Set<Object> keys() {
				refreshes.incrementAndGet();
				return super.keys();
			}
		};
		CheckedExpiredCache cache = new CheckedExpiredCache(delegate, 5, TimeUnit.MILLISECONDS);
		while (refreshes.get() < 2) {
			Thread.sleep(5);
		}
		cache.close();
		Thread.sleep(20);
		int afterClose = refreshes.get();
		Thread.sleep(60);
		assertEquals(afterClose, refreshes.get());
	}

	// --------------------------------------------------------------- concurrency

	@Test
	@Timeout(10)
	public void checkedExpiredLruCache_concurrentAccessWithRefresh() throws Exception {
		final int maxSize = 50;
		LruCache lru = new LruCache(maxSize);
		MemoryCacheStore store = new MemoryCacheStore();
		lru.setCacheStore(store);
		final CheckedExpiredCache cache = new CheckedExpiredCache(lru, 1, TimeUnit.HOURS);
		ExecutorService pool = Executors.newFixedThreadPool(8);
		try {
			final CountDownLatch start = new CountDownLatch(1);
			List<Future<?>> futures = new ArrayList<Future<?>>();
			for (int t = 0; t < 8; t++) {
				final int thread = t;
				futures.add(pool.submit(() -> {
					start.await();
					ThreadLocalRandom random = ThreadLocalRandom.current();
					for (int i = 0; i < 2000; i++) {
						Integer key = random.nextInt(200);
						switch (random.nextInt(5)) {
						case 0:
							cache.putObject(key, key, false, 60);
							break;
						case 1:
							cache.putObject(key, key, true);
							break;
						case 2:
							Object value = cache.getObject(key);
							if (value != null && !key.equals(value)) {
								throw new AssertionError(key + " -> " + value);
							}
							break;
						case 3:
							cache.removeObject(key);
							break;
						default:
							if (thread == 0) {
								cache.refresh();
							} else {
								cache.hasKey(key);
							}
						}
					}
					return null;
				}));
			}
			start.countDown();
			for (Future<?> future : futures) {
				future.get();
			}
			// memory is bounded, everything else is in the store, nothing is counted twice
			assertTrue(lru.getSize() - store.getSize() <= maxSize);
			assertEquals(lru.keys().size(), cache.getSize());
			for (Object key : lru.keys()) {
				assertEquals(key, cache.getObject(key));
			}
		} finally {
			pool.shutdownNow();
			cache.close();
		}
	}

}
