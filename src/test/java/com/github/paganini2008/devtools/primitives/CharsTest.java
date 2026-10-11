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

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.paganini2008.devtools.CharValueOverflowException;

/**
 * CharsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CharsTest {

	private static char[] c(String s) {
		return s.toCharArray();
	}

	@Test
	public void testBasics() {
		assertNull(Chars.clone(null));
		char[] a = c("abc");
		assertNotSame(a, Chars.clone(a));
		assertArrayEquals(a, Chars.clone(a));
		assertEquals(0, Chars.length(null));
		assertEquals(3, Chars.length(a));
		assertArrayEquals(c("xx"), Chars.create(2, 'x'));
		assertArrayEquals(new char[2], Chars.create(2, Character.MIN_VALUE));
		assertArrayEquals(c("zzz"), Chars.create(2, 3, 'z')[1]);
		assertTrue(Chars.isEmpty(null));
		assertTrue(Chars.isNotEmpty(a));
		assertEquals('\r', Chars.CR);
		assertEquals('\n', Chars.LF);
		assertEquals(0, Chars.EMPTY_ARRAY.length);
		assertEquals(0, Chars.EMPTY_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		char[] a = c("abcb");
		assertEquals(1, Chars.indexOf(a, 'b'));
		assertEquals(3, Chars.indexOf(a, 'b', 2, 100));
		assertEquals(-1, Chars.indexOf(null, 'b'));
		assertEquals(-1, Chars.indexOf(a, 'b', -1, 4));
		assertEquals(3, Chars.lastIndexOf(a, 'b'));
		assertEquals(1, Chars.lastIndexOf(a, 'b', 2));
		assertEquals(-1, Chars.lastIndexOf(a, 'z'));
		assertEquals(-1, Chars.lastIndexOf(null, 'z'));
		assertEquals(-1, Chars.lastIndexOf(a, 'b', -1));
		assertTrue(Chars.contains(a, 'c'));
		assertTrue(Chars.notContains(a, 'z'));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(c("abc"), Chars.concat(c("a"), c("bc")));
		assertThrows(IllegalArgumentException.class, () -> Chars.concat(null, c("a")));
		assertArrayEquals(c("ab"), Chars.add(c("a"), 'b'));
		assertArrayEquals(c("ac"), Chars.remove(c("abc"), 'b'));
		char[] a = c("abc");
		assertSame(a, Chars.remove(a, 'z'));
		assertArrayEquals(c("ab"), Chars.removeAt(a, -1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Chars.removeAt(a, 3));
		assertArrayEquals(a, Chars.copy(a));
		assertArrayEquals(c("ab"), Chars.copy(a, 2));
		assertArrayEquals(new char[] { 'b', 'c', 0 }, Chars.copy(a, 1, 3));
		assertArrayEquals(c("c-"), Chars.copy(a, 2, 2, '-'));
	}

	@Test
	public void testWrappersAndCapacity() {
		assertArrayEquals(new Character[] { 'a', 'b' }, Chars.toWrappers(c("ab")));
		assertArrayEquals(c("ac"), Chars.toPrimitives(new Character[] { 'a', null, 'c' }));
		char[] a = c("abc");
		assertSame(a, Chars.ensureCapacity(a, 3));
		assertArrayEquals(c("a"), Chars.ensureCapacity(a, 1));
		assertArrayEquals(new char[] { 'a', 'b', 'c', 0, 0, 0 }, Chars.expandCapacity(a));
		assertArrayEquals(new char[] { 'a', 'b', 'c', 0 }, Chars.expandCapacity(a, 1));
	}

	@Test
	public void testJoinMaxMin() {
		assertEquals("[a,b]", Chars.toString(c("ab")));
		assertEquals("ab", Chars.join(c("ab"), null));
		assertEquals("", Chars.join(null));
		assertEquals("a=c,b=d", Chars.join(c("ab"), c("cd"), "=", ","));
		assertEquals("acbd", Chars.join(c("ab"), c("cd"), null));
		assertEquals("", Chars.join(null, c("a"), ","));
		assertEquals('z', Chars.max(c("azb")));
		assertEquals('a', Chars.min(c("zab")));
		assertThrows(IllegalArgumentException.class, () -> Chars.max(null));
		assertThrows(IllegalArgumentException.class, () -> Chars.min(new char[0]));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 9, 10, 11, 64, 300 })
	public void testSort(int size) {
		Random random = new Random(size);
		char[] a = new char[size];
		for (int i = 0; i < size; i++) {
			a[i] = (char) ('a' + random.nextInt(26));
		}
		char[] expected = a.clone();
		Arrays.sort(expected);
		char[] asc = a.clone();
		Chars.asc(asc);
		assertArrayEquals(expected, asc);
		char[] desc = a.clone();
		Chars.desc(desc);
		Chars.reverse(desc);
		assertArrayEquals(expected, desc);
	}

	@Test
	public void testSetOperations() {
		char[] left = new char[] { 0, 'a', 'b', 'b' };
		char[] right = new char[] { 'b', 'c', 0 };
		assertArrayEquals(new char[] { 0, 'a', 'b', 'b', 'b', 'c', 0 }, Chars.unionAll(left, right));
		assertArrayEquals(new char[] { 0, 'a', 'b', 'c' }, Chars.union(left, right));
		assertArrayEquals(c("a"), Chars.minus(left, right));
		assertArrayEquals(new char[] { 0, 'b', 'b' }, Chars.intersect(left, right));
		assertNull(Chars.unionAll(null, null));
		assertSame(left, Chars.unionAll(left, null));
		assertSame(right, Chars.unionAll(null, right));
		assertNull(Chars.union(null, null));
		assertSame(left, Chars.union(left, null));
		assertSame(right, Chars.union(null, right));
		assertNull(Chars.minus(null, null));
		assertSame(left, Chars.minus(left, null));
		assertSame(right, Chars.minus(null, right));
		assertNull(Chars.intersect(null, null));
		assertSame(left, Chars.intersect(left, null));
		assertSame(right, Chars.intersect(null, right));
	}

	@Test
	public void testCollectionsAndConversions() {
		assertArrayEquals(c("ac"), Chars.toArray(Arrays.asList('a', null, 'c')));
		assertEquals(Arrays.asList('a', 'b'), Chars.toList(c("ab")));
		assertEquals(Arrays.asList('x', 'y'), Chars.toCharacterList("xy"));
		assertArrayEquals(new Character[] { 'x', 'y', 'z' }, Chars.toCharacterArray("xyz"));
		assertEquals('Y', Chars.cast(true));
		assertEquals('N', Chars.cast(false));
		assertArrayEquals(c("YN"), Chars.casts(new boolean[] { true, false }));
		assertEquals('a', Chars.cast(97));
		assertThrows(CharValueOverflowException.class, () -> Chars.cast(-1));
		assertThrows(CharValueOverflowException.class, () -> Chars.cast(0x10000));
		assertArrayEquals(c("ab"), Chars.casts(new int[] { 97, 98 }));
		assertThrows(CharValueOverflowException.class, () -> Chars.casts(new int[] { -1 }));
		assertArrayEquals(new char[] { 0, 'a' }, Chars.casts(new int[] { -1, 97 }, false));
		assertEquals(Character.valueOf('Y'), Chars.valueOf(Boolean.TRUE));
		assertNull(Chars.valueOf((Boolean) null));
		assertEquals(Character.valueOf('a'), Chars.valueOf(Integer.valueOf(97)));
		assertNull(Chars.valueOf((Integer) null));
		assertEquals(Character.valueOf('?'), Chars.valueOf(Integer.valueOf(-1), '?'));
		assertEquals(Character.valueOf('h'), Chars.valueOf("hello"));
		assertNull(Chars.valueOf(" "));
		assertEquals(Character.valueOf('?'), Chars.valueOf((String) null, '?'));
	}

	@Test
	public void testMisc() {
		char[] a = c("abc");
		Chars.swap(a, 0, 2);
		assertArrayEquals(c("cba"), a);
		Chars.reverse(null);
		char[] b = c("abcde");
		Chars.shuffle(b, new Random(3));
		Chars.shuffle(b);
		Chars.asc(b);
		assertArrayEquals(c("abcde"), b);
		Chars.shuffle(null);
		assertEquals(Character.hashCode('a'), Chars.hashCode('a'));
		assertTrue(Chars.deepEquals(a, a));
		assertTrue(Chars.deepEquals(null, null));
		assertFalse(Chars.deepEquals(null, a));
		assertFalse(Chars.deepEquals(a, null));
		assertFalse(Chars.deepEquals(a, c("c")));
		assertFalse(Chars.deepEquals(a, c("cbx")));
		assertTrue(Chars.deepEquals(a, c("cba")));
		assertEquals(Arrays.hashCode(a), Chars.deepHashCode(a));
		assertEquals(0, Chars.deepHashCode(null));
		assertArrayEquals(new String[] { "a", "b" }, Chars.toStringArray(c("ab")));
		assertNull(Chars.toStringArray(null));
	}

	@Test
	public void testCharClasses() {
		assertTrue(Chars.isAlpha('a'));
		assertTrue(Chars.isAlpha('Z'));
		assertFalse(Chars.isAlpha('1'));
		assertTrue(Chars.isPrintable(' '));
		assertFalse(Chars.isPrintable('\n'));
		assertFalse(Chars.isPrintable((char) 127));
		assertTrue(Chars.isControl('\t'));
		assertTrue(Chars.isControl((char) 127));
		assertFalse(Chars.isControl('a'));
		assertTrue(Chars.isAscii('~'));
		assertFalse(Chars.isAscii('é'));
		assertTrue(Chars.isUpper('Q'));
		assertFalse(Chars.isUpper('q'));
		assertTrue(Chars.isLower('q'));
		assertFalse(Chars.isLower('Q'));
		assertTrue(Chars.isNumber('7'));
		assertFalse(Chars.isNumber('x'));
		assertTrue(Chars.isAlphaNumber('7'));
		assertTrue(Chars.isAlphaNumber('x'));
		assertFalse(Chars.isAlphaNumber('-'));
	}

	@Test
	public void testToUnicode() {
		assertEquals("\\u0009", Chars.toUnicode('\t'));
		assertEquals("\\u0041", Chars.toUnicode('A'));
		assertEquals("\\u00e9", Chars.toUnicode('é'));
		assertEquals("\\u0100", Chars.toUnicode('Ā'));
		assertEquals("\\u4e2d", Chars.toUnicode('中'));
	}

	@Test
	public void testPredicates() {
		assertTrue(Chars.isSameLength(null, new char[0]));
		assertFalse(Chars.isSameLength(null, c("a")));
		assertTrue(Chars.isSameLength(new char[0], null));
		assertFalse(Chars.isSameLength(c("a"), null));
		assertTrue(Chars.isSameLength(c("a"), c("b")));
		assertTrue(Chars.isSubarray(c("abcd"), c("bc")));
		assertFalse(Chars.isSubarray(c("abc"), c("cd")));
		assertFalse(Chars.isSubarray(c("abc"), c("bd")));
		assertFalse(Chars.isSubarray(c("a"), c("ab")));
		assertTrue(Chars.containsAll(c("ab"), c("bba")));
		assertFalse(Chars.containsAll(c("ab"), c("c")));
		assertFalse(Chars.containsAll(null, c("c")));
		assertTrue(Chars.same(c("aaa")));
		assertFalse(Chars.same(c("ab")));
		assertFalse(Chars.same(null));
		char[] a = c("abcde");
		Chars.rightScroll(a, 1);
		assertArrayEquals(c("eabcd"), a);
		Chars.leftScroll(a, 6);
		assertArrayEquals(c("abcde"), a);
		assertTrue(Chars.compare('a', 'b') < 0);
		assertEquals(0, Chars.compare('a', 'a'));
		assertTrue(Chars.defaultComparator().compare(c("a"), c("b")) < 0);
		assertTrue(Chars.defaultComparator().compare(c("ab"), c("a")) > 0);
		assertEquals(0, Chars.defaultComparator().compare(c("ab"), c("ab")));
	}

}
