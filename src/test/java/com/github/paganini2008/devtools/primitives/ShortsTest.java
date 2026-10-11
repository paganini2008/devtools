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
 * ShortsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ShortsTest {

	private static short[] s(int... values) {
		short[] result = new short[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = (short) values[i];
		}
		return result;
	}

	@Test
	public void testBasics() {
		assertNull(Shorts.clone(null));
		short[] a = s(1, 2, 3);
		assertNotSame(a, Shorts.clone(a));
		assertArrayEquals(a, Shorts.clone(a));
		assertEquals(0, Shorts.length(null));
		assertEquals(3, Shorts.length(a));
		assertArrayEquals(s(7, 7), Shorts.create(2, (short) 7));
		assertArrayEquals(s(0, 0), Shorts.create(2, (short) 0));
		assertArrayEquals(s(5, 5, 5), Shorts.create(2, 3, (short) 5)[1]);
		assertTrue(Shorts.isEmpty(null));
		assertTrue(Shorts.isNotEmpty(a));
		assertEquals(2, Shorts.BYTES);
		assertEquals(1 << 14, Shorts.MAX_POWER_OF_TWO);
		assertEquals(0, Shorts.EMPTY_SHORT_ARRAY.length);
		assertEquals(0, Shorts.EMPTY_SHORT_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		short[] a = s(1, 2, 3, 2);
		assertEquals(1, Shorts.indexOf(a, (short) 2));
		assertEquals(3, Shorts.indexOf(a, (short) 2, 2));
		assertEquals(-1, Shorts.indexOf(null, (short) 2));
		assertEquals(-1, Shorts.indexOf(a, (short) 2, -1, 4));
		assertEquals(3, Shorts.lastIndexOf(a, (short) 2));
		assertEquals(1, Shorts.lastIndexOf(a, (short) 2, 2));
		assertEquals(-1, Shorts.lastIndexOf(a, (short) 9));
		assertEquals(-1, Shorts.lastIndexOf(null, (short) 9));
		assertEquals(-1, Shorts.lastIndexOf(a, (short) 2, -1));
		assertTrue(Shorts.contains(a, (short) 3));
		assertTrue(Shorts.notContains(a, (short) 4));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(s(1, 2, 3), Shorts.concat(s(1), s(2, 3)));
		assertThrows(IllegalArgumentException.class, () -> Shorts.concat(null, s(1)));
		assertArrayEquals(s(1, 2), Shorts.add(s(1), (short) 2));
		assertArrayEquals(s(1, 3), Shorts.remove(s(1, 2, 3), (short) 2));
		short[] a = s(1, 2, 3);
		assertSame(a, Shorts.remove(a, (short) 5));
		assertArrayEquals(s(1, 3), Shorts.removeAt(a, 1));
		assertArrayEquals(s(1, 2), Shorts.removeAt(a, -1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Shorts.removeAt(a, 3));
		assertArrayEquals(a, Shorts.copy(a));
		assertArrayEquals(s(1, 2), Shorts.copy(a, 2));
		assertArrayEquals(s(2, 3, 0), Shorts.copy(a, 1, 3));
		assertArrayEquals(s(3, 9), Shorts.copy(a, 2, 2, (short) 9));
	}

	@Test
	public void testWrappersAndCapacity() {
		assertArrayEquals(new Short[] { 1, -1 }, Shorts.toWrappers(new byte[] { 1, -1 }));
		assertArrayEquals(new Short[] { 300 }, Shorts.toWrappers(s(300)));
		assertArrayEquals(s(1, 3), Shorts.toPrimitives(new Short[] { 1, null, 3 }));
		short[] a = s(1, 2, 3);
		assertSame(a, Shorts.ensureCapacity(a, 3));
		assertArrayEquals(s(1), Shorts.ensureCapacity(a, 1));
		assertArrayEquals(s(1, 2, 3, 0, 0, 0), Shorts.expandCapacity(a, 0));
		assertArrayEquals(s(1, 2, 3, 0), Shorts.expandCapacity(a, 0, 1));
	}

	@Test
	public void testAggregatesAndJoin() {
		short[] a = s(3, -1, 7);
		assertEquals(7, Shorts.max(a));
		assertEquals(-1, Shorts.min(a));
		assertEquals(9, Shorts.sum(a));
		assertEquals(3d, Shorts.avg(a), 0.0001);
		assertThrows(IllegalArgumentException.class, () -> Shorts.max(null));
		assertThrows(IllegalArgumentException.class, () -> Shorts.min(new short[0]));
		assertEquals("[3,-1,7]", Shorts.toString(a));
		assertEquals("3-17", Shorts.join(a, null));
		assertEquals("", Shorts.join(null));
		assertEquals("1=3,2=4", Shorts.join(s(1, 2), s(3, 4), "=", ","));
		assertEquals("1324", Shorts.join(s(1, 2), s(3, 4), null));
		assertEquals("", Shorts.join(s(1), null, ","));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 9, 10, 11, 64, 300 })
	public void testSort(int size) {
		Random random = new Random(size);
		short[] a = new short[size];
		for (int i = 0; i < size; i++) {
			a[i] = (short) (random.nextInt(200) - 100);
		}
		short[] expected = a.clone();
		Arrays.sort(expected);
		short[] asc = a.clone();
		Shorts.sort(asc, true);
		assertArrayEquals(expected, asc);
		short[] desc = a.clone();
		Shorts.sort(desc, false);
		Shorts.reverse(desc);
		assertArrayEquals(expected, desc);
	}

	@Test
	public void testSetOperations() {
		short[] left = s(0, 1, 2, 2);
		short[] right = s(2, 3, 0);
		assertArrayEquals(s(0, 1, 2, 2, 2, 3, 0), Shorts.unionAll(left, right));
		assertArrayEquals(s(0, 1, 2, 3), Shorts.union(left, right));
		assertArrayEquals(s(1), Shorts.minus(left, right));
		assertArrayEquals(s(0, 2, 2), Shorts.intersect(left, right));
		assertNull(Shorts.unionAll(null, null));
		assertSame(left, Shorts.unionAll(left, null));
		assertSame(right, Shorts.unionAll(null, right));
		assertNull(Shorts.union(null, null));
		assertSame(left, Shorts.union(left, null));
		assertSame(right, Shorts.union(null, right));
		assertNull(Shorts.minus(null, null));
		assertSame(left, Shorts.minus(left, null));
		assertSame(right, Shorts.minus(null, right));
		assertNull(Shorts.intersect(null, null));
		assertSame(left, Shorts.intersect(left, null));
		assertSame(right, Shorts.intersect(null, right));
	}

	@Test
	public void testCollectionsAndMisc() {
		assertArrayEquals(s(1, 0, 3), Shorts.toArray(Arrays.asList(1, "x", 3.7d)));
		assertEquals(Arrays.asList((short) 1, (short) 2), Shorts.toList(s(1, 2)));
		short[] a = s(1, 2, 3);
		Shorts.swap(a, 0, 2);
		assertArrayEquals(s(3, 2, 1), a);
		Shorts.reverse(null);
		short[] b = s(1, 2, 3, 4, 5);
		Shorts.shuffle(b, new Random(3));
		Shorts.shuffle(b);
		Shorts.sort(b, true);
		assertArrayEquals(s(1, 2, 3, 4, 5), b);
		Shorts.shuffle(null);
		assertEquals(Short.hashCode((short) 5), Shorts.hashCode((short) 5));
		assertTrue(Shorts.deepEquals(a, a));
		assertTrue(Shorts.deepEquals(null, null));
		assertFalse(Shorts.deepEquals(null, a));
		assertFalse(Shorts.deepEquals(a, null));
		assertFalse(Shorts.deepEquals(a, s(3)));
		assertFalse(Shorts.deepEquals(a, s(3, 2, 0)));
		assertTrue(Shorts.deepEquals(a, s(3, 2, 1)));
		assertEquals(Arrays.hashCode(a), Shorts.deepHashCode(a));
		assertEquals(0, Shorts.deepHashCode(null));
	}

	@Test
	public void testValueOf() {
		assertEquals(Short.valueOf((short) 1), Shorts.valueOf(Boolean.TRUE));
		assertNull(Shorts.valueOf((Boolean) null));
		assertEquals(Short.valueOf((short) 97), Shorts.valueOf('a'));
		assertNull(Shorts.valueOf((Character) null));
		assertEquals(Short.valueOf((short) 12), Shorts.valueOf("12"));
		assertNull(Shorts.valueOf("abc"));
		assertEquals(Short.valueOf((short) -1), Shorts.valueOf("", (short) -1));
		assertArrayEquals(new Short[] { 1, null }, Shorts.valueOf(new String[] { "1", "x" }));
		Short v = Short.valueOf((short) 1000);
		assertSame(v, Shorts.valueOf(v));
		assertEquals(Short.valueOf((short) 3), Shorts.valueOf(3.9d));
		assertNull(Shorts.valueOf((Number) null));
		assertNull(Shorts.valueOf(100000));
		assertArrayEquals(new Short[] { 1, -1 }, Shorts.valueOf(new Number[] { 1, 100000 }, (short) -1));
		assertArrayEquals(new Short[] { 1 }, Shorts.valueOf(new Number[] { 1 }));
	}

	@ParameterizedTest
	@CsvSource({ "12,12", "-12,-12", "0x1F,31", "#7FFF,32767", "12L,12", "1.9,1", "1e3,1000", "+5,5" })
	public void testParse(String input, short expected) {
		Shorts.clearCache();
		assertEquals(expected, Shorts.parse(input));
		assertEquals(expected, Shorts.parse(input));
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "0x10000", "-", "1..2", "100000" })
	public void testParseInvalid(String input) {
		assertThrows(IllegalArgumentException.class, () -> Shorts.parse(input));
	}

	@Test
	public void testParseManyAndCasts() {
		assertArrayEquals(s(1, 2), Shorts.parseMany(new String[] { "1", "2" }));
		assertThrows(IllegalArgumentException.class, () -> Shorts.parseMany(new String[] { "x" }));
		assertArrayEquals(s(1, 3), Shorts.parseMany(new String[] { "1", "x", "3" }, false));
		assertEquals(1, Shorts.cast(true));
		assertArrayEquals(s(1, 0), Shorts.casts(new boolean[] { true, false }));
		assertEquals(97, Shorts.cast('a'));
		assertThrows(NumberOverflowException.class, () -> Shorts.cast('￿'));
		assertArrayEquals(s(97), Shorts.casts(new char[] { 'a' }));
		assertThrows(NumberOverflowException.class, () -> Shorts.casts(new char[] { '￿' }));
		assertArrayEquals(s(0, 97), Shorts.casts(new char[] { '￿', 'a' }, false));
		assertArrayEquals(s(-1), Shorts.casts(new byte[] { -1 }));
		assertArrayEquals(s(7), Shorts.casts(new int[] { 7 }));
		assertThrows(NumberOverflowException.class, () -> Shorts.casts(new int[] { 70000 }));
		assertArrayEquals(s(0, 7), Shorts.casts(new int[] { 70000, 7 }, false));
		assertArrayEquals(s(7), Shorts.casts(new long[] { 7 }));
		assertThrows(NumberOverflowException.class, () -> Shorts.casts(new long[] { 70000 }));
		assertArrayEquals(s(0, 7), Shorts.casts(new long[] { 70000, 7 }, false));
		assertEquals(1, Shorts.cast(1.5f));
		assertArrayEquals(s(1), Shorts.casts(new float[] { 1.5f }));
		assertThrows(NumberOverflowException.class, () -> Shorts.casts(new float[] { 1e6f }));
		assertArrayEquals(s(0, 1), Shorts.casts(new float[] { 1e6f, 1 }, false));
		assertArrayEquals(s(-2), Shorts.casts(new double[] { -2.5 }));
		assertThrows(NumberOverflowException.class, () -> Shorts.casts(new double[] { 1e6 }));
		assertArrayEquals(s(0, 1), Shorts.casts(new double[] { 1e6, 1 }, false));
		assertArrayEquals(s(1, 2, 3, 4), Shorts.casts(new Number[] { 1, 2L, new BigDecimal("3.3"), BigInteger.valueOf(4) }));
		assertThrows(NullPointerException.class, () -> Shorts.casts(new Number[] { null }));
		assertThrows(NumberOverflowException.class, () -> Shorts.casts(new Number[] { BigInteger.valueOf(40000) }));
		assertArrayEquals(s(0, 0, 4), Shorts.casts(new Number[] { null, 40000, 4 }, false));
		assertEquals(Short.MIN_VALUE, Shorts.cast(BigInteger.valueOf(Short.MIN_VALUE)));
		assertEquals(5, Shorts.cast(new BigDecimal("5.5")));
		assertEquals(5, Shorts.cast(5L));
	}

	@Test
	public void testPredicates() {
		assertTrue(Shorts.isEven((short) 0));
		assertTrue(Shorts.isOdd((short) -3));
		assertTrue(Shorts.isEvens(s(2, 4)));
		assertFalse(Shorts.isEvens(s(2, 3)));
		assertFalse(Shorts.isEvens(null));
		assertTrue(Shorts.isOdds(s(1, 3)));
		assertFalse(Shorts.isOdds(s(1, 2)));
		assertFalse(Shorts.isOdds(null));
		assertArrayEquals(new String[] { "1" }, Shorts.toStringArray(s(1), null));
		assertArrayEquals(new String[] { "01" }, Shorts.toStringArray(s(1), new DecimalFormat("00", DecimalFormatSymbols.getInstance(Locale.ROOT))));
		assertNull(Shorts.toStringArray(null, null));
		assertTrue(Shorts.isSameLength(null, new short[0]));
		assertFalse(Shorts.isSameLength(null, s(1)));
		assertTrue(Shorts.isSameLength(new short[0], null));
		assertTrue(Shorts.isSameLength(s(1), s(2)));
		assertTrue(Shorts.same(s(4, 4)));
		assertFalse(Shorts.same(s(4, 5)));
		assertTrue(Shorts.isSerial(s(1, 2, 3)));
		assertTrue(Shorts.isSerial(s(1, 3), 2));
		assertFalse(Shorts.isSerial(null));
		assertTrue(Shorts.isSubarray(s(1, 2, 3, 4), s(2, 3)));
		assertFalse(Shorts.isSubarray(s(1, 2, 3), s(3, 4)));
		assertFalse(Shorts.isSubarray(s(1, 2, 3), s(2, 4)));
		assertFalse(Shorts.isSubarray(null, s(1)));
		assertTrue(Shorts.containsAll(s(1, 2), s(2, 2, 1)));
		assertFalse(Shorts.containsAll(s(1, 2), s(3)));
		assertFalse(Shorts.containsAll(null, s(3)));
		short[] a = s(1, 2, 3, 4, 5);
		Shorts.rightScroll(a, 1);
		assertArrayEquals(s(5, 1, 2, 3, 4), a);
		Shorts.leftScroll(a, 6);
		assertArrayEquals(s(1, 2, 3, 4, 5), a);
		assertTrue(Shorts.compare((short) 1, (short) 2) < 0);
		assertEquals(0, Shorts.compare((short) 2, (short) 2));
		assertTrue(Shorts.defaultComparator().compare(s(1), s(2)) < 0);
		assertTrue(Shorts.defaultComparator().compare(s(1, 2), s(1)) > 0);
		assertEquals(0, Shorts.defaultComparator().compare(s(1, 2), s(1, 2)));
	}

}
