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

import org.junit.jupiter.api.Test;

/**
 * MutableMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MutableMapTest {

	static class RoundingMap extends MutableMap<Integer, String> {

		private static final long serialVersionUID = 1L;

		RoundingMap() {
			super(new ConcurrentHashMap<Integer, String>());
		}

		@Override
		protected Integer mutate(Object key) {
			return ((Integer) key / 10) * 10;
		}
	}

	@Test
	public void testMutatedKeys() {
		RoundingMap map = new RoundingMap();
		assertNull(map.put(12, "a"));
		assertEquals("a", map.get(19));
		assertTrue(map.containsKey(10));
		assertFalse(map.containsKey(20));
		assertEquals("a", map.putIfAbsent(15, "b"));
		assertNull(map.putIfAbsent(25, "c"));
		assertTrue(map.containsValue("c"));
		assertEquals(2, map.size());
		assertTrue(map.keySet().contains(20));
		assertEquals(2, map.values().size());
		assertEquals(2, map.entrySet().size());
		assertEquals("c", map.remove(29));
		map.clear();
		assertTrue(map.isEmpty());
	}

}
