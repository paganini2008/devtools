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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * ConcurrentSortedBoundedMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConcurrentSortedBoundedMapTest {

	@Test
	public void testEvictSmallestKey() {
		final List<String> evicted = new ArrayList<String>();
		ConcurrentSortedBoundedMap<Integer, String> map = new ConcurrentSortedBoundedMap<Integer, String>(new TreeMap<Integer, String>(), 2) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Integer eldestKey, String eldestValue) {
				evicted.add(eldestKey + "=" + eldestValue);
			}
		};
		assertNull(map.put(5, "e"));
		map.put(3, "c");
		map.put(8, "h");
		assertEquals("{5=e, 8=h}", map.toString());
		assertEquals("[3=c]", evicted.toString());
		assertEquals("e", map.putIfAbsent(5, "x"));
		assertNull(map.putIfAbsent(9, "i"));
		assertEquals("{8=h, 9=i}", map.getDelegate().toString());
		assertTrue(map.containsKey(8));
		assertTrue(map.containsValue("i"));
		assertEquals("h", map.get(8));
		assertEquals(2, map.getMaxSize());
	}

	@Test
	public void testEvictLargestKeyAndRemove() {
		ConcurrentSortedBoundedMap<Integer, String> map = new ConcurrentSortedBoundedMap<Integer, String>(2);
		map.setAsc(false);
		map.put(5, "e");
		map.put(3, "c");
		map.put(8, "h");
		assertFalse(map.containsKey(8));
		assertEquals("c", map.remove(3));
		map.put(1, "a");
		assertEquals(2, map.size());
		assertTrue(map.containsKey(5));
		map.entrySet().iterator().next();
		map.keySet().remove(5);
		map.put(0, "z");
		assertEquals(2, map.size());
		map.clear();
		map.put(10, "j");
		map.put(20, "t");
		assertEquals(2, map.size());
		map.merge(10, "!", String::concat);
		assertEquals("j!", map.get(10));
		map.merge(30, "x", String::concat);
		assertEquals(2, map.size());
		assertFalse(map.containsKey(30));
		assertEquals(2, map.values().size());
	}

	@Test
	public void testClearAndRemoveDoNotLeaveStaleKeys() {
		final List<String> evicted = new ArrayList<String>();
		ConcurrentSortedBoundedMap<Integer, String> map = new ConcurrentSortedBoundedMap<Integer, String>(2) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Integer eldestKey, String eldestValue) {
				evicted.add(eldestKey + "=" + eldestValue);
			}
		};
		map.put(1, "a");
		map.put(2, "b");
		map.clear();
		map.put(5, "e");
		map.put(6, "f");
		assertEquals(2, map.size());
		assertTrue(evicted.isEmpty());
		map.remove(5);
		map.put(7, "g");
		assertEquals(2, map.size());
		assertTrue(evicted.isEmpty());
		// a key smaller than all retained keys is evicted immediately by design
		map.put(0, "z");
		assertEquals("[0=z]", evicted.toString());
		assertEquals(2, map.size());
	}

	@Test
	public void testConcurrentPut() throws Exception {
		final ConcurrentSortedBoundedMap<Integer, Integer> map = new ConcurrentSortedBoundedMap<Integer, Integer>(30);
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			final int base = t * 1000;
			new Thread(() -> {
				for (int i = 0; i < 300; i++) {
					map.put(base + i, i);
					if (i % 4 == 0) {
						map.remove(base + i);
					}
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertTrue(map.size() <= 30);
		for (int i = 10000; i < 10040; i++) {
			map.put(i, i);
		}
		assertEquals(30, map.size());
		assertTrue(map.containsKey(10039));
	}

}
