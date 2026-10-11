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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * NumberUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class NumberUtilsTest {

	@Test
	public void testIsInteger() {
		assertTrue(NumberUtils.isInteger("123"));
		assertTrue(NumberUtils.isInteger("-12"));
		assertTrue(NumberUtils.isInteger("+1"));
		assertTrue(NumberUtils.isInteger("12L"));
		assertTrue(NumberUtils.isInteger("12l"));
		assertTrue(NumberUtils.isInteger("7"));
		assertFalse(NumberUtils.isInteger("1.2"));
		assertFalse(NumberUtils.isInteger("1a"));
		assertFalse(NumberUtils.isInteger("a1"));
		assertFalse(NumberUtils.isInteger("L"));
		assertFalse(NumberUtils.isInteger("-"));
		assertFalse(NumberUtils.isInteger(""));
		assertFalse(NumberUtils.isInteger(null));
		assertTrue(NumberUtils.isNotInteger("x"));
	}

	@Test
	public void testIsNumber() {
		assertTrue(NumberUtils.isNumber("1"));
		assertTrue(NumberUtils.isNumber("1.5"));
		assertTrue(NumberUtils.isNumber("-1.5e10"));
		assertTrue(NumberUtils.isNumber("1e+5"));
		assertTrue(NumberUtils.isNumber(".5"));
		assertTrue(NumberUtils.isNumber("1."));
		assertTrue(NumberUtils.isNumber("1.5f"));
		assertTrue(NumberUtils.isNumber("1.5D"));
		assertTrue(NumberUtils.isNumber("10L"));
		assertFalse(NumberUtils.isNumber("1.5L"));
		assertFalse(NumberUtils.isNumber("1.2.3"));
		assertFalse(NumberUtils.isNumber("1.2."));
		assertFalse(NumberUtils.isNumber("1e"));
		assertFalse(NumberUtils.isNumber("1e5e"));
		assertFalse(NumberUtils.isNumber("1e5.1"));
		assertFalse(NumberUtils.isNumber("e5"));
		assertFalse(NumberUtils.isNumber("1-"));
		assertFalse(NumberUtils.isNumber("1-2"));
		assertFalse(NumberUtils.isNumber("1x"));
		assertFalse(NumberUtils.isNumber("1x2"));
		assertFalse(NumberUtils.isNumber("1e+"));
		assertFalse(NumberUtils.isNumber("abc"));
		assertFalse(NumberUtils.isNumber(" "));
		assertFalse(NumberUtils.isNumber(null));
		assertTrue(NumberUtils.isNotNumber("x"));
	}

	@Test
	public void testIsHex() {
		assertTrue(NumberUtils.isHex("0x1F"));
		assertTrue(NumberUtils.isHex("-0X1f"));
		assertTrue(NumberUtils.isHex("#fff"));
		assertTrue(NumberUtils.isHex("0xaBc9"));
		assertFalse(NumberUtils.isHex("0x"));
		assertFalse(NumberUtils.isHex("123"));
		assertFalse(NumberUtils.isHex("0xG"));
		assertFalse(NumberUtils.isHex(null));
	}

	@Test
	public void testRead() {
		assertEquals("1.5", NumberUtils.read("+1.5"));
		assertEquals("0.5", NumberUtils.read(".5"));
		assertEquals("-0.5", NumberUtils.read("-.5"));
		assertEquals("10", NumberUtils.read("10L"));
		assertEquals("10", NumberUtils.read("10l"));
		assertEquals("1.5", NumberUtils.read("1.5f"));
		assertEquals("1.5", NumberUtils.read("1.5d"));
		assertEquals("1.5L", NumberUtils.read("1.5L"));
		assertEquals("-3", NumberUtils.read("-3"));
		assertEquals("", NumberUtils.read(""));
		assertEquals("", NumberUtils.read(null));
		assertThrows(NumberFormatException.class, () -> NumberUtils.read("+"));
		assertThrows(NumberFormatException.class, () -> NumberUtils.read("-"));
	}

	@Test
	public void testConvert() {
		assertEquals(Byte.valueOf((byte) 1), NumberUtils.toByte("1"));
		assertNull(NumberUtils.toByte("1000"));
		assertEquals(Short.valueOf((short) 1), NumberUtils.toShort("1"));
		assertEquals(Short.valueOf((short) 9), NumberUtils.toShort("x", (short) 9));
		assertEquals(Integer.valueOf(1), NumberUtils.toInteger("1"));
		assertNull(NumberUtils.toInteger(null));
		assertEquals(Long.valueOf(1), NumberUtils.toLong("1"));
		assertEquals(Long.valueOf(2), NumberUtils.toLong("x", 2L));
		assertEquals(Double.valueOf(1.5), NumberUtils.toDouble("1.5"));
		assertNull(NumberUtils.toDouble("x"));
		assertEquals(BigInteger.TEN, NumberUtils.toBigInteger("10"));
		assertNull(NumberUtils.toBigInteger("1.5"));
		assertEquals(new BigDecimal("1.5"), NumberUtils.toBigDecimal("1.5"));
		assertNull(NumberUtils.toBigDecimal(null));
	}

	@Test
	public void testPlainString() {
		assertEquals("100", NumberUtils.toPlainString(new BigDecimal("1E+2")));
		assertEquals("", NumberUtils.toPlainString(null));
		assertEquals("x", NumberUtils.toPlainString(null, "x"));
		assertArrayEquals(new String[] { "1", "2.5" }, NumberUtils.toStringArray(new Number[] { 1, 2.5 }, null));
		DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		assertArrayEquals(new String[] { "1.00" }, NumberUtils.toStringArray(new Number[] { 1 }, df));
		assertThrows(IllegalArgumentException.class, () -> NumberUtils.toStringArray(null, df));
	}

	@Test
	public void testFormat() {
		Locale defaultLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
		try {
			assertEquals("1.23", NumberUtils.format(1.234, 2));
			assertEquals("1", NumberUtils.format(1.0, 2));
			assertEquals("1.20", NumberUtils.format(1.2, -2));
			assertEquals("2", NumberUtils.format(1.7, 0));
			assertEquals("", NumberUtils.format(null, 2));
			assertEquals("1.50", NumberUtils.format(1.5));
			assertEquals("1.5", NumberUtils.format(1.5, "0.#"));
			assertEquals("-", NumberUtils.format(null, "0.#", "-"));
			DecimalFormat df = NumberUtils.getDecimalFormat("0", 1);
			assertEquals("1.2", df.format(1.23));
			assertNotSame(df, NumberUtils.getDecimalFormat("0", 1));
			assertEquals("3.1", NumberUtils.format(3.14, df));
			assertEquals("", NumberUtils.format(null, df));
			assertThrows(IllegalArgumentException.class, () -> NumberUtils.format(1, (DecimalFormat) null));
			assertArrayEquals(new String[] { "1.00", "" }, NumberUtils.formatMany(new Number[] { 1, null }));
			assertArrayEquals(new String[] { "1.0", null }, NumberUtils.formatMany(new Number[] { 1, null }, new DecimalFormat("0.0")));
			assertArrayEquals(new String[] { "2" }, NumberUtils.formatMany(new Number[] { 2 }, "0"));
			assertArrayEquals(new String[0], NumberUtils.formatMany(null));
		} finally {
			Locale.setDefault(defaultLocale);
		}
	}

}
