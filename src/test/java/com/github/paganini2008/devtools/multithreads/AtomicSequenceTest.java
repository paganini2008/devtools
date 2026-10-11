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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * AtomicSequenceTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class AtomicSequenceTest {

	@Test
	public void intSequence_constructorValidatesRange() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new AtomicIntegerSequence(-1));
		assertEquals("Initial value must >= 0.", e.getMessage());
		e = assertThrows(IllegalArgumentException.class, () -> new AtomicIntegerSequence(5, 5));
		assertEquals("Maximum value must > initial value.", e.getMessage());
		assertEquals(0, new AtomicIntegerSequence().get());
	}

	@Test
	public void intSequence_incrementWrapsToInitialValue() {
		AtomicIntegerSequence seq = new AtomicIntegerSequence(1, 3);
		assertEquals(1, seq.getAndIncrement());
		assertEquals(3, seq.incrementAndGet());
		assertEquals(1, seq.incrementAndGet());
		seq.set(3);
		assertEquals(3, seq.getAndIncrement());
		assertEquals(1, seq.get());
	}

	@Test
	public void intSequence_decrementWrapsToMaxValue() {
		AtomicIntegerSequence seq = new AtomicIntegerSequence(1, 3);
		assertEquals(1, seq.getAndDecrement());
		assertEquals(3, seq.get());
		assertEquals(2, seq.decrementAndGet());
		assertEquals(1, seq.decrementAndGet());
		assertEquals(3, seq.decrementAndGet());
	}

	@Test
	public void intSequence_addWrapsAtBothEnds() {
		AtomicIntegerSequence seq = new AtomicIntegerSequence(0, 10);
		assertEquals(0, seq.getAndAdd(10));
		assertEquals(10, seq.get());
		assertEquals(0, seq.addAndGet(1));
		assertEquals(5, seq.addAndGet(5));
		assertEquals(10, seq.addAndGet(-6));
		assertEquals(10, seq.getAndAdd(-3));
		assertEquals(7, seq.get());
	}

	@Test
	public void intSequence_negativeDeltaDoesNotOverflow() {
		AtomicIntegerSequence seq = new AtomicIntegerSequence(0);
		seq.set(5);
		assertEquals(4, seq.addAndGet(-1));
		assertEquals(Integer.MAX_VALUE, seq.addAndGet(Integer.MIN_VALUE));
		assertEquals(0, seq.addAndGet(1));
	}

	@Test
	public void intSequence_setAndCompareValidateRange() {
		AtomicIntegerSequence seq = new AtomicIntegerSequence(1, 5);
		assertThrows(IllegalArgumentException.class, () -> seq.set(0));
		assertThrows(IllegalArgumentException.class, () -> seq.set(6));
		assertThrows(IllegalArgumentException.class, () -> seq.getAndSet(6));
		assertThrows(IllegalArgumentException.class, () -> seq.compareAndSet(1, 0));
		assertThrows(IllegalArgumentException.class, () -> seq.weakCompareAndSet(1, 9));
		assertEquals(1, seq.getAndSet(4));
		assertTrue(seq.compareAndSet(4, 2));
		assertFalse(seq.compareAndSet(4, 3));
		assertTrue(seq.weakCompareAndSet(2, 3) || seq.get() == 2);
		seq.setMaxValue(100);
		seq.set(100);
		assertEquals(100, seq.get());
	}

	@Test
	public void intSequence_numberConversionsAndEquality() {
		AtomicIntegerSequence a = new AtomicIntegerSequence(7);
		AtomicIntegerSequence b = new AtomicIntegerSequence(7);
		assertEquals((byte) 7, a.byteValue());
		assertEquals((short) 7, a.shortValue());
		assertEquals(7, a.intValue());
		assertEquals(7L, a.longValue());
		assertEquals(7f, a.floatValue());
		assertEquals(7d, a.doubleValue());
		assertEquals("7", a.toString());
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, new AtomicIntegerSequence(8));
		assertNotEquals(a, "7");
	}

	@Test
	public void longSequence_behavesLikeIntSequence() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new AtomicLongSequence(-1));
		assertEquals("Initial value must >= 0.", e.getMessage());
		assertThrows(IllegalArgumentException.class, () -> new AtomicLongSequence(3, 2));
		AtomicLongSequence seq = new AtomicLongSequence(1, 3);
		assertEquals(1, seq.getAndIncrement());
		assertEquals(3, seq.incrementAndGet());
		assertEquals(1, seq.incrementAndGet());
		assertEquals(1, seq.getAndDecrement());
		assertEquals(2, seq.decrementAndGet());
		assertEquals(1, seq.decrementAndGet());
		assertEquals(3, seq.decrementAndGet());
		assertEquals(0, new AtomicLongSequence().get());
	}

	@Test
	public void longSequence_addKeepsHistoricalUpperBoundSemantics() {
		AtomicLongSequence seq = new AtomicLongSequence(0, 10);
		// historical behavior: adding up to exactly maxValue wraps to the initial value
		assertEquals(0, seq.getAndAdd(10));
		assertEquals(0, seq.get());
		assertEquals(9, seq.addAndGet(9));
		assertEquals(0, seq.addAndGet(1));
		assertEquals(10, seq.addAndGet(-1));
		assertEquals(10, seq.getAndAdd(-4));
		assertEquals(6, seq.get());
	}

	@Test
	public void longSequence_negativeDeltaDoesNotOverflow() {
		AtomicLongSequence seq = new AtomicLongSequence(0);
		seq.set(5);
		assertEquals(4, seq.addAndGet(-1));
		assertEquals(Long.MAX_VALUE, seq.addAndGet(Long.MIN_VALUE));
	}

	@Test
	public void longSequence_setCompareAndConversions() {
		AtomicLongSequence seq = new AtomicLongSequence(1, 5);
		assertThrows(IllegalArgumentException.class, () -> seq.set(0));
		assertThrows(IllegalArgumentException.class, () -> seq.getAndSet(6));
		assertThrows(IllegalArgumentException.class, () -> seq.compareAndSet(1, 0));
		assertThrows(IllegalArgumentException.class, () -> seq.weakCompareAndSet(1, 9));
		assertEquals(1, seq.getAndSet(4));
		assertTrue(seq.compareAndSet(4, 2));
		assertFalse(seq.compareAndSet(4, 3));
		assertTrue(seq.weakCompareAndSet(2, 3) || seq.get() == 2);
		seq.setMaxValue(50);
		seq.set(50);
		assertEquals((byte) 50, seq.byteValue());
		assertEquals((short) 50, seq.shortValue());
		assertEquals(50, seq.intValue());
		assertEquals(50L, seq.longValue());
		assertEquals(50f, seq.floatValue());
		assertEquals(50d, seq.doubleValue());
		assertEquals("50", seq.toString());
		AtomicLongSequence other = new AtomicLongSequence(50);
		assertEquals(seq, other);
		assertEquals(seq.hashCode(), other.hashCode());
		assertNotEquals(seq, new AtomicLongSequence(1));
		assertNotEquals(seq, 50L);
	}

	@Test
	public void stress_concurrentIncrementsProduceUniqueValues() throws Exception {
		final int threads = 8;
		final int loops = 1000;
		AtomicIntegerSequence ints = new AtomicIntegerSequence(0);
		AtomicLongSequence longs = new AtomicLongSequence(0);
		Set<Integer> intValues = ConcurrentHashMap.newKeySet();
		Set<Long> longValues = ConcurrentHashMap.newKeySet();
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
						intValues.add(ints.getAndIncrement());
						longValues.add(longs.incrementAndGet());
					}
				});
			}
			start.countDown();
			executor.shutdown();
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
		assertEquals(threads * loops, intValues.size());
		assertEquals(threads * loops, longValues.size());
		assertEquals(threads * loops, ints.get());
		assertEquals(threads * loops, longs.get());
	}

	@Test
	public void stress_concurrentWrappingStaysInRange() throws Exception {
		final int threads = 6;
		AtomicIntegerSequence seq = new AtomicIntegerSequence(0, 7);
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		Set<Integer> outOfRange = ConcurrentHashMap.newKeySet();
		try {
			for (int i = 0; i < threads; i++) {
				final boolean up = i % 2 == 0;
				executor.execute(() -> {
					try {
						start.await();
					} catch (InterruptedException e) {
						return;
					}
					for (int j = 0; j < 2000; j++) {
						int v = up ? seq.addAndGet(3) : seq.addAndGet(-2);
						if (v < 0 || v > 7) {
							outOfRange.add(v);
						}
					}
				});
			}
			start.countDown();
			executor.shutdown();
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
		assertTrue(outOfRange.isEmpty(), outOfRange.toString());
	}

}
