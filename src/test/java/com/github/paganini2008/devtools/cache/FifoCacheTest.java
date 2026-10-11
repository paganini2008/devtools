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
 * FifoCacheTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FifoCacheTest {

	static class RecordingFifoCache extends FifoCache {

		final List<Object> disposed = new ArrayList<Object>();

		RecordingFifoCache(int maxSize) {
			super(maxSize);
		}

		@Override
		protected void dispose(Object eldestKey, Object eldestObject) {
			disposed.add(eldestKey + "=" + eldestObject);
		}
	}

	@Test
	public void evictsFirstInserted_regardlessOfAccess() {
		RecordingFifoCache cache = new RecordingFifoCache(3);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		// access does not change the eviction order
		assertEquals(1, cache.getObject("a"));
		cache.putObject("d", 4);
		assertFalse(cache.hasKey("a"));
		assertTrue(cache.hasKey("b"));
		cache.putObject("e", 5);
		assertFalse(cache.hasKey("b"));
		assertEquals(Arrays.asList("a=1", "b=2"), cache.disposed);
		assertEquals(3, cache.getSize());
		assertEquals(new HashSet<Object>(Arrays.asList("c", "d", "e")), cache.keys());
		assertTrue(cache.toString().contains("c=3"));
	}

	@Test
	public void overwrite_keepsInsertionPosition() {
		RecordingFifoCache cache = new RecordingFifoCache(2);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("a", 10);
		assertEquals(10, cache.getObject("a"));
		assertTrue(cache.disposed.isEmpty());
		cache.putObject("c", 3);
		assertEquals(Arrays.asList("a=10"), cache.disposed);
	}

	@Test
	public void putIfAbsent_keepsExistingValue() {
		FifoCache cache = new FifoCache(2);
		cache.putObject("a", 1, true);
		cache.putObject("a", 2, true);
		assertEquals(1, cache.getObject("a"));
		assertEquals(1, cache.getSize());
	}

	@Test
	public void removeThenReinsert_isTreatedAsNewEntry() {
		RecordingFifoCache cache = new RecordingFifoCache(2);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		assertEquals(1, cache.removeObject("a"));
		assertEquals(Arrays.asList("a=1"), cache.disposed);
		cache.putObject("a", 3);
		cache.putObject("c", 4);
		// 'b' is the eldest now
		assertFalse(cache.hasKey("b"));
		assertTrue(cache.hasKey("a"));
		assertNull(cache.removeObject("zzz"));
	}

	@Test
	public void nullKeyOrValue_isIgnored() {
		FifoCache cache = new FifoCache(2);
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
		FifoCache cache = new FifoCache(2);
		cache.setCacheStore(store);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		assertEquals(Arrays.asList("a"), store.written);
		assertEquals(3, cache.getSize());
		assertTrue(cache.hasKey("a"));
		assertEquals(new HashSet<Object>(Arrays.asList("a", "b", "c")), cache.keys());
		assertEquals(1, cache.getObject("a"));
		assertFalse(store.hasKey("a"));
		assertTrue(store.hasKey("b"));
		assertEquals(2, cache.removeObject("b"));
		assertNull(cache.getObject("zzz"));
		cache.clear();
		assertEquals(0, cache.getSize());
		assertEquals(0, store.getSize());
	}

}
