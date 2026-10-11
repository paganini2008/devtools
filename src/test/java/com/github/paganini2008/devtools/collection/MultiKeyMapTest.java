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

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * MultiKeyMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MultiKeyMapTest {

	@Test
	public void testDuplicatedKeys() {
		MultiKeyMap<String, Integer> map = new MultiKeyMap<String, Integer>();
		assertTrue(map.isEmpty());
		assertNull(map.put("a", 1));
		map.put("b", 2);
		map.put("a", 3);
		assertEquals(3, map.size());
		assertEquals(Integer.valueOf(1), map.get("a"));
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsValue(3));
		assertEquals("[a=1, b=2, a=3]", map.entrySet().toString());
		assertEquals(3, map.keySet().size());
		assertEquals(Integer.valueOf(1), map.remove("a"));
		assertEquals(Integer.valueOf(3), map.get("a"));
		assertEquals("[b=2, a=3]", map.entrySet().toString());
		assertNull(map.remove("zz"));
		map.put("a", 4);
		map.removeAll("a");
		assertFalse(map.containsKey("a"));
		assertEquals("[b=2]", map.entrySet().toString());
		Map.Entry<String, Integer> entry = map.entrySet().iterator().next();
		assertEquals(Integer.valueOf(2), entry.setValue(5));
		assertEquals(Integer.valueOf(5), map.get("b"));
		assertEquals("[5]", map.values().toString());
		map.clear();
		assertEquals(0, map.size());
	}

	@Test
	public void testNullValuesAndSynchronizedMap() {
		MultiKeyMap<String, Integer> map = new MultiKeyMap<String, Integer>();
		map.put("a", null);
		map.put("a", 1);
		map.removeAll("a");
		assertTrue(map.isEmpty());

		Map<String, Integer> sync = MultiKeyMap.synchronizedMap();
		sync.put("x", 1);
		sync.put("x", 2);
		sync.remove("x");
		assertEquals(Integer.valueOf(2), sync.get("x"));
		assertEquals(1, sync.size());
	}

}
