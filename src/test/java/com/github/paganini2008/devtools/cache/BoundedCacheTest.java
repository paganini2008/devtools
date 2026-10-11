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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * BoundedCacheTest, common behaviors of the bounded caches backed by a
 * {@link CacheStore}
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BoundedCacheTest {

	private static BoundedCache create(String type, int maxSize) {
		switch (type) {
		case "lru":
			return new LruCache(maxSize);
		case "fifo":
			return new FifoCache(maxSize);
		default:
			return new SortedCache(maxSize);
		}
	}

	/**
	 * Fills the cache so that key 'a' is moved into the store
	 */
	private static BoundedCache prepare(String type, MemoryCacheStore store) {
		BoundedCache cache = create(type, 2);
		cache.setCacheStore(store);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		assertTrue(store.hasKey("a"));
		return cache;
	}

	@ParameterizedTest
	@ValueSource(strings = { "lru", "fifo", "sorted" })
	public void putIfAbsent_keyInStore_isRegardedAsPresent(String type) {
		MemoryCacheStore store = new MemoryCacheStore();
		BoundedCache cache = prepare(type, store);
		cache.putObject("a", 100, true);
		assertEquals(1, cache.getObject("a"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "lru", "fifo", "sorted" })
	public void overwriteKeyInStore_dropsStaleCopy(String type) {
		MemoryCacheStore store = new MemoryCacheStore();
		BoundedCache cache = prepare(type, store);
		cache.putObject("a", 100);
		assertEquals(100, cache.getObject("a"));
		assertEquals(3, cache.getSize());
		assertEquals(cache.keys().size(), cache.getSize());
	}

	@ParameterizedTest
	@ValueSource(strings = { "lru", "fifo", "sorted" })
	public void removeObject_removesFromMemoryAndStore(String type) {
		MemoryCacheStore store = new MemoryCacheStore();
		BoundedCache cache = prepare(type, store);
		assertEquals(1, cache.removeObject("a"));
		assertFalse(cache.hasKey("a"));
		assertNull(cache.getObject("a"));
		assertEquals(2, cache.getSize());
		assertNull(cache.removeObject("missing"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "lru", "fifo", "sorted" })
	public void withoutStore_evictedEntriesAreDropped(String type) {
		BoundedCache cache = create(type, 2);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		assertEquals(2, cache.getSize());
		assertFalse(cache.hasKey("a"));
		assertNull(cache.getObject("a"));
		assertNull(cache.removeObject("a"));
		cache.clear();
		assertEquals(0, cache.getSize());
	}

}
