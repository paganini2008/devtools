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
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * LruSetTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LruSetTest {

	@Test
	public void testEviction() {
		final List<String> evicted = new ArrayList<String>();
		LruSet<String> set = new LruSet<String>(2) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onEviction(String element) {
				evicted.add(element);
			}
		};
		assertTrue(set.add("a"));
		assertTrue(set.add("b"));
		assertFalse(set.add("a"));
		assertTrue(set.add("c"));
		assertEquals("[b]", evicted.toString());
		assertTrue(set.contains("a"));
		set.add("d");
		assertEquals("[b, c]", evicted.toString());
		assertEquals(2, set.size());
		assertFalse(set.contains("zz"));
	}

	@Test
	public void testRemoveAndClear() {
		LruSet<String> set = new LruSet<String>(new TreeSet<String>(), 3);
		set.add("c");
		set.add("a");
		set.add("b");
		assertEquals("[a, b, c]", set.toString());
		assertTrue(set.remove("a"));
		assertFalse(set.remove("a"));
		set.add("d");
		set.add("e");
		// "c" is the least recently used one
		assertEquals("[b, d, e]", set.getDelegate().toString());
		set.clear();
		assertTrue(set.isEmpty());
		set.add("x");
		assertEquals(1, set.size());
	}

	@Test
	public void testIteratorRemove() {
		LruSet<String> tree = new LruSet<String>(new TreeSet<String>(), 2);
		tree.add("a");
		tree.add("b");
		Iterator<String> it = tree.iterator();
		assertThrows(IllegalStateException.class, it::remove);
		it.next();
		it.remove();
		assertEquals("[b]", tree.toString());
		tree.add("c");
		assertEquals(2, tree.size());

		LruSet<String> cow = new LruSet<String>(2);
		cow.add("a");
		cow.add("b");
		Iterator<String> it2 = cow.iterator();
		it2.next();
		it2.remove();
		assertEquals("[b]", cow.toString());
		cow.add("c");
		cow.add("d");
		assertEquals("[c, d]", cow.toString());
	}

	@Test
	public void testConcurrentAdd() throws Exception {
		final LruSet<Integer> set = new LruSet<Integer>(20);
		final CountDownLatch done = new CountDownLatch(4);
		for (int t = 0; t < 4; t++) {
			final int base = t * 1000;
			new Thread(() -> {
				for (int i = 0; i < 200; i++) {
					set.add(base + i);
					set.contains(base + i / 2);
				}
				done.countDown();
			}).start();
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		assertEquals(20, set.size());
		LruSet<Integer> defaults = new LruSet<Integer>();
		for (int i = 0; i < 130; i++) {
			defaults.add(i);
		}
		assertEquals(128, defaults.size());
		assertFalse(defaults.contains(0));
	}

}
