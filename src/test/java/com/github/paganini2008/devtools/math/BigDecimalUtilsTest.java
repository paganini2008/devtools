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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * BigDecimalUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BigDecimalUtilsTest {

	private static BigDecimal d(String s) {
		return new BigDecimal(s);
	}

	private static void assertNumEquals(String expected, BigDecimal actual) {
		assertEquals(0, d(expected).compareTo(actual), "Expected " + expected + " but was " + actual);
	}

	private static final MathContext MC = new MathContext(3, RoundingMode.HALF_UP);

	@Test
	public void testValueOfArrays() {
		assertArrayEquals(new BigDecimal[] { d("1"), d("2") }, BigDecimalUtils.valueOf(new byte[] { 1, 2 }));
		assertArrayEquals(new BigDecimal[] { d("1"), d("2") }, BigDecimalUtils.valueOf(new short[] { 1, 2 }));
		assertArrayEquals(new BigDecimal[] { d("1"), d("2") }, BigDecimalUtils.valueOf(new int[] { 1, 2 }));
		assertArrayEquals(new BigDecimal[] { d("1"), d("2") }, BigDecimalUtils.valueOf(new long[] { 1, 2 }));
		assertArrayEquals(new BigDecimal[] { d("0.5") }, BigDecimalUtils.valueOf(new float[] { 0.5f }));
		assertArrayEquals(new BigDecimal[] { d("1.5") }, BigDecimalUtils.valueOf(new double[] { 1.5 }));
		assertArrayEquals(new BigDecimal[] { d("1"), null }, BigDecimalUtils.valueOf(new Number[] { 1, null }));
		assertArrayEquals(new BigDecimal[] { d("1.5"), null }, BigDecimalUtils.valueOf(new String[] { "1.5", "x" }));
		assertArrayEquals(new BigDecimal[] { BigDecimal.ZERO }, BigDecimalUtils.valueOf(new String[] { null }, BigDecimal.ZERO));
		assertThrows(IllegalArgumentException.class, () -> BigDecimalUtils.valueOf((double[]) null));
	}

	@Test
	public void testValueOf() {
		BigDecimal v = d("1.25");
		assertSame(v, BigDecimalUtils.valueOf(v));
		assertEquals(d("12"), BigDecimalUtils.valueOf(BigInteger.valueOf(12)));
		assertEquals(d("1.1"), BigDecimalUtils.valueOf(1.1f));
		assertEquals(d("1.1"), BigDecimalUtils.valueOf(1.1d));
		assertEquals(d("7"), BigDecimalUtils.valueOf(7));
		assertNull(BigDecimalUtils.valueOf((Number) null));
		assertNull(BigDecimalUtils.valueOf(Double.NaN));
		assertEquals(BigDecimal.ZERO, BigDecimalUtils.valueOf(Float.POSITIVE_INFINITY, BigDecimal.ZERO));
		assertEquals(BigDecimal.valueOf('A'), BigDecimalUtils.valueOf('A'));
		assertNull(BigDecimalUtils.valueOf((Character) null));
		assertEquals(BigDecimal.ONE, BigDecimalUtils.valueOf(Boolean.TRUE));
		assertEquals(BigDecimal.ZERO, BigDecimalUtils.valueOf(Boolean.FALSE));
		assertNull(BigDecimalUtils.valueOf((Boolean) null));
		assertEquals(d("1.5"), BigDecimalUtils.valueOf("1.5"));
		assertNull(BigDecimalUtils.valueOf("x"));
		assertNull(BigDecimalUtils.valueOf(""));
	}

	@Test
	public void testParse() {
		BigDecimalUtils.clearCache();
		assertEquals(d("1.50"), BigDecimalUtils.parse("1.50"));
		assertSame(BigDecimalUtils.parse("1.50"), BigDecimalUtils.parse("1.50"));
		assertNumEquals("0.5", BigDecimalUtils.parse(".5"));
		assertNumEquals("-3", BigDecimalUtils.parse("-3"));
		assertThrows(NumberFormatException.class, () -> BigDecimalUtils.parse("1x"));
		assertThrows(IllegalArgumentException.class, () -> BigDecimalUtils.parse(null));

		assertArrayEquals(new BigDecimal[] { d("1"), d("2") }, BigDecimalUtils.parseMany(new String[] { "1", "2" }));
		assertThrows(NumberFormatException.class, () -> BigDecimalUtils.parseMany(new String[] { "x" }));
		assertArrayEquals(new BigDecimal[] { d("1"), d("2") }, BigDecimalUtils.parseMany(new String[] { "1", "x", "2" }, false));
	}

	@Test
	public void testCompare() {
		assertTrue(BigDecimalUtils.lt("1", "2"));
		assertTrue(BigDecimalUtils.lte("2", "2.0"));
		assertTrue(BigDecimalUtils.gt("3", "2"));
		assertTrue(BigDecimalUtils.gte("2", "2"));
		assertTrue(BigDecimalUtils.eq("2", "2.00"));
		assertTrue(BigDecimalUtils.lt(1, 2.5));
		assertTrue(BigDecimalUtils.lte(2, 2));
		assertTrue(BigDecimalUtils.gt(3L, 2));
		assertTrue(BigDecimalUtils.gte(2.0, 2));
		assertTrue(BigDecimalUtils.eq(2, d("2.00")));
		assertFalse(BigDecimalUtils.eq(2, 3));
		assertEquals(-1, Integer.signum(BigDecimalUtils.compareTo(1, 2)));
		assertEquals(1, Integer.signum(BigDecimalUtils.compareTo("2", "1")));
	}

	@Test
	public void testAggregate() {
		Number[] array = { 3, 1.5, d("2") };
		assertNumEquals("1.5", BigDecimalUtils.min(array));
		assertNumEquals("3", BigDecimalUtils.max(array));
		assertNumEquals("6.5", BigDecimalUtils.sum(array));
		assertEquals(d("2.17"), BigDecimalUtils.avg(array, 2));
		assertThrows(IllegalArgumentException.class, () -> BigDecimalUtils.sum(new Number[0]));
		assertThrows(IllegalArgumentException.class, () -> BigDecimalUtils.min(new Number[0]));
		assertThrows(IllegalArgumentException.class, () -> BigDecimalUtils.max(new Number[0]));
		assertThrows(IllegalArgumentException.class, () -> BigDecimalUtils.avg(null, 2));
	}

	@Test
	public void testRange() {
		assertTrue(BigDecimalUtils.between(d("1"), 1, 2));
		assertTrue(BigDecimalUtils.notBetween(d("2.1"), 1, 2));
		assertFalse(BigDecimalUtils.in(d("1"), 1, 2));
		assertTrue(BigDecimalUtils.notIn(d("2"), 1, 2));
		assertTrue(BigDecimalUtils.between(d("2"), "1", "2"));
		assertTrue(BigDecimalUtils.notBetween(d("0.9"), "1", "2"));
		assertTrue(BigDecimalUtils.in(d("1.5"), "1", "2"));
		assertTrue(BigDecimalUtils.notIn(d("1"), "1", "2"));
	}

	@Test
	public void testMinMax() {
		assertNumEquals("2", BigDecimalUtils.max(1, 2));
		assertNumEquals("1", BigDecimalUtils.max(1, null));
		assertNumEquals("2", BigDecimalUtils.max(null, 2));
		assertNumEquals("2", BigDecimalUtils.max("1", "2"));
		assertNumEquals("1", BigDecimalUtils.min(1, 2));
		assertNull(BigDecimalUtils.min(1, null));
		assertNull(BigDecimalUtils.min(null, 1));
		assertNumEquals("1", BigDecimalUtils.min("1", "2"));
	}

	@Test
	public void testPowAbs() {
		assertNumEquals("8", BigDecimalUtils.pow(2, 3));
		assertNumEquals("1.37E+3", BigDecimalUtils.pow(d("11.1"), 3, MC));
		assertNull(BigDecimalUtils.pow((Number) null, 3));
		assertNull(BigDecimalUtils.pow((Number) null, 3, MC));
		assertNumEquals("8", BigDecimalUtils.pow("2", 3));
		assertNumEquals("1.37E+3", BigDecimalUtils.pow("11.1", 3, MC));
		assertNumEquals("2", BigDecimalUtils.abs(-2));
		assertNull(BigDecimalUtils.abs((Number) null));
		assertNumEquals("2.5", BigDecimalUtils.abs("-2.5"));
	}

	@Test
	public void testAddSubtract() {
		assertNumEquals("3.5", BigDecimalUtils.add(1, 2.5));
		assertNumEquals("2.5", BigDecimalUtils.add(null, 2.5));
		assertNull(BigDecimalUtils.add((Number) null, null));
		assertNumEquals("3.5", BigDecimalUtils.add("1", "2.5"));
		assertNumEquals("1.23", BigDecimalUtils.add(1, 0.2345, MC));
		assertNumEquals("1", BigDecimalUtils.add(1, null, MC));
		assertNumEquals("1.23", BigDecimalUtils.add("1", "0.2345", MC));

		assertNumEquals("-1.5", BigDecimalUtils.subtract(1, 2.5));
		assertNumEquals("1", BigDecimalUtils.subtract(1, null));
		assertNumEquals("-2.5", BigDecimalUtils.subtract(null, 2.5));
		assertNull(BigDecimalUtils.subtract((Number) null, null));
		assertNumEquals("-1.5", BigDecimalUtils.subtract("1", "2.5"));
		assertNumEquals("0.766", BigDecimalUtils.subtract(1, 0.2345, MC));
		assertNumEquals("-0.2345", BigDecimalUtils.subtract(null, 0.2345, MC));
		assertNumEquals("0.766", BigDecimalUtils.subtract("1", "0.2345", MC));

		assertEquals("3.50", BigDecimalUtils.addAndFormat(1, 2.5, "0.00", "-"));
		assertEquals("3.50", BigDecimalUtils.addAndFormat("1", "2.5", "0.00", "-"));
		assertEquals("1.23", BigDecimalUtils.addAndFormat(1, 0.2345, MC, "0.00", "-"));
		assertEquals("1.23", BigDecimalUtils.addAndFormat("1", "0.2345", MC, "0.00", "-"));
		assertEquals("-", BigDecimalUtils.addAndFormat((Number) null, null, "0.00", "-"));
		assertEquals("-1.50", BigDecimalUtils.subtractAndFormat(1, 2.5, "0.00", "-"));
		assertEquals("-1.50", BigDecimalUtils.subtractAndFormat("1", "2.5", "0.00", "-"));
		assertEquals("0.77", BigDecimalUtils.subtractAndFormat(1, 0.2345, MC, "0.00", "-"));
		assertEquals("0.77", BigDecimalUtils.subtractAndFormat("1", "0.2345", MC, "0.00", "-"));
	}

	@Test
	public void testMultiplyDivide() {
		assertNumEquals("5", BigDecimalUtils.multiply(2, 2.5));
		assertNumEquals("2", BigDecimalUtils.multiply(2, null));
		assertNumEquals("5", BigDecimalUtils.multiply("2", "2.5"));
		assertNumEquals("1.11", BigDecimalUtils.multiply(1.111, 1, MC));
		assertNumEquals("1", BigDecimalUtils.multiply(null, 1, MC));
		assertNumEquals("1.11", BigDecimalUtils.multiply("1.111", "1", MC));
		assertEquals("5.00", BigDecimalUtils.multiplyAndFormat(2, 2.5, "0.00", "-"));
		assertEquals("5.00", BigDecimalUtils.multiplyAndFormat("2", "2.5", "0.00", "-"));
		assertEquals("1.11", BigDecimalUtils.multiplyAndFormat(1.111, 1, MC, "0.00", "-"));
		assertEquals("1.11", BigDecimalUtils.multiplyAndFormat("1.111", "1", MC, "0.00", "-"));

		assertNumEquals("0.333", BigDecimalUtils.divide(1, 3, MC));
		assertNumEquals("0.333", BigDecimalUtils.divide("1", "3", MC));
		assertNull(BigDecimalUtils.divide(1, 0, MC));
		assertNull(BigDecimalUtils.divide("1", "0", MC));
		assertNumEquals("1", BigDecimalUtils.divide(1, null, MC));
		assertNull(BigDecimalUtils.divide(null, 3, MC));
		assertEquals(d("0.67"), BigDecimalUtils.divide(2, 3, 2, RoundingMode.HALF_UP));
		assertEquals(d("0.67"), BigDecimalUtils.divide("2", "3", 2, RoundingMode.HALF_UP));
		assertNull(BigDecimalUtils.divide(2, 0, 2, RoundingMode.HALF_UP));
		assertNull(BigDecimalUtils.divide("2", "0", 2, RoundingMode.HALF_UP));
		assertNull(BigDecimalUtils.divide(null, 3, 2, RoundingMode.HALF_UP));
		assertEquals("0.33", BigDecimalUtils.divideAndFormat(1, 3, MC, "0.00", "-"));
		assertEquals("0.33", BigDecimalUtils.divideAndFormat("1", "3", MC, "0.00", "-"));
		assertEquals("0.67", BigDecimalUtils.divideAndFormat(2, 3, 2, RoundingMode.HALF_UP, "0.00", "-"));
		assertEquals("0.67", BigDecimalUtils.divideAndFormat("2", "3", 2, RoundingMode.HALF_UP, "0.00", "-"));
		assertEquals("-", BigDecimalUtils.divideAndFormat(1, 0, MC, "0.00", "-"));
	}

	@Test
	public void testRounding() {
		assertEquals(BigInteger.valueOf(3), BigDecimalUtils.round(2.5));
		assertEquals(BigInteger.valueOf(3), BigDecimalUtils.round("2.5"));
		assertEquals(d("2.35"), BigDecimalUtils.round(2.345, 2));
		assertEquals(d("2.35"), BigDecimalUtils.round("2.345", 2));
		assertEquals(BigInteger.valueOf(-3), BigDecimalUtils.floor(-2.1));
		assertEquals(BigInteger.valueOf(-3), BigDecimalUtils.floor("-2.1"));
		assertEquals(d("2.34"), BigDecimalUtils.floor(2.349, 2));
		assertEquals(d("2.34"), BigDecimalUtils.floor("2.349", 2));
		assertEquals(BigInteger.valueOf(3), BigDecimalUtils.ceil(2.1));
		assertEquals(BigInteger.valueOf(3), BigDecimalUtils.ceil("2.1"));
		assertEquals(d("2.35"), BigDecimalUtils.ceil(2.341, 2));
		assertEquals(d("2.35"), BigDecimalUtils.ceil("2.341", 2));
		assertEquals(BigInteger.valueOf(-2), BigDecimalUtils.down(-2.9));
		assertEquals(BigInteger.valueOf(-2), BigDecimalUtils.down("-2.9"));
		assertEquals(d("2.34"), BigDecimalUtils.down(2.349, 2));
		assertEquals(d("2.34"), BigDecimalUtils.down("2.349", 2));
		assertEquals(BigInteger.valueOf(-3), BigDecimalUtils.up(-2.1));
		assertEquals(BigInteger.valueOf(-3), BigDecimalUtils.up("-2.1"));
		assertEquals(d("2.35"), BigDecimalUtils.up(2.341, 2));
		assertEquals(d("2.35"), BigDecimalUtils.up("2.341", 2));
		assertEquals(BigInteger.valueOf(2), BigDecimalUtils.roundDown(2.5));
		assertEquals(BigInteger.valueOf(2), BigDecimalUtils.roundDown("2.5"));
		assertEquals(d("2.34"), BigDecimalUtils.roundDown(2.345, 2));
		assertEquals(d("2.34"), BigDecimalUtils.roundDown("2.345", 2));
		assertEquals(BigInteger.valueOf(2), BigDecimalUtils.roundEven(2.5));
		assertEquals(BigInteger.valueOf(4), BigDecimalUtils.roundEven("3.5"));
		assertEquals(d("2.34"), BigDecimalUtils.roundEven(2.345, 2));
		assertEquals(d("2.36"), BigDecimalUtils.roundEven("2.355", 2));
		assertNull(BigDecimalUtils.setScale((Number) null, RoundingMode.HALF_UP));
		assertNull(BigDecimalUtils.setScale((Number) null, 2, RoundingMode.HALF_UP));
		// a negative scale leaves the value untouched
		assertEquals(d("123.45"), BigDecimalUtils.setScale(d("123.45"), -1, RoundingMode.HALF_UP));
		assertEquals(d("123.45"), BigDecimalUtils.setScale("123.45", -1, RoundingMode.HALF_UP));
	}

	@Test
	public void testDivideAndRemainder() {
		assertArrayEquals(new BigDecimal[] { d("3"), d("1") }, BigDecimalUtils.divideAndRemainder(7, 2, MathContext.DECIMAL64));
		assertArrayEquals(new BigDecimal[] { d("3"), d("1") }, BigDecimalUtils.divideAndRemainder("7", "2", MathContext.DECIMAL64));
		assertArrayEquals(new BigDecimal[] { null, d("2") }, BigDecimalUtils.divideAndRemainder(null, 2, MathContext.DECIMAL64));
	}

	@Test
	public void testToPlainString() {
		assertEquals("1000", BigDecimalUtils.toPlainString("1E+3"));
		assertEquals("1000", BigDecimalUtils.toPlainString("1E+3"));
		assertEquals("0.5", BigDecimalUtils.toPlainString(".5"));
		assertEquals("", BigDecimalUtils.toPlainString("abc"));
		assertEquals("-", BigDecimalUtils.toPlainString((String) null, "-"));
		assertEquals("123", BigDecimalUtils.toPlainString(123));
		assertEquals("123", BigDecimalUtils.toPlainString(123L));
		assertEquals("0.00000015", BigDecimalUtils.toPlainString(1.5e-7));
		assertEquals("0.00000015", BigDecimalUtils.toPlainString(1.5e-7));
		assertEquals("", BigDecimalUtils.toPlainString((Number) null));
		assertEquals("-", BigDecimalUtils.toPlainString(Double.NaN, "-"));
	}

	@Test
	public void testPrecisionAndScale() {
		assertEquals(2, BigDecimalUtils.getPrecision(d("123.45")));
		assertEquals(3, BigDecimalUtils.getPrecision(d("0.00123")));
		assertEquals(2, BigDecimalUtils.getScale(d("1.2300")));
		assertEquals(0, BigDecimalUtils.getScale(d("100")));
	}

	@Test
	public void testMergeAndSum() {
		Map<String, Integer> m1 = new LinkedHashMap<>();
		m1.put("a", 1);
		m1.put("b", 2);
		Map<String, Integer> m2 = new LinkedHashMap<>();
		m2.put("b", 3);
		m2.put("c", 4);
		Map<String, BigDecimal> result = BigDecimalUtils.mergeAndSum(m1, m2);
		assertEquals(3, result.size());
		assertNumEquals("1", result.get("a"));
		assertNumEquals("5", result.get("b"));
		assertNumEquals("4", result.get("c"));
	}

}
