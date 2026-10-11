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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * LruListTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LruListTest {

	private static <E> LruList<E> newList(int maxSize, final List<E> evicted) {
		return new LruList<E>(maxSize) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(E element) {
				evicted.add(element);
			}
		};
	}

	@Test
	public void testEvictLeastRecentlyUsed() {
		List<String> evicted = new ArrayList<String>();
		LruList<String> list = newList(3, evicted);
		list.add("a");
		list.add("b");
		list.add("c");
		assertEquals("a", list.get(0));
		list.add("d");
		assertEquals("[a, c, d]", list.toString());
		assertEquals("[b]", evicted.toString());
		assertTrue(list.contains("c"));
		list.add("e");
		assertEquals("[c, d, e]", list.toString());
		assertEquals("[b, a]", evicted.toString());
	}

	@Test
	public void testDuplicatesAndIndexes() {
		List<String> evicted = new ArrayList<String>();
		LruList<String> list = newList(3, evicted);
		list.add("x");
		list.add("x");
		list.add(0, "y");
		assertEquals("[y, x, x]", list.toString());
		assertEquals("x", list.set(2, "z"));
		assertEquals(1, list.indexOf("x"));
		assertEquals(1, list.lastIndexOf("x"));
		list.get(1);
		list.add("w");
		// "y" is the least recently used one
		assertEquals("[x, z, w]", list.toString());
		assertEquals("x", list.remove(0));
		assertTrue(list.remove("w"));
		assertFalse(list.remove("w"));
		assertFalse(list.contains("w"));
		assertEquals(1, list.size());
		list.add("1");
		list.add("2");
		list.add("3");
		assertEquals("[1, 2, 3]", list.toString());
		list.clear();
		assertTrue(list.isEmpty());
		list.add("q");
		assertEquals("[q]", list.getDelegate().toString());
	}

	@Test
	public void testExistingElementsAndCustomDelegate() {
		List<Integer> delegate = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
		LruList<Integer> list = new LruList<Integer>(delegate, 3);
		assertEquals("[2, 3, 4]", list.toString());
		Iterator<Integer> it = list.iterator();
		assertThrows(IllegalStateException.class, it::remove);
		it.next();
		it.remove();
		it.next();
		it.remove();
		assertEquals("[4]", list.toString());
		list.add(5);
		list.add(6);
		list.add(7);
		assertEquals("[5, 6, 7]", list.toString());
		ListIterator<Integer> lit = list.listIterator();
		assertEquals(Integer.valueOf(5), lit.next());
		assertThrows(UnsupportedOperationException.class, () -> lit.set(1));
		LruList<String> cow = new LruList<String>();
		cow.add("a");
		Iterator<String> cowIt = cow.iterator();
		cowIt.next();
		assertThrows(UnsupportedOperationException.class, cowIt::remove);
		assertEquals("[a]", cow.toString());
		assertEquals(Arrays.asList(5, 6, 7), new ArrayList<Integer>(list));
	}

	@Test
	public void testConcurrentAdd() throws Exception {
		final LruList<Integer> list = new LruList<Integer>(30);
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			final int base = t * 1000;
			new Thread(() -> {
				for (int i = 0; i < 200; i++) {
					list.add(base + i);
					list.contains(base + i / 2);
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertEquals(30, list.size());
	}

}
