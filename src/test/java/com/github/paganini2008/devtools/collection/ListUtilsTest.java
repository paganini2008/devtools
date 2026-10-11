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
package com.github.paganini2008.devtools.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ListUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ListUtilsTest {

	private static List<String> list(String... args) {
		return new ArrayList<String>(Arrays.asList(args));
	}

	@Test
	public void testBasics() {
		assertTrue(ListUtils.emptyList().isEmpty());
		assertTrue(ListUtils.isList(new ArrayList<String>()));
		assertFalse(ListUtils.isList(null));
		assertTrue(ListUtils.isNotList("a"));
		assertTrue(ListUtils.isEmpty(null));
		assertTrue(ListUtils.isEmpty(new ArrayList<String>()));
		assertTrue(ListUtils.isNotEmpty(list("a")));
		List<String> u = ListUtils.unmodifiableList("a", "b");
		assertThrows(UnsupportedOperationException.class, () -> u.add("c"));
		assertEquals(1, ListUtils.unmodifiableList(new HashSet<String>(Arrays.asList("a"))).size());
		assertTrue(ListUtils.synchronizedList().isEmpty());
		assertEquals("[a]", ListUtils.synchronizedList(Arrays.asList("a")).toString());
		assertEquals("[a, b]", ListUtils.create("a", "b").toString());
		assertTrue(ListUtils.create().isEmpty());
		List<String> l = list("a");
		assertSame(l, ListUtils.toList(l));
		assertTrue(ListUtils.toList((Collection<String>) null).isEmpty());
	}

	@Test
	public void testGetAndRemove() {
		List<String> l = list("a", "b", "c");
		assertEquals("a", ListUtils.get(l, 0));
		assertEquals("c", ListUtils.get(l, -1));
		assertEquals("a", ListUtils.get(l, -3));
		assertNull(ListUtils.get(l, -4));
		assertNull(ListUtils.get(l, 3));
		assertEquals("x", ListUtils.get(l, 3, "x"));
		assertEquals("x", ListUtils.get(null, 0, "x"));
		assertEquals("a", ListUtils.getFirst(l));
		assertEquals("x", ListUtils.getFirst(null, "x"));
		assertEquals("c", ListUtils.getLast(l));
		assertEquals("x", ListUtils.getLast(new ArrayList<String>(), "x"));

		List<String> dup = list("a", "b", "a", "b");
		assertEquals("a", ListUtils.remove(dup, 2));
		assertEquals("[a, b, b]", dup.toString());
		assertEquals("b", ListUtils.remove(dup, -1));
		assertEquals("[a, b]", dup.toString());
		assertNull(ListUtils.remove(dup, 5));
		assertNull(ListUtils.remove(null, 0));
		List<String> dup2 = list("x", "y", "x");
		assertEquals("x", ListUtils.removeLast(dup2));
		assertEquals("[x, y]", dup2.toString());
		assertEquals("x", ListUtils.removeFirst(dup2));
		assertEquals("[y]", dup2.toString());
		assertNull(ListUtils.removeFirst(null));
		assertNull(ListUtils.removeLast(new ArrayList<String>()));
	}

	@Test
	public void testCopy() {
		List<String> l = list("a", "b", "c", "d");
		assertEquals("[a, b]", ListUtils.copy(l, 2).toString());
		assertEquals("[a, b, c, d]", ListUtils.copy(l, 10).toString());
		assertEquals("[b, c]", ListUtils.copy(l, 1, 3).toString());
		List<String> dest = list("1", "2", "3", "4");
		ListUtils.copy(l, 1, dest, 0, 2);
		assertEquals("[b, c, 3, 4]", dest.toString());
		ListUtils.copy(l, 2, dest, 3, 5);
		assertEquals("[b, c, 3, c]", dest.toString());
		assertThrows(IllegalArgumentException.class, () -> ListUtils.copy(null, 0, dest, 0, 1));
	}

	@Test
	public void testSlice() {
		List<String> l = list("a", "b", "c", "d");
		assertEquals("[a, b]", ListUtils.slice(l, 2).toString());
		assertEquals("[c, d]", ListUtils.slice(l, 5, 2).toString());
		assertEquals("[b, c, d]", ListUtils.slice(l, -1, 1).toString());
		assertTrue(ListUtils.slice(l, 2, 4).isEmpty());
		assertTrue(ListUtils.slice(l, -1, 10).isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> ListUtils.slice(l, 2).add("x"));
		assertTrue(ListUtils.slice(l, 0, 0).isEmpty());
		assertTrue(ListUtils.slice(l, 0, 9).isEmpty());
		assertEquals("[b, c, d]", ListUtils.slice(l, Integer.MAX_VALUE, 1).toString());
		assertThrows(IllegalArgumentException.class, () -> ListUtils.slice(l, -2, 0));
		assertThrows(IllegalArgumentException.class, () -> ListUtils.slice(l, 1, -1));
	}

	@Test
	public void testSort() {
		List<String> l = list("b", "c", "a");
		ListUtils.asc(l);
		assertEquals("[a, b, c]", l.toString());
		ListUtils.desc(l);
		assertEquals("[c, b, a]", l.toString());
		List<String> linked = new LinkedList<String>(Arrays.asList("bb", "a", "ccc"));
		ListUtils.sort(linked, (x, y) -> x.length() - y.length());
		assertEquals("[a, bb, ccc]", linked.toString());
		List<String> empty = new ArrayList<String>();
		ListUtils.asc(empty);
		ListUtils.desc(empty);
		ListUtils.sort(empty, null);
		assertTrue(empty.isEmpty());
		assertThrows(IllegalArgumentException.class, () -> ListUtils.asc(null));
	}

	@Test
	public void testReverseAndSwap() {
		Iterator<String> it = Arrays.asList("a", "b").iterator();
		assertEquals("[a, b]", ListUtils.list(it).toString());
		assertEquals("[a]", ListUtils.list(Collections.enumeration(Arrays.asList("a"))).toString());
		assertEquals("[c, b, a]", ListUtils.reverse(Arrays.asList("a", "b", "c").iterator()).toString());
		assertEquals("[b, a]", ListUtils.reverse(Collections.enumeration(Arrays.asList("a", "b"))).toString());
		List<String> l = list("a", "b", "c", "d");
		ListUtils.reverse(l);
		assertEquals("[d, c, b, a]", l.toString());
		ListUtils.reverse((List<String>) null);
		ListUtils.swap(l, 0, 3);
		assertEquals("[a, c, b, d]", l.toString());
		assertThrows(IllegalArgumentException.class, () -> ListUtils.list((Iterator<String>) null));
	}

	@Test
	public void testToStringList() {
		assertEquals("[1, 2]", ListUtils.toStringList(Arrays.asList(1, 2)).toString());
		assertNull(ListUtils.toStringList((Collection<?>) null));
		assertNull(ListUtils.toStringList(new ArrayList<Object>()));
		assertNull(ListUtils.toStringList((Iterator<?>) null));
		assertEquals("[a, ]", ListUtils.toStringList(Collections.enumeration(Arrays.asList("a", null))).toString());
		assertNull(ListUtils.toStringList(Collections.enumeration(new ArrayList<Object>())));
	}

}
