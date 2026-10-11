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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * LruCacheTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LruCacheTest {

	static class RecordingLruCache extends LruCache {

		final List<Object> disposed = new ArrayList<Object>();

		RecordingLruCache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected void dispose(Object eldestKey, Object eldestObject) {
			disposed.add(eldestKey + "=" + eldestObject);
		}
	}

	@Test
	public void putAndGet_withinCapacity() {
		LruCache cache = new LruCache(3);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		assertEquals(1, cache.getObject("a"));
		assertEquals(2, cache.getObject("b"));
		assertNull(cache.getObject("c"));
		assertEquals(2, cache.getSize());
		assertTrue(cache.hasKey("a"));
		assertFalse(cache.hasKey("c"));
		assertEquals(new HashSet<Object>(Arrays.asList("a", "b")), cache.keys());
		assertTrue(cache.toString().contains("a=1"));
	}

	@Test
	public void evictsLeastRecentlyUsed_whenCapacityExceeded() {
		RecordingLruCache cache = new RecordingLruCache(3);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		// touch 'a', so 'b' becomes the eldest
		assertEquals(1, cache.getObject("a"));
		cache.putObject("d", 4);
		assertEquals(3, cache.getSize());
		assertFalse(cache.hasKey("b"));
		assertEquals(Arrays.asList("b=2"), cache.disposed);
		cache.putObject("e", 5);
		assertFalse(cache.hasKey("c"));
		assertEquals(Arrays.asList("b=2", "c=3"), cache.disposed);
		assertEquals(new HashSet<Object>(Arrays.asList("a", "d", "e")), cache.keys());
	}

	@Test
	public void overwriteExistingKey_doesNotEvict() {
		RecordingLruCache cache = new RecordingLruCache(2);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("a", 10);
		assertEquals(2, cache.getSize());
		assertEquals(10, cache.getObject("a"));
		assertTrue(cache.disposed.isEmpty());
		// 'b' is now the eldest
		cache.putObject("c", 3);
		assertEquals(Arrays.asList("b=2"), cache.disposed);
	}

	@Test
	public void putIfAbsent_keepsExistingValue() {
		LruCache cache = new LruCache(2);
		cache.putObject("a", 1, true);
		cache.putObject("a", 2, true);
		assertEquals(1, cache.getObject("a"));
	}

	@Test
	public void capacityOfOne() {
		RecordingLruCache cache = new RecordingLruCache(1);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		assertEquals(1, cache.getSize());
		assertEquals(2, cache.getObject("b"));
		assertNull(cache.getObject("a"));
	}

	@Test
	public void removeObject_disposesValue() {
		RecordingLruCache cache = new RecordingLruCache(2);
		cache.putObject("a", 1);
		assertEquals(1, cache.removeObject("a"));
		assertEquals(Arrays.asList("a=1"), cache.disposed);
		assertNull(cache.removeObject("a"));
		assertEquals(1, cache.disposed.size());
		assertEquals(0, cache.getSize());
	}

	@Test
	public void nullKeyOrValue_isIgnored() {
		LruCache cache = new LruCache(2);
		cache.putObject(null, 1);
		cache.putObject("a", null);
		assertEquals(0, cache.getSize());
		assertNull(cache.getObject(null));
		assertNull(cache.removeObject(null));
		assertFalse(cache.hasKey(null));
	}

	@Test
	public void evictedEntries_areMovedToCacheStore_andLoadedBack() {
		MemoryCacheStore store = new MemoryCacheStore();
		LruCache cache = new LruCache(2);
		cache.setCacheStore(store);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		assertEquals(Arrays.asList("a"), store.written);
		assertEquals(3, cache.getSize());
		assertTrue(cache.hasKey("a"));
		assertEquals(new HashSet<Object>(Arrays.asList("a", "b", "c")), cache.keys());

		// read from store, moved back to memory, 'b' goes to the store
		assertEquals(1, cache.getObject("a"));
		assertFalse(store.hasKey("a"));
		assertTrue(store.hasKey("b"));
		assertEquals(3, cache.getSize());

		// remove from store
		assertEquals(2, cache.removeObject("b"));
		assertEquals(2, cache.getSize());
		assertNull(cache.getObject("zzz"));

		cache.putObject("d", 4);
		assertEquals(3, cache.getSize());
		cache.clear();
		assertEquals(0, cache.getSize());
		assertEquals(0, store.getSize());
		assertTrue(cache.keys().isEmpty());
	}

	@Test
	public void protectedConstructor_acceptsCustomMap() {
		LruCache cache = new LruCache(new com.github.paganini2008.devtools.collection.LruMap<Object, Object>(2)) {
		};
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		assertEquals(2, cache.getSize());
		assertFalse(cache.hasKey("a"));
	}

}
