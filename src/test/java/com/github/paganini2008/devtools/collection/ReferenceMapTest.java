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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * ReferenceMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ReferenceMapTest {

	@Test
	public void testSoftReferenceMap() {
		SoftReferenceMap<String, String> map = new SoftReferenceMap<String, String>();
		assertTrue(map.isEmpty());
		assertNull(map.put("a", "1"));
		assertEquals("1", map.put("a", "2"));
		map.putAll(Collections.singletonMap("b", "3"));
		assertEquals(2, map.size());
		assertEquals("2", map.get("a"));
		assertTrue(map.containsKey("a"));
		assertFalse(map.containsKey("z"));
		assertTrue(map.containsValue("3"));
		assertFalse(map.containsValue("9"));
		assertFalse(map.containsValue(null));
		assertEquals(2, map.values().size());
		assertEquals(2, map.entrySet().size());
		assertEquals(2, map.keySet().size());
		assertTrue(map.toString().contains("b=3"));
		assertEquals("3", map.remove("b"));
		assertNull(map.remove("b"));
		map.clear();
		assertTrue(map.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> map.values().clear());
	}

	@Test
	public void testWeakReferenceMap() {
		WeakReferenceMap<String, Object> map = new WeakReferenceMap<String, Object>();
		Object value = new Object();
		map.put("a", value);
		assertEquals(value, map.get("a"));
		assertTrue(map.containsValue(value));
		assertEquals(1, map.entrySet().size());
		assertEquals(value, map.remove("a"));
		assertEquals(0, map.size());
	}

	@Test
	public void testClearedReferenceBehavesAsAbsent() {
		final Map<String, Reference<String>> delegate = new LinkedHashMap<String, Reference<String>>();
		ReferenceMap<String, String> map = new ReferenceMap<String, String>(delegate) {

			@Override
			protected Reference<String> fold(String key, String value) {
				return new WeakReference<String>(value);
			}
		};
		map.put("a", new String("1"));
		map.put("b", "2");
		delegate.get("a").clear();
		assertFalse(map.containsKey("a"));
		assertNull(map.get("a"));
		assertEquals(1, map.values().size());
		assertEquals(1, map.entrySet().size());
		assertEquals("{b=2}", map.toString());
		assertEquals(2, map.size());
		Reference<String> ref = delegate.get("b");
		assertFalse(map.removeReference("b", new WeakReference<String>("2")));
		assertTrue(map.removeReference("b", ref));
		assertFalse(map.containsKey("b"));
	}

	@Test
	public void testEvictionOfReclaimedValues() throws Exception {
		final Map<String, Object> evicted = new HashMap<String, Object>();
		WeakReferenceMap<String, Object> map = new WeakReferenceMap<String, Object>() {

			@Override
			protected void onEviction(String key, Object value) {
				evicted.put(key, value);
			}
		};
		map.put("gone", new Object());
		Object kept = new Object();
		long deadline = System.currentTimeMillis() + 3000;
		while (!evicted.containsKey("gone") && System.currentTimeMillis() < deadline) {
			System.gc();
			Thread.sleep(10);
			map.put("kept", kept);
		}
		// GC is not guaranteed, but when it happens the key must be purged
		if (evicted.containsKey("gone")) {
			assertNull(evicted.get("gone"));
			assertFalse(map.keySet().contains("gone"));
		}
		assertEquals(kept, map.get("kept"));
	}

}
