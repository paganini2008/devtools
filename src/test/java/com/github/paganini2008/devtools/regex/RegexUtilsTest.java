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
package com.github.paganini2008.devtools.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * RegexUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RegexUtilsTest {

	@Test
	public void testGetPattern() {
		Pattern p = RegexUtils.getPattern("a+b");
		assertSame(p, RegexUtils.getPattern("a+b"));
		Pattern ci = RegexUtils.getPattern("a+b", Pattern.CASE_INSENSITIVE);
		assertEquals(Pattern.CASE_INSENSITIVE, ci.flags());
		assertSame(ci, RegexUtils.getPattern("a+b", Pattern.CASE_INSENSITIVE));
		// a single space is a valid regex
		assertEquals(" ", RegexUtils.getPattern(" ").pattern());
		assertThrows(IllegalArgumentException.class, () -> RegexUtils.getPattern(""));
		assertThrows(IllegalArgumentException.class, () -> RegexUtils.getPattern(null));
	}

	@Test
	public void testGetMatcher() {
		Matcher m = RegexUtils.getMatcher("xxab", "ab");
		assertTrue(m.find());
		m = RegexUtils.getMatcher("abab", "^ab", 0, 2);
		assertTrue(m.find());
		assertThrows(IllegalArgumentException.class, () -> RegexUtils.getMatcher(null, "a"));
	}

	@Test
	public void testMatches() {
		assertTrue(RegexUtils.matches("abc", "[a-c]+"));
		assertFalse(RegexUtils.matches("abcd", "[a-c]+"));
		assertTrue(RegexUtils.matches("xxabc", "[a-c]+", 2));
		assertTrue(RegexUtils.matches("ABC", "[a-c]+", Pattern.CASE_INSENSITIVE, 0));
		assertTrue(RegexUtils.matches("   ", "\\s+"));
		assertTrue(RegexUtils.matches("", "a*"));
		assertFalse(RegexUtils.matches(null, "a*"));
	}

	@Test
	public void testMatch() {
		assertEquals("123", RegexUtils.match("abc123def", "\\d+"));
		assertEquals("", RegexUtils.match("abc", "\\d+"));
		assertEquals("45", RegexUtils.match("12a45", "\\d+", 2));
		assertEquals("ABC", RegexUtils.match("xABC", "[a-c]+", Pattern.CASE_INSENSITIVE, 0));
		assertEquals("2021", RegexUtils.match("date: 2021-10", "(\\d{4})-(\\d{2})", 0, 1, 0));
		assertEquals("10", RegexUtils.match("date: 2021-10", "(\\d{4})-(\\d{2})", 0, 2, 0));
		assertNull(RegexUtils.match("b", "(a)?b", 0, 1, 0));
		assertEquals("", RegexUtils.match(null, "\\d+"));
	}

	@Test
	public void testFind() {
		assertTrue(RegexUtils.find("abc", "b", 0));
		assertFalse(RegexUtils.find("abc", "a", 1));
		assertTrue(RegexUtils.find("aBc", "b", Pattern.CASE_INSENSITIVE, 0));
		assertTrue(RegexUtils.notFind("abc", "x", 0));
		assertFalse(RegexUtils.notFind("abc", "c", 0));
		assertTrue(RegexUtils.notFind("abc", "B", 0, 0));
		assertFalse(RegexUtils.find(null, "a", 0));
		assertTrue(RegexUtils.notFind(null, "a", 0));
	}

	@Test
	public void testIndexOf() {
		assertEquals(3, RegexUtils.indexOf("abc123", "\\d+", 0));
		assertEquals(-1, RegexUtils.indexOf("abc", "\\d+", 0));
		assertEquals(4, RegexUtils.indexOf("1a2b3", "\\d", 3));
		// whitespace matches are real matches
		assertEquals(1, RegexUtils.indexOf("a  b", "\\s+", 0));
		// the index of the actual match, not of an earlier occurrence of the same text
		assertEquals(3, RegexUtils.indexOf("ac ab", "a(?=b)", 0));
		assertEquals(1, RegexUtils.indexOf("xAB", "ab", Pattern.CASE_INSENSITIVE, 0));
		assertEquals(-1, RegexUtils.indexOf(null, "a", 0));
		assertEquals(-1, RegexUtils.indexOf("abc", "a", 10));
		assertEquals(-1, RegexUtils.indexOf("abc", "a", -1));
	}

	@Test
	public void testSplit() {
		assertEquals(Arrays.asList("a", "b", "c"), RegexUtils.split("a,b,c", ","));
		assertEquals(Arrays.asList("a", "b", "c"), RegexUtils.split("a b  c", "\\s+"));
		assertEquals(Arrays.asList("a", "b", "c"), RegexUtils.split("aXbxc", "x", Pattern.CASE_INSENSITIVE));
		assertEquals(Arrays.asList("a", ",", "b"), RegexUtils.split("a,b", ",", 0, true));
		assertEquals(Collections.emptyList(), RegexUtils.split("", ","));
		assertEquals(Collections.emptyList(), RegexUtils.split(null, ","));
	}

	@Test
	public void testPredefinedPatterns() {
		assertTrue(RegexUtils.EMAIL_PATTERN.matcher("fred.feng@example.com").matches());
		assertFalse(RegexUtils.EMAIL_PATTERN.matcher("fred@").matches());
		assertTrue(RegexUtils.URL_PATTERN.matcher("http://www.example.com/a/b?c=1").matches());
		assertTrue(RegexUtils.ADDRESS_PATTERN.matcher("192.168.1.1:8080").matches());
		assertFalse(RegexUtils.ADDRESS_PATTERN.matcher("192.168.1.1").matches());
		assertTrue(RegexUtils.LOCAL_IP_PATTERN.matcher("127.0.0.1").matches());
		assertTrue(RegexUtils.IP_PATTERN.matcher("10.0.0.1").matches());
		assertTrue(RegexUtils.BLANK_PATTERN.matcher("　").matches() || RegexUtils.BLANK_PATTERN.matcher(" ").matches());
	}

}
