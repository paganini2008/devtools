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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * CacheConcurrencyTest, concurrent read/write stress tests
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CacheConcurrencyTest {

	private static final int THREADS = 8;
	private static final int OPERATIONS = 5000;
	private static final int KEYS = 300;

	/**
	 * Runs random put/get/remove from many threads started at the same time, and
	 * checks that every value read belongs to its key
	 */
	private static void stress(Cache cache, boolean remove) throws Exception {
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(THREADS);
		final List<Throwable> errors = new CopyOnWriteArrayList<Throwable>();
		for (int t = 0; t < THREADS; t++) {
			Thread thread = new Thread(() -> {
				try {
					start.await();
					ThreadLocalRandom random = ThreadLocalRandom.current();
					for (int i = 0; i < OPERATIONS; i++) {
						int key = random.nextInt(KEYS);
						int op = random.nextInt(10);
						if (op < 5) {
							cache.putObject(key, "v" + key);
						} else if (op < 9 || !remove) {
							Object value = cache.getObject(key);
							if (value != null && !value.equals("v" + key)) {
								throw new AssertionError("Inconsistent value: " + key + "=" + value);
							}
						} else {
							cache.removeObject(key);
						}
					}
				} catch (Throwable e) {
					errors.add(e);
				} finally {
					done.countDown();
				}
			});
			thread.start();
		}
		start.countDown();
		assertTrue(done.await(20, TimeUnit.SECONDS));
		assertTrue(errors.isEmpty(), errors.toString());
	}

	private static void assertConsistent(Cache cache, int maxSize) {
		assertTrue(cache.getSize() <= maxSize, "size: " + cache.getSize());
		for (Object key : cache.keys()) {
			Object value = cache.getObject(key);
			if (value != null) {
				assertEquals("v" + key, value);
			}
		}
	}

	private static void stressBounded(Supplier<BoundedCache> supplier, int maxSize) throws Exception {
		BoundedCache cache = supplier.get();
		stress(cache, true);
		assertConsistent(cache, maxSize);
		assertEquals(cache.keys().size(), cache.getSize());
		// the eviction bookkeeping must still match the entries: filling the cache with
		// maxSize new keys must evict every old entry (a stale or phantom key in the
		// order tracker would leave old entries behind or evict a new one)
		Set<Object> fresh = new HashSet<Object>();
		for (int i = 0; i < maxSize; i++) {
			cache.putObject(KEYS + i, "v" + (KEYS + i));
			fresh.add(KEYS + i);
		}
		assertEquals(fresh, cache.keys());
		assertEquals(maxSize, cache.getSize());
	}

	@Test
	@Timeout(30)
	public void lruCache_neverExceedsCapacity() throws Exception {
		stressBounded(() -> new LruCache(50), 50);
	}

	@Test
	@Timeout(30)
	public void fifoCache_neverExceedsCapacity() throws Exception {
		stressBounded(() -> new FifoCache(50), 50);
	}

	@Test
	@Timeout(30)
	public void sortedCache_neverExceedsCapacity() throws Exception {
		// no remove: see the known issue of ConcurrentSortedBoundedMap.remove
		SortedCache cache = new SortedCache(50);
		stress(cache, false);
		assertConsistent(cache, 50);
	}

	@Test
	@Timeout(30)
	public void lruCacheWithStore_keepsAllEntries() throws Exception {
		LruCache cache = new LruCache(50);
		MemoryCacheStore store = new MemoryCacheStore();
		cache.setCacheStore(store);
		stress(cache, false);
		// nothing is lost: every key ever put is either in memory or in the store
		assertConsistent(cache, KEYS);
		for (Object key : cache.keys()) {
			assertEquals("v" + key, cache.getObject(key));
		}
	}

	@Test
	@Timeout(30)
	public void hashAndExpiredCache_concurrentAccess() throws Exception {
		HashCache hashCache = new HashCache();
		stress(hashCache, true);
		assertConsistent(hashCache, KEYS);

		ExpiredCache expiredCache = new HashCache().expiredCache();
		stress(expiredCache, true);
		assertConsistent(expiredCache, KEYS);

		MasterStandbyCache multiLevel = new MasterStandbyCache(new LruCache(20), new HashCache());
		stress(multiLevel, true);
		assertConsistent(multiLevel, KEYS);
	}

}
