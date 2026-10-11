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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * ConcurrentSortedBoundedListTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConcurrentSortedBoundedListTest {

	@Test
	public void testEvictSmallest() {
		final List<Integer> evicted = new ArrayList<Integer>();
		ConcurrentSortedBoundedList<Integer> list = new ConcurrentSortedBoundedList<Integer>(3) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(Integer element) {
				evicted.add(element);
			}
		};
		assertTrue(list.add(5));
		assertTrue(list.add(1));
		assertTrue(list.add(9));
		assertTrue(list.add(7));
		assertEquals("[5, 9, 7]", list.toString());
		assertFalse(list.add(2));
		assertEquals("[5, 9, 7]", list.toString());
		assertEquals("[1, 2]", evicted.toString());
		assertFalse(list.add(9));
		assertEquals("[5, 7, 9]", list.toString());
		assertEquals(3, list.getMaxSize());
	}

	@Test
	public void testEvictLargest() {
		ConcurrentSortedBoundedList<Integer> list = new ConcurrentSortedBoundedList<Integer>(new ArrayList<Integer>(), 2);
		list.setAsc(false);
		list.add(5);
		list.add(1);
		list.add(9);
		assertEquals("[5, 1]", list.toString());
		list.add(0, 3);
		assertEquals("[3, 1]", list.toString());
		list.add(0, 1);
		assertEquals("[1, 3]", list.toString());
		assertEquals(Integer.valueOf(3), list.set(1, 2));
		assertEquals("[1, 2]", list.toString());
		assertEquals(Integer.valueOf(2), list.set(1, 1));
		assertEquals("[1]", list.toString());
		list.add(0);
		list.add(-1);
		assertEquals("[0, -1]", list.toString());
	}

	@Test
	public void testBulkOperations() {
		ConcurrentSortedBoundedList<Integer> list = new ConcurrentSortedBoundedList<Integer>(5);
		for (int i = 1; i <= 5; i++) {
			list.add(i);
		}
		assertTrue(list.contains(3));
		assertTrue(list.containsAll(Arrays.asList(1, 2)));
		assertEquals(2, list.indexOf(3));
		assertEquals(2, list.lastIndexOf(3));
		assertEquals("[2, 3]", list.subList(1, 3).toString());
		assertTrue(list.remove(Integer.valueOf(3)));
		assertFalse(list.remove(Integer.valueOf(3)));
		assertTrue(list.removeAll(Arrays.asList(1, 2)));
		assertFalse(list.removeAll(Arrays.asList(1, 2)));
		assertTrue(list.retainAll(Arrays.asList(4)));
		assertFalse(list.retainAll(Arrays.asList(4)));
		assertEquals(Integer.valueOf(4), list.get(0));
		assertEquals(Integer.valueOf(4), list.remove(0));
		assertTrue(list.add(4));
		list.clear();
		assertTrue(list.isEmpty());
		for (int i = 10; i > 0; i--) {
			list.add(i);
		}
		assertEquals("[10, 9, 8, 7, 6]", list.getDelegate().toString());
	}

	@Test
	public void testConcurrentAdd() throws Exception {
		final ConcurrentSortedBoundedList<Integer> list = new ConcurrentSortedBoundedList<Integer>(20);
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			final int base = t * 1000;
			new Thread(() -> {
				for (int i = 0; i < 200; i++) {
					list.add(base + i);
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertEquals(20, list.size());
		for (int i = 3180; i < 3200; i++) {
			assertTrue(list.contains(i));
		}
	}

}
