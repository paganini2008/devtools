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
 * IntsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class IntsTest {

	@Test
	public void testBasics() {
		assertNull(Ints.clone(null));
		int[] a = { 1, 2, 3 };
		int[] c = Ints.clone(a);
		assertNotSame(a, c);
		assertArrayEquals(a, c);
		assertEquals(0, Ints.length(null));
		assertEquals(3, Ints.length(a));
		assertArrayEquals(new int[] { 7, 7 }, Ints.create(2, 7));
		assertArrayEquals(new int[] { 0, 0 }, Ints.create(2, 0));
		int[][] grid = Ints.create(2, 3, 5);
		assertEquals(2, grid.length);
		assertArrayEquals(new int[] { 5, 5, 5 }, grid[1]);
		assertTrue(Ints.isEmpty(null));
		assertTrue(Ints.isEmpty(new int[0]));
		assertTrue(Ints.isNotEmpty(a));
		assertEquals(4, Ints.BYTES);
		assertEquals(1 << 30, Ints.MAX_POWER_OF_TWO);
		assertEquals(0, Ints.EMPTY_INT_ARRAY.length);
		assertEquals(0, Ints.EMPTY_INTEGER_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		int[] a = { 1, 2, 3, 2 };
		assertEquals(1, Ints.indexOf(a, 2));
		assertEquals(-1, Ints.indexOf(a, 9));
		assertEquals(-1, Ints.indexOf(null, 9));
		assertEquals(3, Ints.indexOf(a, 2, 2, 100));
		assertEquals(-1, Ints.indexOf(a, 2, -1, 4));
		assertEquals(3, Ints.lastIndexOf(a, 2));
		assertEquals(1, Ints.lastIndexOf(a, 2, 2));
		assertEquals(3, Ints.lastIndexOf(a, 2, 100));
		assertEquals(-1, Ints.lastIndexOf(a, 9));
		assertEquals(-1, Ints.lastIndexOf(null, 2));
		assertEquals(-1, Ints.lastIndexOf(a, 2, -1));
		assertTrue(Ints.contains(a, 3));
		assertTrue(Ints.notContains(a, 4));
		assertFalse(Ints.contains(null, 4));
	}

	@Test
	public void testConcatAddRemove() {
		assertArrayEquals(new int[] { 1, 2, 3 }, Ints.concat(new int[] { 1 }, new int[] { 2, 3 }));
		assertThrows(IllegalArgumentException.class, () -> Ints.concat(null, new int[] { 1 }));
		assertThrows(IllegalArgumentException.class, () -> Ints.concat(new int[] { 1 }, null));
		assertArrayEquals(new int[] { 1, 2 }, Ints.add(new int[] { 1 }, 2));
		assertArrayEquals(new int[] { 2 }, Ints.add(new int[0], 2));
		assertThrows(IllegalArgumentException.class, () -> Ints.add(null, 2));
		assertArrayEquals(new int[] { 1, 3 }, Ints.remove(new int[] { 1, 2, 3 }, 2));
		int[] a = { 1, 2 };
		assertSame(a, Ints.remove(a, 5));
		assertNull(Ints.remove(null, 5));
		assertArrayEquals(new int[] { 2, 3 }, Ints.removeAt(new int[] { 1, 2, 3 }, 0));
		assertArrayEquals(new int[] { 1, 2 }, Ints.removeAt(new int[] { 1, 2, 3 }, -1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Ints.removeAt(new int[] { 1 }, 1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Ints.removeAt(new int[] { 1 }, -2));
		assertThrows(IllegalArgumentException.class, () -> Ints.removeAt(null, 0));
	}

	@Test
	public void testCopy() {
		int[] a = { 1, 2, 3 };
		assertArrayEquals(a, Ints.copy(a));
		// copy(int[], int) takes a start index (unlike the other primitive classes)
		assertArrayEquals(new int[] { 3 }, Ints.copy(a, 2));
		assertArrayEquals(a, Ints.copy(a, 0));
		assertArrayEquals(new int[0], Ints.copy(a, 3));
		assertArrayEquals(new int[] { 2, 3 }, Ints.copy(a, 1, 2));
		assertArrayEquals(new int[] { 2, 3, 0 }, Ints.copy(a, 1, 3));
		assertArrayEquals(new int[] { 3, 9, 9 }, Ints.copy(a, 2, 3, 9));
		assertArrayEquals(new int[] { 9 }, Ints.copy(a, 5, 1, 9));
	}

	@Test
	public void testWrappers() {
		assertArrayEquals(new Integer[] { 1, -1 }, Ints.toWrappers(new byte[] { 1, -1 }));
		assertArrayEquals(new Integer[] { 97 }, Ints.toWrappers(new char[] { 'a' }));
		assertArrayEquals(new Integer[] { 300 }, Ints.toWrappers(new short[] { 300 }));
		assertArrayEquals(new Integer[] { 1, 2 }, Ints.toWrappers(new int[] { 1, 2 }));
		assertArrayEquals(new int[] { 1, 3 }, Ints.toPrimitives(new Integer[] { 1, null, 3 }));
		assertThrows(IllegalArgumentException.class, () -> Ints.toPrimitives(null));
		assertThrows(IllegalArgumentException.class, () -> Ints.toWrappers((int[]) null));
	}

	@Test
	public void testCapacity() {
		int[] a = { 1, 2, 3 };
		assertSame(a, Ints.ensureCapacity(a, 3));
		assertArrayEquals(new int[] { 1 }, Ints.ensureCapacity(a, 1));
		assertArrayEquals(new int[] { 1, 2, 3 }, Ints.ensureCapacity(a, 10));
		assertArrayEquals(new int[] { 1, 2, 3, 0, 0, 0 }, Ints.expandCapacity(a));
		assertArrayEquals(new int[] { 1, 2, 3, 0 }, Ints.expandCapacity(a, 1));
		assertArrayEquals(new int[] { 1, 2, 3 }, Ints.expandCapacity(a, -1));
	}

	@Test
	public void testAggregates() {
		int[] a = { 3, -1, 7, 2 };
		assertEquals(7, Ints.max(a));
		assertEquals(-1, Ints.min(a));
		assertEquals(11, Ints.sum(a));
		assertEquals(2.75d, Ints.avg(a), 0.0001);
		assertThrows(IllegalArgumentException.class, () -> Ints.max(new int[0]));
		assertThrows(IllegalArgumentException.class, () -> Ints.min(null));
		assertThrows(IllegalArgumentException.class, () -> Ints.sum(null));
	}

	@Test
	public void testJoin() {
		assertEquals("[1,2,3]", Ints.toString(new int[] { 1, 2, 3 }));
		assertEquals("[]", Ints.toString(null));
		assertEquals("1|2", Ints.join(new int[] { 1, 2 }, "|"));
		assertEquals("12", Ints.join(new int[] { 1, 2 }, null));
		assertEquals("1=3,2=4", Ints.join(new int[] { 1, 2 }, new int[] { 3, 4, 5 }, "=", ","));
		assertEquals("1,3,2,4", Ints.join(new int[] { 1, 2 }, new int[] { 3, 4 }, ","));
		assertEquals("1324", Ints.join(new int[] { 1, 2 }, new int[] { 3, 4 }, null, null));
		assertEquals("", Ints.join(null, new int[] { 3, 4 }, ","));
		assertEquals("", Ints.join(new int[] { 1 }, new int[0], ","));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 2, 5, 9, 10, 11, 31, 100, 1000 })
	public void testSort(int size) {
		Random random = new Random(size);
		int[] a = new int[size];
		for (int i = 0; i < size; i++) {
			a[i] = random.nextInt(50) - 25;
		}
		int[] expected = a.clone();
		Arrays.sort(expected);
		int[] asc = a.clone();
		Ints.sort(asc, true);
		assertArrayEquals(expected, asc);
		int[] desc = a.clone();
		Ints.sort(desc, false);
		Ints.reverse(desc);
		assertArrayEquals(expected, desc);
		Ints.sort(null, true);
	}

	@Test
	public void testSetOperations() {
		int[] left = { 0, 1, 2, 2 };
		int[] right = { 2, 3, 0 };
		assertArrayEquals(new int[] { 0, 1, 2, 2, 2, 3, 0 }, Ints.unionAll(left, right));
		assertArrayEquals(new int[] { 0, 1, 2, 3 }, Ints.union(left, right));
		assertArrayEquals(new int[] { 1 }, Ints.minus(left, right));
		assertArrayEquals(new int[] { 0, 2, 2 }, Ints.intersect(left, right));
		assertNull(Ints.unionAll(null, null));
		assertSame(left, Ints.unionAll(left, null));
		assertSame(right, Ints.unionAll(null, right));
		assertNull(Ints.union(null, null));
		assertSame(left, Ints.union(left, null));
		assertSame(right, Ints.union(null, right));
		assertNull(Ints.minus(null, null));
		assertSame(right, Ints.minus(null, right));
		assertSame(left, Ints.minus(left, null));
		assertNull(Ints.intersect(null, null));
		assertSame(left, Ints.intersect(left, null));
		assertSame(right, Ints.intersect(null, right));
	}

	@Test
	public void testCollections() {
		assertArrayEquals(new int[] { 1, -1, 3 }, Ints.toArray(Arrays.asList(1, "x", 3.7d), -1));
		assertArrayEquals(new int[] { 0, 5 }, Ints.toArray(Arrays.asList(null, 5L), 0));
		assertEquals(Arrays.asList(1, 2), Ints.toList(new int[] { 1, 2 }));
		assertThrows(IllegalArgumentException.class, () -> Ints.toArray(null, 0));
	}

	@Test
	public void testReverseSwapShuffle() {
		int[] a = { 1, 2, 3, 4, 5 };
		Ints.reverse(a);
		assertArrayEquals(new int[] { 5, 4, 3, 2, 1 }, a);
		Ints.reverse(null);
		Ints.swap(a, 0, 4);
		assertArrayEquals(new int[] { 1, 4, 3, 2, 5 }, a);
		int[] b = { 1, 2, 3, 4, 5, 6, 7, 8 };
		Ints.shuffle(b, new Random(1));
		Ints.sort(b, true);
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }, b);
		Ints.shuffle(b);
		assertEquals(36, Ints.sum(b));
		Ints.shuffle(null);
	}

	@Test
	public void testEqualsAndHash() {
		assertEquals(Integer.hashCode(5), Ints.hashCode(5));
		int[] a = { 1, 2 };
		assertTrue(Ints.deepEquals(a, a));
		assertTrue(Ints.deepEquals(a, new int[] { 1, 2 }));
		assertFalse(Ints.deepEquals(a, new int[] { 1, 3 }));
		assertFalse(Ints.deepEquals(a, new int[] { 1 }));
		assertFalse(Ints.deepEquals(null, a));
		assertFalse(Ints.deepEquals(a, null));
		assertTrue(Ints.deepEquals(null, null));
		assertEquals(Arrays.hashCode(a), Ints.deepHashCode(a));
		assertEquals(0, Ints.deepHashCode(null));
	}

	@Test
	public void testValueOf() {
		assertEquals(Integer.valueOf(97), Ints.valueOf('a'));
		assertNull(Ints.valueOf((Character) null));
		assertEquals(Integer.valueOf(1), Ints.valueOf(Boolean.TRUE));
		assertEquals(Integer.valueOf(0), Ints.valueOf(Boolean.FALSE));
		assertNull(Ints.valueOf((Boolean) null));
		assertEquals(Integer.valueOf(12), Ints.valueOf("12"));
		assertNull(Ints.valueOf("abc"));
		assertEquals(Integer.valueOf(-1), Ints.valueOf(" ", -1));
		assertEquals(Integer.valueOf(-1), Ints.valueOf("-", -1));
		assertEquals(Integer.valueOf(-1), Ints.valueOf("+", -1));
		assertEquals(Integer.valueOf(-1), Ints.valueOf("1e20", -1));
		assertEquals(Integer.valueOf(-1), Ints.valueOf("3000000000", -1));
		assertArrayEquals(new Integer[] { 1, null, 3 }, Ints.valueOf(new String[] { "1", "x", "3" }));
		assertArrayEquals(new Integer[] { 1, 0 }, Ints.valueOf(new String[] { "1", null }, 0));
		Integer i = Integer.valueOf(1000);
		assertSame(i, Ints.valueOf(i));
		assertEquals(Integer.valueOf(3), Ints.valueOf(3.9d));
		assertNull(Ints.valueOf((Number) null));
		assertEquals(Integer.valueOf(-1), Ints.valueOf(Long.MAX_VALUE, -1));
		assertArrayEquals(new Integer[] { 1, null, null }, Ints.valueOf(new Number[] { 1L, Long.MAX_VALUE, null }));
		assertThrows(NullPointerException.class, () -> Ints.valueOf((Number[]) null));
	}

	@ParameterizedTest
	@CsvSource({ "12,12", "-12,-12", "0x1F,31", "#10,16", "-0x10,-16", "12L,12", "1.9,1", ".5,0", "-.5,0", "+7,7", "1e3,1000", "2.5e1,25",
			"12d,12" })
	public void testParse(String input, int expected) {
		Ints.clearCache();
		assertEquals(expected, Ints.parse(input));
		// second time from cache
		assertEquals(expected, Ints.parse(input));
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "0xFFFFFFFFFF", "--1", "1..2", "-", "+", "1e20", "3000000000" })
	public void testParseInvalid(String input) {
		assertThrows(IllegalArgumentException.class, () -> Ints.parse(input));
	}

	@Test
	public void testParseMany() {
		assertThrows(IllegalArgumentException.class, () -> Ints.parse(null));
		assertArrayEquals(new int[] { 1, 2 }, Ints.parseMany(new String[] { "1", "2" }));
		assertThrows(IllegalArgumentException.class, () -> Ints.parseMany(new String[] { "1", "x" }));
		assertArrayEquals(new int[] { 1, 3 }, Ints.parseMany(new String[] { "1", "x", "3" }, false));
	}

	@Test
	public void testCasts() {
		assertArrayEquals(new int[] { 1, -1 }, Ints.casts(new byte[] { 1, -1 }));
		assertArrayEquals(new int[] { 300 }, Ints.casts(new short[] { 300 }));
		assertArrayEquals(new int[] { 1, 0 }, Ints.casts(new boolean[] { true, false }));
		assertArrayEquals(new int[] { 97 }, Ints.casts(new char[] { 'a' }));
		assertArrayEquals(new int[] { 1, 2 }, Ints.casts(new long[] { 1, 2 }));
		assertThrows(NumberOverflowException.class, () -> Ints.casts(new long[] { 1, Long.MAX_VALUE }));
		// unconvertible elements are left as 0 when not thrown
		assertArrayEquals(new int[] { 1, 0, 3 }, Ints.casts(new long[] { 1, Long.MAX_VALUE, 3 }, false));
		assertArrayEquals(new int[] { 1, 2 }, Ints.casts(new float[] { 1.5f, 2.2f }));
		assertThrows(NumberOverflowException.class, () -> Ints.casts(new float[] { 1e20f }));
		assertArrayEquals(new int[] { 0, 1 }, Ints.casts(new float[] { 1e20f, 1f }, false));
		assertArrayEquals(new int[] { 1, -2 }, Ints.casts(new double[] { 1.5, -2.5 }));
		assertThrows(NumberOverflowException.class, () -> Ints.casts(new double[] { 1e20 }));
		assertArrayEquals(new int[] { 0, 2 }, Ints.casts(new double[] { 1e20, 2 }, false));
		assertArrayEquals(new int[] { 1, 0 }, Ints.casts(new Boolean[] { true, false }));
		assertThrows(NullPointerException.class, () -> Ints.casts(new Boolean[] { null }));
		assertArrayEquals(new int[] { 1 }, Ints.casts(new Boolean[] { null, true }, false));
		assertArrayEquals(new int[] { 97 }, Ints.casts(new Character[] { 'a' }));
		assertThrows(NullPointerException.class, () -> Ints.casts(new Character[] { null }));
		assertArrayEquals(new int[] { 98 }, Ints.casts(new Character[] { null, 'b' }, false));
		assertArrayEquals(new int[] { 1, 2, 3 }, Ints.casts(new Number[] { 1, 2L, new BigDecimal("3.3") }));
		assertThrows(NullPointerException.class, () -> Ints.casts(new Number[] { null }));
		assertArrayEquals(new int[] { 0, 0, 4 }, Ints.casts(new Number[] { null, BigInteger.TEN.pow(20), 4 }, false));
	}

	@Test
	public void testCast() {
		assertEquals(1, Ints.cast(true));
		assertEquals(97, Ints.cast('a'));
		assertEquals(5, Ints.cast(5L));
		assertThrows(NumberOverflowException.class, () -> Ints.cast(1L + Integer.MAX_VALUE));
		assertEquals(5, Ints.cast(new BigDecimal("5.9")));
		assertEquals(Integer.MIN_VALUE, Ints.cast(BigInteger.valueOf(Integer.MIN_VALUE)));
		assertThrows(NumberOverflowException.class, () -> Ints.cast(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE)));
		assertEquals(7, Ints.cast((Number) Integer.valueOf(7)));
		assertEquals(7, Ints.cast((Number) new BigDecimal("7")));
		assertEquals(7, Ints.cast((Number) BigInteger.valueOf(7)));
		assertThrows(NullPointerException.class, () -> Ints.cast((Number) null));
	}

	@Test
	public void testEvenOdd() {
		assertTrue(Ints.isEven(0));
		assertTrue(Ints.isEven(-2));
		assertTrue(Ints.isOdd(-3));
		assertTrue(Ints.isEvens(new int[] { 2, 4 }));
		assertFalse(Ints.isEvens(new int[] { 2, 3 }));
		assertFalse(Ints.isEvens(null));
		assertTrue(Ints.isOdds(new int[] { 1, 3 }));
		assertFalse(Ints.isOdds(new int[] { 1, 2 }));
		assertFalse(Ints.isOdds(null));
	}

	@Test
	public void testToStringArray() {
		assertArrayEquals(new String[] { "1", "22" }, Ints.toStringArray(new int[] { 1, 22 }, null));
		DecimalFormat df = new DecimalFormat("000", DecimalFormatSymbols.getInstance(Locale.ROOT));
		assertArrayEquals(new String[] { "001", "022" }, Ints.toStringArray(new int[] { 1, 22 }, df));
		assertNull(Ints.toStringArray(null, df));
	}

	@Test
	public void testArrayPredicates() {
		assertTrue(Ints.isSameLength(null, new int[0]));
		assertTrue(Ints.isSameLength(null, null));
		assertFalse(Ints.isSameLength(null, new int[1]));
		assertTrue(Ints.isSameLength(new int[0], null));
		assertFalse(Ints.isSameLength(new int[1], null));
		assertTrue(Ints.isSameLength(new int[2], new int[2]));
		assertFalse(Ints.isSameLength(new int[2], new int[3]));
		assertTrue(Ints.same(new int[] { 4, 4, 4 }));
		assertFalse(Ints.same(new int[] { 4, 5 }));
		assertFalse(Ints.same(null));
		assertTrue(Ints.isSerial(new int[] { 1, 2, 3 }));
		assertFalse(Ints.isSerial(new int[] { 1, 3 }));
		assertTrue(Ints.isSerial(new int[] { 1, 3, 5 }, 2));
		assertTrue(Ints.isSubarray(new int[] { 1, 2, 3, 4 }, new int[] { 2, 3 }));
		assertTrue(Ints.isSubarray(new int[] { 1, 2, 1, 3 }, new int[] { 1, 3 }));
		assertFalse(Ints.isSubarray(new int[] { 1, 2, 3 }, new int[] { 3, 4 }));
		assertFalse(Ints.isSubarray(new int[] { 1, 2, 3 }, new int[] { 2, 4 }));
		assertFalse(Ints.isSubarray(new int[] { 1 }, new int[] { 1, 2 }));
		assertFalse(Ints.isSubarray(null, new int[] { 1 }));
		assertTrue(Ints.containsAll(new int[] { 1, 2, 3 }, new int[] { 3, 1 }));
		assertTrue(Ints.containsAll(new int[] { 1, 2 }, new int[] { 1, 1, 1 }));
		assertFalse(Ints.containsAll(new int[] { 1, 2, 3 }, new int[] { 4 }));
		assertFalse(Ints.containsAll(new int[] { 1 }, null));
	}

	@Test
	public void testScroll() {
		int[] a = { 1, 2, 3, 4, 5 };
		Ints.rightScroll(a, 2);
		assertArrayEquals(new int[] { 4, 5, 1, 2, 3 }, a);
		Ints.leftScroll(a, 2);
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, a);
		Ints.leftScroll(a, 7);
		assertArrayEquals(new int[] { 3, 4, 5, 1, 2 }, a);
		Ints.rightScroll(a, 5);
		assertArrayEquals(new int[] { 3, 4, 5, 1, 2 }, a);
		Ints.leftScroll(a, 0);
		Ints.rightScroll(null, 1);
		assertArrayEquals(new int[] { 3, 4, 5, 1, 2 }, a);
	}

	@Test
	public void testCompare() {
		assertTrue(Ints.compare(Integer.MIN_VALUE, 1) < 0);
		assertTrue(Ints.compare(Integer.MAX_VALUE, -1) > 0);
		assertEquals(0, Ints.compare(3, 3));
		assertTrue(Ints.defaultComparator().compare(new int[] { 1, 2 }, new int[] { 1, 3 }) < 0);
		assertTrue(Ints.defaultComparator().compare(new int[] { 1, 2, 0 }, new int[] { 1, 2 }) > 0);
		assertEquals(0, Ints.defaultComparator().compare(new int[] { 1, 2 }, new int[] { 1, 2 }));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, -1, 5, -5, 123456, Integer.MIN_VALUE, Integer.MAX_VALUE, 0x55AA55AA })
	public void testBits(int n) {
		assertEquals(String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0'), Ints.toBinaryString(n));
		assertEquals(n, Ints.parseBinaryString(Ints.toBinaryString(n)));
		for (int amount : new int[] { 0, 1, 7, 31, 33 }) {
			assertEquals(n << amount, Ints.shiftLeft(n, amount));
			assertEquals(n >> amount, Ints.shiftRight(n, amount));
			assertEquals(n >>> amount, Ints.unsignedShiftRight(n, amount));
		}
		int m = 0x0F0F1234;
		assertEquals(n & m, Ints.and(n, m));
		assertEquals(n | m, Ints.or(n, m));
		assertEquals(n ^ m, Ints.xor(n, m));
		assertEquals(~n, Ints.not(n));
	}

	@Test
	public void testParseBinaryString() {
		assertEquals(5, Ints.parseBinaryString("0101"));
		// a leading '1' is always treated as the sign bit
		assertEquals(-3, Ints.parseBinaryString("101"));
		assertThrows(NumberFormatException.class, () -> Ints.parseBinaryString("10" + Ints.toBinaryString(1)));
	}

}
