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
 * ConcurrentBoundedListTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConcurrentBoundedListTest {

	@Test
	public void testFifoEviction() {
		final List<String> evicted = new ArrayList<String>();
		ConcurrentBoundedList<String> list = new ConcurrentBoundedList<String>(3) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(String element) {
				evicted.add(element);
			}
		};
		assertTrue(list.add("a"));
		list.add("b");
		list.add("c");
		list.get(0);
		list.add("d");
		assertEquals("[b, c, d]", list.toString());
		assertEquals("[a]", evicted.toString());
		list.add(0, "e");
		assertEquals("[e, c, d]", list.toString());
		assertEquals("c", list.set(1, "f"));
		assertEquals("[e, f, d]", list.toString());
		list.add("g");
		assertEquals("[e, f, g]", list.toString());
		assertEquals("[a, b, d]", evicted.toString());
		assertEquals(3, list.getMaxSize());
	}

	@Test
	public void testClearResetsCapacity() {
		ConcurrentBoundedList<Integer> list = new ConcurrentBoundedList<Integer>(2);
		list.add(1);
		list.add(2);
		list.clear();
		list.add(3);
		list.add(4);
		list.add(5);
		assertEquals("[4, 5]", list.toString());
	}

	@Test
	public void testBulkOperations() {
		ConcurrentBoundedList<Integer> list = new ConcurrentBoundedList<Integer>(new ArrayList<Integer>(), 5);
		for (int i = 1; i <= 5; i++) {
			list.add(i);
		}
		assertTrue(list.contains(3));
		assertTrue(list.containsAll(Arrays.asList(1, 2)));
		assertEquals(2, list.indexOf(3));
		assertEquals(2, list.lastIndexOf(3));
		assertTrue(list.remove(Integer.valueOf(3)));
		assertFalse(list.remove(Integer.valueOf(3)));
		assertTrue(list.removeAll(Arrays.asList(1, 2)));
		assertFalse(list.removeAll(Arrays.asList(1, 2)));
		assertTrue(list.retainAll(Arrays.asList(4)));
		assertFalse(list.retainAll(Arrays.asList(4)));
		assertEquals(Integer.valueOf(4), list.remove(0));
		assertTrue(list.isEmpty());
		for (int i = 10; i < 20; i++) {
			list.add(i);
		}
		assertEquals("[15, 16, 17, 18, 19]", list.getDelegate().toString());
		assertEquals("[16, 17]", list.subList(1, 3).toString());
	}

	@Test
	public void testConcurrentAdd() throws Exception {
		final ConcurrentBoundedList<Integer> list = new ConcurrentBoundedList<Integer>(25);
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			final int base = t * 1000;
			new Thread(() -> {
				for (int i = 0; i < 200; i++) {
					list.add(base + i);
					if (i % 3 == 0) {
						list.remove(Integer.valueOf(base + i - 1));
					}
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertTrue(list.size() <= 25);
		for (int i = 0; i < 30; i++) {
			list.add(-i);
		}
		assertEquals(25, list.size());
	}

}
