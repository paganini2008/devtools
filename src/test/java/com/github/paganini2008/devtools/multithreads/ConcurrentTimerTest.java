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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * ConcurrentTimerTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class ConcurrentTimerTest {

	private final List<ConcurrentTimer> timers = new ArrayList<ConcurrentTimer>();

	private ConcurrentTimer newTimer() {
		ConcurrentTimer timer = new ConcurrentTimer();
		timers.add(timer);
		return timer;
	}

	@AfterEach
	@Timeout(10)
	public void tearDown() {
		for (ConcurrentTimer timer : timers) {
			timer.cancel();
		}
	}

	@Test
	public void executablesWithSameScheduleShareOneTimer() throws Exception {
		ConcurrentTimer timer = newTimer();
		Probe a = new Probe(3);
		Probe b = new Probe(5);
		Timer t1 = timer.scheduleAtFixedRate(a, 1, 5);
		Timer t2 = timer.scheduleAtFixedRate(b, 1, 5);
		assertSame(t1, t2);
		Timer t3 = timer.scheduleWithFixedDelay(new Probe(1), 1, 5);
		assertFalse(t1 == t3);
		assertTrue(a.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(b.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(3, a.runs.get());
		assertEquals(5, b.runs.get());
		assertEquals(1, a.cancellations.get());
		assertNull(a.cause.get());
	}

	@Test
	public void failingExecutableDoesNotAffectOthers() throws Exception {
		ConcurrentTimer timer = newTimer();
		Probe failing = new Probe(Integer.MAX_VALUE);
		failing.failAt = 2;
		Probe healthy = new Probe(6);
		timer.scheduleWithFixedDelay(failing, 1, 5);
		timer.scheduleWithFixedDelay(healthy, 1, 5);
		assertTrue(failing.cancelled.await(5, TimeUnit.SECONDS));
		assertTrue(healthy.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(2, failing.runs.get());
		assertEquals(1, failing.errors.get());
		assertEquals("fail 2", failing.cause.get().getMessage());
		assertEquals(6, healthy.runs.get());
		assertEquals(0, healthy.errors.get());
	}

	@Test
	public void onErrorReturningTrueKeepsExecutableScheduled() throws Exception {
		ConcurrentTimer timer = newTimer();
		Probe probe = new Probe(4);
		probe.failAt = 2;
		probe.continueOnError = true;
		timer.scheduleAtFixedRate(probe, 1, 5);
		assertTrue(probe.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(4, probe.runs.get());
		assertEquals(1, probe.errors.get());
		assertNull(probe.cause.get());
	}

	@Test
	public void throwingCallbacksAreContained() throws Exception {
		ConcurrentTimer timer = newTimer();
		CountDownLatch cancelCalled = new CountDownLatch(1);
		Probe healthy = new Probe(4);
		timer.scheduleAtFixedRate(new Executable() {
			public boolean execute() {
				throw new IllegalStateException("execute");
			}

			public boolean onError(Throwable e) {
				throw new IllegalStateException("onError");
			}

			public void onCancellation(Throwable e) {
				cancelCalled.countDown();
				throw new IllegalStateException("onCancellation");
			}
		}, 1, 5);
		timer.scheduleAtFixedRate(healthy, 1, 5);
		assertTrue(cancelCalled.await(5, TimeUnit.SECONDS));
		assertTrue(healthy.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(4, healthy.runs.get());
	}

	@Test
	public void timerIsReleasedWhenAllExecutablesFinishAndCanBeReused() throws Exception {
		ConcurrentTimer timer = newTimer();
		Probe first = new Probe(1);
		timer.scheduleWithFixedDelay(first, 1, 5);
		assertTrue(first.cancelled.await(5, TimeUnit.SECONDS));
		Probe second = new Probe(2);
		timer.scheduleWithFixedDelay(second, 1, 5);
		assertTrue(second.cancelled.await(5, TimeUnit.SECONDS));
		assertEquals(2, second.runs.get());
	}

	@Test
	public void cancel_stopsTimersAndNotifiesExecutables() throws Exception {
		ConcurrentTimer timer = newTimer();
		Probe probe = new Probe(Integer.MAX_VALUE);
		Probe delayed = new Probe(Integer.MAX_VALUE);
		timer.scheduleAtFixedRate(probe, 1, 5);
		timer.scheduleWithFixedDelay(delayed, 10000, 10000);
		while (probe.runs.get() < 2) {
			Thread.sleep(1);
		}
		timer.cancel();
		assertEquals(1, probe.cancellations.get());
		assertEquals(1, delayed.cancellations.get());
		int runs = probe.runs.get();
		Thread.sleep(30);
		assertTrue(probe.runs.get() <= runs + 1);
		timer.cancel();
		assertEquals(1, probe.cancellations.get());
	}

	@Test
	public void overloadsConvertUnitsAndValidateDates() throws Exception {
		ConcurrentTimer timer = newTimer();
		List<Probe> probes = new CopyOnWriteArrayList<Probe>();
		for (int i = 0; i < 8; i++) {
			probes.add(new Probe(1));
		}
		Date future = new Date(System.currentTimeMillis() + 20);
		timer.scheduleAtFixedRate(probes.get(0), 5, TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(probes.get(1), 1, 5, TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(probes.get(2), future, 5, TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(probes.get(3), 1, TimeUnit.MILLISECONDS, 5, TimeUnit.MILLISECONDS);
		timer.scheduleWithFixedDelay(probes.get(4), 5, TimeUnit.MILLISECONDS);
		timer.scheduleWithFixedDelay(probes.get(5), 1, 5, TimeUnit.MILLISECONDS);
		timer.scheduleWithFixedDelay(probes.get(6), future, 5, TimeUnit.MILLISECONDS);
		timer.scheduleWithFixedDelay(probes.get(7), 1, TimeUnit.MILLISECONDS, 5, TimeUnit.MILLISECONDS);
		for (Probe probe : probes) {
			assertTrue(probe.cancelled.await(5, TimeUnit.SECONDS));
		}
		Date past = new Date(System.currentTimeMillis() - 1000);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> timer.scheduleAtFixedRate(new Probe(1), past, 5));
		assertTrue(e.getMessage().startsWith("Past time: "));
		assertThrows(IllegalArgumentException.class, () -> timer.scheduleWithFixedDelay(new Probe(1), past, 5));
		assertThrows(NullPointerException.class, () -> timer.scheduleAtFixedRate(null, 1, 5));
	}

	@Test
	public void timerFeature_equalsAndHashCode() {
		ConcurrentTimer.TimerFeature a = new ConcurrentTimer.TimerFeature(1, 2, true);
		ConcurrentTimer.TimerFeature b = new ConcurrentTimer.TimerFeature(1, 2, true);
		assertEquals(a, a);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, new ConcurrentTimer.TimerFeature(1, 2, false));
		assertNotEquals(a, new ConcurrentTimer.TimerFeature(1, 3, true));
		assertNotEquals(a, new ConcurrentTimer.TimerFeature(0, 2, true));
		assertNotEquals(a, "x");
	}

	@Test
	public void serialExecutable_onErrorReportsWhetherToKeepRunning() {
		ConcurrentTimer timer = newTimer();
		List<Executable> list = new CopyOnWriteArrayList<Executable>();
		ConcurrentTimer.SerialExecutable serial = timer.new SerialExecutable(new ConcurrentTimer.TimerFeature(1, 1, true), list);
		assertFalse(serial.onError(new Exception()));
		list.add(new Probe(1));
		assertTrue(serial.onError(new Exception()));
	}

	@Test
	public void stress_concurrentSchedulingCreatesSingleTimerPerFeature() throws Exception {
		ConcurrentTimer timer = newTimer();
		final int threads = 8;
		AtomicReference<Timer> first = new AtomicReference<Timer>();
		AtomicInteger distinct = new AtomicInteger();
		List<Probe> probes = new CopyOnWriteArrayList<Probe>();
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);
		for (int i = 0; i < threads; i++) {
			new Thread(() -> {
				try {
					start.await();
					Probe probe = new Probe(2);
					probes.add(probe);
					Timer t = timer.scheduleAtFixedRate(probe, 200, 5);
					if (!first.compareAndSet(null, t) && first.get() != t) {
						distinct.incrementAndGet();
					}
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			}).start();
		}
		start.countDown();
		assertTrue(done.await(5, TimeUnit.SECONDS));
		assertEquals(0, distinct.get());
		for (Probe probe : probes) {
			assertTrue(probe.cancelled.await(5, TimeUnit.SECONDS));
			assertEquals(2, probe.runs.get());
		}
	}

	static class Probe implements Executable {

		final int stopAt;
		int failAt = -1;
		boolean continueOnError;
		final AtomicInteger runs = new AtomicInteger();
		final AtomicInteger errors = new AtomicInteger();
		final AtomicInteger cancellations = new AtomicInteger();
		final AtomicReference<Throwable> cause = new AtomicReference<Throwable>();
		final CountDownLatch cancelled = new CountDownLatch(1);

		Probe(int stopAt) {
			this.stopAt = stopAt;
		}

		public boolean execute() {
			int n = runs.incrementAndGet();
			if (n == failAt) {
				throw new IllegalStateException("fail " + n);
			}
			return n < stopAt;
		}

		public boolean onError(Throwable e) {
			errors.incrementAndGet();
			return continueOnError;
		}

		public void onCancellation(Throwable e) {
			cause.set(e);
			cancellations.incrementAndGet();
			cancelled.countDown();
		}
	}

}
