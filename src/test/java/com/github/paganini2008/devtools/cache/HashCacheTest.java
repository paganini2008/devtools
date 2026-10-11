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
package com.github.paganini2008.devtools.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * HashCacheTest, also covers the default methods of {@link Cache} and
 * {@link AbstractCache}
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class HashCacheTest {

	@Test
	public void basicOperations() {
		HashCache cache = new HashCache();
		cache.putObject("a", 1);
		cache.putObject("a", 2, true);
		assertEquals(1, cache.getObject("a"));
		cache.putObject("a", 3, false);
		assertEquals(3, cache.getObject("a"));
		assertTrue(cache.hasKey("a"));
		assertEquals(1, cache.getSize());
		assertEquals("{a=3}", cache.toString());
		assertEquals(3, cache.removeObject("a"));
		assertNull(cache.removeObject("a"));
		assertFalse(cache.hasKey("a"));
		cache.putObject("b", 1);
		cache.clear();
		assertEquals(0, cache.getSize());
		cache.close();
	}

	@Test
	public void getObject_withDefaultValue() {
		HashCache cache = new HashCache();
		assertEquals("x", cache.getObject("a", "x"));
		// the default value is stored
		assertEquals("x", cache.getObject("a"));
		assertEquals("x", cache.getObject("a", "y"));
		assertEquals("z", cache.getObject("b", () -> "z"));
		// a null default value is not stored
		assertNull(cache.getObject("c", (Object) null));
		assertNull(cache.getObject("c", () -> null));
		assertFalse(cache.hasKey("c"));
	}

	@Test
	public void toEntries_andIterator() {
		HashCache cache = new HashCache(new LinkedHashMap<Object, Object>());
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		Map<Object, Object> entries = cache.toEntries();
		assertEquals(3, entries.size());
		assertEquals(2, entries.get("b"));

		List<Object> values = new ArrayList<Object>();
		for (Object value : cache) {
			values.add(value);
		}
		assertEquals(Arrays.asList(1, 2, 3), values);

		// removing while iterating is safe even on a non-concurrent map
		Iterator<Object> it = cache.iterator();
		assertThrows(IllegalStateException.class, () -> it.remove());
		while (it.hasNext()) {
			if (it.next().equals(2)) {
				it.remove();
				assertThrows(IllegalStateException.class, () -> it.remove());
			}
		}
		assertThrows(NoSuchElementException.class, () -> it.next());
		assertEquals(2, cache.getSize());
		assertFalse(cache.hasKey("b"));
	}

	@Test
	public void customMap_allowsNull() {
		HashCache cache = new HashCache(new HashMap<Object, Object>());
		cache.putObject(null, "n");
		assertEquals("n", cache.getObject(null));
	}

	@Test
	public void masterStandbyCache_factory() {
		HashCache master = new HashCache();
		HashCache backup = new HashCache();
		Cache cache = master.masterStandbyCache(backup);
		cache.putObject("a", 1);
		assertEquals(1, master.getObject("a"));
		assertEquals(1, backup.getObject("a"));
	}

}
