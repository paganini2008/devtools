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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.RecursiveLatch;

/**
 * GenericThreadPoolTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class GenericThreadPoolTest {

	static final ThreadFactory SILENT = new ThreadFactoryBuilder().setNameFormat("test-pool-%d").setDaemon(true)
			.setUncaughtExceptionHandler((t, e) -> {
			}).build();

	private final List<ExecutorService> pools = new ArrayList<ExecutorService>();

	private GenericThreadPool newPool(int size, long timeout, int queueSize) {
		GenericThreadPool pool = new GenericThreadPool(size, timeout, queueSize, SILENT);
		pools.add(pool);
		return pool;
	}

	@AfterEach
	@Timeout(10)
	public void tearDown() {
		for (ExecutorService pool : pools) {
			pool.shutdownNow();
		}
	}

	@Test
	public void apply_runsTaskImmediatelyWhenPermitAvailable() throws Exception {
		GenericThreadPool pool = newPool(2, 0, 10);
		CountDownLatch done = new CountDownLatch(2);
		assertTrue(pool.apply(done::countDown));
		pool.execute(done::countDown);
		assertTrue(done.await(5, TimeUnit.SECONDS));
		pool.shutdown();
		assertTrue(pool.isShutdown());
		assertEquals(2, pool.getCompletedTaskCount());
		assertEquals(0, pool.getFailedTaskCount());
		assertEquals(2, pool.getMaxPoolSize());
	}

	@Test
	public void apply_nullTaskThrowsNpe() {
		GenericThreadPool pool = newPool(1, 0, 1);
		assertThrows(NullPointerException.class, () -> pool.apply(null));
	}

	@Test
	public void apply_parksTaskWhenBusyAndRunsItLater() throws Exception {
		GenericThreadPool pool = newPool(1, 0, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		CountDownLatch started = new CountDownLatch(1);
		AtomicBoolean parkedRan = new AtomicBoolean();
		assertTrue(pool.apply(() -> {
			started.countDown();
			await(blocker);
		}));
		assertTrue(started.await(5, TimeUnit.SECONDS));
		assertFalse(pool.apply(() -> parkedRan.set(true)));
		assertEquals(1, pool.getQueueSize());
		assertEquals(1, pool.getActiveThreadSize());
		assertEquals(0, pool.getIdleThreadSize());
		assertTrue(pool.toString().startsWith("[GenericThreadPool]: maxPoolSize=1"));
		blocker.countDown();
		pool.shutdown();
		assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
		assertTrue(parkedRan.get());
		assertEquals(0, pool.getQueueSize());
	}

	@Test
	public void apply_withPositiveTimeout_waitsThenParks() throws Exception {
		GenericThreadPool pool = newPool(1, 30, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		long begin = System.nanoTime();
		assertFalse(pool.apply(() -> {
		}));
		assertTrue(System.nanoTime() - begin >= TimeUnit.MILLISECONDS.toNanos(25));
		assertEquals(1, pool.getQueueSize());
		blocker.countDown();
		pool.shutdown();
	}

	@Test
	public void apply_withNegativeTimeout_blocksUntilPermitReleased() throws Exception {
		GenericThreadPool pool = newPool(1, -1, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		AtomicBoolean result = new AtomicBoolean();
		Thread t = new Thread(() -> result.set(pool.apply(() -> {
		})));
		t.start();
		Thread.sleep(30);
		assertTrue(t.isAlive());
		blocker.countDown();
		t.join();
		assertTrue(result.get());
		pool.shutdown();
	}

	@Test
	public void apply_throwsWhenWaitQueueFull() throws Exception {
		GenericThreadPool pool = newPool(1, 0, 1);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		pool.apply(() -> {
		});
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> pool.apply(() -> {
		}));
		assertEquals("WaitQueue Full!", e.getMessage());
		blocker.countDown();
		pool.shutdown();
	}

	@Test
	public void apply_invokesRejectedExecutionHandlerWhenQueueUnavailable() throws Exception {
		GenericThreadPool pool = newPool(1, 0, 0);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		AtomicReference<Runnable> rejected = new AtomicReference<Runnable>();
		AtomicReference<ThreadPool> rejectedBy = new AtomicReference<ThreadPool>();
		RejectedExecutionHandler handler = (r, tp) -> {
			rejected.set(r);
			rejectedBy.set(tp);
		};
		pool.setRejectedExecutionHandler(handler);
		Runnable task = () -> {
		};
		assertFalse(pool.apply(task));
		assertSame(task, rejected.get());
		assertSame(pool, rejectedBy.get());
		blocker.countDown();
		pool.shutdown();
	}

	@Test
	public void apply_afterShutdown_isRejected() {
		CounterLatch latch = new CounterLatch(1);
		GenericThreadPool pool = new GenericThreadPool(1, latch, 0, 1, null);
		pools.add(pool);
		pool.shutdown();
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> pool.apply(() -> {
		}));
		assertEquals("ThreadPool is shutdown.", e.getMessage());
		AtomicInteger rejected = new AtomicInteger();
		RejectedExecutionHandler handler = (r, tp) -> rejected.incrementAndGet();
		pool.setRejectedExecutionHandler(handler);
		assertFalse(pool.apply(() -> {
		}));
		assertEquals(1, rejected.get());
		// permits were released on rejection
		assertTrue(pool.isShutdown());
		assertEquals(0, latch.cons());
		assertFalse(latch.isLocked());
	}

	@Test
	public void apply_permitFreedWhileParkingIsUsedImmediately() throws Exception {
		// The first tryAcquire fails (as if all workers were busy); by the time the task is parked
		// the permit is available again, and no running task is left to drain the wait queue.
		AtomicInteger tries = new AtomicInteger();
		CounterLatch latch = new CounterLatch(1) {
			public boolean tryAcquire() {
				return tries.incrementAndGet() > 1 && super.tryAcquire();
			}
		};
		GenericThreadPool pool = new GenericThreadPool(1, latch, 0, 10, null);
		pools.add(pool);
		CountDownLatch ran = new CountDownLatch(1);
		assertFalse(pool.apply(ran::countDown));
		assertTrue(ran.await(5, TimeUnit.SECONDS));
		pool.shutdown();
		assertEquals(0, pool.getQueueSize());
	}

	@Test
	public void failedTasks_areCounted() throws Exception {
		GenericThreadPool pool = newPool(2, -1, 10);
		for (int i = 0; i < 4; i++) {
			final int n = i;
			pool.apply(() -> {
				if (n % 2 == 0) {
					throw new IllegalStateException("fail-" + n);
				}
			});
		}
		pool.shutdown();
		assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
		assertEquals(2, pool.getFailedTaskCount());
		assertEquals(4, pool.getCompletedTaskCount());
	}

	@Test
	public void constructor_defaultsNullLatchAndThreadFactory() throws Exception {
		GenericThreadPool pool = new GenericThreadPool(1, null, 0, 1, null);
		pools.add(pool);
		CountDownLatch done = new CountDownLatch(1);
		pool.apply(done::countDown);
		assertTrue(done.await(5, TimeUnit.SECONDS));
		pool.shutdown();
	}

	@Test
	public void submit_returnsResultOfAction() {
		GenericThreadPool pool = newPool(2, -1, 10);
		Promise<Integer> promise = pool.submit(action(() -> 42));
		assertEquals(Integer.valueOf(42), promise.get());
		assertTrue(promise.isDone());
		assertFalse(promise.isCancelled());
		assertTrue(promise.getElapsed() >= 0);
		pool.shutdown();
	}

	@Test
	public void submit_runsReactionChainAndKeepsLastNonNullResult() {
		GenericThreadPool pool = newPool(1, -1, 10);
		List<String> threadPools = new ArrayList<String>();
		Promise<Integer> promise = pool.submit(new Action<Integer>() {

			public Integer execute() {
				return 1;
			}

			public boolean shouldReact(Integer result) {
				return result != null && result < 5;
			}

			public Integer onReaction(Integer result, ThreadPool threadPool) {
				threadPools.add(threadPool == pool ? "same" : "other");
				return result + 1;
			}
		});
		assertEquals(Integer.valueOf(5), promise.get(5000));
		assertEquals(4, threadPools.size());
		assertTrue(threadPools.stream().allMatch("same"::equals));
		pool.shutdown();
	}

	@Test
	public void submit_failureInvokesOnFailureAndYieldsNull() {
		GenericThreadPool pool = newPool(1, -1, 10);
		AtomicReference<Exception> failure = new AtomicReference<Exception>();
		Promise<String> promise = pool.submit(new Action<String>() {

			public String execute() throws Exception {
				throw new IllegalArgumentException("bad");
			}

			public void onFailure(Exception e, ThreadPool threadPool) {
				failure.set(e);
			}
		});
		assertNull(promise.get());
		assertTrue(promise.isDone());
		assertEquals("bad", failure.get().getMessage());
		pool.shutdown();
	}

	@Test
	public void promise_getWithTimeoutReturnsNullWhenNotDone() throws Exception {
		GenericThreadPool pool = newPool(1, -1, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		Promise<String> promise = pool.submit(action(() -> {
			blocker.await();
			return "late";
		}));
		assertNull(promise.get(20));
		assertFalse(promise.isDone());
		blocker.countDown();
		assertEquals("late", promise.get());
		assertEquals("late", promise.get(10));
		pool.shutdown();
	}

	@Test
	public void promise_cancelWakesUpWaiters() throws Exception {
		GenericThreadPool pool = newPool(1, -1, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		Promise<String> promise = pool.submit(action(() -> {
			blocker.await();
			return "x";
		}));
		AtomicBoolean returned = new AtomicBoolean();
		Thread waiter = new Thread(() -> {
			promise.get();
			returned.set(true);
		});
		waiter.start();
		Thread.sleep(20);
		promise.cancel();
		waiter.join();
		assertTrue(returned.get());
		assertTrue(promise.isCancelled());
		assertTrue(promise.isDone());
		blocker.countDown();
		pool.shutdown();
		promise.cancel();
	}

	@Test
	public void promise_getIsInterruptible() throws Exception {
		GenericThreadPool pool = newPool(1, -1, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		Promise<String> promise = pool.submit(action(() -> {
			blocker.await();
			return "x";
		}));
		AtomicBoolean interrupted = new AtomicBoolean();
		Thread waiter = new Thread(() -> {
			Thread.currentThread().interrupt();
			promise.get();
			promise.get(1000);
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		waiter.start();
		waiter.join();
		assertTrue(interrupted.get());
		blocker.countDown();
		pool.shutdown();
	}

	@Test
	public void shutdown_waitsForParkedTasks() throws Exception {
		GenericThreadPool pool = newPool(2, 0, 100);
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0; i < 50; i++) {
			pool.apply(() -> {
				ThreadUtils.sleep(1);
				counter.incrementAndGet();
			});
		}
		pool.shutdown();
		assertEquals(50, counter.get());
		assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
	}

	@Test
	public void recursiveLatch_allowsWorkersToSubmitNestedTasks() throws Exception {
		GenericThreadPool pool = new GenericThreadPool(2, new RecursiveLatch(4), -1, Integer.MAX_VALUE, SILENT);
		pools.add(pool);
		AtomicInteger counter = new AtomicInteger();
		CountDownLatch done = new CountDownLatch(10);
		for (int i = 0; i < 5; i++) {
			pool.apply(() -> {
				counter.incrementAndGet();
				done.countDown();
				pool.apply(() -> {
					counter.incrementAndGet();
					done.countDown();
				});
			});
		}
		assertTrue(done.await(5, TimeUnit.SECONDS));
		pool.shutdown();
		assertEquals(10, counter.get());
	}

	@Test
	public void stress_concurrentProducersLoseNoTasks() throws Exception {
		GenericThreadPool pool = new GenericThreadPool(4, new CounterLatch(4), 0, Integer.MAX_VALUE, SILENT);
		pools.add(pool);
		final int producers = 8;
		final int perProducer = 500;
		Set<Integer> executed = ConcurrentHashMap.newKeySet();
		AtomicInteger duplicates = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService producerPool = Executors.newFixedThreadPool(producers);
		try {
			for (int p = 0; p < producers; p++) {
				final int base = p * perProducer;
				producerPool.execute(() -> {
					try {
						start.await();
					} catch (InterruptedException e) {
						return;
					}
					for (int i = 0; i < perProducer; i++) {
						final int id = base + i;
						pool.apply(() -> {
							if (!executed.add(id)) {
								duplicates.incrementAndGet();
							}
						});
					}
				});
			}
			start.countDown();
			producerPool.shutdown();
			assertTrue(producerPool.awaitTermination(8, TimeUnit.SECONDS));
		} finally {
			producerPool.shutdownNow();
		}
		pool.shutdown();
		assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
		assertEquals(producers * perProducer, executed.size());
		assertEquals(0, duplicates.get());
		assertEquals(0, pool.getQueueSize());
	}

	@Test
	public void stress_concurrentSubmitAndGet() throws Exception {
		GenericThreadPool pool = newPool(4, -1, Integer.MAX_VALUE);
		final int callers = 6;
		final int perCaller = 100;
		AtomicInteger sum = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService callerPool = Executors.newFixedThreadPool(callers);
		try {
			for (int c = 0; c < callers; c++) {
				callerPool.execute(() -> {
					try {
						start.await();
					} catch (InterruptedException e) {
						return;
					}
					List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();
					for (int i = 1; i <= perCaller; i++) {
						final int n = i;
						promises.add(pool.submit(action(() -> n)));
					}
					for (Promise<Integer> p : promises) {
						sum.addAndGet(p.get());
					}
				});
			}
			start.countDown();
			callerPool.shutdown();
			assertTrue(callerPool.awaitTermination(8, TimeUnit.SECONDS));
		} finally {
			callerPool.shutdownNow();
		}
		assertEquals(callers * (perCaller * (perCaller + 1) / 2), sum.get());
		pool.shutdown();
	}

	static <R> Action<R> action(Action<R> action) {
		return action;
	}

	static void await(CountDownLatch latch) {
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
