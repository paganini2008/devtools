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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * LruQueueTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LruQueueTest {

	@Test
	public void testFifoAndEviction() {
		final List<String> evicted = new ArrayList<String>();
		LruQueue<String> queue = new LruQueue<String>(3) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(String element) {
				evicted.add(element);
			}
		};
		queue.offer("a");
		queue.offer("b");
		queue.offer("c");
		assertEquals("a", queue.peek());
		queue.offer("d");
		// "a" was peeked, so "b" is evicted
		assertEquals("[a, c, d]", queue.toString());
		assertEquals("[b]", evicted.toString());
		assertTrue(queue.contains("c"));
		queue.offer("e");
		assertEquals("[c, d, e]", queue.toString());
		assertEquals("c", queue.poll());
		assertEquals("d", queue.poll());
		assertEquals(1, queue.size());
		queue.offer("f");
		queue.offer("g");
		assertEquals("[e, f, g]", queue.toString());
	}

	@Test
	public void testRemoveAndClear() {
		LruQueue<Integer> queue = new LruQueue<Integer>(new LinkedList<Integer>(Arrays.asList(1, 2, 3)), 2);
		assertEquals("[2, 3]", queue.toString());
		assertTrue(queue.remove(3));
		assertFalse(queue.remove(3));
		assertFalse(queue.contains(3));
		queue.add(4);
		queue.add(5);
		assertEquals("[4, 5]", queue.getDelegate().toString());
		Iterator<Integer> it = queue.iterator();
		assertThrows(IllegalStateException.class, it::remove);
		it.next();
		it.remove();
		assertEquals("[5]", queue.toString());
		queue.add(6);
		queue.add(7);
		assertEquals("[6, 7]", queue.toString());
		queue.clear();
		assertTrue(queue.isEmpty());
		assertNull(queue.poll());
		assertNull(queue.peek());
		queue.add(8);
		assertEquals(Integer.valueOf(8), queue.element());
	}

	@Test
	public void testConcurrentOfferAndPoll() throws Exception {
		final LruQueue<Integer> queue = new LruQueue<Integer>(40);
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			final int base = t * 1000;
			new Thread(() -> {
				for (int i = 0; i < 200; i++) {
					queue.offer(base + i);
					if (i % 5 == 0) {
						queue.poll();
					}
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertEquals(40, queue.size());
		assertTrue(new LruQueue<String>().isEmpty());
	}

}
