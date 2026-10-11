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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.junit.jupiter.api.Test;

/**
 * CollectionUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CollectionUtilsTest {

	private static Enumeration<String> en(String... args) {
		return Collections.enumeration(Arrays.asList(args));
	}

	private static Iterator<String> it(String... args) {
		return Arrays.asList(args).iterator();
	}

	@Test
	public void testEmptiness() {
		assertTrue(CollectionUtils.isEmpty((Collection<?>) null));
		assertTrue(CollectionUtils.isEmpty(new ArrayList<String>()));
		assertFalse(CollectionUtils.isEmpty(Arrays.asList("a")));
		assertTrue(CollectionUtils.isNotEmpty(Arrays.asList("a")));
		assertTrue(CollectionUtils.isEmpty((Iterator<?>) null));
		assertTrue(CollectionUtils.isEmpty(it()));
		assertTrue(CollectionUtils.isNotEmpty(it("a")));
		assertTrue(CollectionUtils.isEmpty((Enumeration<?>) null));
		assertTrue(CollectionUtils.isEmpty(en()));
		assertTrue(CollectionUtils.isNotEmpty(en("a")));
	}

	@Test
	public void testUnmodifiable() {
		Collection<String> c = CollectionUtils.unmodifiableCollection("a", "b");
		assertEquals(2, c.size());
		assertThrows(UnsupportedOperationException.class, () -> c.add("c"));
		assertTrue(CollectionUtils.unmodifiableCollection((Collection<String>) null).isEmpty());
	}

	@Test
	public void testFirstLastAndIndex() {
		assertEquals("c", CollectionUtils.getLast(Arrays.asList("a", "b", "c")));
		assertEquals("x", CollectionUtils.getLast(new ArrayList<String>(), "x"));
		assertEquals("x", CollectionUtils.getLast((Collection<String>) null, "x"));
		assertNull(CollectionUtils.getLast((Iterator<String>) null));
		assertEquals("b", CollectionUtils.getLast(en("a", "b")));
		assertEquals("x", CollectionUtils.getLast(en(), "x"));
		assertEquals("a", CollectionUtils.getFirst(Arrays.asList("a", "b")));
		assertEquals("x", CollectionUtils.getFirst(new ArrayList<String>(), "x"));
		assertEquals("x", CollectionUtils.getFirst((Collection<String>) null, "x"));
		assertNull(CollectionUtils.getFirst((Iterator<String>) null));
		assertEquals("a", CollectionUtils.getFirst(en("a", "b")));
		assertNull(CollectionUtils.getFirst((Enumeration<String>) null));
		assertEquals("b", CollectionUtils.get(it("a", "b", "c"), 1));
		assertNull(CollectionUtils.get(it("a"), 5));
		assertEquals("x", CollectionUtils.get(it("a"), 5, "x"));
		assertEquals("x", CollectionUtils.get(it("a"), -1, "x"));
		assertEquals("x", CollectionUtils.get((Iterator<String>) null, 0, "x"));
		assertEquals("c", CollectionUtils.get(en("a", "b", "c"), 2));
		assertNull(CollectionUtils.get(en("a"), 3));
		assertEquals("x", CollectionUtils.get(en("a"), 3, "x"));
		assertEquals("x", CollectionUtils.get((Enumeration<String>) null, 0, "x"));
	}

	@Test
	public void testConversions() {
		Enumeration<String> e = CollectionUtils.enumeration(new String[] { "a", "b" });
		assertEquals("[a, b]", CollectionUtils.toList(e).toString());
		assertEquals("[a]", CollectionUtils.toList(CollectionUtils.enumeration(it("a"))).toString());
		assertFalse(CollectionUtils.emptyIterator().hasNext());
		assertThrows(UnsupportedOperationException.class, () -> CollectionUtils.emptyIterator().next());
		assertThrows(UnsupportedOperationException.class, () -> CollectionUtils.emptyIterator().remove());
		assertFalse(CollectionUtils.emptyEnumeration().hasMoreElements());
		assertThrows(UnsupportedOperationException.class, () -> CollectionUtils.emptyEnumeration().nextElement());
		assertEquals("[a, b]", CollectionUtils.toList(CollectionUtils.iterator(new String[] { "a", "b" })).toString());
		assertEquals("[a]", CollectionUtils.toList(CollectionUtils.iterator(en("a"))).toString());
		assertThrows(IllegalArgumentException.class, () -> CollectionUtils.iterator((String[]) null));
		List<String> list = new ArrayList<String>();
		for (String s : CollectionUtils.forEach(it("a", "b"))) {
			list.add(s);
		}
		for (String s : CollectionUtils.forEach(en("c"))) {
			list.add(s);
		}
		assertEquals("[a, b, c]", list.toString());
		assertEquals("[a]", CollectionUtils.toList((Iterable<String>) Arrays.asList("a")).toString());
		assertTrue(CollectionUtils.toList((Iterable<String>) null).isEmpty());
		Iterator<Integer> lengths = CollectionUtils.iterator(en("a", "bb"), String::length);
		assertEquals("[1, 2]", CollectionUtils.toList(lengths).toString());
		Iterator<Integer> lengths2 = CollectionUtils.iterator(it("abc"), String::length);
		assertEquals(Integer.valueOf(3), lengths2.next());
		assertFalse(lengths2.hasNext());
		assertThrows(UnsupportedOperationException.class, lengths2::remove);
		assertThrows(UnsupportedOperationException.class, () -> CollectionUtils.iterator(en(), String::length).remove());
	}

	@Test
	public void testJoin() {
		assertEquals("[a,b]", CollectionUtils.toString(Arrays.asList("a", "b")));
		assertEquals("[a,b]", CollectionUtils.toString(it("a", "b")));
		assertEquals("[a,b]", CollectionUtils.toString(en("a", "b")));
		assertEquals("", CollectionUtils.join((Iterable<?>) null));
		assertEquals("a|b", CollectionUtils.join(Arrays.asList("a", "b"), "|"));
		assertEquals("ab", CollectionUtils.join(it("a", "b"), null));
		assertEquals("", CollectionUtils.join(it()));
		assertEquals("<a>,<b>", CollectionUtils.join(it("a", "b"), ",", "<{}>"));
		assertEquals("<a>", CollectionUtils.join(it("a"), (String) null, "<{}>"));
		assertEquals("", CollectionUtils.join(it(), ",", "<{}>"));
		assertEquals("a=1,b=2", CollectionUtils.join(Arrays.asList("a", "b"), Arrays.asList("1", "2", "3"), "=", ","));
		assertEquals("a,1", CollectionUtils.join(Arrays.asList("a"), Arrays.asList("1"), ","));
		assertEquals("", CollectionUtils.join(null, Arrays.asList("1"), ","));
		assertEquals("a1b2", CollectionUtils.join(it("a", "b"), it("1", "2"), null, null));
		assertEquals("", CollectionUtils.join(it(), it("1"), ","));
		assertEquals("a,b", CollectionUtils.join(en("a", "b")));
		assertEquals("ab", CollectionUtils.join(en("a", "b"), null));
		assertEquals("", CollectionUtils.join((Enumeration<?>) null));
		assertEquals("a:1;b:2", CollectionUtils.join(en("a", "b"), en("1", "2"), ":", ";"));
		assertEquals("a1", CollectionUtils.join(en("a"), en("1"), null, null));
		assertEquals("a,1", CollectionUtils.join(en("a"), en("1"), ","));
		assertEquals("", CollectionUtils.join(en(), en("1"), ","));
	}

	@Test
	public void testTypeChecks() {
		assertTrue(CollectionUtils.isCollection(new ArrayList<String>()));
		assertTrue(CollectionUtils.isNotCollection(null));
		assertTrue(CollectionUtils.isIterator(it()));
		assertTrue(CollectionUtils.isNotIterator("a"));
		assertTrue(CollectionUtils.isEnumeration(en()));
		assertTrue(CollectionUtils.isNotEnumeration(null));
		assertTrue(CollectionUtils.isQueue(new LinkedList<String>()));
		assertTrue(CollectionUtils.isNotQueue(new ArrayList<String>()));
	}

	@Test
	public void testDeepEqualsAndHashCode() {
		List<String> a = Arrays.asList("a", "b");
		assertTrue(CollectionUtils.deepEquals(a, a));
		assertTrue(CollectionUtils.deepEquals(a, Arrays.asList("a", "b")));
		assertFalse(CollectionUtils.deepEquals(a, Arrays.asList("a", "c")));
		assertFalse(CollectionUtils.deepEquals(a, Arrays.asList("a")));
		assertFalse(CollectionUtils.deepEquals(a, new ArrayList<String>(a)));
		assertFalse(CollectionUtils.deepEquals((Iterable<?>) null, a));
		assertFalse(CollectionUtils.deepEquals(it("a"), null));
		Iterator<String> i = it("a");
		assertTrue(CollectionUtils.deepEquals(i, i));
		assertFalse(CollectionUtils.deepEquals(it("a"), new ArrayList<String>(a).iterator()));
		Enumeration<String> e = en("a");
		assertTrue(CollectionUtils.deepEquals(e, e));
		assertTrue(CollectionUtils.deepEquals((Enumeration<?>) null, null));
		assertFalse(CollectionUtils.deepEquals((Enumeration<?>) null, en()));
		assertFalse(CollectionUtils.deepEquals(en(), null));
		assertTrue(CollectionUtils.deepEquals(en("a", "b"), en("a", "b")));
		assertFalse(CollectionUtils.deepEquals(en("a", "b"), en("a", "c")));
		assertFalse(CollectionUtils.deepEquals(en("a"), en("a", "b")));
		assertFalse(CollectionUtils.deepEquals(en("a"), new Vector<String>(a).elements()));
		assertEquals(0, CollectionUtils.deepHashCode((Iterable<?>) null));
		assertEquals(0, CollectionUtils.deepHashCode((Iterator<?>) null));
		assertEquals(0, CollectionUtils.deepHashCode((Enumeration<?>) null));
		assertEquals(CollectionUtils.deepHashCode(a), CollectionUtils.deepHashCode(en("a", "b")));
		assertEquals(CollectionUtils.deepHashCode(Arrays.asList("a", null)), CollectionUtils.deepHashCode(en("a")));
		assertEquals(CollectionUtils.deepHashCode(Arrays.asList("a", null)), CollectionUtils.deepHashCode(Arrays.asList("a")));
	}

	@Test
	public void testSetOperations() {
		List<String> left = Arrays.asList("a", "b", "c");
		List<String> right = Arrays.asList("b", "d");
		assertTrue(CollectionUtils.containsIgnoreCase(left, "A"));
		assertFalse(CollectionUtils.containsIgnoreCase(left, "z"));
		assertFalse(CollectionUtils.containsIgnoreCase(null, "z"));
		assertEquals("[a, c]", CollectionUtils.minus(left, right).toString());
		assertEquals(left, CollectionUtils.minus(left, null));
		assertEquals(right, CollectionUtils.minus(null, right));
		assertNull(CollectionUtils.minus(null, null));
		assertEquals("[b]", CollectionUtils.intersect(left, right).toString());
		assertNull(CollectionUtils.intersect(left, null));
		assertEquals("[a, b, c, d]", CollectionUtils.union(left, right).toString());
		assertEquals("[a]", CollectionUtils.union(Arrays.asList("a", "a"), new ArrayList<String>()).toString());
		assertEquals(left, CollectionUtils.union(left, null));
		assertEquals(right, CollectionUtils.union(null, right));
		assertNull(CollectionUtils.union(null, null));
		assertEquals("[a, b, c, b, d]", CollectionUtils.unionAll(left, right).toString());
		assertEquals(left, CollectionUtils.unionAll(left, null));
		assertEquals(right, CollectionUtils.unionAll(null, right));
		assertNull(CollectionUtils.unionAll(null, null));
	}

	@Test
	public void testAddAllAndFormats() {
		List<String> list = new ArrayList<String>();
		CollectionUtils.addAll(list, new String[] { "a" });
		CollectionUtils.addAll(list, it("b"));
		CollectionUtils.addAll(list, en("c"));
		CollectionUtils.addAll(list, (String[]) null);
		CollectionUtils.addAll(list, (Iterator<String>) null);
		CollectionUtils.addAll(list, (Enumeration<String>) null);
		CollectionUtils.addAll(null, new String[] { "a" });
		CollectionUtils.addAll(null, it("a"));
		CollectionUtils.addAll(null, en("a"));
		assertEquals("[a, b, c]", list.toString());
		assertEquals("[<1>, <2>]", CollectionUtils.formats(Arrays.asList(1, 2), "<%d>").toString());
		assertEquals("[<a>]", CollectionUtils.formats(en("a"), "<%s>").toString());
		assertThrows(IllegalArgumentException.class, () -> CollectionUtils.formats((Collection<?>) null, "%s"));
	}

	@Test
	public void testSequentialIterators() {
		List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
		iterators.add(it());
		iterators.add(it("a", "b"));
		iterators.add(it());
		iterators.add(it("c"));
		iterators.add(it());
		Iterator<String> seq = CollectionUtils.toSequentialIterator(iterators);
		assertEquals("[a, b, c]", CollectionUtils.toList(seq).toString());
		assertThrows(NoSuchElementException.class, seq::next);
		assertFalse(CollectionUtils.toSequentialIterator(new ArrayList<Iterator<String>>()).hasNext());

		List<Enumeration<String>> enumerations = new ArrayList<Enumeration<String>>();
		enumerations.add(en());
		enumerations.add(en("a"));
		enumerations.add(en());
		enumerations.add(en("b", "c"));
		Iterator<String> seq2 = CollectionUtils.toSequentialEnumeration(enumerations);
		assertEquals("[a, b, c]", CollectionUtils.toList(seq2).toString());
		assertThrows(NoSuchElementException.class, seq2::next);
		assertThrows(NullPointerException.class, () -> CollectionUtils.toSequentialIterator(null));
		assertThrows(NullPointerException.class, () -> CollectionUtils.toSequentialEnumeration(null));
		assertTrue(new HashSet<String>(CollectionUtils.toList(CollectionUtils.toSequentialEnumeration(
				Arrays.asList(en("x"))))).contains("x"));
	}

}
