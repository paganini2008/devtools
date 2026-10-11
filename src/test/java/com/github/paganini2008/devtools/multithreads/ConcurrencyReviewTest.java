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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
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
 * ConcurrencyReviewTest
 * <p>
 * Regression tests for concurrency defects found during review: permit leaks on failing executors,
 * lost elements after a rejection, deadlocks caused by callbacks re-entering a pool or a
 * {@link ForEach}, interrupt status leaking between tasks, and timers dying silently.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(20)
public class ConcurrencyReviewTest {

	private static final Duration DEADLOCK_GUARD = Duration.ofSeconds(5);

	// ---------------------------------------------------------------- ForEach

	@Test
	public void forEach_rejectedElementIsNotProcessedInsteadOfALaterOne() {
		AtomicInteger calls = new AtomicInteger();
		List<String> processed = Collections.synchronizedList(new ArrayList<String>());
		ForEach<String> forEach = new ForEach<String>(r -> {
			if (calls.incrementAndGet() == 2) {
				throw new RejectedExecutionException("busy");
			}
			r.run();
		}, 4) {
			protected void process(String element) {
				processed.add(element);
			}
		};
		forEach.accept("a");
		assertThrows(RejectedExecutionException.class, () -> forEach.accept("b"));
		forEach.accept("c");
		forEach.join(false);
		assertEquals(Arrays.asList("a", "c"), processed);
	}

	@Test
	public void forEach_anyRuntimeExceptionFromExecutorReleasesPermit() {
		ForEach<Integer> forEach = new ForEach<Integer>(r -> {
			throw new IllegalStateException("WaitQueue Full!");
		}, 1) {
			protected void process(Integer element) {
			}
		};
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> forEach.accept(1));
		assertEquals("WaitQueue Full!", e.getMessage());
		// the permit must not leak: otherwise the second accept and join block forever
		assertTimeoutPreemptively(DEADLOCK_GUARD, () -> {
			assertThrows(IllegalStateException.class, () -> forEach.accept(2));
			forEach.join(false);
		});
	}

	@Test
	public void forEach_withFullGenericThreadPoolQueueDoesNotLeakPermits() throws Exception {
		CountDownLatch blocker = new CountDownLatch(1);
		GenericThreadPool pool = new GenericThreadPool(1, 0L, 1, null);
		try {
			AtomicInteger processed = new AtomicInteger();
			ForEach<Integer> forEach = new ForEach<Integer>(pool, 10) {
				protected void process(Integer element) {
					GenericThreadPoolTest.await(blocker);
					processed.incrementAndGet();
				}
			};
			forEach.accept(1); // running
			forEach.accept(2); // parked in the wait queue
			assertThrows(IllegalStateException.class, () -> forEach.accept(3)); // wait queue full
			blocker.countDown();
			assertTimeoutPreemptively(DEADLOCK_GUARD, () -> forEach.join(false));
			assertEquals(2, processed.get());
		} finally {
			blocker.countDown();
			pool.shutdownNow();
		}
	}

	@Test
	public void forEach_recursiveAcceptFromWorkersDoesNotDeadlock() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		AtomicInteger processed = new AtomicInteger();
		try {
			ForEach<Integer> forEach = new ForEach<Integer>(executor, 2) {
				protected void process(Integer element) {
					processed.incrementAndGet();
					if (element > 0) {
						accept(element - 1);
						accept(element - 1);
					}
				}
			};
			assertTimeoutPreemptively(DEADLOCK_GUARD, () -> {
				forEach.accept(6);
				forEach.join(false);
			});
			assertEquals(127, processed.get());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void forEach_joinFromWorkerShutsDownAsynchronouslyInsteadOfDeadlocking() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		CountDownLatch returned = new CountDownLatch(1);
		AtomicInteger processed = new AtomicInteger();
		ForEach<Integer> forEach = new ForEach<Integer>(executor, 4) {
			protected void process(Integer element) {
				processed.incrementAndGet();
				if (element == 0) {
					join(false);
					join(true);
					returned.countDown();
				}
			}
		};
		try {
			forEach.accept(Arrays.asList(1, 2, 0));
			assertTrue(returned.await(5, TimeUnit.SECONDS));
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
			assertEquals(3, processed.get());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void forEach_stressManyRoundsOfConcurrentProducers() throws Exception {
		for (int round = 0; round < 50; round++) {
			ExecutorService executor = Executors.newFixedThreadPool(3);
			AtomicInteger sum = new AtomicInteger();
			ForEach<Integer> forEach = new ForEach<Integer>(executor, 2) {
				protected void process(Integer element) {
					sum.addAndGet(element);
					if (element % 7 == 0) {
						accept(1); // nested accept under contention
					}
				}
			};
			final int producers = 4;
			final int perProducer = 50;
			CountDownLatch start = new CountDownLatch(1);
			CountDownLatch done = new CountDownLatch(producers);
			for (int p = 0; p < producers; p++) {
				new Thread(() -> {
					try {
						start.await();
						for (int i = 1; i <= perProducer; i++) {
							forEach.accept(i);
						}
					} catch (InterruptedException ignored) {
					} finally {
						done.countDown();
					}
				}).start();
			}
			start.countDown();
			assertTrue(done.await(5, TimeUnit.SECONDS), "round " + round);
			forEach.join(true);
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
			// 1..50 sums to 1275; 7 multiples of 7 each add 1
			assertEquals(producers * (1275 + 7), sum.get(), "round " + round);
		}
	}

	// ---------------------------------------------------------------- GenericThreadPool

	@Test
	public void genericThreadPool_shutdownFromTaskDoesNotDeadlock() throws Exception {
		GenericThreadPool pool = new GenericThreadPool(2, 0L, 10, null);
		CountDownLatch returned = new CountDownLatch(1);
		AtomicInteger completed = new AtomicInteger();
		try {
			pool.apply(() -> {
				pool.shutdown();
				returned.countDown();
				completed.incrementAndGet();
			});
			assertTrue(returned.await(5, TimeUnit.SECONDS));
			assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
			assertEquals(1, completed.get());
			assertTrue(pool.isShutdown());
		} finally {
			pool.shutdownNow();
		}
	}

	@Test
	public void genericThreadPool_stressManyRoundsSubmitAndShutdown() throws Exception {
		for (int round = 0; round < 50; round++) {
			GenericThreadPool pool = new GenericThreadPool(3, 0L, 1000, null);
			AtomicInteger counter = new AtomicInteger();
			final int producers = 4;
			final int perProducer = 50;
			CountDownLatch start = new CountDownLatch(1);
			CountDownLatch done = new CountDownLatch(producers);
			for (int p = 0; p < producers; p++) {
				new Thread(() -> {
					try {
						start.await();
						for (int i = 0; i < perProducer; i++) {
							pool.apply(counter::incrementAndGet);
						}
					} catch (InterruptedException ignored) {
					} finally {
						done.countDown();
					}
				}).start();
			}
			start.countDown();
			assertTrue(done.await(5, TimeUnit.SECONDS), "round " + round);
			pool.shutdown();
			assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
			assertEquals(producers * perProducer, counter.get(), "round " + round);
			assertEquals(producers * perProducer, pool.getCompletedTaskCount(), "round " + round);
			assertEquals(0, pool.getQueueSize());
		}
	}

	// ---------------------------------------------------------------- Jdk14ThreadPool

	@Test
	public void jdk14ThreadPool_shutdownFromTaskDoesNotDeadlockAndReleasesWorkers() throws Exception {
		Jdk14ThreadPool pool = new Jdk14ThreadPool(2, -1L, 10);
		CountDownLatch returned = new CountDownLatch(1);
		AtomicReference<Thread> worker = new AtomicReference<Thread>();
		pool.apply(() -> {
			worker.set(Thread.currentThread());
			pool.shutdown();
			returned.countDown();
		});
		assertTrue(returned.await(5, TimeUnit.SECONDS));
		assertTrue(pool.isShutdown());
		worker.get().join(5000);
		assertFalse(worker.get().isAlive());
		assertEquals(0, pool.getPoolSize());
		assertThrows(IllegalStateException.class, () -> pool.apply(() -> {
		}));
	}

	@Test
	public void jdk14ThreadPool_interruptStatusDoesNotLeakIntoNextTask() throws Exception {
		Jdk14ThreadPool pool = new Jdk14ThreadPool(1, -1L, 10);
		try {
			CountDownLatch release = new CountDownLatch(1);
			AtomicBoolean secondSawInterrupt = new AtomicBoolean(true);
			CountDownLatch secondDone = new CountDownLatch(1);
			assertTrue(pool.apply(() -> {
				GenericThreadPoolTest.await(release);
				Thread.currentThread().interrupt();
			}));
			// parked: it is handed to the same worker right after the first task, without waiting
			assertFalse(pool.apply(() -> {
				secondSawInterrupt.set(Thread.currentThread().isInterrupted());
				secondDone.countDown();
			}));
			release.countDown();
			assertTrue(secondDone.await(5, TimeUnit.SECONDS));
			assertFalse(secondSawInterrupt.get());
		} finally {
			pool.shutdown();
		}
	}

	@Test
	public void jdk14ThreadPool_stressManyRoundsSubmitAndShutdown() throws Exception {
		for (int round = 0; round < 50; round++) {
			Jdk14ThreadPool pool = new Jdk14ThreadPool(3, -1L, 1000);
			AtomicInteger counter = new AtomicInteger();
			final int producers = 4;
			final int perProducer = 50;
			CountDownLatch start = new CountDownLatch(1);
			CountDownLatch done = new CountDownLatch(producers);
			for (int p = 0; p < producers; p++) {
				new Thread(() -> {
					try {
						start.await();
						for (int i = 0; i < perProducer; i++) {
							pool.apply(counter::incrementAndGet);
						}
					} catch (InterruptedException ignored) {
					} finally {
						done.countDown();
					}
				}).start();
			}
			start.countDown();
			assertTrue(done.await(5, TimeUnit.SECONDS), "round " + round);
			pool.shutdown();
			assertEquals(producers * perProducer, counter.get(), "round " + round);
			assertEquals(producers * perProducer, pool.getCompletedTaskCount(), "round " + round);
			assertEquals(0, pool.getPoolSize(), "round " + round);
			assertEquals(0, pool.getQueueSize());
		}
	}

	// ---------------------------------------------------------------- ThreadUtils timers

	@Test
	public void scheduleAtFixedRate_throwingOnErrorCancelsTimerAndNotifies() throws Exception {
		assertThrowingOnErrorCancels(true);
	}

	@Test
	public void scheduleWithFixedDelay_throwingOnErrorCancelsTimerAndNotifies() throws Exception {
		assertThrowingOnErrorCancels(false);
	}

	private static void assertThrowingOnErrorCancels(boolean fixedRate) throws Exception {
		AtomicInteger runs = new AtomicInteger();
		AtomicReference<Throwable> cancelled = new AtomicReference<Throwable>();
		CountDownLatch latch = new CountDownLatch(1);
		IllegalStateException failure = new IllegalStateException("task");
		Executable e = new Executable() {
			public boolean execute() {
				runs.incrementAndGet();
				throw failure;
			}

			public boolean onError(Throwable e) {
				throw new IllegalArgumentException("onError");
			}

			public void onCancellation(Throwable e) {
				cancelled.set(e);
				latch.countDown();
			}
		};
		Timer timer = fixedRate ? ThreadUtils.scheduleAtFixedRate(e, 0, 5) : ThreadUtils.scheduleWithFixedDelay(e, 0, 5);
		try {
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertSame(failure, cancelled.get());
			Thread.sleep(30);
			assertEquals(1, runs.get());
		} finally {
			timer.cancel();
		}
	}

	// ---------------------------------------------------------------- ActionPromise

	@Test
	public void promise_cancelAfterCompletionIsIgnored() throws Exception {
		GenericThreadPool pool = new GenericThreadPool(1, 0L, 10, null);
		try {
			Promise<String> promise = pool.submit((Action<String>) () -> "done");
			assertEquals("done", promise.get());
			promise.cancel();
			assertFalse(promise.isCancelled());
			assertTrue(promise.isDone());
			assertEquals("done", promise.get(10));
		} finally {
			pool.shutdown();
		}
	}

}
