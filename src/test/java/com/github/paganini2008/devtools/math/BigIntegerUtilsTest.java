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
package com.github.paganini2008.devtools.math;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/**
 * BigIntegerUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BigIntegerUtilsTest {

	private static BigInteger b(long v) {
		return BigInteger.valueOf(v);
	}

	@Test
	public void testConstants() {
		assertEquals(b(Byte.MAX_VALUE), BigIntegerUtils.BYTE_MAX_VALUE);
		assertEquals(b(Long.MIN_VALUE), BigIntegerUtils.LONG_MIN_VALUE);
		assertEquals(0, BigIntegerUtils.EMPTY_ARRAY.length);
	}

	@Test
	public void testRange() {
		assertTrue(BigIntegerUtils.between(b(5), 5, 10));
		assertTrue(BigIntegerUtils.between(b(10), 5, 10L));
		assertFalse(BigIntegerUtils.between(b(11), 5, 10));
		assertTrue(BigIntegerUtils.notBetween(b(4), 5, 10));
		assertFalse(BigIntegerUtils.in(b(5), 5, 10));
		assertTrue(BigIntegerUtils.in(b(6), 5, 10));
		assertTrue(BigIntegerUtils.notIn(b(10), 5, 10));
	}

	@Test
	public void testArithmetic() {
		assertEquals(b(3), BigIntegerUtils.add(1, 2));
		assertEquals(b(2), BigIntegerUtils.add(null, 2));
		assertEquals(b(1), BigIntegerUtils.add(1, null));
		assertNull(BigIntegerUtils.add((Number) null, null));
		assertEquals(b(3), BigIntegerUtils.add("1", "2"));
		assertEquals(b(6), BigIntegerUtils.multiply(2, 3));
		assertEquals(b(3), BigIntegerUtils.multiply(null, 3));
		assertEquals(b(6), BigIntegerUtils.multiply("2", "3"));
		assertEquals("1,000", BigIntegerUtils.addAndFormat(999, 1, "#,###", "-"));
		assertEquals("1,000", BigIntegerUtils.addAndFormat("999", "1", "#,###", "-"));
		assertEquals("-", BigIntegerUtils.addAndFormat((Number) null, null, "#,###", "-"));
		assertEquals("2,000", BigIntegerUtils.multiplyAndFormat(1000, 2, "#,###", "-"));
		assertEquals("2,000", BigIntegerUtils.multiplyAndFormat("1000", "2", "#,###", "-"));
	}

	@Test
	public void testEvenOdd() {
		assertTrue(BigIntegerUtils.isEven(b(4)));
		assertFalse(BigIntegerUtils.isEven(b(3)));
		assertTrue(BigIntegerUtils.isEven(b(-2)));
		assertFalse(BigIntegerUtils.isEven(null));
		assertTrue(BigIntegerUtils.isOdd(b(3)));
		assertTrue(BigIntegerUtils.isOdd(b(-3)));
		assertFalse(BigIntegerUtils.isOdd(b(4)));
		assertFalse(BigIntegerUtils.isOdd(null));
	}

	@Test
	public void testValueOfArrays() {
		BigInteger[] expected = { b(1), b(2) };
		assertArrayEquals(expected, BigIntegerUtils.valueOf(new byte[] { 1, 2 }));
		assertArrayEquals(expected, BigIntegerUtils.valueOf(new short[] { 1, 2 }));
		assertArrayEquals(expected, BigIntegerUtils.valueOf(new int[] { 1, 2 }));
		assertArrayEquals(expected, BigIntegerUtils.valueOf(new long[] { 1, 2 }));
		assertArrayEquals(expected, BigIntegerUtils.valueOf(new float[] { 1.9f, 2.1f }));
		assertArrayEquals(expected, BigIntegerUtils.valueOf(new double[] { 1.9, 2.1 }));
		assertArrayEquals(new BigInteger[] { b(1), null }, BigIntegerUtils.valueOf(new Number[] { 1, null }));
		assertArrayEquals(new BigInteger[] { b(1), b(0) }, BigIntegerUtils.valueOf(new Number[] { 1, null }, BigInteger.ZERO));
		assertArrayEquals(new BigInteger[] { b(1), null, null }, BigIntegerUtils.valueOf(new String[] { "1", "x", null }));
		assertThrows(IllegalArgumentException.class, () -> BigIntegerUtils.valueOf((int[]) null));
	}

	@Test
	public void testValueOf() {
		BigInteger big = new BigInteger("123456789012345678901234567890");
		assertSame(big, BigIntegerUtils.valueOf(big));
		assertEquals(b(12), BigIntegerUtils.valueOf(new BigDecimal("12.9")));
		assertEquals(b(7), BigIntegerUtils.valueOf(7));
		assertSame(BigIntegerUtils.valueOf(7), BigIntegerUtils.valueOf(7L));
		assertEquals(b(1000), BigIntegerUtils.valueOf(1000));
		assertEquals(b(-1), BigIntegerUtils.valueOf(-1));
		assertNull(BigIntegerUtils.valueOf((Number) null));
		assertEquals(b(65), BigIntegerUtils.valueOf('A'));
		assertNull(BigIntegerUtils.valueOf((Character) null));
		assertEquals(BigInteger.ONE, BigIntegerUtils.valueOf(Boolean.TRUE));
		assertEquals(BigInteger.ZERO, BigIntegerUtils.valueOf(Boolean.FALSE));
		assertNull(BigIntegerUtils.valueOf((Boolean) null));
		assertEquals(b(12), BigIntegerUtils.valueOf("12"));
		assertNull(BigIntegerUtils.valueOf("abc"));
		assertNull(BigIntegerUtils.valueOf(" "));
		assertEquals(b(-1), BigIntegerUtils.valueOf("abc", b(-1)));
	}

	@Test
	public void testDecode() {
		assertEquals(b(255), BigIntegerUtils.decode("0xff"));
		assertEquals(b(255), BigIntegerUtils.decode("0XFF"));
		assertEquals(b(-255), BigIntegerUtils.decode("-#ff"));
		assertEquals(b(8), BigIntegerUtils.decode("010"));
		assertEquals(b(0), BigIntegerUtils.decode("0"));
		assertEquals(b(-12), BigIntegerUtils.decode("-12"));
	}

	@Test
	public void testParse() {
		BigIntegerUtils.clearCache();
		assertEquals(b(123), BigIntegerUtils.parse("123"));
		assertSame(BigIntegerUtils.parse("123"), BigIntegerUtils.parse("123"));
		assertEquals(b(12), BigIntegerUtils.parse("12.7"));
		assertEquals(b(255), BigIntegerUtils.parse("0xFF"));
		assertEquals(b(-5), BigIntegerUtils.parse("-5"));
		assertThrows(NumberFormatException.class, () -> BigIntegerUtils.parse("1a"));
		assertThrows(IllegalArgumentException.class, () -> BigIntegerUtils.parse(""));
	}

	@Test
	public void testParseMany() {
		assertArrayEquals(new BigInteger[] { b(1), b(2) }, BigIntegerUtils.parseMany(new String[] { "1", "2" }));
		assertThrows(NumberFormatException.class, () -> BigIntegerUtils.parseMany(new String[] { "1", "x" }));
		// invalid elements are skipped without leaving null holes
		assertArrayEquals(new BigInteger[] { b(1), b(3) }, BigIntegerUtils.parseMany(new String[] { "1", "x", "3" }, false));
		assertArrayEquals(new BigInteger[0], BigIntegerUtils.parseMany(new String[] { "x" }, false));
	}

}
