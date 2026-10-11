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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ClockTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ClockTest {

	private ExecutorService executor;
	private Clock clock;

	@BeforeEach
	public void setUp() {
		executor = Executors.newFixedThreadPool(4, GenericThreadPoolTest.SILENT);
		clock = new Clock(executor, true, 5);
	}

	@AfterEach
	@Timeout(10)
	public void tearDown() throws Exception {
		clock.stop();
		executor.shutdownNow();
	}

	@Test
	public void schedule_runsTaskOnceAfterDelay() throws Exception {
		CountingTask task = new CountingTask(1);
		long begin = System.currentTimeMillis();
		clock.schedule(task, 30, TimeUnit.MILLISECONDS);
		assertEquals(1, clock.getTaskCount());
		assertTrue(task.latch.await(5, TimeUnit.SECONDS));
		assertTrue(System.currentTimeMillis() - begin >= 30);
		Thread.sleep(30);
		assertEquals(1, task.runs.get());
		assertEquals(0, clock.getTaskCount());
	}

	@Test
	public void schedule_withZeroDelayRunsOnNextTick() throws Exception {
		CountingTask task = new CountingTask(1);
		clock.schedule(task, 0, TimeUnit.MILLISECONDS);
		assertTrue(task.latch.await(5, TimeUnit.SECONDS));
	}

	@Test
	public void scheduleWithPeriod_repeatsUntilCancelled() throws Exception {
		CountingTask task = new CountingTask(3);
		clock.schedule(task, 0, 10, TimeUnit.MILLISECONDS);
		assertTrue(task.latch.await(5, TimeUnit.SECONDS));
		task.cancel();
		int runs = task.runs.get();
		Thread.sleep(50);
		assertTrue(task.runs.get() <= runs + 1);
		while (clock.getTaskCount() > 0) {
			Thread.sleep(1);
		}
	}

	@Test
	public void scheduleAtFixedRate_repeatsIncludingSlowRuns() throws Exception {
		AtomicInteger runs = new AtomicInteger();
		CountDownLatch latch = new CountDownLatch(3);
		ClockTask slow = new ClockTask() {
			protected void runTask() {
				if (runs.incrementAndGet() == 1) {
					ThreadUtils.sleep(30);
				}
				latch.countDown();
			}
		};
		clock.scheduleAtFixedRate(slow, 0, 10, TimeUnit.MILLISECONDS);
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		slow.cancel();
		CountingTask normal = new CountingTask(3);
		clock.scheduleAtFixedRate(normal, 0, 20, TimeUnit.MILLISECONDS);
		assertTrue(normal.latch.await(5, TimeUnit.SECONDS));
		normal.cancel();
	}

	@Test
	public void failingTask_isNotRepeated() throws Exception {
		AtomicInteger runs = new AtomicInteger();
		CountDownLatch ran = new CountDownLatch(1);
		clock.schedule(new ClockTask() {
			protected void runTask() {
				runs.incrementAndGet();
				ran.countDown();
				throw new IllegalStateException("boom");
			}
		}, 0, 5, TimeUnit.MILLISECONDS);
		assertTrue(ran.await(5, TimeUnit.SECONDS));
		Thread.sleep(50);
		assertEquals(1, runs.get());
		assertEquals(0, clock.getTaskCount());
	}

	@Test
	public void manyTasks_allRunExactlyOnce() throws Exception {
		final int n = 100;
		CountDownLatch latch = new CountDownLatch(n);
		AtomicInteger runs = new AtomicInteger();
		for (int i = 0; i < n; i++) {
			clock.schedule(new ClockTask() {
				protected void runTask() {
					runs.incrementAndGet();
					latch.countDown();
				}
			}, i % 20, TimeUnit.MILLISECONDS);
		}
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		Thread.sleep(30);
		assertEquals(n, runs.get());
	}

	@Test
	public void stop_discardsPendingTasksAndShutsDownExecutor() throws Exception {
		CountingTask task = new CountingTask(1);
		clock.schedule(task, 1, TimeUnit.HOURS);
		assertTrue(clock.isRunning());
		clock.stop();
		assertFalse(clock.isRunning());
		assertEquals(0, clock.getTaskCount());
		assertFalse(clock.execute());
		clock.schedule(task, 1, TimeUnit.MILLISECONDS);
		clock.schedule(task, 1, 1, TimeUnit.MILLISECONDS);
		clock.scheduleAtFixedRate(task, 1, 1, TimeUnit.MILLISECONDS);
		assertEquals(0, clock.getTaskCount());
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		clock.stop();
	}

	@Test
	public void publicConstructors_startAndStop() {
		Clock a = new Clock(1);
		Clock b = new Clock();
		Clock c = new Clock(ExecutorUtils.directExecutor(), false);
		try {
			assertTrue(a.isRunning());
			assertTrue(b.isRunning());
			assertTrue(c.execute());
		} finally {
			a.stop();
			b.stop();
			c.stop();
		}
	}

	@Test
	public void clockEvent_sourceAndClone() {
		Clock.ClockEvent event = new Clock.ClockEvent(clock, 123L);
		assertSame(clock, event.getSource());
		Clock.ClockEvent copy = event.clone();
		assertNotSame(event, copy);
		assertEquals(Long.valueOf(123L), copy.getArgument());
		assertSame(clock, copy.getSource());
	}

	static class CountingTask extends ClockTask {

		final AtomicInteger runs = new AtomicInteger();
		final CountDownLatch latch;

		CountingTask(int expected) {
			latch = new CountDownLatch(expected);
		}

		protected void runTask() {
			runs.incrementAndGet();
			latch.countDown();
		}
	}

}
