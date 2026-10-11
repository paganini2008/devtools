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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.primitives.Bytes;
import com.github.paganini2008.devtools.primitives.Chars;
import com.github.paganini2008.devtools.primitives.Doubles;
import com.github.paganini2008.devtools.primitives.Floats;
import com.github.paganini2008.devtools.primitives.Ints;
import com.github.paganini2008.devtools.primitives.Longs;
import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ObjectUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ObjectUtilsTest {

	@Test
	public void testToString() {
		assertEquals("", ObjectUtils.toString(null));
		assertEquals("a", ObjectUtils.toString(" a "));
		assertEquals("d", ObjectUtils.toString(null, "d"));
		assertEquals(" a ", ObjectUtils.toString(" a ", false, "d"));
	}

	@Test
	public void testToStringSelectively() {
		assertEquals("", ObjectUtils.toStringSelectively(null));
		assertEquals("abc", ObjectUtils.toStringSelectively(" abc "));
		assertEquals("12", ObjectUtils.toStringSelectively(12));
		assertEquals("true", ObjectUtils.toStringSelectively(Boolean.TRUE));
		assertEquals(new StringBuilder("sb").toString(), ObjectUtils.toStringSelectively(new StringBuilder("sb")));
		Date date = new Date(0);
		assertEquals(date.toString(), ObjectUtils.toStringSelectively(date));
		Object obj = new Object();
		assertEquals(Object.class.toString(), ObjectUtils.toStringSelectively(obj));
		assertEquals(Arrays.asList(1).getClass().toString(), ObjectUtils.toStringSelectively(Arrays.asList(1), cls -> false));
		assertEquals("[1]", ObjectUtils.toStringSelectively(Arrays.asList(1), cls -> true));
	}

	@Test
	public void testEqualsAndHashCode() {
		assertTrue(ObjectUtils.equals(null, null));
		assertFalse(ObjectUtils.equals(null, "a"));
		assertTrue(ObjectUtils.equals("a", "a"));
		assertTrue(ObjectUtils.notEquals("a", "b"));
		assertEquals(0, ObjectUtils.hashCode(null));
		assertEquals("a".hashCode(), ObjectUtils.hashCode("a"));
	}

	@Test
	public void testTypes() {
		assertTrue(ObjectUtils.isArray(new int[0]));
		assertFalse(ObjectUtils.isArray(null));
		assertTrue(ObjectUtils.isNotArray("a"));
		assertTrue(ObjectUtils.isInstance("a", CharSequence.class));
		assertFalse(ObjectUtils.isInstance(null, Object.class));
		assertTrue(ObjectUtils.isNotInstance(1, String.class));
		assertTrue(ObjectUtils.isSameType("a", "b"));
		assertTrue(ObjectUtils.isSameType(null, null));
		assertFalse(ObjectUtils.isSameType("a", null));
		assertTrue(ObjectUtils.isNotSameType("a", 1));
	}

	@Test
	public void testDeepToString() {
		assertEquals("", ObjectUtils.deepToString(null));
		assertEquals("[a,b]", ObjectUtils.deepToString(new Object[] { "a", "b" }));
		assertEquals(Bytes.toString(new byte[] { 1 }), ObjectUtils.deepToString(new byte[] { 1 }));
		assertEquals(Chars.toString(new char[] { 'a' }), ObjectUtils.deepToString(new char[] { 'a' }));
		assertEquals(Shorts.toString(new short[] { 1 }), ObjectUtils.deepToString(new short[] { 1 }));
		assertEquals(Ints.toString(new int[] { 1, 2 }), ObjectUtils.deepToString(new int[] { 1, 2 }));
		assertEquals(Longs.toString(new long[] { 1 }), ObjectUtils.deepToString(new long[] { 1 }));
		assertEquals(Floats.toString(new float[] { 1 }), ObjectUtils.deepToString(new float[] { 1 }));
		assertEquals(Doubles.toString(new double[] { 1 }), ObjectUtils.deepToString(new double[] { 1 }));
		List<Integer> list = Arrays.asList(1, 2);
		assertEquals(CollectionUtils.toString(list), ObjectUtils.deepToString(list));
		Map<String, Integer> map = Collections.singletonMap("a", 1);
		assertEquals(MapUtils.toString(map), ObjectUtils.deepToString(map));
		assertEquals(CollectionUtils.toString(list.iterator()), ObjectUtils.deepToString(list.iterator()));
		assertEquals(CollectionUtils.toString(new Vector<>(list).elements()), ObjectUtils.deepToString(new Vector<>(list).elements()));
		assertEquals("x", ObjectUtils.deepToString("x"));
		assertEquals("[[1,2],[3]]", ObjectUtils.deepToString(new int[][] { { 1, 2 }, { 3 } }));
		assertEquals("[[a,[1]],,b]", ObjectUtils.deepToString(new Object[] { new Object[] { "a", new long[] { 1 } }, null, " b " }));
		assertEquals("[[true]]", ObjectUtils.deepToString(new Object[] { new boolean[] { true } }));
	}

	@Test
	public void testDeepEquals() {
		Object a = new Object();
		assertTrue(ObjectUtils.deepEquals(a, a));
		assertTrue(ObjectUtils.deepEquals(null, null));
		assertFalse(ObjectUtils.deepEquals(null, a));
		assertFalse(ObjectUtils.deepEquals(a, null));
		assertTrue(ObjectUtils.deepEquals(new Object[] { 1 }, new Object[] { 1 }));
		assertTrue(ObjectUtils.deepEquals(new boolean[] { true }, new boolean[] { true }));
		assertTrue(ObjectUtils.deepEquals(new byte[] { 1 }, new byte[] { 1 }));
		assertTrue(ObjectUtils.deepEquals(new char[] { 'a' }, new char[] { 'a' }));
		assertTrue(ObjectUtils.deepEquals(new short[] { 1 }, new short[] { 1 }));
		assertTrue(ObjectUtils.deepEquals(new int[] { 1 }, new int[] { 1 }));
		assertFalse(ObjectUtils.deepEquals(new int[] { 1 }, new int[] { 2 }));
		assertTrue(ObjectUtils.deepEquals(new long[] { 1 }, new long[] { 1 }));
		assertTrue(ObjectUtils.deepEquals(new float[] { 1 }, new float[] { 1 }));
		assertTrue(ObjectUtils.deepEquals(new double[] { 1 }, new double[] { 1 }));
		assertTrue(ObjectUtils.deepEquals(Arrays.asList(1, 2), Arrays.asList(1, 2)));
		Map<String, Object> m1 = new HashMap<>();
		m1.put("a", 1);
		Map<String, Object> m2 = new HashMap<>(m1);
		assertTrue(ObjectUtils.deepEquals(m1, m2));
		assertTrue(ObjectUtils.deepEquals(Arrays.asList(1).iterator(), Arrays.asList(1).iterator()));
		assertTrue(ObjectUtils.deepEquals(new Vector<>(Arrays.asList(1)).elements(), new Vector<>(Arrays.asList(1)).elements()));
		assertTrue(ObjectUtils.deepEquals("a", "a"));
		assertFalse(ObjectUtils.deepEquals("a", 1));
	}

	@Test
	public void testDeepHashCode() {
		assertEquals(0, ObjectUtils.deepHashCode(null));
		Object[] values = { (byte) 1, 'c', (short) 2, 3, 4f, 5d, 6L, true, new Object[] { 1 }, new boolean[] { true },
				new byte[] { 1 }, new char[] { 'a' }, new short[] { 1 }, new int[] { 1 }, new long[] { 1 }, new float[] { 1 },
				new double[] { 1 }, "str" };
		for (Object value : values) {
			assertEquals(ObjectUtils.deepHashCode(value), ObjectUtils.deepHashCode(value));
		}
		assertEquals(ObjectUtils.deepHashCode(new int[] { 1, 2 }), ObjectUtils.deepHashCode(new int[] { 1, 2 }));
		assertNotEquals(ObjectUtils.deepHashCode(new int[] { 1, 2 }), ObjectUtils.deepHashCode(new int[] { 2, 1 }));
		assertEquals(ObjectUtils.deepHashCode(Arrays.asList(1, 2)), ObjectUtils.deepHashCode(Arrays.asList(1, 2)));
		assertEquals(ObjectUtils.deepHashCode(Collections.singletonMap("a", 1)), ObjectUtils.deepHashCode(Collections.singletonMap("a", 1)));
		assertEquals(ObjectUtils.deepHashCode(Arrays.asList(1).iterator()), ObjectUtils.deepHashCode(Arrays.asList(1).iterator()));
		assertEquals(ObjectUtils.deepHashCode(new Vector<>(Arrays.asList(1)).elements()),
				ObjectUtils.deepHashCode(new Vector<>(Arrays.asList(1)).elements()));
		assertEquals("str".hashCode(), ObjectUtils.deepHashCode("str"));
	}

}
