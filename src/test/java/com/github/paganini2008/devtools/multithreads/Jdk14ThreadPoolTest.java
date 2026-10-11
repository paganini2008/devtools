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

import static com.github.paganini2008.devtools.multithreads.GenericThreadPoolTest.await;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Jdk14ThreadPoolTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class Jdk14ThreadPoolTest {

	private final List<Jdk14ThreadPool> pools = new ArrayList<Jdk14ThreadPool>();

	private Jdk14ThreadPool newPool(int size, long timeout, int queueSize) {
		Jdk14ThreadPool pool = new Jdk14ThreadPool(size, timeout, queueSize);
		pools.add(pool);
		return pool;
	}

	@AfterEach
	@Timeout(10)
	public void tearDown() {
		for (Jdk14ThreadPool pool : pools) {
			pool.shutdown();
		}
	}

	@Test
	public void constructor_rejectsNonPositivePoolSize() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Jdk14ThreadPool(0, 0, 1));
		assertEquals("MaxPoolSize must be greater than 0.", e.getMessage());
	}

	@Test
	public void apply_runsTasksAndReusesWorkers() throws Exception {
		Jdk14ThreadPool pool = newPool(2, 0, 10);
		for (int i = 0; i < 5; i++) {
			CountDownLatch done = new CountDownLatch(1);
			pool.execute(done::countDown);
			assertTrue(done.await(5, TimeUnit.SECONDS));
		}
		pool.shutdown();
		assertEquals(5, pool.getCompletedTaskCount());
		assertEquals(0, pool.getFailedTaskCount());
		assertEquals(2, pool.getMaxPoolSize());
		assertTrue(pool.getPoolSize() <= 2);
		assertTrue(pool.isShutdown());
		assertEquals(0, pool.getPoolSize());
	}

	@Test
	public void apply_nullTaskThrowsNpe() {
		Jdk14ThreadPool pool = newPool(1, 0, 1);
		assertThrows(NullPointerException.class, () -> pool.apply(null));
	}

	@Test
	public void apply_negativeTimeoutParksTaskWhenBusy() throws Exception {
		Jdk14ThreadPool pool = newPool(1, -1, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		CountDownLatch started = new CountDownLatch(1);
		CountDownLatch parkedDone = new CountDownLatch(1);
		assertTrue(pool.apply(() -> {
			started.countDown();
			await(blocker);
		}));
		assertTrue(started.await(5, TimeUnit.SECONDS));
		assertFalse(pool.apply(parkedDone::countDown));
		assertEquals(1, pool.getQueueSize());
		assertEquals(1, pool.getActiveThreadSize());
		assertEquals(0, pool.getIdleThreadSize());
		assertTrue(pool.toString().startsWith("[Jdk14ThreadPool]: poolSize=1"));
		blocker.countDown();
		assertTrue(parkedDone.await(5, TimeUnit.SECONDS));
	}

	@Test
	public void apply_zeroTimeoutBlocksUntilWorkerFree() throws Exception {
		Jdk14ThreadPool pool = newPool(1, 0, 10);
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
	}

	@Test
	public void apply_positiveTimeoutWaitsThenParks() throws Exception {
		Jdk14ThreadPool pool = newPool(1, 30, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		long begin = System.nanoTime();
		assertFalse(pool.apply(() -> {
		}));
		assertTrue(System.nanoTime() - begin >= TimeUnit.MILLISECONDS.toNanos(25));
		blocker.countDown();
	}

	@Test
	public void apply_interruptedWhileWaitingParksTask() throws Exception {
		Jdk14ThreadPool pool = newPool(1, 0, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		AtomicBoolean result = new AtomicBoolean(true);
		AtomicBoolean timedResult = new AtomicBoolean(true);
		Jdk14ThreadPool timedPool = newPool(1, 5000, 10);
		CountDownLatch timedBlocker = new CountDownLatch(1);
		timedPool.apply(() -> await(timedBlocker));
		Thread t = new Thread(() -> {
			Thread.currentThread().interrupt();
			result.set(pool.apply(() -> {
			}));
			Thread.currentThread().interrupt();
			timedResult.set(timedPool.apply(() -> {
			}));
		});
		t.start();
		t.join();
		assertFalse(result.get());
		assertFalse(timedResult.get());
		blocker.countDown();
		timedBlocker.countDown();
	}

	@Test
	public void apply_throwsWhenWaitQueueFull() throws Exception {
		Jdk14ThreadPool pool = newPool(1, -1, 1);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		pool.apply(() -> {
		});
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> pool.apply(() -> {
		}));
		assertEquals("WaitQueue Full!", e.getMessage());
		assertEquals(1, pool.getQueueSize());
		AtomicReference<Runnable> rejected = new AtomicReference<Runnable>();
		RejectedExecutionHandler handler = (r, tp) -> rejected.set(r);
		pool.setRejectedExecutionHandler(handler);
		Runnable task = () -> {
		};
		assertFalse(pool.apply(task));
		assertSame(task, rejected.get());
		assertEquals(1, pool.getQueueSize());
		blocker.countDown();
	}

	@Test
	public void apply_afterShutdownThrows() {
		Jdk14ThreadPool pool = newPool(1, 0, 1);
		pool.shutdown();
		pool.shutdown();
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> pool.apply(() -> {
		}));
		assertEquals("ThreadPool is shutdown now.", e.getMessage());
	}

	@Test
	public void hooksAreInvokedAndFailuresCounted() throws Exception {
		List<String> events = new ArrayList<String>();
		CountDownLatch done = new CountDownLatch(2);
		Jdk14ThreadPool pool = new Jdk14ThreadPool(1, 0, 10) {

			protected void beforeRun(Thread thread, Runnable r) {
				synchronized (events) {
					events.add("before");
				}
			}

			protected void afterRun(Runnable r, Throwable e) {
				synchronized (events) {
					events.add(e == null ? "ok" : "error:" + e.getMessage());
				}
				done.countDown();
				throw new IllegalStateException("ignored");
			}

			protected Thread newThread(Runnable task) {
				Thread t = new Thread(task, "custom-worker");
				t.setDaemon(true);
				return t;
			}
		};
		pools.add(pool);
		pool.apply(() -> {
		});
		pool.apply(() -> {
			throw new IllegalArgumentException("boom");
		});
		assertTrue(done.await(5, TimeUnit.SECONDS));
		pool.shutdown();
		assertEquals(1, pool.getFailedTaskCount());
		assertEquals(2, pool.getCompletedTaskCount());
		synchronized (events) {
			assertEquals("[before, ok, before, error:boom]", events.toString());
		}
	}

	@Test
	public void keepIdleSize_validatesAndRetainsIdleWorkers() throws Exception {
		Jdk14ThreadPool pool = newPool(4, -1, 10);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> pool.keepIdleSize(0, 5));
		assertEquals("MaxIdleSize must greater than 0.", e.getMessage());
		CountDownLatch blocker = new CountDownLatch(1);
		CountDownLatch started = new CountDownLatch(4);
		for (int i = 0; i < 4; i++) {
			pool.apply(() -> {
				started.countDown();
				await(blocker);
			});
		}
		assertTrue(started.await(5, TimeUnit.SECONDS));
		assertEquals(4, pool.getPoolSize());
		blocker.countDown();
		while (pool.getIdleThreadSize() < 4) {
			Thread.sleep(1);
		}
		// intervals shorter than 3 seconds are ignored
		pool.keepIdleSize(1, 1);
		pool.keepIdleSize(1, 3600);
		pool.keepIdleSize(2, 3600);
		Jdk14ThreadPool.IdleQueueKeeper keeper = pool.new IdleQueueKeeper(1);
		assertTrue(keeper.execute());
		assertEquals(1, pool.getIdleThreadSize());
		assertEquals(1, pool.getPoolSize());
		CountDownLatch again = new CountDownLatch(3);
		for (int i = 0; i < 3; i++) {
			pool.apply(again::countDown);
		}
		assertTrue(again.await(5, TimeUnit.SECONDS));
		pool.shutdown();
		assertFalse(keeper.execute());
		pool.keepIdleSize(1, 3600);
	}

	@Test
	public void submit_returnsResultAndHandlesFailure() {
		Jdk14ThreadPool pool = newPool(2, 0, 10);
		Promise<String> ok = pool.submit(() -> "done");
		assertEquals("done", ok.get());
		AtomicReference<Exception> failure = new AtomicReference<Exception>();
		Promise<String> failed = pool.submit(new Action<String>() {

			public String execute() throws Exception {
				throw new IllegalStateException("bad");
			}

			public void onFailure(Exception e, ThreadPool threadPool) {
				failure.set(e);
			}
		});
		assertNull(failed.get());
		assertEquals("bad", failure.get().getMessage());
	}

	@Test
	public void shutdown_waitsForRunningAndParkedTasks() throws Exception {
		Jdk14ThreadPool pool = newPool(2, -1, 1000);
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0; i < 100; i++) {
			pool.apply(() -> {
				ThreadUtils.sleep(1);
				counter.incrementAndGet();
			});
		}
		pool.shutdown();
		assertEquals(100, counter.get());
		assertEquals(0, pool.getQueueSize());
		assertEquals(0, pool.getActiveThreadSize());
	}

	@Test
	public void shutdown_stopsWhenInterrupted() throws Exception {
		Jdk14ThreadPool pool = newPool(1, -1, 10);
		CountDownLatch blocker = new CountDownLatch(1);
		pool.apply(() -> await(blocker));
		AtomicBoolean interrupted = new AtomicBoolean();
		Thread t = new Thread(() -> {
			Thread.currentThread().interrupt();
			pool.shutdown();
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		t.start();
		t.join();
		assertTrue(interrupted.get());
		assertTrue(pool.isShutdown());
		assertEquals(1, pool.getPoolSize());
		blocker.countDown();
		pool.shutdown();
		assertEquals(0, pool.getPoolSize());
	}

	@Test
	public void stress_concurrentProducersLoseNoTasks() throws Exception {
		Jdk14ThreadPool pool = newPool(4, -1, Integer.MAX_VALUE);
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
		assertEquals(producers * perProducer, executed.size());
		assertEquals(0, duplicates.get());
		assertEquals(producers * perProducer, pool.getCompletedTaskCount());
	}

	@Test
	public void stress_blockingProducersWithSmallPool() throws Exception {
		Jdk14ThreadPool pool = newPool(2, 0, 10);
		final int producers = 4;
		final int perProducer = 300;
		AtomicInteger counter = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService producerPool = Executors.newFixedThreadPool(producers);
		try {
			for (int p = 0; p < producers; p++) {
				producerPool.execute(() -> {
					try {
						start.await();
					} catch (InterruptedException e) {
						return;
					}
					for (int i = 0; i < perProducer; i++) {
						pool.apply(counter::incrementAndGet);
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
		assertEquals(producers * perProducer, counter.get());
	}

}
