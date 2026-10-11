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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ThreadUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ThreadUtilsTest {

	@Test
	public void sleep_returnsTrueAfterSleeping() {
		long begin = System.nanoTime();
		assertTrue(ThreadUtils.sleep(10));
		assertTrue(System.nanoTime() - begin >= TimeUnit.MILLISECONDS.toNanos(9));
		assertTrue(ThreadUtils.sleep(0));
		assertTrue(ThreadUtils.sleep(-5));
		assertTrue(ThreadUtils.sleep(1, TimeUnit.MILLISECONDS));
		assertTrue(ThreadUtils.randomSleep(3));
		assertTrue(ThreadUtils.randomSleep(1, 3));
	}

	@Test
	public void sleep_returnsFalseAndKeepsInterruptStatus() {
		Thread.currentThread().interrupt();
		try {
			assertFalse(ThreadUtils.sleep(1000));
			assertTrue(Thread.currentThread().isInterrupted());
		} finally {
			Thread.interrupted();
		}
	}

	@Test
	public void wait_returnsFalseForNullMonitorAndRejectsNullCondition() {
		assertFalse(ThreadUtils.wait(null, () -> true));
		assertFalse(ThreadUtils.wait(null, () -> true, 10));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ThreadUtils.wait(new Object(), null));
		assertEquals("NonNull condition", e.getMessage());
		assertThrows(IllegalArgumentException.class, () -> ThreadUtils.wait(new Object(), null, 10));
		assertThrows(IllegalArgumentException.class, () -> ThreadUtils.notify(new Object(), null));
		assertThrows(IllegalArgumentException.class, () -> ThreadUtils.notifyAll(new Object(), null));
		assertThrows(IllegalArgumentException.class, () -> ThreadUtils.forUpdate(null, null, () -> {
		}));
		assertThrows(IllegalArgumentException.class, () -> ThreadUtils.forUpdate(null, () -> true, null));
	}

	@Test
	public void wait_wakesUpWhenConditionBecomesTrue() throws Exception {
		Object monitor = new Object();
		AtomicBoolean flag = new AtomicBoolean();
		AtomicBoolean result = new AtomicBoolean();
		Thread t = new Thread(() -> result.set(ThreadUtils.wait(monitor, flag::get)));
		t.start();
		while (t.getState() != Thread.State.WAITING) {
			Thread.sleep(1);
		}
		ThreadUtils.notify(monitor, () -> false);
		ThreadUtils.notify(monitor);
		Thread.sleep(10);
		assertTrue(t.isAlive());
		flag.set(true);
		ThreadUtils.notifyAll(monitor);
		t.join();
		assertTrue(result.get());
		ThreadUtils.notify(null);
		ThreadUtils.notifyAll(null);
		ThreadUtils.notifyAll(monitor, () -> false);
	}

	@Test
	public void wait_isInterruptible() throws Exception {
		Object monitor = new Object();
		AtomicBoolean result = new AtomicBoolean(true);
		AtomicBoolean timedResult = new AtomicBoolean(true);
		AtomicBoolean interrupted = new AtomicBoolean();
		Thread t = new Thread(() -> {
			Thread.currentThread().interrupt();
			result.set(ThreadUtils.wait(monitor, () -> false));
			timedResult.set(ThreadUtils.wait(monitor, () -> false, 5000));
			interrupted.set(Thread.currentThread().isInterrupted());
		});
		t.start();
		t.join();
		assertFalse(result.get());
		assertFalse(timedResult.get());
		assertTrue(interrupted.get());
	}

	@Test
	public void waitWithTimeout_returnsFalseAfterTimeoutAndTrueWhenSatisfied() throws Exception {
		Object monitor = new Object();
		long begin = System.nanoTime();
		assertFalse(ThreadUtils.wait(monitor, () -> false, 50));
		long elapsed = System.nanoTime() - begin;
		assertTrue(elapsed >= TimeUnit.MILLISECONDS.toNanos(45), "elapsed " + elapsed);
		assertTrue(elapsed < TimeUnit.SECONDS.toNanos(3), "elapsed " + elapsed);
		assertTrue(ThreadUtils.wait(monitor, () -> true, 0));
		assertFalse(ThreadUtils.wait(monitor, () -> false, 0));
		AtomicBoolean flag = new AtomicBoolean();
		AtomicBoolean result = new AtomicBoolean();
		Thread t = new Thread(() -> result.set(ThreadUtils.wait(monitor, flag::get, 5000)));
		t.start();
		while (t.getState() != Thread.State.TIMED_WAITING) {
			Thread.sleep(1);
		}
		flag.set(true);
		ThreadUtils.notifyAll(monitor);
		t.join();
		assertTrue(result.get());
	}

	@Test
	public void forUpdate_runsOperationOnlyWhenConditionHolds() {
		AtomicInteger counter = new AtomicInteger();
		ThreadUtils.forUpdate(null, () -> true, counter::incrementAndGet);
		ThreadUtils.forUpdate(new Object(), () -> false, counter::incrementAndGet);
		assertEquals(1, counter.get());
	}

	@Test
	public void nullConditionResult_isTreatedAsFalse() {
		AtomicInteger counter = new AtomicInteger();
		ThreadUtils.forUpdate(new Object(), () -> null, counter::incrementAndGet);
		assertEquals(0, counter.get());
		assertFalse(ThreadUtils.wait(new Object(), () -> null, 1));
		ThreadUtils.notify(new Object(), () -> null);
		ThreadUtils.notifyAll(new Object(), () -> null);
	}

	@Test
	public void forUpdate_isAtomicUnderContention() throws Exception {
		Object monitor = new Object();
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger flushes = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		Thread[] threads = new Thread[6];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(() -> {
				try {
					start.await();
				} catch (InterruptedException e) {
					return;
				}
				for (int j = 0; j < 500; j++) {
					counter.incrementAndGet();
					ThreadUtils.forUpdate(monitor, () -> counter.get() >= 10, () -> {
						counter.addAndGet(-10);
						flushes.incrementAndGet();
					});
				}
			});
			threads[i].start();
		}
		start.countDown();
		for (Thread t : threads) {
			t.join();
		}
		assertEquals(3000, flushes.get() * 10 + counter.get());
		assertTrue(counter.get() < 10);
	}

	@Test
	public void runAsThread_variants() throws Exception {
		AtomicReference<String> name = new AtomicReference<String>();
		ThreadUtils.runAsThread("named", () -> name.set(ThreadUtils.currentThreadName())).join();
		assertEquals("named", name.get());
		AtomicReference<Throwable> caught = new AtomicReference<Throwable>();
		ThreadUtils.runAsThread(() -> {
			throw new IllegalStateException("x");
		}, (t, e) -> caught.set(e)).join();
		assertEquals("x", caught.get().getMessage());
		ThreadUtils.runAsThread("named2", () -> {
			throw new IllegalStateException("y");
		}, (t, e) -> caught.set(e)).join();
		assertEquals("y", caught.get().getMessage());
		AtomicBoolean ran = new AtomicBoolean();
		ThreadUtils.runAsThread(() -> ran.set(true)).join();
		assertTrue(ran.get());
		Thread t = ThreadUtils.runAsThread(r -> new Thread(r, "factory"), () -> name.set(ThreadUtils.currentThreadName()));
		t.join();
		assertEquals("factory", name.get());
	}

	@Test
	public void schedule_runsOnceAfterDelay() throws Exception {
		CountDownLatch done = new CountDownLatch(3);
		ThreadUtils.schedule(done::countDown, 5);
		ThreadUtils.schedule(done::countDown, 5, TimeUnit.MILLISECONDS);
		ThreadUtils.schedule(done::countDown, -5);
		assertTrue(done.await(5, TimeUnit.SECONDS));
		CountDownLatch dated = new CountDownLatch(1);
		ThreadUtils.schedule(dated::countDown, new Date(System.currentTimeMillis() + 20));
		assertTrue(dated.await(5, TimeUnit.SECONDS));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> ThreadUtils.schedule(() -> {
				}, new Date(System.currentTimeMillis() - 1000)));
		assertTrue(e.getMessage().startsWith("Past time: "));
	}

	@Test
	public void scheduleAtFixedRate_stopsWhenExecutableReturnsFalse() throws Exception {
		CountingExecutable e = new CountingExecutable(3, false);
		Timer timer = ThreadUtils.scheduleAtFixedRate(e, -1, 5);
		assertTrue(e.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(3, e.runs.get());
		assertNull(e.cancelCause.get());
		timer.cancel();
	}

	@Test
	public void scheduleAtFixedRate_onErrorDecidesWhetherToContinue() throws Exception {
		CountingExecutable e = new CountingExecutable(2, true);
		Timer timer = ThreadUtils.scheduleAtFixedRate(e, 1, 5, TimeUnit.MILLISECONDS);
		assertTrue(e.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(2, e.runs.get());
		assertEquals("fail 2", e.cancelCause.get().getMessage());
		timer.cancel();
	}

	@Test
	public void scheduleAtFixedRate_overloads() throws Exception {
		CountingExecutable a = new CountingExecutable(1, false);
		CountingExecutable b = new CountingExecutable(1, false);
		CountingExecutable c = new CountingExecutable(1, false);
		ThreadUtils.scheduleAtFixedRate(a, 5, TimeUnit.MILLISECONDS);
		ThreadUtils.scheduleAtFixedRate(b, new Date(System.currentTimeMillis() + 10), 5, TimeUnit.MILLISECONDS);
		ThreadUtils.scheduleAtFixedRate(c, 1, TimeUnit.MILLISECONDS, 5, TimeUnit.MILLISECONDS);
		assertTrue(a.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(b.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(c.cancelled.await(5, TimeUnit.SECONDS));
		assertThrows(IllegalArgumentException.class,
				() -> ThreadUtils.scheduleAtFixedRate(a, new Date(System.currentTimeMillis() - 1000), 5));
	}

	@Test
	public void scheduleWithFixedDelay_overloadsAndCancellation() throws Exception {
		CountingExecutable a = new CountingExecutable(2, false);
		CountingExecutable b = new CountingExecutable(1, true);
		CountingExecutable c = new CountingExecutable(1, false);
		CountingExecutable d = new CountingExecutable(1, false);
		ThreadUtils.scheduleWithFixedDelay(a, 5, TimeUnit.MILLISECONDS);
		ThreadUtils.scheduleWithFixedDelay(b, new Date(System.currentTimeMillis() + 10), 5, TimeUnit.MILLISECONDS);
		ThreadUtils.scheduleWithFixedDelay(c, 1, 5, TimeUnit.MILLISECONDS);
		ThreadUtils.scheduleWithFixedDelay(d, -10, 5);
		assertTrue(a.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(b.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(c.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(d.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(2, a.runs.get());
		assertEquals("fail 1", b.cancelCause.get().getMessage());
		assertThrows(IllegalArgumentException.class,
				() -> ThreadUtils.scheduleWithFixedDelay(a, new Date(System.currentTimeMillis() - 1000), 5));
	}

	@Test
	public void newThreadLocal_usesSupplierPerThread() throws Exception {
		AtomicInteger seq = new AtomicInteger();
		ThreadLocal<Integer> local = ThreadUtils.newThreadLocal(seq::incrementAndGet);
		assertEquals(Integer.valueOf(1), local.get());
		assertEquals(Integer.valueOf(1), local.get());
		AtomicReference<Integer> other = new AtomicReference<Integer>();
		Thread t = new Thread(() -> other.set(local.get()));
		t.start();
		t.join();
		assertEquals(Integer.valueOf(2), other.get());
	}

	@Test
	public void loop_runsEveryIterationBeforeReturning() {
		Set<Integer> seen = ConcurrentHashMap.newKeySet();
		ThreadUtils.loop(4, 100, seen::add);
		assertEquals(100, seen.size());
		Set<String> words = ConcurrentHashMap.newKeySet();
		ThreadUtils.loop(2, Arrays.asList("a", "b", "c"), words::add);
		assertEquals(3, words.size());
	}

	@Test
	public void benchmark_runsIterableOncePerThread() {
		AtomicInteger counter = new AtomicInteger();
		ThreadUtils.benchmark(3, 2, 50, i -> counter.incrementAndGet());
		assertEquals(150, counter.get());
	}

	@Test
	public void commonPool_variants() {
		ThreadPool a = ThreadUtils.commonPool();
		ThreadPool b = ThreadUtils.commonPool(3);
		ThreadPool c = ThreadUtils.commonPool(2, 10, 5);
		try {
			assertEquals(ExecutorUtils.processorCount * 2, a.getMaxPoolSize());
			assertEquals(3, b.getMaxPoolSize());
			assertEquals(2, c.getMaxPoolSize());
			assertNotSame(a, b);
			Promise<String> p = c.submit(() -> "ok");
			assertEquals("ok", p.get());
		} finally {
			a.shutdown();
			b.shutdown();
			c.shutdown();
		}
		assertTrue(a.isShutdown());
		assertSame(true, c.isShutdown());
	}

	static class CountingExecutable implements Executable {

		final int stopAt;
		final boolean fail;
		final AtomicInteger runs = new AtomicInteger();
		final CountDownLatch cancelled = new CountDownLatch(1);
		final AtomicReference<Throwable> cancelCause = new AtomicReference<Throwable>();

		CountingExecutable(int stopAt, boolean fail) {
			this.stopAt = stopAt;
			this.fail = fail;
		}

		public boolean execute() {
			int n = runs.incrementAndGet();
			if (n >= stopAt) {
				if (fail) {
					throw new IllegalStateException("fail " + n);
				}
				return false;
			}
			return true;
		}

		public boolean onError(Throwable e) {
			return false;
		}

		public void onCancellation(Throwable e) {
			cancelCause.set(e);
			cancelled.countDown();
		}

	}

}
