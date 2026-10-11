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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * RetryableTimerTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class RetryableTimerTest {

	private final RetryableTimer timer = new RetryableTimer();

	@AfterEach
	@Timeout(10)
	public void tearDown() {
		timer.cancel();
	}

	@Test
	public void successfulFirstAttempt_isNotRetried() {
		TestRetryable r = new TestRetryable(0, Integer.MAX_VALUE);
		timer.executeAndRetryAtFixedRate(r, 5, TimeUnit.MILLISECONDS);
		timer.executeAndRetryWithFixedDelay(r, 5, TimeUnit.MILLISECONDS);
		assertEquals(2, r.attempts.get());
		assertTrue(r.errorCounts.isEmpty());
	}

	@Test
	public void capturedFailure_isRetriedUntilSuccess() throws Exception {
		TestRetryable r = new TestRetryable(3, Integer.MAX_VALUE);
		timer.executeAndRetryAtFixedRate(r, 5, TimeUnit.MILLISECONDS);
		assertTrue(r.succeeded.await(5, TimeUnit.SECONDS));
		assertEquals(4, r.attempts.get());
		synchronized (r.errorCounts) {
			assertEquals(Arrays.asList(1, 2), r.errorCounts);
		}
	}

	@Test
	public void retriesStopAfterMaxRetriesWithoutDuplicateErrorReport() throws Exception {
		TestRetryable r = new TestRetryable(Integer.MAX_VALUE, 2);
		r.expectedErrors = new CountDownLatch(3);
		timer.executeAndRetryWithFixedDelay(r, 5, TimeUnit.MILLISECONDS);
		assertTrue(r.expectedErrors.await(5, TimeUnit.SECONDS));
		Thread.sleep(40);
		assertEquals(4, r.attempts.get());
		synchronized (r.errorCounts) {
			assertEquals(Arrays.asList(1, 2, 3), r.errorCounts);
		}
	}

	@Test
	public void uncapturedFailure_isReportedImmediately() {
		TestRetryable r = new TestRetryable(Integer.MAX_VALUE, 5);
		r.failure = new IOException("io");
		r.captures = new Class[] { IllegalStateException.class };
		timer.executeAndRetryAtFixedRate(r, 5, TimeUnit.MILLISECONDS);
		timer.executeAndRetryWithFixedDelay(r, 5, TimeUnit.MILLISECONDS);
		assertEquals(2, r.attempts.get());
		assertEquals(Arrays.asList(0, 0), r.errorCounts);
		TestRetryable none = new TestRetryable(Integer.MAX_VALUE, 5);
		none.captures = new Class[0];
		timer.executeAndRetryAtFixedRate(none, 5, TimeUnit.MILLISECONDS);
		assertEquals(Arrays.asList(0), none.errorCounts);
	}

	@Test
	public void failureOfDifferentTypeDuringRetry_stopsRetrying() throws Exception {
		TestRetryable r = new TestRetryable(Integer.MAX_VALUE, 10) {
			public void execute() throws Throwable {
				if (attempts.incrementAndGet() == 1) {
					throw new IllegalStateException("captured");
				}
				throw new Error("not captured");
			}
		};
		r.expectedErrors = new CountDownLatch(1);
		timer.executeAndRetryAtFixedRate(r, 5, TimeUnit.MILLISECONDS);
		assertTrue(r.expectedErrors.await(5, TimeUnit.SECONDS));
		Thread.sleep(30);
		assertEquals(2, r.attempts.get());
		assertEquals(Arrays.asList(1), r.errorCounts);
	}

	@Test
	public void sameRetryableIsScheduledOnlyOnce() throws Exception {
		CountDownLatch release = new CountDownLatch(1);
		AtomicInteger attempts = new AtomicInteger();
		CountDownLatch done = new CountDownLatch(1);
		Retryable r = new Retryable() {
			public void execute() throws Throwable {
				attempts.incrementAndGet();
				if (release.getCount() > 0) {
					throw new IllegalStateException("not yet");
				}
				done.countDown();
			}
		};
		timer.executeAndRetryAtFixedRate(r, 300, TimeUnit.MILLISECONDS);
		timer.executeAndRetryAtFixedRate(r, 300, TimeUnit.MILLISECONDS);
		timer.executeAndRetryWithFixedDelay(r, 300, TimeUnit.MILLISECONDS);
		assertEquals(3, attempts.get());
		release.countDown();
		assertTrue(done.await(5, TimeUnit.SECONDS));
		Thread.sleep(350);
		assertEquals(4, attempts.get());
	}

	@Test
	public void cancel_stopsPendingRetries() throws Exception {
		TestRetryable r = new TestRetryable(Integer.MAX_VALUE, Integer.MAX_VALUE);
		r.expectedErrors = new CountDownLatch(2);
		timer.executeAndRetryAtFixedRate(r, 5, TimeUnit.MILLISECONDS);
		assertTrue(r.expectedErrors.await(5, TimeUnit.SECONDS));
		timer.cancel();
		int attempts = r.attempts.get();
		Thread.sleep(30);
		assertTrue(r.attempts.get() <= attempts + 1);
		// a cancelled retryable can be scheduled again
		TestRetryable again = new TestRetryable(1, Integer.MAX_VALUE);
		timer.executeAndRetryAtFixedRate(again, 5, TimeUnit.MILLISECONDS);
		assertTrue(again.succeeded.await(5, TimeUnit.SECONDS));
	}

	static class TestRetryable implements Retryable {

		final int failures;
		final int maxRetries;
		final AtomicInteger attempts = new AtomicInteger();
		final List<Integer> errorCounts = new ArrayList<Integer>();
		final CountDownLatch succeeded = new CountDownLatch(1);
		volatile CountDownLatch expectedErrors = new CountDownLatch(0);
		volatile Throwable failure = new IllegalStateException("retry");
		@SuppressWarnings("rawtypes")
		volatile Class[] captures;

		TestRetryable(int failures, int maxRetries) {
			this.failures = failures;
			this.maxRetries = maxRetries;
		}

		public void execute() throws Throwable {
			if (attempts.incrementAndGet() <= failures) {
				throw failure;
			}
			succeeded.countDown();
		}

		public int getMaxRetries() {
			return maxRetries;
		}

		@SuppressWarnings("unchecked")
		public Class<? extends Exception>[] captureClasses() {
			return captures != null ? captures : Retryable.super.captureClasses();
		}

		public void onError(int count, Throwable e) {
			synchronized (errorCounts) {
				errorCounts.add(count);
			}
			expectedErrors.countDown();
		}
	}

}
