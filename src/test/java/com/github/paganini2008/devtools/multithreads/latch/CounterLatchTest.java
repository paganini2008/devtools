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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * CounterLatchTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class CounterLatchTest {

	@Test
	public void tryAcquire_grantsUpToMaxPermits() {
		CounterLatch latch = new CounterLatch(2);
		assertFalse(latch.isLocked());
		assertEquals(2, latch.availablePermits());
		assertTrue(latch.tryAcquire());
		assertTrue(latch.tryAcquire());
		assertFalse(latch.tryAcquire());
		assertEquals(2, latch.cons());
		assertEquals(0, latch.availablePermits());
		assertTrue(latch.isLocked());
		latch.release();
		assertEquals(1, latch.cons());
		assertTrue(latch.tryAcquire());
	}

	@Test
	public void release_withoutAcquire_isNoop() {
		CounterLatch latch = new CounterLatch(1);
		latch.release();
		latch.release();
		assertEquals(0, latch.cons());
		assertTrue(latch.tryAcquire());
		assertFalse(latch.tryAcquire());
	}

	@Test
	public void acquire_blocksUntilRelease() throws Exception {
		CounterLatch latch = new CounterLatch(1);
		assertTrue(latch.acquire());
		AtomicBoolean acquired = new AtomicBoolean();
		Thread t = new Thread(() -> acquired.set(latch.acquire()));
		t.start();
		waitForState(t, Thread.State.WAITING);
		assertFalse(acquired.get());
		latch.release();
		t.join();
		assertTrue(acquired.get());
		assertEquals(1, latch.cons());
	}

	@Test
	public void acquire_returnsFalseWhenInterrupted() throws Exception {
		CounterLatch latch = new CounterLatch(1);
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
	public void acquireWithTimeout_timesOutWhenFull() {
		CounterLatch latch = new CounterLatch(1);
		assertTrue(latch.acquire(10, TimeUnit.MILLISECONDS));
		long begin = System.nanoTime();
		assertFalse(latch.acquire(50, TimeUnit.MILLISECONDS));
		assertTrue(System.nanoTime() - begin >= TimeUnit.MILLISECONDS.toNanos(45));
		assertFalse(latch.acquire(0, TimeUnit.MILLISECONDS));
		assertFalse(latch.acquire(-1, TimeUnit.MILLISECONDS));
	}

	@Test
	public void acquireWithTimeout_succeedsWhenReleasedInTime() throws Exception {
		CounterLatch latch = new CounterLatch(1);
		latch.acquire();
		AtomicBoolean result = new AtomicBoolean();
		Thread t = new Thread(() -> result.set(latch.acquire(5, TimeUnit.SECONDS)));
		t.start();
		waitForState(t, Thread.State.TIMED_WAITING);
		latch.release();
		t.join();
		assertTrue(result.get());
	}

	@Test
	public void acquireWithTimeout_returnsFalseWhenInterrupted() throws Exception {
		CounterLatch latch = new CounterLatch(1);
		latch.acquire();
		AtomicBoolean result = new AtomicBoolean(true);
		AtomicBoolean interrupted = new AtomicBoolean();
		Thread t = new Thread(() -> {
			result.set(latch.acquire(5, TimeUnit.SECONDS));
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		t.start();
		waitForState(t, Thread.State.TIMED_WAITING);
		t.interrupt();
		t.join();
		assertFalse(result.get());
		assertTrue(interrupted.get());
	}

	@Test
	public void join_returnsImmediatelyWhenUnlocked() {
		CounterLatch latch = new CounterLatch(3);
		assertTrue(latch.join() >= 0);
	}

	@Test
	public void join_waitsForAllReleases() throws Exception {
		CounterLatch latch = new CounterLatch(3);
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
	}

	@Test
	public void join_stopsWhenInterrupted() throws Exception {
		CounterLatch latch = new CounterLatch(1);
		latch.acquire();
		AtomicBoolean interrupted = new AtomicBoolean();
		Thread t = new Thread(() -> {
			latch.join();
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		t.start();
		waitForState(t, Thread.State.WAITING);
		t.interrupt();
		t.join();
		assertTrue(interrupted.get());
		assertTrue(latch.isLocked());
	}

	@Test
	public void factories_createExpectedPermits() {
		Latch single = CounterLatch.newSingleLatch();
		assertEquals(1, single.availablePermits());
		Latch unlimited = CounterLatch.newUnlimitedLatch();
		assertEquals(Integer.MAX_VALUE, unlimited.availablePermits());
	}

	@Test
	public void stress_neverExceedsMaxPermitsAndEndsUnlocked() throws Exception {
		final int maxPermits = 3;
		final int threads = 8;
		final int loops = 500;
		CounterLatch latch = new CounterLatch(maxPermits);
		AtomicInteger inside = new AtomicInteger();
		AtomicInteger maxInside = new AtomicInteger();
		AtomicInteger completed = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			for (int i = 0; i < threads; i++) {
				final int n = i;
				executor.execute(() -> {
					try {
						start.await();
					} catch (InterruptedException e) {
						return;
					}
					for (int j = 0; j < loops; j++) {
						boolean ok = n % 2 == 0 ? latch.acquire() : latch.acquire(5, TimeUnit.SECONDS);
						if (!ok) {
							continue;
						}
						int now = inside.incrementAndGet();
						maxInside.accumulateAndGet(now, Math::max);
						inside.decrementAndGet();
						completed.incrementAndGet();
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
		assertEquals(threads * loops, completed.get());
		assertTrue(maxInside.get() <= maxPermits);
		assertEquals(0, latch.cons());
		assertFalse(latch.isLocked());
	}

	@Test
	public void stress_concurrentTryAcquireNeverOversubscribes() throws Exception {
		final int maxPermits = 5;
		final int threads = 16;
		CounterLatch latch = new CounterLatch(maxPermits);
		AtomicInteger granted = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			for (int i = 0; i < threads; i++) {
				executor.execute(() -> {
					try {
						start.await();
						if (latch.tryAcquire()) {
							granted.incrementAndGet();
						}
					} catch (InterruptedException ignored) {
					} finally {
						done.countDown();
					}
				});
			}
			start.countDown();
			assertTrue(done.await(5, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
		assertEquals(maxPermits, granted.get());
		assertEquals(maxPermits, latch.cons());
	}

	@Test
	public void forEach_processesAllElementsAndWaits() {
		CounterLatch latch = new CounterLatch(2);
		List<Integer> seen = Collections.synchronizedList(new ArrayList<Integer>());
		ExecutorService executor = Executors.newFixedThreadPool(4);
		try {
			latch.forEach(Arrays.asList(1, 2, 3, 4, 5, 6), executor, seen::add);
		} finally {
			executor.shutdownNow();
		}
		assertEquals(6, seen.size());
		assertFalse(latch.isLocked());
	}

	@Test
	public void forEach_releasesPermitWhenConsumerThrows() {
		CounterLatch latch = new CounterLatch(1);
		AtomicInteger calls = new AtomicInteger();
		assertThrows(IllegalStateException.class, () -> latch.forEach(Arrays.asList(1, 2), Runnable::run, e -> {
			calls.incrementAndGet();
			throw new IllegalStateException("boom");
		}));
		assertEquals(1, calls.get());
		assertFalse(latch.isLocked());
	}

	@Test
	public void forEach_releasesPermitWhenRejected() {
		CounterLatch latch = new CounterLatch(1);
		RejectedExecutionException e = assertThrows(RejectedExecutionException.class,
				() -> latch.forEach(Arrays.asList(1), r -> {
					throw new RejectedExecutionException("rejected");
				}, x -> {
				}));
		assertEquals("rejected", e.getMessage());
		assertFalse(latch.isLocked());
	}

	@Test
	public void forEach_stopsWhenInterrupted() throws Exception {
		CounterLatch latch = new CounterLatch(1);
		latch.acquire();
		AtomicInteger calls = new AtomicInteger();
		AtomicReference<Boolean> interrupted = new AtomicReference<Boolean>();
		Thread t = new Thread(() -> {
			Thread.currentThread().interrupt();
			latch.forEach(Arrays.asList(1, 2), Runnable::run, x -> calls.incrementAndGet());
			interrupted.set(Thread.interrupted());
		});
		t.start();
		t.join();
		assertEquals(0, calls.get());
		assertSame(Boolean.TRUE, interrupted.get());
	}

	@Test
	public void forEach_releasesPermitWhenExecutorThrowsAnyRuntimeException() {
		CounterLatch latch = new CounterLatch(1);
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> latch.forEach(Arrays.asList(1, 2), r -> {
					throw new IllegalStateException("WaitQueue Full!");
				}, x -> {
				}));
		assertEquals("WaitQueue Full!", e.getMessage());
		assertFalse(latch.isLocked());
		assertEquals(0, latch.cons());
	}

	@Test
	public void stress_manyRoundsAcquireReleaseJoinWithoutLostWakeup() throws Exception {
		for (int round = 0; round < 50; round++) {
			final CounterLatch latch = new CounterLatch(2);
			final int threads = 6;
			final int loops = 50;
			AtomicInteger inside = new AtomicInteger();
			AtomicInteger maxInside = new AtomicInteger();
			CountDownLatch start = new CountDownLatch(1);
			CountDownLatch done = new CountDownLatch(threads);
			for (int i = 0; i < threads; i++) {
				final boolean timed = i % 2 == 0;
				new Thread(() -> {
					try {
						start.await();
						for (int j = 0; j < loops; j++) {
							boolean ok = timed ? latch.acquire(5, TimeUnit.SECONDS) : latch.acquire();
							assertTrue(ok);
							maxInside.accumulateAndGet(inside.incrementAndGet(), Math::max);
							inside.decrementAndGet();
							latch.release();
						}
					} catch (InterruptedException ignored) {
					} finally {
						done.countDown();
					}
				}).start();
			}
			start.countDown();
			// join concurrently with the workers; it must return once they are done
			assertTrue(done.await(5, TimeUnit.SECONDS), "round " + round);
			latch.join();
			assertTrue(maxInside.get() <= 2, "round " + round);
			assertEquals(0, latch.cons());
			assertEquals(2, latch.availablePermits());
		}
	}

	static void waitForState(Thread t, Thread.State state) throws InterruptedException {
		while (t.getState() != state && t.isAlive()) {
			Thread.sleep(1);
		}
	}

}
