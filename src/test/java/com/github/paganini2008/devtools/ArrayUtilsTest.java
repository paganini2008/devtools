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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * ArrayUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ArrayUtilsTest {

	@Test
	public void testLength() {
		assertTrue(ArrayUtils.isSameLength(null, null));
		assertTrue(ArrayUtils.isSameLength(null, new Object[0]));
		assertTrue(ArrayUtils.isSameLength(new Object[0], null));
		assertFalse(ArrayUtils.isSameLength(new Object[1], null));
		assertFalse(ArrayUtils.isSameLength(null, new Object[1]));
		assertTrue(ArrayUtils.isSameLength(new Object[2], new String[2]));
		assertTrue(ArrayUtils.isNotSameLength(new Object[2], new String[3]));
		assertEquals(0, ArrayUtils.getLength(null));
		assertEquals(3, ArrayUtils.getLength(new Object[3]));
		assertTrue(ArrayUtils.isEmpty(null));
		assertTrue(ArrayUtils.isEmpty(new Object[0]));
		assertTrue(ArrayUtils.isNotEmpty(new Object[] { 1 }));
	}

	@Test
	public void testReverseAndSwap() {
		Integer[] a = { 1, 2, 3, 4, 5 };
		ArrayUtils.reverse(a);
		assertArrayEquals(new Integer[] { 5, 4, 3, 2, 1 }, a);
		Integer[] b = { 1, 2 };
		ArrayUtils.reverse(b);
		assertArrayEquals(new Integer[] { 2, 1 }, b);
		ArrayUtils.reverse(null);
		ArrayUtils.swap(a, 0, 4);
		assertArrayEquals(new Integer[] { 1, 4, 3, 2, 5 }, a);
	}

	@Test
	public void testUnionAll() {
		assertNull(ArrayUtils.unionAll(null, null));
		String[] a = { "a", "b" };
		String[] b = { "b", "c" };
		assertSame(a, ArrayUtils.unionAll(a, null));
		assertSame(b, ArrayUtils.unionAll(null, b));
		String[] r = ArrayUtils.unionAll(a, b);
		assertArrayEquals(new String[] { "a", "b", "b", "c" }, r);
		assertEquals(String.class, r.getClass().getComponentType());
	}

	@Test
	public void testUnion() {
		assertNull(ArrayUtils.union(null, null));
		String[] a = { "a", "b", "a" };
		String[] b = { "b", "c" };
		assertSame(a, ArrayUtils.union(a, null));
		assertSame(b, ArrayUtils.union(null, b));
		assertArrayEquals(new String[] { "a", "b", "c" }, ArrayUtils.union(a, b));
		// null elements are kept once
		assertArrayEquals(new String[] { null, "a", "b" }, ArrayUtils.union(new String[] { null, "a" }, new String[] { "b", null }));
	}

	@Test
	public void testMinus() {
		assertNull(ArrayUtils.minus(null, null));
		String[] a = { "a", "b", "c" };
		assertSame(a, ArrayUtils.minus(a, null));
		// legacy behavior: a null left operand returns the right operand
		assertSame(a, ArrayUtils.minus(null, a));
		assertArrayEquals(new String[] { "a", "c" }, ArrayUtils.minus(a, new String[] { "b", "x" }));
		assertArrayEquals(new String[0], ArrayUtils.minus(a, a));
	}

	@Test
	public void testIntersect() {
		assertNull(ArrayUtils.intersect(null, null));
		String[] a = { "a", "b", "c" };
		// legacy behavior: a null operand returns the other operand
		assertSame(a, ArrayUtils.intersect(a, null));
		assertSame(a, ArrayUtils.intersect(null, a));
		assertArrayEquals(new String[] { "b", "c" }, ArrayUtils.intersect(a, new String[] { "c", "b", "x" }));
	}

	@Test
	public void testIndexOf() {
		Object[] a = { "a", null, "b", "a" };
		assertEquals(0, ArrayUtils.indexOf(a, "a"));
		assertEquals(1, ArrayUtils.indexOf(a, null));
		assertEquals(-1, ArrayUtils.indexOf(a, "z"));
		assertEquals(-1, ArrayUtils.indexOf(null, "a"));
		assertEquals(3, ArrayUtils.indexOf(a, "a", 1, 10));
		assertEquals(-1, ArrayUtils.indexOf(a, "a", -1, 10));
		assertEquals(-1, ArrayUtils.indexOf(a, "b", 0, 2));
		assertTrue(ArrayUtils.contains(a, "b"));
		assertTrue(ArrayUtils.notContains(a, "c"));
		assertEquals(3, ArrayUtils.lastIndexOf(a, "a"));
		assertEquals(0, ArrayUtils.lastIndexOf(a, "a", 2));
		assertEquals(3, ArrayUtils.lastIndexOf(a, "a", 100));
		assertEquals(-1, ArrayUtils.lastIndexOf(a, "z"));
		assertEquals(-1, ArrayUtils.lastIndexOf(a, "a", -1));
		assertEquals(-1, ArrayUtils.lastIndexOf(null, "a"));
	}

	@Test
	public void testPrimitiveIndexOf() {
		char[] cs = { 'a', 'b' };
		assertEquals(1, ArrayUtils.indexOf(cs, 'b'));
		assertTrue(ArrayUtils.contains(cs, 'a'));
		assertTrue(ArrayUtils.notContains(cs, 'z'));
		assertEquals(-1, ArrayUtils.indexOf((char[]) null, 'a'));
		double[] ds = { 1.0, 2.0 };
		assertEquals(1, ArrayUtils.indexOf(ds, 2.0));
		assertTrue(ArrayUtils.contains(ds, 1.0));
		assertTrue(ArrayUtils.notContains(ds, 3.0));
		assertEquals(-1, ArrayUtils.indexOf((double[]) null, 1.0));
		long[] ls = { 1L, 2L };
		assertEquals(1, ArrayUtils.indexOf(ls, 2L));
		assertTrue(ArrayUtils.contains(ls, 1L));
		assertTrue(ArrayUtils.notContains(ls, 3L));
		float[] fs = { 1f, 2f };
		assertEquals(1, ArrayUtils.indexOf(fs, 2f));
		assertTrue(ArrayUtils.contains(fs, 1f));
		assertTrue(ArrayUtils.notContains(fs, 3f));
		int[] is = { 1, 2 };
		assertEquals(1, ArrayUtils.indexOf(is, 2));
		assertTrue(ArrayUtils.contains(is, 1));
		assertTrue(ArrayUtils.notContains(is, 3));
		assertEquals(-1, ArrayUtils.indexOf((int[]) null, 1));
		short[] ss = { 1, 2 };
		assertEquals(1, ArrayUtils.indexOf(ss, (short) 2));
		assertEquals(-1, ArrayUtils.indexOf(ss, (short) 1, 1));
		assertTrue(ArrayUtils.contains(ss, (short) 1));
		assertTrue(ArrayUtils.notContains(ss, (short) 3));
		boolean[] bs = { true, false };
		assertEquals(1, ArrayUtils.indexOf(bs, false));
		assertEquals(-1, ArrayUtils.indexOf(bs, true, 1));
		assertTrue(ArrayUtils.contains(bs, true));
		assertFalse(ArrayUtils.notContains(bs, false));
		assertEquals(-1, ArrayUtils.indexOf((boolean[]) null, true));
	}

	@Test
	public void testCapacity() {
		String[] a = { "a", "b", "c" };
		assertSame(a, ArrayUtils.ensureCapacity(a, 3));
		assertArrayEquals(new String[] { "a", "b" }, ArrayUtils.ensureCapacity(a, 2));
		assertArrayEquals(new String[] { "a", "b", "c" }, ArrayUtils.ensureCapacity(a, 5));
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.ensureCapacity(null, 1));
		assertArrayEquals(new String[] { "a", "b", "c", null, null, null }, ArrayUtils.expandCapacity(a));
		assertArrayEquals(new String[] { "a", "b", "c", null }, ArrayUtils.expandCapacity(a, 1));
		assertArrayEquals(a, ArrayUtils.expandCapacity(a, -1));
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.expandCapacity(null));
	}

	@Test
	public void testSort() {
		Integer[] small = { 3, 1, 2 };
		ArrayUtils.asc(small);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, small);
		ArrayUtils.desc(small);
		assertArrayEquals(new Integer[] { 3, 2, 1 }, small);

		Random random = new Random(42);
		Integer[] big = new Integer[200];
		for (int i = 0; i < big.length; i++) {
			big[i] = random.nextInt(1000);
		}
		Integer[] expected = big.clone();
		Arrays.sort(expected);
		Integer[] copy = big.clone();
		ArrayUtils.asc(copy);
		assertArrayEquals(expected, copy);

		copy = big.clone();
		ArrayUtils.desc(copy);
		Arrays.sort(expected, Comparator.reverseOrder());
		assertArrayEquals(expected, copy);

		String[] words = new String[57];
		for (int i = 0; i < words.length; i++) {
			words[i] = Integer.toString(random.nextInt(100000), 36);
		}
		String[] wordsExpected = words.clone();
		Arrays.sort(wordsExpected, Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
		ArrayUtils.sort(words, Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
		assertArrayEquals(wordsExpected, words);

		Integer[] empty = new Integer[0];
		ArrayUtils.asc(empty);
		ArrayUtils.desc(empty);
		ArrayUtils.sort(empty, Comparator.naturalOrder());
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.asc((Integer[]) null));
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.sort(small, null));
	}

	@Test
	public void testNewArray() {
		String[] a = ArrayUtils.newArray(String.class, 3);
		assertArrayEquals(new String[3], a);
		String[] b = ArrayUtils.newArray(String.class, 2, "x");
		assertArrayEquals(new String[] { "x", "x" }, b);
		String[][] c = ArrayUtils.newArray(String.class, 2, 3);
		assertEquals(2, c.length);
		assertArrayEquals(new String[3], c[1]);
		String[][] d = ArrayUtils.newArray(String.class, 2, 1, "y");
		assertArrayEquals(new String[] { "y" }, d[0]);
		assertNotSame(d[0], d[1]);
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.newArray(null, 1));
	}

	@Test
	public void testConcatAddRemove() {
		String[] a = { "a", "b" };
		assertArrayEquals(new String[] { "a", "b", "c" }, ArrayUtils.concat(a, new String[] { "c" }));
		assertArrayEquals(new String[] { "a", "b", "c" }, ArrayUtils.add(a, "c"));
		assertArrayEquals(new String[] { "b" }, ArrayUtils.remove(a, "a"));
		assertSame(a, ArrayUtils.remove(a, "z"));
		assertArrayEquals(new String[] { "a" }, ArrayUtils.removeAt(a, -1));
		assertArrayEquals(new String[] { "b" }, ArrayUtils.removeAt(a, 0));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> ArrayUtils.removeAt(a, 2));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> ArrayUtils.removeAt(a, -3));
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.concat(null, a));
	}

	@Test
	public void testCopy() {
		String[] a = { "a", "b", "c" };
		String[] c = ArrayUtils.copy(a);
		assertArrayEquals(a, c);
		assertNotSame(a, c);
		assertArrayEquals(new String[] { "b", "c" }, ArrayUtils.copy(a, 1));
		assertArrayEquals(new String[] { "b" }, ArrayUtils.copy(a, 1, 1));
		assertArrayEquals(new String[] { "c", "z", "z" }, ArrayUtils.copy(a, 2, 3, "z"));
	}

	@Test
	public void testJoin() {
		Object[] a = { " a ", null, 1 };
		assertEquals("a,,1", ArrayUtils.join(a));
		assertEquals("[a,,1]", ArrayUtils.toString(a));
		assertEquals("[a||1]", ArrayUtils.toString(a, "|"));
		assertEquals(" a ;;1", ArrayUtils.join(a, ";", false));
		assertEquals("a1", ArrayUtils.join(new Object[] { "a", 1 }, (String) null));
		assertEquals("", ArrayUtils.join((Object[]) null));
		assertEquals("", ArrayUtils.join(new Object[0]));
		assertEquals("A-B", ArrayUtils.join(new String[] { "a", "b" }, s -> s.toUpperCase(), "-", true));

		String[] keys = { "a", "b", "c" };
		Integer[] values = { 1, 2 };
		assertEquals("a,1,b,2", ArrayUtils.join(keys, values));
		assertEquals("a;1;b;2", ArrayUtils.join(keys, values, ";"));
		assertEquals("a=1&b=2", ArrayUtils.join(keys, values, "=", "&"));
		assertEquals("a1b2", ArrayUtils.join(keys, values, null, null));
		assertEquals("", ArrayUtils.join(keys, new Integer[0]));
		assertEquals("", ArrayUtils.join(null, values));
	}

	@Test
	public void testDeepEqualsAndHashCode() {
		assertTrue(ArrayUtils.deepEquals(null, null));
		Object[] a = { "a", new int[] { 1, 2 } };
		Object[] b = { "a", new int[] { 1, 2 } };
		assertTrue(ArrayUtils.deepEquals(a, a));
		assertTrue(ArrayUtils.deepEquals(a, b));
		assertEquals(ArrayUtils.deepHashCode(a), ArrayUtils.deepHashCode(b));
		assertFalse(ArrayUtils.deepEquals(a, null));
		assertFalse(ArrayUtils.deepEquals(null, a));
		assertFalse(ArrayUtils.deepEquals(new Object[] { "a" }, new String[] { "a" }));
		assertFalse(ArrayUtils.deepEquals(new Object[] { "a" }, new Object[] { "a", "b" }));
		assertFalse(ArrayUtils.deepEquals(new Object[] { "a" }, new Object[] { "b" }));
		assertEquals(0, ArrayUtils.deepHashCode(null));
		assertEquals(0, ArrayUtils.deepHashCode(new Object[0]));
		assertEquals(Arrays.hashCode(new Object[] { "x", "y" }), ArrayUtils.deepHashCode(new Object[] { "x", "y" }));
	}

	@Test
	public void testToStringArray() {
		assertArrayEquals(new String[] { "1", "", "a" }, ArrayUtils.toStringArray(new Object[] { 1, null, " a " }, true));
		assertArrayEquals(new String[] { " a " }, ArrayUtils.toStringArray(new Object[] { " a " }, false));
		assertNull(ArrayUtils.toStringArray(null, true));
	}

	@Test
	public void testSubarrayAndContainsAll() {
		String[] a = { "a", "b", "c", "d" };
		assertTrue(ArrayUtils.isSubarray(a, new String[] { "b", "c" }));
		assertTrue(ArrayUtils.isSubarray(a, new String[] { new String("c"), "d" }));
		assertTrue(ArrayUtils.isSubarray(a, new String[] { "a" }));
		assertFalse(ArrayUtils.isSubarray(a, new String[] { "b", "d" }));
		assertFalse(ArrayUtils.isSubarray(a, new String[] { "d", "e" }));
		assertFalse(ArrayUtils.isSubarray(a, new String[0]));
		assertFalse(ArrayUtils.isSubarray(new String[] { "a" }, new String[] { "a", "b" }));
		assertTrue(ArrayUtils.containsAll(a, new String[] { "d", "a" }));
		assertFalse(ArrayUtils.containsAll(new String[] { "a" }, new String[] { "a", "b" }));
		assertFalse(ArrayUtils.containsAll(a, new String[] { "x" }));
		assertFalse(ArrayUtils.containsAll(null, a));
	}

	@Test
	public void testSame() {
		assertTrue(ArrayUtils.same(new Object[] { 1, 1, 1 }));
		assertTrue(ArrayUtils.same(new Object[] { null }));
		assertFalse(ArrayUtils.same(new Object[] { 1, 2 }));
		assertFalse(ArrayUtils.same(null));
	}

	@Test
	public void testScroll() {
		Integer[] a = { 1, 2, 3, 4, 5 };
		ArrayUtils.rightScroll(a, 2);
		assertArrayEquals(new Integer[] { 4, 5, 1, 2, 3 }, a);
		ArrayUtils.leftScroll(a, 2);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 }, a);
		ArrayUtils.leftScroll(a, 7);
		assertArrayEquals(new Integer[] { 3, 4, 5, 1, 2 }, a);
		ArrayUtils.rightScroll(a, 7);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 }, a);
		ArrayUtils.leftScroll(a, 5);
		ArrayUtils.rightScroll(a, 0);
		ArrayUtils.leftScroll(null, 1);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 }, a);
	}

	@Test
	public void testSliceAndDivide() {
		Integer[] a = { 1, 2, 3, 4, 5 };
		Object[][] s = ArrayUtils.slice(a, 2);
		assertEquals(3, s.length);
		assertArrayEquals(new Integer[] { 1, 2 }, s[0]);
		assertArrayEquals(new Integer[] { 5 }, s[2]);
		assertEquals(1, ArrayUtils.slice(a, 5).length);
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.slice(new Integer[0], 2));
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.slice(a, 0));

		Object[][] d = ArrayUtils.divide(a, 2);
		assertEquals(2, d.length);
		assertArrayEquals(new Integer[] { 1, 2 }, d[0]);
		assertArrayEquals(new Integer[] { 3, 4, 5 }, d[1]);
		d = ArrayUtils.divide(new Integer[] { 1, 2 }, 4);
		assertEquals(4, d.length);
		assertArrayEquals(new Integer[] { 1 }, d[0]);
		assertArrayEquals(new Integer[] { 2 }, d[1]);
		assertEquals(0, d[2].length);
		assertEquals(0, d[3].length);
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.divide(a, 0));
	}

	@Test
	public void testShuffle() {
		Integer[] a = { 1, 2, 3, 4, 5, 6, 7, 8 };
		ArrayUtils.shuffle(a, new Random(1));
		Integer[] sorted = a.clone();
		Arrays.sort(sorted);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8 }, sorted);
		ArrayUtils.shuffle(a);
		Arrays.sort(a);
		assertArrayEquals(sorted, a);
		ArrayUtils.shuffle(null);
	}

	@Test
	public void testFirstLast() {
		Object[] a = { 1, 2, 3 };
		assertEquals(1, ArrayUtils.getFirst(a));
		assertEquals(3, ArrayUtils.getLast(a));
		assertNull(ArrayUtils.getFirst(null));
		assertNull(ArrayUtils.getLast(new Object[0]));
		assertEquals("x", ArrayUtils.getFirst(null, "x"));
		assertEquals("x", ArrayUtils.getLast(null, "x"));
	}

	@Test
	public void testMapAndCast() {
		Integer[] r = ArrayUtils.map(new String[] { "1", "22" }, String::length, Integer.class);
		assertArrayEquals(new Integer[] { 1, 2 }, r);
		String[] s = ArrayUtils.cast(new Object[] { "a", null }, String.class);
		assertArrayEquals(new String[] { "a", null }, s);
		assertThrows(ClassCastException.class, () -> ArrayUtils.cast(new Object[] { 1 }, String.class));
		assertNull(ArrayUtils.map((String[]) null, String::length, Integer.class));
		assertNull(ArrayUtils.cast(null, String.class));
	}

}
