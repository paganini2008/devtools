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
package com.github.paganini2008.devtools.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * URLCodecUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class URLCodecUtilsTest {

	@Test
	public void encodeAndDecode_formEncoding() throws Exception {
		assertEquals("a+b%26c%3Dd", URLCodecUtils.encode("a b&c=d"));
		assertEquals("a b&c=d", URLCodecUtils.decode("a+b%26c%3Dd"));
		assertEquals("%E4%B8%AD%E6%96%87", URLCodecUtils.encode("中文"));
		assertEquals("中文", URLCodecUtils.decode("%E4%B8%AD%E6%96%87"));
		assertEquals("中文", URLCodecUtils.decode("%e4%b8%ad%e6%96%87"));
		assertEquals("", URLCodecUtils.encode(""));
		assertEquals("", URLCodecUtils.decode(""));
		String sample = "x=1&y=a b/c?d*e~f-g_h.i+j!'()中文😀";
		assertEquals(URLEncoder.encode(sample, "UTF-8"), URLCodecUtils.encode(sample));
		assertEquals(sample, URLCodecUtils.decode(URLCodecUtils.encode(sample)));
	}

	@Test
	public void encodeAndDecode_withCharset() {
		Charset gbk = Charset.forName("GBK");
		assertEquals("%D6%D0%CE%C4", URLCodecUtils.encode("中文", gbk));
		assertEquals("中文", URLCodecUtils.decode("%D6%D0%CE%C4", gbk));
		assertEquals("%E4%B8%AD", URLCodecUtils.encode("中", null));
		assertEquals("中", URLCodecUtils.decode("%E4%B8%AD", null));
		assertEquals("%D6%D0%20%CE%C4", URLCodecUtils.encodeComponent("中 文", gbk));
		assertEquals("中 文", URLCodecUtils.decodeComponent("%D6%D0%20%CE%C4", gbk));
	}

	@ParameterizedTest
	@ValueSource(strings = { "%G1", "%", "%4", "a%zz" })
	public void decode_rejectsIllegalEscapes(String str) {
		assertThrows(IllegalArgumentException.class, () -> URLCodecUtils.decode(str));
		assertThrows(IllegalArgumentException.class, () -> URLCodecUtils.decodeComponent(str));
		assertThrows(IllegalArgumentException.class, () -> URLCodecUtils.parseQuery("k=" + str));
	}

	@Test
	public void encodeComponent_rfc3986() {
		assertEquals("a%20b%2Bc", URLCodecUtils.encodeComponent("a b+c"));
		assertEquals("-_.~%2A", URLCodecUtils.encodeComponent("-_.~*"));
		assertEquals("%E4%B8%AD%E6%96%87", URLCodecUtils.encodeComponent("中文"));
		assertEquals("%2F%3F%23%5B%5D%40%21%24%26%27%28%29%2C%3B%3D%25", URLCodecUtils.encodeComponent("/?#[]@!$&'(),;=%"));
		assertEquals("%F0%9F%98%80", URLCodecUtils.encodeComponent("😀"));
		assertEquals("a%E4%B8%ADb%20c", URLCodecUtils.encodeComponent("a中b c"));
		assertEquals("", URLCodecUtils.encodeComponent(""));
		String unreserved = "ABCXYZabcxyz0189-_.~";
		assertSame(unreserved, URLCodecUtils.encodeComponent(unreserved));
		assertEquals("%E4%B8%AD", URLCodecUtils.encodeComponent("中", null));
	}

	@Test
	public void encodeComponent_crossCheckWithJdk() throws Exception {
		Random random = new Random(3);
		for (int n = 0; n < 500; n++) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 12; i++) {
				sb.appendCodePoint(random.nextBoolean() ? 32 + random.nextInt(95) : 0x4E00 + random.nextInt(500));
			}
			String s = sb.toString();
			String expected = URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
			String actual = URLCodecUtils.encodeComponent(s);
			assertEquals(expected, actual);
			assertEquals(s, URLCodecUtils.decodeComponent(actual));
			// the result is a valid URI path segment that the JDK decodes back
			assertEquals(s, new URI("http://h/" + actual).getPath().substring(1));
		}
	}

	@Test
	public void decodeComponent_keepsPlus() {
		assertEquals("a b+c", URLCodecUtils.decodeComponent("a%20b%2Bc"));
		assertEquals("a+b", URLCodecUtils.decodeComponent("a+b"));
		assertEquals("a b", URLCodecUtils.decode("a+b"));
	}

	@Test
	public void nullInputs() {
		assertNull(URLCodecUtils.encode(null));
		assertNull(URLCodecUtils.encode(null, StandardCharsets.UTF_8));
		assertNull(URLCodecUtils.decode(null));
		assertNull(URLCodecUtils.decode(null, StandardCharsets.UTF_8));
		assertNull(URLCodecUtils.encodeComponent(null));
		assertNull(URLCodecUtils.encodeComponent(null, StandardCharsets.UTF_8));
		assertNull(URLCodecUtils.decodeComponent(null));
		assertNull(URLCodecUtils.decodeComponent(null, StandardCharsets.UTF_8));
		assertNull(URLCodecUtils.encodeQuery(null));
		assertNull(URLCodecUtils.encodeQuery(null, StandardCharsets.UTF_8));
		assertTrue(URLCodecUtils.parseQuery(null).isEmpty());
		assertTrue(URLCodecUtils.parseQueryMulti(null).isEmpty());
	}

	@Test
	public void encodeQuery_keepsOrderAndHandlesNullValues() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("b", 2);
		params.put("a", 1);
		assertEquals("b=2&a=1", URLCodecUtils.encodeQuery(params));

		params.clear();
		params.put("a", 1);
		params.put("b", 2);
		assertEquals("a=1&b=2", URLCodecUtils.encodeQuery(params));

		params.clear();
		params.put("q", "a b&c");
		params.put("empty", null);
		params.put(null, "skipped");
		params.put("中", "文");
		assertEquals("q=a+b%26c&empty=&%E4%B8%AD=%E6%96%87", URLCodecUtils.encodeQuery(params));
		assertEquals("%D6%D0=%CE%C4", URLCodecUtils.encodeQuery(Collections.singletonMap("中", "文"), Charset.forName("GBK")));

		assertEquals("", URLCodecUtils.encodeQuery(new HashMap<String, Object>()));
		assertEquals("", URLCodecUtils.encodeQuery(Collections.singletonMap((String) null, "x")));
		Map<String, Object> leadingNull = new LinkedHashMap<String, Object>();
		leadingNull.put(null, "x");
		leadingNull.put("k", "v");
		assertEquals("k=v", URLCodecUtils.encodeQuery(leadingNull));
	}

	@Test
	public void parseQuery_firstValueWins() {
		Map<String, String> map = URLCodecUtils.parseQuery("?a=1&b=2&a=3");
		assertEquals(Arrays.asList("a", "b"), Arrays.asList(map.keySet().toArray()));
		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));

		map = URLCodecUtils.parseQuery("q=a+b%26c&flag");
		assertEquals("a b&c", map.get("q"));
		assertEquals("", map.get("flag"));

		map = URLCodecUtils.parseQuery("&&x=&=v&y=1=2&");
		assertEquals(3, map.size());
		assertEquals("", map.get("x"));
		assertEquals("v", map.get(""));
		assertEquals("1=2", map.get("y"));

		assertTrue(URLCodecUtils.parseQuery("").isEmpty());
		assertTrue(URLCodecUtils.parseQuery("?").isEmpty());
		assertTrue(URLCodecUtils.parseQuery("&").isEmpty());
		assertEquals("文", URLCodecUtils.parseQuery("%D6%D0=%CE%C4", Charset.forName("GBK")).get("中"));
		assertEquals("文", URLCodecUtils.parseQuery("%E4%B8%AD=%E6%96%87", null).get("中"));
	}

	@Test
	public void parseQueryMulti_keepsAllValues() {
		Map<String, List<String>> map = URLCodecUtils.parseQueryMulti("a=1&b=2&a=3");
		assertEquals(Arrays.asList("a", "b"), Arrays.asList(map.keySet().toArray()));
		assertEquals(Arrays.asList("1", "3"), map.get("a"));
		assertEquals(Arrays.asList("2"), map.get("b"));
		assertEquals(Arrays.asList("文"), URLCodecUtils.parseQueryMulti("%D6%D0=%CE%C4", Charset.forName("GBK")).get("中"));
		assertTrue(URLCodecUtils.parseQueryMulti("").isEmpty());
	}

	@Test
	public void encodeQueryThenParse_roundTrip() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("name", "张 三");
		params.put("expr", "1+1=2&3");
		params.put("none", null);
		LinkedHashMap<String, String> parsed = URLCodecUtils.parseQuery(URLCodecUtils.encodeQuery(params));
		assertEquals("张 三", parsed.get("name"));
		assertEquals("1+1=2&3", parsed.get("expr"));
		assertEquals("", parsed.get("none"));
		assertEquals(Arrays.asList("name", "expr", "none"), Arrays.asList(parsed.keySet().toArray()));
	}

	@Test
	public void largeInput() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20000; i++) {
			sb.append("k").append(i).append('=').append("v ").append(i).append('&');
		}
		Map<String, String> map = URLCodecUtils.parseQuery(sb.toString());
		assertEquals(20000, map.size());
		assertEquals("v 19999", map.get("k19999"));
		char[] chars = new char[100000];
		Arrays.fill(chars, '中');
		String s = new String(chars);
		assertEquals(s, URLCodecUtils.decodeComponent(URLCodecUtils.encodeComponent(s)));
	}

}
