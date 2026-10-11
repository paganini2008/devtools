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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * SetUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SetUtilsTest {

	@Test
	public void testSetUtils() {
		assertTrue(SetUtils.emptySet().isEmpty());
		Set<String> u = SetUtils.unmodifiableSet("a", "a", "b");
		assertEquals(2, u.size());
		assertThrows(UnsupportedOperationException.class, () -> u.add("c"));
		assertEquals(1, SetUtils.unmodifiableSet(Arrays.asList("a", "a")).size());
		assertTrue(SetUtils.synchronizedSet().isEmpty());
		assertEquals(2, SetUtils.synchronizedSet(Arrays.asList("a", "b", "a")).size());
		assertTrue(SetUtils.create().isEmpty());
		Set<String> s = new HashSet<String>();
		assertSame(s, SetUtils.toSet(s));
		assertTrue(SetUtils.isSet(s));
		assertFalse(SetUtils.isSet(null));
		assertTrue(SetUtils.isNotSet("a"));
	}

}
