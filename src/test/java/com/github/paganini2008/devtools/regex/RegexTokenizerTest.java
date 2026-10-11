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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * RegexTokenizerTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RegexTokenizerTest {

	private static List<String> tokens(RegexTokenizer tokenizer) {
		List<String> list = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			list.add(tokenizer.nextElement());
		}
		return list;
	}

	private static List<String> tokens(String content, String regex, boolean returnDelims) {
		return tokens(new RegexTokenizer(content, regex, returnDelims));
	}

	@Test
	public void testWithoutDelimiters() {
		assertEquals(Arrays.asList("a", "b", "c"), tokens("a,b,c", ",", false));
		assertEquals(Arrays.asList("a", "b"), tokens(",a,,b,", ",", false));
		assertEquals(Arrays.asList("abc"), tokens("abc", ",", false));
		assertEquals(Collections.emptyList(), tokens(",", ",", false));
		assertEquals(Collections.emptyList(), tokens("", ",", false));
		assertEquals(Arrays.asList("a", "b", "c"), tokens("a b \t c", "\\s+", false));
		assertEquals(Arrays.asList("   "), tokens("   ", ",", false));
	}

	@Test
	public void testWithDelimiters() {
		assertEquals(Arrays.asList("a", ",", "b"), tokens("a,b", ",", true));
		assertEquals(Arrays.asList("a", ",", "b", ","), tokens("a,b,", ",", true));
		assertEquals(Arrays.asList(",", "a"), tokens(",a", ",", true));
		assertEquals(Arrays.asList("a", ",", ",", "b"), tokens("a,,b", ",", true));
		assertEquals(Arrays.asList(","), tokens(",", ",", true));
		assertEquals(Arrays.asList("1", " + ", "2"), tokens("1 + 2", "\\s*\\+\\s*", true));
	}

	@Test
	public void testMultipleRegexesUseEarliestMatch() {
		RegexTokenizer tokenizer = new RegexTokenizer("a;b,c", new String[] { ",", ";" }, true);
		assertEquals(Arrays.asList("a", ";", "b", ",", "c"), tokens(tokenizer));
		assertEquals("c", tokenizer.getLastElement());
		assertEquals(Arrays.asList("a;b,c"), tokens(new RegexTokenizer("a;b,c", (String[]) null, false)));
	}

	@Test
	public void testZeroLengthMatchesAreSkipped() {
		assertEquals(Arrays.asList("b", "c"), tokens("baac", "a*", false));
		assertEquals(Arrays.asList("ab cd"), tokens("ab cd", "\\b", false));
	}

	@Test
	public void testLookaround() {
		assertEquals(Arrays.asList("ac ", "b"), tokens("ac ab", "a(?=b)", false));
	}

	@Test
	public void testFlags() {
		RegexTokenizer tokenizer = new RegexTokenizer("aXbxc", "x", false);
		tokenizer.setFlags(Pattern.CASE_INSENSITIVE);
		assertNull(tokenizer.getLastElement());
		assertEquals(Arrays.asList("a", "b", "c"), tokens(tokenizer));
		assertFalse(tokenizer.hasMoreElements());
	}

	@Test
	public void testNullContent() {
		assertThrows(IllegalArgumentException.class, () -> new RegexTokenizer(null, ",", false));
	}

}
