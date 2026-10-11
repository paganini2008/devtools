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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.MatchMode;

/**
 * KeyMatchedMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class KeyMatchedMapTest {

	@Test
	public void testStartMatch() {
		StringKeyMatchedMap<Integer> map = new StringKeyMatchedMap<Integer>(MatchMode.START);
		map.put("abc", 1);
		map.put("abd", 2);
		assertEquals(Integer.valueOf(1), map.get("ab"));
		assertTrue(map.containsKey("abd"));
		assertFalse(map.containsKey("x"));
		assertTrue(map.existsKey("abc"));
		assertFalse(map.existsKey("ab"));
		assertNull(map.get(null));
		assertNull(map.get("zz"));
		assertNull(map.remove("zz"));
		assertEquals(Integer.valueOf(1), map.remove("ab"));
		// the removed key must not shadow the others
		assertEquals(Integer.valueOf(2), map.get("ab"));
		assertEquals(Integer.valueOf(2), map.putIfAbsent("abd", 3));
		assertNull(map.putIfAbsent("abe", 4));
		assertEquals(2, map.size());
		assertThrows(IllegalArgumentException.class, () -> map.put(null, 1));
		map.clear();
		assertTrue(map.isEmpty());
		assertNull(map.get("ab"));
	}

	@Test
	public void testDescendingOrderAndEntryRemoval() {
		StringKeyMatchedMap<Integer> map = new StringKeyMatchedMap<Integer>(MatchMode.START, false);
		map.put("abc", 1);
		map.put("abd", 2);
		assertEquals(Integer.valueOf(2), map.get("ab"));
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getKey().equals("abd")) {
				it.remove();
			}
		}
		assertEquals(Integer.valueOf(1), map.get("ab"));
		assertEquals(Integer.valueOf(1), map.get(new StringBuilder("ab")));
	}

}
