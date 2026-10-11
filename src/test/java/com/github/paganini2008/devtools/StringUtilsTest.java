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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * StringUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class StringUtilsTest {

	private static final String NL = StringUtils.NEWLINE;

	@Test
	public void testToString() {
		assertEquals("", StringUtils.toString(null));
		assertEquals("", StringUtils.toString(" "));
		assertEquals("a", StringUtils.toString("a"));
		assertEquals("d", StringUtils.toString(null, "d"));
	}

	@Test
	public void testToStringBuilder() {
		assertEquals("truefalse", StringUtils.toStringBuilder(new boolean[] { true, false }).toString());
		assertEquals("true" + NL + "false" + NL, StringUtils.toStringBuilder(new boolean[] { true, false }, true).toString());
		assertEquals("ab", StringUtils.toStringBuilder(new char[] { 'a', 'b' }).toString());
		assertEquals("a" + NL, StringUtils.toStringBuilder(new char[] { 'a' }, true).toString());
		assertEquals("12", StringUtils.toStringBuilder(new int[] { 1, 2 }).toString());
		assertEquals("1" + NL, StringUtils.toStringBuilder(new int[] { 1 }, true).toString());
		assertEquals("1.5", StringUtils.toStringBuilder(new float[] { 1.5f }).toString());
		assertEquals("1.5" + NL, StringUtils.toStringBuilder(new float[] { 1.5f }, true).toString());
		assertEquals("2.5", StringUtils.toStringBuilder(new double[] { 2.5 }).toString());
		assertEquals("2.5" + NL, StringUtils.toStringBuilder(new double[] { 2.5 }, true).toString());
		assertEquals("3", StringUtils.toStringBuilder(new long[] { 3L }).toString());
		assertEquals("3" + NL, StringUtils.toStringBuilder(new long[] { 3L }, true).toString());
		assertEquals("anull", StringUtils.toStringBuilder(new Object[] { "a", null }).toString());
		assertEquals("a" + NL, StringUtils.toStringBuilder(new Object[] { "a" }, true).toString());
		assertEquals("", StringUtils.toStringBuilder((boolean[]) null).toString());
		assertEquals("", StringUtils.toStringBuilder((char[]) null).toString());
		assertEquals("", StringUtils.toStringBuilder((int[]) null).toString());
		assertEquals("", StringUtils.toStringBuilder((float[]) null).toString());
		assertEquals("", StringUtils.toStringBuilder((double[]) null).toString());
		assertEquals("", StringUtils.toStringBuilder((long[]) null).toString());
		assertEquals("", StringUtils.toStringBuilder((Object[]) null).toString());
	}

	@Test
	public void testToLetter() {
		assertEquals("a", StringUtils.toLetter(0));
		assertEquals("z", StringUtils.toLetter(25));
		assertEquals("aa", StringUtils.toLetter(26));
		assertEquals("ab", StringUtils.toLetter(27));
		assertEquals("zz", StringUtils.toLetter(701));
		assertEquals("aaa", StringUtils.toLetter(702));
		assertEquals("", StringUtils.toLetter(-1));
	}

	@Test
	public void testReverseAndTrim() {
		assertEquals("cba", StringUtils.reverse("abc"));
		assertEquals("", StringUtils.reverse(null));
		assertEquals("", StringUtils.reverse(" "));
		assertEquals("a b  ", StringUtils.trimLeft("  a b  "));
		assertEquals("  a b", StringUtils.trimRight("  a b  "));
		assertEquals("", StringUtils.trimLeft("   "));
		assertEquals("", StringUtils.trimRight("   "));
		assertEquals("ab", StringUtils.trimLeft("ab"));
		assertEquals("ab", StringUtils.trimRight("ab"));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.trimLeft(null));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.trimRight(null));
	}

	@Test
	public void testEmptyAndBlank() {
		assertTrue(StringUtils.isEmpty(null));
		assertTrue(StringUtils.isEmpty(""));
		assertFalse(StringUtils.isEmpty(" "));
		assertTrue(StringUtils.isNotEmpty("a"));
		assertTrue(StringUtils.isBlank(null));
		assertTrue(StringUtils.isBlank(""));
		assertTrue(StringUtils.isBlank(" \t\n"));
		assertFalse(StringUtils.isBlank(" a "));
		assertTrue(StringUtils.isNotBlank("bob"));
	}

	@Test
	public void testJoin() {
		String[] arr = { "a", "b" };
		assertEquals("a,b", StringUtils.join(arr));
		assertEquals("a|b", StringUtils.join(arr, "|"));
		assertEquals("'a','b'", StringUtils.join(arr, "'", ","));
		assertEquals("[a]|[b]", StringUtils.join(arr, "[", "]", "|"));
		assertEquals("", StringUtils.join(null));
		assertEquals("", StringUtils.join(new String[0]));
	}

	@Test
	public void testRepeat() {
		assertEquals("aaa", StringUtils.repeat('a', 3));
		assertEquals("abab", StringUtils.repeat("ab", 2));
		assertEquals("ab,ab", StringUtils.repeat("ab", ",", 2));
		assertEquals("x", StringUtils.repeat("x", 1));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.repeat("x", 0));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.repeat(null, 2));
	}

	@Test
	public void testText() {
		assertEquals("   ab", StringUtils.textRight("ab", 5));
		assertEquals("**ab", StringUtils.textRight("ab", '*', 4));
		assertEquals("ab ", StringUtils.textRight("ab", 1, 3));
		assertEquals("*ab**", StringUtils.textRight("ab", 2, '*', 5));
		assertEquals("abcdef", StringUtils.textRight("abcdef", 3));
		assertEquals("ab   ", StringUtils.textLeft("ab", 5));
		assertEquals("ab**", StringUtils.textLeft("ab", '*', 4));
		assertEquals(" ab", StringUtils.textLeft("ab", 1, 3));
		assertEquals("**ab*", StringUtils.textLeft("ab", 2, '*', 5));
		assertEquals("abcdef", StringUtils.textLeft("abcdef", 3));
		assertEquals(" ab", StringUtils.textMiddle("ab", 3));
		assertEquals("*ab*", StringUtils.textMiddle("ab", '*', 4));
		assertEquals("***ab**", StringUtils.textMiddle("ab", '*', 7));
		assertEquals("**ab**", StringUtils.textMiddle("ab", '*', 6));
		assertEquals("abc", StringUtils.textMiddle("abc", 2));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.textLeft(null, 2));
	}

	@Test
	public void testCase() {
		assertEquals("HellO", StringUtils.toUpperCase("hello", 0, 4));
		assertEquals("hELLo", StringUtils.toLowerCase("HELLO", 0, 4));
		assertEquals("Abc", StringUtils.capitalize("abc"));
		assertEquals("aBC", StringUtils.uncapitalize("ABC"));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.capitalize(""));
		assertEquals("userNameId", StringUtils.toCamelCase("user_name_id"));
		assertEquals("aB", StringUtils.toCamelCase("a-b", "-"));
		assertEquals("user_name_id", StringUtils.toSegmentCase("userNameId"));
		assertEquals("user-name", StringUtils.toSegmentCase("userName", "-"));
	}

	@Test
	public void testInsertThumbSubstring() {
		assertEquals("aXbc", StringUtils.insert("abc", "X", 1));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.insert("abc", null, 1));
		assertEquals("abc...", StringUtils.thumb("abcdef", 3));
		assertEquals("ab", StringUtils.thumb("ab", 3));
		assertEquals("abc", StringUtils.substring("abcdef", 3));
		assertEquals("bc", StringUtils.substring("abc", 1, 10));
	}

	@Test
	public void testSplit() {
		assertEquals(Arrays.asList("a", "b", "c"), StringUtils.split("a b  c", " "));
		assertEquals(Arrays.asList("a", "b"), StringUtils.split("a\tb", (String) null));
		assertEquals(Arrays.asList("a", ",", "b"), StringUtils.split("a,b", ",", true));
		assertEquals(Arrays.asList("a", " ", "b"), StringUtils.split("a, ,b", ",", false));
		assertEquals(Arrays.asList("a", "b"), StringUtils.split("a, ,b", ",", false, false));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.split("", ","));

		String[] delims = { "::", "," };
		assertEquals(Arrays.asList("a", "b", "c"), StringUtils.split("a::b,,c", delims));
		assertEquals(Arrays.asList("a", "::", "b", ",", ",", "c"), StringUtils.split("a::b,,c", delims, true));
		assertEquals(Arrays.asList("a", "c"), StringUtils.split("a:: ,c", delims, false, false));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.split("a", (String[]) null));
	}

	@Test
	public void testSplitAsMap() {
		Map<String, String> expected = new LinkedHashMap<>();
		expected.put("a", "1");
		expected.put("b", "2");
		expected.put("c", null);
		assertEquals(expected, StringUtils.splitAsMap("a 1 b 2 c", " "));
		assertEquals(expected, StringUtils.splitAsMap("a 1 b 2 c", (String) null));
		assertEquals(expected, StringUtils.splitAsMap("a=1&b=2&c", "&", "="));
		assertEquals(expected, StringUtils.splitAsMap("a::1,b::2,c", new String[] { "::", "," }));
		assertEquals(expected, StringUtils.splitAsMap("a=1, b=2,c", new String[] { ", ", "," }, "="));

		Map<String, String> m = StringUtils.splitAsMap("=x&k=v", "&", "=");
		assertTrue(m.containsKey("=x"));
		assertNull(m.get("=x"));
		assertEquals("v", m.get("k"));
		Map<String, String> even = StringUtils.splitAsMap("a::1", new String[] { "::" });
		assertEquals(Collections.singletonMap("a", "1"), even);
	}

	@Test
	public void testEquals() {
		assertTrue(StringUtils.equals(null, null));
		assertTrue(StringUtils.equals("a", "a"));
		assertTrue(StringUtils.notEquals("a", null));
		assertTrue(StringUtils.equalsStrictly(null, " "));
		assertTrue(StringUtils.equalsStrictly("a", "a"));
		assertTrue(StringUtils.notEqualsStrictly("a", ""));
		assertTrue(StringUtils.equalsIgnoreCase("A", "a"));
		assertTrue(StringUtils.equalsIgnoreCase(null, null));
		assertTrue(StringUtils.notEqualsIgnoreCase("A", "b"));
		assertTrue(StringUtils.equalsIgnoreCaseStrictly("", null));
		assertTrue(StringUtils.equalsIgnoreCaseStrictly("A", "a"));
		assertTrue(StringUtils.notEqualsIgnoreCaseStrictly("A", " "));
		assertTrue(StringUtils.hasContent(null, "a"));
		assertTrue(StringUtils.hasContent("a", null));
		assertFalse(StringUtils.hasContent("a", "b"));
		assertFalse(StringUtils.hasContent(null, ""));
	}

	@Test
	public void testParseText() {
		assertEquals("Hello a, b", StringUtils.parseText("Hello ${0}, ${1}", "${", "}", "a", "b"));
		assertEquals("Hello ${0}", StringUtils.parseText("Hello ${0}", "${", "}", new Object[0]));

		Map<String, Object> params = new HashMap<>();
		params.put("name", "x");
		params.put("age", 3);
		assertEquals("x is 3", StringUtils.parseText("${name} is ${age}", "${", "}", params));
		assertEquals("${name}", StringUtils.parseText("${name}", "${", "}", new HashMap<String, Object>()));
		System.setProperty("devtools.test.stringutils", "sys");
		try {
			assertEquals("v=sys", StringUtils.parseText("v=${devtools.test.stringutils}", "${", "}", params));
		} finally {
			System.clearProperty("devtools.test.stringutils");
		}

		assertEquals("Hi x, age 3!", StringUtils.parseText("Hi :name, age :age!", ":", params));
		assertEquals("Hi :name", StringUtils.parseText("Hi :name", ":", new HashMap<String, Object>()));
		assertEquals("a and b", StringUtils.parseText(":0 and :1", ":", new Object[] { "a", "b" }));
		assertEquals(":0", StringUtils.parseText(":0", ":", new Object[0]));
		assertEquals(" ", StringUtils.parseText(" ", ":", new Object[] { "a" }));
		assertEquals(null, StringUtils.parseText(null, ":", new Object[] { "a" }));
		assertEquals("", StringUtils.parseText("", "${", "}", "a"));
		assertEquals(" ", StringUtils.parseText(" ", ":", params));
		assertEquals(" ", StringUtils.parseText(" ", "${", "}", params));
		assertEquals(" ", StringUtils.format(" ", "{}", 1));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.parseText("a", " ", new Object[] { "a" }));
	}

	@Test
	public void testParseTextSequentially() {
		Object[] args = { "a", "b" };
		assertEquals("a and b", StringUtils.parseText("{} and {}", "{}", args));
		assertEquals("b, a, b", StringUtils.parseText("{}1, {}, {}1", "{}", args));
		assertEquals("a b {}", StringUtils.parseText("{} {} {}", "{}", args));
		assertEquals("{}9 ax", StringUtils.parseText("{}9 {}x", "{}", new Object[] { "a" }));
		assertEquals("abac", StringUtils.parseText("{}0b{}0c", "{}", new Object[] { "a", "b" }));
		assertEquals("{} is a", StringUtils.parseText("\\{} is {}", "{}", new Object[] { "a" }));
		assertEquals("x=a, y=${name}, z=${9}", StringUtils.parseText("x=${0}, y=${name}, z=${9}", "${", "}", "a", "b"));
	}

	@Test
	public void testFormat() {
		assertEquals("1 + 2 = 2", StringUtils.format("{} + {} = {}", "{}", 1, 2));
		assertEquals("{} is 1", StringUtils.format("\\{} is {}", "{}", 1));
		assertEquals("{}", StringUtils.format("{}", "{}"));
	}

	@Test
	public void testUnicode() {
		assertEquals("中文", StringUtils.unicodeEscape("\\u4e2d\\u6587"));
		assertEquals("\\u4e2d\\u0041", StringUtils.unicodeString("中A"));
		assertEquals("", StringUtils.unicodeString(null));
	}

	@Test
	public void testAlphaNumeric() {
		assertTrue(StringUtils.isAlpha("abc"));
		assertFalse(StringUtils.isAlpha("ab1"));
		assertFalse(StringUtils.isAlpha(null));
		assertFalse(StringUtils.isAlpha(""));
		assertTrue(StringUtils.isNumeric("123"));
		assertFalse(StringUtils.isNumeric("12.3"));
		assertFalse(StringUtils.isNumeric(null));
		assertFalse(StringUtils.isNumeric(""));
	}

	@Test
	public void testCount() {
		assertEquals(2, StringUtils.count("abcabc", "bc"));
		assertEquals(1, StringUtils.count("aaa", "aa"));
		assertEquals(0, StringUtils.count("abc", ""));
		assertEquals(0, StringUtils.count(null, "a"));
		assertEquals(3, StringUtils.count("banana", 'a'));
		assertEquals(0, StringUtils.count("", 'a'));
		assertEquals(0, StringUtils.count(null, 'a'));
	}

	@Test
	public void testReplaceFirst() {
		assertEquals("a+b-c", StringUtils.replaceFirst("a-b-c", "-", "+"));
		assertEquals("abc", StringUtils.replaceFirst("abc", "-", "+"));
	}

	@Test
	public void testMessageFormat() {
		MessageFormat mf = StringUtils.getMessageFormat("{0} items");
		assertEquals("3 items", mf.format(new Object[] { 3 }));
		MessageFormat other = StringUtils.getMessageFormat("{0} items");
		assertNotSame(mf, other);
		assertEquals(mf.toPattern(), other.toPattern());
	}

	@Test
	public void testMatchesWildcard() {
		assertTrue(StringUtils.matchesWildcard("*", "x"));
		assertTrue(StringUtils.matchesWildcard("*", ""));
		assertTrue(StringUtils.matchesWildcard("a*", "abc"));
		assertFalse(StringUtils.matchesWildcard("a*", "ba"));
		assertTrue(StringUtils.matchesWildcard("*c", "abc"));
		assertFalse(StringUtils.matchesWildcard("*c", "abcd"));
		assertTrue(StringUtils.matchesWildcard("a*c", "abc"));
		assertTrue(StringUtils.matchesWildcard("a*c", "ac"));
		assertTrue(StringUtils.matchesWildcard("a*b*c", "axbyc"));
		assertFalse(StringUtils.matchesWildcard("a*b*c", "acb"));
		assertFalse(StringUtils.matchesWildcard("a*x*c", "abc"));
		assertFalse(StringUtils.matchesWildcard("ab*ab", "ab"));
		assertTrue(StringUtils.matchesWildcard("a**b", "ab"));
		assertTrue(StringUtils.matchesWildcard("*b*", "abc"));
		assertFalse(StringUtils.matchesWildcard("a*bc*bc", "abcbx"));
		assertTrue(StringUtils.matchesWildcard("abc", "abc"));
		assertFalse(StringUtils.matchesWildcard("abc", "abd"));
		assertFalse(StringUtils.matchesWildcard("a*", null));
		assertThrows(IllegalArgumentException.class, () -> StringUtils.matchesWildcard("", "a"));
	}

	@Test
	public void testMatchMany() {
		String[] array = { "apple", "banana", "apricot" };
		assertArrayEquals(new String[] { "apple", "apricot" }, StringUtils.matchMany(array, "ap", MatchMode.START));
		assertArrayEquals(new String[] { "banana" }, StringUtils.matchMany(array, "*an*", MatchMode.WILDCARD));
		assertArrayEquals(new String[] { "banana" }, StringUtils.matchMany(array, "na", MatchMode.END));
		assertArrayEquals(new String[0], StringUtils.matchMany(null, "a", MatchMode.START));
	}

}
