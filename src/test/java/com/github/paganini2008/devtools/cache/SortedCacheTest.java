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
 * SortedCacheTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SortedCacheTest {

	static class RecordingSortedCache extends SortedCache {

		final List<Object> disposed = new ArrayList<Object>();

		RecordingSortedCache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected void dispose(Object eldestKey, Object eldestObject) {
			disposed.add(eldestKey + "=" + eldestObject);
		}
	}

	@Test
	public void ascending_evictsSmallestKey() {
		RecordingSortedCache cache = new RecordingSortedCache(3);
		cache.putObject(5, "five");
		cache.putObject(1, "one");
		cache.putObject(3, "three");
		cache.putObject(4, "four");
		assertEquals(Arrays.asList("1=one"), cache.disposed);
		cache.putObject(2, "two");
		// 2 is the smallest now, so it is evicted immediately
		assertEquals(Arrays.asList("1=one", "2=two"), cache.disposed);
		assertEquals(new HashSet<Object>(Arrays.asList(3, 4, 5)), cache.keys());
		assertEquals(3, cache.getSize());
		assertTrue(cache.toString().contains("3=three"));
	}

	@Test
	public void descending_evictsLargestKey() {
		RecordingSortedCache cache = new RecordingSortedCache(2);
		cache.setAsc(false);
		cache.putObject("b", 2);
		cache.putObject("a", 1);
		cache.putObject("c", 3);
		assertEquals(Arrays.asList("c=3"), cache.disposed);
		assertEquals(new HashSet<Object>(Arrays.asList("a", "b")), cache.keys());
	}

	@Test
	public void putIfAbsent_getAndRemove() {
		RecordingSortedCache cache = new RecordingSortedCache(3);
		cache.putObject("a", 1, true);
		cache.putObject("a", 2, true);
		assertEquals(1, cache.getObject("a"));
		assertTrue(cache.hasKey("a"));
		assertEquals(1, cache.removeObject("a"));
		assertEquals(Arrays.asList("a=1"), cache.disposed);
		assertNull(cache.removeObject("a"));
		assertNull(cache.getObject("a"));
		assertFalse(cache.hasKey("a"));
	}

	@Test
	public void nullKeyOrValue_isIgnored() {
		SortedCache cache = new SortedCache(2);
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
		SortedCache cache = new SortedCache(2);
		cache.setCacheStore(store);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		cache.putObject("d", 4);
		assertEquals(Arrays.asList("b"), store.written);
		assertEquals(3, cache.getSize());
		assertTrue(cache.hasKey("b"));
		assertEquals(new HashSet<Object>(Arrays.asList("b", "c", "d")), cache.keys());
		assertEquals(2, cache.removeObject("b"));
		assertEquals(2, cache.getSize());
		cache.putObject("a", 1);
		// 'a' is the smallest, moved to the store directly
		assertTrue(store.hasKey("a"));
		assertEquals(1, cache.getObject("a"));
		assertNull(cache.getObject("zzz"));
		cache.clear();
		assertEquals(0, cache.getSize());
		assertEquals(0, store.getSize());
	}

}
