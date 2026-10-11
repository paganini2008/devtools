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
package com.github.paganini2008.devtools.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * LruMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LruMapTest {

	@Test
	public void testEvictLeastRecentlyUsed() {
		List<String> evicted = new ArrayList<String>();
		LruMap<String, Integer> map = new LruMap<String, Integer>(3) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(String eldestKey, Integer eldestValue) {
				evicted.add(eldestKey + "=" + eldestValue);
			}
		};
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		assertEquals(Integer.valueOf(1), map.get("a"));
		map.put("d", 4);
		assertEquals(3, map.size());
		assertFalse(map.containsKey("b"));
		assertEquals("[b=2]", evicted.toString());
		assertTrue(map.containsKey("c"));
		map.put("e", 5);
		assertFalse(map.containsKey("a"));
		assertEquals("[b=2, a=1]", evicted.toString());
		assertEquals(3, map.getMaxSize());
	}

	@Test
	public void testPutIfAbsentAndRemove() {
		LruMap<String, Integer> map = new LruMap<String, Integer>(2);
		assertNull(map.putIfAbsent("a", 1));
		assertEquals(Integer.valueOf(1), map.putIfAbsent("a", 2));
		assertEquals(Integer.valueOf(1), map.get("a"));
		map.put("b", 2);
		assertEquals(Integer.valueOf(2), map.remove("b"));
		map.put("c", 3);
		map.put("d", 4);
		assertEquals(2, map.size());
		assertTrue(map.containsValue(4));
		assertFalse(map.containsValue(1));
		map.clear();
		assertTrue(map.isEmpty());
		map.put("x", 1);
		map.put("y", 2);
		assertEquals(2, map.size());
	}

	@Test
	public void testRemoveThroughViews() {
		LruMap<String, Integer> map = new LruMap<String, Integer>(new ConcurrentHashMap<String, Integer>(), 3);
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		map.keySet().remove("a");
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		it.next();
		it.remove();
		assertEquals(1, map.size());
		// the removed keys must no longer occupy capacity
		map.put("x", 10);
		map.put("y", 20);
		assertEquals(3, map.size());
		map.values().remove(10);
		assertFalse(map.containsKey("x"));
		assertEquals(2, map.size());
		map.entrySet().clear();
		assertTrue(map.isEmpty());
		assertThrows(IllegalStateException.class, () -> map.entrySet().iterator().remove());
	}

	@Test
	public void testSortedAndDelegate() {
		LruMap<String, Integer> map = new LruMap<String, Integer>(true, 10);
		map.put("b", 2);
		map.put("a", 1);
		assertEquals("{a=1, b=2}", map.toString());
		assertEquals("{a=1, b=2}", map.getDelegate().toString());
		assertEquals(128, new LruMap<String, String>().getMaxSize());
		assertThrows(IllegalArgumentException.class, () -> new LruMap<String, String>(0));
	}

	@Test
	public void testComputeAndMergeGoThroughCapacity() {
		LruMap<String, Integer> map = new LruMap<String, Integer>(2);
		map.computeIfAbsent("a", k -> 1);
		map.merge("b", 1, Integer::sum);
		map.merge("b", 1, Integer::sum);
		map.merge("c", 1, Integer::sum);
		assertEquals(2, map.size());
		assertEquals(Integer.valueOf(2), map.get("b"));
		assertFalse(map.containsKey("a"));
	}

	@Test
	public void testConcurrentAccessKeepsBound() throws Exception {
		final AtomicInteger evictions = new AtomicInteger();
		final LruMap<Integer, Integer> map = new LruMap<Integer, Integer>(50) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Integer eldestKey, Integer eldestValue) {
				evictions.incrementAndGet();
			}
		};
		int threads = 8;
		final int perThread = 500;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(threads);
		for (int t = 0; t < threads; t++) {
			final int base = t * perThread;
			executor.execute(() -> {
				try {
					start.await();
					for (int i = 0; i < perThread; i++) {
						map.put(base + i, i);
						map.get(base + i / 2);
						if (i % 7 == 0) {
							map.remove(base + i);
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					done.countDown();
				}
			});
		}
		start.countDown();
		assertTrue(done.await(10, TimeUnit.SECONDS));
		executor.shutdown();
		assertTrue(map.size() <= 50);
		assertTrue(evictions.get() > 0);
		// the map still evicts properly afterwards
		for (int i = 0; i < 100; i++) {
			map.put(-i - 1, i);
		}
		assertEquals(50, map.size());
		assertSame(null, map.get(-1));
	}

}
