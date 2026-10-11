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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * TokenParserTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TokenParserTest {

	@Test
	public void testSimpleTokenParser() {
		SimpleTokenParser parser = new SimpleTokenParser("{}");
		assertEquals("ax0bx1", parser.parse("a{}b{}", i -> "x" + i));
		assertEquals("ax0b", parser.parse("a{}b", i -> "x" + i));
		assertEquals("plain", parser.parse("plain", i -> "x"));
		assertEquals("{}x0", parser.parse("\\{}{}", i -> "x" + i));
		assertEquals("x0", parser.parse("{}", i -> "x" + i));
	}

	@Test
	public void testPrefixationTokenParser() {
		Map<String, Object> params = new HashMap<>();
		params.put("id", 1);
		params.put("name", "n");
		PrefixationTokenParser parser = new PrefixationTokenParser(":");
		assertEquals("select 1, n from t where x=:y", parser.parse("select :id, :name from t where x=\\:y", params::get));
		assertEquals("no token", parser.parse("no token", params::get));
		assertEquals("1", parser.parse(":id", params::get));

		PrefixationTokenParser dotted = new PrefixationTokenParser("$") {
			@Override
			protected boolean acceptCharacter(char ch) {
				return super.acceptCharacter(ch) || ch == '.';
			}
		};
		assertEquals("v=A.B;", dotted.parse("v=$a.b;", key -> key.toUpperCase()));
	}

	@Test
	public void testPlaceholderTokenParser() {
		Map<String, Object> params = new HashMap<>();
		params.put("a", 1);
		params.put("b", 2);
		PlaceholderTokenParser parser = new PlaceholderTokenParser("${", "}");
		assertEquals("1-2!", parser.parse("${a}-${b}!", params::get));
		assertEquals("${a} 1", parser.parse("\\${a} ${a}", params::get));
		assertEquals("x ${a", parser.parse("x ${a", params::get));
		assertEquals("x", parser.parse("${}x", params::get));
		assertEquals("plain", parser.parse("plain", params::get));
		System.setProperty("devtools.test.placeholder", "sys");
		try {
			assertEquals("sys", parser.parse("${devtools.test.placeholder}", params::get));
		} finally {
			System.clearProperty("devtools.test.placeholder");
		}
		PlaceholderTokenParser same = new PlaceholderTokenParser("%", "%");
		assertEquals("1+2", same.parse("%a%+%b%", params::get));
	}

}
