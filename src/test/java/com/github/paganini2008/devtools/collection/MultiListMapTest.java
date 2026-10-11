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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * MultiListMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MultiListMapTest {

	@Test
	public void testListOperations() {
		MultiListMap<String, Integer> map = new MultiListMap<String, Integer>();
		assertEquals(Integer.valueOf(1), map.add("a", 1));
		map.add("a", 2);
		map.add("a", 1);
		map.add("a", 3);
		map.addAll("b", Arrays.asList(7, 8));
		map.addAll("b", Arrays.asList(9));
		assertEquals("[1, 2, 1, 3]", map.get("a").toString());
		assertEquals(Integer.valueOf(2), map.get("a", 1));
		assertEquals(Integer.valueOf(3), map.get("a", -1));
		assertEquals(Integer.valueOf(1), map.peekFirst("a"));
		assertEquals(Integer.valueOf(3), map.peekLast("a"));
		assertEquals(4, map.size("a"));
		assertEquals(0, map.size("zz"));
		assertEquals(2, map.size());
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsValue("a", 2));
		assertFalse(map.containsValue("zz", 2));
		assertEquals(Integer.valueOf(1), map.remove("a", 2));
		assertEquals("[1, 2, 3]", map.get("a").toString());
		assertEquals(Integer.valueOf(1), map.pollFirst("a"));
		assertEquals(Integer.valueOf(3), map.pollLast("a"));
		assertNull(map.remove("zz", 0));
		assertNull(map.pollFirst("zz"));
		assertNull(map.pollLast("zz"));
		map.add("a", 2);
		map.removeValue("a", 2);
		map.removeValue("zz", 2);
		assertTrue(map.get("a").isEmpty());
		map.clear("b");
		map.clear("zz");
		assertTrue(map.get("b").isEmpty());
		map.add("c", 5);
		Map<String, Integer> single = map.toSingleValueMap();
		assertEquals(Integer.valueOf(5), single.get("c"));
		assertNull(single.get("a"));
		assertEquals(3, map.keys().size());
		assertEquals(3, map.entrySet().size());
		assertTrue(map.toString().contains("c=[5]"));
		List<Integer> list = new ArrayList<Integer>();
		assertNull(map.put("d", list));
		assertEquals("[5]", map.remove("c").toString());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testCustomSupplier() {
		MultiListMap<String, Integer> map = new MultiListMap<String, Integer>(() -> new ArrayList<Integer>());
		map.add("a", 1);
		assertTrue(map.get("a") instanceof ArrayList);
	}

}
