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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * ReferenceCacheTest, tests {@link SoftReferenceCache} and
 * {@link WeakReferenceCache}
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ReferenceCacheTest {

	private static Cache create(boolean soft) {
		return soft ? new SoftReferenceCache() : new WeakReferenceCache();
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	public void basicOperations(boolean soft) {
		Cache cache = create(soft);
		// strongly referenced values are never reclaimed
		String a = new String("A"), b = new String("B");
		cache.putObject("a", a);
		cache.putObject("a", b, true);
		assertEquals("A", cache.getObject("a"));
		cache.putObject("b", b);
		assertTrue(cache.hasKey("b"));
		assertEquals(2, cache.getSize());
		assertEquals(new HashSet<Object>(Arrays.asList("a", "b")), cache.keys());
		assertTrue(cache.toString().length() > 0);
		assertEquals("B", cache.removeObject("b"));
		assertFalse(cache.hasKey("b"));
		cache.clear();
		assertEquals(0, cache.getSize());
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	public void nullKeyOrValue_isIgnored(boolean soft) {
		Cache cache = create(soft);
		cache.putObject(null, "x");
		cache.putObject("a", null);
		assertEquals(0, cache.getSize());
		assertNull(cache.getObject(null));
		assertNull(cache.removeObject(null));
		assertFalse(cache.hasKey(null));
	}

}
