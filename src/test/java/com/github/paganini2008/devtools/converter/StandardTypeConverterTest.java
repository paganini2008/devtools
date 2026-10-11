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
package com.github.paganini2008.devtools.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * StandardTypeConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class StandardTypeConverterTest {

	static class Point {
		final int x;

		Point(int x) {
			this.x = x;
		}
	}

	static class PointConverter extends BasicConverter<Point> {
		PointConverter() {
			registerType(String.class, (String s, Point d) -> new Point(Integer.parseInt(s)));
		}
	}

	@Test
	public void testBuiltinTypes() {
		StandardTypeConverter converter = new StandardTypeConverter();
		assertEquals(Integer.valueOf(12), converter.convertValue("12", Integer.class, null));
		assertEquals(Integer.valueOf(12), converter.convertValue("12", int.class, null));
		assertEquals(Long.valueOf(12L), converter.convertValue(12, long.class, null));
		assertEquals(Boolean.TRUE, converter.convertValue("true", boolean.class, null));
		assertEquals(new BigDecimal("1.5"), converter.convertValue("1.5", BigDecimal.class, null));
		assertEquals(BigInteger.TEN, converter.convertValue("10", BigInteger.class, null));
		assertEquals(StandardCharsets.UTF_8, converter.convertValue("UTF-8", java.nio.charset.Charset.class, null));
		UUID uuid = UUID.randomUUID();
		assertEquals(uuid, converter.convertValue(uuid.toString(), UUID.class, null));
		assertEquals("12", converter.convertValue(12, String.class, null));
		assertArrayEquals(new int[] { 1, 2, 3 }, converter.convertValue("1,2,3", int[].class, null));
		assertNotNull(converter.convertValue("en_US", Locale.class, null));
	}

	@Test
	public void testCastAndNull() {
		StandardTypeConverter converter = new StandardTypeConverter();
		String s = "abc";
		assertSame(s, converter.convertValue(s, CharSequence.class, null));
		assertEquals("dft", converter.convertValue(null, String.class, "dft"));
		// no converter registered for this type
		assertNull(converter.convertValue(1, StringBuilder.class, null));
	}

	@Test
	public void testRegistry() {
		StandardTypeConverter converter = new StandardTypeConverter();
		assertFalse(converter.hasType(Point.class));
		PointConverter pc = new PointConverter();
		converter.registerType(Point.class, pc);
		assertTrue(converter.hasType(Point.class));
		assertSame(pc, converter.lookupType(Point.class));
		assertEquals(7, converter.convertValue("7", Point.class, null).x);
		converter.removeType(Point.class);
		assertNull(converter.lookupType(Point.class));
		assertNull(converter.convertValue("7", Point.class, null));
		// instances do not share registrations
		assertFalse(new StandardTypeConverter().hasType(Point.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testConvertUtils() {
		assertEquals(Integer.valueOf(3), ConvertUtils.convertValue("3", Integer.class));
		assertNull(ConvertUtils.convertValue(null, Integer.class));
		assertEquals("3", ConvertUtils.convertValue("3", String.class));
		assertEquals("12", ConvertUtils.convertValue(12, String.class));
		assertArrayEquals(new int[] { 1, 2, 3 }, ConvertUtils.convertValue("1,2,3", int[].class));
		assertEquals("x", ConvertUtils.convertValue(null, String.class, "x"));
		assertEquals(Integer.valueOf(9), ConvertUtils.convertValue("abc", Integer.class, 9));
		assertNotNull(ConvertUtils.lookupType(Integer.class));
		assertNull(ConvertUtils.lookupType(Point.class));
		ConvertUtils.hasType(Point.class);
		ConvertUtils.registerType(Point.class, new PointConverter());
		try {
			assertNotNull(ConvertUtils.lookupType(Point.class));
			assertEquals(4, ConvertUtils.convertValue("4", Point.class).x);
		} finally {
			ConvertUtils.removeType(Point.class);
		}
		assertNull(ConvertUtils.lookupType(Point.class));
	}

}
