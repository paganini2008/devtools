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
package com.github.paganini2008.devtools.multithreads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * AtomicDoubleTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class AtomicDoubleTest {

	@Test
	public void set_acceptsValuesInRange() {
		AtomicDouble d = new AtomicDouble(1.5, 10);
		d.set(2.5);
		assertEquals(2.5, d.get());
		d.set(10);
		assertEquals(10.0, d.get());
		d.set(1.5);
		assertEquals(1.5, d.get());
	}

	@Test
	public void set_rejectsValuesOutOfRange() {
		AtomicDouble d = new AtomicDouble(1.0, 10);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> d.set(0.5));
		assertEquals("New value must >= 1.0 and <= 10.", e.getMessage());
		assertThrows(IllegalArgumentException.class, () -> d.set(10.5));
		assertEquals(1.0, d.get());
	}

	@Test
	public void getAndSetAndCompareAndSet() {
		AtomicDouble d = new AtomicDouble();
		assertEquals(0.0, d.getAndSet(3.25));
		assertTrue(d.compareAndSet(3.25, 4.5));
		assertFalse(d.compareAndSet(3.25, 5));
		assertEquals(4.5, d.get());
		assertTrue(d.weakCompareAndSet(4.5, 6) || d.get() == 4.5);
	}

	@Test
	public void add_wrapsToInitialValueAtMaximum() {
		AtomicDouble d = new AtomicDouble(1.0, 10);
		assertEquals(1.0, d.getAndAdd(2.5));
		assertEquals(3.5, d.get());
		assertEquals(8.5, d.addAndGet(5));
		assertEquals(1.0, d.addAndGet(5));
		d.set(9);
		assertEquals(9.0, d.getAndAdd(1));
		assertEquals(1.0, d.get());
	}

	@Test
	public void numberConversionsAndEquality() {
		AtomicDouble a = new AtomicDouble(2.75);
		assertEquals(2, a.intValue());
		assertEquals(2L, a.longValue());
		assertEquals(2.75f, a.floatValue());
		assertEquals(2.75, a.doubleValue());
		assertEquals("2.75", a.toString());
		AtomicDouble b = new AtomicDouble(2.75);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, new AtomicDouble(2.5));
		assertNotEquals(a, new AtomicLongSequence(2));
		assertNotEquals(a, null);
	}

	@Test
	public void stress_concurrentAddsAreNotLost() throws Exception {
		final int threads = 8;
		final int loops = 1000;
		AtomicDouble d = new AtomicDouble();
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			for (int i = 0; i < threads; i++) {
				executor.execute(() -> {
					try {
						start.await();
					} catch (InterruptedException e) {
						return;
					}
					for (int j = 0; j < loops; j++) {
						d.addAndGet(1.0);
					}
				});
			}
			start.countDown();
			executor.shutdown();
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
		assertEquals(threads * loops, d.get(), 0.0);
	}

}
