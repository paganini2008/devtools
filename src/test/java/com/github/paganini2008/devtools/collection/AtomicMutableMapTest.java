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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

import org.junit.jupiter.api.Test;

/**
 * AtomicMutableMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class AtomicMutableMapTest {

	static class CaseMap extends AtomicMutableMap<String, Integer> {

		private static final long serialVersionUID = 1L;

		CaseMap() {
			super(new ConcurrentHashMap<String, AtomicStampedReference<Integer>>());
		}

		@Override
		protected String mutate(Object key) {
			return key.toString().toLowerCase();
		}
	}

	static class PlainMap extends AtomicMutableMap<String, Integer> {

		private static final long serialVersionUID = 1L;

		PlainMap() {
			super(new ConcurrentHashMap<String, AtomicStampedReference<Integer>>());
		}
	}

	@Test
	public void testBasicOperations() {
		CaseMap map = new CaseMap();
		assertNull(map.put("A", 1));
		assertEquals(Integer.valueOf(1), map.put("a", 2));
		assertEquals(Integer.valueOf(2), map.get("A"));
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsValue(2));
		assertFalse(map.containsValue(3));
		assertEquals(1, map.size());
		assertEquals("[a]", map.keySet().toString());
		assertEquals("[2]", map.values().toString());
		assertEquals("{a=2}", map.toMap().toString());
		assertEquals(1, map.entrySet().size());
		assertEquals(Integer.valueOf(2), map.remove("A"));
		assertNull(map.remove("A"));
		assertNull(map.get("A"));
		map.put("x", null);
		assertEquals("{x=null}", map.toMap().toString());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testConcurrentMerge() throws Exception {
		final PlainMap map = new PlainMap();
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			new Thread(() -> {
				for (int i = 0; i < 1000; i++) {
					map.merge("k", 1, (current, value) -> current != null ? current + value : value);
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertEquals(Integer.valueOf(4000), map.get("k"));
	}

}
