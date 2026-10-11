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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * RandomStringUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RandomStringUtilsTest {

	@Test
	public void testCharsetsContainBoundaryCharacters() {
		Set<Character> seen = new HashSet<Character>();
		for (char c : RandomStringUtils.randomString(5000).toCharArray()) {
			seen.add(c);
		}
		assertEquals(62, seen.size());
		assertTrue(seen.contains('z'));
		assertTrue(seen.contains('Z'));
		assertTrue(seen.contains('9'));
	}

	@Test
	public void testCharsetSelection() {
		assertTrue(RandomStringUtils.randomString(200, true, false, false).matches("[0-9]{200}"));
		assertTrue(RandomStringUtils.randomString(200, false, true, false).matches("[a-z]{200}"));
		assertTrue(RandomStringUtils.randomString(200, false, false, true).matches("[A-Z]{200}"));
		assertTrue(RandomStringUtils.randomString(200, true, true, false).matches("[0-9a-z]{200}"));
		assertTrue(RandomStringUtils.randomString(200, true, false, true).matches("[0-9A-Z]{200}"));
		assertTrue(RandomStringUtils.randomString(200, false, true, true).matches("[a-zA-Z]{200}"));
		assertTrue(RandomStringUtils.randomString(200, true, true, true).matches("[0-9a-zA-Z]{200}"));
		assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.randomString(1, false, false, false));
	}

	@Test
	public void testRandomStringWithExample() {
		assertEquals("aaa", RandomStringUtils.randomString(3, "a"));
		assertEquals("bbb", RandomStringUtils.randomString(3, "ab", 1));
		assertEquals("bb", RandomStringUtils.randomString(2, "abc", 1, 2));
		assertEquals("cc", RandomStringUtils.randomString(2, new char[] { 'a', 'c' }, 1));
		assertEquals("", RandomStringUtils.randomString(0, "abc"));
		assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.randomString(1, ""));
		assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.randomString(1, "abc", 2, 5));
	}

	@Test
	public void testRandomStrings() {
		String[] array = RandomStringUtils.randomStrings(10, 8);
		assertEquals(10, new HashSet<String>(Arrays.asList(array)).size());
		for (String s : array) {
			assertEquals(8, s.length());
		}
		array = RandomStringUtils.randomStrings(10, 1, true, false, false);
		Arrays.sort(array);
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]", Arrays.toString(array));
		array = RandomStringUtils.randomStrings(4, 2, "ab");
		Arrays.sort(array);
		assertEquals("[aa, ab, ba, bb]", Arrays.toString(array));
		array = RandomStringUtils.randomStrings(2, 1, "xab", 1);
		Arrays.sort(array);
		assertEquals("[a, b]", Arrays.toString(array));
		array = RandomStringUtils.randomStrings(1, 3, "xab", 1, 2);
		assertEquals("[aaa]", Arrays.toString(array));
		array = RandomStringUtils.randomStrings(2, 1, new char[] { 'q', 'r' });
		Arrays.sort(array);
		assertEquals("[q, r]", Arrays.toString(array));
	}

	@Test
	public void testRandomStringsRejectsImpossibleRequest() {
		assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.randomStrings(11, 1, true, false, false));
		assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.randomStrings(2, 3, "aaa"));
	}

}
