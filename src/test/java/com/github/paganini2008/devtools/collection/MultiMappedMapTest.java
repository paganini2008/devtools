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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

/**
 * MultiMappedMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MultiMappedMapTest {

	@Test
	public void testNestedOperations() {
		MultiMappedMap<String, String, Integer> map = new MultiMappedMap<String, String, Integer>();
		assertNull(map.put("s1", "a", 1));
		assertEquals(Integer.valueOf(1), map.put("s1", "a", 2));
		assertEquals(Integer.valueOf(2), map.get("s1", "a"));
		assertNull(map.get("s1", "b"));
		assertEquals(Integer.valueOf(9), map.get("s1", "b", 9));
		assertEquals(Integer.valueOf(9), map.get("zz", "b", 9));
		assertEquals(Integer.valueOf(2), map.putIfAbsent("s1", "a", 5));
		assertNull(map.putIfAbsent("s2", "x", 7));
		assertEquals(Integer.valueOf(3), map.getIfNecessary("s3", "y", 3));
		assertEquals(Integer.valueOf(3), map.getIfNecessary("s3", "y", 4));
		assertNull(map.getIfNecessary("s3", "z", (Integer) null));
		assertTrue(map.containsKey("s3"));
		assertTrue(map.containsValue("s3", "y"));
		assertFalse(map.containsValue("s3", "z"));
		assertFalse(map.containsValue("zz", "z"));
		assertEquals(3, map.size());
		assertEquals(1, map.size("s1"));
		assertEquals(0, map.size("zz"));
		map.append("s1", Collections.singletonMap("b", 4));
		map.append("s4", Collections.singletonMap("b", 4));
		assertEquals(2, map.get("s1").size());
		assertEquals(Integer.valueOf(4), map.removeValue("s1", "b"));
		assertNull(map.removeValue("zz", "b"));
		map.clear("s1");
		map.clear("zz");
		assertTrue(map.get("s1").isEmpty());
		assertEquals(4, map.keySet().size());
		assertEquals(4, map.entrySet().size());
		assertNull(map.put("s5", new HashMap<String, Integer>()));
		assertTrue(map.toString().contains("s5={}"));
		assertTrue(map.remove("s5").isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testConstructors() {
		MultiMappedMap<String, String, Integer> sorted = new MultiMappedMap<String, String, Integer>(
				Collections.reverseOrder());
		sorted.put("k", "a", 1);
		sorted.put("k", "b", 2);
		assertEquals("{b=2, a=1}", sorted.get("k").toString());
		MultiMappedMap<String, String, Integer> supplied = new MultiMappedMap<String, String, Integer>(
				() -> new TreeMap<String, Integer>());
		supplied.put("k", "a", 1);
		assertTrue(supplied.get("k") instanceof TreeMap);
		Map<String, Map<String, Integer>> delegate = new LinkedHashMap<String, Map<String, Integer>>();
		MultiMappedMap<String, String, Integer> delegated = new MultiMappedMap<String, String, Integer>(delegate);
		delegated.put("k", "a", 1);
		assertEquals(1, delegate.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void serializeRoundTripWithDefaultSupplier() throws Exception {
		MultiMappedMap<String, String, Integer> map = new MultiMappedMap<String, String, Integer>();
		map.put("2024", "Jan", 10);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
			out.writeObject(map);
		}
		MultiMappedMap<String, String, Integer> copy;
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
			copy = (MultiMappedMap<String, String, Integer>) in.readObject();
		}
		assertEquals(Integer.valueOf(10), copy.get("2024", "Jan"));
		copy.put("2025", "Feb", 20);
		assertEquals(Integer.valueOf(20), copy.get("2025", "Feb"));
	}

}
