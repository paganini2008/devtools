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
package com.github.paganini2008.devtools.primitives;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * DoublesTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class DoublesTest {

	private static final double D = 0.0;

	@Test
	public void testBasics() {
		assertNull(Doubles.clone(null));
		double[] a = { 1, 2, 3 };
		assertNotSame(a, Doubles.clone(a));
		assertArrayEquals(a, Doubles.clone(a), D);
		assertEquals(0, Doubles.length(null));
		assertEquals(3, Doubles.length(a));
		assertTrue(Doubles.isFinite(1));
		assertFalse(Doubles.isFinite(Double.POSITIVE_INFINITY));
		assertFalse(Doubles.isFinite(Double.NaN));
		assertArrayEquals(new double[] { 0, 0 }, Doubles.create(2), D);
		assertArrayEquals(new double[] { 1.5, 1.5 }, Doubles.create(2, 1.5), D);
		assertArrayEquals(new double[] { 0, 0, 0 }, Doubles.create(2, 3)[1], D);
		assertArrayEquals(new double[] { 2, 2, 2 }, Doubles.create(2, 3, 2)[1], D);
		assertTrue(Doubles.isEmpty(null));
		assertTrue(Doubles.isNotEmpty(a));
		assertEquals(8, Doubles.BYTES);
		assertEquals(0, Doubles.EMPTY_ARRAY.length);
		assertEquals(0, Doubles.EMPTY_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		double[] a = { 1, 2, 3, 2 };
		assertEquals(1, Doubles.indexOf(a, 2));
		assertEquals(3, Doubles.indexOf(a, 2, 2, 100));
		assertEquals(-1, Doubles.indexOf(a, 2, -1, 4));
		assertEquals(-1, Doubles.indexOf(null, 2));
		assertEquals(3, Doubles.lastIndexOf(a, 2));
		assertEquals(1, Doubles.lastIndexOf(a, 2, 2));
		assertEquals(-1, Doubles.lastIndexOf(a, 9));
		assertEquals(-1, Doubles.lastIndexOf(null, 9));
		assertEquals(-1, Doubles.lastIndexOf(a, 2, -1));
		assertTrue(Doubles.contains(a, 3));
		assertTrue(Doubles.notContains(a, 4));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(new double[] { 1, 2, 3 }, Doubles.concat(new double[] { 1 }, new double[] { 2, 3 }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.concat(null, new double[] { 1 }));
		assertArrayEquals(new double[] { 1, 2 }, Doubles.add(new double[] { 1 }, 2), D);
		assertArrayEquals(new double[] { 1, 3 }, Doubles.remove(new double[] { 1, 2, 3 }, 2), D);
		double[] a = { 1, 2, 3 };
		assertSame(a, Doubles.remove(a, 5));
		assertArrayEquals(new double[] { 1, 2 }, Doubles.removeAt(a, -1), D);
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Doubles.removeAt(a, 3));
		assertArrayEquals(a, Doubles.copy(a), D);
		assertArrayEquals(new double[] { 1, 2 }, Doubles.copy(a, 2), D);
		assertArrayEquals(new double[] { 2, 3, 0 }, Doubles.copy(a, 1, 3), D);
		assertArrayEquals(new double[] { 3, 9 }, Doubles.copy(a, 2, 2, 9), D);
	}

	@Test
	public void testPrimitivesAndWrappers() {
		assertArrayEquals(new double[] { 1 }, Doubles.toPrimitives(new Byte[] { 1 }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.toPrimitives(new Byte[] { null }));
		assertArrayEquals(new double[] { 1 }, Doubles.toPrimitives(new Short[] { 1 }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.toPrimitives(new Short[] { null }));
		assertArrayEquals(new double[] { 1 }, Doubles.toPrimitives(new Integer[] { 1 }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.toPrimitives(new Integer[] { null }));
		assertArrayEquals(new double[] { 1 }, Doubles.toPrimitives(new Long[] { 1L }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.toPrimitives(new Long[] { null }));
		assertArrayEquals(new double[] { 1.5 }, Doubles.toPrimitives(new Float[] { 1.5f }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.toPrimitives(new Float[] { null }));
		assertArrayEquals(new double[] { 1.5 }, Doubles.toPrimitives(new Double[] { 1.5 }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.toPrimitives(new Double[] { null }));
		assertArrayEquals(new Double[] { 1d }, Doubles.toWrappers(new byte[] { 1 }));
		assertArrayEquals(new Double[] { 1d }, Doubles.toWrappers(new short[] { 1 }));
		assertArrayEquals(new Double[] { 1d }, Doubles.toWrappers(new int[] { 1 }));
		assertArrayEquals(new Double[] { 1d }, Doubles.toWrappers(new long[] { 1 }));
		assertArrayEquals(new Double[] { 1.5d }, Doubles.toWrappers(new float[] { 1.5f }));
		assertArrayEquals(new Double[] { 1.5d }, Doubles.toWrappers(new double[] { 1.5 }));
		assertArrayEquals(new Double[] { 97d }, Doubles.toWrappers(new char[] { 'a' }));
		assertArrayEquals(new double[] { 1 }, Doubles.casts(new byte[] { 1 }), D);
		assertArrayEquals(new double[] { 1 }, Doubles.casts(new short[] { 1 }), D);
		assertArrayEquals(new double[] { 1 }, Doubles.casts(new int[] { 1 }), D);
		assertArrayEquals(new double[] { 1 }, Doubles.casts(new long[] { 1 }), D);
		assertArrayEquals(new double[] { 1.5 }, Doubles.casts(new float[] { 1.5f }), D);
		assertArrayEquals(new double[] { 1, 0 }, Doubles.casts(new boolean[] { true, false }), D);
		assertArrayEquals(new double[] { 97 }, Doubles.casts(new char[] { 'a' }), D);
		assertEquals(1d, Doubles.cast(true), D);
		assertEquals(97d, Doubles.cast('a'), D);
		assertEquals(2.5d, Doubles.cast(new BigDecimal("2.5")), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.cast((Number) null));
		assertArrayEquals(new double[] { 1, 2.5 }, Doubles.casts(new Number[] { 1, 2.5f }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.casts(new Number[] { null }));
		assertArrayEquals(new double[] { 0, 2 }, Doubles.casts(new Number[] { null, 2 }, false), D);
	}

	@Test
	public void testCapacityAndAggregates() {
		double[] a = { 3, -1, 7 };
		assertSame(a, Doubles.ensureCapacity(a, 3));
		assertArrayEquals(new double[] { 3 }, Doubles.ensureCapacity(a, 1), D);
		assertArrayEquals(new double[] { 3, -1, 7, 0, 0, 0 }, Doubles.expandCapacity(a), D);
		assertArrayEquals(new double[] { 3, -1, 7, 0 }, Doubles.expandCapacity(a, 1), D);
		assertEquals(7, Doubles.max(a), D);
		assertEquals(-1, Doubles.min(a), D);
		assertEquals(9, Doubles.sum(a), D);
		assertEquals(3, Doubles.avg(a), D);
		assertEquals(5, Doubles.max(new double[] { Double.NaN, 5, 1 }), D);
		assertEquals(1, Doubles.min(new double[] { 5, Double.NaN, 1 }), D);
		assertEquals(2, Doubles.max(2, Double.NaN), D);
		assertEquals(2, Doubles.max(Double.NaN, 2), D);
		assertEquals(2, Doubles.min(2, Double.NaN), D);
		assertEquals(2, Doubles.min(Double.NaN, 2), D);
		assertEquals(1, Doubles.min(1, 2), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.max(null));
		assertThrows(IllegalArgumentException.class, () -> Doubles.min(new double[0]));
		assertThrows(IllegalArgumentException.class, () -> Doubles.sum(null));
	}

	@Test
	public void testJoin() {
		assertEquals("[1.0,2.5]", Doubles.toString(new double[] { 1, 2.5 }));
		assertEquals("1.02.5", Doubles.join(new double[] { 1, 2.5 }, null));
		assertEquals("", Doubles.join(null));
		assertEquals("1.0=3.0,2.0=4.0", Doubles.join(new double[] { 1, 2 }, new double[] { 3, 4 }, "=", ","));
		assertEquals("1.03.0", Doubles.join(new double[] { 1 }, new double[] { 3 }, null));
		assertEquals("", Doubles.join(null, new double[] { 3 }, ","));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 9, 10, 11, 64, 300 })
	public void testSort(int size) {
		Random random = new Random(size);
		double[] a = new double[size];
		for (int i = 0; i < size; i++) {
			a[i] = random.nextInt(100) - 50 + 0.5;
		}
		double[] expected = a.clone();
		Arrays.sort(expected);
		double[] asc = a.clone();
		Doubles.sort(asc, true);
		assertArrayEquals(expected, asc, D);
		double[] desc = a.clone();
		Doubles.sort(desc, false);
		Doubles.reverse(desc);
		assertArrayEquals(expected, desc, D);
	}

	@Test
	public void testSetOperations() {
		double[] left = { 0, 1, 2, 2 };
		double[] right = { 2, 3, 0 };
		assertArrayEquals(new double[] { 0, 1, 2, 2, 2, 3, 0 }, Doubles.unionAll(left, right), D);
		assertArrayEquals(new double[] { 0, 1, 2, 3 }, Doubles.union(left, right), D);
		assertArrayEquals(new double[] { 1 }, Doubles.minus(left, right), D);
		assertArrayEquals(new double[] { 0, 2, 2 }, Doubles.intersect(left, right), D);
		assertNull(Doubles.unionAll(null, null));
		assertSame(left, Doubles.unionAll(left, null));
		assertSame(right, Doubles.unionAll(null, right));
		assertNull(Doubles.union(null, null));
		assertSame(left, Doubles.union(left, null));
		assertSame(right, Doubles.union(null, right));
		assertNull(Doubles.minus(null, null));
		assertSame(left, Doubles.minus(left, null));
		assertSame(right, Doubles.minus(null, right));
		assertNull(Doubles.intersect(null, null));
		assertSame(left, Doubles.intersect(left, null));
		assertSame(right, Doubles.intersect(null, right));
	}

	@Test
	public void testCollectionsAndMisc() {
		assertArrayEquals(new double[] { 1, 0, 3.5 }, Doubles.toArray(Arrays.asList(1, "x", 3.5d)), D);
		assertEquals(Arrays.asList(1d, 2d), Doubles.toList(new double[] { 1, 2 }));
		double[] a = { 1, 2, 3 };
		Doubles.swap(a, 0, 2);
		assertArrayEquals(new double[] { 3, 2, 1 }, a, D);
		Doubles.reverse(null);
		double[] b = { 1, 2, 3, 4, 5 };
		Doubles.shuffle(b, new Random(3));
		Doubles.shuffle(b);
		Doubles.sort(b, true);
		assertArrayEquals(new double[] { 1, 2, 3, 4, 5 }, b, D);
		Doubles.shuffle(null);
		assertEquals(Double.hashCode(1.5), Doubles.hashCode(1.5));
		assertTrue(Doubles.deepEquals(a, a));
		assertTrue(Doubles.deepEquals(null, null));
		assertFalse(Doubles.deepEquals(null, a));
		assertFalse(Doubles.deepEquals(a, null));
		assertFalse(Doubles.deepEquals(a, new double[] { 3 }));
		assertFalse(Doubles.deepEquals(a, new double[] { 3, 2, 0 }));
		assertTrue(Doubles.deepEquals(a, new double[] { 3, 2, 1 }));
		assertEquals(Arrays.hashCode(a), Doubles.deepHashCode(a));
		assertEquals(0, Doubles.deepHashCode(null));
	}

	@Test
	public void testValueOf() {
		assertEquals(Double.valueOf(97), Doubles.valueOf('a'));
		assertNull(Doubles.valueOf((Character) null));
		assertEquals(Double.valueOf(1), Doubles.valueOf(Boolean.TRUE));
		assertNull(Doubles.valueOf((Boolean) null));
		assertEquals(Double.valueOf(1.5), Doubles.valueOf("1.5"));
		assertNull(Doubles.valueOf("abc"));
		assertEquals(Double.valueOf(-1), Doubles.valueOf("-", -1d));
		assertEquals(Double.valueOf(-1), Doubles.valueOf(" ", -1d));
		assertArrayEquals(new Double[] { 1d, null }, Doubles.valueOf(new String[] { "1", "x" }));
		assertEquals(Double.valueOf(3), Doubles.valueOf(3));
		assertNull(Doubles.valueOf((Number) null));
		assertArrayEquals(new Double[] { 1d, null }, Doubles.valueOf(new Number[] { 1, null }));
		assertArrayEquals(new Double[] { 1d, 0d }, Doubles.valueOf(new Number[] { 1, null }, 0d));
	}

	@ParameterizedTest
	@CsvSource({ "1.5,1.5", "-2,-2", "1e3,1000", "12L,12", ".5,0.5", "+.5,0.5", "-.5,-0.5" })
	public void testParse(String input, double expected) {
		Doubles.clearCache();
		assertEquals(expected, Doubles.parse(input), D);
		assertEquals(expected, Doubles.parse(input), D);
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "-", "+", "1..2", "--1" })
	public void testParseInvalid(String input) {
		assertThrows(IllegalArgumentException.class, () -> Doubles.parse(input));
	}

	@Test
	public void testParseMany() {
		assertArrayEquals(new double[] { 1, 2.5 }, Doubles.parseMany(new String[] { "1", "2.5" }), D);
		assertThrows(IllegalArgumentException.class, () -> Doubles.parseMany(new String[] { "x" }));
		assertArrayEquals(new double[] { 1, 3 }, Doubles.parseMany(new String[] { "1", "x", "3" }, false), D);
	}

	@Test
	public void testToFixedAndPlainString() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.GERMANY);
			assertEquals(1.23, Doubles.toFixed(1.2345, 2), D);
		} finally {
			Locale.setDefault(defaultLocale);
		}
		assertEquals(2.0, Doubles.toFixed(2.5, 0), D);
		assertEquals(4.0, Doubles.toFixed(3.5, 0), D);
		assertEquals(3.0, Doubles.toFixed(2.5, 0, RoundingMode.HALF_UP), D);
		assertEquals(1.2, Doubles.toFixed(1.29, 1, RoundingMode.DOWN), D);
		assertTrue(Double.isNaN(Doubles.toFixed(Double.NaN, 2)));
		assertEquals(Double.POSITIVE_INFINITY, Doubles.toFixed(Double.POSITIVE_INFINITY, 2, RoundingMode.UP), D);
		assertEquals("10000000000", Doubles.toPlainString(1e10));
		assertEquals("0.00010", Doubles.toPlainString(1e-4));
		assertEquals("NaN", Doubles.toPlainString(Double.NaN));
		assertEquals("-Infinity", Doubles.toPlainString(Double.NEGATIVE_INFINITY));
		assertArrayEquals(new String[] { "1.5", "10000000000" }, Doubles.toStringArray(new double[] { 1.5, 1e10 }, null));
		assertArrayEquals(new String[] { "1.50" },
				Doubles.toStringArray(new double[] { 1.5 }, new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ROOT))));
		assertNull(Doubles.toStringArray(null, null));
	}

	@Test
	public void testPredicates() {
		assertTrue(Doubles.isSameLength(null, new double[0]));
		assertFalse(Doubles.isSameLength(null, new double[1]));
		assertTrue(Doubles.isSameLength(new double[0], null));
		assertFalse(Doubles.isSameLength(new double[1], null));
		assertTrue(Doubles.isSameLength(new double[1], new double[1]));
		assertTrue(Doubles.same(new double[] { 4, 4 }));
		assertFalse(Doubles.same(new double[] { 4, 5 }));
		assertFalse(Doubles.same(null));
		assertTrue(Doubles.isSerial(new double[] { 1, 1.5, 2 }, 0.5));
		assertFalse(Doubles.isSerial(new double[] { 1, 2 }, 0.5));
		assertTrue(Doubles.isSubarray(new double[] { 1, 2, 3, 4 }, new double[] { 2, 3 }));
		assertFalse(Doubles.isSubarray(new double[] { 1, 2, 3 }, new double[] { 3, 4 }));
		assertFalse(Doubles.isSubarray(new double[] { 1, 2, 3 }, new double[] { 2, 4 }));
		assertFalse(Doubles.isSubarray(new double[] { 1 }, new double[] { 1, 2 }));
		assertTrue(Doubles.containsAll(new double[] { 1, 2 }, new double[] { 2, 2, 1 }));
		assertFalse(Doubles.containsAll(new double[] { 1, 2 }, new double[] { 3 }));
		assertFalse(Doubles.containsAll(null, new double[] { 3 }));
		double[] a = { 1, 2, 3, 4, 5 };
		Doubles.rightScroll(a, 1);
		assertArrayEquals(new double[] { 5, 1, 2, 3, 4 }, a, D);
		Doubles.leftScroll(a, 6);
		assertArrayEquals(new double[] { 1, 2, 3, 4, 5 }, a, D);
		assertTrue(Doubles.between(1, 1, 2));
		assertTrue(Doubles.between(2, 1, 2));
		assertFalse(Doubles.between(2.1, 1, 2));
		assertFalse(Doubles.in(1, 1, 2));
		assertTrue(Doubles.in(1.5, 1, 2));
		assertTrue(Doubles.rightIn(1, 1, 2));
		assertFalse(Doubles.rightIn(2, 1, 2));
		assertTrue(Doubles.leftIn(2, 1, 2));
		assertFalse(Doubles.leftIn(1, 1, 2));
		assertTrue(Doubles.compare(1, 2) < 0);
		assertEquals(0, Doubles.compare(2, 2));
		assertTrue(Doubles.defaultComparator().compare(new double[] { 1 }, new double[] { 2 }) < 0);
		assertTrue(Doubles.defaultComparator().compare(new double[] { 1, 2 }, new double[] { 1 }) > 0);
		assertEquals(0, Doubles.defaultComparator().compare(new double[] { 1, 2 }, new double[] { 1, 2 }));
	}

}
