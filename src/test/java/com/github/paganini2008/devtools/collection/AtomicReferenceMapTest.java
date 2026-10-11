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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * AtomicReferenceMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class AtomicReferenceMapTest {

	@Test
	public void testBasicOperations() {
		AtomicReferenceMap<String, Integer> map = new AtomicReferenceMap<String, Integer>(true);
		assertNull(map.put("b", 2));
		assertEquals(Integer.valueOf(2), map.put("b", 3));
		map.put("a", 1);
		assertEquals("{a=1, b=3}", map.toString());
		assertEquals("[1, 3]", map.values().toString());
		assertEquals(2, map.entrySet().size());
		assertEquals("[a, b]", map.keySet().toString());
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsValue(3));
		assertFalse(map.containsValue(4));
		assertEquals(2, map.size());
		// putIfAbsent stores the result of the merge hook and returns it
		assertEquals(Integer.valueOf(5), map.putIfAbsent("a", 5));
		assertEquals(Integer.valueOf(7), map.putIfAbsent("c", 7));
		assertEquals(Integer.valueOf(5), map.remove("a"));
		assertNull(map.remove("a"));
		assertNull(map.get("a"));
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testConcurrentMergeHook() throws Exception {
		final AtomicReferenceMap<String, Integer> map = new AtomicReferenceMap<String, Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Integer merge(String key, Integer current, Integer value) {
				return current != null ? current + value : value;
			}
		};
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			new Thread(() -> {
				for (int i = 0; i < 1000; i++) {
					map.putIfAbsent("k", 1);
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertEquals(Integer.valueOf(4000), map.get("k"));
	}

}
