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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * RecursiveLatchTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(10)
public class RecursiveLatchTest {

	@Test
	public void sameThread_reacquiresWithoutConsumingPermits() {
		RecursiveLatch latch = new RecursiveLatch();
		assertTrue(latch.acquire());
		assertTrue(latch.acquire());
		assertTrue(latch.tryAcquire());
		assertTrue(latch.acquire(10, TimeUnit.MILLISECONDS));
		assertEquals(1, latch.cons());
		assertEquals(0, latch.availablePermits());
		latch.release();
		latch.release();
		latch.release();
		assertTrue(latch.isLocked());
		latch.release();
		assertFalse(latch.isLocked());
		assertEquals(1, latch.availablePermits());
	}

	@Test
	public void otherThread_isBlockedWhileHeld() throws Exception {
		RecursiveLatch latch = new RecursiveLatch(1);
		latch.acquire();
		AtomicBoolean tryResult = new AtomicBoolean(true);
		AtomicBoolean timedResult = new AtomicBoolean(true);
		Thread t = new Thread(() -> {
			tryResult.set(latch.tryAcquire());
			timedResult.set(latch.acquire(20, TimeUnit.MILLISECONDS));
		});
		t.start();
		t.join();
		assertFalse(tryResult.get());
		assertFalse(timedResult.get());
	}

	@Test
	public void blockedAcquire_doesNotDeadlockWithReleaseFromOwner() throws Exception {
		RecursiveLatch latch = new RecursiveLatch(new CounterLatch(1));
		latch.acquire();
		AtomicBoolean acquired = new AtomicBoolean();
		Thread t = new Thread(() -> acquired.set(latch.acquire()));
		t.start();
		waitForState(t, Thread.State.WAITING);
		latch.release();
		t.join();
		assertTrue(acquired.get());
	}

	@Test
	public void interruptedAcquire_returnsFalse() throws Exception {
		RecursiveLatch latch = new RecursiveLatch(1);
		latch.acquire();
		AtomicBoolean result = new AtomicBoolean(true);
		Thread t = new Thread(() -> {
			Thread.currentThread().interrupt();
			result.set(latch.acquire());
		});
		t.start();
		t.join();
		assertFalse(result.get());
	}

	@Test
	public void releaseFromAnotherThread_releasesDelegate() throws Exception {
		RecursiveLatch latch = new RecursiveLatch(2);
		latch.acquire();
		Thread t = new Thread(latch::release);
		t.start();
		t.join();
		assertFalse(latch.isLocked());
		assertTrue(latch.join() >= 0);
	}

	@Test
	public void crossThreadRelease_leavesAcquirerHoldCount_documentedConstraint() throws Exception {
		RecursiveLatch latch = new RecursiveLatch(1);
		latch.acquire();
		Thread t = new Thread(latch::release);
		t.start();
		t.join();
		assertEquals(0, latch.cons());
		// the acquiring thread still holds a reentrant count and bypasses the delegate
		assertTrue(latch.tryAcquire());
		assertEquals(0, latch.cons());
		latch.release();
		latch.release();
		assertEquals(0, latch.cons());
		assertTrue(latch.tryAcquire());
		assertEquals(1, latch.cons());
	}

	@Test
	public void noopDelegate_neverLocksAndForEachIsUnsupported() {
		RecursiveLatch latch = new RecursiveLatch(new NoopLatch());
		assertThrows(UnsupportedOperationException.class, () -> new NoopLatch().forEach(Arrays.asList(1), Runnable::run, x -> {
		}));
		assertTrue(latch.acquire());
		assertFalse(latch.isLocked());
		assertEquals(Long.MAX_VALUE, latch.availablePermits());
	}

	@Test
	public void noopLatch_alwaysGrantsPermits() {
		NoopLatch latch = new NoopLatch();
		assertTrue(latch.acquire());
		assertTrue(latch.tryAcquire());
		assertTrue(latch.acquire(1, TimeUnit.MILLISECONDS));
		latch.release();
		assertEquals(0, latch.cons());
		assertEquals(Long.MAX_VALUE, latch.availablePermits());
		assertFalse(latch.isLocked());
		assertTrue(latch.join() >= 0);
	}

}
