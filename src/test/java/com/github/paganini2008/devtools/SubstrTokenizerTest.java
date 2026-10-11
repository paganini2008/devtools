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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * SubstrTokenizerTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SubstrTokenizerTest {

	private static List<String> tokens(String content, String[] delims, boolean returnDelims) {
		SubstrTokenizer tokenizer = new SubstrTokenizer(content, delims, returnDelims);
		List<String> list = new ArrayList<>();
		int guard = 0;
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
			if (++guard > 100) {
				throw new IllegalStateException("Endless loop");
			}
		}
		return list;
	}

	@Test
	public void testTokens() {
		String[] delims = { ",", ";" };
		assertEquals(Arrays.asList("a", "b", "c"), tokens("a,b;;c", delims, false));
		assertEquals(Arrays.asList("a", ",", "b", ";", ";", "c"), tokens("a,b;;c", delims, true));
		assertEquals(Arrays.asList(",", "a", ","), tokens(",a,", new String[] { "," }, true));
		assertEquals(Arrays.asList("a"), tokens(",a,", new String[] { "," }, false));
		assertEquals(Arrays.asList("a"), tokens("a", new String[] { "," }, true));
		assertEquals(Arrays.asList("ab", "c", "ab"), tokens("abcab", new String[] { "ab" }, true));
		assertEquals(Arrays.asList("x", "::", "y", ",", "z"), tokens("x::y,z", new String[] { ",", "::" }, true));
		assertEquals(Arrays.asList("x", ",", "y", "::"), tokens("x,y::", new String[] { ",", "::" }, true));
		assertEquals(Arrays.asList("x", "y"), tokens("x,y", new String[] { ",", "::" }, false));
	}

	@Test
	public void testIllegalDelimiters() {
		assertEquals(Arrays.asList("a", "b"), tokens("a,b", new String[] { "", null, "," }, false));
		assertEquals(Arrays.asList("abc"), tokens("abc", new String[] { "" }, false));
		assertEquals(Arrays.asList("abc"), tokens("abc", null, false));
		assertThrows(IllegalArgumentException.class, () -> new SubstrTokenizer("", new String[] { "," }, false));
	}

	@Test
	public void testEnumeration() {
		SubstrTokenizer tokenizer = new SubstrTokenizer("a--b", new String[] { "--" }, false);
		assertNull(tokenizer.getLastElement());
		List<String> list = new ArrayList<>();
		while (tokenizer.hasMoreElements()) {
			list.add(tokenizer.nextElement());
			assertEquals(list.get(list.size() - 1), tokenizer.getLastElement());
		}
		assertEquals(Arrays.asList("a", "b"), list);
		assertFalse(tokenizer.hasMoreElements());
	}

}
