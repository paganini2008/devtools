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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ExecutorUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ExecutorUtilsTest {

	@Test
	public void statistics_fromThreadPoolExecutor() throws Exception {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		try {
			CountDownLatch blocker = new CountDownLatch(1);
			CountDownLatch started = new CountDownLatch(2);
			for (int i = 0; i < 3; i++) {
				executor.execute(() -> {
					started.countDown();
					GenericThreadPoolTest.await(blocker);
				});
			}
			assertTrue(started.await(5, TimeUnit.SECONDS));
			assertEquals(2, ExecutorUtils.getPoolSize(executor, -1));
			assertEquals(2, ExecutorUtils.getCorePoolSize(executor, -1));
			assertEquals(2, ExecutorUtils.getLargestPoolSize(executor, -1));
			assertEquals(2, ExecutorUtils.getMaximumPoolSize(executor, -1));
			assertEquals(2, ExecutorUtils.getActiveCount(executor, -1));
			assertEquals(1, ExecutorUtils.getQueueSize(executor, -1));
			blocker.countDown();
			executor.shutdown();
			assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
			assertEquals(3, ExecutorUtils.getCompletedTaskCount(executor, -1));
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void statistics_returnDefaultForOtherExecutorsAndNull() {
		Executor direct = ExecutorUtils.directExecutor();
		assertEquals(-1, ExecutorUtils.getPoolSize(direct, -1));
		assertEquals(-2, ExecutorUtils.getCorePoolSize(null, -2));
		assertEquals(-3, ExecutorUtils.getLargestPoolSize(direct, -3));
		assertEquals(-4, ExecutorUtils.getMaximumPoolSize(null, -4));
		assertEquals(-5, ExecutorUtils.getActiveCount(direct, -5));
		assertEquals(-6, ExecutorUtils.getCompletedTaskCount(direct, -6));
		assertEquals(-7, ExecutorUtils.getQueueSize(null, -7));
		assertTrue(ExecutorUtils.processorCount > 0);
	}

	@Test
	public void callInBackground_usesExecutorOrCallsDirectly() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Thread caller = Thread.currentThread();
			assertNotSame(caller, ExecutorUtils.callInBackground(executor, Thread::currentThread));
			assertSame(caller, ExecutorUtils.callInBackground(null, Thread::currentThread));
			ExecutionException e = assertThrows(ExecutionException.class, () -> ExecutorUtils.callInBackground(executor, () -> {
				throw new IllegalStateException("bad");
			}));
			assertEquals("bad", e.getCause().getMessage());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void runInBackground_usesExecutorOrRunsDirectly() throws Exception {
		AtomicReference<Thread> ran = new AtomicReference<Thread>();
		ExecutorUtils.runInBackground(null, () -> ran.set(Thread.currentThread()));
		assertSame(Thread.currentThread(), ran.get());
		CountDownLatch done = new CountDownLatch(1);
		ExecutorUtils.runInBackground(ExecutorUtils.newThreadExecutor(), () -> {
			ran.set(Thread.currentThread());
			done.countDown();
		});
		assertTrue(done.await(5, TimeUnit.SECONDS));
		assertNotSame(Thread.currentThread(), ran.get());
	}

	@Test
	public void isShutdownAndIsTerminated() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		assertFalse(ExecutorUtils.isShutdown(executor));
		assertFalse(ExecutorUtils.isTerminated(executor));
		executor.shutdown();
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		assertTrue(ExecutorUtils.isShutdown(executor));
		assertTrue(ExecutorUtils.isTerminated(executor));
		assertFalse(ExecutorUtils.isShutdown(ExecutorUtils.directExecutor()));
		assertFalse(ExecutorUtils.isTerminated(null));
	}

	@Test
	public void gracefulShutdown_letsRunningTasksFinish() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CountDownLatch release = new CountDownLatch(1);
		CountDownLatch finished = new CountDownLatch(1);
		executor.execute(() -> {
			GenericThreadPoolTest.await(release);
			finished.countDown();
		});
		ExecutorUtils.gracefulShutdown(executor, 5000);
		assertTrue(executor.isShutdown());
		release.countDown();
		assertTrue(finished.await(5, TimeUnit.SECONDS));
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
	}

	@Test
	public void gracefulShutdown_forcesShutdownAfterTimeout() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CountDownLatch started = new CountDownLatch(1);
		AtomicReference<Boolean> interrupted = new AtomicReference<Boolean>(false);
		executor.execute(() -> {
			started.countDown();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				interrupted.set(true);
			}
		});
		assertTrue(started.await(5, TimeUnit.SECONDS));
		ExecutorUtils.gracefulShutdown(executor, 20);
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		assertTrue(interrupted.get());
	}

	@Test
	public void gracefulShutdown_ignoresNullNonServiceAndTerminatedExecutors() throws Exception {
		ExecutorUtils.gracefulShutdown(null, 10);
		ExecutorUtils.gracefulShutdown(ExecutorUtils.directExecutor(), 10);
		ExecutorService idle = Executors.newSingleThreadExecutor();
		ExecutorUtils.gracefulShutdown(idle, 10);
		assertTrue(idle.awaitTermination(5, TimeUnit.SECONDS));
		ExecutorUtils.gracefulShutdown(idle, 10);
		assertTrue(idle.isTerminated());
	}

	@Test
	public void directExecutor_runsInCallerThread() {
		AtomicReference<Thread> ran = new AtomicReference<Thread>();
		ExecutorUtils.directExecutor().execute(() -> ran.set(Thread.currentThread()));
		assertSame(Thread.currentThread(), ran.get());
	}

}
