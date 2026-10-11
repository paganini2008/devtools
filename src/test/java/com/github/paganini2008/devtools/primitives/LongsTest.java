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
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.paganini2008.devtools.NumberOverflowException;

/**
 * LongsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LongsTest {

	@Test
	public void testBasics() {
		assertNull(Longs.clone(null));
		long[] a = { 1, 2, 3 };
		long[] c = Longs.clone(a);
		assertNotSame(a, c);
		assertArrayEquals(a, c);
		assertEquals(0, Longs.length(null));
		assertEquals(3, Longs.length(a));
		assertArrayEquals(new long[] { 7, 7 }, Longs.create(2, 7L));
		assertArrayEquals(new long[] { 0, 0 }, Longs.create(2, 0L));
		long[][] grid = Longs.create(2, 3, 5L);
		assertArrayEquals(new long[] { 5, 5, 5 }, grid[1]);
		assertTrue(Longs.isEmpty(null));
		assertTrue(Longs.isNotEmpty(a));
		assertEquals(8, Longs.BYTES);
		assertEquals(1L << 62, Longs.MAX_POWER_OF_TWO);
		assertEquals(0, Longs.EMPTY_LONG_ARRAY.length);
		assertEquals(0, Longs.EMPTY_LONG_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		long[] a = { 1, 2, 3, 2 };
		assertEquals(1, Longs.indexOf(a, 2));
		assertEquals(-1, Longs.indexOf(null, 9));
		assertEquals(3, Longs.indexOf(a, 2, 2, 100));
		assertEquals(-1, Longs.indexOf(a, 2, -1, 4));
		assertEquals(3, Longs.lastIndexOf(a, 2));
		assertEquals(1, Longs.lastIndexOf(a, 2, 2));
		assertEquals(-1, Longs.lastIndexOf(a, 9));
		assertEquals(-1, Longs.lastIndexOf(null, 2));
		assertEquals(-1, Longs.lastIndexOf(a, 2, -1));
		assertTrue(Longs.contains(a, 3));
		assertTrue(Longs.notContains(a, 4));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(new long[] { 1, 2, 3 }, Longs.concat(new long[] { 1 }, new long[] { 2, 3 }));
		assertThrows(IllegalArgumentException.class, () -> Longs.concat(null, new long[] { 1 }));
		assertArrayEquals(new long[] { 1, 2 }, Longs.add(new long[] { 1 }, 2));
		assertThrows(IllegalArgumentException.class, () -> Longs.add(null, 2));
		assertArrayEquals(new long[] { 1, 3 }, Longs.remove(new long[] { 1, 2, 3 }, 2));
		long[] a = { 1, 2, 3 };
		assertSame(a, Longs.remove(a, 5));
		assertArrayEquals(new long[] { 1, 2 }, Longs.removeAt(a, -1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Longs.removeAt(new long[] { 1 }, 1));
		assertArrayEquals(a, Longs.copy(a));
		assertArrayEquals(new long[] { 1, 2 }, Longs.copy(a, 2));
		assertArrayEquals(new long[] { 2, 3, 0 }, Longs.copy(a, 1, 3));
		assertArrayEquals(new long[] { 3, 9 }, Longs.copy(a, 2, 2, 9));
	}

	@Test
	public void testWrappers() {
		assertArrayEquals(new Long[] { 1L, -1L }, Longs.toWrappers(new byte[] { 1, -1 }));
		assertArrayEquals(new Long[] { 97L }, Longs.toWrappers(new char[] { 'a' }));
		assertArrayEquals(new Long[] { 1L, 0L }, Longs.toWrappers(new boolean[] { true, false }));
		assertArrayEquals(new Long[] { 300L }, Longs.toWrappers(new short[] { 300 }));
		assertArrayEquals(new Long[] { 5L }, Longs.toWrappers(new int[] { 5 }));
		assertArrayEquals(new Long[] { 5L }, Longs.toWrappers(new long[] { 5 }));
		assertArrayEquals(new Long[] { 1L, null }, Longs.valueOf(new Boolean[] { true, null }));
		assertArrayEquals(new Long[] { 1L, 9L }, Longs.valueOf(new Boolean[] { true, null }, 9L));
		assertArrayEquals(new Long[] { 97L, null }, Longs.valueOf(new Character[] { 'a', null }));
		assertArrayEquals(new Long[] { 97L, 9L }, Longs.valueOf(new Character[] { 'a', null }, 9L));
		assertArrayEquals(new long[] { 1, 2 }, Longs.toPrimitives(new Byte[] { 1, 2 }));
		assertThrows(IllegalArgumentException.class, () -> Longs.toPrimitives(new Byte[] { null }));
		assertArrayEquals(new long[] { 2 }, Longs.toPrimitives(new Byte[] { null, 2 }, false));
		assertArrayEquals(new long[] { 1 }, Longs.toPrimitives(new Short[] { 1 }));
		assertThrows(IllegalArgumentException.class, () -> Longs.toPrimitives(new Short[] { null }));
		assertArrayEquals(new long[] { 2 }, Longs.toPrimitives(new Short[] { null, 2 }, false));
		assertArrayEquals(new long[] { 97 }, Longs.toPrimitives(new Character[] { 'a' }));
		assertThrows(IllegalArgumentException.class, () -> Longs.toPrimitives(new Character[] { null }));
		assertArrayEquals(new long[] { 98 }, Longs.toPrimitives(new Character[] { null, 'b' }, false));
		assertArrayEquals(new long[] { 1 }, Longs.toPrimitives(new Integer[] { 1 }));
		assertThrows(IllegalArgumentException.class, () -> Longs.toPrimitives(new Integer[] { null }));
		assertArrayEquals(new long[] { 2 }, Longs.toPrimitives(new Integer[] { null, 2 }, false));
	}

	@Test
	public void testCapacity() {
		long[] a = { 1, 2, 3 };
		assertSame(a, Longs.ensureCapacity(a, 3));
		assertArrayEquals(new long[] { 1 }, Longs.ensureCapacity(a, 1));
		assertArrayEquals(new long[] { 1, 2, 3, 0, 0, 0 }, Longs.expandCapacity(a, 0));
		assertArrayEquals(new long[] { 1, 2, 3, 0 }, Longs.expandCapacity(a, 0, 1));
		assertArrayEquals(new long[] { 1, 2, 3 }, Longs.expandCapacity(a, 0, -1));
	}

	@Test
	public void testAggregatesAndJoin() {
		long[] a = { 3, -1, 7, Integer.MAX_VALUE };
		assertEquals(Integer.MAX_VALUE, Longs.max(a));
		assertEquals(-1, Longs.min(a));
		assertEquals(9L + Integer.MAX_VALUE, Longs.sum(a));
		assertEquals(2.5d, Longs.avg(new long[] { 2, 3 }), 0.0001);
		assertThrows(IllegalArgumentException.class, () -> Longs.max(null));
		assertThrows(IllegalArgumentException.class, () -> Longs.min(null));
		assertEquals("[1,2]", Longs.toString(new long[] { 1, 2 }));
		assertEquals("12", Longs.join(new long[] { 1, 2 }, null));
		assertEquals("", Longs.join(null));
		assertEquals("1=3,2=4", Longs.join(new long[] { 1, 2 }, new long[] { 3, 4 }, "=", ","));
		assertEquals("1324", Longs.join(new long[] { 1, 2 }, new long[] { 3, 4 }, null));
		assertEquals("", Longs.join(null, new long[] { 3 }, ","));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 9, 10, 11, 64, 500 })
	public void testSort(int size) {
		Random random = new Random(size);
		long[] a = new long[size];
		for (int i = 0; i < size; i++) {
			a[i] = random.nextLong() % 1000;
		}
		long[] expected = a.clone();
		Arrays.sort(expected);
		long[] asc = a.clone();
		Longs.sort(asc, true);
		assertArrayEquals(expected, asc);
		long[] desc = a.clone();
		Longs.sort(desc, false);
		Longs.reverse(desc);
		assertArrayEquals(expected, desc);
	}

	@Test
	public void testSetOperations() {
		long[] left = { 0, 1, 2, 2 };
		long[] right = { 2, 3, 0 };
		assertArrayEquals(new long[] { 0, 1, 2, 2, 2, 3, 0 }, Longs.unionAll(left, right));
		assertArrayEquals(new long[] { 0, 1, 2, 3 }, Longs.union(left, right));
		assertArrayEquals(new long[] { 1 }, Longs.minus(left, right));
		assertArrayEquals(new long[] { 0, 2, 2 }, Longs.intersect(left, right));
		assertNull(Longs.unionAll(null, null));
		assertSame(left, Longs.unionAll(left, null));
		assertSame(right, Longs.unionAll(null, right));
		assertNull(Longs.union(null, null));
		assertSame(left, Longs.union(left, null));
		assertSame(right, Longs.union(null, right));
		assertSame(right, Longs.minus(null, right));
		assertNull(Longs.minus(null, null));
		assertSame(left, Longs.minus(left, null));
		assertNull(Longs.intersect(null, null));
		assertSame(left, Longs.intersect(left, null));
		assertSame(right, Longs.intersect(null, right));
	}

	@Test
	public void testCollectionsAndMisc() {
		assertArrayEquals(new long[] { 1, 0, 3, 0 }, Longs.toArray(Arrays.asList(1, "x", 3.7d, null)));
		assertEquals(Arrays.asList(1L, 2L), Longs.toList(new long[] { 1, 2 }));
		long[] a = { 1, 2, 3 };
		Longs.swap(a, 0, 2);
		assertArrayEquals(new long[] { 3, 2, 1 }, a);
		Longs.reverse(null);
		long[] b = { 1, 2, 3, 4, 5 };
		Longs.shuffle(b, new Random(3));
		assertEquals(15, Longs.sum(b));
		Longs.shuffle(b);
		Longs.sort(b, true);
		assertArrayEquals(new long[] { 1, 2, 3, 4, 5 }, b);
		Longs.shuffle(null);
		assertEquals(Long.hashCode(1L << 40), Longs.hashCode(1L << 40));
		assertTrue(Longs.deepEquals(a, a));
		assertTrue(Longs.deepEquals(null, null));
		assertFalse(Longs.deepEquals(null, a));
		assertFalse(Longs.deepEquals(a, null));
		assertFalse(Longs.deepEquals(a, new long[] { 3 }));
		assertFalse(Longs.deepEquals(a, new long[] { 3, 2, 0 }));
		assertTrue(Longs.deepEquals(a, new long[] { 3, 2, 1 }));
		assertEquals(Arrays.hashCode(a), Longs.deepHashCode(a));
		assertEquals(0, Longs.deepHashCode(null));
	}

	@Test
	public void testValueOf() {
		assertEquals(Long.valueOf(97), Longs.valueOf('a'));
		assertNull(Longs.valueOf((Character) null));
		assertEquals(Long.valueOf(1), Longs.valueOf(Boolean.TRUE));
		assertNull(Longs.valueOf((Boolean) null));
		assertEquals(Long.valueOf(12), Longs.valueOf("12"));
		assertNull(Longs.valueOf("abc"));
		assertEquals(Long.valueOf(-1), Longs.valueOf("", -1L));
		assertEquals(Long.valueOf(-1), Longs.valueOf("1e30", -1L));
		assertArrayEquals(new Long[] { 1L, null }, Longs.valueOf(new String[] { "1", "x" }));
		Long l = Long.valueOf(1000);
		assertSame(l, Longs.valueOf(l));
		assertEquals(Long.valueOf(3), Longs.valueOf(3.9d));
		assertNull(Longs.valueOf((Number) null));
		assertEquals(Long.valueOf(Long.MAX_VALUE), Longs.valueOf(1e30, -1L));
		assertArrayEquals(new Long[] { 1L, null }, Longs.valueOf(new Number[] { 1, BigInteger.TEN.pow(30) }));
		assertThrows(NullPointerException.class, () -> Longs.valueOf((Number[]) null));
	}

	@ParameterizedTest
	@CsvSource({ "12,12", "-12,-12", "0x1F,31", "9223372036854775807,9223372036854775807", "12L,12", "1.9,1", "1e3,1000",
			"-9.9e1,-99" })
	public void testParse(String input, long expected) {
		Longs.clearCache();
		assertEquals(expected, Longs.parse(input));
		assertEquals(expected, Longs.parse(input));
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "-", "1..2", "1e30", "9223372036854775808" })
	public void testParseInvalid(String input) {
		assertThrows(IllegalArgumentException.class, () -> Longs.parse(input));
	}

	@Test
	public void testParseManyAndCasts() {
		assertArrayEquals(new long[] { 1, 2 }, Longs.parseMany(new String[] { "1", "2" }));
		assertThrows(IllegalArgumentException.class, () -> Longs.parseMany(new String[] { "x" }));
		assertArrayEquals(new long[] { 1, 3 }, Longs.parseMany(new String[] { "1", "x", "3" }, false));
		assertArrayEquals(new long[] { 1, 0 }, Longs.casts(new boolean[] { true, false }));
		assertArrayEquals(new long[] { 97 }, Longs.casts(new char[] { 'a' }));
		assertArrayEquals(new long[] { -1 }, Longs.casts(new byte[] { -1 }));
		assertArrayEquals(new long[] { 300 }, Longs.casts(new short[] { 300 }));
		assertArrayEquals(new long[] { 7 }, Longs.casts(new int[] { 7 }));
		assertArrayEquals(new long[] { 1, -2 }, Longs.casts(new float[] { 1.5f, -2.5f }));
		assertArrayEquals(new long[] { 1, 0 }, Longs.casts(new float[] { 1f, Float.NaN }, false));
		assertArrayEquals(new long[] { 1 }, Longs.casts(new double[] { 1.5 }));
		assertArrayEquals(new long[] { 2 }, Longs.casts(new double[] { 2 }, false));
		assertArrayEquals(new long[] { 1, 2, 3 }, Longs.casts(new Number[] { 1, 2L, new BigDecimal("3.3") }));
		assertThrows(NullPointerException.class, () -> Longs.casts(new Number[] { null }));
		assertThrows(NumberOverflowException.class, () -> Longs.casts(new Number[] { BigInteger.TEN.pow(20) }));
		assertArrayEquals(new long[] { 0, 0, 4 }, Longs.casts(new Number[] { null, BigInteger.TEN.pow(20), 4 }, false));
		assertEquals(1, Longs.cast(true));
		assertEquals(97, Longs.cast('a'));
		assertEquals(5, Longs.cast(new BigDecimal("5.9")));
		assertEquals(Long.MIN_VALUE, Longs.cast(BigInteger.valueOf(Long.MIN_VALUE)));
		assertThrows(NumberOverflowException.class, () -> Longs.cast(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
		assertEquals(7, Longs.cast((Number) Integer.valueOf(7)));
		assertEquals(7, Longs.cast((Number) 7.5f));
	}

	@Test
	public void testPredicates() {
		assertTrue(Longs.isEven(0));
		assertTrue(Longs.isOdd(-3));
		assertTrue(Longs.isEvens(new long[] { 2, 4 }));
		assertFalse(Longs.isEvens(new long[] { 2, 3 }));
		assertFalse(Longs.isEvens(null));
		assertTrue(Longs.isOdds(new long[] { 1, 3 }));
		assertFalse(Longs.isOdds(new long[] { 1, 2 }));
		assertFalse(Longs.isOdds(null));
		assertArrayEquals(new String[] { "1" }, Longs.toStringArray(new long[] { 1 }, null));
		assertArrayEquals(new String[] { "01" }, Longs.toStringArray(new long[] { 1 }, new DecimalFormat("00", DecimalFormatSymbols.getInstance(Locale.ROOT))));
		assertNull(Longs.toStringArray(null, null));
		assertTrue(Longs.isSameLength(null, new long[0]));
		assertFalse(Longs.isSameLength(null, new long[1]));
		assertTrue(Longs.isSameLength(new long[0], null));
		assertTrue(Longs.isSameLength(new long[1], new long[1]));
		assertTrue(Longs.same(new long[] { 4, 4 }));
		assertFalse(Longs.same(new long[] { 4, 5 }));
		assertTrue(Longs.isSerial(new long[] { 1, 2, 3 }));
		assertFalse(Longs.isSerial(null));
		assertTrue(Longs.isSubarray(new long[] { 1, 2, 3, 4 }, new long[] { 2, 3 }));
		assertFalse(Longs.isSubarray(new long[] { 1, 2, 3 }, new long[] { 3, 4 }));
		assertFalse(Longs.isSubarray(new long[] { 1, 2, 3 }, new long[] { 2, 4 }));
		assertFalse(Longs.isSubarray(new long[] { 1 }, new long[] { 1, 2 }));
		assertTrue(Longs.containsAll(new long[] { 1, 2 }, new long[] { 2, 2, 1 }));
		assertFalse(Longs.containsAll(new long[] { 1, 2 }, new long[] { 3 }));
		assertFalse(Longs.containsAll(null, new long[] { 3 }));
		long[] a = { 1, 2, 3, 4, 5 };
		Longs.rightScroll(a, 1);
		assertArrayEquals(new long[] { 5, 1, 2, 3, 4 }, a);
		Longs.leftScroll(a, 6);
		assertArrayEquals(new long[] { 1, 2, 3, 4, 5 }, a);
		assertTrue(Longs.compare(Long.MIN_VALUE, 1) < 0);
		assertTrue(Longs.compare(2, 1) > 0);
		assertEquals(0, Longs.compare(2, 2));
		assertTrue(Longs.defaultComparator().compare(new long[] { 1 }, new long[] { 2 }) < 0);
		assertTrue(Longs.defaultComparator().compare(new long[] { 1, 2 }, new long[] { 1 }) > 0);
	}

	@ParameterizedTest
	@ValueSource(longs = { 0, 1, -1, 5, -5, 123456789012L, Long.MIN_VALUE, Long.MAX_VALUE })
	public void testBits(long n) {
		String expected = String.format("%64s", Long.toBinaryString(n)).replace(' ', '0');
		assertEquals(expected, Longs.toBinaryString(n));
		assertEquals(n, Longs.parseBinaryString(expected));
		for (int amount : new int[] { 0, 1, 7, 63, 65 }) {
			assertEquals(n << amount, Longs.shiftLeft(n, amount));
			assertEquals(n >> amount, Longs.shiftRight(n, amount));
			assertEquals(n >>> amount, Longs.unsignedShiftRight(n, amount));
		}
		long m = 0x0F0F12340F0F1234L;
		assertEquals(n & m, Longs.and(n, m));
		assertEquals(n | m, Longs.or(n, m));
		assertEquals(n ^ m, Longs.xor(n, m));
		assertEquals(~n, Longs.not(n));
		assertThrows(NumberFormatException.class, () -> Longs.parseBinaryString("10" + expected));
	}

}
