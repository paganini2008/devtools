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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * KeyConversionMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class KeyConversionMapTest {

	@Test
	public void testCaseInsensitiveMap() {
		CaseInsensitiveMap<Integer> map = new CaseInsensitiveMap<Integer>();
		assertNull(map.put("Name", 1));
		assertEquals(Integer.valueOf(1), map.get("NAME"));
		assertTrue(map.containsKey("name"));
		assertEquals(Integer.valueOf(1), map.put("NAME", 2));
		assertEquals(1, map.size());
		assertEquals("{NAME=2}", map.toString());
		assertEquals(Integer.valueOf(2), map.put("NAME", 3));
		assertEquals(1, map.size());
		assertEquals(Integer.valueOf(3), map.remove("nAmE"));
		assertNull(map.remove("name"));
		assertFalse(map.containsKey("name"));
		assertNull(map.get("name"));
		map.put(null, 9);
		assertEquals(Integer.valueOf(9), map.get(""));
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testExistingDelegateAndEntrySetRemoval() {
		Map<String, Integer> delegate = new LinkedHashMap<String, Integer>();
		delegate.put("Alpha", 1);
		delegate.put("Beta", 2);
		CaseInsensitiveMap<Integer> map = new CaseInsensitiveMap<Integer>(delegate);
		assertEquals(Integer.valueOf(1), map.get("ALPHA"));
		Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
		it.next();
		it.remove();
		assertFalse(map.containsKey("alpha"));
		map.put("ALPHA", 3);
		assertEquals("{Beta=2, ALPHA=3}", map.toString());
	}

	@Test
	public void testCamelCaseInsensitiveMap() {
		CamelCaseInsensitiveMap<String> map = new CamelCaseInsensitiveMap<String>();
		map.put("user_name", "fred");
		assertEquals("fred", map.get("userName"));
		assertEquals("fred", map.get("user-name"));
		assertTrue(map.containsKey("user_name"));
		assertNull(map.get(null));
		assertEquals("fred", map.remove("userName"));
		assertTrue(map.isEmpty());
	}

}
