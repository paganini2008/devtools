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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * MultiDequeMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MultiDequeMapTest {

	@Test
	public void testDequeOperations() {
		MultiDequeMap<String, Integer> map = new MultiDequeMap<String, Integer>();
		assertEquals(Integer.valueOf(2), map.addLast("a", 2));
		map.addFirst("a", 1);
		map.addLast("a", 3);
		map.addFirst("b", 9);
		map.addAll("c", Arrays.asList(1, 1, 2));
		map.addAll("c", Arrays.asList(3));
		assertEquals("[1, 2, 3]", map.get("a").toString());
		assertEquals(3, map.size());
		assertEquals(3, map.size("a"));
		assertEquals(0, map.size("zz"));
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsValue("c", 3));
		assertFalse(map.containsValue("zz", 3));
		assertEquals(Integer.valueOf(1), map.peekFirst("a"));
		assertEquals(Integer.valueOf(3), map.peekLast("a"));
		assertEquals(Integer.valueOf(1), map.pollFirst("a"));
		assertEquals(Integer.valueOf(3), map.pollLast("a"));
		map.removeValue("c", 1);
		map.removeValue("zz", 1);
		assertEquals("[2, 3]", map.get("c").toString());
		map.clear("c");
		map.clear("zz");
		assertTrue(map.get("c").isEmpty());
		Map<String, Integer> single = map.toSingleValueMap();
		assertEquals(Integer.valueOf(2), single.get("a"));
		assertNull(single.get("c"));
		assertEquals(3, map.keySet().size());
		assertEquals(3, map.entrySet().size());
		assertEquals("[9]", map.remove("b").toString());
		assertTrue(map.toString().contains("a=[2]"));
		Deque<Integer> deque = new ArrayDeque<Integer>();
		assertNull(map.put("d", deque));
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testMissingKeyIsNullSafe() {
		MultiDequeMap<String, Integer> map = new MultiDequeMap<String, Integer>(new HashMap<String, Deque<Integer>>(),
				() -> new ArrayDeque<Integer>());
		assertNull(map.pollFirst("x"));
		assertNull(map.pollLast("x"));
		assertNull(map.peekFirst("x"));
		assertNull(map.peekLast("x"));
	}

}
