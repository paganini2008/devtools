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
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.paganini2008.devtools.BooleanFormatException;

/**
 * BooleansTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class BooleansTest {

	private static final boolean T = true;
	private static final boolean F = false;

	@Test
	public void testBasics() {
		assertNull(Booleans.clone(null));
		boolean[] a = { T, F };
		assertNotSame(a, Booleans.clone(a));
		assertArrayEquals(a, Booleans.clone(a));
		assertEquals(0, Booleans.length(null));
		assertEquals(2, Booleans.length(a));
		assertArrayEquals(new boolean[] { F, F }, Booleans.create(2));
		assertArrayEquals(new boolean[] { T, T }, Booleans.create(2, T));
		assertArrayEquals(new boolean[] { F, F, F }, Booleans.create(2, 3)[1]);
		assertArrayEquals(new boolean[] { T, T, T }, Booleans.create(2, 3, T)[1]);
		assertTrue(Booleans.isEmpty(null));
		assertTrue(Booleans.isNotEmpty(a));
		assertEquals(0, Booleans.EMPTY_ARRAY.length);
		assertEquals(0, Booleans.EMPTY_OBJECT_ARRAY.length);
	}

	@Test
	public void testIndexOf() {
		boolean[] a = { T, F, T };
		assertEquals(1, Booleans.indexOf(a, F));
		assertEquals(2, Booleans.indexOf(a, T, 1, 100));
		assertEquals(-1, Booleans.indexOf(a, T, -1, 3));
		assertEquals(-1, Booleans.indexOf(null, T));
		assertEquals(-1, Booleans.indexOf(new boolean[] { T }, F));
		assertEquals(2, Booleans.lastIndexOf(a, T));
		assertEquals(0, Booleans.lastIndexOf(a, T, 1));
		assertEquals(-1, Booleans.lastIndexOf(new boolean[] { T }, F));
		assertEquals(-1, Booleans.lastIndexOf(null, T));
		assertEquals(-1, Booleans.lastIndexOf(a, T, -1));
		assertTrue(Booleans.contains(a, F));
		assertTrue(Booleans.notContains(new boolean[] { T }, F));
	}

	@Test
	public void testConcatAddRemoveCopy() {
		assertArrayEquals(new boolean[] { T, F, T }, Booleans.concat(new boolean[] { T }, new boolean[] { F, T }));
		assertThrows(IllegalArgumentException.class, () -> Booleans.concat(null, new boolean[] { T }));
		assertArrayEquals(new boolean[] { T, F }, Booleans.add(new boolean[] { T }, F));
		assertArrayEquals(new boolean[] { T, T }, Booleans.remove(new boolean[] { T, F, T }, F));
		boolean[] a = { T, T };
		assertSame(a, Booleans.remove(a, F));
		assertArrayEquals(new boolean[] { T }, Booleans.removeAt(a, -1));
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> Booleans.removeAt(a, 2));
		assertArrayEquals(a, Booleans.copy(a));
		assertArrayEquals(new boolean[] { T, T, F }, Booleans.copy(a, 3));
		assertArrayEquals(new boolean[] { F, F }, Booleans.copy(new boolean[] { T, F }, 1, 2));
		assertArrayEquals(new boolean[] { F, T }, Booleans.copy(new boolean[] { T, F }, 1, 2, T));
	}

	@Test
	public void testWrappersAndCapacity() {
		assertArrayEquals(new Boolean[] { T, F }, Booleans.toWrappers(new boolean[] { T, F }));
		assertArrayEquals(new boolean[] { T, F }, Booleans.toPrimitives(new Boolean[] { T, null, F }));
		boolean[] a = { T, T };
		assertSame(a, Booleans.ensureCapacity(a, 2));
		assertArrayEquals(new boolean[] { T }, Booleans.ensureCapacity(a, 1));
		assertArrayEquals(new boolean[] { T, T, F, F }, Booleans.expandCapacity(a));
		assertArrayEquals(new boolean[] { T, T, F }, Booleans.expandCapacity(a, 1));
	}

	@Test
	public void testJoin() {
		assertEquals("[true|false]", Booleans.toString(new boolean[] { T, F }, "|"));
		assertEquals("true,false", Booleans.join(new boolean[] { T, F }));
		assertEquals("truefalse", Booleans.join(new boolean[] { T, F }, null));
		assertEquals("", Booleans.join((boolean[]) null));
		assertEquals("true=false,false=true", Booleans.join(new boolean[] { T, F }, new boolean[] { F, T }, "=", ","));
		assertEquals("truefalse", Booleans.join(new boolean[] { T }, new boolean[] { F }, null));
		assertEquals("", Booleans.join(null, new boolean[] { F }, ","));
	}

	@Test
	public void testCollectionsAndHash() {
		assertArrayEquals(new boolean[] { T, F }, Booleans.toArray(Arrays.asList(T, null, F)));
		assertEquals(Arrays.asList(T, F), Booleans.toList(new boolean[] { T, F }));
		assertEquals(Boolean.hashCode(T), Booleans.hashCode(T));
		boolean[] a = { T, F };
		assertTrue(Booleans.deepEquals(a, a));
		assertTrue(Booleans.deepEquals(null, null));
		assertFalse(Booleans.deepEquals(null, a));
		assertFalse(Booleans.deepEquals(a, null));
		assertFalse(Booleans.deepEquals(a, new boolean[] { T }));
		assertFalse(Booleans.deepEquals(a, new boolean[] { T, T }));
		assertTrue(Booleans.deepEquals(a, new boolean[] { T, F }));
		assertEquals(Arrays.hashCode(a), Booleans.deepHashCode(a));
		assertEquals(0, Booleans.deepHashCode(null));
	}

	@ParameterizedTest
	@CsvSource({ "TRUE,true", "false,false", "yes,true", "NO,false", "on,true", "off,false", "1,true", "0,false" })
	public void testParseString(String input, boolean expected) {
		assertEquals(expected, Booleans.parse(input));
		assertEquals(Boolean.valueOf(expected), Booleans.valueOf(input));
	}

	@ParameterizedTest
	@CsvSource({ "T,true", "f,false", "Y,true", "n,false", "1,true", "0,false" })
	public void testParseChar(char input, boolean expected) {
		assertEquals(expected, Booleans.parse(input));
		assertEquals(Boolean.valueOf(expected), Booleans.valueOf(Character.valueOf(input)));
	}

	@Test
	public void testParseInvalid() {
		assertThrows(BooleanFormatException.class, () -> Booleans.parse("maybe"));
		assertThrows(BooleanFormatException.class, () -> Booleans.parse('x'));
		assertThrows(IllegalArgumentException.class, () -> Booleans.parse(" "));
		assertNull(Booleans.valueOf("maybe"));
		assertEquals(Boolean.TRUE, Booleans.valueOf("maybe", true));
		assertEquals(Boolean.TRUE, Booleans.valueOf("", true));
		assertNull(Booleans.valueOf((Character) null));
		assertNull(Booleans.valueOf('x'));
		assertArrayEquals(new Boolean[] { T, null }, Booleans.valuesOf(new String[] { "yes", "x" }));
		assertArrayEquals(new Boolean[] { T, F }, Booleans.valuesOf(new String[] { "yes", "x" }, F));
		assertArrayEquals(new boolean[] { T, F }, Booleans.parseMany(new String[] { "yes", "off" }));
		assertThrows(BooleanFormatException.class, () -> Booleans.parseMany(new String[] { "x" }));
		// unparsable elements are left as false when not thrown
		assertArrayEquals(new boolean[] { F, T }, Booleans.parseMany(new String[] { "x", "on" }, false));
		assertArrayEquals(new boolean[] { T, F }, Booleans.parseMany(new char[] { 'y', 'n' }));
		assertThrows(BooleanFormatException.class, () -> Booleans.parseMany(new char[] { 'x' }));
		assertArrayEquals(new boolean[] { F, T }, Booleans.parseMany(new char[] { 'x', 'y' }, false));
	}

	@Test
	public void testAddTrueAndFalse() throws Exception {
		assertThrows(BooleanFormatException.class, () -> Booleans.parse("oui"));
		CountDownLatch start = new CountDownLatch(1);
		AtomicReference<Throwable> error = new AtomicReference<Throwable>();
		Thread[] threads = new Thread[4];
		for (int i = 0; i < threads.length; i++) {
			final int n = i;
			threads[i] = new Thread(() -> {
				try {
					start.await();
					for (int j = 0; j < 50; j++) {
						Booleans.addTrueAndFalse("t" + n + "_" + j, "f" + n + "_" + j);
					}
				} catch (Throwable e) {
					error.set(e);
				}
			});
			threads[i].start();
		}
		start.countDown();
		for (Thread t : threads) {
			t.join();
		}
		assertNull(error.get());
		for (int i = 0; i < threads.length; i++) {
			for (int j = 0; j < 50; j++) {
				assertTrue(Booleans.parse("t" + i + "_" + j));
				assertFalse(Booleans.parse("f" + i + "_" + j));
			}
		}
		Booleans.addTrueAndFalse("oui", "non");
		assertTrue(Booleans.parse("oui"));
		assertFalse(Booleans.parse("non"));
		Booleans.addTrueAndFalse('J', 'K');
		assertTrue(Booleans.parse('J'));
		assertFalse(Booleans.parse('K'));
	}

	@Test
	public void testNumberAndToString() {
		assertEquals(Boolean.TRUE, Booleans.valueOf(5));
		assertEquals(Boolean.FALSE, Booleans.valueOf(0));
		assertEquals(Boolean.FALSE, Booleans.valueOf(-1L));
		assertEquals(Boolean.FALSE, Booleans.valueOf(new BigDecimal("0.5")));
		assertNull(Booleans.valueOf((Number) null));
		assertEquals(Boolean.TRUE, Booleans.valueOf((Number) null, true));
		assertEquals("yes", Booleans.toStringYesNo(Boolean.TRUE));
		assertEquals("no", Booleans.toStringYesNo(null));
		assertEquals("on", Booleans.toStringOnOff(Boolean.TRUE));
		assertEquals("off", Booleans.toStringOnOff(Boolean.FALSE));
		assertEquals("true", Booleans.toString(Boolean.TRUE));
		assertEquals("false", Booleans.toString((Boolean) null));
		assertEquals("?", Booleans.toString(null, "1", "0", "?"));
		assertEquals("0", Booleans.toString(Boolean.FALSE, "1", "0", "?"));
		assertArrayEquals(new String[] { "true", "false" }, Booleans.toStringArray(new boolean[] { T, F }));
		assertNull(Booleans.toStringArray(null));
	}

	@Test
	public void testPredicates() {
		assertTrue(Booleans.isSameLength(null, new boolean[0]));
		assertFalse(Booleans.isSameLength(null, new boolean[1]));
		assertTrue(Booleans.isSameLength(new boolean[0], null));
		assertFalse(Booleans.isSameLength(new boolean[1], null));
		assertTrue(Booleans.isSameLength(new boolean[1], new boolean[1]));
		assertTrue(Booleans.isSubarray(new boolean[] { T, F, F, T }, new boolean[] { F, T }));
		assertFalse(Booleans.isSubarray(new boolean[] { T, T, F }, new boolean[] { F, F }));
		assertFalse(Booleans.isSubarray(new boolean[] { T, F, T }, new boolean[] { F, F }));
		assertFalse(Booleans.isSubarray(new boolean[] { T }, new boolean[] { T, T }));
		assertTrue(Booleans.containsAll(new boolean[] { T, F }, new boolean[] { F, F, T }));
		assertFalse(Booleans.containsAll(new boolean[] { T }, new boolean[] { F }));
		assertFalse(Booleans.containsAll(null, new boolean[] { F }));
		assertTrue(Booleans.same(new boolean[] { T, T }));
		assertFalse(Booleans.same(new boolean[] { T, F }));
		assertFalse(Booleans.same(null));
		assertEquals(0, Booleans.compare(T, T));
		assertEquals(1, Booleans.compare(T, F));
		assertEquals(-1, Booleans.compare(F, T));
		assertTrue(Booleans.defaultComparator().compare(new boolean[] { F }, new boolean[] { T }) < 0);
		assertTrue(Booleans.defaultComparator().compare(new boolean[] { T, F }, new boolean[] { T }) > 0);
		assertEquals(0, Booleans.defaultComparator().compare(new boolean[] { T }, new boolean[] { T }));
	}

}
