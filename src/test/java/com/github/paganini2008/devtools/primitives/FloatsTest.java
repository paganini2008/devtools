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
 * FloatsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class FloatsTest {

	private static final float D = 0f;

	private static float[] f(float... values) {
		return values;
	}

	@Test
	public void testBasics() {
		assertNull(Floats.clone(null));
		float[] a = f(1, 2, 3);
		assertNotSame(a, Floats.clone(a));
		assertArrayEquals(a, Floats.clone(a), D);
		assertEquals(0, Floats.length(null));
		assertEquals(3, Floats.length(a));
		assertTrue(Floats.isFinite(1f));
		assertFalse(Floats.isFinite(Float.NEGATIVE_INFINITY));
		assertFalse(Floats.isFinite(Float.NaN));
		assertArrayEquals(f(0, 0), Floats.create(2), D);
		assertArrayEquals(f(1.5f, 1.5f), Floats.create(2, 1.5f), D);
		assertArrayEquals(f(0, 0, 0), Floats.create(2, 3)[1], D);
		assertArrayEquals(f(2, 2, 2), Floats.create(2, 3, 2f)[1], D);
		assertTrue(Floats.isEmpty(null));
		assertTrue(Floats.isNotEmpty(a));
		assertEquals(4, Floats.BYTES);
		assertEquals(0, Floats.EMPTY_ARRAY.length);
		assertEquals(0, Floats.EMPTY_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		float[] a = f(1, 2, 3, 2);
		assertEquals(1, Floats.indexOf(a, 2f));
		assertEquals(3, Floats.indexOf(a, 2f, 2, 100));
		assertEquals(-1, Floats.indexOf(a, 2f, -1, 4));
		assertEquals(-1, Floats.indexOf(null, 2f));
		assertEquals(3, Floats.lastIndexOf(a, 2f));
		assertEquals(1, Floats.lastIndexOf(a, 2f, 2));
		assertEquals(-1, Floats.lastIndexOf(a, 9f));
		assertEquals(-1, Floats.lastIndexOf(null, 9f));
		assertEquals(-1, Floats.lastIndexOf(a, 2f, -1));
		assertTrue(Floats.contains(a, 3f));
		assertTrue(Floats.notContains(a, 4f));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(f(1, 2, 3), Floats.concat(f(1), f(2, 3)), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.concat(f(1), null));
		assertArrayEquals(f(1, 2), Floats.add(f(1), 2f), D);
		assertArrayEquals(f(1, 3), Floats.remove(f(1, 2, 3), 2f), D);
		float[] a = f(1, 2, 3);
		assertSame(a, Floats.remove(a, 5f));
		assertArrayEquals(f(1, 2), Floats.removeAt(a, -1), D);
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Floats.removeAt(a, 3));
		assertArrayEquals(a, Floats.copy(a), D);
		assertArrayEquals(f(1, 2), Floats.copy(a, 2), D);
		assertArrayEquals(f(2, 3, 0), Floats.copy(a, 1, 3), D);
		assertArrayEquals(f(3, 9), Floats.copy(a, 2, 2, 9f), D);
	}

	@Test
	public void testPrimitivesAndWrappers() {
		assertArrayEquals(f(1), Floats.toPrimitives(new Byte[] { 1 }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.toPrimitives(new Byte[] { null }));
		assertArrayEquals(f(1), Floats.toPrimitives(new Short[] { 1 }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.toPrimitives(new Short[] { null }));
		assertArrayEquals(f(97), Floats.toPrimitives(new Character[] { 'a' }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.toPrimitives(new Character[] { null }));
		assertArrayEquals(f(1), Floats.toPrimitives(new Integer[] { 1 }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.toPrimitives(new Integer[] { null }));
		assertArrayEquals(f(1), Floats.toPrimitives(new Long[] { 1L }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.toPrimitives(new Long[] { null }));
		assertArrayEquals(f(1.5f, 2), Floats.toPrimitives(new Float[] { 1.5f, null, 2f }), D);
		assertArrayEquals(new Float[] { 1f }, Floats.toWrappers(new byte[] { 1 }));
		assertArrayEquals(new Float[] { 97f }, Floats.toWrappers(new char[] { 'a' }));
		assertArrayEquals(new Float[] { 1f }, Floats.toWrappers(new short[] { 1 }));
		assertArrayEquals(new Float[] { 1.5f }, Floats.toWrappers(f(1.5f)));
		assertArrayEquals(new Float[] { 1f }, Floats.toWrappers(new int[] { 1 }));
		assertArrayEquals(new Float[] { 1f }, Floats.toWrappers(new long[] { 1 }));
		assertArrayEquals(f(1), Floats.casts(new byte[] { 1 }), D);
		assertArrayEquals(f(1), Floats.casts(new short[] { 1 }), D);
		assertArrayEquals(f(1), Floats.casts(new int[] { 1 }), D);
		assertArrayEquals(f(1), Floats.casts(new long[] { 1 }), D);
		assertArrayEquals(f(1.5f), Floats.casts(new double[] { 1.5 }), D);
		assertArrayEquals(f(1.5f), Floats.casts(new double[] { 1.5 }, false), D);
		assertArrayEquals(f(1, 0), Floats.casts(new boolean[] { true, false }), D);
		assertArrayEquals(f(97), Floats.casts(new char[] { 'a' }), D);
		assertEquals(1f, Floats.cast(true), D);
		assertEquals(97f, Floats.cast('a'), D);
		assertEquals(2.5f, Floats.cast(new BigDecimal("2.5")), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.cast((Number) null));
		assertArrayEquals(f(1, 2.5f), Floats.casts(new Number[] { 1, 2.5d }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.casts(new Number[] { null }));
		assertArrayEquals(f(0, 2), Floats.casts(new Number[] { null, 2 }, false), D);
	}

	@Test
	public void testCapacityAndAggregates() {
		float[] a = f(3, -1, 7);
		assertSame(a, Floats.ensureCapacity(a, 3));
		assertArrayEquals(f(3), Floats.ensureCapacity(a, 1), D);
		assertArrayEquals(f(3, -1, 7, 0, 0, 0), Floats.expandCapacity(a), D);
		assertArrayEquals(f(3, -1, 7, 0), Floats.expandCapacity(a, 1), D);
		assertEquals(7f, Floats.max(a), D);
		assertEquals(-1f, Floats.min(a), D);
		assertEquals(9f, Floats.sum(a), D);
		assertEquals(3d, Floats.avg(a), 0d);
		assertEquals(5f, Floats.max(f(Float.NaN, 5, 1)), D);
		assertEquals(1f, Floats.min(f(5, Float.NaN, 1)), D);
		assertEquals(2f, Floats.max(2f, Float.NaN), D);
		assertEquals(2f, Floats.max(Float.NaN, 2f), D);
		assertEquals(2f, Floats.min(2f, Float.NaN), D);
		assertEquals(2f, Floats.min(Float.NaN, 2f), D);
		assertEquals(1f, Floats.min(1f, 2f), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.max(null));
		assertThrows(IllegalArgumentException.class, () -> Floats.min(new float[0]));
		assertThrows(IllegalArgumentException.class, () -> Floats.sum(null));
	}

	@Test
	public void testJoin() {
		assertEquals("[1.0,2.5]", Floats.toString(f(1, 2.5f)));
		assertEquals("1.02.5", Floats.join(f(1, 2.5f), null));
		assertEquals("", Floats.join(null));
		assertEquals("1.0=3.0,2.0=4.0", Floats.join(f(1, 2), f(3, 4), "=", ","));
		assertEquals("1.03.0", Floats.join(f(1), f(3), null));
		assertEquals("", Floats.join(null, f(3), ","));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 9, 10, 11, 64, 300 })
	public void testSort(int size) {
		Random random = new Random(size);
		float[] a = new float[size];
		for (int i = 0; i < size; i++) {
			a[i] = random.nextInt(100) - 50 + 0.5f;
		}
		float[] expected = a.clone();
		Arrays.sort(expected);
		float[] asc = a.clone();
		Floats.sort(asc, true);
		assertArrayEquals(expected, asc, D);
		float[] desc = a.clone();
		Floats.sort(desc, false);
		Floats.reverse(desc);
		assertArrayEquals(expected, desc, D);
	}

	@Test
	public void testSetOperations() {
		float[] left = f(0, 1, 2, 2);
		float[] right = f(2, 3, 0);
		assertArrayEquals(f(0, 1, 2, 2, 2, 3, 0), Floats.unionAll(left, right), D);
		assertArrayEquals(f(0, 1, 2, 3), Floats.union(left, right), D);
		assertArrayEquals(f(1), Floats.minus(left, right), D);
		assertArrayEquals(f(0, 2, 2), Floats.intersect(left, right), D);
		assertNull(Floats.unionAll(null, null));
		assertSame(left, Floats.unionAll(left, null));
		assertSame(right, Floats.unionAll(null, right));
		assertNull(Floats.union(null, null));
		assertSame(left, Floats.union(left, null));
		assertSame(right, Floats.union(null, right));
		assertNull(Floats.minus(null, null));
		assertSame(left, Floats.minus(left, null));
		assertSame(right, Floats.minus(null, right));
		assertNull(Floats.intersect(null, null));
		assertSame(left, Floats.intersect(left, null));
		assertSame(right, Floats.intersect(null, right));
	}

	@Test
	public void testCollectionsAndMisc() {
		assertArrayEquals(f(1, 0, 3.5f), Floats.toArray(Arrays.asList(1, "x", 3.5d)), D);
		assertEquals(Arrays.asList(1f, 2f), Floats.toList(f(1, 2)));
		float[] a = f(1, 2, 3);
		Floats.swap(a, 0, 2);
		assertArrayEquals(f(3, 2, 1), a, D);
		Floats.reverse(null);
		float[] b = f(1, 2, 3, 4, 5);
		Floats.shuffle(b, new Random(3));
		Floats.shuffle(b);
		Floats.sort(b, true);
		assertArrayEquals(f(1, 2, 3, 4, 5), b, D);
		Floats.shuffle(null);
		assertEquals(Float.hashCode(1.5f), Floats.hashCode(1.5f));
		assertTrue(Floats.deepEquals(a, a));
		assertTrue(Floats.deepEquals(null, null));
		assertFalse(Floats.deepEquals(null, a));
		assertFalse(Floats.deepEquals(a, null));
		assertFalse(Floats.deepEquals(a, f(3)));
		assertFalse(Floats.deepEquals(a, f(3, 2, 0)));
		assertTrue(Floats.deepEquals(a, f(3, 2, 1)));
		assertEquals(Arrays.hashCode(a), Floats.deepHashCode(a));
		assertEquals(0, Floats.deepHashCode(null));
	}

	@Test
	public void testValueOf() {
		// Floats.valueOf(Character) parses the character as a digit (unlike the other primitive classes)
		assertEquals(Float.valueOf(5f), Floats.valueOf('5'));
		assertNull(Floats.valueOf('a'));
		assertNull(Floats.valueOf((Character) null));
		assertEquals(Float.valueOf(1f), Floats.valueOf(Boolean.TRUE));
		assertNull(Floats.valueOf((Boolean) null));
		assertEquals(Float.valueOf(1.5f), Floats.valueOf("1.5"));
		assertNull(Floats.valueOf("abc"));
		assertEquals(Float.valueOf(-1f), Floats.valueOf("-", -1f));
		assertEquals(Float.valueOf(-1f), Floats.valueOf(" ", -1f));
		assertArrayEquals(new Float[] { 1f, null }, Floats.valueOf(new String[] { "1", "x" }));
		Float v = Float.valueOf(2.5f);
		assertSame(v, Floats.valueOf(v, null));
		assertEquals(Float.valueOf(3f), Floats.valueOf(3, null));
		assertEquals(Float.valueOf(0f), Floats.valueOf((Number) null, 0f));
		assertArrayEquals(new Float[] { 1f, null }, Floats.valueOf(new Number[] { 1, null }));
		assertArrayEquals(new Float[] { 1f, 0f }, Floats.valueOf(new Number[] { 1, null }, 0f));
	}

	@ParameterizedTest
	@CsvSource({ "1.5,1.5", "-2,-2", "1e3,1000", "12L,12", ".5,0.5", "-.5,-0.5" })
	public void testParse(String input, float expected) {
		Floats.clearCache();
		assertEquals(expected, Floats.parse(input), D);
		assertEquals(expected, Floats.parse(input), D);
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "-", "+", "1..2", "--1" })
	public void testParseInvalid(String input) {
		assertThrows(IllegalArgumentException.class, () -> Floats.parse(input));
	}

	@Test
	public void testParseMany() {
		assertArrayEquals(f(1, 2.5f), Floats.parseMany(new String[] { "1", "2.5" }), D);
		assertThrows(IllegalArgumentException.class, () -> Floats.parseMany(new String[] { "x" }));
		// unparsable elements are left as 0 when not thrown
		assertArrayEquals(f(1, 0, 3), Floats.parseMany(new String[] { "1", "x", "3" }, false), D);
	}

	@Test
	public void testToFixedAndPlainString() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.GERMANY);
			assertEquals(1.25f, Floats.toFixed(1.2534f, 2), D);
		} finally {
			Locale.setDefault(defaultLocale);
		}
		assertEquals(2f, Floats.toFixed(2.5f, 0), D);
		assertEquals(3f, Floats.toFixed(2.5f, 0, RoundingMode.HALF_UP), D);
		assertEquals(1.2f, Floats.toFixed(1.29f, 1, RoundingMode.DOWN), D);
		assertTrue(Float.isNaN(Floats.toFixed(Float.NaN, 2)));
		assertEquals("10000000000", Floats.toPlainString(1e10f));
		assertEquals("0.5", Floats.toPlainString(0.5f));
		assertEquals("NaN", Floats.toPlainString(Float.NaN));
		assertEquals("Infinity", Floats.toPlainString(Float.POSITIVE_INFINITY));
		assertArrayEquals(new String[] { "1.5" }, Floats.toStringArray(f(1.5f), null));
		assertArrayEquals(new String[] { "1.50" },
				Floats.toStringArray(f(1.5f), new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ROOT))));
		assertNull(Floats.toStringArray(null, null));
	}

	@Test
	public void testPredicates() {
		assertTrue(Floats.isSameLength(null, new float[0]));
		assertFalse(Floats.isSameLength(null, new float[1]));
		assertTrue(Floats.isSameLength(new float[0], null));
		assertFalse(Floats.isSameLength(new float[1], null));
		assertTrue(Floats.isSameLength(new float[1], new float[1]));
		assertTrue(Floats.same(f(4, 4)));
		assertFalse(Floats.same(f(4, 5)));
		assertFalse(Floats.same(null));
		assertTrue(Floats.isSerial(f(1, 1.5f, 2), 0.5f));
		assertFalse(Floats.isSerial(f(1, 2), 0.5f));
		assertTrue(Floats.isSubarray(f(1, 2, 3, 4), f(2, 3)));
		assertFalse(Floats.isSubarray(f(1, 2, 3), f(3, 4)));
		assertFalse(Floats.isSubarray(f(1, 2, 3), f(2, 4)));
		assertFalse(Floats.isSubarray(f(1), f(1, 2)));
		assertTrue(Floats.containsAll(f(1, 2), f(2, 2, 1)));
		assertFalse(Floats.containsAll(f(1, 2), f(3)));
		assertFalse(Floats.containsAll(null, f(3)));
		float[] a = f(1, 2, 3, 4, 5);
		Floats.rightScroll(a, 1);
		assertArrayEquals(f(5, 1, 2, 3, 4), a, D);
		Floats.leftScroll(a, 6);
		assertArrayEquals(f(1, 2, 3, 4, 5), a, D);
		assertTrue(Floats.between(1f, 1f, 2f));
		assertFalse(Floats.between(2.1f, 1f, 2f));
		assertFalse(Floats.in(1f, 1f, 2f));
		assertTrue(Floats.in(1.5f, 1f, 2f));
		assertTrue(Floats.rightIn(1f, 1f, 2f));
		assertFalse(Floats.rightIn(2f, 1f, 2f));
		assertTrue(Floats.leftIn(2f, 1f, 2f));
		assertFalse(Floats.leftIn(1f, 1f, 2f));
		assertTrue(Floats.compare(1f, 2f) < 0);
		assertEquals(0, Floats.compare(2f, 2f));
		assertTrue(Floats.defaultComparator().compare(f(1), f(2)) < 0);
		assertTrue(Floats.defaultComparator().compare(f(1, 2), f(1)) > 0);
		assertEquals(0, Floats.defaultComparator().compare(f(1, 2), f(1, 2)));
	}

}
