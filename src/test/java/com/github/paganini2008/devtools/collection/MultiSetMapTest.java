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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 * MultiSetMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MultiSetMapTest {

	@Test
	public void testSetOperations() {
		MultiSetMap<String, Integer> map = new MultiSetMap<String, Integer>();
		assertEquals(Integer.valueOf(3), map.add("a", 3));
		map.add("a", 1);
		map.add("a", 3);
		map.addAll("b", Arrays.asList(5, 4));
		map.addAll("b", Arrays.asList(6));
		assertEquals("[1, 3]", map.get("a").toString());
		assertEquals(Integer.valueOf(1), map.peekFirst("a"));
		assertEquals(Integer.valueOf(3), map.peekLast("a"));
		assertEquals(2, map.size("a"));
		assertEquals(0, map.size("zz"));
		assertEquals(2, map.size());
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsValue("b", 6));
		assertFalse(map.containsValue("zz", 6));
		assertEquals(Integer.valueOf(4), map.pollFirst("b"));
		assertEquals(Integer.valueOf(6), map.pollLast("b"));
		assertNull(map.pollFirst("zz"));
		assertNull(map.pollLast("zz"));
		assertNull(map.peekFirst("zz"));
		assertNull(map.peekLast("zz"));
		map.removeValue("b", 5);
		map.removeValue("zz", 5);
		assertTrue(map.get("b").isEmpty());
		assertNull(map.peekFirst("b"));
		assertNull(map.peekLast("b"));
		Map<String, Integer> single = map.toSingleValueMap();
		assertEquals(Integer.valueOf(1), single.get("a"));
		assertNull(single.get("b"));
		map.clear("a");
		map.clear("zz");
		assertTrue(map.get("a").isEmpty());
		assertEquals(2, map.keySet().size());
		assertEquals(2, map.entrySet().size());
		assertNull(map.put("c", new TreeSet<Integer>()));
		assertTrue(map.toString().contains("c=[]"));
		assertTrue(map.remove("c").isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testComparatorAndSupplier() {
		MultiSetMap<String, Integer> map = new MultiSetMap<String, Integer>(Collections.reverseOrder());
		map.add("a", 1);
		map.add("a", 2);
		assertEquals("[2, 1]", map.get("a").toString());
		MultiSetMap<String, Integer> map2 = new MultiSetMap<String, Integer>(() -> new TreeSet<Integer>());
		map2.add("a", 1);
		assertTrue(map2.get("a") instanceof TreeSet);
	}

}
