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
package com.github.paganini2008.devtools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * MatchModeTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MatchModeTest {

	@Test
	public void testMatches() {
		assertTrue(MatchMode.START.matches("abc", "ab"));
		assertFalse(MatchMode.START.matches("abc", "bc"));
		assertFalse(MatchMode.START.matches(" ", ""));
		assertFalse(MatchMode.START.matches(null, "a"));
		assertTrue(MatchMode.END.matches("abc", "bc"));
		assertFalse(MatchMode.END.matches("abc", "ab"));
		assertTrue(MatchMode.ANY_WHERE.matches("abc", "b"));
		assertFalse(MatchMode.ANY_WHERE.matches("abc", "x"));
		assertTrue(MatchMode.REGEX.matches("abc", "a.c"));
		assertTrue(MatchMode.REGEX.matches("a[c", "a[c"));
		assertFalse(MatchMode.REGEX.matches("abc", "a.d"));
		assertTrue(MatchMode.WILDCARD.matches("abc", "a*"));
		assertTrue(MatchMode.WILDCARD.matches("test.properties", "*.properties"));
		assertFalse(MatchMode.WILDCARD.matches("abc", "b*"));
		assertFalse(MatchMode.WILDCARD.matches("", "*"));
		assertSame(MatchMode.WILDCARD, MatchMode.valueOf("WILDCARD"));
		assertEquals(5, MatchMode.values().length);
	}

}
