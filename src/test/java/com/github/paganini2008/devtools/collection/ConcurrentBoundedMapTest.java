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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * ConcurrentBoundedMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConcurrentBoundedMapTest {

	@Test
	public void testFifoEviction() {
		final List<String> evicted = new ArrayList<String>();
		ConcurrentBoundedMap<String, Integer> map = new ConcurrentBoundedMap<String, Integer>(new LinkedHashMap<String, Integer>(), 2) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(String eldestKey, Integer eldestValue) {
				evicted.add(eldestKey + "=" + eldestValue);
			}
		};
		assertNull(map.put("a", 1));
		map.put("b", 2);
		assertEquals(Integer.valueOf(1), map.put("a", 10));
		map.put("c", 3);
		assertEquals("[a=10]", evicted.toString());
		assertEquals("{b=2, c=3}", map.toString());
		assertEquals(Integer.valueOf(2), map.putIfAbsent("b", 20));
		assertNull(map.putIfAbsent("d", 4));
		assertEquals("{c=3, d=4}", map.getDelegate().toString());
		assertTrue(map.containsKey("c"));
		assertTrue(map.containsValue(4));
		assertEquals(Integer.valueOf(3), map.get("c"));
		assertEquals(2, map.getMaxSize());
	}

	@Test
	public void testRemoveAndClear() {
		ConcurrentBoundedMap<String, Integer> map = new ConcurrentBoundedMap<String, Integer>(2);
		map.put("a", 1);
		map.put("b", 2);
		assertEquals(Integer.valueOf(1), map.remove("a"));
		map.put("c", 3);
		assertEquals(2, map.size());
		assertTrue(map.containsKey("b"));
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		it.next();
		it.remove();
		map.keySet().remove(map.keySet().iterator().next());
		assertTrue(map.isEmpty());
		map.put("x", 1);
		map.put("y", 2);
		assertEquals(2, map.size());
		map.clear();
		assertEquals(0, map.size());
		map.put("p", 1);
		map.put("q", 2);
		map.put("r", 3);
		assertFalse(map.containsKey("p"));
		assertEquals(2, map.values().size());
	}

	@Test
	public void testConcurrentPut() throws Exception {
		final ConcurrentBoundedMap<Integer, Integer> map = new ConcurrentBoundedMap<Integer, Integer>(30);
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
		for (int i = 0; i < 40; i++) {
			map.put(-i - 1, i);
		}
		assertEquals(30, map.size());
	}

}
