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
package com.github.paganini2008.devtools.multithreads.latch;

import static com.github.paganini2008.devtools.multithreads.latch.CounterLatchTest.waitForState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * SemaphoreLatchTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class SemaphoreLatchTest {

	@Test
	public void defaultConstructor_hasSinglePermit() {
		SemaphoreLatch latch = new SemaphoreLatch();
		assertEquals(1, latch.getMaxPermits());
		assertTrue(latch.tryAcquire());
		assertFalse(latch.tryAcquire());
		assertEquals(1, latch.cons());
		assertEquals(0, latch.availablePermits());
		assertTrue(latch.isLocked());
		latch.release();
		assertFalse(latch.isLocked());
	}

	@Test
	public void release_withoutAcquire_doesNotAddPermits() {
		SemaphoreLatch latch = new SemaphoreLatch(2);
		latch.release();
		latch.release();
		assertEquals(2, latch.availablePermits());
		assertTrue(latch.tryAcquire());
		assertTrue(latch.tryAcquire());
		assertFalse(latch.tryAcquire());
	}

	@Test
	public void acquire_blocksUntilReleasedFromAnotherThread() throws Exception {
		SemaphoreLatch latch = new SemaphoreLatch(1);
		assertTrue(latch.acquire());
		AtomicBoolean acquired = new AtomicBoolean();
		Thread t = new Thread(() -> acquired.set(latch.acquire()));
		t.start();
		waitForState(t, Thread.State.WAITING);
		assertFalse(acquired.get());
		latch.release();
		t.join();
		assertTrue(acquired.get());
	}

	@Test
	public void acquire_returnsFalseWhenInterrupted() throws Exception {
		SemaphoreLatch latch = new SemaphoreLatch(1);
		latch.acquire();
		AtomicBoolean result = new AtomicBoolean(true);
		AtomicBoolean interrupted = new AtomicBoolean();
		Thread t = new Thread(() -> {
			result.set(latch.acquire());
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		t.start();
		waitForState(t, Thread.State.WAITING);
		t.interrupt();
		t.join();
		assertFalse(result.get());
		assertTrue(interrupted.get());
		assertEquals(1, latch.cons());
	}

	@Test
	public void acquireWithTimeout_timesOutAndSucceeds() {
		SemaphoreLatch latch = new SemaphoreLatch(1);
		assertTrue(latch.acquire(10, TimeUnit.MILLISECONDS));
		assertFalse(latch.acquire(30, TimeUnit.MILLISECONDS));
		latch.release();
		assertTrue(latch.acquire(0, TimeUnit.MILLISECONDS));
	}

	@Test
	public void acquireWithTimeout_returnsFalseWhenInterrupted() {
		SemaphoreLatch latch = new SemaphoreLatch(1);
		latch.acquire();
		Thread.currentThread().interrupt();
		try {
			assertFalse(latch.acquire(1, TimeUnit.SECONDS));
			assertTrue(Thread.currentThread().isInterrupted());
		} finally {
			Thread.interrupted();
		}
	}

	@Test
	public void join_waitsUntilAllPermitsReleased() throws Exception {
		SemaphoreLatch latch = new SemaphoreLatch(2);
		latch.acquire();
		latch.acquire();
		AtomicBoolean joined = new AtomicBoolean();
		Thread t = new Thread(() -> {
			latch.join();
			joined.set(true);
		});
		t.start();
		waitForState(t, Thread.State.WAITING);
		latch.release();
		Thread.sleep(20);
		assertFalse(joined.get());
		latch.release();
		t.join();
		assertTrue(joined.get());
		assertTrue(latch.join() >= 0);
	}

	@Test
	public void join_stopsWhenInterrupted() {
		SemaphoreLatch latch = new SemaphoreLatch(1);
		latch.acquire();
		Thread.currentThread().interrupt();
		try {
			latch.join();
			assertTrue(Thread.currentThread().isInterrupted());
		} finally {
			Thread.interrupted();
		}
	}

	@Test
	public void fairLatch_isSemaphoreLatchWithFairOrdering() throws Exception {
		FairLatch latch = new FairLatch();
		assertEquals(1, latch.getMaxPermits());
		assertEquals(3, new FairLatch(3).getMaxPermits());
		latch.acquire();
		List<Integer> order = Collections.synchronizedList(new ArrayList<Integer>());
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 4; i++) {
			final int n = i;
			Thread t = new Thread(() -> {
				if (latch.acquire()) {
					order.add(n);
					latch.release();
				}
			});
			t.start();
			waitForState(t, Thread.State.WAITING);
			threads.add(t);
		}
		latch.release();
		for (Thread t : threads) {
			t.join();
		}
		assertEquals(Arrays.asList(0, 1, 2, 3), order);
		assertFalse(latch.isLocked());
	}

	@Test
	public void fairLatch_providesMutualExclusionAcrossThreads() throws Exception {
		FairLatch latch = new FairLatch();
		final int threads = 6;
		final int loops = 300;
		AtomicInteger inside = new AtomicInteger();
		AtomicInteger violations = new AtomicInteger();
		AtomicInteger counter = new AtomicInteger();
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
						latch.acquire();
						if (inside.incrementAndGet() != 1) {
							violations.incrementAndGet();
						}
						counter.incrementAndGet();
						inside.decrementAndGet();
						latch.release();
					}
				});
			}
			start.countDown();
			executor.shutdown();
			assertTrue(executor.awaitTermination(8, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
		assertEquals(0, violations.get());
		assertEquals(threads * loops, counter.get());
		assertEquals(0, latch.cons());
	}

	@Test
	public void stress_concurrentReleaseNeverExceedsMaxPermits() throws Exception {
		SemaphoreLatch latch = new SemaphoreLatch(2);
		latch.acquire();
		final int threads = 8;
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);
		for (int i = 0; i < threads; i++) {
			new Thread(() -> {
				try {
					start.await();
					latch.release();
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			}).start();
		}
		start.countDown();
		assertTrue(done.await(5, TimeUnit.SECONDS));
		assertEquals(2, latch.availablePermits());
		assertEquals(0, latch.cons());
	}

}
