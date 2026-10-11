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
import java.nio.ByteBuffer;
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
 * BytesTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BytesTest {

	private static byte[] b(int... values) {
		byte[] result = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = (byte) values[i];
		}
		return result;
	}

	@Test
	public void testBasics() {
		assertNull(Bytes.clone(null));
		byte[] a = b(1, 2, 3);
		assertNotSame(a, Bytes.clone(a));
		assertArrayEquals(a, Bytes.clone(a));
		assertEquals(0, Bytes.length(null));
		assertEquals(3, Bytes.length(a));
		assertArrayEquals(b(7, 7), Bytes.create(2, (byte) 7));
		assertArrayEquals(b(0, 0), Bytes.create(2, (byte) 0));
		assertArrayEquals(b(5, 5, 5), Bytes.create(2, 3, (byte) 5)[1]);
		assertTrue(Bytes.isEmpty(null));
		assertTrue(Bytes.isNotEmpty(a));
		assertEquals(0, Bytes.EMPTY_ARRAY.length);
		assertEquals(0, Bytes.EMPTY_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		byte[] a = b(1, 2, 3, 2);
		assertEquals(1, Bytes.indexOf(a, (byte) 2));
		assertEquals(3, Bytes.indexOf(a, (byte) 2, 2));
		assertEquals(-1, Bytes.indexOf(null, (byte) 2));
		assertEquals(-1, Bytes.indexOf(a, (byte) 2, -1, 4));
		assertEquals(3, Bytes.lastIndexOf(a, (byte) 2));
		assertEquals(1, Bytes.lastIndexOf(a, (byte) 2, 2));
		assertEquals(-1, Bytes.lastIndexOf(a, (byte) 9));
		assertEquals(-1, Bytes.lastIndexOf(null, (byte) 9));
		assertEquals(-1, Bytes.lastIndexOf(a, (byte) 2, -1));
		assertTrue(Bytes.contains(a, (byte) 3));
		assertTrue(Bytes.notContains(a, (byte) 4));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(b(1, 2, 3), Bytes.concat(b(1), b(2, 3)));
		assertThrows(IllegalArgumentException.class, () -> Bytes.concat(b(1), null));
		assertArrayEquals(b(1, 2), Bytes.add(b(1), (byte) 2));
		assertArrayEquals(b(1, 3), Bytes.remove(b(1, 2, 3), (byte) 2));
		byte[] a = b(1, 2, 3);
		assertSame(a, Bytes.remove(a, (byte) 5));
		assertArrayEquals(b(1, 2), Bytes.removeAt(a, -1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Bytes.removeAt(a, 3));
		assertArrayEquals(a, Bytes.copy(a));
		assertArrayEquals(b(1, 2), Bytes.copy(a, 2));
		assertArrayEquals(b(2, 3, 0), Bytes.copy(a, 1, 3));
		assertArrayEquals(b(3, 9), Bytes.copy(a, 2, 2, (byte) 9));
	}

	@Test
	public void testWrappersAndCapacity() {
		assertArrayEquals(new Byte[] { 97, null }, Bytes.toWrappers(new char[] { 'a', 'Ā' }));
		assertArrayEquals(new Byte[] { 1, 0 }, Bytes.toWrappers(new boolean[] { true, false }));
		assertArrayEquals(new Byte[] { 1, -1 }, Bytes.toWrappers(b(1, -1)));
		assertArrayEquals(b(1, 3), Bytes.toPrimitives(new Byte[] { 1, null, 3 }));
		byte[] a = b(1, 2, 3);
		assertSame(a, Bytes.ensureCapacity(a, 3));
		assertArrayEquals(b(1), Bytes.ensureCapacity(a, 1));
		assertArrayEquals(b(1, 2, 3, 0, 0, 0), Bytes.expandCapacity(a));
		assertArrayEquals(b(1, 2, 3, 0), Bytes.expandCapacity(a, 1));
	}

	@Test
	public void testAggregatesAndJoin() {
		byte[] a = b(100, 100, -1);
		assertEquals(100, Bytes.max(a));
		assertEquals(-1, Bytes.min(a));
		assertEquals(199, Bytes.sum(a));
		assertEquals(199d / 3, Bytes.avg(a), 0.0001);
		assertThrows(IllegalArgumentException.class, () -> Bytes.max(null));
		assertThrows(IllegalArgumentException.class, () -> Bytes.min(null));
		assertEquals("[1,2]", Bytes.toString(b(1, 2)));
		assertEquals("12", Bytes.join(b(1, 2), null));
		assertEquals("", Bytes.join(null));
		assertEquals("1=3,2=4", Bytes.join(b(1, 2), b(3, 4), "=", ","));
		assertEquals("1324", Bytes.join(b(1, 2), b(3, 4), null));
		assertEquals("", Bytes.join(null, b(1), ","));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 9, 10, 11, 64, 300 })
	public void testSort(int size) {
		Random random = new Random(size);
		byte[] a = new byte[size];
		random.nextBytes(a);
		byte[] expected = a.clone();
		Arrays.sort(expected);
		byte[] asc = a.clone();
		Bytes.sort(asc, true);
		assertArrayEquals(expected, asc);
		byte[] desc = a.clone();
		Bytes.sort(desc, false);
		Bytes.reverse(desc);
		assertArrayEquals(expected, desc);
	}

	@Test
	public void testSetOperations() {
		byte[] left = b(0, 1, 2, 2);
		byte[] right = b(2, 3, 0);
		assertArrayEquals(b(0, 1, 2, 2, 2, 3, 0), Bytes.unionAll(left, right));
		assertArrayEquals(b(0, 1, 2, 3), Bytes.union(left, right));
		assertArrayEquals(b(1), Bytes.minus(left, right));
		assertArrayEquals(b(0, 2, 2), Bytes.intersect(left, right));
		assertNull(Bytes.unionAll(null, null));
		assertSame(left, Bytes.unionAll(left, null));
		assertSame(right, Bytes.unionAll(null, right));
		assertNull(Bytes.union(null, null));
		assertSame(left, Bytes.union(left, null));
		assertSame(right, Bytes.union(null, right));
		assertNull(Bytes.minus(null, null));
		assertSame(left, Bytes.minus(left, null));
		assertSame(right, Bytes.minus(null, right));
		assertNull(Bytes.intersect(null, null));
		assertSame(left, Bytes.intersect(left, null));
		assertSame(right, Bytes.intersect(null, right));
	}

	@Test
	public void testCollectionsAndMisc() {
		assertArrayEquals(b(1, 0, 3), Bytes.toArray(Arrays.asList(1, "x", 3.7d)));
		assertEquals(Arrays.asList((byte) 1, (byte) 2), Bytes.toList(b(1, 2)));
		byte[] a = b(1, 2, 3);
		Bytes.swap(a, 0, 2);
		assertArrayEquals(b(3, 2, 1), a);
		Bytes.reverse(null);
		byte[] c = b(1, 2, 3, 4, 5);
		Bytes.shuffle(c, new Random(3));
		Bytes.shuffle(c);
		Bytes.sort(c, true);
		assertArrayEquals(b(1, 2, 3, 4, 5), c);
		Bytes.shuffle(null);
		assertEquals(Byte.hashCode((byte) 5), Bytes.hashCode((byte) 5));
		assertTrue(Bytes.deepEquals(a, a));
		assertTrue(Bytes.deepEquals(null, null));
		assertFalse(Bytes.deepEquals(null, a));
		assertFalse(Bytes.deepEquals(a, null));
		assertFalse(Bytes.deepEquals(a, b(3)));
		assertFalse(Bytes.deepEquals(a, b(3, 2, 0)));
		assertTrue(Bytes.deepEquals(a, b(3, 2, 1)));
		assertEquals(Arrays.hashCode(a), Bytes.deepHashCode(a));
		assertEquals(0, Bytes.deepHashCode(null));
	}

	@Test
	public void testValueOf() {
		assertEquals(Byte.valueOf((byte) 1), Bytes.valueOf(Boolean.TRUE));
		assertNull(Bytes.valueOf((Boolean) null));
		assertEquals(Byte.valueOf((byte) 97), Bytes.valueOf('a'));
		assertNull(Bytes.valueOf('Ā'));
		assertNull(Bytes.valueOf((Character) null));
		assertEquals(Byte.valueOf((byte) 12), Bytes.valueOf("12"));
		assertNull(Bytes.valueOf("abc"));
		assertEquals(Byte.valueOf((byte) -1), Bytes.valueOf("", (byte) -1));
		assertArrayEquals(new Byte[] { 1, null }, Bytes.valueOf(new String[] { "1", "x" }));
		Byte v = Byte.valueOf((byte) 1);
		assertSame(v, Bytes.valueOf(v));
		assertEquals(Byte.valueOf((byte) 3), Bytes.valueOf(3.9d));
		assertNull(Bytes.valueOf((Number) null));
		assertNull(Bytes.valueOf(1000));
		assertArrayEquals(new Byte[] { 1, -1 }, Bytes.valueOf(new Number[] { 1, 1000 }, (byte) -1));
		assertArrayEquals(new Byte[] { 1 }, Bytes.valueOf(new Number[] { 1 }));
	}

	@ParameterizedTest
	@CsvSource({ "12,12", "-12,-12", "0x1F,31", "#7F,127", "12L,12", "1.9,1", "1e2,100", "+5,5" })
	public void testParse(String input, byte expected) {
		Bytes.clearCache();
		assertEquals(expected, Bytes.parse(input));
		assertEquals(expected, Bytes.parse(input));
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "0x100", "-", "1..2", "1000", "1e3" })
	public void testParseInvalid(String input) {
		assertThrows(IllegalArgumentException.class, () -> Bytes.parse(input));
	}

	@Test
	public void testParseManyAndCasts() {
		assertArrayEquals(b(1, 2), Bytes.parseMany(new String[] { "1", "2" }));
		assertThrows(IllegalArgumentException.class, () -> Bytes.parseMany(new String[] { "x" }));
		assertArrayEquals(b(1, 3), Bytes.parseMany(new String[] { "1", "x", "3" }, false));
		assertArrayEquals(b(7), Bytes.casts(new int[] { 7 }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new int[] { 700 }));
		assertArrayEquals(b(0, 7), Bytes.casts(new int[] { 700, 7 }, false));
		assertArrayEquals(b(7), Bytes.casts(new short[] { 7 }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new short[] { 700 }));
		assertArrayEquals(b(0, 7), Bytes.casts(new short[] { 700, 7 }, false));
		assertArrayEquals(b(7), Bytes.casts(new long[] { 7 }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new long[] { 700 }));
		assertArrayEquals(b(0, 7), Bytes.casts(new long[] { 700, 7 }, false));
		assertArrayEquals(b(1), Bytes.casts(new float[] { 1.5f }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new float[] { 1e6f }));
		assertArrayEquals(b(0, 1), Bytes.casts(new float[] { 1e6f, 1 }, false));
		assertArrayEquals(b(-2), Bytes.casts(new double[] { -2.5 }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new double[] { 1e6 }));
		assertArrayEquals(b(0, 1), Bytes.casts(new double[] { 1e6, 1 }, false));
		assertEquals(1, Bytes.cast(true));
		assertArrayEquals(b(1, 0), Bytes.casts(new boolean[] { true, false }));
		assertEquals(97, Bytes.cast('a'));
		assertThrows(NumberOverflowException.class, () -> Bytes.cast('Ā'));
		assertArrayEquals(b(97), Bytes.casts(new char[] { 'a' }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new char[] { 'Ā' }));
		assertArrayEquals(b(0, 97), Bytes.casts(new char[] { 'Ā', 'a' }, false));
		assertArrayEquals(b(1, 2, 3, 4), Bytes.casts(new Number[] { 1, 2L, new BigDecimal("3.3"), BigInteger.valueOf(4) }));
		assertThrows(NullPointerException.class, () -> Bytes.casts(new Number[] { null }));
		assertThrows(NumberOverflowException.class, () -> Bytes.casts(new Number[] { BigInteger.valueOf(400) }));
		assertArrayEquals(b(0, 0, 4), Bytes.casts(new Number[] { null, 400, 4 }, false));
		assertEquals(Byte.MIN_VALUE, Bytes.cast(BigInteger.valueOf(Byte.MIN_VALUE)));
		assertEquals(5, Bytes.cast(new BigDecimal("5.5")));
		assertEquals(5, Bytes.cast(5L));
	}

	@Test
	public void testPredicates() {
		assertTrue(Bytes.isEven((byte) 0));
		assertTrue(Bytes.isOdd((byte) -3));
		assertTrue(Bytes.isEvens(b(2, 4)));
		assertFalse(Bytes.isEvens(b(2, 3)));
		assertFalse(Bytes.isEvens(null));
		assertTrue(Bytes.isOdds(b(1, 3)));
		assertFalse(Bytes.isOdds(b(1, 2)));
		assertFalse(Bytes.isOdds(null));
		assertArrayEquals(new String[] { "1" }, Bytes.toStringArray(b(1), null));
		assertArrayEquals(new String[] { "01" }, Bytes.toStringArray(b(1), new DecimalFormat("00", DecimalFormatSymbols.getInstance(Locale.ROOT))));
		assertNull(Bytes.toStringArray(null, null));
		assertTrue(Bytes.isSameLength(null, new byte[0]));
		assertFalse(Bytes.isSameLength(null, b(1)));
		assertTrue(Bytes.isSameLength(new byte[0], null));
		assertTrue(Bytes.isSameLength(b(1), b(2)));
		assertTrue(Bytes.same(b(4, 4)));
		assertFalse(Bytes.same(b(4, 5)));
		assertTrue(Bytes.isSerial(b(1, 2, 3)));
		assertTrue(Bytes.isSerial(b(1, 3), 2));
		assertFalse(Bytes.isSerial(null));
		assertTrue(Bytes.isSubarray(b(1, 2, 3, 4), b(2, 3)));
		assertFalse(Bytes.isSubarray(b(1, 2, 3), b(3, 4)));
		assertFalse(Bytes.isSubarray(b(1, 2, 3), b(2, 4)));
		assertFalse(Bytes.isSubarray(null, b(1)));
		assertTrue(Bytes.containsAll(b(1, 2), b(2, 2, 1)));
		assertFalse(Bytes.containsAll(b(1, 2), b(3)));
		assertFalse(Bytes.containsAll(null, b(3)));
		byte[] a = b(1, 2, 3, 4, 5);
		Bytes.rightScroll(a, 1);
		assertArrayEquals(b(5, 1, 2, 3, 4), a);
		Bytes.leftScroll(a, 6);
		assertArrayEquals(b(1, 2, 3, 4, 5), a);
		assertTrue(Bytes.compare((byte) 1, (byte) 2) < 0);
		assertEquals(0, Bytes.compare((byte) 2, (byte) 2));
		assertTrue(Bytes.defaultComparator().compare(b(1), b(2)) < 0);
		assertTrue(Bytes.defaultComparator().compare(b(1, 2), b(1)) > 0);
		assertEquals(0, Bytes.defaultComparator().compare(b(1, 2), b(1, 2)));
	}

	@ParameterizedTest
	@ValueSource(longs = { 0, 1, -1, 255, 0x1234, Long.MIN_VALUE, Long.MAX_VALUE, 0x0102030405060708L })
	public void testBinaryConversion(long n) {
		assertEquals(n, Bytes.toLong(Bytes.toBytes(n)));
		assertArrayEquals(ByteBuffer.allocate(8).putLong(n).array(), Bytes.toBytes(n));
		int i = (int) n;
		assertEquals(i, Bytes.toInt(Bytes.toBytes(i)));
		assertArrayEquals(ByteBuffer.allocate(4).putInt(i).array(), Bytes.toBytes(i));
		short s = (short) n;
		assertEquals(s, Bytes.toShort(Bytes.toBytes(s)));
		char c = (char) n;
		assertEquals(2, Bytes.toBytes(c).length);
		assertEquals(c, Bytes.toChar(Bytes.toBytes(c)));
		double d = Double.longBitsToDouble(n);
		assertEquals(Double.doubleToLongBits(d), Double.doubleToLongBits(Bytes.toDouble(Bytes.toBytes(d))));
		float f = Float.intBitsToFloat(i);
		assertEquals(Float.floatToIntBits(f), Float.floatToIntBits(Bytes.toFloat(Bytes.toBytes(f))));
	}

	@Test
	public void testBinaryConversionErrors() {
		assertTrue(Bytes.toBoolean(Bytes.toBytes(true)));
		assertFalse(Bytes.toBoolean(Bytes.toBytes(false)));
		assertThrows(IllegalArgumentException.class, () -> Bytes.toBoolean(new byte[2]));
		assertThrows(IllegalArgumentException.class, () -> Bytes.toChar(new byte[1]));
		assertThrows(IllegalArgumentException.class, () -> Bytes.toShort(new byte[1]));
		assertThrows(IllegalArgumentException.class, () -> Bytes.toInt(new byte[1]));
		assertThrows(IllegalArgumentException.class, () -> Bytes.toLong(new byte[1]));
	}

	@Test
	public void testToByteArray() {
		ByteBuffer bb = ByteBuffer.wrap(b(1, 2, 3, 4));
		bb.get();
		assertArrayEquals(b(2, 3, 4), Bytes.toByteArray(bb));
		assertEquals(0, bb.remaining());
		assertNull(Bytes.toByteArray(null));
		byte[] def = b(9);
		assertSame(def, Bytes.toByteArray(null, def));
	}

}
